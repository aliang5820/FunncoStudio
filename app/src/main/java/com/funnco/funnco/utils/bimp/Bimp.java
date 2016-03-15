package com.funnco.funnco.utils.bimp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.funnco.funnco.bean.ImageItem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 2015/5/27.
 * @author Shawn
 * @QQ 1206816341
 */
public class Bimp {
    public static int max = 0;
    //选择的图片的临时列表
    public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>();

    //选择的相册的临时列表
    public static ArrayList<ImageItem> tempCheckedBucketBitmap = new ArrayList<ImageItem>();

    /**
     * 对图片进行压缩
     * @param path
     * @return
     * @throws IOException
     */
    public static Bitmap revitionImageSize(String path) throws IOException {
//        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
//                new File(path)));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);//修改
//        BitmapFactory.decodeStream(in, null, options);
//        in.close();
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
//                in = new BufferedInputStream(
//                        new FileInputStream(new File(path)));
                //图片压缩比
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
//                bitmap = BitmapFactory.decodeStream(in, null, options);
                bitmap = BitmapFactory.decodeFile(path,options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }
}
