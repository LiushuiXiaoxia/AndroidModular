package cn.mycommons.module_plugin.ksp.model

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration


data class RouterConfig(
    val uri: String,
    val clazzPackage: String,
    val clazzName: String,
) {

    override fun toString(): String {
        return "RouterConfig(uri='$uri', clazzPackage='$clazzPackage', clazzName='$clazzName')"
    }
}

data class RouterParamConfig(
    val field: KSPropertyDeclaration,
    val annotation: KSAnnotation,
) {

}