package cn.mycommons.androidmodular;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import cn.mycommons.modulebase.IModuleConfig;
import cn.mycommons.modulebase.IRouterProcess;

/**
 * RouterHelper <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class RouterHelper {

    public static void dispatch(@NonNull Context context, @NonNull String uri) {
        IModuleConfig config = InjectHelper.getIModuleConfig();
        Class<? extends Activity> activityClass = config.getRouterActivity(uri);
        if (activityClass != null) {
            Intent intent = new Intent(context, activityClass);
            context.startActivity(intent);
        } else {
            IRouterProcess process = config.getRouterProcess(uri);
            if (process != null) {
                process.proeces(uri);
            } else {
                throw new RuntimeException("can not dispatch uri = " + uri);
            }
        }
    }
}