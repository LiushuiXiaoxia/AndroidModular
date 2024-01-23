package cn.mycommons.moduleuser

import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.moduleservice.IUserService

/**
 * UserServiceImpl <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */

@Implements(parent = IUserService::class)
class UserServiceImpl : IUserService {

    override fun getUserName(): String {
        return "UserServiceImpl.getUserName"
    }
}