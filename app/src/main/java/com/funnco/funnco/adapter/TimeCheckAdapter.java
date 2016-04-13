package com.funnco.funnco.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.ScheduleTimeInfo;
import com.funnco.funnco.utils.log.LogUtils;

import java.util.List;

/**
 * Created by Edison on 2016/4/13.
 */
public class TimeCheckAdapter extends BaseAdapter {
    private static final String TAG = TimeCheckAdapter.class.getName();
    private List<ScheduleTimeInfo> list;

    public TimeCheckAdapter(List<ScheduleTimeInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_conventiontime, null);
            holder.title = (CheckBox) convertView.findViewById(R.id.id_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScheduleTimeInfo timePart = list.get(position);
        holder.title.setText(timePart.getFormatTime());
        if(!timePart.isEnable()) {
            LogUtils.e(TAG, "该时间段已经排满：" + timePart.getFormatTime());
            convertView.setEnabled(false);
            holder.title.setEnabled(false);
        } else {
            convertView.setEnabled(true);
            holder.title.setEnabled(true);
        }
        return convertView;
    }

    class ViewHolder {
        CheckBox title;
    }
}
