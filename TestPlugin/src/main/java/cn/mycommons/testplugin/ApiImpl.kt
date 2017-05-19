package cn.mycommons.testplugin

import cn.mycommons.modulebase.annotations.Implements

/**
 * ApiImpl <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */
@Implements(parent = IApi::class)
class ApiImpl : IApi