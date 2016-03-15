package com.funnco.funnco.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.funnco.funnco.utils.log.LogUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * 2.1 新增字段 共用与课程对象(service_type 进行区分)
 * 2.1 对象克隆 实现Cloneable 接口
 * 服务实体类
 * Created by user on 2015/5/26.
 * @author Shawn
 */

@Table(name = "Serve")
public class Serve implements Parcelable,Cloneable{
    @Column(column = "id")
    @Id
    private String id;
    @Column(column = "service_name")
    private String service_name;
    @Column(column = "duration")
    private String duration;
    @Column(column = "numbers")
    private String numbers;//服务人数、课程人数（MaxNumber）
    @Column(column = "price")
    private String price;
    @Column(column = "description")
    private String description;
    //新加字段
    @Column(column = "repeat_type")
    private String repeat_type;//重复类型，0不重复、1永久重复
    @Column(column = "weeks")
    private String weeks;//星期天到星期六索引是0-6
    @Column(column = "startdate")
    private String startdate;//开始日期格式：2015-06-10
    @Column(column = "enddate")
    private String enddate;//结束日期
    @Column(column = "starttime")
    private String starttime;//开始时间
    @Column(column = "endtime")
    private String endtime;//结束时间 单位分钟：960
    @Column(column = "relations")
    private String relations;//可同时进行的服务id，多个服务用英文逗号隔开

    //2.1新增字段
    @Column(column = "service_type")
    private String service_type;//服务类型 0:服务 1:课程
    @Column(column = "remind_time")
    private String remind_time;//课程提醒时间
    @Column(column = "min_numbers")
    private String min_numbers;//最小开课人数
    @Column(column = "team_id")
    private String team_id;//团队Id
    @Column(column = "team_name")
    private String team_name;//团队名字

    //2.1自定义新增字段  目的按照周次排序 （周一放在第一个，故周日为7）
    @Transient//忽略字段
    private String week_index;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepeat_type() {
        return repeat_type;
    }

    public void setRepeat_type(String repeat_type) {
        this.repeat_type = repeat_type;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getRemind_time() {
        return remind_time;
    }

    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }

    public String getMin_numbers() {
        return min_numbers;
    }

    public void setMin_numbers(String min_numbers) {
        this.min_numbers = min_numbers;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getWeek_index() {
        return week_index;
    }

    public void setWeek_index(String week_index) {
        this.week_index = week_index;
    }

    public Serve(String id, String service_name, String duration,
                 String numbers, String price, String description,
                 String repeat_type, String weeks,
                 String startdate, String enddate, String starttime,
                 String endtime, String relations,String service_type,
                 String remind_time,String min_numbers,String team_id,
                 String team_name, String week_index) {
        this.id = id;
        this.service_name = service_name;
        this.duration = duration;
        this.numbers = numbers;
        this.price = price;
        this.description = description;
        this.repeat_type = repeat_type;
        this.weeks = weeks;
        this.startdate = startdate;
        this.enddate = enddate;
        this.starttime = starttime;
        this.endtime = endtime;
        this.relations = relations;
        this.service_type = service_type;
        this.remind_time = remind_time;
        this.min_numbers = min_numbers;
        this.team_id = team_id;
        this.team_name = team_name;
        this.week_index = week_index;
    }

    @Override
    public Object clone() {
        Serve s = null;
        if ( s== null){
            try {
                s = (Serve) super.clone();
            } catch (CloneNotSupportedException e) {
                LogUtils.e("funnco","对象复制异常");
            }
        }
        return s;
    }

    public Serve() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(service_name);
        dest.writeString(duration);
        dest.writeString(numbers);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(repeat_type);
        dest.writeString(weeks);
        dest.writeString(startdate);
        dest.writeString(enddate);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeString(relations);
        dest.writeString(service_type);
        dest.writeString(remind_time);
        dest.writeString(min_numbers);
        dest.writeString(team_id);
        dest.writeString(team_name);
        dest.writeString(week_index);
    }

    public static Parcelable.Creator<Serve> CREATOR = new Creator<Serve>() {
        @Override
        public Serve createFromParcel(Parcel source) {
            String id = source.readString();
            String service_name = source.readString();
            String duration = source.readString();
            String numbers = source.readString();
            String price = source.readString();
            String description = source.readString();
            String repeat_type = source.readString();
            String weeks = source.readString();
            String startdate = source.readString();
            String enddate = source.readString();
            String starttime = source.readString();
            String endtime = source.readString();
            String relations = source.readString();
            String service_type = source.readString();
            String remind_time = source.readString();
            String min_number = source.readString();
            String team_id = source.readString();
            String team_name = source.readString();
            String week_index = source.readString();
            return new Serve(id,service_name,duration,numbers,price,
                    description,repeat_type,weeks,startdate,enddate,
                    starttime,endtime,relations,service_type,
                    remind_time,min_number,team_id,team_name,week_index);
        }

        @Override
        public Serve[] newArray(int size) {
            return new Serve[0];
        }
    };

    @Override
    public String toString() {
        return "Serve{" +
                "id='" + id + '\'' +
                ", service_name='" + service_name + '\'' +
                ", duration='" + duration + '\'' +
                ", numbers='" + numbers + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", repeat_type='" + repeat_type + '\'' +
                ", weeks='" + weeks + '\'' +
                ", startdate='" + startdate + '\'' +
                ", enddate='" + enddate + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", relations='" + relations + '\'' +
                ", service_type='" + service_type + '\'' +
                ", remind_time='" + remind_time + '\'' +
                ", min_numbers='" + min_numbers + '\'' +
                ", team_id='" + team_id + '\'' +
                ", team_name='" + team_name + '\'' +
                ", week_index='" + week_index + '\'' +
                '}';
    }
}
