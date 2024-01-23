package cn.mycommons.module_plugin.core

import java.io.File
import java.util.jar.JarFile

object PluginKit {

    fun isImplementsManager(file: File): Boolean {
        return JarFile(file).getEntry(Consts.IMPLEMENTS_MANAGER) != null
    }

    fun genConfigMethod(list: List<ImplementRecord>): List<String> {
        val config = parseConfig(list)
        return genMethodBody(config)
    }

    private fun parseConfig(list: List<ImplementRecord>): LinkedHashMap<String, String> {
        val config = linkedMapOf<String, String>()
        list.forEach {
            val str = it.anImplements.toString()
            val parent = str.substring(str.indexOf("(") + 1, str.indexOf(")"))
                .replace("parent=", "").replace(".class", "")

            // 收集所有的接口以及实现类的路径
            config[parent] = it.ctClass.name
        }
        return config
    }

    private fun genMethodBody(config: Map<String, String>): List<String> {
        return mutableListOf<String>().apply {
            add("{")
            add("\tCONFIG = new java.util.HashMap();")
            config.forEach {
                add("\tCONFIG.put(${it.key}.class, ${it.value}.class);")
            }
            add("}")
        }
    }
}