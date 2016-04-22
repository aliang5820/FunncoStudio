package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.listview.MyListview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的团队列表
 * Created by user on 2015/8/21.
 */
public class TeamMyActivity extends BaseActivity {


    private View parentView;
    private FrameLayout container;
    private TextView tvRView;//右上角创建团队

    private View vAddNotify;//团队列表为空 进行创建、添加
    private View vTeamList;//有团队列表
    private MyListview listView;
    private Button btAddteam;

    private Button btCreateNow,btJoinNow;

    private List<TeamMy> list = new ArrayList<>();
    private CommonAdapter<TeamMy> adapter;

    private static final int REQUEST_CODE_JOINTEAM = 0xf01;
    private static final int RESULT_CODE_JOINTEAM = 0xf11;

    private static final int REQUEST_CODE_CREATENOW = 0xf02;
    private static final int RESULT_CODE_CREATENOW = 0xf22;

    private static final int REQUEST_CODE_TEAMINFO = 0xf03;
    private static final int RESULT_CODE_TEAMINFO = 0xf13;

    private static final int RESULT_CODE_TEAM_BREAKUP = 0xf502;
    private static final int RESULT_CODE_TEAM_LOGOUT = 0xf503;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        tvRView = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvRView.setVisibility(View.VISIBLE);
        tvRView.setText(R.string.str_create);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_my);
        container = (FrameLayout) findViewById(R.id.layout_container);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);

        vTeamList = getLayoutInflater().inflate(R.layout.layout_activity_teammy, null);
        listView = (MyListview) vTeamList.findViewById(R.id.id_listView);
        vAddNotify = getLayoutInflater().inflate(R.layout.layout_activity_teamnofity, null);
        btCreateNow = (Button) vAddNotify.findViewById(R.id.id_title_0);
        btJoinNow = (Button) vAddNotify.findViewById(R.id.id_title_1);
        //方便测试
        container.addView(vAddNotify);
        btAddteam = (Button) vTeamList.findViewById(R.id.id_button);//如果当前团队超过两个则隐藏
        initAdapter();
    }

    private void initAdapter() {
        adapter = new CommonAdapter<TeamMy>(mContext, list, R.layout.layout_item_teammy) {
            @Override
            public void convert(ViewHolder helper, TeamMy item, int position) {
                ImageView iv = helper.getView(R.id.iv_item_teammy_teamicon);
                imageLoader.displayImage(item.getCover_pic(),iv,options);
//                iv.setImageResource(R.mipmap.common_schedule_conventiontype_icon);
                helper.setText(R.id.tv_item_teammy_teamname, item.getTeam_name());
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        btAddteam.setOnClickListener(this);
        btCreateNow.setOnClickListener(this);
        btJoinNow.setOnClickListener(this);
        tvRView.setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //单击各个项跳转到相应的   TeamInfoActivity.class
                if (position < list.size()) {
                    TeamMy team = list.get(position);
                    BaseApplication.getInstance().setT("teamMy", team);
                    Intent intent = new Intent();
                    intent.setClass(mContext, TeamMenuActivity.class);
                    intent.putExtra(KEY, "teamMy");
                    clearAsyncTask();
                    startActivityForResult(intent, REQUEST_CODE_TEAMINFO);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getTeamList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_button://添加团队
            case R.id.id_title_1://立刻加入(notify)
                if (list.size() >= 2){
                    showSimpleMessageDialog(R.string.str_team_maxsize_notify);
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(mContext, TeamAddActivity.class);
                startActivityForResult(intent, REQUEST_CODE_JOINTEAM);
                break;
            case R.id.tv_headcommon_headr://右上角 创建团队
            case R.id.id_title_0://立刻创建(notify)
                if (list.size() >= 2){
                    showSimpleMessageDialog(R.string.str_team_maxsize_notify);
                    return;
                }
                startActivityForResult(new Intent().setClass(mContext,TeamCreateActivity.class), REQUEST_CODE_CREATENOW);
                break;
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
        }
    }

    public void btnClick(View view) {

    }

    /**
     * 网络获取团队列表
     */
    private void getTeamList() {
        showLoading(parentView);
        putAsyncTask(AsyncTaskUtils.requestPost(null, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                if (JsonUtils.getResponseCode(result) == 0) {
                    JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                    if (paramsJSONObject != null) {
                        JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                        if (listJSONArray != null){
                            List<TeamMy> ls = JsonUtils.getObjectArray(listJSONArray.toString(), TeamMy.class);
                            if (ls != null && ls.size() > 0){
                                container.removeAllViews();
                                container.addView(vTeamList);
                                list.clear();
                                list.addAll(ls);
                                refreshAdapter();
                            }
                        }
                    }
                }
            }
            @Override
            public void getBitmap(String url, Bitmap bitmap) {}
        }, false, FunncoUrls.getTeamListUrl()));
    }
    private void refreshAdapter(){
        adapter.notifyDataSetChanged();
        if (list.size() >= 2){
            btAddteam.setVisibility(View.GONE);
        }else{
            btAddteam.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TEAMINFO && resultCode == RESULT_CODE_TEAM_BREAKUP|| resultCode == RESULT_CODE_TEAM_LOGOUT){
            if (data != null){
                String team_id = data.getStringExtra("team_id");
                if (list != null && list.size() >0){
                    for (TeamMy tm : list){
                        if (team_id.equals(tm.getTeam_id())){
                            list.remove(tm);
                            break;
                        }
                    }
                    refreshAdapter();
                }
            }
        }else if (requestCode == REQUEST_CODE_CREATENOW && resultCode == RESULT_CODE_CREATENOW){
            getTeamList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
