package com.funnco.funnco.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by user on 2015/8/26.
 */
public class HandyLinearLayout extends LinearLayout {
    public HandyLinearLayout(Context context) {
        super(context);
    }

    public HandyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HandyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
