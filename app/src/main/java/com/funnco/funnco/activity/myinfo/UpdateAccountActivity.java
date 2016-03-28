package com.funnco.funnco.activity.myinfo;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.view.circleview.BaseCircleView;
import com.funnco.funnco.view.circleview.CircleProgressRightView;

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
        //左边圆
        vipLeftView.setMaxCount(1000);
        vipLeftView.setCurrentCount(400);
        vipLeftView.setScore((int) (vipLeftView.getMaxCount() - vipLeftView.getCurrentCount()));
        //右边圆
        vipRightView.setMaxCount(1000);
        vipRightView.setCurrentCount(600);
        vipRightView.setScore((int) (vipRightView.getMaxCount() - vipRightView.getCurrentCount()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }

    public void onQJCharge(View view) {
        //旗舰版
        Intent intent = new Intent(this, UpdateAccountActivity_2.class);
        intent.putExtra(Constants.ORDER_TYPE, Constants.ORDER_TYPE_QJ);
        startActivity(intent);
    }

    public void onSWCharge(View view) {
        //商务版
        Intent intent = new Intent(this, UpdateAccountActivity_2.class);
        intent.putExtra(Constants.ORDER_TYPE, Constants.ORDER_TYPE_SW);
        startActivity(intent);
    }

    public void onGLCharge(View view) {
        //人数管理版
        Intent intent = new Intent(this, UpdateAccountActivity_2.class);
        intent.putExtra(Constants.ORDER_TYPE, Constants.ORDER_TYPE_GL);
        startActivity(intent);
    }
}
