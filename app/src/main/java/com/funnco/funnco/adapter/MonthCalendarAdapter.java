package com.funnco.funnco.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.MonthCalendar;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.date.TimeUtils;

import java.util.Hashtable;
import java.util.List;


/**
 * 按月显示日期适配器
 */
public class MonthCalendarAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<MonthCalendar> mDateInfoList = null;
    //有预约的日期集合
    private Hashtable<String, String> scheduleNewList = new Hashtable<>();
    //有事件的日期集合
    private Hashtable<String, String> scheduleNewList2 = new Hashtable<>();
    //每天循环的事件Id集合
//    private Map<String, String> repeadEveryDayEventMap = new HashMap<>();
    //添加
    private Context mCtx = null;
    private String mSelectedDay = "";
    private Resources mRes;
    int sdk = android.os.Build.VERSION.SDK_INT;
    public MonthCalendarAdapter(Activity activity,
                                List<MonthCalendar> dateInfoList, Hashtable<String, String> scheduleNewList,
                                Hashtable<String, String> scheduleNewList2) {
        mCtx = activity;
        mDateInfoList = dateInfoList;
        this.scheduleNewList.putAll(scheduleNewList);
        this.scheduleNewList2.putAll(scheduleNewList2);
        mActivity = activity;
        mRes = mCtx.getResources();
//        mGridItemSize = DeviceInfo.getScreenWidth(mActivity) / 7;//修改***
//           mGridItemSize = DeviceInfo.getScreenWidth(mActivity) / 10;
    }

    public void setDateInfoList(List<MonthCalendar> dateInfoList) {
        mDateInfoList.clear();
        mDateInfoList.addAll(dateInfoList);
    }

    public void setScheduleNewList(Hashtable<String, String> ls, Hashtable<String, String> ls2) {
        LogUtils.e("日历适配器(月)：", "原数据集合大小是：" + scheduleNewList.size() + " ,有事件的日期结合大小:" + scheduleNewList2.size());
        scheduleNewList.clear();
        scheduleNewList2.clear();
        LogUtils.e("日历适配器(月)：", "接收到的数据集合大小是：" + ls.size() + "  " + ls2.size());
        scheduleNewList.putAll(ls);
        scheduleNewList2.putAll(ls2);
        LogUtils.e("日历适配器(月)：", "现数据集合大小是：" + scheduleNewList.size() + " ,有事件的日期结合大小:" + scheduleNewList2.size());
        notifyDataSetInvalidated();
//        notifyDataSetChanged();
    }

    /**
     * 设置每天循环的事件数据
     *
     * @param
     */
