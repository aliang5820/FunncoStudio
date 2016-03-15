package com.funnco.funnco.fragment.notification;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.funnco.funnco.R;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.NotificationRemind;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.view.listview.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知 - 提醒
 * Created by user on 2015/10/21.
 */
public class NotificationRemindFragment extends BaseFragment {

    private XListView xListView;
    private CommonAdapter<NotificationRemind> adapter;
    private List<NotificationRemind> list = new ArrayList<>();
    private UserLoginInfo user;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        xListView = new XListView(mContext);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        xListView.setDividerHeight(0);
        initViews();
        initEvents();
        return xListView;
    }

    @Override
    protected void initViews() {
        user = BaseApplication.getInstance().getUser();
        initAdapter();
    }

    private void initAdapter() {
        adapter = new CommonAdapter<NotificationRemind>(mContext, list, R.layout.layout_item_notification_remind) {
            @Override
            public void convert(ViewHolder helper, NotificationRemind item, int position) {
                helper.setText(R.id.id_title_0, item.getTime());
                helper.setText(R.id.id_title_1, item.getTitle());
            }
        };
        xListView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0, 3000);
                getData();
            }

            @Override
            public void onLoadMore() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0, 3000);
                getData();
            }
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void init() {

    }
    private void getData(){
        if (user != null && NetUtils.isConnection(mContext)){
            getData4Net();
        } else{
            getData4Db();
        }
    }

    private void getData4Db() {
        if (dbUtils != null){
            List<NotificationRemind> ls = SQliteAsynchTask.selectTall(dbUtils, NotificationRemind.class, null, null, null);
            if (ls != null && ls.size() > 0){
                list.clear();
                list.addAll(ls);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void getData4Net() {
        Map<String, Object> map = new HashMap<>();
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0){

                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        },false,""));
    }

    @Override
    public void onMainAction(String data) {}

    @Override
    public void onMainData(List<?>... list) {}
}
