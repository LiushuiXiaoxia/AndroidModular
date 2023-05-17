package cn.mycommons.module_plugin.ksp

import cn.mycommons.modulebase.annotations.Implements
import javassist.CtClass

/**
 * Entry <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */
class Entry(
    val anImplements: Implements,
    val ctClass: CtClass,
) {

    override fun toString(): String {
        return "Entry{" +
                "anImplements=" + anImplements +
                ", ctClass=" + ctClass +
                '}'
    }
}