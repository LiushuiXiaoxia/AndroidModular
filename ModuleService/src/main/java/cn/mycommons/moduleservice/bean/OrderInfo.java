package cn.mycommons.moduleservice.bean;

import java.io.Serializable;

/**
 * OrderInfo <br/>
 * Created by xiaqiulei on 2017-05-14.
 */
public class OrderInfo implements Serializable {

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}