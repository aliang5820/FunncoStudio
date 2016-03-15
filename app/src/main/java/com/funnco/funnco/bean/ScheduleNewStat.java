package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 标识有预约的日期
 * Created by user on 2015/6/30.
 */
@Table(name = "ScheduleNewStat")
public class ScheduleNewStat {
    @Column(column = "date")
    @Id
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ScheduleNewStat() {
    }

    public ScheduleNewStat(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ScheduleNewStat{" +
                ", date='" + date + '\'' +
                '}';
    }
}
