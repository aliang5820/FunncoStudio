package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 有事件的日期
 * Created by user on 2015/8/31.
 */
@Table(name = "ScheduleNewStat_2")
public class ScheduleNewStat_2 {
    @Column(column = "date")
    @Id
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ScheduleNewStat_2() {
    }

    public ScheduleNewStat_2(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ScheduleNewStat{" +
                ", date='" + date + '\'' +
                '}';
    }
}
