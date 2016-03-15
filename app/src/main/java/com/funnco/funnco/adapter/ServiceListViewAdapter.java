package com.funnco.funnco.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.view.layout.HandyLinearLayout;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.view.switcher.SwipeLayout;

import java.util.ArrayList;

/**
 * 服务列表适配器
 * Created by user on 2015/6/6.
 */
public class ServiceListViewAdapter extends BaseSwipeAdapter {

    private ArrayList<Serve> list;
    private Context context;
    private Post post;
    //用于最后关闭打开的SwipLayout
    private SwipeLayout swip;

    public ServiceListViewAdapter(Context context,ArrayList<Serve> list,Post post){
        this.context = context;
        this.list = list;
        this.post = post;
    }
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swip;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_service, parent, false);
        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                super.onOpen(layout);
                swip = swipeLayout;
            }

            @Override
            public void onClose(SwipeLayout layout) {
                super.onClose(layout);
            }
        });
        return view;
    }
    MyHolder holder = null;
    @Override
    public void fillValues(final int position, View convertView) {
        if (convertView != null && convertView.getTag() == null){
            holder = new MyHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (MyHolder) convertView.getTag();
        }
        Serve serve = list.get(position);
        if (getPositionForSection(serve.getTeam_id()) == position){
            if (serve.getTeam_id().equals("0")) {
                holder.tvTip.setText(R.string.str_service_my);
            }else {
                holder.tvTip.setText(serve.getTeam_name()+"");
            }
            holder.tvTip.setVisibility(View.VISIBLE);
        }else{
            holder.tvTip.setVisibility(View.GONE);
        }
        if (serve.getTeam_id().equals("0")) {
            holder.ivTeamIcon.setVisibility(View.GONE);
        }else {
            holder.ivTeamIcon.setVisibility(View.VISIBLE);
        }
        holder.tvServicename.setText(serve.getService_name());
        String weeks = serve.getWeeks();
        for (int i = 0; i < holder.hllayout.getChildCount(); i ++){
            CheckBox cb = (CheckBox) holder.hllayout.getChildAt(i);
            cb.setChecked(false);
            cb.setTextColor(Color.parseColor("#ff9fa7b0"));
        }
        if (!TextUtils.isNull(weeks)){
            String[] week = weeks.split(",");
            for (String d : week){
                d = d.trim();
                if (!TextUtils.isNull(d)){
                    int index = Integer.parseInt(d);
                    CheckBox cb = (CheckBox) holder.hllayout.getChildAt(index);
                    cb.setChecked(true);
                    cb.setTextColor(Color.parseColor("#ffffffff"));
                }
            }
        }
        int hour = Integer.parseInt(serve.getDuration());
        holder.tvPrice.setText(DateUtils.getTime4Minutes2(hour) + "/" + serve.getPrice() + "RMB");
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swip != null){
                    swip.close();
                }
                post.post(position);
            }
        });
    }
    public int getPositionForSection(String section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getTeam_id();
//            char firstChar = sortStr.toUpperCase().charAt(0);
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
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

    public SwipeLayout getSwip() {
        return swip;
    }

    public void setSwip(SwipeLayout swip) {
        this.swip = swip;
    }

    private class MyHolder{
        private TextView tvServicename;
        private TextView tvTip;
        private TextView tvPrice;
        private TextView tvDelete;
        private ImageView ivTeamIcon;
        private HandyLinearLayout hllayout;
//        private CheckBox cb0,cb1,cb2,cb3,cb4,cb5,cb6;
        public MyHolder(View view){
            this.tvTip = (TextView) view.findViewById(R.id.tv_item_service_tip);
            this.tvServicename = (TextView) view.findViewById(R.id.tv_item_service_servicename);
            this.tvPrice = (TextView) view.findViewById(R.id.tv_item_service_price);
            this.tvDelete = (TextView) view.findViewById(R.id.delete);
            this.ivTeamIcon = (ImageView) view.findViewById(R.id.iv_item_service_teamicon);
            this.hllayout = (HandyLinearLayout) view.findViewById(R.id.id_linearlayout);
//            this.cb0 = (CheckBox) view.findViewById(R.id.cb_item_service_sun);
//            this.cb1 = (CheckBox) view.findViewById(R.id.cb_item_service_mon);
//            this.cb2 = (CheckBox) view.findViewById(R.id.cb_item_service_tue);
//            this.cb3 = (CheckBox) view.findViewById(R.id.cb_item_service_wed);
//            this.cb4 = (CheckBox) view.findViewById(R.id.cb_item_service_thu);
//            this.cb5 = (CheckBox) view.findViewById(R.id.cb_item_service_fri);
//            this.cb6 = (CheckBox) view.findViewById(R.id.cb_item_service_sat);
        }
    }
}
