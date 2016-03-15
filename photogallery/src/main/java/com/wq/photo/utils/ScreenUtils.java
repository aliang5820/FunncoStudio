package com.wq.photo.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by user on 2015/5/21.
 */
public class ScreenUtils {
    /**
     * 获得屏幕宽度
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
    }

    /**
     * 获得屏幕高度
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context){
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getHeight();
    }

    /**
     * 获取屏幕的密度
     * @param activity
     * @return
     */
    @SuppressWarnings("deprecation")
    public static float getScreenDensity(Activity activity){
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//        mScreenHeight = outMetrics.heightPixels;
//        mScreenWidth = outMetrics.widthPixels;
//        int desity = (int) activity.getResources().getDisplayMetrics().density;
        return outMetrics.density;
    }

    public static int dp2px(Activity activity,float dp){
        float density = getScreenDensity(activity);
        return dp2px(density, dp);
    }

    public static int dp2px(float density,float dp){
        return (int) (dp * density + 0.5f);
    }


}
