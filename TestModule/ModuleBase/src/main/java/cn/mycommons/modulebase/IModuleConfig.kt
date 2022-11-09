package cn.mycommons.modulebase

import android.app.Activity

/**
 * IModuleConfig <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
interface IModuleConfig {

    fun registerRouter(uri: String, activityClass: Class<out Activity>)

    fun getRouterActivity(uri: String): Class<out Activity>?

    fun registerRouter(uri: String, routerProcess: IRouterProcess)

    fun getRouterProcess(uri: String): IRouterProcess?

    fun <T> registerService(serviceClass: Class<T>, implementClass: Class<out T>)

    fun <T> getServiceImplementClass(serviceClass: Class<T>): Class<out T>?
}