//package cn.mycommons.module_plugin.core.v2
//
//import com.android.build.api.artifact.MultipleArtifact
//import com.android.build.api.variant.AndroidComponentsExtension
//import javassist.ClassPool
//import org.gradle.api.DefaultTask
//import org.gradle.api.Project
//import org.gradle.api.file.DirectoryProperty
//import org.gradle.api.tasks.OutputFiles
//import org.gradle.api.tasks.TaskAction
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//
//abstract class AddClassesTask : DefaultTask() {
//
//    companion object {
//
//        fun setup(project: Project, ac: AndroidComponentsExtension<*, *, *>) {
//            ac.onVariants {
//                val taskProvider = project.tasks.register("${it.name}AddClassess", AddClassesTask::class.java)
//                it.artifacts.use<AddClassesTask>(taskProvider)
//                    .wiredWith(AddClassesTask::output)
//                    .toAppendTo(MultipleArtifact.ALL_CLASSES_DIRS)
//            }
//        }
//    }
//
//    private val log: Logger = LoggerFactory.getLogger(javaClass)
//
//    @get:OutputFiles
//    abstract val output: DirectoryProperty
//
//    @TaskAction
//    fun taskAction() {
//        log.error("Adding ${output.asFile.get().absolutePath}")
//
//        val pool = ClassPool(ClassPool.getDefault())
//
//        val interfaceClass = pool.makeInterface("aaa.android.api.tests.SomeInterface")
//        log.error("Adding $interfaceClass")
//        interfaceClass.writeFile(output.get().asFile.absolutePath)
//    }
//}