package cn.mycommons.moduleservice.bean

import java.io.Serializable

/**
 * OrderInfo <br></br>
 * Created by xiaqiulei on 2017-05-14.
 */
class OrderInfo : Serializable {

    var id: Int = 0

    var name: String? = null

    override fun toString(): String {
        return "OrderInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'
    }
}