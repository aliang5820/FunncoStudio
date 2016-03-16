package com.funnco.funnco.activity.login;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.FragmentAdapter;
import com.funnco.funnco.fragment.guide.BaseGuideFragment;
import com.funnco.funnco.fragment.guide.GuideFragment_1;
import com.funnco.funnco.fragment.guide.GuideFragment_2;
import com.funnco.funnco.fragment.guide.GuideFragment_3;
import com.funnco.funnco.support.FixedSpeedScroller;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;

import java.lang.reflect.Field;

public class WelcomeActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static boolean sended = false;
    private static boolean isDone = true;//默认没有任务处理

    private static boolean isJumped = false;
    private int currentPagerindex = 0;
    private long sendTime = 0;
    final float PARALLAX_COEFFICIENT = 1.2f;
    final float DISTANCE_COEFFICIENT = 0.5f;

    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private FragmentAdapter fAdapter;

    private SparseArray<int[]> mLayoutViewIdsMap = new SparseArray<>();
    private View[] views = new View[3];
    private int[] queue = new int[]{1, 0, 0};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && sended) {
                isJumped = true;
                startActivity(LoginActivity.class);
                finish();
            } else if (msg.what == 1) {
                if (viewPager != null) {
                    viewPager.setCurrentItem((currentPagerindex + 1) % queue.length);
                    sendTime = System.currentTimeMillis();
                }
            }
            isDone = true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_welcome);
    }

    private void addGuideFragment(BaseGuideFragment fragment) {
        fAdapter.addItem(fragment);
        mLayoutViewIdsMap.put(fragment.getRootViewId(), fragment.getChildViewId());

    }

    @Override
    protected void initView() {
        String device_token = SharedPreferencesUtils.getValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.DEVICE_TOKEN);
        if (TextUtils.isEmpty(device_token)) {
            SharedPreferencesUtils.setValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.DEVICE_TOKEN, device_token);
        }
        views[0] = findViewById(R.id.v_welcom_dot_1);
        views[1] = findViewById(R.id.v_welcom_dot_2);
        views[2] = findViewById(R.id.v_welcom_dot_3);
        views[0].setBackgroundResource(R.drawable.bg_circle_dot_red_select);

        viewPager = (ViewPager) findViewById(R.id.vp_welcom_main);
        fragmentManager = getSupportFragmentManager();
        fAdapter = new FragmentAdapter(fragmentManager);
        addGuideFragment(new GuideFragment_1());
        addGuideFragment(new GuideFragment_2());
        addGuideFragment(new GuideFragment_3());
        viewPager.setAdapter(fAdapter);
        viewPager.setPageTransformer(true, new ParallaxTransformer(PARALLAX_COEFFICIENT, DISTANCE_COEFFICIENT));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isJumped) {
                    try {
                        Thread.sleep(3000);
                        if (System.currentTimeMillis() - sendTime >= 2500) {
                            handler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        controlViewPagerSpeed();
    }

    FixedSpeedScroller mScroller = null;

    /**
     * 利用反射机制修改ViewPager 的默认滑动效果
     */
    private void controlViewPagerSpeed() {
        try {
            Field mField;

            mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new FixedSpeedScroller(
                    viewPager.getContext(),
                    new AccelerateInterpolator());
            mScroller.setmDuration(250); // 2000ms
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initEvents() {
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);
        handler.removeMessages(1);
    }

    @Override
    public void onClick(View v) {
    }

    public void enterApp() {
        isJumped = true;
        startActivity(LoginActivity.class);
        finishOk();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPagerindex = position;
        sendTime = System.currentTimeMillis();
        if (queue[position % 3] <= 0 || queue[(position + 1) % 3] <= 0 || queue[(position + 2) % 3] <= 0) {
            findViewById(R.id.bt_welcome_start).setVisibility(View.INVISIBLE);
            queue[position] = (position + 1);
        } else {
            findViewById(R.id.bt_welcome_start).setVisibility(View.VISIBLE);
        }
        if (position == queue.length - 1) {
            findViewById(R.id.bt_welcome_start).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < views.length; i++) {
            if (i == position) {
                views[i].setBackgroundResource(R.drawable.bg_circle_dot_red_select);
            } else {
                views[i].setBackgroundResource(R.drawable.bg_circle_dot_red);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    class ParallaxTransformer implements ViewPager.PageTransformer {

        float parallaxCoefficient;
        float distanceCoefficient;

        public ParallaxTransformer(float parallaxCoefficient, float distanceCoefficient) {
            this.parallaxCoefficient = parallaxCoefficient;
            this.distanceCoefficient = distanceCoefficient;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void transformPage(View page, float position) {
            float scrollXOffset = page.getWidth() * parallaxCoefficient;

            ViewGroup pageViewWrapper = (ViewGroup) page;
            @SuppressWarnings("SuspiciousMethodCalls")
            int[] layer = mLayoutViewIdsMap.get(pageViewWrapper.getChildAt(0).getId());
            if (layer != null) {
                LogUtils.e(TAG, "layer  不为空" + layer.length);
                for (int id : layer) {
                    View view = page.findViewById(id);
                    if (view != null) {
                        view.setTranslationX(scrollXOffset * position);
                    }
                    scrollXOffset *= distanceCoefficient;
                }
            } else {
                LogUtils.e(TAG, "layer  为空null");
            }
        }
    }
}
