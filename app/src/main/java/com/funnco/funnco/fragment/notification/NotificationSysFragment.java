package com.funnco.funnco.fragment.notification;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.notification.NotificationTeamInviteActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.bean.NotificationSys;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.view.listview.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同时 - 系统
 * Created by user on 2015/9/9.
 */
public class NotificationSysFragment extends BaseFragment implements View.OnClickListener {

    private XListView xListView;
    private CommonAdapter<NotificationSys> adapter;
    private List<NotificationSys> list = new ArrayList<>();
    private int pageIndex = 1;
    private int pageSize = 8;
    private Map<String,Object> map = new HashMap<>();

    private View header;
    private View designDot;
    private static final int REQUEST_CODE_NOTIFICATION_INVITE = 0xf500;
    private static final int RESULT_CODE_NOTIFICATION_INVITE = 0xf501;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        xListView = new XListView(mContext);
        xListView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        xListView.setDividerHeight(0);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);

        header = inflater.inflate(R.layout.layout_item_notification_sys_head,null);
        designDot = header.findViewById(R.id.v_item_notification_sys_head);
        initViews();
        initEvents();
        return xListView;
    }

    @Override
    protected void initViews() {
        xListView.addHeaderView(header);
        adapter = new CommonAdapter<NotificationSys>(mContext, list, R.layout.layout_item_notification_remind) {
            @Override
            public void convert(ViewHolder helper, NotificationSys item, int position) {
                helper.setText(R.id.id_title_0,item.getTime());
                helper.setText(R.id.id_title_1, item.getContent());
            }
        };
        xListView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        if (!NetUtils.isConnection(mContext)){
            getData4Db();
        }else{
            getData4Net();
        }
    }

    private void getData4Db(){
        List<NotificationSys> ls =  SQliteAsynchTask.selectTall(dbUtils, NotificationSys.class,null,null,null);
        if (ls != null && ls.size() >0){
            list.clear();
            list.addAll(ls);
            adapter.notifyDataSetChanged();
        }
    }
    private void getData4Net(){
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0) {
                    //数据返回成功进行解析


                } else {
                    showSimpleMessageDialog(JsonUtils.getResponseMsg(result));
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        }, false, ""));
    }

    @Override
    protected void initEvents() {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAsyncTask();
                startActivityForResult(new Intent()
                .setClass(mContext, NotificationTeamInviteActivity.class),REQUEST_CODE_NOTIFICATION_INVITE);
            }
        });
        if (xListView != null){
            xListView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {

                }

                @Override
                public void onLoadMore() {

                }
            });
            xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LogUtils.e("funnco------",""+position);
                }
            });
        }
    }

    @Override
    protected void init() {

    }

    @Override
    public void onMainAction(String data) {

    }

    @Override
    public void onMainData(List<?>... list) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NOTIFICATION_INVITE && resultCode == RESULT_CODE_NOTIFICATION_INVITE){

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
