package cn.mycommons.module_plugin.ksp

import cn.mycommons.module_plugin.ksp.process.RouterParamProcess
import cn.mycommons.module_plugin.ksp.process.RouterProcess
import cn.mycommons.module_plugin.ksp.util.LogKit
import cn.mycommons.modulebase.annotations.Router
import cn.mycommons.modulebase.annotations.RouterParam
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ModuleSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // val symbols = resolver.getSymbolsWithAnnotation(RouterParam::class.java.name).filterIsInstance<KSClassDeclaration>()
        val list = resolver.getSymbolsWithAnnotation(Router::class.java.name).filterIsInstance<KSClassDeclaration>()
        RouterProcess(codeGenerator).process(list)

//        resolver.getAllFiles().forEach {
//            LogKit.warn("files: $it")
//            LogKit.warn("declarations: ${it.declarations.toList()}")
//            // it.accept(RouterParamKSVisitorVoid(), Unit)
//        }

        val list2 = resolver.getSymbolsWithAnnotation(RouterParam::class.java.name).map { it.parent }
            .filterNotNull()
            .filter { it is KSClassDeclaration }
            .distinct()


        val list3 = list2.toList()
            //.flatMap { it.annotations.toList() }
            .map {
                "${it.origin} ~ ${it.parent?.javaClass}"
            }
            .joinToString("\n")
        LogKit.warn("list2 = ${list2.toList()}")
        LogKit.warn("list3 = $list3")

        RouterParamProcess(codeGenerator).process(list.toList())

        return listOf()
    }


    override fun finish() {
        LogKit.warn("========")
    }
}