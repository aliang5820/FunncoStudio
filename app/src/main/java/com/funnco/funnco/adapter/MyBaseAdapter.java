package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by user on 2015/7/10.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> list;

    public MyBaseAdapter(Context context,List<T> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getContectView(position,convertView,parent);
    }

    public abstract View getContectView(int position, View convertView, ViewGroup parent);
}
