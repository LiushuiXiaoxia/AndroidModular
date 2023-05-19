package cn.mycommons.moduleuser;

import cn.mycommons.modulebase.annotations.Router;
import cn.mycommons.modulebase.annotations.RouterParam;
import cn.mycommons.modulebase.base.BaseActivity;

@Router(uri = "app://java-user")
public class JavaUserActivity extends BaseActivity {

    @RouterParam(name = "message111")
    String message;

    @RouterParam(name = "message222")
    String message2;
}
