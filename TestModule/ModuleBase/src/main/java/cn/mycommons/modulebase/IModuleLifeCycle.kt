package cn.mycommons.modulebase

/**
 * IModuleLifeCycle <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
interface IModuleLifeCycle {

    fun onCreate(config: IModuleConfig)

    fun onTerminate()
}