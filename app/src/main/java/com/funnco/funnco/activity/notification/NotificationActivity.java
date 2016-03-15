package com.funnco.funnco.activity.notification;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.fragment.notification.NotificationRemindFragment;
import com.funnco.funnco.fragment.notification.NotificationSysFragment;
import com.funnco.funnco.support.FragmentTabHost;

/**
 * 消息通知
 * Created by user on 2015/10/21.
 */
public class NotificationActivity extends BaseActivity {
    private FragmentTabHost fragmentTabHost;
    private FragmentManager fragmentManager;
    private final Class<?>[] classes = {NotificationRemindFragment.class,NotificationSysFragment.class};
    private final String[] tag = {"notification","sys"};
    private final int[] menu_layout = {R.layout.layout_table_notification_remind, R.layout.layout_table_notification_sys};

    private View parentView;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_notification, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_notification);
        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentManager = getSupportFragmentManager();
        fragmentTabHost.setup(mContext, fragmentManager, R.id.realtabcontent);
        for (int i = 0; i < classes.length; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tab = fragmentTabHost.newTabSpec(tag[i]).setIndicator(getTabView(i));
            // 将对应Fragment添加到TabHost中
            fragmentTabHost.addTab(tab, classes[i], null);//bundle 为传递到Fragment中的数据

        }
    }
    private View getTabView(int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        return layoutInflater.inflate(menu_layout[position], null);
    }
    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
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
