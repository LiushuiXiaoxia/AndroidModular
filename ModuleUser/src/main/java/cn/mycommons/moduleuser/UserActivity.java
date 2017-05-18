package cn.mycommons.moduleuser;

import android.os.Bundle;

import cn.mycommons.modulebase.annotations.Router;
import cn.mycommons.modulebase.base.BaseActivity;


@Router(uri = "app://user")
public class UserActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }
}