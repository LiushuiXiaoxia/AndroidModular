package cn.mycommons.modulebase.simple

import android.app.Application

import cn.mycommons.modulebase.IModuleConfig
import cn.mycommons.modulebase.IModuleLifeCycle
import cn.mycommons.modulebase.util.AppLog

/**
 * SimpleModuleLifeCycle <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
abstract class SimpleModuleLifeCycle(val application: Application) : IModuleLifeCycle {

    override fun onCreate(config: IModuleConfig) {
        AppLog.i("onCreate")
    }

    override fun onTerminate() {
        AppLog.i("onTerminate")
    }
}