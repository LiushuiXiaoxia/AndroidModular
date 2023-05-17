package cn.mycommons.androidmodular

import android.content.Context
import android.content.Intent

/**
 * RouterHelper <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
object RouterHelper {

    fun dispatch(context: Context, uri: String) {
        val config = InjectHelper.iModuleConfig
        val activityClass = config.getRouterActivity(uri)
        if (activityClass != null) {
            val intent = Intent(context, activityClass)
            context.startActivity(intent)
        } else {
            val process = config.getRouterProcess(uri)
            if (process != null) {
                process.proeces(uri)
            } else {
                throw RuntimeException("can not dispatch uri = $uri")
            }
        }
    }
}