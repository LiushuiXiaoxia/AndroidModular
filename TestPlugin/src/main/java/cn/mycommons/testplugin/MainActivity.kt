package cn.mycommons.testplugin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import cn.mycommons.modulebase.annotations.ImplementsManager
import cn.mycommons.testpluginlib.ITest

private val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.e(TAG, "IApi = " + ImplementsManager.getImplementsClass(IApi::class.java))
        Log.e(TAG, "ITest = " + ImplementsManager.getImplementsClass(ITest::class.java))
    }
}