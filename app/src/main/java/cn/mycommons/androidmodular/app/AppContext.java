package cn.mycommons.androidmodular.app;

import android.app.Application;
import android.support.annotation.NonNull;

import cn.mycommons.modulebase.IModuleConfig;

/**
 * AppContext <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class AppContext extends Application {

    private static AppContext appContext;

    public static AppContext getAppContext() {
        return appContext;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ModuleLifeCycleManager lifeCycleManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        lifeCycleManager = new ModuleLifeCycleManager(this);
        lifeCycleManager.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        lifeCycleManager.onTerminate();
    }

    @NonNull
    public IModuleConfig getModuleConfig() {
        return lifeCycleManager.getModuleConfig();
    }
}