package com.funnco.funnco.view.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by user on 2015/8/26.
 */
public class MyListView2 extends ListView {
    public MyListView2(Context context) {
        super(context);
    }

    public MyListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
