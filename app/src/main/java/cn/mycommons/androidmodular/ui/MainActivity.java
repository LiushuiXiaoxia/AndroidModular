package cn.mycommons.androidmodular.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import cn.mycommons.androidmodular.InjectHelper;
import cn.mycommons.androidmodular.R;
import cn.mycommons.androidmodular.RouterHelper;
import cn.mycommons.androidmodular.databinding.ActivityMainBinding;
import cn.mycommons.modulebase.base.BaseActivity;
import cn.mycommons.moduleservice.IUserService;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();
    }

    private void init() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IUserService userService = InjectHelper.getInstance(IUserService.class);
                if (userService != null) {
                    Toast.makeText(getContext(), userService.getUserName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnGotoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterHelper.dispatch(getContext(), "app://user");
            }
        });
    }
}