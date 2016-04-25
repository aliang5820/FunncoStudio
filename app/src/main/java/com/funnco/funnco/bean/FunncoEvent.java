package com.funnco.funnco.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

import java.util.ArrayList;

/**
 * Created by user on 2015/5/18.
 * @author Shawn
 */
@Table(name = "FunncoEvent")//建议制定表明 混肴不受影响
public class FunncoEvent implements Parcelable{
    @Column(column = "id")
    @Id
    private String id;
    @Column(column = "types")
    private String types;
    @Column(column = "numbers")
    private String numbers;
    @Column(column = "headpic")
    private String headpic;
    @Column(column = "title")
    private String title;
    @Column(column = "starttime")
    private String starttime;
    @Column(column = "endtime")
    private String endtime;
    @Column(column = "contents")
    private String contents;
    @Column(column = "remark")
    private String remark;
    @Column(column = "dates")
    private String dates;
    @Column(column = "repeat_type")
    private String repeat_type;
    @Column(column = "times")
    private String times;
    @Column(column = "admin_headpic")
    private String admin_headpic;
    @Transient//本列忽略 不存入数据库 同时静态字段也不会存入数据库
    private ArrayList<FunncoEventCustomer> list;
    //单独加的字段用于数据库存储
    @Column(column = "date_manager")
    private String date_manager;

    public String getAdmin_headpic() {
        return admin_headpic;
    }

    public void setAdmin_headpic(String admin_headpic) {
        this.admin_headpic = admin_headpic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getRepeat_type() {
        return repeat_type;
    }

    public void setRepeat_type(String repeat_type) {
        this.repeat_type = repeat_type;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public ArrayList<FunncoEventCustomer> getList() {
        return list;
    }

    public void setList(ArrayList<FunncoEventCustomer> list) {
        this.list = list;
    }

    public String getDate_manager() {
        return date_manager;
    }

    public void setDate_manager(String date_manager) {
        this.date_manager = date_manager;
    }

    public FunncoEvent(String id, String types, String numbers,
                       String headpic, String title, String starttime,
                       String endtime, String contents,String remark, String dates,
                       String repeat_type,String times, ArrayList<FunncoEventCustomer> list,
                       String date_manager) {
        this.id = id;
        this.types = types;
        this.numbers = numbers;
        this.headpic = headpic;
        this.title = title;
        this.starttime = starttime;
        this.endtime = endtime;
        this.contents = contents;
        this.remark = remark;
        this.dates = dates;
        this.repeat_type = repeat_type;
        this.times = times;
        this.list = list;
        this.date_manager = date_manager;
    }

    public FunncoEvent() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(types);
        dest.writeString(numbers);
        dest.writeString(headpic);
        dest.writeString(title);
        dest.writeString(starttime);
        dest.writeString(endtime);
        dest.writeString(contents);
        dest.writeString(remark);
        dest.writeString(dates);
        dest.writeString(repeat_type);
        dest.writeString(times);
        dest.writeList(list);
        dest.writeString(date_manager);
    }

    public static Creator<FunncoEvent> CREATOR = new Creator<FunncoEvent>(){

        @Override
        public FunncoEvent createFromParcel(Parcel source) {
            String id = source.readString();
            String types = source.readString();
            String numbers = source.readString();
            String headpic = source.readString();
            String title = source.readString();
            String starttime = source.readString();
            String endtime = source.readString();
            String contents = source.readString();
            String remark = source.readString();
            String dates = source.readString();
            String repeat_type = source.readString();
            String times = source.readString();
            ArrayList<FunncoEventCustomer> list = source.readArrayList(FunncoEventCustomer.class.getClassLoader());
            String date_manager = source.readString();
            return new FunncoEvent(id,types,numbers,
                    headpic,title,starttime,endtime,contents,remark,
                    dates,repeat_type,times,list,date_manager);
        }

        @Override
        public FunncoEvent[] newArray(int size) {
            return new FunncoEvent[0];
        }
    };
}
