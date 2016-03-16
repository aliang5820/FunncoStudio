package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.switcher.SwipeLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 未来预约（ 也就是新增预约）
 * Created by user on 2015/6/10.
 */
public class SortAdapterD_future extends BaseAdapter{
    private List<MyCustomerD> list;
    private Context mContext;
    private Post post;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private SwipeLayout swip;

    public SortAdapterD_future(Context mContext, List<MyCustomerD> list, Post post, ImageLoader imageLoader, DisplayImageOptions options) {
        this.mContext = mContext;
        this.list = list;
        this.post = post;
        this.imageLoader = imageLoader;
        this.options = options;
    }

    public void closeSwip(){
        if (swip != null){
            swip.close();
        }
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
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_customerbydate_future, null);
            viewHolder.llayout = view.findViewById(R.id.llayout_d);
            viewHolder.tvTrueName = (TextView) view.findViewById(R.id.tv_item_customerfuture_truename);
            viewHolder.tvLastTime = (TextView) view.findViewById(R.id.tv_item_customerfuture_lasttime);
            viewHolder.tvOk = (TextView) view.findViewById(R.id.tv_item_customerfuture_ok);
            viewHolder.tvOk.setVisibility(View.GONE);
            viewHolder.tvDelete = (TextView) view.findViewById(R.id.delete);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.tvDate = (TextView) view.findViewById(R.id.date);
            viewHolder.tvWeek = (TextView) view.findViewById(R.id.week);
            view.findViewById(R.id.iv_item_customerfuture_call).setVisibility(View.GONE);
            viewHolder.ivIcon = (CircleImageView) view.findViewById(R.id.iv_item_customerfuture_icon);
            viewHolder.swip = (SwipeLayout) view.findViewById(R.id.swip);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
//        viewHolder.ivIcon.setImageDrawable(new BitmapDrawable());
        viewHolder.ivIcon.setTag(list.get(position).getHeadpic());
        imageLoader.displayImage(list.get(position).getHeadpic(),viewHolder.ivIcon, options);
        //打上标记
        viewHolder.tvOk.setTag(position+"");
        viewHolder.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Integer.valueOf(v.getTag()+"");
                post.post(0,Integer.parseInt((String)v.getTag()),index);
            }
        });
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
        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSwip();
                post.post(1,position);
            }
        });
        viewHolder.swip.addSwipeListener(new SimpleSwipeListener(){
            @Override
            public void onOpen(SwipeLayout layout) {
                super.onOpen(layout);
                if (swip != null && swip != layout){
                    swip.close();
                }
                swip = layout;
            }
        });
//        viewHolder.tvTrueName.setText(list.get(position).getRemark_name()+"");
//        viewHolder.tvLastTime.setText(list.get(position).getService_name()+"");
        viewHolder.tvTrueName.setText(list.get(position).getTitle() + "");
        viewHolder.tvLastTime.setText(list.get(position).getTimes()+"");
        return view;

    }



    final static class ViewHolder {
        View llayout;
        TextView tvDelete;
        TextView tvLetter;
        TextView tvDate;
        TextView tvWeek;
        TextView tvTrueName;
        TextView tvLastTime;
        TextView tvOk;
//        ImageView ivCall;
        CircleImageView ivIcon;
        SwipeLayout swip;
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
}
