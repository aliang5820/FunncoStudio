package com.funnco.funnco.bean;

import java.util.Calendar;

/**
 * 按周展示日期控件实体类
 * 
 * @author gufei
 */
public class WeekendCalendar {

    private int daysOfMonth = 0;//每月的天数
    private int dayOfWeek = 0;
    private int eachDayOfWeek = 0;

    public boolean isLeapYear(int year) {
        if (year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 100 != 0 && year % 4 == 0) {
            return true;
        }
        return false;
    }

    /**
     * 得到指定月天数
     * @param isLeapyear
     * @param month
     * @return
     */
    public int getDaysOfMonth(boolean isLeapyear, int month) {
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            daysOfMonth = 31;
            break;
        case 4:
        case 6:
        case 9:
        case 11:
            daysOfMonth = 30;
            break;
        case 2:
            if (isLeapyear) {
                daysOfMonth = 29;
            } else {
                daysOfMonth = 28;
            }

        }
        return daysOfMonth;
    }

    /**
     * 得到指定月第一天是周几
     * @param year
     * @param month
     * @return
     */
    public int getWeekdayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return dayOfWeek;
    }

    /**
     * 指定日期是周几
     * @param year
     * @param month
     * @param day
     * @return
     */
    public int getWeekDayOfLastMonth(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        eachDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return eachDayOfWeek;
    }

}
