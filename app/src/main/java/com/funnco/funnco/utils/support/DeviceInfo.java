package com.funnco.funnco.utils.support;

import android.app.Activity;
import android.util.DisplayMetrics;

public class DeviceInfo {

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(mDisplayMetrics);
        int width = mDisplayMetrics.widthPixels;
        return width;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(mDisplayMetrics);
        int height = mDisplayMetrics.heightPixels;
        return height;
    }
}
