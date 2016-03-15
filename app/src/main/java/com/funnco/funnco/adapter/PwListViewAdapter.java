package com.funnco.funnco.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.FunncoEventCustomer;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.bimp.BitmapUtils;
import com.funnco.funnco.utils.http.VolleyUtils;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.view.switcher.SwipeLayout;

import java.util.ArrayList;

public class PwListViewAdapter extends BaseSwipeAdapter {

    // 上下文对象
    private Context mContext;
    private ArrayList<FunncoEventCustomer> list;
    private Post post;
    private int deleteposition;
    private VolleyUtils utils;

    // 构造函数
    public PwListViewAdapter(Context mContext, ArrayList<FunncoEventCustomer> list, Post post) {
        this.mContext = mContext;
        this.list = list;
        this.post = post;
        utils = new VolleyUtils(mContext);
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

    // SwipeLayout的布局id
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swip;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_callcustomer_pw, parent, false);

        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));

        final TextView delete = (TextView) swipeLayout.findViewById(R.id.delete);
//		delete.setVisibility(View.INVISIBLE);
        // 当隐藏的删除menu被打开的时候的回调函数
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                super.onOpen(layout);
                deleteposition = Integer.parseInt((String) swipeLayout.findViewById(R.id.delete).getTag(R.id.id_delete));
                Toast.makeText(mContext, "Open " + deleteposition, Toast.LENGTH_LONG).show();
            }
        });
        LinearLayout dele = (LinearLayout) view.findViewById(R.id.ll_menu);
        dele.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close();
                post.post(deleteposition);

            }
        });
        return view;
    }


    @Override
    public void fillValues(int position, View convertView) {
        convertView.setTag(R.id.id_delete, position + "");
        FunncoEventCustomer funncoEventCustomer = list.get(position);
        //给删除按钮打标记
        convertView.findViewById(R.id.delete).setTag(R.id.id_delete, position + "");

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_item_callcustomer_truename);
        tvTitle.setText(funncoEventCustomer.getTruename() + "");
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_item_callcustomer_mobile);
        tvTime.setText(funncoEventCustomer.getRemark() + "");

        final ImageView ivCustomer = (ImageView) convertView.findViewById(R.id.iv_item_callcustomer_icon);

        utils.imageRequest(funncoEventCustomer.getHeadpic(), 0, 0, new DataBack() {
            @Override
            public void getString(String result) {

            }

            @Override
            public void getBitmap(String rul,Bitmap bitmap) {
                if (bitmap != null) {
                    Bitmap bt = BitmapUtils.getCircleBitmap2(bitmap, R.mipmap.icon_edit_profile_many_pic_2x);
                    ivCustomer.setImageBitmap(bt);
                }
            }
        });
    }
}
