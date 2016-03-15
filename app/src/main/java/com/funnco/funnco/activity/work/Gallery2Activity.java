package com.funnco.funnco.activity.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.view.imageview.PhotoView;
import com.funnco.funnco.view.viewpager.ViewPagerFixed;

import java.util.ArrayList;

/**
 * Created by user on 2015/6/1.
 * 用于本地图片播放
 * @author Shawn
 * @QQ 1206816341
 */
public class Gallery2Activity extends BaseActivity {

    private View parentView;//布局
    private ViewPagerFixed viewPagerFixed;//用于图片播放的
    //头部
    private TextView tvTitle;
    private TextView tvBack;
    //图片信息i
    private TextView tvTitle2;
    private TextView tvDate;
    private TextView tvContent;
    private TextView tvIndex;
    //所有的可预览的作品对象集合
    private ArrayList<ImageItem> list  = new ArrayList<>();
    private ArrayList<View> listViews;

    private Intent intent;
    //播放的起始位置
    private int displayIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化帮助类，获取信息
        intent = getIntent();
        if (intent != null){
            displayIndex = intent.getIntExtra("position",0);
            LogUtils.e("Gallery2Activity 拿到的播放起始点是：",""+displayIndex);
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_galley, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
//        btmUtil = new BitmapUtils(Gallery2Activity.this);
        //拿到当前的图片集合
        ArrayList<ImageItem> ls = (ArrayList<ImageItem>) BaseApplication.getInstance().getT("dataList");
        if (ls != null && ls.size() > 0){
            if (list.size() > 0)
                list.clear();
            list.addAll(ls);
            //通过Application 传递的值接收完毕后需要清除
            BaseApplication.getInstance().removeT("dataList");
        }
        viewPagerFixed = (ViewPagerFixed) findViewById(R.id.vpf_galley_image);

        tvTitle = (TextView) findViewById(R.id.id_mview);
        tvTitle.setText(getResources().getString(R.string.preview));
        tvBack = (TextView) findViewById(R.id.id_lview);

        tvTitle2 = (TextView) findViewById(R.id.tv_activity_galley_title);
        tvContent = (TextView) findViewById(R.id.tv_activity_galley_content);
        tvDate = (TextView) findViewById(R.id.tv_activity_galley_date);
        tvIndex = (TextView)parentView.findViewById(R.id.tv_activity_galley_index);
        findViewById(R.id.rlayout_activity_galley_bottominfo).setVisibility(View.GONE);
        listViews = new ArrayList<>();
        initListView();
        adapter = new MyPageAdapter(listViews);
        viewPagerFixed.setAdapter(adapter);
        viewPagerFixed.setCurrentItem(displayIndex);
    }

    private void initListView() {
        for (ImageItem item : list){
            PhotoView img = new PhotoView(this);
            img.setBackgroundColor(0xff000000);
            img.setTag(item.getImagePath());
            img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            listViews.add(img);
        }
    }

    @Override
    protected void initEvents() {
        tvBack.setOnClickListener(this);
        viewPagerFixed.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_lview://返回
                finishOk();
                break;
        }
    }

    private MyPageAdapter adapter;
    class MyPageAdapter extends PagerAdapter{

        private ArrayList<View> listViews;

        private int size;
        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }
        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            LogUtils.e("MyPageAdapter------------","getCount");
            return size;
        }

        public int getItemPosition(Object object) {
            LogUtils.e("MyPageAdapter------------", "getItemPosition");
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            LogUtils.e("MyPageAdapter------------","destroyItem");
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
            LogUtils.e("MyPageAdapter------------","finishUpdate");
        }

        public Object instantiateItem(View arg0, int arg1) {
            LogUtils.e("MyPageAdapter------------","instantiateItem");
            PhotoView pv = (PhotoView) listViews.get(arg1 % size);
                try {
                    String url = (String) pv.getTag();
                    if (null != url) {
                      pv.setImageBitmap(list.get(arg1 % size).getBitmap());
                    }
                    ((ViewPagerFixed) arg0).addView(pv, 0);
                } catch (Exception e) {
            }
            return pv;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            LogUtils.e("MyPageAdapter------------","isViewFromObject");
            return arg0 == arg1;
        }
    }
}
