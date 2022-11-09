package cn.mycommons.testplugin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import cn.mycommons.modulebase.annotations.ImplementsManager
import cn.mycommons.testpluginlib.ITest

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.e(TAG, "IApi = " + ImplementsManager.getImplementsClass(IApi::class.java))
        Log.e(TAG, "ITest = " + ImplementsManager.getImplementsClass(ITest::class.java))
    }
}