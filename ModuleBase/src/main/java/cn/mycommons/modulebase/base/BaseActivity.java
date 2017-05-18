package cn.mycommons.modulebase.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.mycommons.modulebase.annotations.LogTrace;

/**
 * BaseActivity <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class BaseActivity extends AppCompatActivity {

    @LogTrace
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @LogTrace
    @Override
    protected void onStart() {
        super.onStart();
    }

    @LogTrace
    @Override
    protected void onResume() {
        super.onResume();
    }

    @LogTrace
    @Override
    protected void onPause() {
        super.onPause();
    }

    @LogTrace
    @Override
    protected void onStop() {
        super.onStop();
    }

    @LogTrace
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @NonNull
    public Context getContext() {
        return this;
    }
}