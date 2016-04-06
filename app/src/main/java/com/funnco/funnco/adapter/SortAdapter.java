package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.MyCustomer;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by user on 2015/6/10.
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<MyCustomer> list;
    private Context mContext;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
//    private VolleyUtils utils;

    public SortAdapter(Context mContext, List<MyCustomer> list, ImageLoader imageLoader, DisplayImageOptions options) {
        this.mContext = mContext;
        this.list = list;
        this.imageLoader = imageLoader;
        this.options = options;
//        utils = new VolleyUtils(mContext);
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<MyCustomer> list){
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
        final MyCustomer mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_callcustomer, null);
            viewHolder.llayout = view.findViewById(R.id.llayout_d);
            viewHolder.ivIcon = (CircleImageView) view.findViewById(R.id.iv_item_callcustomer_icon);
            viewHolder.ivCall = (ImageView) view.findViewById(R.id.iv_item_callcustomer_call);
            viewHolder.ivCall.setVisibility(View.INVISIBLE);
            viewHolder.tvTrueName = (TextView) view.findViewById(R.id.tv_item_callcustomer_truename);
            viewHolder.tvLastTime = (TextView) view.findViewById(R.id.tv_item_callcustomer_lasttime);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
//        viewHolder.tvTitle
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            viewHolder.llayout.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getLetter());
        }else{
            viewHolder.llayout.setVisibility(View.GONE);
        }
        //给每一个需要显示头像的ImagVeiw 打上标记（url）
        String url = list.get(position).getHeadpic();
        viewHolder.ivIcon.setTag(url);

        if (viewHolder.ivIcon.getTag().equals(url)){
            imageLoader.displayImage(url,viewHolder.ivIcon,options);
//            utils.imageLoader(url, viewHolder.ivIcon);
        }
            viewHolder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunncoUtils.callPhone(mContext, list.get(position).getMobile());
            }
        });

        viewHolder.tvTrueName.setText(list.get(position).getRemark_name()+"");
        viewHolder.tvLastTime.setText("最近预约："+list.get(position).getLast_time());
        return view;

    }


    final static class ViewHolder {
        View llayout;
        CircleImageView ivIcon;
        ImageView ivCall;
        TextView tvLetter;
        TextView tvTrueName;
        TextView tvLastTime;
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
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

    @Override
    public Object[] getSections() {
        return null;
    }
}
