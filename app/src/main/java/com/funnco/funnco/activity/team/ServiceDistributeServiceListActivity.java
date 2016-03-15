package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.bean.TeamMemberMy;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
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
 * 团队服务分配-- 已有服务列表
 * Created by user on 2015/9/29.
 */
public class ServiceDistributeServiceListActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private View vInfo;
    private XListView xListView;
    private CommonAdapter<Serve> adapter;
    private List<Serve> list = new ArrayList<>();
    private View viewHeader;
    private String team_id;
    private TeamMemberMy teamMemberMy;

    private Intent intent;
    private Map<String,Object> map = new HashMap<>();
    private static final int REQUEST_CODE_SERVICE_CHOOSE = 0xf700;
    private static final int RESULT_CODE_SERVICE_CHOOSE = 0xf701;
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
        intent = getIntent();
        if (intent != null){
            String key = intent.getStringExtra(KEY);
            team_id = intent.getStringExtra("team_id");
            if (!TextUtils.isNull(key)){
                teamMemberMy = (TeamMemberMy) BaseApplication.getInstance().getT(key);
                if (teamMemberMy == null){
                    finishOk();
                    return;
                }
                BaseApplication.getInstance().removeT(key);
            }
        }else{
            finishOk();
            return;
        }
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_service_distribute);
        ((TextView)findViewById(R.id.llayout_foot)).setText(R.string.str_project_modity);
        container = (FrameLayout) findViewById(R.id.layout_container);
        viewHeader = getLayoutInflater().inflate(R.layout.layout_item_memberchoose_pw, null);

        vInfo = getLayoutInflater().inflate(R.layout.layout_item_teammember_my, null);
        vInfo.findViewById(R.id.tip).setVisibility(View.GONE);
        vInfo.findViewById(R.id.id_textview).setVisibility(View.GONE);
        ((SwipeLayout)vInfo.findViewById(R.id.swip)).setSwipeEnabled(false);
        CircleImageView civ = (CircleImageView) vInfo.findViewById(R.id.id_imageview) ;
        TextView tvName = (TextView) vInfo.findViewById(R.id.id_textview);
        imageLoader.displayImage(teamMemberMy.getHeadpic(), civ, options);
        tvName.setText(teamMemberMy.getNickname() + "");
        container.addView(vInfo);

        xListView = new XListView(mContext);
        xListView.setFooterVisibleState(false);
        xListView.setHeaderVisibleState(false);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setDividerHeight(1);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, FunncoUtils.getViewMesureSpec(vInfo)[1],0,0);
        xListView.setLayoutParams(params);
        container.addView(xListView);
        adapter = new CommonAdapter<Serve>(mContext,list,R.layout.layout_item_member_3) {
            @Override
            public void convert(ViewHolder helper, Serve item, int position) {
                //判断显示  他的服务   他的课程
                TextView tip = helper.getView(R.id.tip);
                String letter = item.getService_type();
                if (position == getPositionLetter(letter)){
                    tip.setVisibility(View.VISIBLE);
                    if (letter.equals("0")){
                        tip.setText(R.string.str_service_hisservice);
                    }else if (letter.equals("1")){
                        tip.setText(R.string.str_service_hiscourses);
                    }
                }else{
                    tip.setVisibility(View.GONE);
                }
                helper.setText(R.id.id_title_0, item.getService_name());
                if (letter.equals("0")){// 服务
                    helper.getView(R.id.id_title_1).setVisibility(View.VISIBLE);
                    helper.getView(R.id.id_title_2).setVisibility(View.GONE);
                    helper.getView(R.id.id_layout_0).setVisibility(View.VISIBLE);
                    int duration = Integer.valueOf(item.getDuration());
                    helper.setText(R.id.id_title_1, item.getPrice() + "/耗时" + DateUtils.getTime4Minutes2(duration));
                    HandyLinearLayout handyLinearLayout = helper.getView(R.id.id_layout_0);
                    for (int i = 0; i < handyLinearLayout.getChildCount(); i ++){
                        CheckBox cb = (CheckBox) handyLinearLayout.getChildAt(i);
                        cb.setChecked(false);
                        cb.setTextColor(Color.parseColor("#ff9fa7b0"));
                    }
                    String weeks = item.getWeeks();
                    if (!TextUtils.isNull(weeks)){
                        String[] week = weeks.split(",");
                        for (String d : week){
                            d = d.trim();
                            if (!TextUtils.isNull(d)){
                                int index = Integer.parseInt(d);
//                                if (index == 0){
//                                    index = 6;
//                                }else{
//                                    index = index - 1;
//                                }
                                CheckBox cb = (CheckBox) handyLinearLayout.getChildAt(index);
                                cb.setChecked(true);
                                cb.setTextColor(Color.parseColor("#ffffffff"));
                            }
                        }
                    }
                }else{// 课程
                    helper.getView(R.id.id_title_1).setVisibility(View.GONE);
                    helper.getView(R.id.id_title_2).setVisibility(View.VISIBLE);
                    helper.getView(R.id.id_layout_0).setVisibility(View.GONE);
                    int stime = Integer.valueOf(item.getStarttime());
                    int etime = Integer.valueOf(item.getEndtime());
                    helper.setText(R.id.id_title_2, DateUtils.getTime4Minutes(stime)+" ~ "+DateUtils.getTime4Minutes(etime));
                }

//                CircleImageView civ = helper.getView(R.id.id_imageview);
//                imageLoader.displayImage(item.getHeadpic(),civ,options);
//                helper.setText(R.id.id_textview,item.getNickname());
            }
        };
        xListView.setAdapter(adapter);
        getData();
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0,2000);
            }
            @Override
            public void onLoadMore() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0,2000);
            }
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("------", "选中的项是：" + position);
            }
        });
    }
    private int getPositionLetter(String letter){
        int count = 0;
        for (Serve s : list){
            if (s.getService_type().equals(letter)){
                return count;
            }
            count ++;
        }
        return -1;
    }
    //请求的接口。。。。。。
    private void getData(){
        map.clear();
        map.put("team_id", team_id + "");
        map.put("team_uid", teamMemberMy.getUid() + "");
        showLoading(parentView);
        postData2(map, FunncoUrls.getServiceListUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
        if (paramsJSONObject != null){
            JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
            if (listJSONArray != null){
                List<Serve> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Serve.class);
                if (ls != null && ls.size() > 0){
                    list.clear();
                    list.addAll(ls);
                    sortData();
                }
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
    }

    private void sortData(){
        if (list != null && list.size() > 0){
            Collections.sort(list, new Comparator<Serve>() {
                @Override
                public int compare(Serve lhs, Serve rhs) {
                    return lhs.getService_type().compareTo(rhs.getService_type());
                }
            });
        }
    }

    private String getIds(){
        String ids = "";
        if (list != null){
            for (Serve s : list){
                if (s == null){
                    continue;
                }
                String name = s.getService_name();
                if (!TextUtils.isNull(name)){
                    ids += name+"===";
                }
            }
        }
        if (ids.length() != 0){
            ids = ids.substring(0,ids.length() - 3);
        }
        return ids;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.llayout_foot://修改项目
                startActivityForResult(new Intent().setClass(mContext, ServiceDistributeServiceChooseActivity.class)
                        .putExtra("team_id", team_id+"")
                        .putExtra("team_uid",teamMemberMy.getUid()+"")
                        .putExtra("names", getIds())
                        .putExtra(KEY,"teamMemberMy"),REQUEST_CODE_SERVICE_CHOOSE);
                BaseApplication.getInstance().setT("teamMemberMy", teamMemberMy);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SERVICE_CHOOSE && resultCode == RESULT_CODE_SERVICE_CHOOSE){
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
