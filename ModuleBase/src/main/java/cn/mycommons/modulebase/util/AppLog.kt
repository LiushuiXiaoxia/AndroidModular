package cn.mycommons.modulebase.util

import android.util.Log

/**
 * AppLog <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
private val TAG = "AppLog"

object AppLog {

    fun i(msg: String, vararg args: Any) {
        Log.i(TAG, String.format(msg, *args))
    }
}