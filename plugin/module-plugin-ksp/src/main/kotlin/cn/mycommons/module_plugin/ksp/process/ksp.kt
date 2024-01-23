package cn.mycommons.module_plugin.ksp.process

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument
import kotlin.reflect.KClass

@KspExperimental
fun <T : Annotation> KSAnnotated.filterAnnotationsByType(kClass: KClass<T>): Sequence<KSAnnotation> {
    return annotations.filter {
        it.annotationType.resolve().declaration.qualifiedName?.asString() == kClass.qualifiedName
                && it.shortName.getShortName() == kClass.simpleName
    }
}

fun KSAnnotation.getArgument(name: String): KSValueArgument? {
    return arguments.firstOrNull { it.name?.asString() == name }
}