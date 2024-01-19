package cn.mycommons.module_plugin.v2

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import javassist.ClassPool
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
import java.io.BufferedOutputStream
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

        val pool = ClassPool(ClassPool.getDefault())

        val jarOutput = JarOutputStream(BufferedOutputStream(FileOutputStream(output.get().asFile)))
        allJars.get().forEach { file ->
            log.error("handling " + file.asFile.getAbsolutePath())
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                log.error("Adding from jar ${jarEntry.name}")
                jarOutput.putNextEntry(JarEntry(jarEntry.name))
                jarFile.getInputStream(jarEntry).use {
                    it.copyTo(jarOutput)
                }
                jarOutput.closeEntry()
            }
            jarFile.close()
        }
        allDirectories.get().forEach { directory ->
            log.error("handling " + directory.asFile.getAbsolutePath())
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {
                    if (file.name.endsWith("SomeSource.class")) {
                        log.error("Found $file.name")
                        val interfaceClass = pool.makeInterface("com.android.api.tests.SomeInterface");
                        log.error("Adding $interfaceClass")
                        jarOutput.putNextEntry(JarEntry("com/android/api/tests/SomeInterface.class"))
                        jarOutput.write(interfaceClass.toBytecode())
                        jarOutput.closeEntry()
                        val ctClass = file.inputStream().use {
                            pool.makeClass(it)
                        }
                        ctClass.addInterface(interfaceClass)

                        val m = ctClass.getDeclaredMethod("toString");
                        if (m != null) {
                            m.insertBefore("{ System.out.println(\"Some Extensive Tracing\"); }");

                            val relativePath = directory.asFile.toURI().relativize(file.toURI()).getPath()
                            jarOutput.putNextEntry(JarEntry(relativePath.replace(File.separatorChar, '/')))
                            jarOutput.write(ctClass.toBytecode())
                            jarOutput.closeEntry()
                        } else {
                            addClass(directory, file, jarOutput)
                        }
                    } else {
                        addClass(directory, file, jarOutput)
                    }
                }
            }
        }
        jarOutput.close()
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