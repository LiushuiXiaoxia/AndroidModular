package cn.mycommons.moduleshopping

import android.os.Bundle
import cn.mycommons.modulebase.base.BaseActivity


// @Router(uri = "app://shopping")
class ShoppingActivity : BaseActivity() {

//    @RouterParam(name = "shopping")
//    var shopping: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)
    }
}