package cn.mycommons.module_plugin.ksp.process

import cn.mycommons.module_plugin.ksp.model.RouterParamConfig
import cn.mycommons.module_plugin.ksp.util.LogKit
import cn.mycommons.modulebase.annotations.RouterParam
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

class RouterParamProcess(private val codeGenerator: CodeGenerator) {

    @OptIn(KspExperimental::class)
    fun process(symbols: List<KSClassDeclaration>) {
        if (symbols.isEmpty()) {
            return
        }
        symbols.forEach {
            val fields = it.getAllProperties()
                .filter { p ->
                    // LogKit.warn("p = $p, pa = ${p.annotations.toList()}")
//                    p.annotations.any { a ->
//                        a.shortName.asString() == RouterParam::class.java.simpleName
//                    }
                    p.getAnnotationsByType(RouterParam::class).toList().isNotEmpty()
                }
                .map { p ->
                    val param = p.getAnnotationsByType(RouterParam::class).first()
                    // val ann = p.annotations.first { a -> a.shortName.asString() == RouterParam::class.java.simpleName }
                    RouterParamConfig(p, param)
                }
                .toList()

            LogKit.warn("class: $it, fields = $fields")
            genRouterParamInject(it, fields)
        }
    }

    private fun genRouterParamInject(
        clazz: KSClassDeclaration,
        fields: List<RouterParamConfig>,
    ) {
        val genClassName = "${clazz.simpleName.asString()}__RouterInject"
        val genPackageName = clazz.packageName.asString()
        val t = ClassName(clazz.packageName.asString(), clazz.simpleName.asString())

        val os = codeGenerator.createNewFile(Dependencies(false), genPackageName, genClassName)
        val fs = FileSpec.builder(genPackageName, genClassName)
            .addType(
                TypeSpec.objectBuilder(genClassName)
                    .addFunction(
                        FunSpec.builder("inject")
                            .addParameter("obj", t)
                            .addCode(genConfigMethodBody(fields))
                            .addAnnotation(JvmStatic::class)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("uninject")
                            .addParameter("obj", t)
                            .addCode("")
                            .addAnnotation(JvmStatic::class)
                            .build()
                    )
                    .addKdoc("generate by router ksp")
                    .build()
            ).build()

        os.bufferedWriter().use { fs.writeTo(it) }
    }

    private fun genConfigMethodBody(list: List<RouterParamConfig>): String {
        val sb = StringBuilder()
        // config["${it.uri}"] = ${it.clazzPackage}.${it.clazzName}::class.java
        list.forEach {
            val item = """
                obj.${it.field.simpleName.asString()} = obj.intent.getStringExtra("${it.annotation.name}")
            """.trimIndent()
            sb.append(item).append("\n")
        }
        return sb.toString()
    }
}