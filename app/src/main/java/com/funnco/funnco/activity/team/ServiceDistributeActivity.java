package com.funnco.funnco.activity.team;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.bean.TeamMember;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务分配
 * Created by user on 2015/9/29.
 */
public class ServiceDistributeActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private XListView xListView;
    private CommonAdapter<TeamMember> adapter;
    private List<TeamMember> list = new ArrayList<>();

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.id_mview)).setText(R.string.str_service_distribute);
        container = (FrameLayout) findViewById(R.id.layout_container);
        xListView = new XListView(mContext);
        xListView.setFooterVisibleState(false);
        xListView.setHeaderVisibleState(false);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setDividerHeight(1);
        container.addView(xListView);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);
        adapter = new CommonAdapter<TeamMember>(mContext,list,R.layout.layout_item_member_2) {
            @Override
            public void convert(ViewHolder helper, TeamMember item, int position) {
                TextView tip = helper.getView(R.id.tip);
                if (position == 1){
                    tip.setVisibility(View.VISIBLE);
                    tip.setText(R.string.str_team_member_choose);
                }else{
                    tip.setVisibility(View.GONE);
                }
                CircleImageView civ = helper.getView(R.id.id_imageview);
                imageLoader.displayImage(item.getHeadpic(),civ,options);
                helper.setText(R.id.id_textview,item.getNickname());
            }
        };
        xListView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.id_lview).setOnClickListener(this);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

            }
            @Override
            public void onLoadMore() {}
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("------", "选中的项是：" + position);
            }
        });
    }
    //请求的接口是否需要上传相关参数来区分不同的服务对应的团队分组
    private void getData(){
        Map<String,Object> map = new HashMap<>();
        AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {

            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        },false, FunncoUrls.getTeamMemberUrl());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_lview:
                finishOk();
                break;
        }
    }
}
