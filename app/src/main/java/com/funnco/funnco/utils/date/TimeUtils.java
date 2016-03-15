package com.funnco.funnco.utils.date;

import android.text.format.Time;

import com.funnco.funnco.bean.MonthCalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 时间工具类
 * 
 * @author gufei
 */
public class TimeUtils {
    /**
     * yyyyMMddHHmmss
     */
    public static final String TIME_FORMAT = "yyyyMMddHHmmss";
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String TIME_FORMAT2 = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyy-MM-dd
     */
    public static final String TIME_FORMAT3 = "yyyy-MM-dd";
    /**
     * yyyyMMdd
     */
    public static final String TIME_FORMAT4 = "yyyyMMdd";
    /**
     * yyyy-MM-dd HH:mm
     */
    public static final String TIME_FORMAT5 = "yyyy-MM-dd HH:mm";
    /**
     * yyyyMM
     */
    public static final String TIME_FORMAT6 = "yyyyMM";
    /**
     * MM-dd
     */
    public static final String TIME_FORMAT7 = "MM-dd";


    public static int getCurrentYear() {
        Time t = new Time();
        t.setToNow();
        return t.year;
    }

    public static int getCurrentMonth() {
        Time t = new Time();
        t.setToNow();
        return t.month + 1;
    }

    public static int getCurrentDay() {
        Time t = new Time();
        t.setToNow();
        return t.monthDay;
    }

    public static String getCurrentTime(String format) {
        return formatTime(System.currentTimeMillis(), format);
    }

    /**
     * 得到上一个月----yyyy-MM-01
     * @param dates
     * @return
     */
    public static String getPreDate(String dates){
        String[] strs = dates.split("-");
        int month = Integer.parseInt(strs[1]);
        if (month == 1){
            strs[0] = String.valueOf(Integer.parseInt(strs[0]) - 1);
            strs[1] = "12";
        }else if (month <=10){
            strs[1] = "0"+String.valueOf(month - 1);
        }else{
            strs[1] = String.valueOf(month - 1);
        }
        return strs[0]+"-"+strs[1]+"-01";
    }

    /**
     * 得到下一个月----yyyy-MM-01
     * @param dates
     * @return
     */
    public static String getNextDate(String dates){
        String[] strs = dates.split("-");
        int month = Integer.parseInt(strs[1]);
        if (month == 12){
            strs[0] = String.valueOf(Integer.parseInt(strs[0]) + 1);
            strs[1] = "01";
        }else if (month >=9){
            strs[1] = String.valueOf(month + 1);
        }else{
            strs[1] = "0"+String.valueOf(month + 1);
        }
        return strs[0]+"-"+strs[1]+"-01";
    }
    /**
     * 得到当前日期----yyyy-MM-dd
     * @return
     */
    public static String getCurrentDate() {
        return getCurrentDate(TIME_FORMAT3);
    }
    /**
     * 得到当前日期----format
     * @return
     */
    public static String getCurrentDate(String format){
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat simple = new SimpleDateFormat(format);
        return simple.format(date);
    }

    public static String formatTime(long time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return dateFormat.format(calendar.getTime());
    }

    public static int getTimeByPosition(int position, int originYear,
            int originMonth, String type) {
        int year = originYear, month = originMonth;
        if (position > 500) {
            for (int i = 500; i < position; i++) {
                month++;
                if (month == 13) {
                    month = 1;
                    year++;
                }
            }
        } else if (position < 500) {
            for (int i = 500; i > position; i--) {
                month--;
                if (month == 0) {
                    month = 12;
                    year--;
                }
            }
        }
        if (type.equals("year")) {
            return year;
        }
        return month;
    }

