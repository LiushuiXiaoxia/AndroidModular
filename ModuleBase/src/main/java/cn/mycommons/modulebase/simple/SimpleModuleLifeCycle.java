package cn.mycommons.modulebase.simple;

import android.app.Application;
import android.support.annotation.NonNull;

import cn.mycommons.modulebase.IModuleConfig;
import cn.mycommons.modulebase.IModuleLifeCycle;
import cn.mycommons.modulebase.util.AppLog;

/**
 * SimpleModuleLifeCycle <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public abstract class SimpleModuleLifeCycle implements IModuleLifeCycle {

    @NonNull
    private final Application application;

    public SimpleModuleLifeCycle(@NonNull Application application) {
        this.application = application;
    }


    @Override
    public void onCreate(@NonNull IModuleConfig config) {
        AppLog.i("onCreate");
    }

    @Override
    public void onTerminate() {
        AppLog.i("onTerminate");
    }

    @NonNull
    public Application getApplication() {
        return application;
    }
}