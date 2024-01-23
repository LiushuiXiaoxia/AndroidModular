package cn.mycommons.module_plugin.ksp.process

import cn.mycommons.module_plugin.ksp.util.LogKit
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid

class RouterParamKSVisitorVoid : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        LogKit.warn("visitClassDeclaration: ${classDeclaration.simpleName.asString()}")
    }

    override fun visitFile(file: KSFile, data: Unit) {
        LogKit.warn("visitFile: ${file}")
    }
}