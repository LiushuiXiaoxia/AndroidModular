package cn.mycommons.module_plugin.core.v1

import cn.mycommons.module_plugin.core.Consts
import cn.mycommons.module_plugin.core.ImplementRecord
import cn.mycommons.module_plugin.core.PluginKit
import cn.mycommons.module_plugin.util.safeDelete
import cn.mycommons.modulebase.annotations.Implements
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project
import java.io.File
import java.io.FileOutputStream

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * ImplementsTransform11 <br/>
 * Created by xiaqiulei on 2017-05-15.
 */
class ModulePluginTransform(private val project: Project) : Transform() {

    override fun getName(): String = "ModulePlugin"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_JARS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental() = false

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        val now = System.currentTimeMillis()

        val outputProvider = transformInvocation.outputProvider
        outputProvider.deleteAll()

        val classPool = ClassPool.getDefault()
        classPool.appendSystemPath()

        // 记录所有的符合扫描条件的记录
        val implementsList = mutableListOf<ImplementRecord>()
        // ImplementsManager 注解所在的jar文件
        var managerJar: JarInput? = null

        // 扫描所有的文件
        transformInvocation.inputs.forEach { input ->
            input.directoryInputs.forEach { dir ->
                classPool.appendClassPath(dir.file.absolutePath)
                val dst = outputProvider.getContentLocation(dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY)
                dst.safeDelete()
                dst.mkdirs()
                dir.file.copyRecursively(dst)

                project.fileTree(dst).forEach {
                    var path = it.absolutePath.replace(dst.absolutePath, "").replace("/", ".").substring(1)
                    if (path.endsWith(".class")) {
                        path = path.substring(0, path.length - 6)
                        val clazz: CtClass = classPool.get(path)
                        // 如果class中的类包含注解则先收集起来
                        kotlin.runCatching {
                            val an = clazz.getAnnotation(Implements::class.java) as Implements?
                            if (an != null) {
                                implementsList.add(ImplementRecord(an, clazz))
                            }
                        }
                    }
                }
            }
            input.jarInputs.forEach { jar ->
                classPool.appendClassPath(jar.file.absolutePath)

                if (managerJar == null && PluginKit.isImplementsManager(jar.file)) {
                    managerJar = jar
                } else {
                    val dst = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
                    dst.safeDelete()
                    jar.file.copyTo(dst)

                    val jarFile = JarFile(jar.file)
                    val entries = jarFile.entries()

                    // 如果jar中的class中的类包含注解则先收集起来
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        var path = entry.name.replace("/", ".")
                        if (path.endsWith(".class")) {
                            path = path.substring(0, path.length - 6)
                            val clazz = classPool.get(path)
                            kotlin.runCatching {
                                val an = clazz.getAnnotation(Implements::class.java) as Implements?
                                if (an != null) {
                                    implementsList.add(ImplementRecord(an, clazz))
                                }
                            }
                        }
                    }
                }
            }
        }

        project.logger.info("implementsManagerJar = $managerJar")
        val config = parseConfig(implementsList)
        project.logger.info("config = $config")

        if (managerJar != null) {
            val managerCtClass = classPool.get(Consts.IMPLEMENTS_MANAGER_NAME)

            // 修改class，在class中插入静态代码块，做初始化
            val body = genMethodBody(config)

            project.logger.quiet("body = " + body.joinToString("\n"))

            if (managerCtClass.isFrozen) {
                managerCtClass.defrost()
            }
            managerCtClass.makeClassInitializer().setBody(body.joinToString("\n"))

            val jar = managerJar!!
            val dst = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
            project.logger.info(dst.absolutePath)

            // 修改完成后，完成后再写入到jar文件中
            rewriteJar(jar.file, dst, managerCtClass.toBytecode())
            project.logger.quiet("dst = $dst")
        }

        project.logger.quiet("${name}.transform time = ${System.currentTimeMillis() - now}ms")
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

    private fun parseConfig(implementsList: MutableList<ImplementRecord>): LinkedHashMap<String, String> {
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


    private fun rewriteJar(src: File, dst: File, bytes: ByteArray) {
        val name = Consts.IMPLEMENTS_MANAGER
        dst.parentFile.mkdirs()

        val target = JarOutputStream(FileOutputStream(dst))
        val srcFile = JarFile(src)
        for (entry in srcFile.entries()) {
            if (entry.name == name) {
                target.putNextEntry(JarEntry(name))
                target.write(bytes)
                continue
            }
            val data = srcFile.getInputStream(entry).use { it.readAllBytes() }
            target.putNextEntry(entry)
            target.write(data)
        }

        target.close()
    }
}