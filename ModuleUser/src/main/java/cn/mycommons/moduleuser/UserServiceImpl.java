package cn.mycommons.moduleuser;

import cn.mycommons.modulebase.annotations.Implements;
import cn.mycommons.moduleservice.IUserService;

/**
 * UserServiceImpl <br/>
 * Created by xiaqiulei on 2017-05-14.
 */

@Implements(parent = IUserService.class)
public class UserServiceImpl implements IUserService {

    @Override
    public String getUserName() {
        return "UserServiceImpl.getUserName";
    }
}