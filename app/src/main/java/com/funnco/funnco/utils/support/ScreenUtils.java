package com.funnco.funnco.utils.support;

import android.content.Context;
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

}
