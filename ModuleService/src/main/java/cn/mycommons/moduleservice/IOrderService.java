package cn.mycommons.moduleservice;

import java.util.List;

import cn.mycommons.moduleservice.bean.OrderInfo;

/**
 * IOrderService <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public interface IOrderService {

    List<OrderInfo> getAllOrders();
}