package cn.mycommons.androidmodular.app;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import cn.mycommons.modulebase.IModuleConfig;
import cn.mycommons.modulebase.IRouterProcess;

/**
 * ModuleConfig <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class ModuleConfig implements IModuleConfig {

    @NonNull
    private final Map<String, Class<? extends Activity>> activityRouterConfig;
    @NonNull
    private final Map<String, IRouterProcess> routerProcessConfig;
    @NonNull
    private final Map<Class<?>, Class<?>> serviceConfig;

    ModuleConfig() {
        activityRouterConfig = new HashMap<>();
        routerProcessConfig = new HashMap<>();
        serviceConfig = new HashMap<>();
    }

    @Override
    public void registerRouter(String uri, Class<? extends Activity> activityClass) {
        activityRouterConfig.put(uri, activityClass);
    }

    @Override
    public Class<? extends Activity> getRouterActivity(String uri) {
        return activityRouterConfig.get(uri);
    }

    @Override
    public void registerRouter(String uri, IRouterProcess routerProcess) {
        routerProcessConfig.put(uri, routerProcess);
    }

    @Override
    public IRouterProcess getRouterProcess(String uri) {
        return routerProcessConfig.get(uri);
    }

    @Override
    public <T> void registerService(Class<T> serviceClass, Class<? extends T> implementClass) {
        serviceConfig.put(serviceClass, implementClass);
    }

    @Override
    public <T> Class<? extends T> getServiceImplementClass(Class<T> serviceClass) {
        return (Class<? extends T>) serviceConfig.get(serviceClass);
    }
}