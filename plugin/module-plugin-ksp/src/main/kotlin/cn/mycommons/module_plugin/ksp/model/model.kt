package cn.mycommons.module_plugin.ksp.model

import cn.mycommons.modulebase.annotations.RouterParam
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
    val self: KSClassDeclaration,
    val parent: KSType,
) {

    override fun toString(): String {
        return "ServiceConfig(clazz=$self, annotation=${parent})"
    }
}

data class RouterParamConfig(
    val field: KSPropertyDeclaration,
    val annotation: RouterParam,
) {

    override fun toString(): String {
        return "RouterParamConfig(field=$field, annotation=$annotation)"
    }
}