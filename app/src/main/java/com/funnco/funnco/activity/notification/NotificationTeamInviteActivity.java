package com.funnco.funnco.activity.notification;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamInvite;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队邀请通知 （应答）
 * Created by user on 2015/10/21.
 */
public class NotificationTeamInviteActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private XListView xListView;
    private CommonAdapter adapter;
    private List<TeamInvite> list = new ArrayList<>();
    private UserLoginInfo user;
    Map<String, Object> map = new HashMap<>();

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

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        container = (FrameLayout) findViewById(R.id.layout_container);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_notification_team);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);
        user = BaseApplication.getInstance().getUser();
        xListView = new XListView(mContext);
        xListView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        xListView.setDividerHeight(0);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(true);
        xListView.setFooterVisibleState(false);
        container.addView(xListView);
        initAdapter();
        getData();
    }

    private void getData() {
        if (user != null && NetUtils.isConnection(mContext)){
            map.clear();
            map.put("team_uid", user.getId() + "");
            showLoading(parentView);
            postData2(map,FunncoUrls.getMessageTeamUrl(), false);
        }else{
            getData4Db();
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (xListView != null){
            xListView.stopLoadMore();
            xListView.stopRefresh();
        }
        dismissLoading();
        if (url.equals(FunncoUrls.getMessageTeamUrl())) {
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null) {
                    List<TeamInvite> ls = JsonUtils.getObjectArray(listJSONArray.toString(), TeamInvite.class);
                    if (ls != null && ls.size() > 0) {
                        list.clear();
                        list.addAll(ls);
                    } else {
                        list.clear();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }else if (url.equals(FunncoUrls.getTeamInviteReplyUrl())){
            String msg = JsonUtils.getResponseMsg(result);
            showToast(msg + "");
            clearAsyncTask();
            getData();
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        if (xListView != null){
            xListView.stopLoadMore();
            xListView.stopRefresh();
        }
        dismissLoading();
    }

    private void getData4Db() {
        if (dbUtils != null){
//            SQliteAsynchTask.selectTall(dbUtils,)
        }
    }

    private void initAdapter(){
        adapter = new CommonAdapter<TeamInvite>(mContext,list,R.layout.layout_item_notification_invite) {
            @Override
            public void convert(ViewHolder helper, TeamInvite item, int position) {
                CircleImageView civ = helper.getView(R.id.id_imageview);
                String imagepath = item.getHeadpic();
                if (!TextUtils.isNull(imagepath)) {
                    imageLoader.displayImage(imagepath, civ, options);
                }
                helper.setText(R.id.id_title_0, item.getInviter_nickname());
                helper.setText(R.id.id_title_2, item.getTeam_name());
                helper.setText(R.id.time, item.getCreatetime());
                String status = item.getStatus();
                if (status.equals("0")){
                    helper.getView(R.id.lineView).setVisibility(View.VISIBLE);
                    helper.getView(R.id.id_title_5).setVisibility(View.VISIBLE);
                    helper.getView(R.id.id_title_5).setVisibility(View.VISIBLE);
                    helper.getView(R.id.id_title_6).setVisibility(View.GONE);
                }else {
                    helper.getView(R.id.lineView).setVisibility(View.GONE);
                    helper.getView(R.id.id_title_5).setVisibility(View.GONE);
                    helper.getView(R.id.id_title_7).setVisibility(View.GONE);
                    helper.getView(R.id.id_title_6).setVisibility(View.VISIBLE);
                    if (status.equals("1")){
                        helper.setText(R.id.id_title_6, R.string.str_agreen_done);
                    }else{
                        helper.setText(R.id.id_title_6, R.string.str_refuse_done);
                    }
                }
                helper.setCommonListener(R.id.id_title_5, new Post() {
                    @Override
                    public void post(int... position) {
                        int index = position[0];
                        if (index >=0 && index < list.size()){
                            map.clear();
                            map.put("status", "1");
                            map.put("id", list.get(position[0]).getId());
                            showLoading(parentView);
                            postData2(map, FunncoUrls.getTeamInviteReplyUrl(),false);
                        }
                    }
                });
                helper.setCommonListener(R.id.id_title_7, new Post() {
                    @Override
                    public void post(int... position) {
                        int index = position[0];
                        if (index >=0 && index < list.size()){
                            map.clear();
                            map.put("status", "2");
                            map.put("id",list.get(position[0]).getId());
                            showLoading(parentView);
                            postData2(map, FunncoUrls.getTeamInviteReplyUrl(),false);
                        }
                    }
                });
            }
        };
        adapter.isTag(true, new int[]{R.id.id_title_5,R.id.id_title_7});
        xListView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }
}
