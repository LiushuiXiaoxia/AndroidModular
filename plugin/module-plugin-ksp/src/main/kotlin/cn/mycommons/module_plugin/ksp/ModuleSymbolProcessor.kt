package cn.mycommons.module_plugin.ksp

import cn.mycommons.module_plugin.ksp.process.RouterParamProcess
import cn.mycommons.module_plugin.ksp.process.ModuleConfigProcess
import cn.mycommons.module_plugin.ksp.util.LogKit
import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.modulebase.annotations.Router
import cn.mycommons.modulebase.annotations.RouterParam
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.util.concurrent.atomic.AtomicInteger

class ModuleSymbolProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {

    private val idx = AtomicInteger(0)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        LogKit.warn("~~~ ${KspConsts.PLUGIN_NAME} process(${idx.getAndIncrement()}) ~~~")

        resolver.getAllFiles().forEach {
            LogKit.warn("files: ${it.fileName}")
        }

        val routerList = resolver.getSymbolsWithAnnotation(Router::class.java.name).filterIsInstance<KSClassDeclaration>()
        val serviceList = resolver.getSymbolsWithAnnotation(Implements::class.java.name).filterIsInstance<KSClassDeclaration>()
        ModuleConfigProcess(codeGenerator).process(routerList, serviceList)

        val list2 = resolver.getSymbolsWithAnnotation(RouterParam::class.java.name)
        val list3 = list2.map { it.parent }
            .filter { it != null && it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .distinct()
            .toList()
        RouterParamProcess(codeGenerator).process(list3)

        return listOf()
    }

    override fun finish() {
        LogKit.warn("~~~ ${KspConsts.PLUGIN_NAME} finish ~~~")
    }
}