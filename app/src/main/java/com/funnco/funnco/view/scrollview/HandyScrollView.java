package com.funnco.funnco.view.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.funnco.funnco.impl.ScrollViewListener;

/**
 * Created by user on 2015/8/25.
 */
public class HandyScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener = null;

    public HandyScrollView(Context context) {
        super(context);
    }

    public HandyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HandyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null){
            scrollViewListener.onScrollChanged(this,l,t,oldl,oldt);
        }
    }
}
