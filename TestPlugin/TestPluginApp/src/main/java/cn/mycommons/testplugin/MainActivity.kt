package cn.mycommons.testplugin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.mycommons.modulebase.annotations.ImplementsManager.getImplementsClass
import cn.mycommons.testplugin.api.IApi
import cn.mycommons.testpluginlib.ITest

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.e(TAG, "IApi = " + getImplementsClass(IApi::class.java))
        Log.e(TAG, "ITest = " + getImplementsClass(ITest::class.java))

        Toast.makeText(this, "IApi = " + getImplementsClass(IApi::class.java), Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "ITest = " + getImplementsClass(ITest::class.java), Toast.LENGTH_SHORT).show()
    }
}