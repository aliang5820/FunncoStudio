package com.funnco.funnco.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by user on 2015/6/13.
 * @author Shawn
 */
@Table(name = "MyCustomer")
public class MyCustomer implements Parcelable,Comparable<MyCustomer>{
    //关系Id
    @Column(column = "id")
    @Id
    private String id;
    //客户Id
    @Column(column = "c_uid")
    private String c_uid;
    @Column(column = "truename")
    private String truename;
    @Column(column = "mobile")
    private String mobile;
    @Column(column = "remark_name")
    private String remark_name;
    @Column(column = "description")
    private String description;
    @Column(column = "headpic")
    private String headpic;
    @Column(column = "letter")
    private String letter;//显示数据拼音的首字母
    @Column(column = "last_time")
    private String last_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getC_uid() {
        return c_uid;
    }

    public void setC_uid(String c_uid) {
        this.c_uid = c_uid;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getRemark_name() {
        return remark_name;
    }

    public void setRemark_name(String remark_name) {
        this.remark_name = remark_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public MyCustomer() {
    }

    public MyCustomer(String id, String c_uid, String truename, String mobile, String remark_name, String description, String headpic, String letter, String last_time) {
        this.id = id;
        this.c_uid = c_uid;
        this.truename = truename;
        this.mobile = mobile;
        this.remark_name = remark_name;
        this.description = description;
        this.headpic = headpic;
        this.letter = letter;
        this.last_time = last_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(c_uid);
        dest.writeString(truename);
        dest.writeString(mobile);
        dest.writeString(remark_name);
        dest.writeString(description);
        dest.writeString(headpic);
        dest.writeString(letter);
        dest.writeString(last_time);
    }

    public static Creator<MyCustomer> CREATOR = new Creator<MyCustomer>() {
        @Override
        public MyCustomer createFromParcel(Parcel source) {
            String id = source.readString();
            String c_uid = source.readString();
            String truename = source.readString();
            String mobile = source.readString();
            String remark_name = source.readString();
            String description = source.readString();
            String headpic = source.readString();
            String letter  = source.readString();
            String last_time = source.readString();
            return new MyCustomer(id,c_uid,truename,mobile,remark_name,description,headpic,letter, last_time);
        }


        @Override
        public MyCustomer[] newArray(int size) {
            return new MyCustomer[size];
        }
    };

    @Override
    public int compareTo(MyCustomer another) {
        return getLetter().compareTo(another.getLetter());
    }
}
