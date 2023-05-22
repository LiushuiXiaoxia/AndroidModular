package cn.mycommons.module_plugin.ksp

import cn.mycommons.module_plugin.ksp.util.LogKit
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ModuleProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        LogKit.setup(environment.logger)
        LogKit.warn("options = ${environment.options}")

        val pkg = environment.options[KspConsts.MODULE_PACKAGE]
        val name = environment.options[KspConsts.MODULE_NAME]

        if (pkg.isNullOrBlank() || name.isNullOrBlank()) {
            throw RuntimeException("project must be assign moduleName and modulePackage")
        }

        PluginContextKit.setup(pkg, name)

        return ModuleSymbolProcessor(
            environment.codeGenerator
        )
    }
}