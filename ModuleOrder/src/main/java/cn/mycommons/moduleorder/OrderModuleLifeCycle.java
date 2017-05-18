package cn.mycommons.moduleorder;

import android.app.Application;
import android.support.annotation.NonNull;

import cn.mycommons.modulebase.simple.SimpleModuleLifeCycle;

/**
 * OrderModuleLifeCycle <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class OrderModuleLifeCycle extends SimpleModuleLifeCycle {

    public OrderModuleLifeCycle(@NonNull Application application) {
        super(application);
    }
}