package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.service.AddCoursesActivity;
import com.funnco.funnco.activity.service.AddServiceActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.bean.Team;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.layout.HandyLinearLayout;
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
 * 团队服务
 * Created by user on 2015/10/15.
 */
public class TeamServiceActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private XListView xListView;
    private TextView tvServiceDistribute;
    private TextView tvAddService;
    private ImageView ivAddNotify;
    private CommonAdapter<Serve> adapter;
    private List<Serve> list = new ArrayList<>();
    private Intent intent;
    private UserLoginInfo user;

    private PopupWindow pwAddnotify;
    private View vAddnotify;
    private TeamMy team;
    private Map<String ,Object> map = new HashMap<>();
    private int delposition;

    private static final int REQUEST_CODE_TEAMSERVICE_ADD = 0xf100;
    private static final int RESULT_CODE_TEAMSERVICE_ADD = 0xf101;
    private static final int REQUEST_CODE_TEAMCOURSES_ADD = 0xf110;
    private static final int REQUEST_CODE_TEAMSERVICE_EDIT = 0xf120;
    private static final int REQUEST_CODE_TEAMCOURSES_EDIT = 0xf130;

    private static final int REQUEST_CODE_TEAMSERVICEDISTRIBUTE_MEMBERCHOOSE = 0xf110;
    private static final int RESULT_CODE_TEAMSERVICEDISTRIBUTE_MEMBERCHOOSE = 0xf111;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                if (xListView != null){
                    xListView.stopRefresh();
                    xListView.stopLoadMore();
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
        tvAddService = (TextView) findViewById(R.id.tv_headcommon_headr);
        container = (FrameLayout) findViewById(R.id.layout_container);
        intent = getIntent();
        user = BaseApplication.getInstance().getUser();
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_service);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);
        vAddnotify = LayoutInflater.from(mContext).inflate(R.layout.layout_popupwindow_addservice, null);
        if (intent != null){
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)){
                team = (TeamMy) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
                if (team.getRole().equals("2")){//组员 没有添加服务的权限
                    tvAddService.setVisibility(View.GONE);
                }else{//管理员和创建者
                    tvAddService.setVisibility(View.VISIBLE);
//                    Drawable drawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.selector_titlebar_add));
//                    drawable.setBounds(0,80,0,80);
//                    tvAddService.setCompoundDrawables(null,null,drawable,null);
                    tvAddService.setBackgroundResource(R.drawable.selector_titlebar_add);
                }
            }
        }
    }
    private void showPw(View view){
        pwAddnotify = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pwAddnotify.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }
    private boolean dismissPw(){
        boolean flag = false;
        for(PopupWindow pw : new PopupWindow[]{pwAddnotify, pwLoading}){
            if (pw != null && pw.isShowing()){
                pw.dismiss();
                flag = true;
            }
        }
        return flag;
    }
    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvAddService.setOnClickListener(this);
        vAddnotify.findViewById(R.id.id_tview).setOnClickListener(this);
        vAddnotify.findViewById(R.id.bt_pw_addservice_service).setOnClickListener(this);
        vAddnotify.findViewById(R.id.bt_pw_addservice_courses).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void initListViewListener(){
        if (xListView != null){
            xListView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    clearAsyncTask();
                    handler.sendEmptyMessageDelayed(0,2000);
                    getData();
                }

                @Override
                public void onLoadMore() {
                    handler.sendEmptyMessageDelayed(0,2000);
                }
            });
            xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position - 1 >= 0 && position - 1 < list.size()){
                        if (team != null && team.getRole().equals("2")){//普通成员没有修改服务的权限
//                            showToast(R.string.str_team_notmanager);
                            return;
                        }
                        Serve serve = list.get(position - 1);
                        BaseApplication.getInstance().setT("serve", serve);
//                        bundle.putParcelable("serve", serve);
                        if (serve.getService_type().equals("0")) {

                            startActivityForResult(new Intent(mContext, AddServiceActivity.class)
                                    .putExtra(KEY, "serve")
                                    .putExtra("isEditService", true)
                                    .putExtra("title", getString(R.string.str_service_editservice_team))
                                    .putExtra("isTeamService", true)
                                    .putExtra("team_id",team.getTeam_id())
                                    , REQUEST_CODE_TEAMSERVICE_EDIT);
                        }else{

                            startActivityForResult(new Intent(mContext, AddCoursesActivity.class)
                                    .putExtra(KEY, "serve")
                                    .putExtra("isEdit", true)
                                    .putExtra("isTeamService", true)
                                    .putExtra("team_id", team.getTeam_id()), REQUEST_CODE_TEAMCOURSES_EDIT);
                        }
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
            case R.id.tv_headcommon_headr://添加团队服务
                if (team != null){
                    showPw(vAddnotify);
                    return;
                }else{
                    showSimpleMessageDialog(R.string.data_err);
                }
                break;
            case R.id.bt_pw_addservice_service://添加服务
                String title = getString(R.string.str_team_service);
                startActivityForResult(new Intent(mContext, AddServiceActivity.class)
                        .putExtra("isTeamService", true)
                        .putExtra("team_id", team.getTeam_id())
                        .putExtra("title",""+title), REQUEST_CODE_TEAMSERVICE_ADD);
                break;
            case R.id.bt_pw_addservice_courses:
                startActivityForResult(new Intent(mContext, AddCoursesActivity.class)
                        .putExtra("isTeamService", true)
                        .putExtra("team_id", team.getTeam_id()), REQUEST_CODE_TEAMCOURSES_ADD);
                break;
        }
        dismissPw();
    }

    private void getData(){
        if (user != null && NetUtils.isConnection(mContext)){
            getService4Net();
        }else{
            getService4Db();
        }
    }

    private SwipeLayout swipeLayout;
    private void closeSwip(){
        if (swipeLayout != null){
            swipeLayout.close();
        }
    }
    private void initDataUI(){
        container.removeAllViews();
        tvServiceDistribute = new TextView(mContext);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, FunncoUtils.dp2px(mActivity, 10), 0, FunncoUtils.dp2px(mActivity, 10));
        tvServiceDistribute.setLayoutParams(params1);
        tvServiceDistribute.setPadding(FunncoUtils.dp2px(mActivity, 15), FunncoUtils.dp2px(mActivity, 10), FunncoUtils.dp2px(mActivity, 15), FunncoUtils.dp2px(mActivity, 10));
        tvServiceDistribute.setText(R.string.str_service_distribute);
        tvServiceDistribute.setTextSize(16);
        tvServiceDistribute.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.common_enter_arrow_2x ,0);
        tvServiceDistribute.setBackgroundColor(Color.WHITE);
        tvServiceDistribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team == null) {
                    return;
                }
                if (!team.getRole().equals("0") && !team.getRole().equals("1")) {
                    showToast("无访问权限");
                    return;
                }
                BaseApplication.getInstance().setT("teamMy", team);
                startActivityForResult(new Intent().
                        setClass(mContext, ServiceDistributeMemberChooseActivity.class)
                        .putExtra(KEY, "teamMy")
                        .putExtra("team_uid", user.getId() + "")
                        , REQUEST_CODE_TEAMSERVICEDISTRIBUTE_MEMBERCHOOSE);
            }
        });
        container.addView(tvServiceDistribute);

        xListView = new XListView(mContext);
        FrameLayout.LayoutParams params2 =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params2.setMargins(0, FunncoUtils.dp2px(mActivity, 10) + FunncoUtils.getViewMesureSpec(tvServiceDistribute)[1], 0, 0);
        xListView.setLayoutParams(params2);
        xListView.setSelector(new BitmapDrawable());
        xListView.setDivider(null);
        xListView.setDividerHeight(0);
        xListView.setHeaderVisibleState(true);
        xListView.setFooterVisibleState(false);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        container.addView(xListView);
        adapter = new CommonAdapter<Serve>(mContext,list,R.layout.layout_item_team_service) {
            @Override
            public void convert(ViewHolder helper, Serve item, int position) {
                SwipeLayout swip = helper.getView(R.id.swip);
                String tip = item.getService_type();
                String service_type = item.getService_type();
                if (getPosition(tip) == position){
                    helper.getView(R.id.tip).setVisibility(View.VISIBLE);
                    if (tip.equals("0")){
                        helper.setText(R.id.tip, R.string.str_service_list);
                    }else{
                        helper.setText(R.id.tip, R.string.str_service_courses_list);
                    }
                }else{
                    helper.getView(R.id.tip).setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_item_teamservice_servicename,item.getService_name());
                HandyLinearLayout hlayout = helper.getView(R.id.id_linearlayout);
                if (service_type.equals("0")){//服务
                    helper.getView(R.id.id_textview).setVisibility(View.GONE);
                    helper.getView(R.id.tv_item_teamservice_price).setVisibility(View.VISIBLE);
                    int duration = Integer.valueOf(item.getDuration());
                    helper.setText(R.id.tv_item_teamservice_price, item.getPrice() + "元/" + DateUtils.getTime4Minutes2(duration));
                    hlayout.setVisibility(View.VISIBLE);
                    String weeks = item.getWeeks();
                    for (int i = 0; i < hlayout.getChildCount(); i ++){
                        CheckBox cb = (CheckBox) hlayout.getChildAt(i);
                        cb.setChecked(false);
                        cb.setTextColor(Color.parseColor("#ff9fa7b0"));
                    }
                    if (!TextUtils.isNull(weeks)){
                        String[] week = weeks.split(",");
                        for (String d : week){
                            d = d.trim();
                            if (!TextUtils.isNull(d)){
                                int index = Integer.parseInt(d);
                                CheckBox cb = (CheckBox) hlayout.getChildAt(index);
                                cb.setChecked(true);
                                cb.setTextColor(Color.parseColor("#ffffffff"));
                            }
                        }
                    }
                }else{//课程
                    helper.getView(R.id.tv_item_teamservice_price).setVisibility(View.GONE);
                    hlayout.setVisibility(View.GONE);
                    helper.getView(R.id.id_textview).setVisibility(View.VISIBLE);
                    int stime = Integer.valueOf(item.getStarttime());
                    int etime = Integer.valueOf(item.getEndtime());
                    helper.setText(R.id.id_textview, DateUtils.getTime4Minutes(stime)+" ~ "+DateUtils.getTime4Minutes(etime));
                }




                helper.setCommonListener(R.id.id_delete, new Post() {
                    @Override
                    public void post(int... position) {
                        if (team != null && team.getRole().equals("2")){//普通成员没有修改服务的权限
                            showToast(R.string.str_team_notmanager);
                            return;
                        }
                        closeSwip();
                        delposition = position[0];
                        FunncoUtils.showAlertDialog(mContext, R.string.delete_y_n, new FunncoUtils.DialogCallback() {
                            @Override
                            public void onPositive() {
                                clearAsyncTask();
                                map.clear();
                                map.put("team_id", list.get(delposition).getId());
                                map.put("id", list.get(delposition).getId());
                                postData2(map, FunncoUrls.getDeleteServicdeUrl(), false);
                            }
                        });

                    }
                });
                swip.addSwipeListener(new SimpleSwipeListener(){
                    @Override
                    public void onOpen(SwipeLayout layout) {
                        super.onOpen(layout);
                        if (layout != swipeLayout){
                            closeSwip();
                            swipeLayout = layout;
                        }
                    }
                });
            }
        };
        adapter.isTag(true, new int[]{R.id.id_delete});
        xListView.setAdapter(adapter);
        initListViewListener();
    }

    private int getPosition(String index){
        if (list ==null || list.size() == 0){
            return 0;
        }
        for (int i = 0 ;i < list.size() ; i++){
            if (index.equals(list.get(i).getService_type())){
                return i;
            }
        }
        return 0;
    }

    private void initNotifyUI(){
        container.removeAllViews();
        ivAddNotify = new ImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        ivAddNotify.setLayoutParams(params);
        ivAddNotify.setImageResource(R.mipmap.common_teamservice_add_notify);
        container.addView(ivAddNotify);
        ivAddNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team != null) {
                    startActivityForResult(new Intent(mContext, AddServiceActivity.class).putExtra("isTeamService", true).putExtra("teamId",team.getTeam_id()), REQUEST_CODE_TEAMSERVICE_ADD);
                }else{
                    showSimpleMessageDialog(R.string.data_err);
                }
            }
        });
    }

    private void getService4Db() {
        List<Serve> ls =  SQliteAsynchTask.selectTall(dbUtils, Team.class, null, null, null);
        if (ls != null && ls.size() > 0){
            list.clear();
            list.addAll(ls);
            adapter.notifyDataSetChanged();
        }
    }
    private void getService4Net() {
        showLoading(parentView);
        Map<String, Object> map = new HashMap<>();
        map.put("team_id", team.getTeam_id());
        postData2(map, FunncoUrls.getServiceListUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        if (url.equals(FunncoUrls.getServiceListUrl())){
            if (JsonUtils.getResponseCode(result) == 0) {
                JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (list.size() > 0) {
                    list.clear();
                }
                List<Serve> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Serve.class);
                if (ls != null && ls.size() > 0) {
                    initDataUI();
                    list.addAll(ls);
                    BaseApplication.getInstance().getUser().setService_count(list.size() + "");
                    SQliteAsynchTask.deleteAll(dbUtils, Serve.class);
                    SQliteAsynchTask.saveOrUpdate(dbUtils, list);
                }else if (ls == null || ls.size() == 0){
                    initNotifyUI();
                }
                if (adapter != null) {
                    sortRefresh();
                }
            }
            if (xListView != null) {
                xListView.stopLoadMore();
                xListView.stopRefresh();
                xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
            }
        }else if (url.equals(FunncoUrls.getDeleteServicdeUrl())){
            if (list != null && list.size() > delposition){
                list.remove(delposition);
                sortRefresh();
            }
        }
    }
    private void sortRefresh(){
        if (list != null){
            Collections.sort(list, new Comparator<Serve>() {
                @Override
                public int compare(Serve lhs, Serve rhs) {
                    return lhs.getService_type().compareTo(rhs.getService_type());
                }
            });
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
        initNotifyUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TEAMSERVICE_ADD || requestCode == REQUEST_CODE_TEAMCOURSES_ADD
                || requestCode == REQUEST_CODE_TEAMSERVICE_EDIT || requestCode == REQUEST_CODE_TEAMCOURSES_EDIT){
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
