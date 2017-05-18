package cn.mycommons.androidmodular.app;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.mycommons.modulebase.IModuleConfig;
import cn.mycommons.modulebase.IModuleLifeCycle;
import cn.mycommons.moduleorder.OrderModuleLifeCycle;
import cn.mycommons.moduleshopping.ShoppingModuleLifeCycle;
import cn.mycommons.moduleuser.UserModuleLifeCycle;

/**
 * ModuleLifeCycleManager <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
class ModuleLifeCycleManager {

    @NonNull
    private ModuleConfig moduleConfig;
    @NonNull
    private final List<IModuleLifeCycle> moduleLifeCycleList;

    ModuleLifeCycleManager(@NonNull Application application) {
        moduleConfig = new ModuleConfig();
        moduleLifeCycleList = new ArrayList<>();
        moduleLifeCycleList.add(new UserModuleLifeCycle(application));
        moduleLifeCycleList.add(new OrderModuleLifeCycle(application));
        moduleLifeCycleList.add(new ShoppingModuleLifeCycle(application));
    }

    void onCreate() {
        for (IModuleLifeCycle lifeCycle : moduleLifeCycleList) {
            lifeCycle.onCreate(moduleConfig);
        }
    }

    void onTerminate() {
        for (IModuleLifeCycle lifeCycle : moduleLifeCycleList) {
            lifeCycle.onTerminate();
        }
    }

    @NonNull
    IModuleConfig getModuleConfig() {
        return moduleConfig;
    }
}