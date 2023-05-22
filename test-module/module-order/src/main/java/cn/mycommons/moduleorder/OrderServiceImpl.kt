package cn.mycommons.moduleorder

import cn.mycommons.modulebase.annotations.Implements
import cn.mycommons.moduleservice.IOrderService
import cn.mycommons.moduleservice.bean.OrderInfo

@Implements(parent = IOrderService::class)
class OrderServiceImpl : IOrderService {

    override fun getAllOrders(): List<OrderInfo> {
        return emptyList()
    }
}