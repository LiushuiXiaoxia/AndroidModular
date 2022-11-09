package cn.mycommons.moduleuser

import android.app.Application

import cn.mycommons.modulebase.IModuleConfig
import cn.mycommons.modulebase.simple.SimpleModuleLifeCycle
import cn.mycommons.moduleservice.IUserService

/**
 * UserModuleLifeCycle <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
class UserModuleLifeCycle(application: Application) : SimpleModuleLifeCycle(application) {

    override fun onCreate(config: IModuleConfig) {
        config.registerService(IUserService::class.java, UserServiceImpl::class.java)

        config.registerRouter("app://user", UserActivity::class.java)
    }
}