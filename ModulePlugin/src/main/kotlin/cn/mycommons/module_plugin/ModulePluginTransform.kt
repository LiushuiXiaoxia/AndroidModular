package cn.mycommons.module_plugin

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

    companion object {
        const val IMPLEMENTS_MANAGER = "cn/mycommons/modulebase/annotations/ImplementsManager.class"
        const val IMPLEMENTS_MANAGER_NAME = "cn.mycommons.modulebase.annotations.ImplementsManager"
    }

    private fun log(msg: String, vararg args: Any) {
        val text = String.format(msg, args)
        project.logger.error("[ImplementsPlugin]:${text}")
    }

    override fun getName(): String = "ModulePlugin"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_JARS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental() = false

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        val time1 = System.currentTimeMillis()
        log("$this .....transform")

        val outputProvider: TransformOutputProvider = transformInvocation.outputProvider
        outputProvider.deleteAll()

        val classPool = ClassPool.getDefault()
        classPool.appendSystemPath()

        // 记录所有的符合扫描条件的记录
        val implementsList = mutableListOf<Entry>()
        // ImplementsManager 注解所在的jar文件
        var implementsManagerJar: JarInput? = null

        // 扫描所有的文件
        transformInvocation.inputs.forEach { input ->
            input.directoryInputs.forEach { dir ->
                classPool.appendClassPath(dir.file.absolutePath)
                val dst = outputProvider.getContentLocation(dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY)
                dst.safeDelete()
                dst.mkdirs()
                dir.file.copyRecursively(dst)

                project.fileTree(dst).forEach {
                    var clazzPath = it.absolutePath.replace(dst.absolutePath, "")
                    clazzPath = clazzPath.replace("/", ".").substring(1)
                    if (clazzPath.endsWith(".class")) {
                        clazzPath = clazzPath.substring(0, clazzPath.length - 6)
                        val clazz: CtClass = classPool.get(clazzPath)
                        // 如果class中的类包含注解则先收集起来
                        val annotation = clazz.getAnnotation(Implements::class.java) as Implements?
                        if (annotation != null) {
                            implementsList.add(Entry(annotation, clazz))
                        }
                    }
                }
            }
            input.jarInputs.forEach { jar ->
                classPool.appendClassPath(jar.file.absolutePath)

                if (implementsManagerJar == null && isImplementsManager(jar.file)) {
                    implementsManagerJar = jar
                } else {
                    val dst = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
                    dst.safeDelete()
                    jar.file.copyTo(dst)

                    val jarFile = JarFile(jar.file)
                    val entries = jarFile.entries()

                    // 如果jar中的class中的类包含注解则先收集起来
                    while (entries.hasMoreElements()) {
                        val jarEntry = entries.nextElement()
                        var clazzPath = jarEntry.name.replace("/", ".")
                        if (clazzPath.endsWith(".class")) {
                            clazzPath = clazzPath.substring(0, clazzPath.length - 6)
                            val clazz = classPool.get(clazzPath)
                            kotlin.runCatching {
                                val annotation = clazz.getAnnotation(Implements::class.java) as Implements?
                                if (annotation != null) {
                                    implementsList.add(Entry(annotation, clazz))
                                }
                            }
                        }
                    }
                }
            }
        }

        log("implementsManagerJar = $implementsManagerJar")

        val config = linkedMapOf<String, String>()

        implementsList.forEach {
            val str = it.anImplements.toString()
            log("anImplements =" + it.anImplements)
            val parent =
                str.substring(str.indexOf("(") + 1, str.indexOf(")")).replace("parent=", "").replace(".class", "")
            log("parent =$parent, sub =" + it.ctClass.name)

            // 收集所有的接口以及实现类的路径
            config[parent] = it.ctClass.name
        }

        log("config = $config")

        val time2 = System.currentTimeMillis()

        if (implementsManagerJar != null) {
            val implementsManagerCtClass = classPool.get(IMPLEMENTS_MANAGER_NAME)
            log("implementsManagerCtClass = $implementsManagerCtClass")

            // 修改class，在class中插入静态代码块，做初始化
            val body = mutableListOf<String>()
            body.add("{")
            body.add("CONFIG = new java.util.HashMap();")
            config.forEach {
                body.add("CONFIG.put(${it.key}.class, ${it.value}.class);")
            }
            body.add("}")

            log("body = " + body.joinToString("\n"))

            implementsManagerCtClass.makeClassInitializer().setBody(body.joinToString("\n"))

            val jar = implementsManagerJar!!
            val dst = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
            project.logger.info(dst.absolutePath)

            // 修改完成后，完成后再写入到jar文件中
            rewriteJar(implementsManagerJar!!.file, dst, IMPLEMENTS_MANAGER, implementsManagerCtClass.toBytecode())
        }

        log("time = " + (time2 - time1) / 1000)
    }

    fun isImplementsManager(file: File): Boolean {
        return JarFile(file).getEntry(IMPLEMENTS_MANAGER) != null
    }

    fun rewriteJar(src: File, dst: File, name: String, bytes: ByteArray) {
        dst.parentFile.mkdirs()

        val jarOutput = JarOutputStream(FileOutputStream(dst))
        val rcJarFile = JarFile(src)

        jarOutput.putNextEntry(JarEntry(name))
        jarOutput.write(bytes)

        val entries = rcJarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name == name) {
                continue
            }
            jarOutput.putNextEntry(entry)
            val jarInput = rcJarFile.getInputStream(entry)
            jarOutput.write(jarInput.readAllBytes())
            jarInput.close()
        }

        jarOutput.close()
    }
}