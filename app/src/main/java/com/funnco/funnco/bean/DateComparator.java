package com.funnco.funnco.bean;

import java.util.Comparator;

/**
 * 对未读的消息日期进行排序
 * Created by user on 2015/7/13.
 */
public class DateComparator implements Comparator<ScheduleNew> {
    @Override
    public int compare(ScheduleNew lhs, ScheduleNew rhs) {
        return lhs.getDates().compareTo(rhs.getDates());
    }
}
