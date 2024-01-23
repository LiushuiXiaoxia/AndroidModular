package cn.mycommons.module_plugin.ksp.util

import com.google.devtools.ksp.processing.KSPLogger

object LogKit {

    lateinit var logger: KSPLogger

    fun setup(logger: KSPLogger) {
        this.logger = logger
    }

    fun info(msg: String) {
        logger.info(msg)
    }

    fun warn(msg: String) {
        logger.warn(msg)
    }

    fun error(msg: String) {
        logger.error(msg)
    }
}