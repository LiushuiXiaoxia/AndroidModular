package cn.mycommons.modulebase.base

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import cn.mycommons.modulebase.annotations.LogTrace

/**
 * BaseActivity <br>
 * Created by xiaqiulei on 2017-05-14.
 */
open class BaseActivity : AppCompatActivity() {

    @LogTrace
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    @LogTrace
    override fun onStart() {
        super.onStart()
    }

    @LogTrace
    override fun onResume() {
        super.onResume()
    }

    @LogTrace
    override fun onPause() {
        super.onPause()
    }

    @LogTrace
    override fun onStop() {
        super.onStop()
    }

    @LogTrace
    override fun onDestroy() {
        super.onDestroy()
    }

    fun getContext(): Context {
        return this
    }
}