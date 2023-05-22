package cn.mycommons.moduleorder

import android.os.Bundle

import cn.mycommons.modulebase.annotations.Router
import cn.mycommons.modulebase.annotations.RouterParam
import cn.mycommons.modulebase.base.BaseActivity


@Router(uri = "app://order")
class OrderActivity : BaseActivity() {

    @RouterParam(name = "order")
    var order: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
    }
}