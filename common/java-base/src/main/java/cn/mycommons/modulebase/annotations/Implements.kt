package cn.mycommons.modulebase.annotations

import kotlin.reflect.KClass

/**
 * Router <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Implements(
    val parent: KClass<*>,
    val single: Boolean = true,
)