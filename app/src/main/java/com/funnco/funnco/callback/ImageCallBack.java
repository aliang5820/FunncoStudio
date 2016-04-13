package com.funnco.funnco.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by user on 2015/5/27.
 */
public interface ImageCallBack {
    public void imageLoad(ImageView imageView, Bitmap bitmap,
                          Object... params);
}
