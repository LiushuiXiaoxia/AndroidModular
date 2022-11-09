package cn.mycommons.module_plugin

import cn.mycommons.modulebase.annotations.Implements
import com.android.build.api.transform.*
import com.google.common.collect.ImmutableSet
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
class ImplementsTransform(val project: Project) : Transform() {

    companion object {
        const val IMPLEMENTS_MANAGER = "cn/mycommons/modulebase/annotations/ImplementsManager.class"
        const val IMPLEMENTS_MANAGER_NAME = "cn.mycommons.modulebase.annotations.ImplementsManager"
    }

    private fun log(msg: String, vararg args: Any) {
        val text = String.format(msg, args)
        project.logger.error("[ImplementsPlugin]:${text}")
    }

    override fun getName(): String = "ImplementsTransform"

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return ImmutableSet.of(QualifiedContent.DefaultContentType.CLASSES)
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return mutableSetOf(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    override fun isIncremental() = false


    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        val time1 = System.currentTimeMillis()
        log("$this .....transform")

        val outputProvider: TransformOutputProvider = transformInvocation.outputProvider
        outputProvider.deleteAll()

        val classPool = ClassPool()
        classPool.appendSystemPath()

        // 记录所有的符合扫描条件的记录
        val implementsList = mutableListOf<Entry>()
        // ImplementsManager 注解所在的jar文件
        var implementsManagerJar: JarInput? = null

        // 扫描所有的文件
        transformInvocation.inputs.forEach {
            it.directoryInputs.forEach { dir ->
                classPool.appendClassPath(dir.file.absolutePath)
                val dst = outputProvider.getContentLocation(dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY)
                // FileUtils.copyDirectory(it.file, dst)
                // Files.copy(di.file, dst)
                dst.mkdirs()
                dir.file.copyRecursively(dst)

                project.fileTree(dst).forEach { f ->
                    var clazzPath = f.absolutePath.replace(dst.absolutePath, "")
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
            it.jarInputs.forEach { ji ->
                classPool.appendClassPath(ji.file.absolutePath)

                if (implementsManagerJar == null && isImplementsManager(ji.file)) {
                    implementsManagerJar = ji
                } else {
                    val dst = outputProvider.getContentLocation(ji.name, ji.contentTypes, ji.scopes, Format.JAR)
                    // FileUtils.copyFile(it.file, dst)
                    // Files.copy(ji.file, dst)

                    val jarFile = JarFile(ji.file)
                    val entries = jarFile.entries()

                    // 如果jar中的class中的类包含注解则先收集起来
                    while (entries.hasMoreElements()) {
                        val jarEntry = entries.nextElement()
                        var clazzPath = jarEntry.getName()
                        clazzPath = clazzPath.replace("/", ".")
                        if (clazzPath.endsWith(".class")) {
                            clazzPath = clazzPath.substring(0, clazzPath.length - 6)
                            val clazz = classPool.get(clazzPath)
                            val annotation = clazz.getAnnotation(Implements::class.java) as Implements?
                            if (annotation != null) {
                                implementsList.add(Entry(annotation, clazz))
                            }
                        }
                    }
                }
            }
        }

        log("implementsManagerJar = " + implementsManagerJar)

        val config = linkedMapOf<String, String>()

        implementsList.forEach {
            val str = it.anImplements.toString()
            log("anImplements =" + it.anImplements)
            val parent =
                str.substring(str.indexOf("(") + 1, str.indexOf(")")).replace("parent=", "").replace(".class", "")
            log("parent =" + parent)
            log("sub =" + it.ctClass.name)

            // 收集所有的接口以及实现类的路径
            config.put(parent, it.ctClass.name)
        }

        log("config = " + config)

        val time2 = System.currentTimeMillis()

        if (implementsManagerJar != null) {
            val implementsManagerCtClass = classPool.get(IMPLEMENTS_MANAGER_NAME)
            log("implementsManagerCtClass = " + implementsManagerCtClass)

            // 修改class，在class中插入静态代码块，做初始化
            var body = "{\n"
            body += "CONFIG =  java.util.HashMap();\n"

//            for (Map. Entry<String, String> entry : config . entrySet ()) {
//                body += "CONFIG.put(${entry.key}.class, ${entry.value}.class);\n"
//            }
            config.forEach {
                body += "CONFIG.put(${it.key}.class, ${it.value}.class);\n"
            }

            body += "}\n"
            log("body = " + body)

            implementsManagerCtClass.makeClassInitializer().setBody(body)

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