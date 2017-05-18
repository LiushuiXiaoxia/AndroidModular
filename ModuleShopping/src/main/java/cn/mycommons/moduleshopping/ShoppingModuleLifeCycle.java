package cn.mycommons.moduleshopping;

import android.app.Application;
import android.support.annotation.NonNull;

import cn.mycommons.modulebase.simple.SimpleModuleLifeCycle;

/**
 * ShoppingModuleLifeCycle <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class ShoppingModuleLifeCycle extends SimpleModuleLifeCycle {

    public ShoppingModuleLifeCycle(@NonNull Application application) {
        super(application);
    }
}