package cn.mycommons.moduleshopping

import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.moduleservice.IShoppingService

@Implements(parent = IShoppingService::class)
class ShoppingServiceImpl : IShoppingService {

}