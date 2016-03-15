package com.funnco.funnco.activity.myinfo;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;

/**
 * 收入
 * Created by user on 2015/10/28.
 */
public class IncomeWeekActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private ImageView iv;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        container = (FrameLayout) findViewById(R.id.layout_container);
        iv = new ImageView(mContext);
        iv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(R.mipmap.income);
        container.removeAllViews();
        container.addView(iv);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onClick(View v) {

    }
}
