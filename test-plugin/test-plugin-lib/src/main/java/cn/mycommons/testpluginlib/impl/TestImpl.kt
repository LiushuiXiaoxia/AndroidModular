package cn.mycommons.testpluginlib.impl

import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.testpluginlib.ITest

/**
 * TestImpl <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */

@Implements(parent = ITest::class)
class TestImpl : ITest