package cn.mycommons.module_plugin.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration

object PluginContextKit {

    lateinit var modulePackage: String
    lateinit var moduleName: String

    val routerList: MutableList<KSClassDeclaration> = mutableListOf()
    val serviceList: MutableList<KSClassDeclaration> = mutableListOf()

    val routerParams: MutableList<KSClassDeclaration> = mutableListOf()

    fun setup(pkg: String, name: String) {
        this.modulePackage = pkg
        this.moduleName = name
    }

    fun saveModuleConfig(routers: List<KSClassDeclaration>, services: List<KSClassDeclaration>) {
        routerList.addAll(routers)
        serviceList.addAll(services)
    }

    fun saveRouterParam(symbols: List<KSClassDeclaration>) {
        routerParams.addAll(symbols)
    }
}