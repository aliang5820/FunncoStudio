package com.funnco.funnco.bean;


import com.funnco.funnco.utils.date.TimeUtils;

/**
 * 按月展示日期控件实体类
 * 
 * @author gufei
 */
public class MonthCalendar {

    private int date = 0; // 记录哪一日
    private String everyDay = "";// 每一天的年月日表现格式
    private boolean isToday;// 是否是今日
    private boolean isThisMonth;
    private boolean isWeekend;
    private boolean isHoliday;
    //新添加
    private boolean isHasCus;//是否有预约
    private String lunarDate = "";

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getEveryDay() {
        return everyDay;
    }

    public void setEveryDay(String everyDay) {
        this.everyDay = everyDay;
    }

    public String getCurrDate() {
        // 今日日期
        return TimeUtils.getCurrentDate();
    }

    public boolean isThisMonth() {
        return isThisMonth;
    }

    public void setThisMonth(boolean isThisMonth) {
        this.isThisMonth = isThisMonth;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean isToday) {
        this.isToday = isToday;
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setWeekend(boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setHoliday(boolean isHoliday) {
        this.isHoliday = isHoliday;
    }

    public String getLunarDate() {
        return lunarDate;
    }

    public void setLunarDate(String lunarDate) {
        this.lunarDate = lunarDate;
    }

    public boolean isHasCus() {
        return isHasCus;
    }

    public void setIsHasCus(boolean isHasCus) {
        this.isHasCus = isHasCus;
    }
}
