package cn.mycommons.androidmodular.app

import android.app.Application
import cn.mycommons.modulebase.IModuleConfig
import cn.mycommons.modulebase.IModuleLifeCycle
import cn.mycommons.moduleorder.OrderModuleLifeCycle
import cn.mycommons.moduleshopping.ShoppingModuleLifeCycle
import cn.mycommons.moduleuser.UserModuleLifeCycle
import java.util.*

/**
 * ModuleLifeCycleManager <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
internal class ModuleLifeCycleManager(application: Application) {

    private val moduleConfig: ModuleConfig = ModuleConfig()
    private val moduleLifeCycleList: MutableList<IModuleLifeCycle>

    init {
        moduleLifeCycleList = ArrayList()
        moduleLifeCycleList.add(UserModuleLifeCycle(application))
        moduleLifeCycleList.add(OrderModuleLifeCycle(application))
        moduleLifeCycleList.add(ShoppingModuleLifeCycle(application))
    }

    fun onCreate() {
        for (lifeCycle in moduleLifeCycleList) {
            lifeCycle.onCreate(moduleConfig)
        }
    }

    fun onTerminate() {
        for (lifeCycle in moduleLifeCycleList) {
            lifeCycle.onTerminate()
        }
    }

    fun getModuleConfig(): IModuleConfig {
        return moduleConfig
    }
}