package cn.mycommons.implementstransformplugin

import cn.mycommons.modulebase.annotations.Implements
import com.android.build.api.transform.*
import com.google.common.collect.ImmutableSet
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * ImplementsTransform <br/>
 * Created by xiaqiulei on 2017-05-15.
 */
class ImplementsTransform extends Transform {

    static final String IMPLEMENTS_MANAGER = "cn/mycommons/modulebase/annotations/ImplementsManager.class"
    static final String IMPLEMENTS_MANAGER_NAME = "cn.mycommons.modulebase.annotations.ImplementsManager"
    Project project

    ImplementsTransform(Project project) {
        this.project = project
    }

    void log(String msg, Object... args) {
        String text = String.format(msg, args)

        project.getLogger().error("[ImplementsPlugin]:${text}")
    }

    @Override
    String getName() {
        return "ImplementsTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return ImmutableSet.of(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        long time1 = System.currentTimeMillis()
        log(this.toString() + ".....transform")

        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        outputProvider.deleteAll()

        def classPool = new ClassPool()
        classPool.appendSystemPath()

        // 记录所有的符合扫描条件的记录
        List<Entry> implementsList = []
        // ImplementsManager 注解所在的jar文件
        JarInput implementsManagerJar = null

        // 扫描所有的文件
        transformInvocation.inputs.each {
            it.directoryInputs.each {
                classPool.appendClassPath(it.file.absolutePath)
                def dst = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, dst)

                project.fileTree(dst).each {
                    String clazzPath = it.absolutePath.replace(dst.absolutePath, "")
                    clazzPath = clazzPath.replace("/", ".").substring(1)
                    if (clazzPath.endsWith(".class")) {
                        clazzPath = clazzPath.substring(0, clazzPath.size() - 6)
                        CtClass clazz = classPool.get(clazzPath)
                        // 如果class中的类包含注解则先收集起来
                        Implements annotation = clazz.getAnnotation(Implements.class)
                        if (annotation != null) {
                            implementsList.add(new Entry(annotation, clazz))
                        }
                    }
                }
            }
            it.jarInputs.each {
                classPool.appendClassPath(it.file.absolutePath)

                if (implementsManagerJar == null && isImplementsManager(it.file)) {
                    implementsManagerJar = it
                } else {
                    def dst = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.JAR)
                    FileUtils.copyFile(it.file, dst)

                    def jarFile = new JarFile(it.file)
                    def entries = jarFile.entries()

                    // 如果jar中的class中的类包含注解则先收集起来
                    while (entries.hasMoreElements()) {
                        def jarEntry = entries.nextElement()
                        String clazzPath = jarEntry.getName()
                        clazzPath = clazzPath.replace("/", ".")
                        if (clazzPath.endsWith(".class")) {
                            clazzPath = clazzPath.substring(0, clazzPath.size() - 6)
                            def clazz = classPool.get(clazzPath)
                            Implements annotation = clazz.getAnnotation(Implements.class)
                            if (annotation != null) {
                                implementsList.add(new Entry(annotation, clazz))
                            }
                        }
                    }
                }
            }
        }

        log("implementsManagerJar = " + implementsManagerJar)

        Map<String, String> config = new LinkedHashMap<>()

        implementsList.each {
            def str = it.anImplements.toString()
            log("anImplements =" + it.anImplements)
            def parent = str.substring(str.indexOf("(") + 1, str.indexOf(")")).replace("parent=", "").replace(".class", "")
            log("parent =" + parent)
            log("sub =" + it.ctClass.name)

            // 收集所有的接口以及实现类的路径
            config.put(parent, it.ctClass.name)
        }

        log("config = " + config)

        long time2 = System.currentTimeMillis()

        if (implementsManagerJar != null) {
            def implementsManagerCtClass = classPool.get(IMPLEMENTS_MANAGER_NAME)
            log("implementsManagerCtClass = " + implementsManagerCtClass)

            // 修改class，在class中插入静态代码块，做初始化
            def body = "{\n"
            body += "CONFIG = new java.util.HashMap();\n"

            for (Map.Entry<String, String> entry : config.entrySet()) {
                body += "CONFIG.put(${entry.key}.class, ${entry.value}.class);\n"
            }

            body += "}\n"
            log("body = " + body)

            implementsManagerCtClass.makeClassInitializer().body = body

            def jar = implementsManagerJar
            def dst = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
            println dst.absolutePath

            // 修改完成后，完成后再写入到jar文件中
            rewriteJar(implementsManagerJar.file, dst, IMPLEMENTS_MANAGER, implementsManagerCtClass.toBytecode())
        }

        log("time = " + (time2 - time1) / 1000)
    }

    static boolean isImplementsManager(File file) {
        return new JarFile(file).getEntry(IMPLEMENTS_MANAGER) != null
    }

    static void rewriteJar(File src, File dst, String name, byte[] bytes) {
        dst.getParentFile().mkdirs()

        def jarOutput = new JarOutputStream(new FileOutputStream(dst))
        def rcJarFile = new JarFile(src)

        jarOutput.putNextEntry(new JarEntry(name))
        jarOutput.write(bytes)

        def buffer = new byte[1024]
        int bytesRead
        def entries = rcJarFile.entries()

        while (entries.hasMoreElements()) {
            def entry = entries.nextElement()
            if (entry.name == name) continue
            jarOutput.putNextEntry(entry)

            def jarInput = rcJarFile.getInputStream(entry)
            while ((bytesRead = jarInput.read(buffer)) != -1) {
                jarOutput.write(buffer, 0, bytesRead)
            }
            jarInput.close()
        }

        jarOutput.close()
    }
}