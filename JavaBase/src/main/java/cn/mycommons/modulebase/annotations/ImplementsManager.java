package cn.mycommons.modulebase.annotations;

import java.util.HashMap;
import java.util.Map;

/**
 * ImplementsManager <br/>
 * Created by xiaqiulei on 2017-05-17.
 */
public class ImplementsManager {

    private static final Map<Class, Class> CONFIG = new HashMap<>();


    public static Class getImplementsClass(Class parent) {
        return CONFIG.get(parent);
    }
}