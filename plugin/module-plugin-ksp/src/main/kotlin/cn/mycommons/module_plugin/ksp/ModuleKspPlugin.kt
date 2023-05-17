package cn.mycommons.module_plugin.ksp

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ImplementsPlugin <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */
class ModuleKspPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val app = project.extensions.getByType(AppExtension::class.java)
        app.registerTransform(ModuleKspPluginTransform(project))
    }
}