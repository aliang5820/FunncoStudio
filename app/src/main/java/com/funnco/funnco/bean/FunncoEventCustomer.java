package com.funnco.funnco.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 用于ScheduleFragment 中ExpandListView 显示的预约客户
 * Created by user on 2015/5/25.
 */
@Table(name = "FunncoEventCustomer")
public class FunncoEventCustomer implements Parcelable{
    @Column(column = "id")
    @Id
    private String id;
    @Column(column = "headpic")
    private String headpic;
    @Column(column = "truename")
    private String truename;
    @Column(column = "mobile")
    private String mobile;
    @Column(column = "remark")
    private String remark;
    //该id 为所属FunncoEvent的id
    @Column(column = "id2")
    private String id2;

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public FunncoEventCustomer(String id, String headpic,
                               String truename, String mobile,
                               String remark,String id2) {
        this.id = id;
        this.headpic = headpic;
        this.truename = truename;
        this.mobile = mobile;
        this.remark = remark;
        this.id2 = id2;
    }

    public FunncoEventCustomer() {
    }

    @Override
    public String toString() {
        return "FunncoEventCustomer{" +
                "id='" + id + '\'' +
                ", headpic='" + headpic + '\'' +
                ", truename='" + truename + '\'' +
                ", mobile='" + mobile + '\'' +
                ", remark='" + remark + '\'' +
                ", id2='" + id2 + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(headpic);
        dest.writeString(truename);
        dest.writeString(mobile);
        dest.writeString(remark);
        dest.writeString(id2);
    }

    public static Creator<FunncoEventCustomer> CREATOR = new Creator<FunncoEventCustomer>(){

        @Override
        public FunncoEventCustomer createFromParcel(Parcel source) {
            String id  = source.readString();
            String headpic = source.readString();
            String truename = source.readString();
            String mobile = source.readString();
            String remark = source.readString();
            String id2 = source.readString();
            return new FunncoEventCustomer(id,headpic,truename,mobile,remark,id2);
        }

        @Override
        public FunncoEventCustomer[] newArray(int size) {
            return new FunncoEventCustomer[0];
        }
    };
}
