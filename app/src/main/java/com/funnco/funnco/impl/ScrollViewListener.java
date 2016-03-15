package com.funnco.funnco.impl;

import com.funnco.funnco.view.scrollview.HandyScrollView;

/**
 * Created by user on 2015/8/25.
 */
public interface ScrollViewListener {

    void onScrollChanged(HandyScrollView scrollView, int x, int y, int oldx, int oldy);

}
