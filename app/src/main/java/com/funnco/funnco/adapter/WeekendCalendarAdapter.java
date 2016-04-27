package com.funnco.funnco.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.WeekendCalendar;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.date.TimeUtils;

import java.util.Hashtable;

/**
 * 按周展示日期控件适配器
 *
 * @author gufei Shawn
 */
public class WeekendCalendarAdapter extends BaseAdapter {

    private Activity mActivity;
    private boolean isLeapyear = false; // 是否为闰年
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int nextDayOfWeek = 0;
    private int lastDayOfWeek = 0;
    private int lastDaysOfMonth = 0; // 上一个月的总天数
    private WeekendCalendar mWeekCalendar = null;
    private Integer[] dayNumber = new Integer[7];
    private String[] everyDayStrs = new String[7];
    // 系统当前时间
    private int sysYear;
    private int sysMonth;
    private int sysDay;
    private String currentYear = "";
    private String currentMonth = "";
    private String currentWeek = "";
    private String todayDate = "";
    private int weeksOfMonth;
    private String mSelectedDay = "";
    private boolean isStart;

    int sdk = android.os.Build.VERSION.SDK_INT;
    //有预约de日期
    private Hashtable<String, String> scheduleNewList = new Hashtable<>();
    //有事件de日期
    private Hashtable<String, String> scheduleNewList2 = new Hashtable<>();
    //每天循环的事件Id集合
//    private Map<String,String> repeadEveryDayEventMap = new HashMap<>();

    // 标识选择的Item
    public void setSelectedDay(String selectedDay) {
        mSelectedDay = "".equals(selectedDay) ? todayDate : selectedDay;
    }

    public void setScheduleNewList(Hashtable<String, String> ls, Hashtable<String, String> ls2) {
        LogUtils.e("日历适配器(周)：", "原数据集合大小是：" + scheduleNewList.size() + " ,有事件的日期结合大小:" + scheduleNewList2.size());
        scheduleNewList.clear();
        scheduleNewList2.clear();
        LogUtils.e("日历适配器(周)：", "接收到的数据集合大小是：" + ls.size() + "  " + ls2.size());
        scheduleNewList.putAll(ls);
        scheduleNewList2.putAll(ls2);
        LogUtils.e("日历适配器(周)：", "现数据集合大小是：" + scheduleNewList.size() + " ,有事件的日期结合大小:" + scheduleNewList2.size());
        notifyDataSetInvalidated();
    }

