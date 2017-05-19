package cn.mycommons.androidmodular.app

import android.app.Application

import cn.mycommons.modulebase.IModuleConfig

/**
 * AppContext <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
var context: AppContext? = null

fun getAppContext(): AppContext {
    return context!!
}

class AppContext : Application() {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private var lifeCycleManager: ModuleLifeCycleManager? = null

    override fun onCreate() {
        super.onCreate()
        context = this

        lifeCycleManager = ModuleLifeCycleManager(this)
        lifeCycleManager!!.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()

        lifeCycleManager!!.onTerminate()
    }

    fun getModuleConfig(): IModuleConfig = lifeCycleManager!!.getModuleConfig()
}