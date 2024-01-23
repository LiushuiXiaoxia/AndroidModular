package cn.mycommons.module_plugin.core

import cn.mycommons.modulebase.annotations.Implements
import javassist.CtClass

/**
 * Entry <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */
data class ImplementRecord(
    val anImplements: Implements,
    val ctClass: CtClass,
)