package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.List;

/**
 * Created by user on 2015/8/27.
 */
@Table (name = "MyCustomerInfo")
public class MyCustomerInfo {
    /**
     * birthday :
     * truename : 海淘
     * mobile : 15000761392
     * curMonthCount : 1
     * description :
     * id : 287
     * remark_name : 海淘
     * totalCount : 5
     * headpic : uploadfile/headpic/2015/08/06/1833144744.jpg
     */
    @Column(column = "birthday")
    private String birthday;
    @Column(column = "truename")
    private String truename;
    @Column(column = "mobile")
    private String mobile;
    @Column(column = "curMonthCount")
    private String curMonthCount;
    @Column(column = "description")
    private String description;
    //关系Id
    @Id
    @Column(column = "id")
    private String id;
    //顾客唯一Id
    @Column(column = "c_uid")
    private String c_uid;
    @Column(column = "remark_name")
    private String remark_name;
    @Column(column = "totalCount")
    private String totalCount;
    @Column(column = "headpic")
    private String headpic;
    private String lastDay;//距离上次服务时间
    private List<String> tags;//标签

    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCurMonthCount(String curMonthCount) {
        this.curMonthCount = curMonthCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRemark_name(String remark_name) {
        this.remark_name = remark_name;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getTruename() {
        return truename;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCurMonthCount() {
        return curMonthCount;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getC_uid() {
        return c_uid;
    }

    public void setC_uid(String c_uid) {
        this.c_uid = c_uid;
    }

    public String getRemark_name() {
        return remark_name;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public String getHeadpic() {
        return headpic;
    }
}
