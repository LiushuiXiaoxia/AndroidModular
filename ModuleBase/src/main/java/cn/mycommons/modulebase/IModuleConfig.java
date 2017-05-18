package cn.mycommons.modulebase;

import android.app.Activity;

/**
 * IModuleConfig <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public interface IModuleConfig {

    void registerRouter(String uri, Class<? extends Activity> activityClass);

    Class<? extends Activity> getRouterActivity(String uri);

    void registerRouter(String uri, IRouterProcess routerProcess);

    IRouterProcess getRouterProcess(String uri);

    <T> void registerService(Class<T> serviceClass, Class<? extends T> implementClass);

    <T> Class<? extends T> getServiceImplementClass(Class<T> serviceClass);
}