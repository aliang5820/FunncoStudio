package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by user on 2015/6/10.
 */
public class SortAdapterD extends BaseAdapter{
    private List<MyCustomerD> list;
    private Context mContext;
    private Post post;
//    private VolleyUtils util;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public SortAdapterD(Context mContext, List<MyCustomerD> list,Post post,ImageLoader imageLoader,DisplayImageOptions options) {
        this.mContext = mContext;
        this.list = list;
        this.post = post;
        this.imageLoader = imageLoader;
        this.options = options;
//        util = new VolleyUtils(mContext);
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<MyCustomerD> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    ViewHolder viewHolder = null;
    public View getView(final int position, View view, ViewGroup arg2) {

        final MyCustomerD mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_callcustomer, null);
            viewHolder.llayout = view.findViewById(R.id.llayout_d);
            viewHolder.tvTrueName = (TextView) view.findViewById(R.id.tv_item_callcustomer_truename);
            viewHolder.tvLastTime = (TextView) view.findViewById(R.id.tv_item_callcustomer_lasttime);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.tvDate = (TextView) view.findViewById(R.id.date);
            viewHolder.tvWeek = (TextView) view.findViewById(R.id.week);
            viewHolder.ivCall = (ImageView) view.findViewById(R.id.iv_item_callcustomer_call);
            viewHolder.ivIcon = (CircleImageView) view.findViewById(R.id.iv_item_callcustomer_icon);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
//        viewHolder.ivIcon.setImageDrawable(new BitmapDrawable());
        viewHolder.ivIcon.setTag(list.get(position).getHeadpic());
        imageLoader.displayImage(list.get(position).getHeadpic(),viewHolder.ivIcon, options);

        //打上标记
        viewHolder.ivCall.setTag(position+"");
        viewHolder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.post(Integer.parseInt((String)v.getTag()));
            }
        });
//        viewHolder.tvTitle
        //根据position获取分类的首字母的Char ascii值
        String section = getSectionForPosition2(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.llayout.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getTip());
            viewHolder.tvWeek.setText(mContent.getWeek());
            viewHolder.tvDate.setText(mContent.getDate());
        }else{
            viewHolder.llayout.setVisibility(View.GONE);
        }

        viewHolder.tvTrueName.setText(list.get(position).getTitle()+"");
        viewHolder.tvLastTime.setText(list.get(position).getTimes()+"");
        return view;

    }



    final static class ViewHolder {
        View llayout;
        TextView tvLetter;
        TextView tvDate;
        TextView tvWeek;
        TextView tvTrueName;
        TextView tvLastTime;
        ImageView ivCall;
        CircleImageView ivIcon;
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    /*public int getSectionForPosition(int position) {
//        return list.get(position).getLetter().charAt(0);
        return 0;
    }*/
    public String getSectionForPosition2(int position) {
        return list.get(position).getTip();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(String section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getTip();
//            char firstChar = sortStr.toUpperCase().charAt(0);
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }
/*
    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }*/
}
