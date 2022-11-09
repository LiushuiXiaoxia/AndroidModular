package cn.mycommons.moduleservice

import cn.mycommons.moduleservice.bean.OrderInfo

/**
 * IOrderService <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
interface IOrderService {

    fun getAllOrders(): List<OrderInfo>
}