package cn.mycommons.module_plugin.ksp.process

import cn.mycommons.module_plugin.ksp.KspConsts
import cn.mycommons.module_plugin.ksp.PluginKit
import cn.mycommons.module_plugin.ksp.model.RouterConfig
import cn.mycommons.module_plugin.ksp.model.ServiceConfig
import cn.mycommons.module_plugin.ksp.util.LogKit
import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.modulebase.annotations.Router
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf

class RouterProcess(private val codeGenerator: CodeGenerator) {

    fun process(routers: Sequence<KSClassDeclaration>, services: Sequence<KSClassDeclaration>) {
        val routerList = mutableListOf<RouterConfig>()
        routers.forEach {
            val an = it.annotations.firstOrNull { a -> a.shortName.asString() == Router::class.java.simpleName }
            if (an != null) {
                val uri = an.arguments.firstOrNull { a -> a.name?.asString() == "uri" }?.value
                if (uri != null) {
                    val config = RouterConfig(uri.toString(), it.packageName.asString(), it.simpleName.asString())
                    routerList.add(config)
                }
            }
        }
        val serviceList = mutableListOf<ServiceConfig>()
        services.forEach {
            val an = it.annotations.firstOrNull { a -> a.shortName.asString() == Implements::class.java.simpleName }
            if (an != null) {
                val parent = an.arguments.firstOrNull { a -> a.name?.asString() == "parent" }?.value
                if (parent != null && parent is KSType) {
                    val config = ServiceConfig(it, parent)
                    serviceList.add(config)
                }
            }
        }

        if (routerList.isEmpty()) {
            return
        }

        LogKit.warn("routerList = $routerList")
        LogKit.warn("serviceList = $serviceList")

        genRouterConfig(routerList, serviceList)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun genRouterConfig(
        routerList: MutableList<RouterConfig>,
        serviceList: MutableList<ServiceConfig>,
    ) {
        val genClassName = "${PluginKit.moduleName}__ModuleConfigGen"
        val genPackageName = "${KspConsts.INTERNAL_PKG}.gen"

        val os = codeGenerator.createNewFile(Dependencies(false), genPackageName, genClassName)
        val fs = FileSpec.builder(genPackageName, genClassName)
            .addType(
                TypeSpec.classBuilder(genClassName)
                    .addFunction(
                        FunSpec.builder("router")
                            .returns(typeNameOf<Map<String, Class<*>>>())
                            .addCode(genMethodRouterBody(routerList))
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("service")
                            .returns(typeNameOf<Map<Class<*>, Class<*>>>())
                            .addCode(genMethodServiceBody(serviceList))
                            .build()
                    )
                    .addKdoc("generate by router ksp").build()
            ).apply {
                routerList.forEach { addImport(it.clazzPackage, it.clazzName) }
            }.build()

        os.bufferedWriter().use { fs.writeTo(it) }
    }

    private fun genMethodRouterBody(list: List<RouterConfig>): String {
        val sb = StringBuilder()
        sb.append("val map = HashMap<String, Class<*>>()").append("\n")
        list.forEach {
            val item = """
                map["${it.uri}"] = ${it.clazzPackage}.${it.clazzName}::class.java
            """.trimIndent()
            sb.append(item).append("\n")
        }
        sb.append("return map").append("\n")
        return sb.toString()
    }

    private fun genMethodServiceBody(list: List<ServiceConfig>): String {
        val sb = StringBuilder()
        sb.append("val map = HashMap<Class<*>, Class<*>>()").append("\n")
        list.forEach {
            val parent =
                "${it.parent.declaration.packageName.asString()}.${it.parent.declaration.simpleName.asString()}"
            val self = "${it.clazz.packageName.asString()}.${it.clazz.simpleName.asString()}"

            val item = """
                map[${parent}::class.java] = ${self}::class.java
            """.trimIndent()
            sb.append(item).append("\n")
        }
        sb.append("return map").append("\n")
        return sb.toString()
    }
}