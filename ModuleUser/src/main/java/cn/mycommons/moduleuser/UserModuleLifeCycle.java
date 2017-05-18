package cn.mycommons.moduleuser;

import android.app.Application;
import android.support.annotation.NonNull;

import cn.mycommons.modulebase.IModuleConfig;
import cn.mycommons.modulebase.simple.SimpleModuleLifeCycle;
import cn.mycommons.moduleservice.IUserService;

/**
 * UserModuleLifeCycle <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class UserModuleLifeCycle extends SimpleModuleLifeCycle {

    public UserModuleLifeCycle(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate(@NonNull IModuleConfig config) {
        config.registerService(IUserService.class, UserServiceImpl.class);

        config.registerRouter("app://user", UserActivity.class);
    }
}