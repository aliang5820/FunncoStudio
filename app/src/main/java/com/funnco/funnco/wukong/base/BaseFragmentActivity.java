package com.funnco.funnco.wukong.base;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.funnco.funnco.R;


/**
 * Created by zengchan.lzc on 2014/12/29.
 */
public class BaseFragmentActivity extends FragmentActivity {

    /**
     * init ActionBar property
     */
    protected void initActionBar(String title) {
        ActionBar ab = getActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.common_navigator));
            ab.setDisplayShowHomeEnabled(false);
            if (!TextUtils.isEmpty(title)) {
                ab.setTitle(title);
            }
        }
    }

    /**
     * 在高版本上一体化系统状态拦颜色
     */
    protected void initSystemStatusBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(this, true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.common_navigator_alpha60));
//        }
    }

    @TargetApi(19)
    protected void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
