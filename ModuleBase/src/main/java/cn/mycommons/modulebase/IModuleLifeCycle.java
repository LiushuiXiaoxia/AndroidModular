package cn.mycommons.modulebase;

/**
 * IModuleLifeCycle <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public interface IModuleLifeCycle {

    void onCreate(IModuleConfig config);

    void onTerminate();
}