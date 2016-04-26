package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.work.AddWorkActivity;
import com.funnco.funnco.activity.work.GalleryActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.bean.WorkItem;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.listview.XListView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 团队照片 不规则宫格显示
 * Created by user on 2015/10/16.
 */
public class TeamWorkActivity extends BaseActivity {

    private View parentView;
    private XListView xListView;
    private MyAdapter adapter;
    private boolean isMyPhoto = false;
    private Intent intent;
    private TeamMy team;
    //页数 和容量
    private int pageSize = 9;
    private int pageIndex = 1;
    //作品对象集合
    private List<WorkItem> list = new CopyOnWriteArrayList<>();

    private static final int REQUEST_CODE_GALLERY = 0xf401;
    private static final int RESULT_CODE_GALLERY = 0xf411;
    private static final int REQUEST_CODE_ADDTEAMWORK = 0xf421;
    private static final int RESULT_CODE_ADDTEAMWORK = 0xf422;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (xListView != null) {
                    xListView.stopLoadMore();
                    xListView.stopRefresh();
                }
            }
        }
    };

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teamwork, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null) {
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)) {
                team = (TeamMy) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
            }
            String title = intent.getStringExtra("info");
            if (!TextUtils.isNull(title)) {
                ((TextView) findViewById(R.id.id_mview)).setText(title);
                isMyPhoto = true;
            } else {
                ((TextView) findViewById(R.id.id_mview)).setText(R.string.str_team_photo);
            }
        }

        xListView = (XListView) findViewById(R.id.id_listView);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(true);
        xListView.setFooterVisibleState(false);
        xListView.setHeaderVisibleState(true);
        adapter = new MyAdapter();
        xListView.setAdapter(adapter);
        getData();
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.id_lview).setOnClickListener(this);
        findViewById(R.id.id_rview).setOnClickListener(this);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                //下载新数据
//                pageIndex = 1;
//                if (list.size() <= 9) {
//                    pageSize = 9;
//                }else{
//                    pageSize = list.size();
//                }
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0, 2000);
                getData();
//                pageSize = 9;
            }

            @Override
            public void onLoadMore() {

            }
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                BaseApplication.list.clear();
                BaseApplication.list.addAll(list);
                BaseApplication.getInstance().setT("team", team);
                intent.putExtra(KEY, "team");
                intent.putExtra("position", 0);
                intent.putExtra("isTeamwork", true);
                intent.putExtra("team_id", team != null ? team.getTeam_id() : "");
                intent.setClass(mContext, GalleryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_lview://返回
                finishOk();
                break;
            case R.id.id_rview://添加
                //先判断大小 决定是否可以在进行添加图片
                Intent i = new Intent();
                i.setClass(mContext, AddWorkActivity.class);
                i.putExtra("number", 9);
                if (!isMyPhoto) {
                    i.putExtra("team_id", team.getTeam_id());
                }
                i.putExtra("isTeamWork", true);
                startActivityForResult(i, REQUEST_CODE_ADDTEAMWORK);
                break;
        }
    }

    private void getData() {
        if (NetUtils.isConnection(mContext)) {
            getData4Net();
        } else {
            showNetInfo();
        }
    }

    private void getData4Net() {
        Map<String, Object> map = new HashMap<>();
        if (!isMyPhoto) {
            if (team == null || TextUtils.isNull(team.getTeam_id())) {
                showToast(R.string.data_err);
                return;
            }
            map.put("team_id", team.getTeam_id() + "");
        } else {
            map.put("team_id", "");
        }
        map.put("page", pageIndex + "");
        map.put("pagesize", pageSize + "");
        showLoading(parentView);
        postData2(map, FunncoUrls.getWorkListUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        dismissLoading();
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getWorkListUrl())) {
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null) {
                    List<WorkItem> ls = JsonUtils.getObjectArray(listJSONArray.toString(), WorkItem.class);
                    if (ls != null && ls.size() > 0) {
                        list.clear();
                        list.addAll(ls);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        dismissLoading();
        super.dataPostBackF(result, url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_CODE_GALLERY) {

        } else if (requestCode == REQUEST_CODE_ADDTEAMWORK && resultCode == RESULT_CODE_ADDTEAMWORK) {//添加作品
            clearAsyncTask();
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MyAdapter extends BaseAdapter implements ViewSwitcher.ViewFactory {
        private int range = 9;

        @Override
        public int getCount() {
//            int count = imagePahts.length % 9;
//            if(count == 0){
//                return imagePahts.length / 9;
//            }
//            return imagePahts.length / 9 - 1;
            int count = list.size() % range;
            if (count == 0) {
                return list.size() / range;
            }
            return list.size() / range + 1;
        }

        @Override
        public Object getItem(int position) {
//            return imagePahts[position];
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        MyHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int startPosition = position * range;
            int endPosition = position * range + range;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_item_teamwork, null);
                holder = new MyHolder(convertView, list.size());
                convertView.setTag(holder);
            } else {
                holder = (MyHolder) convertView.getTag();
            }
            for (int i = startPosition; i < endPosition; i++) {
                if (i >= list.size()) {
                    continue;
                }
                setAni(this, holder.iss[i], list.get(i).getPic_sm(), startPosition);
//                setAni(this,holder.iss[i],imagePahts[i]);
            }
            return convertView;
        }

        private void setAni(ViewSwitcher.ViewFactory vf, final ImageSwitcher is, String imagePath, int startPosition) {
            int count = is.getChildCount();
            LogUtils.e("funnco-----", "ImageSwitcher 的child view 个数是：" + count);
            is.removeAllViews();
            is.setFactory(vf);
            is.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_alpha_in));
            is.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_alpha_out));

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(mScreenWidth / 3, mScreenWidth / 3);
            LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(mScreenWidth / 3 * 2, mScreenWidth / 3 * 2);
            if (imagePath.equals(list.get(startPosition).getPic_sm())) {
                is.setLayoutParams(params0);
            } else {
                is.setLayoutParams(params1);
            }
            imageLoader.displayImage(imagePath, new ImageView(mContext), options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap != null) {
                        is.setImageDrawable(new BitmapDrawable(bitmap));
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }

        private class MyHolder {
            private ImageSwitcher[] iss;
            private int[] ids = new int[]{R.id.id_title_0, R.id.id_title_1, R.id.id_title_2,
                    R.id.id_title_3, R.id.id_title_4, R.id.id_title_5,
                    R.id.id_title_6, R.id.id_title_7, R.id.id_title_8};

            public MyHolder(View view, int size) {
                iss = new ImageSwitcher[size];
                for (int i = 0; i < size; i++) {
                    if (i > 8) {
                        break;
                    }
                    iss[i] = (ImageSwitcher) view.findViewById(ids[i]);
                }
            }
        }

        @Override
        public View makeView() {
            ImageView iView = new ImageView(mContext);
            iView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
                    ImageSwitcher.LayoutParams.MATCH_PARENT, ImageSwitcher.LayoutParams.MATCH_PARENT);
            params.setMargins(1, 1, 1, 1);
            iView.setLayoutParams(params);
            iView.setBackgroundColor(0xFF000000);
            return iView;
        }
    }
}
