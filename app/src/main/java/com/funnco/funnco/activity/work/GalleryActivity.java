package com.funnco.funnco.activity.work;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.bean.WorkItem;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.PhotoView;
import com.funnco.funnco.view.viewpager.ViewPagerFixed;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片预览（网络）
 * Created by user on 2015/6/1.
 */
public class GalleryActivity extends BaseActivity {

    private View parentView;//布局
    private ViewPagerFixed viewPagerFixed;//用于图片播放的
    //头部
    private TextView tvBack;
    //图片信息i
    private TextView tvTitle2;
    private TextView tvDate;
    private TextView tvContent;
    private TextView tvIndex;
    //所有的可预览的作品对象集合
    private ArrayList<WorkItem> list  = new ArrayList<>();
    private ArrayList<View> listViews;
    private boolean isTeamWork = false;
    private String team_id;
    private Intent intent;
    private int playPosition = 0;
    //播放的起始位置
    private int displayIndex = 0;
    private Map<String, Object> map = new HashMap<>();
    private TeamMy team;

    private static final int REQUEST_CODE_EDITWORK = 0xf600;
    private static final int REQUEST_CODE_EDITTEAMWORK = 0xf610;
    private static final int RESULT_CODE_EDITWORK = 0xf601;
    private static final int RESULT_CODE_EDITTEAMWORK = 103;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        intent = getIntent();
        if (intent != null){
            displayIndex = intent.getIntExtra("position",0);
            isTeamWork = intent.getBooleanExtra("isTeamWork", false);
            if (isTeamWork){
                team_id = intent.getStringExtra("team_id");
                String key = intent.getStringExtra(KEY);
                if (!TextUtils.isNull(key)){
                    team = (TeamMy) BaseApplication.getInstance().getT(key);//用于团队照片的浏览
                    BaseApplication.getInstance().removeT(key);
                }
            }
            LogUtils.e("GalleryActivity 拿到的播放起始点是：",""+displayIndex);
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_galley, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
//        utils = new VolleyUtils(this);
//        btmUtil = new BitmapUtils(this, Environment.getExternalStorageDirectory().getAbsolutePath()+"/Funnco");
        list.addAll(BaseApplication.list);
        BaseApplication.list.clear();
        viewPagerFixed = (ViewPagerFixed) findViewById(R.id.vpf_galley_image);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.icon_edit_profile_default_2x)
                .showImageForEmptyUri(R.mipmap.icon_edit_profile_default_2x)
                .showImageOnFail(R.mipmap.icon_edit_profile_default_2x)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ((TextView) findViewById(R.id.id_mview)).setText(R.string.preview);
        tvBack = (TextView) findViewById(R.id.id_lview);

        tvTitle2 = (TextView) findViewById(R.id.tv_activity_galley_title);
        tvContent = (TextView) findViewById(R.id.tv_activity_galley_content);
        tvDate = (TextView) findViewById(R.id.tv_activity_galley_date);
        tvIndex = (TextView)parentView.findViewById(R.id.tv_activity_galley_index);
        listViews = new ArrayList<>();
        initListView();
        adapter = new MyPageAdapter(listViews);
        viewPagerFixed.setAdapter(adapter);
        viewPagerFixed.setCurrentItem(displayIndex);
    }

    private void initListView() {
        setText(displayIndex);
        for (WorkItem item : list){
            PhotoView img = new PhotoView(this);
            img.setBackgroundColor(0xff000000);
            img.setTag(item.getPic_bg());
            img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            listViews.add(img);
        }
    }

    @Override
    protected void initEvents() {
        tvBack.setOnClickListener(this);//返回
        findViewById(R.id.id_rview).setOnClickListener(this);//修改
        findViewById(R.id.id_delete).setOnClickListener(this);//删除

        viewPagerFixed.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                playPosition = position;
                setText(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    private void setText(int position){
        if (tvIndex == null || tvDate == null || tvTitle2 == null || list == null || list.size() <= 0 || position >= list.size()){
            return;
        }
        WorkItem item = list.get(position);
        tvIndex.setText((position+1)+"/"+list.size());
        String title = item.getTitle()+"";
        String desc = item.getDescription()+"";
        if (TextUtils.isNull(title)){
            title = "";
        }
        if (TextUtils.isNull(desc)){
            desc = "";
        }
        tvBack.setText(title);
        tvDate.setText(item.getCreatetime()+"");
        tvContent.setText(desc);
        tvTitle2.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_lview://返回
                finishOk();
                break;
            case R.id.id_rview://编辑信息
                if (team != null && team.getRole().equals("2")){
                    showToast(R.string.str_team_notmanager);
                    return;
                }
                LogUtils.e("funnco------", "点击了编辑按钮");
                Intent i = new Intent();
                i.setClass(mContext, UpdateWorkActivity.class);
                i.putExtra(KEY, "itemWork");
                i.putExtra("isTeamwork", isTeamWork);
                if (isTeamWork) {
                    i.putExtra("team_id", team_id + "");
                }
                BaseApplication.getInstance().setT("itemWork",list.get(playPosition));
                startActivityForResult(i,REQUEST_CODE_EDITTEAMWORK);
                break;
            case R.id.id_delete://删除
                if (team != null && team.getRole().equals("2")){
                    showToast(R.string.str_team_notmanager);
                    return;
                }
                String title = getString(R.string.delete_y_n);
                FunncoUtils.showAlertDialog(mContext, title, new FunncoUtils.DialogCallback() {
                    @Override
                    public void onPositive() {
                        map.clear();
                        map.put("team_id", team_id);
                        map.put("id", list.get(playPosition).getId());
                        postData2(map, FunncoUrls.getDeleteWorkUrl(), false);
                    }
                });
                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        showToast(R.string.success);
        if (url.equals(FunncoUrls.getDeleteWorkUrl())){
            if (list != null && playPosition < list.size()){
                list.remove(playPosition);
                adapter.notifyDataSetChanged();
            }
        }
        super.dataPostBack(result, url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EDITTEAMWORK && resultCode == REQUEST_CODE_EDITTEAMWORK){
            if (data != null){
                String key = data.getStringExtra(KEY);
                if (!TextUtils.isNull(key)){
                    WorkItem item = (WorkItem) BaseApplication.getInstance().getT(key);
                    if (list != null && playPosition<list.size()){
                        list.remove(playPosition);
                        list.add(playPosition, item);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
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
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            PhotoView pv = (PhotoView) listViews.get(arg1 % size);
            try {

                String url = (String) pv.getTag();
                String url2 = list.get(arg1 % size).getPic_bg();
                LogUtils.e("图片的地址是："+url,",url2:"+url2);
                if (null != url) {
                    imageLoader.displayImage(url2,pv);
                }
                ((ViewPagerFixed) arg0).addView(pv, 0);
            } catch (Exception e) {}
            return pv;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null!= list && list.size() == 0 && null != listViews && listViews.size() ==0){
            list.addAll(BaseApplication.list);
            initListView();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.clearTask();
        list.clear();
        listViews.clear();
    }
}
