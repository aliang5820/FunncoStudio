package com.funnco.funnco.activity.myinfo;

import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.view.circleview.BaseCircleView;
import com.funnco.funnco.view.circleview.CircleProgressRightView;
import com.funnco.funnco.view.circleview.CircleProgressView;

/**
 * 账号升级
 * Created by user on 2015/11/10.
 */
public class UpdateAccountActivity extends BaseActivity {

    private View parentView;
    private BaseCircleView vipTimeView;
    private BaseCircleView vipLeftView;
    private CircleProgressRightView vipRightView;
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_update_account, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_money);
        vipTimeView = (BaseCircleView) findViewById(R.id.vip_last_time_progress);
        vipLeftView = (BaseCircleView) findViewById(R.id.vip_left_progress);
        vipRightView = (CircleProgressRightView) findViewById(R.id.vip_right_progress);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        vipTimeView.setMaxCount(100);
        vipTimeView.setCurrentCount(80);
        vipTimeView.setScore((int)(vipTimeView.getMaxCount() - vipTimeView.getCurrentCount()));
        //
        vipLeftView.setMaxCount(1000);
        vipLeftView.setCurrentCount(400);
        vipLeftView.setScore((int)(vipLeftView.getMaxCount() - vipLeftView.getCurrentCount()));
        //
        /*vipRightView.setMaxCount(1000);
        vipRightView.setCurrentCount(600);
        vipRightView.setScore((int)(vipRightView.getMaxCount() - vipRightView.getCurrentCount()));*/
        vipRightView.setMax(1000);
        vipRightView.setProgress(600);
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
