package com.funnco.funnco.utils.date;

import android.annotation.SuppressLint;
import android.content.Context;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.CustomDate;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2015/5/18.
 *
 * @author Shawn
 */
public class DateUtils {
    public static Calendar calendar = Calendar.getInstance();
    public static final String FORMATTIMESTR = "yyyy-MM-dd";
    public static final String FORMATTIMESTR_3 = "yyyy年MM月dd日";

    private static final String FORMATTIMESTR_H1 = FORMATTIMESTR + " HH:mm";
    private static final String FORMATTIMESTR_H2 = FORMATTIMESTR_3 + " HH:mm";
    public static String TAG_HOUR = "小时";
    public static String TAG_MINUTE = "分钟";

    public static String[] weekName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    /**
     * 返回指定月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfMonth(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29; // 闰年2月29天
        }

        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }

        return days;
    }


    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static CustomDate getNextSunday() {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7 - getWeekDay() + 1);
        CustomDate date = new CustomDate(c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        return date;
    }

    public static int[] getWeekSunday(int year, int month, int day, int pervious) {
        int[] time = new int[3];
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.add(Calendar.DAY_OF_MONTH, pervious);
        time[0] = c.get(Calendar.YEAR);
        time[1] = c.get(Calendar.MONTH) + 1;
        time[2] = c.get(Calendar.DAY_OF_MONTH);
        return time;

    }

    public static int getWeekDayFromDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateFromString(year, month));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(int year, int month) {
        String dateString = year + "-" + (month > 9 ? month : ("0" + month))
                + "-01";
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static boolean isToday(CustomDate date) {
        return (date.year == DateUtils.getYear() &&
                date.month == DateUtils.getMonth()
                && date.day == DateUtils.getCurrentMonthDay());
    }

    /**
     * 判断是否是历史预约
     *
     * @param dates
     * @return
     */
    public static boolean isHistory(String dates, String format) {
        if (isToday(dates, format)) {
            return false;
        }
        return getMills4String(dates, format) < getDateMills();
    }

    /**
     * 判断传入的字符串是今天----yyyy-MM-dd
     *
     * @param dates
     * @return
     */
    public static boolean isToday(String dates) {
        return getCurrentDate().equals(dates);
    }

    /**
     * 根据指定格式进行格式化日期进行比较
     *
     * @param dates
     * @param format
     * @return
     */
    public static boolean isToday(String dates, String format) {
        return getCurrentDate(format).equals(dates);
    }

    public static boolean isCurrentMonth(CustomDate date) {
        return (date.year == DateUtils.getYear() &&
                date.month == DateUtils.getMonth());
    }
    public static boolean isCurrentDay(long time){
        String cur = getCurrentDate(FORMATTIMESTR);
        String des = getDate(time, FORMATTIMESTR);
        return cur.equals(des);
    }
    /**
     * 获得当前小时
     *
     * @return
     */
    public static int getCurrentHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 当前日
     *
     * @return
     */
    public static int getCurrentDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return
     */
    public static String getDayOfWeek(CustomDate customDate) {
        return getDayInWeek(customDate.toString());
    }

    /**
     * 返回周几
     * @param date yyyy-MM-dd
     * @return
     */
    public static String getDayInWeek(String date) {
        return getDayInWeek(date, FORMATTIMESTR);
    }

    /**
     * 返回周几
     * @param date 日期字符串
     * @param format_src 日期格式
     * @return
     */
    public static String getDayInWeek(String date, String format_src){
        String week = "星期";
        SimpleDateFormat format = new SimpleDateFormat(format_src);//也可将此值当参数传进来
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getWeek(c.get(Calendar.DAY_OF_WEEK) - 1, week);
    }

