package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 通讯录 时间排序
 * Created by user on 2015/9/16.
 * @author Shawn
 */
@Table(name = "MyCustomerDate")
public class MyCustomerDate implements Comparable<MyCustomerDate>{
    @Column(column = "truename")
    private String truename;
    @Column(column = "c_uid")
    private String c_uid;
    @Column(column = "service_name")
    private String service_name;
    @Column(column = "mobile")
    private String mobile;
    @Column(column = "description")
    private String description;
    @Column(column = "endtime")
    private String endtime;
    @Id
    @Column(column = "id")
    private String id;
    @Column(column = "remark_name")
    private String remark_name;
    @Column(column = "starttime")
    private String starttime;
    @Column(column = "startdate")
    private String startdate;
    @Column(column = "headpic")
    private String headpic;
    @Column(column = "date")
    private String date;
    @Column(column = "tip")
    private String tip;
    @Column(column = "week")
    private String week;
    //新加入的date2 用于存储毫秒数
    @Column(column = "date2")
    private String date2;
    public void setTruename(String truename) {
        this.truename = truename;
    }

    public void setC_uid(String c_uid) {
        this.c_uid = c_uid;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRemark_name(String remark_name) {
        this.remark_name = remark_name;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getTruename() {
        return truename;
    }

    public String getC_uid() {
        return c_uid;
    }

    public String getService_name() {
        return service_name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDescription() {
        return description;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getId() {
        return id;
    }

    public String getRemark_name() {
        return remark_name;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getHeadpic() {
        return headpic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public MyCustomerDate() {
    }

    public MyCustomerDate(String truename, String c_uid,
                          String service_name, String mobile,
                          String description, String endtime,
                          String id, String remark_name,
                          String starttime, String startdate,
                          String headpic, String date,
                          String tip, String week,String date2) {
        this.truename = truename;
        this.c_uid = c_uid;
        this.service_name = service_name;
        this.mobile = mobile;
        this.description = description;
        this.endtime = endtime;
        this.id = id;
        this.remark_name = remark_name;
        this.starttime = starttime;
        this.startdate = startdate;
        this.headpic = headpic;
        this.date = date;
        this.tip = tip;
        this.week = week;
        this.date2 = date2;
    }

    @Override
    public int compareTo(MyCustomerDate another) {
        return another.getDate2().compareTo(getDate2());
    }
}
