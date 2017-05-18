package cn.mycommons.androidmodular;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import cn.mycommons.moduleservice.IUserService;
import cn.mycommons.moduleuser.UserServiceImpl;

/**
 * Implement$$Database <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class Implement_$$_Database {

    @NonNull
    private final Map<Class<?>, Class<?>> serviceConfig;

    public Implement_$$_Database() {

        serviceConfig = new HashMap<>();
        serviceConfig.put(IUserService.class, UserServiceImpl.class);
    }

    public <T> Class<? extends T> getServiceImplementClass(Class<T> serviceClass) {
        return (Class<? extends T>) serviceConfig.get(serviceClass);
    }
}