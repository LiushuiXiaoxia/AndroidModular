package cn.mycommons.module_plugin.core.v2

import cn.mycommons.module_plugin.core.Consts
import cn.mycommons.module_plugin.core.ImplementRecord
import cn.mycommons.module_plugin.core.PluginKit
import cn.mycommons.modulebase.annotations.Implements
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class ModuleProcessTask : DefaultTask() {

    companion object {

        fun setup(project: Project, ac: AndroidComponentsExtension<*, *, *>) {
            ac.onVariants {
                val name = "modularProcess${it.name.replaceFirstChar { c -> c.uppercaseChar() }}"
                val taskProvider = project.tasks.register(name, ModuleProcessTask::class.java)
                it.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                    .use(taskProvider)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        ModuleProcessTask::allJars,
                        ModuleProcessTask::allDirectories,
                        ModuleProcessTask::output,
                    )
            }
        }

    }

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        // 记录所有的符合扫描条件的记录
        val implementsList = mutableListOf<ImplementRecord>()
        // ImplementsManager 注解所在的jar文件
        var managerJar: File? = null
        val pool = ClassPool(ClassPool.getDefault())
        pool.appendSystemPath()

        allDirectories.get().forEach { d ->
            pool.appendClassPath(d.asFile.absolutePath)
            d.asFile.walkTopDown().filter { it.name.endsWith(".class") }.forEach {
                val rel = it.toRelativeString(d.asFile).replace("/", ".")
                // log.error("allDirectories.scanClass: $it -> $rel")

                if (rel.endsWith(".class")) {
                    val cc: CtClass = pool.get(rel.replace(".class", ""))
                    checkCollectClass(cc, implementsList)
                }
            }
        }

        allJars.get().forEach {
            val file = it.asFile
            pool.appendClassPath(file.absolutePath)

            if (managerJar == null && PluginKit.isImplementsManager(file)) {
                managerJar = file
            }

            for (entry in JarFile(file).entries()) {
                val path = entry.name.replace("/", ".")
                // log.error("allJars.scanClass: $it -> $path")
                if (path.endsWith(".class")) {
                    val cc = pool.get(path.replace(".class", ""))
                    checkCollectClass(cc, implementsList)
                }
            }
        }

        logger.quiet("implementsList: ${implementsList.size}")
        implementsList.forEach {
            logger.quiet("implementsList: $it")
        }

        val outputSet = mutableSetOf<String>() // 已经添加过的文件
        val tmpOutput = File.createTempFile("${name}-", ".jar")
        logger.quiet("tmpOutput = $tmpOutput")
        val tmpOutputJar = JarOutputStream(FileOutputStream(tmpOutput))

        allDirectories.get().forEach { d ->
            for (file in d.asFile.walk()) {
                if (file.isFile) {
                    val entryName = file.toRelativeString(d.asFile)
                    if (outputSet.contains(entryName)) {
                        logger.error("skip entry: $entryName in $d")
                        continue
                    }
                    addClass(d, file, tmpOutputJar)
                    outputSet.add(entryName)
                }
            }
        }

        allJars.get().forEach { file ->
            val jarFile = JarFile(file.asFile)
            for (e in jarFile.entries().iterator()) {
                if (outputSet.contains(e.name)) {
                    if (!e.isDirectory) {
                        logger.error("skip entry: ${e.name} in $file")
                    }
                    continue
                }
                tmpOutputJar.putNextEntry(JarEntry(e.name))
                val data = if (e.name == Consts.IMPLEMENTS_MANAGER) {
                    modifyClass(pool.get(Consts.IMPLEMENTS_MANAGER_NAME), implementsList)
                } else {
                    jarFile.getInputStream(e).readBytes()
                }
                tmpOutputJar.write(data)
                tmpOutputJar.closeEntry()
                outputSet.add(e.name)
            }
            jarFile.close()
        }

        tmpOutputJar.close()
        output.get().asFile.let {
            if (it.exists()) {
                it.delete()
            }
            tmpOutput.copyTo(it)
            tmpOutput.delete()
        }
    }

    // 如果class中的类包含注解则先收集起来
    private fun checkCollectClass(cc: CtClass, implementsList: MutableList<ImplementRecord>) {
        kotlin.runCatching {
            val an = cc.getAnnotation(Implements::class.java) as Implements?
            if (an != null) {
                implementsList.add(ImplementRecord(an, cc))
            }
        }
    }

    private fun modifyClass(cc: CtClass, implementsList: List<ImplementRecord>): ByteArray {
        // 修改class，在class中插入静态代码块，做初始化
        val body = PluginKit.genConfigMethod(implementsList)
        logger.quiet("body = " + body.joinToString("\n"))

        if (cc.isFrozen) {
            cc.defrost()
        }
        cc.makeClassInitializer().setBody(body.joinToString("\n"))

        return cc.toBytecode()
    }

    private fun addClass(directory: Directory, file: File, jos: JarOutputStream) {
        val rel = file.toRelativeString(directory.asFile)
        jos.putNextEntry(JarEntry(rel))
        jos.write(file.readBytes())
        jos.closeEntry()
    }
}