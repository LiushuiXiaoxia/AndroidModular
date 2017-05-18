package cn.mycommons.implementstransformplugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * ImplementsPlugin <br/>
 * Created by xiaqiulei on 2017-05-15.
 */
public class ImplementsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        AppExtension app = project.getExtensions().getByType(AppExtension.class);
        app.registerTransform(new ImplementsTransform(project));
    }
}