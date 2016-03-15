package com.funnco.funnco.utils.bimp;

import android.content.Context;
import android.graphics.*;

import com.funnco.funnco.application.BaseApplication;

/**
 * 该类主要实现三个功能：
 * 1，返回一个圆形的图片
 * 2，对图片进行模糊处理
 * 3，对图片透明度进行处理
 * 4，对图片进行压缩
 */
public class BitmapUtils {


    /**
     * 根据指定图片的大小  来 画圆形图
     * @param originBitmap
     * @param resourcerId
     * @return
     */
    public static Bitmap getCircleBitmap2(Bitmap originBitmap, int resourcerId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(),resourcerId,options);
        if (options.outHeight > options.outWidth) {
            return getCircleBitmap(originBitmap, options.outWidth, options.outWidth);
        }else{
            return getCircleBitmap(originBitmap, options.outHeight, options.outHeight);
        }
    }
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

    public static Bitmap getCircleBitmap(Bitmap originBitmap, int targetWidth, int targetHeight) {
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(targetBitmap);
        localCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
        //计算原图的高宽
        int originWidth = originBitmap.getWidth(), originHeight = originBitmap.getHeight();

        Matrix matrix = new Matrix();
        float scale = 1;
        int tran = 0;
//        if (originWidth / originHeight > targetWidth / targetHeight) {
        if (originWidth > originHeight) {
            scale = targetWidth / (float) originWidth;
            matrix.postScale(scale, scale);
            tran = (int) ((targetHeight - originHeight * scale) * 0.5);
            matrix.postTranslate(0, tran);
        } else {
            scale = targetHeight / (float) originHeight;
            matrix.postScale(scale, scale);
            tran = (int) ((targetWidth - originWidth * scale) * 0.5);
            matrix.postTranslate(tran, 0);
        }
        localCanvas.drawBitmap(originBitmap, matrix, null);


        Paint localPaint = new Paint();
        localPaint.setFilterBitmap(false);
        Xfermode localXfermode1 = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        localPaint.setXfermode(localXfermode1);

        Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
        Bitmap maskBitmap = Bitmap.createBitmap(targetWidth, targetHeight, localConfig);
        Canvas maskCanvas = new Canvas(maskBitmap);
        Paint maskPaint = new Paint(1);
        maskPaint.setColor(-16777216);

        RectF maskRectF = new RectF(0.0F, 0.0F, targetWidth, targetHeight);
        maskCanvas.drawOval(maskRectF, maskPaint);
        localCanvas.drawBitmap(maskBitmap, 0.0F, 0.0F, localPaint);

        return targetBitmap;


    }

    /******
     * 给bitmap添加模糊效果
     *
     * @param context
     * @param sentBitmap  源bitmap
     * @param radius  模糊半径 0-25
     * @return Bitmap
     */
    public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {
        try {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            if (radius < 1) {
                return (null);
            }
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;
            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];
            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }
            yw = yi = 0;
            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;
            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;
                for (x = 0; x < w; x++) {
                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];
                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;
                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];
                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];
                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;
                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];
                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;
                    sir = stack[i + radius];
                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];
                    rbs = r1 - Math.abs(i);
                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                            | (dv[gsum] << 8) | dv[bsum];
                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;
                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];
                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];
                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];
                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;
                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];
                    yi += w;
                }
            }
            r = null;
            g = null;
            b = null;
            sir = null;
            dv = null;
            stack = null;
            vmin = null;
            bitmap.setPixels(pix, 0, w, 0, 0, w, h);
            pix = null;
            System.gc();
            return (bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;

    }

    /****
     * 给bitmap添加透明度
     * @param sourceImg 源bitmap
     * @param number  透明度 0-100(0表示完全透明，100表示不透明)
     * @return
     */
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        try {
            int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
            sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
                    sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
            number = number * 255 / 100;
            for (int i = 0; i < argb.length; i++) {
                argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
            }
            sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
                    sourceImg.getHeight(), Bitmap.Config.ARGB_8888);
            argb = null;
            System.gc();
            return sourceImg;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对图片进行压缩
     * @param context 上下文
     * @param resId 资源ID
     * @param reqWidth 适应的容器宽度
     * @param reqHeight 适应的容器高度
     * @return
     */
    public static Bitmap decodeBitmapResource(Context context, int resId, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),resId,options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        options.inPurgeable = true;
        options.inInputShareable = false;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(),resId,options);
    }
    /**
     * 对图片进行压缩
     * @param data
     * @param reqWidth 适应的容器宽度
     * @param reqHeight 适应的容器高度
     * @return
     */
    public static Bitmap decodeBitmap(byte[] data, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data,0,data.length,options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        options.inPurgeable = true;
        options.inInputShareable = false;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length,options);
    }

    /**
     * 计算压缩比
     * @param options
     * @param reqWidth 适应的容器宽度
     * @param reqHeight 适应的容器高度
     * @return 压缩比
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height > reqHeight || width > reqWidth){
            if (width > height){
                inSampleSize = Math.round((float)height/(float)reqHeight);
            }else{
                inSampleSize = Math.round((float)width/(float)reqWidth);
            }
        }
        return inSampleSize;
    }
}
