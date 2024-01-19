package cn.mycommons.module_plugin.v2

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter

interface ExampleParams : InstrumentationParameters {
    @get:Input
    val writeToStdout: Property<Boolean>
}

abstract class ExampleClassVisitorFactory : AsmClassVisitorFactory<ExampleParams> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return if (parameters.get().writeToStdout.get()) {
            TraceClassVisitor(nextClassVisitor, PrintWriter(System.out))
        } else {
            TraceClassVisitor(nextClassVisitor, PrintWriter(File("build/trace_out-${System.currentTimeMillis()}")))
        }
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        // return classData.className.startsWith("com.example")
        return true
    }
}