    public static int getWeekDay(String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            dayOfWeek = 0;
        } else {
            dayOfWeek -= 1;
        }
        return dayOfWeek;
    }

    public static boolean isLeapYear(int year) {
        if (year % 400 == 0 || year % 100 != 0 && year % 4 == 0) {
            return true;
        }
        return false;
    }

    public static int getDaysOfMonth(int year, int month) {
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            return 31;
        case 4:
        case 6:
        case 9:
        case 11:
            return 30;
        default:
            if (isLeapYear(year)) {
                return 29;
            }
            return 28;
        }
    }

    public static String getFormatDate(int year, int month) {
        String formatYear = year + "";
        String formatMonth = "";
        if (month < 10) {
            formatMonth = "0" + month;
        } else {
            formatMonth = month + "";
        }
        return formatYear + "-" + formatMonth + "-01";
    }

    public static String getFormatDate(int year, int month, int day) {
        String formatYear = year + "";
        String formatMonth = "";
        String formatDay = "";
        if (month < 10) {
            formatMonth = "0" + month;
        } else {
            formatMonth = month + "";
        }
        if (day < 10) {
            formatDay = "0" + day;
        } else {
            formatDay = day + "";
        }
        return formatYear + "-" + formatMonth + "-" + formatDay;
    }

    public static List<MonthCalendar> initCalendar(String formatDate, int month)
            throws Exception {
        int dates = 1;
        int year = Integer.parseInt(formatDate.substring(0, 4));
        int[] allDates = new int[42];
        for (int i = 0; i < allDates.length; i++) {
            allDates[i] = -1;
        }
        int currYear = TimeUtils.getCurrentYear();
        int currMonth = TimeUtils.getCurrentMonth();
        int currDay = TimeUtils.getCurrentDay();
        int firstDayOfMonth = TimeUtils.getWeekDay(formatDate);
        int totalDays = TimeUtils.getDaysOfMonth(year, month);
        for (int i = firstDayOfMonth; i < totalDays + firstDayOfMonth; i++) {
            allDates[i] = dates;
            dates++;
        }
        List<MonthCalendar> list = new CopyOnWriteArrayList<>();//修改。。。
        MonthCalendar monthCalendar;
        for (int allDate : allDates) {
            monthCalendar = new MonthCalendar();
            monthCalendar.setDate(allDate);
            if (allDate == -1) {
                monthCalendar.setLunarDate("");
                monthCalendar.setThisMonth(false);
                monthCalendar.setWeekend(false);
                monthCalendar.setToday(false);
            } else {
                String date = TimeUtils.getFormatDate(year, month, allDate);
                monthCalendar.setEveryDay(date);
                // 闰年月，假日、周末计算暂时屏蔽，提升切换速度
                // SimpleDateFormat sdf = new SimpleDateFormat(
                // TimeUtils.TIME_FORMAT3);
                // long time = sdf.parse(date).getTime();
                // Lunar lunar = new Lunar(time);
                // if (lunar.isSFestival()) {
                // monthCalendar.setLunarDate(lunar.getSFestivalName());
                // monthCalendar.setHoliday(true);
                // } else {
                // if (lunar.isLFestival()
                // && lunar.getLunarMonthString().substring(0, 1)
                // .equals("闰") == false) {
                // monthCalendar.setLunarDate(lunar.getLFestivalName());
                // monthCalendar.setHoliday(true);
                // } else {
                // if (lunar.getLunarDayString().equals("初一")) {
                // monthCalendar.setLunarDate(lunar
                // .getLunarMonthString() + "月");
                // } else {
                // monthCalendar.setLunarDate(lunar
                // .getLunarDayString());
                // }
                // monthCalendar.setHoliday(false);
                // }
                // }
                monthCalendar.setThisMonth(true);
                // int t = getWeekDay(getFormatDate(year, month, allDate));
                // if (t == 0 || t == 6) {
                // monthCalendar.setWeekend(true);
                // } else {
                // monthCalendar.setWeekend(false);
                // }
                if (year == currYear && month == currMonth
                        && allDate == currDay) {
                    monthCalendar.setToday(true);
                }
            }
            list.add(monthCalendar);
        }

        int front = getFirstIndexOf(list);
        int back = getLastIndexOf(list);
        int lastMonthDays = getDaysOfMonth(year, month - 1);
        int nextMonthDays = 1;
        for (int i = front - 1; i >= 0; i--) {
            list.get(i).setDate(lastMonthDays);
            lastMonthDays--;
        }
        for (int i = back + 1; i < list.size(); i++) {
            list.get(i).setDate(nextMonthDays);
            nextMonthDays++;
        }
        return list;
    }

    public static String timeFormat(int time) {
        String temp = "";
        if (time < 10) {
            temp = "0" + time;
        } else {
            temp = time + "";
        }
        return temp;
    }

    /**
     * 对指定日期进行计算前后标示
     * @param targetDate
     * @param format
     * @return
     */
    public static String timePast(String targetDate,String format) {
        long day = 0;
        try {
            SimpleDateFormat dfs = new SimpleDateFormat(format);
            Date begin = dfs.parse(getCurrentDate(format));
            Date end = dfs.parse(targetDate);
            long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
            day = between / (24 * 3600);

            DateFormat df = new SimpleDateFormat(
                    format);
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            try {
                c1.setTime(df.parse(getCurrentDate(format)));
                c2.setTime(df.parse(targetDate));
            } catch (ParseException e) {
            }
            int result = c1.compareTo(c2);
            if (result == 0) {
                return "今天";
            } else if (result < 0) {
                if (day == 1) {
                    return "明天";
                } else if (day == 2) {
                    return "后天";
                } else {
                    return Math.abs(day) + "天后";
                }
            } else {
                if (day == -1) {
                    return "昨天";
                } else if (day == -2) {
                    return "前天";
                } else {
                    return Math.abs(day) + "天前";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "今天";
    }

    public static String timePast(String targetDate) {
        return timePast(targetDate,TIME_FORMAT3);
    }

    /**
     * @param date1 需要比较的时间 不能为空(null),需要正确的日期格式
     * @param date2 被比较的时间 为空(null)则为当前时间
     * @param stype 返回值类型 0为多少天，1为多少个月，2为多少年
     * @return
     */
    public static int compareDate(String date1, String date2, int stype) {
        int n = 0;
        String[] u = { "天", "月", "年" };
        String formatStyle = stype == 1 ? "yyyy-MM" : "yyyy-MM-dd";
        date2 = date2 == null ? getCurrentDate() : date2;
        DateFormat df = new SimpleDateFormat(formatStyle);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(date1));
            c2.setTime(df.parse(date2));
        } catch (Exception e3) {
        }
        // List list = new ArrayList();
        while (!c1.after(c2)) { // 循环对比，直到相等，n 就是所要的结果
            // list.add(df.format(c1.getTime())); // 这里可以把间隔的日期存到数组中 打印出来
            n++;
            if (stype == 1) {
                c1.add(Calendar.MONTH, 1); // 比较月份，月份+1
            } else {
                c1.add(Calendar.DATE, 1); // 比较天数，日期+1
            }
        }
        n = n - 1;
        if (stype == 2) {
            n = n / 365;
        }
        return n;
    }

    public static int getFirstIndexOf(List<MonthCalendar> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDate() != -1) {
                return i;
            }
        }
        return -1;
    }

    public static int getLastIndexOf(List<MonthCalendar> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getDate() != -1) {
                return i;
            }
        }
        return -1;
    }

}
