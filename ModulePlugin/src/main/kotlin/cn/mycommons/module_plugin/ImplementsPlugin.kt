package cn.mycommons.module_plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * ImplementsPlugin <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */
class ImplementsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val app = project.extensions.getByType(AppExtension::class.java)
        app.registerTransform(ImplementsTransform(project))
    }
}