package cn.mycommons.module_plugin.ksp

object PluginKit {

    lateinit var modulePackage: String
    lateinit var moduleName: String

    fun setup(pkg: String, name: String) {
        this.modulePackage = pkg
        this.moduleName = name
    }
}