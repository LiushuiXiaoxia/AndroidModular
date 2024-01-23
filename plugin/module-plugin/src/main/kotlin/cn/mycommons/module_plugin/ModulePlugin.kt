package cn.mycommons.module_plugin

import cn.mycommons.module_plugin.core.v2.ModuleProcessTask
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ImplementsPlugin <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */
class ModulePlugin : Plugin<Project> {

    override fun apply(project: Project) {
//        val app = project.extensions.getByType(AppExtension::class.java)
//        app.registerTransform(ModulePluginTransform(project))

        val ac = project.extensions.getByType(AndroidComponentsExtension::class.java)
        ModuleProcessTask.setup(project, ac)
    }
}