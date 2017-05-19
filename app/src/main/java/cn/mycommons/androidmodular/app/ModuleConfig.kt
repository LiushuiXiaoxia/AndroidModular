package cn.mycommons.androidmodular.app

import android.app.Activity
import cn.mycommons.modulebase.IModuleConfig
import cn.mycommons.modulebase.IRouterProcess
import java.util.*

/**
 * ModuleConfig <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
class ModuleConfig internal constructor() : IModuleConfig {

    private val activityRouterConfig: MutableMap<String, Class<out Activity>>
    private val routerProcessConfig: MutableMap<String, IRouterProcess>
    private val serviceConfig: MutableMap<Class<*>, Class<*>>

    init {
        activityRouterConfig = HashMap<String, Class<out Activity>>()
        routerProcessConfig = HashMap<String, IRouterProcess>()
        serviceConfig = HashMap<Class<*>, Class<*>>()
    }

    override fun registerRouter(uri: String, activityClass: Class<out Activity>) {
        activityRouterConfig[uri] = activityClass
    }

    override fun getRouterActivity(uri: String): Class<out Activity>? {
        return activityRouterConfig[uri]
    }

    override fun registerRouter(uri: String, routerProcess: IRouterProcess) {
        routerProcessConfig.put(uri, routerProcess)
    }

    override fun getRouterProcess(uri: String): IRouterProcess? {
        return routerProcessConfig[uri]
    }

    override fun <T> registerService(serviceClass: Class<T>, implementClass: Class<out T>) {
        serviceConfig.put(serviceClass, implementClass)
    }

    override fun <T> getServiceImplementClass(serviceClass: Class<T>): Class<out T> {
        return serviceConfig[serviceClass] as Class<out T>
    }
}