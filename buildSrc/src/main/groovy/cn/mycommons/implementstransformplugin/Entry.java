package cn.mycommons.implementstransformplugin;

import cn.mycommons.modulebase.annotations.Implements;
import javassist.CtClass;

/**
 * Entry <br/>
 * Created by xiaqiulei on 2017-05-15.
 */
public class Entry {

    final Implements anImplements;
    final CtClass ctClass;

    public Entry(Implements anImplements, CtClass ctClass) {
        this.anImplements = anImplements;
        this.ctClass = ctClass;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "anImplements=" + anImplements +
                ", ctClass=" + ctClass +
                '}';
    }
}