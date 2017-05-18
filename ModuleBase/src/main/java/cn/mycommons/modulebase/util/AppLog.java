package cn.mycommons.modulebase.util;

import android.util.Log;

/**
 * AppLog <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class AppLog {

    private static final String TAG = "AppLog";

    public static void i(String msg, Object... args) {
        Log.i(TAG, String.format(msg, args));
    }
}