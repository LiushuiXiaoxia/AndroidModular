package cn.mycommons.moduleuser

import android.os.Bundle

import cn.mycommons.modulebase.annotations.Router
import cn.mycommons.modulebase.base.BaseActivity


@Router(uri = "app://user")
class UserActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }
}