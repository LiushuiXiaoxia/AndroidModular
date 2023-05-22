package cn.mycommons.module_plugin.ksp.process

import cn.mycommons.module_plugin.ksp.KspConsts
import cn.mycommons.module_plugin.ksp.PluginKit
import cn.mycommons.module_plugin.ksp.model.RouterConfig
import cn.mycommons.module_plugin.ksp.util.LogKit
import cn.mycommons.modulebase.annotations.Router
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf

class RouterProcess(private val codeGenerator: CodeGenerator) {

    fun process(symbols: Sequence<KSClassDeclaration>) {
        val configList = mutableListOf<RouterConfig>()
        symbols.forEach {
            val an = it.annotations.firstOrNull { a -> a.shortName.asString() == Router::class.java.simpleName }
            if (an != null) {
                val uri = an.arguments.firstOrNull { a -> a.name?.asString() == "uri" }?.value
                if (uri != null) {
                    val config = RouterConfig(uri.toString(), it.packageName.asString(), it.simpleName.asString())
                    configList.add(config)
                }
            }
        }

        if (configList.isEmpty()) {
            return
        }

        LogKit.warn("config = $configList")
        genRouterConfig(configList)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun genRouterConfig(configList: MutableList<RouterConfig>) {
        val genClassName = "${PluginKit.moduleName}__ModuleConfigGen"
        val genPackageName = "${KspConsts.INTERNAL_PKG}.gen"

        val os = codeGenerator.createNewFile(Dependencies(false), genPackageName, genClassName)
        val fs = FileSpec.builder(genPackageName, genClassName)
            .addType(
                TypeSpec.classBuilder(genClassName)
                    .addFunction(
                        FunSpec.builder("router")
                            .returns(typeNameOf<Map<String, Class<*>>>())
                            .addCode(genConfigMethodBody(configList))
                            .build()
                    )
                    .addKdoc("generate by router ksp").build()
            ).apply {
                configList.forEach { addImport(it.clazzPackage, it.clazzName) }
            }.build()

        os.bufferedWriter().use { fs.writeTo(it) }
    }

    private fun genConfigMethodBody(list: List<RouterConfig>): String {
        val sb = StringBuilder()
        sb.append("val config = HashMap<String, Class<*>>()").append("\n")
        list.forEach {
            val item = """
                config["${it.uri}"] = ${it.clazzPackage}.${it.clazzName}::class.java
            """.trimIndent()
            sb.append(item).append("\n")
        }
        sb.append("return config").append("\n")
        return sb.toString()
    }
}