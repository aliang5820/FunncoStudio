package com.funnco.funnco.activity;

//标题栏控件

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.fragment.MyCustomerFragment;
import com.funnco.funnco.fragment.NewConventionFragment;
import com.funnco.funnco.task.MyLoginAsynchTask;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.TabUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的预约 未来&历史
 * Created by user on 2015/5/18.
 *
 * @author Shawn
 */
public class MyConventionActivity extends BaseActivity {

    //标题栏控件
    private Intent intent = null;
    private String title;
    private List<MyCustomerD> list = new ArrayList<>();
    private MyLoginAsynchTask task = null;
    private View parentView;
    private RadioGroup rg;
    //滚动的line
    private ImageView ivLine;
    int currIndex = 0;// 当前页卡编号
    int bmpW;// 图片宽度
    int offset;// 动画图片偏移量
    private TabUtils tabUtils;
    private List<Fragment> fragmentList = new ArrayList<>();
    private MyCustomerFragment historyFragment;
    private NewConventionFragment futureFragment;
    //
//    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_myconvention, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                title = bundle.getString("info");
            }
        }
        if (!TextUtils.isEmpty(title)) {
            ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(title);
        } else {
            ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_myconventation);
        }

        ivLine = (ImageView) findViewById(R.id.iv_mycustomer_sortbydate_line);
        bmpW = mScreenWidth / 2;
        offset = bmpW;//此处特殊处理
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        ivLine.setImageMatrix(matrix);// 设置动画初始位置

        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        futureFragment = new NewConventionFragment(imageLoader,options);
        fragmentList.add(futureFragment);
        historyFragment = new MyCustomerFragment(imageLoader,options);
        fragmentList.add(historyFragment);

        rg = (RadioGroup) findViewById(R.id.rg_mycustomer_sortbydate_rgmenu);
        tabUtils = new TabUtils(getSupportFragmentManager(), fragmentList, R.id.llayout_container, rg, R.color.color_main_bottom_rb_checked, R.color.color_main_bottom_rb_unchecked, mContext);
    }

    @Override
    protected void initEvents() {
        tabUtils.setOnRgsExtraCheckedChangedListener(new TabUtils.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(FragmentTransaction fragmentTransaction, RadioGroup radioGroup, int checkedId, int index) {
                LogUtils.e("MyCustomersSortByDateActivity中绑定的OnRgsExtraCheckedChanged监听事件：" + radioGroup, ",index:" + index + ",checkedId:" + checkedId);
                scroll(index);
            }
        });
    }

    private void scroll(final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation animation = new TranslateAnimation(currIndex * bmpW, bmpW
                        * index, 0, 0);
                currIndex = index;
                animation.setFillAfter(true);// True:图片停在动画结束位置
                animation.setDuration(100);
                ivLine.startAnimation(animation);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl://标题栏的返回按钮
                finishOk();
                break;
        }
    }
}
