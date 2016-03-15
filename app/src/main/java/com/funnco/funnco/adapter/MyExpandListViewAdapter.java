package com.funnco.funnco.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2015/6/13.
 */
public abstract class MyExpandListViewAdapter<T> extends BaseExpandableListAdapter {

    List<T> list ;
    MyHolder holder;
    MyHolder2 holder2;
    private Map<String,View> viewMap = new HashMap<>();
    public MyExpandListViewAdapter(List<T> list){
        this.list = list;
    }

    public void clearMap(){
        if (viewMap != null){
            viewMap.clear();
        }
    }

    public void hindView(int groupPosition){
        if (viewMap.containsKey(String.valueOf(groupPosition))){
            View view = viewMap.get(String.valueOf(groupPosition));
            if (view != null){
                view.setVisibility(View.INVISIBLE);
            }
        }
    }
    public void showView(int groupPosition){
        if (viewMap.containsKey(String.valueOf(groupPosition))){
            View view = viewMap.get(String.valueOf(groupPosition));
            if (view != null){
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = getGroutViewContent(groupPosition,isExpanded,convertView,parent,holder);
        if (!viewMap.containsKey(String.valueOf(groupPosition))) {
            View view = convertView.findViewById(R.id.tv_item_callcustomer_mobile);
            if (view != null) {
//                view.setTag(String.valueOf(groupPosition));
                viewMap.put(String.valueOf(groupPosition), view);
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return getChildViewContent(groupPosition, childPosition, isLastChild, convertView, parent,holder2);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public abstract View getGroutViewContent(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent,MyHolder holder);
    public abstract View getChildViewContent(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent,MyHolder2 holder2);

    public class MyHolder{
        public ImageView iv;
        public TextView tvTruename;
        public TextView tvLastyuyue;
        public ImageView ivCall;
        public MyHolder(View convertView){
            this.iv = (ImageView) convertView.findViewById(R.id.iv_item_callcustomer_icon);
            this.tvTruename = (TextView) convertView.findViewById(R.id.tv_item_callcustomer_truename);
            this.tvLastyuyue = (TextView) convertView.findViewById(R.id.tv_item_callcustomer_mobile);
            this.ivCall = (ImageView) convertView.findViewById(R.id.iv_item_callcustomer_call);
        }
    }

    public class MyHolder2{
        public TextView tv;
        public MyHolder2(View convertView){
            tv = (TextView) convertView.findViewById(R.id.tv_content);
        }
    }
}
