package cn.mycommons.module_plugin.v2

import cn.mycommons.module_plugin.Consts
import cn.mycommons.module_plugin.Entry
import cn.mycommons.module_plugin.PluginKit
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

abstract class ModifyClassesTask : DefaultTask() {

    companion object {

        fun setup(project: Project, ac: AndroidComponentsExtension<*, *, *>) {
            ac.onVariants {
                val taskProvider = project.tasks.register("${it.name}ModifyClasses", ModifyClassesTask::class.java)
                it.artifacts.forScope(ScopedArtifacts.Scope.ALL)
                    .use(taskProvider)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        ModifyClassesTask::allJars,
                        ModifyClassesTask::allDirectories,
                        ModifyClassesTask::output,
                    )
            }
        }

    }

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        log.error("taskAction")
        allJars.get().forEach {
            log.error("taskAction: allJars = ${it.asFile}")
        }
        allDirectories.get().forEach {
            log.error("taskAction: allDirectories = ${it.asFile}")
        }
        log.error("taskAction: output = ${output.get().asFile}")

        // 记录所有的符合扫描条件的记录
        val implementsList = mutableListOf<Entry>()
        // ImplementsManager 注解所在的jar文件
        var managerJar: File? = null
        val pool = ClassPool(ClassPool.getDefault())
        pool.appendSystemPath()

        allDirectories.get().forEach { d ->
            log.error("allDirectories.scan: ${d.asFile}")

            pool.appendClassPath(d.asFile.absolutePath)
            d.asFile.walkTopDown().filter { it.name.endsWith(".class") }
                .forEach {
                    var path = it.absolutePath.replace(d.asFile.absolutePath, "").replace("/", ".").substring(1)
                    // log.error("allDirectories.scanClass: $it -> $path")

                    if (path.endsWith(".class")) {
                        path = path.substring(0, path.length - 6)
                        val clazz: CtClass = pool.get(path)
                        // 如果class中的类包含注解则先收集起来
                        kotlin.runCatching {
                            val an = clazz.getAnnotation(Implements::class.java) as Implements?
                            if (an != null) {
                                implementsList.add(Entry(an, clazz))
                            }
                        }
                    }
                }
        }

        allJars.get().forEach {
            log.error("allJars.scan: ${it.asFile}")

            val file = it.asFile
            pool.appendClassPath(file.absolutePath)

            if (managerJar == null && PluginKit.isImplementsManager(it.asFile)) {
                managerJar = it.asFile
            }

            for (entry in JarFile(file).entries()) {
                var path = entry.name.replace("/", ".")
                // log.error("allJars.scanClass: $it -> $path")

                if (path.endsWith(".class")) {
                    path = path.substring(0, path.length - 6)
                    val clazz = pool.get(path)
                    kotlin.runCatching {
                        val an = clazz.getAnnotation(Implements::class.java) as Implements?
                        if (an != null) {
                            implementsList.add(Entry(an, clazz))
                        }
                    }
                }
            }
        }

        log.error("implementsList: ${implementsList.size}")

        implementsList.forEach {
            log.error("implementsList: $it")
        }


        val outputSet = mutableSetOf<String>()
        val jarOutput = JarOutputStream(FileOutputStream(output.get().asFile))
        allJars.get().forEach { file ->
            // log.error("handling " + file.asFile.getAbsolutePath())
            val jarFile = JarFile(file.asFile)
            for (e in jarFile.entries().iterator()) {
                if (outputSet.contains(e.name)) {
                    continue
                }
                outputSet.add(e.name)
//                log.error("Adding from jar ${e.name}")
                if (e.name == Consts.IMPLEMENTS_MANAGER) {
                    val config = parseConfig(implementsList)
                    project.logger.error("config = $config")
                    val managerCtClass = pool.get(Consts.IMPLEMENTS_MANAGER_NAME)

                    // 修改class，在class中插入静态代码块，做初始化
                    val body = genMethodBody(config)
                    project.logger.error("body = " + body.joinToString("\n"))
                    if (managerCtClass.isFrozen) {
                        managerCtClass.defrost()
                    }
                    managerCtClass.makeClassInitializer().setBody(body.joinToString("\n"))

                    val data = managerCtClass.toBytecode()

                    jarOutput.putNextEntry(JarEntry(e.name))
                    jarOutput.write(data)
                    jarOutput.closeEntry()
                } else {
                    jarOutput.putNextEntry(JarEntry(e.name))
                    jarFile.getInputStream(e).use { it.copyTo(jarOutput) }
                    jarOutput.closeEntry()
                }
            }
            jarFile.close()
        }

        allDirectories.get().forEach { directory ->
            // log.error("handling " + directory.asFile.getAbsolutePath())
            for (file in directory.asFile.walk()) {
                if (file.isFile) {
                    val relativePath = directory.asFile.toURI().relativize(file.toURI()).getPath()
                    val entryName = relativePath.replace(File.separatorChar, '/')
                    if (outputSet.contains(entryName)) {
                        continue
                    }
                    outputSet.add(entryName)
                    addClass(directory, file, jarOutput)
                }
            }
        }
        jarOutput.close()

    }

    private fun parseConfig(implementsList: MutableList<Entry>): LinkedHashMap<String, String> {
        val config = linkedMapOf<String, String>()
        implementsList.forEach {
            val str = it.anImplements.toString()
            val parent = str.substring(str.indexOf("(") + 1, str.indexOf(")"))
                .replace("parent=", "").replace(".class", "")

            // 收集所有的接口以及实现类的路径
            config[parent] = it.ctClass.name
        }
        return config
    }

    private fun genMethodBody(config: Map<String, String>): List<String> {
        return mutableListOf<String>().apply {
            add("{")
            add("\tCONFIG = new java.util.HashMap();")
            config.forEach {
                add("\tCONFIG.put(${it.key}.class, ${it.value}.class);")
            }
            add("}")
        }
    }

    private fun addClass(directory: Directory, file: File, jarOutput: JarOutputStream) {
        val relativePath = directory.asFile.toURI().relativize(file.toURI()).getPath()
        log.error("Adding from directory ${relativePath.replace(File.separatorChar, '/')}")

        jarOutput.putNextEntry(JarEntry(relativePath.replace(File.separatorChar, '/')))
        file.inputStream().use { inputStream ->
            inputStream.copyTo(jarOutput)
        }
        jarOutput.closeEntry()
    }
}