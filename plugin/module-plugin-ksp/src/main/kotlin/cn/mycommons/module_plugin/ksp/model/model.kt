package cn.mycommons.module_plugin.ksp.model

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType


data class RouterConfig(
    val uri: String,
    val clazzPackage: String,
    val clazzName: String,
) {

    override fun toString(): String {
        return "RouterConfig(uri='$uri', clazzPackage='$clazzPackage', clazzName='$clazzName')"
    }
}

data class ServiceConfig(
    val clazz: KSClassDeclaration,
    val parent: KSType,
) {

    override fun toString(): String {
        return "ServiceConfig(clazz=$clazz, parent=${parent.javaClass})"
    }
}

data class RouterParamConfig(
    val field: KSPropertyDeclaration,
    val annotation: KSAnnotation,
) {

    override fun toString(): String {
        return "RouterParamConfig(field=$field, annotation=$annotation)"
    }
}