//    public void setRepeadEveryDayEventMap(Map<String, String> repeadEveryDayEventMap) {
//        this.repeadEveryDayEventMap.clear();
//        this.repeadEveryDayEventMap.putAll(repeadEveryDayEventMap);
//        notifyDataSetInvalidated();
//    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        LogUtils.e("日历适配器(月)", "进行了刷新---notifyDataSetChanged");
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        LogUtils.e("日历适配器(月)", "进行了刷新---notifyDataSetInvalidated");
    }

    public List<MonthCalendar> getList() {
        return mDateInfoList;
    }

    @Override
    public int getCount() {
        return mDateInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDateInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 设置选中日期
     */
    public void setSelectedDay(String selectedDay) {
        mSelectedDay = selectedDay;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup group) {
        ViewHolder viewHolder;
        if (convertView == null) {
//            convertView = LayoutInflater.from(mCtx).inflate(
//                    R.layout.gridview_item, null);
            //修改
            convertView = LayoutInflater.from(mCtx).inflate(
                    R.layout.layout_schedule_calendar_gridview_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MonthCalendar monthCalendar = mDateInfoList.get(position);
        viewHolder.date.setText(TimeUtils.timeFormat(monthCalendar.getDate()));
        //此处处理小黑点显示的问题
//        if (null != scheduleNewList && scheduleNewList.containsKey(monthCalendar.getEveryDay())){
//            viewHolder.point.setVisibility(View.VISIBLE);
//        }else{
//            viewHolder.point.setVisibility(View.GONE);
//        }

        //此处处理小圆点显示问题
//        if (null != scheduleNewList && scheduleNewList.containsKey(monthCalendar.getEveryDay())){
//            viewHolder.dotConventation.setVisibility(View.VISIBLE);
//            viewHolder.dotEvent.setVisibility(View.VISIBLE);
//        }else{
//            viewHolder.dotConventation.setVisibility(View.GONE);
//            viewHolder.dotEvent.setVisibility(View.GONE);
//        }

//        boolean hasCus = monthCalendar.isHasCus();
//        viewHolder.dot.setVisibility(hasCus ? View.VISIBLE : View.GONE);
        // 在本月里的日期且是当前选中的日期
        if (!"".equals(monthCalendar.getEveryDay())
                && monthCalendar.getEveryDay().equals(mSelectedDay)) {
            viewHolder.date.setTextColor(Color.WHITE);
//            viewHolder.point.setImageResource(R.mipmap.icon_dot_select);//-----
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.view.setBackgroundDrawable(mRes
                        .getDrawable(R.drawable.bg_circle_selected));
            }else {
                viewHolder.view.setBackground(mRes
                        .getDrawable(R.drawable.bg_circle_selected));
            }
            viewHolder.dotConventation.setVisibility(View.GONE);//-----
            viewHolder.dotEvent.setVisibility(View.GONE);//-----
        } else {
//            convertView.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.view.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.date.setTextColor(Color.BLACK);
//            viewHolder.point.setImageResource(R.mipmap.icon_dot_unselect);//-----
            // 不在本月里面的日期置灰
            if (!monthCalendar.isThisMonth()) {
                viewHolder.date.setTextColor(mRes
                        .getColor(R.color.calendar_text_inactive));
            }
            //此处处理小圆点显示问题
            if (null != scheduleNewList && scheduleNewList.containsKey(monthCalendar.getEveryDay())) {
                viewHolder.dotConventation.setVisibility(View.VISIBLE);
            } else {
                viewHolder.dotConventation.setVisibility(View.GONE);
            }
            //事件小点的处理
//            if (repeadEveryDayEventMap != null && repeadEveryDayEventMap.size() > 0){
//                viewHolder.dotEvent.setVisibility(View.VISIBLE);
//            }else {
            if (null != scheduleNewList2 && scheduleNewList2.containsKey(monthCalendar.getEveryDay())) {
                viewHolder.dotEvent.setVisibility(View.VISIBLE);
               } else {
               viewHolder.dotEvent.setVisibility(View.GONE);
            }
//            }
            // 今日，未选中状态置为灰色
            if (monthCalendar.isToday()) {
                viewHolder.date.setTextColor(Color.WHITE);
//                viewHolder.point.setImageResource(R.mipmap.icon_dot_select);//-----
//                viewHolder.dotConventation.setVisibility(View.GONE);//-----
//                viewHolder.dotEvent.setVisibility(View.GONE);//-----


                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.view.setBackgroundDrawable(mRes.getDrawable(R.drawable.bg_circle_today));
                } else {
                    viewHolder.view.setBackground(mRes.getDrawable(R.drawable.bg_circle_today));
                }
//                convertView.setBackground(mRes
//                        .getDrawable(R.drawable.bg_circle_today));
            }
        }
        //修改
//        convertView.setLayoutParams(new GridView.LayoutParams(mGridItemSize - 10,
//                mGridItemSize - 10));
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout view;
        View dotEvent;
        View dotConventation;
        TextView date;

        //        TextView date;
//        TextView dot;
        public ViewHolder(View convertView) {
            this.view = (RelativeLayout) convertView.findViewById(R.id.item_layout);
            this.date = (TextView) convertView.findViewById(R.id.tv_calendar);
//            this.point = (ImageButton) convertView.findViewById(R.id.ib_point);
            this.dotConventation = convertView.findViewById(R.id.v_point_conventation);
            this.dotEvent = convertView.findViewById(R.id.v_point_event);
        }
    }

}
