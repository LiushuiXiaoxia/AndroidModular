package cn.mycommons.androidmodular

import cn.mycommons.androidmodular.app.AppContext
import cn.mycommons.androidmodular.app.getAppContext
import cn.mycommons.modulebase.IModuleConfig

/**
 * InjectHelper <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
object InjectHelper {

    val appContext: AppContext get() = getAppContext()

    val iModuleConfig: IModuleConfig get() = appContext.getModuleConfig()

    fun <T> getInstance(tClass: Class<T>): T? {
        val config = iModuleConfig
        val implementClass = config.getServiceImplementClass(tClass)
        if (implementClass != null) {
            try {
                return implementClass.newInstance()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return null
    }

//    fun <T> getInstanceByDatabase(tClass: Class<T>): T? {
//        val database = `Implement_$$_Database`()
//        val implementClass = database.getServiceImplementClass(tClass)
//        if (implementClass != null) {
//            try {
//                return implementClass.newInstance()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//        return null
//    }
}