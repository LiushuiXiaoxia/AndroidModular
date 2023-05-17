package cn.mycommons.testplugin.api.impl

import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.testplugin.api.IApi

/**
 * ApiImpl <br></br>
 * Created by xiaqiulei on 2017-05-15.
 */
@Implements(parent = IApi::class)
class ApiImpl : IApi