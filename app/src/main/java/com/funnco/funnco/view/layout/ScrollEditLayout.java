package com.funnco.funnco.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 该类用于将时间交由子控件处理而本身不处理
 * Created by user on 2015/6/18.
 * @author Shawn
 */
public class ScrollEditLayout extends ScrollView {

    public ScrollEditLayout(Context context) {
        super(context);
    }

    public ScrollEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
