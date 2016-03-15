package com.funnco.funnco.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 我的预约客户--字母排序
 * Created by user on 2015/6/13.
 * @author Shawn
 */
@Table(name = "MyCustomerD")
public class MyCustomerD implements Parcelable,Comparable<MyCustomerD>{
    //关系Id
    @Column(column = "id")
    @Id
    private String id;
    @Column(column = "mobile")
    private String mobile;
    @Column(column = "numbers")
    private int numbers;
    //顾客Id
    @Column(column = "c_uid")
    private String c_uid;
    @Column(column = "title")
    private String title;
    @Column(column = "times")
    private String times;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getNumbers() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }

    public String getC_uid() {
        return c_uid;
    }

    public void setC_uid(String c_uid) {
        this.c_uid = c_uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
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

    public MyCustomerD() {
    }

    public MyCustomerD(String id, String mobile,int numbers, String c_uid,
                       String title,String times,String headpic,
                       String date, String tip, String week,
                       String date2) {
        this.id = id;
        this.mobile = mobile;
        this.numbers = numbers;
        this.c_uid = c_uid;
        this.title = title;
        this.times = times;
        this.headpic = headpic;
        this.date = date;
        this.tip = tip;
        this.week = week;
        this.date2 = date2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mobile);
        dest.writeInt(numbers);
        dest.writeString(c_uid);
        dest.writeString(title);
        dest.writeString(times);
        dest.writeString(headpic);
        dest.writeString(date);
        dest.writeString(tip);
        dest.writeString(week);
        dest.writeString(date2);
    }

    public static Creator<MyCustomerD> CREATOR = new Creator<MyCustomerD>() {
        @Override
        public MyCustomerD createFromParcel(Parcel source) {
            String id = source.readString();
            String mobile = source.readString();
            int number = source.readInt();
            String c_uid = source.readString();
            String title = source.readString();
            String times = source.readString();
            String headpic = source.readString();
            String date = source.readString();
            String tip = source.readString();
            String week = source.readString();
            String date2 = source.readString();
            return new MyCustomerD(id,mobile,number,c_uid,title,times,
                    headpic,date,tip,week,date2);
        }


        @Override
        public MyCustomerD[] newArray(int size) {
            return new MyCustomerD[size];
        }
    };

    @Override
    public int compareTo(MyCustomerD another) {
        return this.getDate2().compareTo(another.getDate2());
    }
}
