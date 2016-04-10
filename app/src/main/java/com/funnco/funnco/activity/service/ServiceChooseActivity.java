package com.funnco.funnco.activity.service;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.bean.Team;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.listview.XListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  服务选择
 * Created by user on 2015/9/24.
 */
public class ServiceChooseActivity extends BaseActivity {

    private View parentView;
    private FrameLayout flayout;
    private XListView xListView;
    private CommonAdapter<Serve> adapter;
    private List<Serve> list = new ArrayList<>();
    private Intent intent;
    private UserLoginInfo user;
    private boolean isTeamservice = false;
    private boolean isConvertionService = false;
    private String team_id;
    private String team_uid;
    private int service_type = 0;
    private Map<String, Object> map = new HashMap<>();

    private static final int RESULT_CODE_SERVICECHOOSE = 0xf17;
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null){
            isTeamservice = intent.getBooleanExtra("isTeamservice", false);
            isConvertionService = intent.getBooleanExtra("isConvertionService", false);
            service_type = intent.getIntExtra("service_type", 0);
            if (isConvertionService){
                team_id = intent.getStringExtra("team_id");
                team_uid = intent.getStringExtra("team_uid");
            }
            if (isTeamservice) {
                team_id = intent.getStringExtra("team_id");
            }
        }
        user = BaseApplication.getInstance().getUser();
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_service_choose);
        flayout = (FrameLayout) findViewById(R.id.layout_container);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);
        xListView = new XListView(mContext);
        xListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        xListView.setSelector(new BitmapDrawable());
        xListView.setDivider(null);
        xListView.setDividerHeight(0);
        xListView.setHeaderVisibleState(false);
        xListView.setFooterVisibleState(false);
        xListView.setPullRefreshEnable(false);
        flayout.addView(xListView);

        adapter = new CommonAdapter<Serve>(mContext,list,R.layout.layout_item_servicetogether) {
            @Override
            public void convert(ViewHolder helper, Serve item, int position) {
                helper.getView(R.id.cb_item_servicetogether_ischecked).setVisibility(View.GONE);
                String extra = "";
                if(Long.valueOf(item.getTeam_id()) > 0) {
                    extra = "(团队服务)";
                }
                helper.setText(R.id.tv_item_servicetogether_servicename,item.getService_name() + extra);
            }
        };
        xListView.setAdapter(adapter);
        getData();
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra(KEY,"service");
                intent.putExtra("service_type", service_type);
                BaseApplication.getInstance().setT("service",list.get(position - 1));
                setResult(RESULT_CODE_SERVICECHOOSE, intent);
                finishOk();
            }
        });
    }

    private void getData(){
        map.clear();
        if (isTeamservice){
            map.put("team_id", team_id);
        }else if (isConvertionService){
            map.put("team_id", team_id);
            map.put("team_uid", team_uid);
        }
        map.put("service_type", service_type + "");
        if (user != null && NetUtils.isConnection(mContext)){
            getService4Net();
        }else{
            getService4Db();
        }
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
        postData2(map, FunncoUrls.getServiceListUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
        JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
        if (list.size() > 0){
            list.clear();
        }
        List<Serve> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Serve.class);
        if (ls != null && ls.size() > 0){
            list.addAll(ls);
            BaseApplication.getInstance().getUser().setService_count(list.size() + "");
            SQliteAsynchTask.deleteAll(dbUtils, Serve.class);
            SQliteAsynchTask.saveOrUpdate(dbUtils,list);
        }
//                        parentView.findViewById(R.id.iv_notify).setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        xListView.stopLoadMore();
        xListView.stopRefresh();
        xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
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
