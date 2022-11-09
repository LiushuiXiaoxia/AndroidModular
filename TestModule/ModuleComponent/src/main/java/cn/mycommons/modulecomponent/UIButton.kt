package cn.mycommons.modulecomponent

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet

/**
 * UIButton <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
class UIButton : AppCompatButton {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}