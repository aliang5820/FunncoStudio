package com.funnco.funnco.activity.notification;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.bean.TeamInvite;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EdisonZhao on 16/4/19.
 */
public abstract class NotificationBaseActivity extends BaseActivity {
    public View parentView;
    public XListView xListView;
    public CommonAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (xListView != null) {
                    xListView.stopLoadMore();
                    xListView.stopRefresh();
                }
            }
        }
    };

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_layout_notification_list, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        findViewById(R.id.id_rview).setVisibility(View.GONE);
        findViewById(R.id.id_lview).setOnClickListener(this);
        xListView = (XListView) findViewById(R.id.id_listView);
        xListView.setDividerHeight(0);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(true);
        xListView.setFooterVisibleState(false);
    }

    @Override
    protected void initEvents() {
        if (xListView != null) {
            xListView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    clearAsyncTask();
                    getData();
                    handler.sendEmptyMessageDelayed(0, 2000);
                }

                @Override
                public void onLoadMore() {
                }
            });
        }

        initAdapter();
        getData();
    }

    @Override
    protected void dataPostBack(String result, String url) {
        dismissLoading();
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        if (xListView != null) {
            xListView.stopLoadMore();
            xListView.stopRefresh();
        }
        dismissLoading();
    }

    //获取数据
    protected abstract void getData();
    protected abstract void initAdapter();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_lview:
                finishOk();
                break;
        }
    }
}
