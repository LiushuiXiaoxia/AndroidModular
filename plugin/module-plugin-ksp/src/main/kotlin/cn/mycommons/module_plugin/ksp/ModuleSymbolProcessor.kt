package cn.mycommons.module_plugin.ksp

import cn.mycommons.module_plugin.ksp.process.ModuleConfigProcess
import cn.mycommons.module_plugin.ksp.process.RouterParamProcess
import cn.mycommons.module_plugin.ksp.util.LogKit
import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.modulebase.annotations.Router
import cn.mycommons.modulebase.annotations.RouterParam
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.google.gson.GsonBuilder
import java.util.concurrent.atomic.AtomicInteger

class ModuleSymbolProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {

    private val idx = AtomicInteger(0)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        LogKit.warn("~~~ ${KspConsts.PLUGIN_NAME} process(${idx.getAndIncrement()}) ~~~")

        resolver.getAllFiles().forEach {
            LogKit.warn("allFiles: ${it.fileName}")
        }
        resolver.getNewFiles().forEach {
            LogKit.warn("newFiles: ${it.fileName}")
        }

        val routerList = resolver.getSymbolsWithAnnotation(Router::class.java.name)
            .filter { !it.validate() }
            .filterIsInstance<KSClassDeclaration>()
            .toList()
        val serviceList = resolver.getSymbolsWithAnnotation(Implements::class.java.name)
            .filter { !it.validate() }
            .filterIsInstance<KSClassDeclaration>()
            .toList()
        PluginContextKit.saveModuleConfig(routerList, serviceList)

        val routerParams = resolver.getSymbolsWithAnnotation(RouterParam::class.java.name)
            .map { it.parent }
            .filter { it != null && it is KSClassDeclaration }
            .map { it as KSClassDeclaration }
            .distinct()
            .toList()
        PluginContextKit.saveRouterParam(routerParams)

        return listOf()
    }

    override fun finish() {
        genCode()

        LogKit.warn("~~~ ${KspConsts.PLUGIN_NAME} finish ~~~")
    }

    private fun genCode() {
        genRes()
        PluginContextKit.apply {
            ModuleConfigProcess(codeGenerator).process(routerList, serviceList)
            RouterParamProcess(codeGenerator).process(routerParams)
        }

        codeGenerator.generatedFile.forEach {
            LogKit.warn("generatedFile = $it")
        }
    }

    private fun genRes() {
        val fs = codeGenerator.createNewFileByPath(
            Dependencies(false),
            "META-INF/module/${PluginContextKit.moduleName}.meta",
            "json"
        )
        val map = mutableMapOf("hello" to "world")
        fs.bufferedWriter().use {
            it.write(GsonBuilder().setPrettyPrinting().create().toJson(map))
        }
    }
}