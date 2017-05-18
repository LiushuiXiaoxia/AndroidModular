package cn.mycommons.testplugin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.mycommons.modulebase.annotations.ImplementsManager;
import cn.mycommons.testpluginlib.ITest;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "IApi = " + ImplementsManager.getImplementsClass(IApi.class));
        Log.e(TAG, "ITest = " + ImplementsManager.getImplementsClass(ITest.class));
    }
}