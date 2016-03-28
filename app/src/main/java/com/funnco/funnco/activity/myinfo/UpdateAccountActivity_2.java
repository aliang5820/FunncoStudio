package com.funnco.funnco.activity.myinfo;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.fragment.desk.service.ServiceFragment_Course;
import com.funnco.funnco.fragment.desk.service.ServiceFragment_Service;
import com.funnco.funnco.fragment.pay.OrderDescBaseFragment;
import com.funnco.funnco.fragment.pay.OrderDescGLFragment;
import com.funnco.funnco.fragment.pay.OrderDescQJFragment;
import com.funnco.funnco.fragment.pay.OrderDescSWFragment;
import com.funnco.funnco.support.FragmentTabHost;
import com.funnco.funnco.utils.support.Constant;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账号升级(购买/了解)
 * Created by user on 2015/11/10.
 */
public class UpdateAccountActivity_2 extends BaseActivity {

    private View parentView;
    //滚动的line
    private LinearLayout lScrollLayout;
    private ImageView ivLine;
    int currIndex = 0;// 当前页卡编号
    int bmpW;// 图片宽度
    int offset;// 动画图片偏移量
    private FragmentTabHost tbh;
    private final Class<?>[] classes = {OrderDescSWFragment.class, OrderDescQJFragment.class, OrderDescGLFragment.class};
    private final String[] tag = {"sw", "qj", "gl"};
    private final int[] menu_layout = {R.layout.layout_table_order_sw, R.layout.layout_table_order_qj, R.layout.layout_table_order_gl};
    private Map<String, BaseFragment> map = new HashMap<>();
    private FragmentManager fm;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (fm != null) {
                    List<Fragment> ls = fm.getFragments();
                    if (ls != null) {
                        for (Fragment fb : ls) {
                            if (fb instanceof ServiceFragment_Course) {
                                map.put(tag[0], (BaseFragment) fb);
                            } else if (fb instanceof ServiceFragment_Service) {
                                map.put(tag[1], (BaseFragment) fb);
                            } else if (fb instanceof ServiceFragment_Service) {
                                map.put(tag[2], (BaseFragment) fb);
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_update_account2, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        init();
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_money_more);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tbh = (FragmentTabHost) parentView.findViewById(android.R.id.tabhost);

        ivLine = (ImageView) parentView.findViewById(R.id.iv_scroll_line);
        bmpW = FunncoUtils.getScreenWidth(mContext) / 3;
        offset = bmpW;//此处特殊处理
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        ivLine.setImageMatrix(matrix);// 设置动画初始位置

        fm = getSupportFragmentManager();
        tbh.setup(mContext, fm, R.id.realtabcontent);
        for (int i = 0; i < classes.length; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tab = tbh.newTabSpec(tag[i]).setIndicator(getTabView(i));
            // 将对应Fragment添加到TabHost中
            BaseFragment bf = (BaseFragment) tbh.addTab(tab, classes[i], null);
            //设置Fragment的背景
            if (bf != null) {
                map.put(tag[i], bf);
            }
            bf = (BaseFragment) fm.findFragmentByTag(tag[i]);
            if (bf != null) {
                map.put(tag[i], bf);
            }
        }
        tbh.getTabWidget().setDividerDrawable(null);
        lScrollLayout = (LinearLayout) parentView.findViewById(R.id.id_scrollview);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lScrollLayout.getLayoutParams();
        params.setMargins(0, FunncoUtils.getViewMesureSpec(tbh.getTabWidget().getChildAt(0))[1], 0, 0);
        lScrollLayout.setLayoutParams(params);
    }


    @Override
    protected void initEvents() {
        tbh.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll(0);
                tbh.setCurrentTab(0);
            }
        });
        tbh.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll(1);
                tbh.setCurrentTab(1);
            }
        });
        tbh.getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll(2);
                tbh.setCurrentTab(2);
            }
        });

        Intent intent = getIntent();
        switch (intent.getIntExtra(Constants.ORDER_TYPE, -1)) {
            case Constants.ORDER_TYPE_SW:
                scroll(0);
                tbh.setCurrentTab(0);
                break;
            case Constants.ORDER_TYPE_QJ:
                scroll(1);
                tbh.setCurrentTab(1);
                break;
            case Constants.ORDER_TYPE_GL:
                scroll(2);
                tbh.setCurrentTab(2);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (map.size() < 3) {
                    try {
                        Thread.sleep(1500);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private View getTabView(int position) {
        return LayoutInflater.from(mContext).inflate(menu_layout[position], null);
    }

    private void scroll(final int index) {
        Animation animation = new TranslateAnimation(currIndex * bmpW, bmpW * index, 0, 0);
        currIndex = index;
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(100);
        ivLine.startAnimation(animation);
    }
}