    /**
     * 返回周几
     * @param index
     * @param weekTip
     * @return
     */
    public static String getWeek(int index,String weekTip){
        switch (index){
            case 1:
                weekTip += "一";
                break;
            case 2:
                weekTip += "二";
                break;
            case 3:
                weekTip += "三";
                break;
            case 4:
                weekTip += "四";
                break;
            case 5:
                weekTip += "五";
                break;
            case 6:
                weekTip += "六";
                break;
            case 7:
            case 0:
                weekTip += "日";
                break;
            default:
                weekTip += " * ";
                break;
        }
        return weekTip;
    }
    /**
     * 当前月
     *
     * @return
     */
    public static int getCurrentMonth() {
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 返回当前年
     *
     * @return
     */
    public static int getCurrentYear() {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 返回当前"yyyy-MM-dd"格式的日期
     *
     * @return
     */
    public static String getCurrentDate() {
        return new SimpleDateFormat(FORMATTIMESTR).format(new Date());
    }

    /**
     * 根据指定格式  格式化当前日期
     *
     * @param format
     * @return
     */
    public static String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    /**
     * 格式化日期---yyyyMMdd
     *
     * @param time
     * @return
     */
    public static Date getDate(String time, String format1) {
        Date date = new Date(System.currentTimeMillis());//防止为空
        SimpleDateFormat format = new SimpleDateFormat(format1);

        try {
            date = format.parse(time);
        } catch (ParseException e) {
            //特别处理关于日程日期错误问题 yyyy-MM-dd (应修改为yyyy-MM-dd HH:mm)
            time += " 00:00";
            try {
                date = format.parse(time);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期格式转换 eg：yyyy-MM-dd  转换成 yyyy年MM月dd日
     *
     * @param time        目标时间
     * @param format_src  原格式
     * @param format_dest 目标格式
     * @return 返回日期的字符串表达形式
     */
    public static String getDate(String time, String format_src, String format_dest) {
        LogUtils.e("日期格式化de工具类  time:", time + ",format_src:" + format_src + ",format_dest:" + format_dest);
        Date date = getDate(time, format_src);
        DateFormat format = new SimpleDateFormat(format_dest);
        return format.format(date);
    }

    /**
     * 返回相对于当前日期的多少个月之前的日期
     *
     * @param months
     * @return
     */
    public static Date getDateAgo(int months) {
        int year = getCurrentYear() - months / 12;
        int month = getCurrentMonth();
        if (month >= months % 12) {
            month -= months % 12;
        } else {
            year -= 1;
            month = months % 12;
        }

        Date date = new Date(year - 1900, month, 1);
        return date;
    }

    /**
     * 返回相对于当前日期的多少个月之后的日期
     *
     * @param months
     * @return
     */
    public static Date getDateAfter(int months) {
        int year = getCurrentYear() + months / 12;
        int month = getCurrentMonth();
        Date date = new Date(year - 1900, month, 1);
        return date;
    }

    /**
     * 该方法具有特殊性，只适用于本程序，因为上传的数据有24时
     * @param date 给定日期的下一个小时
     * @return
     */
    public static String getDateNextHour(String date) {
        String hour = date.substring(date.indexOf(" ") + 1, date.indexOf(" ") + 3);
        int hourI = Integer.valueOf(hour);
        if (hourI < 9) {
            return date.replace(" " + hour, " 0" + (hourI + 1));
        } else {
            return date.replace(" " + hour, " " + (hourI + 1));
        }
    }

    /**
     * 对量个指定格式的日期进行比较大小
     *
     * @param date1
     * @param date2
     * @param format
     * @return
     */
    public static int compareDate(String date1, String date2, String format) {
        return (getMills4String(date1, format) + "").compareTo(getMills4String(date2, format) + "");
    }

    /**
     * 判断两个日期字符串是否是同一天
     *
     * @param date1 必须是yyyy*MM*dd***   因为再次只需要截取前10位
     * @param date2 必须是yyyy*MM*dd***
     * @return
     */
    public static boolean isSameDate(String date1, String date2) {
        if (TextUtils.isNull(date1) || TextUtils.isNull(date2)) {
            return false;
        }

        return date1.substring(0, 10).equals(date2.substring(0, 10));
    }

    public static boolean isSameDate(String date1,String format1,String date2,String format2){
        String date_a = getDate(date1, format1, FORMATTIMESTR);
        String date_b = getDate(date2, format2, FORMATTIMESTR);
        return date_a.equals(date_b);
    }

    /**
     * 判断两个日期的相差的月数
     *
     * @param date1 必须是yyyy-MM-dd 格式的日期数据
     * @param date2
     * @return
     */
    public static int getMonthsBetweenDates(String date1, String date2) {
        int months = 0;
        Date d1 = getDate(date1, FORMATTIMESTR);
        Date d2 = getDate(date2, FORMATTIMESTR);
        months = (d2.getYear() - d1.getYear()) * 12 + (d2.getMonth() - d1.getMonth());
        return months;
    }

    /**
     * 格式化日期,计算日期的毫秒数
     * ----yyyy-MM-dd
     * ----yyyyMMdd
     *
     * @param dates
     * @return
     */
    public static long getMills4String(String dates, String format) {
        Date date = getDate(dates, format);
        return date.getTime();
    }

    /**
     * 获得当前时间的毫秒数
     *
     * @return
     */
    public static long getDateMills() {
        return System.currentTimeMillis();
    }

    /**
     * 具体日期的中文表述
     *
     * @param index
     * @return
     */
    public static String getDayStr(int index) {
        switch (index) {
            case 0:
                return "今天";
            case 1:
                return "明天";
            case 2:
                return "后天";
            case -1:
                return "昨天";
            case -2:
                return "前天";
            default:
                if (index < 0) {
                    return Math.abs(index) + "天前";
                } else {
                    return index + "天后";
                }
        }
    }

    /**
     * 根据传入的分钟时间字符串 计算出HH：mm的表达形式
     *
     * @param minutes
     * @return
     */
    public static String getTime4Minutes(int minutes) {
        int hour = minutes / 60;
        int minute = minutes % 60;
        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : "" + minute);
    }

    /**
     * 根据传入的分钟数 计算出HH小时mm分钟的表达形式
     *
     * @param minutes
     * @return
     */
    public static String getTime4Minutes2(int minutes) {
        int hour = minutes / 60;
        int minute = minutes % 60;
        return hour + TAG_HOUR + (minute == 0 ? "" : minute + TAG_MINUTE);
    }

    /**
     * 是否是开始日期
     *
     * @param dates
     * @return
     */
    public static boolean isBeginOfDay(String dates) {
        boolean flag = false;
        boolean flag2 = false;
        if (!TextUtils.isNull(dates) && dates.length() > 8) {
            String d1 = dates.substring(dates.length() - 5, dates.length());
            String d2 = dates.substring(dates.length() - 8, dates.length());
            flag = d1.equals("00:00");
            flag2 = d2.equals("00:00:00");
        }
        return flag || flag2;
    }

    /**
     * 是否是结束日期
     *
     * @param dates
     * @return
     */
    public static boolean isEndOfDay(String dates) {
        boolean flag = false;
        boolean flag2 = false;
        if (!TextUtils.isNull(dates) && dates.length() > 8) {
            String d1 = dates.substring(dates.length() - 5, dates.length()).trim();
            String d2 = dates.substring(dates.length() - 8, dates.length());
            flag = d1.equals("24:00");
            flag2 = d2.equals("24:00:00");
        }
        return flag || flag2;
    }

    /**
     * 判断起始日期是否正确
     *
     * @param startDate
     * @param endDate
     * @param format
     * @return
     */
    public static boolean isNormalDate(String startDate, String endDate, String format) {
        long start = getMills4String(startDate, format);
        long end = getMills4String(endDate, format);
        return end >= start;
    }

    public static String getDayStr(CustomDate customDate) {
        long strCurrentDate = DateUtils.getDateMills();
        long clickDateMills = customDate.getDateMills();
        long D_Value = 0;
        long D_mills = clickDateMills - strCurrentDate;
        int Cus_Year = customDate.getYear();
        int Cus_Month = customDate.getMonth();
        int Cus_Day = customDate.getDay();
        int Cur_Year = getCurrentYear();
        int Cur_Month = getCurrentMonth();
        int Cur_Day = getCurrentDay();
        D_Value = (clickDateMills - strCurrentDate) / 1000 / 60 / 60;
        LogUtils.e("两个时间差是：", "当前时间是：" + strCurrentDate + "  , 选中的时间是：" + clickDateMills);
        LogUtils.e("两个时间差是：", "小时：" + D_Value);
        /*if (Math.abs(D_Value) <= 24){
            if (Cur_Day == Cus_Day){
                D_Value = 0;
            }else{
                D_Value = D_mills > 0 ? 1:-1;
            }
        }else{
            if (D_Value %24 == 0) {
                D_Value /= 24;
            }else{
                D_Value = D_Value/24+(D_Value > 0 ? 1:-1);
            }
        }*/
        if (D_Value > 0) {
            D_Value /= 24;
        } else {
            D_Value = (D_Value + 24) / 24;
        }
        return getDayStr((int) D_Value);
    }

    /**
     * 对工作圈的时间进行格式化操作
     * @param targetDate
     * @param isAlwayShowHHMM 是否总是显示HH:MM这样的时间段
     * @return
     */
    public static String formatRimetShowTime(Context context,long targetDate, boolean isAlwayShowHHMM) {
        //不是使用SimpleDateFormat，减少cpu计算量
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        long diffSec = now - targetDate;

        calendar.setTimeInMillis(targetDate);
        int year = calendar.get(Calendar.YEAR); //date.year
        int m = calendar.get(Calendar.MONTH) + 1;
        String month = m >= 10 ? String.valueOf(m) : "0" + m; //date.month
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String day =  dayOfMonth >= 10 ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;//date.day
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = h >= 10 ? String.valueOf(h) : "0" + h; //date.hour
        int min = calendar.get(Calendar.MINUTE);
        String minute = min >= 10 ? String.valueOf(min) : "0" + min; //date.minute

        StringBuilder sb = new StringBuilder();
        boolean isFullFormat = false;

        if(currentYear > year){
            if(year >= 2000){
                year = year - 2000;
            }
            if(year < 10){
                sb.append("0" + year).append("-");
            }else{
                sb.append(year).append("-");
            }
            isFullFormat = true;
        }
        sb.append(month).append("-").append(day);

        String hhmm = hour + ":" + minute;

        if(isAlwayShowHHMM) {
            sb.append(" ").append(hhmm);
        }

        if(isFullFormat){
            return sb.toString();
        }

        //两天前
        if(diffSec >= 172800000 || (diffSec > 86400000 && currentHour < Integer.valueOf(hour))){
            return sb.toString();
        }
        //昨天
        if(diffSec >= 86400000 || currentDay != dayOfMonth){
            String result = context.getResources().getString(R.string.calendar_yesterday);
            if(isAlwayShowHHMM) {
                result = result + " " + hhmm;
            }
            return result;
        }
        //今天
        if(diffSec >= 60000){
            if(h < 12){
                return context.getResources().getString(R.string.calendar_morning)+" " + hhmm;
            }else{
                return context.getResources().getString(R.string.calendar_afternoon)+" " + hhmm;
            }
        }
        return context.getResources().getString(R.string.calendar_just_now);
    }

    /**
     * 格式化小时与分钟
     * @param targetDate
     * @return
     */
    public final static String formatHHMM(long targetDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(targetDate);

        int h = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = h >= 10 ? String.valueOf(h) : "0" + h; //date.hour
        int min = calendar.get(Calendar.MINUTE);
        String minute = min >= 10 ? String.valueOf(min) : "0" + min; //date.minute

        return hour + ":"  + minute;
    }

    /**
     * 默认的时间样式
     * @param date yyyy-MM-dd HH:mm
     * @return
     */
    public static String getDate(long date){
        return getDate(date, "yyyy-MM-dd HH:mm");
    }

    /**
     * 根据指定样式返回时间
     * @param date 传入的时间毫秒数 long
     * @param format 徐要返回的时间样式
     * @return
     */
    public static String getDate(long date, String format){
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        return format1.format(date);
    }
}
