package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.PersonalInfoActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMemberMy;
import com.funnco.funnco.bean.TeamMemberSearch;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.switcher.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队成员 我的团队
 * Created by user on 2015/9/29.
 */
public class TeamMemberActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private XListView xListView;
    private List<TeamMemberMy> list = new ArrayList<>();
    private CommonAdapter<TeamMemberMy> adapter;
    
    private UserLoginInfo user;
    private TeamMy team;
    private Intent intent;
    private SwipeLayout swipeLayout;

    private static final int REQUEST_CODE_ADDMEMBER_SEARCH = 0xf211;
    private static final int RESULT_CODE_SEARCH_TEAMMEMBER = 0xf12;
    private static final int REQUEST_CODE_MEMBERINFO = 0xf212;
    private static final int RESULT_CODE_MEMBERINFO = 0xf222;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    stopXlistview();
                    break;
            }
        }
    };
    private void stopXlistview(){
        if (xListView != null){
            xListView.stopLoadMore();
            xListView.stopRefresh();
            xListView.setRefreshTime(DateUtils.getCurrentDate());
        }
    }
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container_2, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null){
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)){
                team = (TeamMy) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
            }
        }
        user = BaseApplication.getInstance().getUser();
        container = (FrameLayout) findViewById(R.id.layout_container);
        ((TextView)findViewById(R.id.id_mview)).setText(R.string.str_team_member_2);
        xListView = new XListView(mContext);
        xListView.setFooterVisibleState(false);
        xListView.setHeaderVisibleState(false);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setDividerHeight(1);
        container.addView(xListView);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);
        adapter = new CommonAdapter<TeamMemberMy>(mContext,list,R.layout.layout_item_teammember_my) {
            @Override
            public void convert(ViewHolder helper, final TeamMemberMy item, int position) {
                //管理员、成员进行分组
                String role = item.getRole();
                final SwipeLayout swip = helper.getView(R.id.swip);
                if (getPositionFirst(role) == position){
                    helper.getView(R.id.tip).setVisibility(View.VISIBLE);
                }else{
                    helper.getView(R.id.tip).setVisibility(View.GONE);
                }
                if (role.equals("0")) {//创建者
                    helper.setText(R.id.tip, R.string.str_creater);
                    swip.setSwipeEnabled(false);
                }else if (role.equals("1")){//管理员
                    helper.setText(R.id.tip, R.string.str_manager);
                    swip.setSwipeEnabled(true);
                    helper.setText(R.id.id_title_0, R.string.str_out_team);
                    helper.setText(R.id.id_title_1, R.string.str_join_member);
                    helper.getView(R.id.id_title_2).setVisibility(View.GONE);
                }else{//成员
                    helper.setText(R.id.tip, R.string.str_member);
                    swip.setSwipeEnabled(true);
                    helper.setText(R.id.id_title_0, R.string.str_out_team);
                    helper.setText(R.id.id_title_1,R.string.str_join_manager);
                    helper.getView(R.id.id_title_2).setVisibility(View.GONE);
                }
                helper.setText(R.id.id_textview, item.getNickname());
                CircleImageView civ = helper.getView(R.id.id_imageview);
                imageLoader.displayImage(item.getHeadpic(), civ, options);
                swip.addSwipeListener(new SimpleSwipeListener(){
                    @Override
                    public void onOpen(SwipeLayout layout) {
                        super.onOpen(layout);
                        if (layout != swipeLayout) {
                            closeSwip();
                            swipeLayout = layout;
                        }
                    }
                });
                String role2 = role;
                if (role2.equals("1")){
                    role2 = "2";
                }else{
                    role2 = "1";
                }
                final String role3 = role2;
                helper.setCommonListener(R.id.id_title_0, new Post() {
                    @Override
                    public void post(int... position) {
                        closeSwip();
                        if (team != null && team.getRole().equals("2")){//普通成员没有修改服务的权限
                            showToast(R.string.str_team_notmanager);
                            return;
                        }
                        FunncoUtils.showAlertDialog(mContext, "是否移出团队？", new FunncoUtils.DialogCallback() {
                            @Override
                            public void onPositive() {
                                clearAsyncTask();
                                changeRole(item.getUid(),role3, true);
                            }
                        });
                    }
                });
                helper.setCommonListener(R.id.id_title_1, new Post() {
                    @Override
                    public void post(int... position) {
                        if (team != null && team.getRole().equals("2")){//普通成员没有修改服务的权限
                            showToast(R.string.str_team_notmanager);
                            return;
                        }
                        closeSwip();
                        clearAsyncTask();
                        changeRole(item.getUid(),role3,false);
                    }
                });
            }
        };
        adapter.isTag(true, new int[]{R.id.id_title_0, R.id.id_title_1, R.id.id_title_2});
        xListView.setAdapter(adapter);
        getData();
    }
    private void closeSwip(){
        if (swipeLayout != null){
            swipeLayout.close();
        }
    }

    private void getData() {
        if (user != null && NetUtils.isConnection(mContext)){

            if (team != null){
                String team_id = team.getTeam_id();
                if (!TextUtils.isNull(team_id)){
                    getData4Net(team_id);
                }else {
                    getData4DB();
                }
            }else{
                getData4DB();
            }
        }else{
            getData4DB();
        }
    }

    private int getPositionFirst(String index){
        if (list == null || list.size() == 0){
            return 0;
        }
        for (int i = 0 ;i < list.size() ;i ++){
            if (list.get(i).getRole().equals(index)){
                return i;
            }
        }
        return 0;
    }
    Map<String, Object>  map = new HashMap<>();
    private void getData4Net(String team_id) {
        showLoading(parentView);
        map.clear();
        map.put("team_id", team_id);
        postData2(map, FunncoUrls.getTeamMemberListUrl(), false);
    }

    /**
     * 发出团队成员邀请
     * @param team_id 团队Id
     * @param team_uid 邀请人的Id
     */
    private void postInvite(String team_id, String team_uid){
        map.clear();
        map.put("team_id", team_id + "");
        map.put("team_uid", team_uid + "");
        postData2(map, FunncoUrls.getTeamInviteUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        if (url.equals(FunncoUrls.getTeamInviteUrl())){
            showToast(R.string.str_team_invite_success);
        } else if(url.equals(FunncoUrls.getTeamMemberListUrl())){
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null) {
                    List<TeamMemberMy> ls = JsonUtils.getObjectArray(listJSONArray.toString(), TeamMemberMy.class);
                    if (ls != null && ls.size() > 0) {
                        SQliteAsynchTask.saveOrUpdate(dbUtils, ls);
                        list.clear();
                        list.addAll(ls);
                        Collections.sort(list, new Comparator<TeamMemberMy>() {
                            @Override
                            public int compare(TeamMemberMy lhs, TeamMemberMy rhs) {
                                return lhs.getRole().compareTo(rhs.getRole());
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
    }

    private void getData4DB() {
        showSimpleMessageDialog(R.string.user_err);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.id_lview).setOnClickListener(this);
        findViewById(R.id.id_rview).setOnClickListener(this);

        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessageDelayed(0, 1500);
                clearAsyncTask();
                getData();
            }

            @Override
            public void onLoadMore() {
            }
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("------", "点击的选项是：" + position);
                if ((position-1) >= 0 && (position - 1) < list.size()){
                    closeSwip();
                    clearAsyncTask();
                    Intent i = new Intent();
                    i.putExtra("isTeamMember",true);
                    i.putExtra(KEY,"teamMember");
                    i.putExtra("team_id",team.getTeam_id()+"");
                    BaseApplication.getInstance().setT("teamMember", list.get(position - 1));
                    i.setClass(mContext, PersonalInfoActivity.class);
                    startActivityForResult(i,REQUEST_CODE_MEMBERINFO);
                }
            }
        });

    }

    private void changeRole(String team_uid,String role, boolean isRemove){
        showLoading(parentView);
        Map<String ,Object> map = new HashMap<>();
        map.put("team_id",team.getTeam_id());
        map.put("team_uid",team_uid);
        if (!isRemove) {
            map.put("role", role);
        }
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                if (JsonUtils.getResponseCode(result) == 0){
                    LogUtils.e("funnco------","修改成功， 进行解析/跟新数据");
                    getData();
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {}
        }, false, isRemove ? FunncoUrls.getTeamMemberRemoveUrl() : FunncoUrls.getTeamMemberSetRoleUrl()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_lview://返回
                finishOk();
                break;
            case R.id.id_rview://添加
                startActivityForResult(new Intent().setClass(mContext, TeamAddMemberActivity.class), REQUEST_CODE_ADDMEMBER_SEARCH);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADDMEMBER_SEARCH && resultCode == RESULT_CODE_SEARCH_TEAMMEMBER){
            if (data != null){
                String key = data.getStringExtra(KEY);
                if (!TextUtils.isNull(key)){
                    TeamMemberSearch teamMemberSearch = (TeamMemberSearch) BaseApplication.getInstance().getT(key);
                    BaseApplication.getInstance().removeT(key);
                    if (teamMemberSearch != null && team != null ){
                        String team_id = team.getTeam_id();
                        String team_uid = teamMemberSearch.getId();
                        postInvite(team_id, team_uid);
                    }
                }
            }

        }else if (requestCode == REQUEST_CODE_MEMBERINFO && resultCode == RESULT_CODE_MEMBERINFO){
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
