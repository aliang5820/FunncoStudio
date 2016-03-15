package com.wq.photo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by user on 2015/7/22.
 */
public class BitmapUtils {
    public static Bitmap getCompressBitmap(String imagePath,int newWidth,int newHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int oldHeight = options.outHeight;
        int oldWidth = options.outWidth;
        int ratioHeight = oldHeight / newHeight;
        int ratioWidth = oldWidth / newWidth;
        options.inSampleSize = ratioHeight > ratioWidth ? ratioHeight : ratioWidth;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
        return bm;
    }
}
