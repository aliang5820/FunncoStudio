package com.funnco.funnco.activity.myinfo;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.view.CircleProgressView;

/**
 * 账号升级
 * Created by user on 2015/11/10.
 */
public class UpdateAccountActivity extends BaseActivity {

    private View parentView;
    private CircleProgressView vipTimeView;
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_update_account, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_money);
        vipTimeView = (CircleProgressView) findViewById(R.id.vip_last_time_progress);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        vipTimeView.setMaxCount(100);
        vipTimeView.setCurrentCount(70);
        vipTimeView.setScore(80);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }
}
