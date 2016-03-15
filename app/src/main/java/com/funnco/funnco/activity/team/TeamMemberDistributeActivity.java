package com.funnco.funnco.activity.team;

import android.view.View;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;

/**
 * 成员分配
 * Created by user on 2015/9/30.
 */
public class TeamMemberDistributeActivity extends BaseActivity {
    private View parentView;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
//        ((TextView)findViewById(R.id.id_mview)).setText();
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onClick(View v) {

    }
}
