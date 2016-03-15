package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMemberMy;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.switcher.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队服务分配-- 成员选择
 * Created by user on 2015/10/19.
 */
public class ServiceDistributeMemberChooseActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private Intent intent;
    private TeamMy team;
    private String team_uid;

    private XListView xListView;
    private CommonAdapter adapter;
    private List<TeamMemberMy> list = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                if (xListView != null){
                    xListView.stopLoadMore();
                    xListView.stopRefresh();
                }
            }
        }
    };
    private static final int REQUEST_CODE_CHOOSESERVICE = 0xf300;
    private static final int RESULT_CODE_CHOOSESERVICE = 0xf301;
    private boolean isListMode = true;//true 表示服务列表模式 false 表示服务选择模式
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        container = (FrameLayout) findViewById(R.id.layout_container);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_service_distribute);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);
        intent = getIntent();
        if (intent != null){
            team_uid = intent.getStringExtra("team_uid");
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)){
                team = (TeamMy) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
                if (team != null){
                    getData();
                }
            }
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
    }
    private void initXListViewListener(){
        if (xListView != null){
            xListView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    clearAsyncTask();
                    getData();
                    handler.sendEmptyMessageDelayed(0, 2000);
                }

                @Override
                public void onLoadMore() {}
            });
            xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LogUtils.e("funnco------",""+position);
                    if (position-1 >=0 && position-1 < list.size()){
                        BaseApplication.getInstance().setT("teamMemberMy", list.get(position-1));
                        startActivityForResult(new Intent()
                                        .setClass(mContext, ServiceDistributeServiceListActivity.class)
                                        .putExtra(KEY, "teamMemberMy")
                                        .putExtra("team_id", team.getTeam_id()+"")
                                        .putExtra("team_uid", team_uid),
                                REQUEST_CODE_CHOOSESERVICE);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
        }
    }
    private void initAdapter(){
        xListView = new XListView(mContext);
        xListView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(true);
        xListView.setFooterVisibleState(false);
        xListView.setHeaderVisibleState(true);
        xListView.setDividerHeight(0);
        adapter = new CommonAdapter<TeamMemberMy>(mContext,list,R.layout.layout_item_teammember_my) {
            @Override
            public void convert(ViewHolder helper, TeamMemberMy item, int position) {
                SwipeLayout swipe = helper.getView(R.id.swip);
                swipe.setSwipeEnabled(false);
                if (position == 0){
                    helper.getView(R.id.tip).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tip, R.string.str_team_member_2);
                }else{
                    helper.getView(R.id.tip).setVisibility(View.GONE);
                }
                helper.setText(R.id.id_textview, item.getNickname());
                CircleImageView civ = helper.getView(R.id.id_imageview);
                imageLoader.displayImage(item.getHeadpic(), civ, options);
                //考虑是否要给自己分配 服务和课程
            }
        };
        xListView.setAdapter(adapter);
        container.removeAllViews();
        container.addView(xListView);
        initXListViewListener();
    }
    private void getData(){
        if (NetUtils.isConnection(mContext)){
            getData4Net();
        }else{
            showNetInfo();
        }
    }

    private void getData4Net() {
        if (team == null){
            LogUtils.e("funnco","数据信息异常。。。team is null...");
            return;
        }
        Map<String ,Object> map = new HashMap<>();
        map.put("team_id", team.getTeam_id());
        showLoading(parentView);
        postData2(map, FunncoUrls.getTeamMemberListUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
        if (url.equals(FunncoUrls.getTeamMemberListUrl())) {
            if (paramsJSONObject != null) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null) {
                    List<TeamMemberMy> ls = JsonUtils.getObjectArray(listJSONArray.toString(), TeamMemberMy.class);
                    if (ls != null && ls.size() > 0) {
                        list.clear();
                        list.addAll(ls);
                        if (xListView == null || adapter == null) {
                            initAdapter();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }else {

        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
    }
}