    /**
     * 设置每天循环的事件数据
     *
     * @param
     */
//    public void setRepeadEveryDayEventMap(Map<String,String> repeadEveryDayEventMap){
//        this.repeadEveryDayEventMap.clear();
//        this.repeadEveryDayEventMap.putAll(repeadEveryDayEventMap);
//        notifyDataSetInvalidated();
//    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        LogUtils.e("日历适配器(周)", "进行了刷新---notifyDataSetChanged");
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        LogUtils.e("日历适配器(周)", "进行了刷新---notifyDataSetInvalidated");
    }

    /**
     * 预处理 得到当前年月日
     * todate : yyyy-MM-dd
     * 选中的日期
     */
    public WeekendCalendarAdapter() {
        sysYear = TimeUtils.getCurrentYear();
        sysMonth = TimeUtils.getCurrentMonth();
        sysDay = TimeUtils.getCurrentDay();
        todayDate = TimeUtils.getFormatDate(sysYear, sysMonth, sysDay);
        mSelectedDay = "".equals(mSelectedDay) ? todayDate : mSelectedDay;
    }

    public WeekendCalendarAdapter(Activity activity, int year, int month,
                                  int week, boolean isStart, Hashtable<String, String> scheduleNewList,
                                  Hashtable<String, String> scheduleNewList2) {
        this();//同样初始化空构造的数据
        mActivity = activity;
        this.isStart = isStart;
        this.scheduleNewList.putAll(scheduleNewList);
        this.scheduleNewList2.putAll(scheduleNewList2);
//        this.repeadEveryDayEventMap.putAll(repeadEveryDayEventMap);

        mWeekCalendar = new WeekendCalendar();
        lastDayOfWeek = mWeekCalendar.getWeekDayOfLastMonth(year, month,
                mWeekCalendar.getDaysOfMonth(mWeekCalendar.isLeapYear(year),
                        month));
        currentYear = String.valueOf(year);
        currentMonth = String.valueOf(TimeUtils.timeFormat(month));
        //计算当年是否为闰年，上个月/下个月的天数
        getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
        currentWeek = String.valueOf(week);
        getWeek(Integer.parseInt(currentYear), Integer.parseInt(currentMonth),
                Integer.parseInt(currentWeek));
    }

    public int getCurrentMonth(int position) {
        int thisDayOfWeek = mWeekCalendar.getWeekdayOfMonth(
                Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
        if (isStart) {
            if (thisDayOfWeek != 7) {
                if (position < thisDayOfWeek) {
                    return Integer.parseInt(currentMonth) - 1 == 0 ? 12
                            : Integer.parseInt(currentMonth) - 1;
                } else {
                    return Integer.parseInt(currentMonth);
                }
            } else {
                return Integer.parseInt(currentMonth);
            }
        } else {
            return Integer.parseInt(currentMonth);
        }

    }

    public int getCurrentYear(int position) {
        int thisDayOfWeek = mWeekCalendar.getWeekdayOfMonth(
                Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
        if (isStart) {
            if (thisDayOfWeek != 7) {
                if (position < thisDayOfWeek) {
                    return Integer.parseInt(currentMonth) - 1 == 0 ? Integer
                            .parseInt(currentYear) - 1 : Integer
                            .parseInt(currentYear);
                } else {
                    return Integer.parseInt(currentYear);
                }
            } else {
                return Integer.parseInt(currentYear);
            }
        } else {
            return Integer.parseInt(currentYear);
        }
    }

    /**
     * 计算当年是否为闰年，上个月/下个月的天数
     *
     * @param year
     * @param month
     */
    public void getCalendar(int year, int month) {
        isLeapyear = mWeekCalendar.isLeapYear(year); // 是否为闰年
        daysOfMonth = mWeekCalendar.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = mWeekCalendar.getWeekdayOfMonth(year, month); // 某月第一天为星期几
        lastDaysOfMonth = mWeekCalendar.getDaysOfMonth(isLeapyear, month - 1);//得到上个月的天数
        nextDayOfWeek = mWeekCalendar.getDaysOfMonth(isLeapyear, month + 1);//得到下个月的天数
    }

    public void getWeek(int year, int month, int week) {
        for (int i = 0; i < dayNumber.length; i++) {
            if (dayOfWeek == 7) {
                dayNumber[i] = (i + 1) + 7 * (week - 1);
                everyDayStrs[i] = year + "-" + TimeUtils.timeFormat(month)
                        + "-" + TimeUtils.timeFormat(dayNumber[i]);
            } else {
                if (week == 1) {
                    if (i < dayOfWeek) {
                        dayNumber[i] = lastDaysOfMonth - (dayOfWeek - (i + 1));
                        everyDayStrs[i] = year + "-"
                                + TimeUtils.timeFormat(month - 1) + "-"
                                + TimeUtils.timeFormat(dayNumber[i]);
                    } else {
                        dayNumber[i] = i - dayOfWeek + 1;
                        everyDayStrs[i] = year + "-"
                                + TimeUtils.timeFormat(month) + "-"
                                + TimeUtils.timeFormat(dayNumber[i]);
                    }
                } else {
                    dayNumber[i] = (7 - dayOfWeek + 1 + i) + 7 * (week - 2);
                    everyDayStrs[i] = year + "-" + TimeUtils.timeFormat(month)
                            + "-" + TimeUtils.timeFormat(dayNumber[i]);
                }
            }

        }
    }

    /**
     * 得到某月有几周(特殊算法)
     */
    public int getWeeksOfMonth() {
        int preMonthRelax = 0;
        if (dayOfWeek != 7) {
            preMonthRelax = dayOfWeek;
        }
        if ((daysOfMonth + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;
    }

    /**
     * 某一天在第几周
     */
    public void getDayInWeek(int year, int month) {

    }

    @Override
    public int getCount() {
        return dayNumber.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(
                    R.layout.layout_schedule_calendar_gridview_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.date.setText(TimeUtils.timeFormat(dayNumber[position]));
        String everyDay = everyDayStrs[position];
//        //此处处理小圆点显示问题
//        if (null != scheduleNewList && scheduleNewList.containsKey(everyDay)){
//            viewHolder.dotConventation.setVisibility(View.VISIBLE);
//            viewHolder.dotEvent.setVisibility(View.VISIBLE);
//        }else{
//            viewHolder.dotConventation.setVisibility(View.GONE);
//            viewHolder.dotEvent.setVisibility(View.GONE);
//        }
        viewHolder.date.setTag(everyDay);
        if (everyDay.equals(mSelectedDay)) {//日期被选中
            viewHolder.date.setSelected(true);
            viewHolder.date.setTextColor(Color.WHITE);
//            viewHolder.point.setImageResource(R.mipmap.icon_dot_select);//-----选中是隐藏
            viewHolder.dotEvent.setVisibility(View.GONE);
            viewHolder.dotConventation.setVisibility(View.GONE);
            viewHolder.view.setBackgroundResource(R.drawable.bg_circle_selected);
//            convertView.setBackgroundResource(R.drawable.bg_circle_selected);
        } else {//日期未被选中
            viewHolder.date.setSelected(false);
            viewHolder.date.setTextColor(Color.BLACK);
//            viewHolder.point.setImageResource(R.mipmap.icon_dot_unselect);//-----未选中显示

            //此处处理小圆点显示问题
            if (null != scheduleNewList && scheduleNewList.containsKey(everyDay)) {
                viewHolder.dotConventation.setVisibility(View.VISIBLE);
//                viewHolder.dotEvent.setVisibility(View.VISIBLE);
            } else {
                viewHolder.dotConventation.setVisibility(View.GONE);
//                viewHolder.dotEvent.setVisibility(View.GONE);
            }
//            if (repeadEveryDayEventMap != null && repeadEveryDayEventMap.size() > 0){
//                viewHolder.dotEvent.setVisibility(View.VISIBLE);
//            }else {
            if (null != scheduleNewList2 && scheduleNewList2.containsKey(everyDay)) {
                viewHolder.dotEvent.setVisibility(View.VISIBLE);
            } else {
                viewHolder.dotEvent.setVisibility(View.GONE);
            }
//            }
            viewHolder.view.setBackgroundColor(Color.TRANSPARENT);
//            convertView.setBackgroundColor(Color.TRANSPARENT);
            // 今日，未选中状态置为蓝色
            if (everyDay.equals(todayDate)) {
                viewHolder.date.setTextColor(Color.WHITE);
//                viewHolder.point.setImageResource(R.mipmap.icon_dot_select);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.view.setBackgroundDrawable(mActivity.getResources().getDrawable(
                            R.drawable.bg_circle_today));
                }else{
                    viewHolder.view.setBackground(mActivity.getResources().getDrawable(
                            R.drawable.bg_circle_today));
                }
//                convertView.setBackground(mActivity.getResources().getDrawable(
//                        R.drawable.bg_circle_today));
            }
        }
//        convertView.setLayoutParams(new GridView.LayoutParams(DeviceInfo
//                .getScreenWidth(mActivity) / 10, DeviceInfo
//                .getScreenWidth(mActivity) / 10));
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout view;
        View dotEvent;
        View dotConventation;
        TextView date;

        //        TextView dot;
        public ViewHolder(View convertView) {
            this.view = (RelativeLayout) convertView.findViewById(R.id.item_layout);
            this.date = (TextView) convertView.findViewById(R.id.tv_calendar);
            this.dotConventation = convertView.findViewById(R.id.v_point_conventation);
            this.dotEvent = convertView.findViewById(R.id.v_point_event);
//            this.point = (ImageButton) convertView.findViewById(R.id.ib_point);
        }
    }
}
