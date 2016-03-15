package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.Team;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 团队成员选择
 * Created by user on 2015/9/22.
 */
public class TeamlistExpAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Team> list;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public TeamlistExpAdapter(Context context, List<Team> list,ImageLoader imageLoader,DisplayImageOptions options){
        this.context = context;
        this.list = list;
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (list.get(groupPosition).getList() != null){
            return list.get(groupPosition).getList().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getList().get(childPosition);
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
        return false;
    }
    private MyHolder holder = null;
    private MyHolder holder_c = null;
    @Override
    public View getGroupView(int grupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_memberchoose_pw, null);
            holder = new MyHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (MyHolder) convertView.getTag();
        }
//        convertView.setClickable(true);

        holder.tvNickname.setText(list.get(grupPosition).getTeam_name());
        holder.civIcon.setImageResource(R.mipmap.common_schedule_conventiontype_icon);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_memberchoose_pw, null);
            holder_c = new MyHolder(convertView);
            convertView.setTag(holder_c);
        }else{
            holder_c = (MyHolder) convertView.getTag();
        }
        holder_c.tvNickname.setText(list.get(groupPosition).getList().get(childPosition).getNickname());
        imageLoader.displayImage(list.get(groupPosition).getList().get(childPosition).getHeadpic(),holder_c.civIcon,options);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    private class MyHolder{
        private CircleImageView civIcon;
        private TextView tvNickname;
        public MyHolder(View convertView){
            this.civIcon = (CircleImageView) convertView.findViewById(R.id.civ_item_memberchoose_icon);
            this.tvNickname = (TextView) convertView.findViewById(R.id.tv_item_memberchoose_name);
        }
    }
}
