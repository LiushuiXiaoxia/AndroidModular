package cn.mycommons.testpluginlib

import cn.mycommons.modulebase.annotations.Implements

/**
 * TestImpl <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */

@Implements(parent = ITest::class)
class TestImpl : ITest