package cn.mycommons.module_plugin.ksp

import java.io.File
import java.util.jar.JarFile

object PluginKit {

    fun isImplementsManager(file: File): Boolean {
        return JarFile(file).getEntry(Consts.IMPLEMENTS_MANAGER) != null
    }
}