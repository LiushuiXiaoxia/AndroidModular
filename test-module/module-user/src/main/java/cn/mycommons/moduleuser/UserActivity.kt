package cn.mycommons.moduleuser

import android.os.Bundle

import cn.mycommons.modulebase.annotations.Router
import cn.mycommons.modulebase.annotations.RouterParam
import cn.mycommons.modulebase.base.BaseActivity


@Router(uri = "app://user")
class UserActivity : BaseActivity() {

    @RouterParam(name = "msg111")
    var msg: String? = null

    @RouterParam(name = "msg222")
    var msg2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // for test
        UserActivity__RouterInject.inject(this)
    }
}