package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 顾客预约记录
 * Created by user on 2015/8/27.
 */
@Table(name = "MyCustomerConventation")
public class MyCustomerConventation {

    /**
     * duration : 1.5
     * service_name : 创业谘询
     * price : 1000
     * remark :
     * dates : 2015-08-27
     * id : 2090
     * starttime : 06:00
     */
    @Column(column = "duration")
    private double duration;
    @Column(column = "service_name")
    private String service_name;
    @Column(column = "price")
    private String price;
    @Column(column = "remark")
    private String remark;
    @Column(column = "dates")
    private String dates;
    //预约记录Id 所有预约不冲突
    @Id
    @Column(column = "id")
    private String id;
    @Column(column = "starttime")
    private String starttime;

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public double getDuration() {
        return duration;
    }

    public String getService_name() {
        return service_name;
    }

    public String getPrice() {
        return price;
    }

    public String getRemark() {
        return remark;
    }

    public String getDates() {
        return dates;
    }

    public String getId() {
        return id;
    }

    public String getStarttime() {
        return starttime;
    }
}
