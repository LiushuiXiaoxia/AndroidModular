package cn.mycommons.androidmodular.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.mycommons.androidmodular.InjectHelper
import cn.mycommons.androidmodular.R
import cn.mycommons.androidmodular.BuildConfig
import cn.mycommons.androidmodular.RouterHelper
import cn.mycommons.androidmodular.databinding.ActivityMainBinding
import cn.mycommons.modulebase.base.BaseActivity
import cn.mycommons.moduleservice.IUserService
import cn.mycommons.moduleuser.UserActivity__RouterInject

class MainActivity : BaseActivity() {

    val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.button.setOnClickListener {
            val userService = InjectHelper.getInstance(IUserService::class.java)
            if (userService != null) {
                Toast.makeText(getContext(), userService.getUserName(), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnGotoUser.setOnClickListener { RouterHelper.dispatch(getContext(), "app://user") }

        Log.i(TAG, "onCreate: ${UserActivity__RouterInject::class.java}")
        Log.i(TAG, "BuildConfig: ${BuildConfig::class.java}")
    }
}