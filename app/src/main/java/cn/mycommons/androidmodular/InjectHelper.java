package cn.mycommons.androidmodular;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.mycommons.androidmodular.app.AppContext;
import cn.mycommons.modulebase.IModuleConfig;

/**
 * InjectHelper <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class InjectHelper {

    @NonNull
    public static AppContext getAppContext() {
        return AppContext.getAppContext();
    }

    @NonNull
    public static IModuleConfig getIModuleConfig() {
        return getAppContext().getModuleConfig();
    }

    @Nullable
    public static <T> T getInstance(Class<T> tClass) {
        IModuleConfig config = getIModuleConfig();
        Class<? extends T> implementClass = config.getServiceImplementClass(tClass);
        if (implementClass != null) {
            try {
                return implementClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static <T> T getInstanceByDatabase(Class<T> tClass) {
        Implement_$$_Database database = new Implement_$$_Database();
        Class<? extends T> implementClass = database.getServiceImplementClass(tClass);
        if (implementClass != null) {
            try {
                return implementClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}