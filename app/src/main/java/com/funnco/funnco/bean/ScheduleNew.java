package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 消息推送后新预约的对象
 * Created by user on 2015/7/13.
 */
@Table(name = "ScheduleNew")
public class ScheduleNew {
    @Id
    @Column(column = "dates")
    private String dates;
    @Column(column = "counts")
    private String counts;

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }

    public ScheduleNew() {
    }

    public ScheduleNew(String dates, String counts) {
        this.dates = dates;
        this.counts = counts;
    }

    @Override
    public String toString() {
        return "ScheduleNew{" +
                "dates='" + dates + '\'' +
                ", counts='" + counts + '\'' +
                '}';
    }
}
