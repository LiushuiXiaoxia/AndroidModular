package cn.mycommons.module_plugin.ksp.process

import cn.mycommons.module_plugin.ksp.KspConsts
import cn.mycommons.module_plugin.ksp.PluginContextKit
import cn.mycommons.module_plugin.ksp.model.RouterConfig
import cn.mycommons.module_plugin.ksp.model.ServiceConfig
import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.modulebase.annotations.Router
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf

class ModuleConfigProcess(private val codeGenerator: CodeGenerator) {

    @OptIn(KspExperimental::class)
    fun process(routers: List<KSClassDeclaration>, services: List<KSClassDeclaration>) {
        val routerList = mutableListOf<RouterConfig>()
        routers.forEach {
            val an = it.getAnnotationsByType(Router::class).firstOrNull()
            if (an != null) {
                val config = RouterConfig(an.uri, it.packageName.asString(), it.simpleName.asString())
                routerList.add(config)
            }
        }

        val serviceList = mutableListOf<ServiceConfig>()
        services.forEach {
            // val an = it.getAnnotationsByType(Implements::class).firstOrNull()
            val an = it.annotations.firstOrNull { a -> a.shortName.asString() == Implements::class.java.simpleName }
            if (an != null) {
                val parent = an.arguments.firstOrNull { a -> a.name?.asString() == "parent" }?.value
                if (parent != null && parent is KSType) {
                    val config = ServiceConfig(it, parent)
                    serviceList.add(config)
                }
            }
        }

        genRouterConfig(routerList, serviceList)
    }

    private fun genRouterConfig(
        routerList: MutableList<RouterConfig>,
        serviceList: MutableList<ServiceConfig>,
    ) {
        val genClassName = "${PluginContextKit.moduleName}__ModuleConfigGen"
        val genPackageName = "${KspConsts.INTERNAL_PKG}.gen"

        val os = codeGenerator.createNewFile(Dependencies(false), genPackageName, genClassName)
        val fs = FileSpec.builder(genPackageName, genClassName)
            .addType(
                TypeSpec.classBuilder(genClassName)
                    .addFunction(genMethodRouter(routerList))
                    .addFunction(genMethodService(serviceList))
                    .addKdoc("generate by router ksp")
                    .build()
            ).apply {
                routerList.forEach { addImport(it.clazzPackage, it.clazzName) }
            }.build()

        os.bufferedWriter().use { fs.writeTo(it) }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun genMethodRouter(list: List<RouterConfig>): FunSpec {
        val lines = mutableListOf<String>()
        lines.add("val map = HashMap<String, Class<*>>()")
        list.forEach {
            val item = """
                map["${it.uri}"] = ${it.clazzPackage}.${it.clazzName}::class.java
            """.trimIndent()
            lines.add(item)
        }
        lines.add("return map")

        return FunSpec.builder("router")
            .addKdoc("router config")
            .addCode(lines.joinToString("\n"))
            .returns(typeNameOf<Map<String, Class<*>>>())
            .build()
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun genMethodService(list: List<ServiceConfig>): FunSpec {
        val lines = mutableListOf<String>()
        lines.add("val map = HashMap<Class<*>, Class<*>>()")
        list.forEach {
            val parent =
                "${it.parent.declaration.packageName.asString()}.${it.parent.declaration.simpleName.asString()}"
            val self = "${it.self.packageName.asString()}.${it.self.simpleName.asString()}"

            val item = """
                map[${parent}::class.java] = ${self}::class.java
            """.trimIndent()
            lines.add(item)
        }
        lines.add("return map")

        return FunSpec.builder("service")
            .addKdoc("module config")
            .addCode(lines.joinToString("\n"))
            .returns(typeNameOf<Map<Class<*>, Class<*>>>())
            .build()
    }
}