package cn.mycommons.androidmodular.ui

import android.os.Bundle
import android.widget.Toast
import cn.mycommons.androidmodular.InjectHelper
import cn.mycommons.androidmodular.R
import cn.mycommons.androidmodular.RouterHelper
import cn.mycommons.modulebase.base.BaseActivity
import cn.mycommons.moduleservice.IUserService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val userService = InjectHelper.getInstance(IUserService::class.java)
            if (userService != null) {
                Toast.makeText(getContext(), userService.getUserName(), Toast.LENGTH_SHORT).show()
            }
        }
        btnGotoUser.setOnClickListener { RouterHelper.dispatch(getContext(), "app://user") }
    }
}