package com.funnco.funnco.activity.myinfo;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.MainActivity;

/**
 * 账号升级
 * Created by user on 2015/11/10.
 */
public class UpdateAccountActivity_3 extends BaseActivity {

//    private View parentView;
//    private FrameLayout container;
    private ImageView iv;
    @Override
    protected void loadLayout() {
        super.loadLayout();
        iv = new ImageView(this);
        iv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(R.mipmap.common_vip_3);
        setContentView(iv);
    }

    @Override
    protected void initView() {
//        container = (FrameLayout) findViewById(R.id.layout_container);


    }

    @Override
    protected void initEvents() {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.class);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
