package com.funnco.funnco.view.textview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * 未读信息小圆点
 * Created by user on 2015/7/5.
 */
public class DesignTextView extends View {
    //默认字体大小
    private static int DEFAULT_SIZE_TEXT = 12;
    //默认字体颜色
    private static int DEFAULT_COLOR_TEXT = Color.WHITE;
    //默认背景色
    private static int DEFAULT_COLOR_BG = Color.TRANSPARENT;
    //默认圆点背景色
    private static int DEFAULT_COLOR_BG_CIRCLE = Color.RED;
    //sharepreference文件名/字段名
//    protected static String SHAREDPREFERENCE_CONFIG = "config";
//    protected static String NEWSCHEDULECOUNT = "newScheduleCount";//未读消息数

    private Context context;

    Paint paint;
    TextPaint textPaint;
    Paint.FontMetrics textFontMetrics;
    int w,h,R;
    //未读数
    private int count = 0;
    private int textWidth;
    private int textHeight;
    public DesignTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }
    public DesignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }
    private void init(){
        // 设置绘制flag的paint
        paint = new Paint();
        paint.setColor(DEFAULT_COLOR_BG_CIRCLE);
        paint.setAntiAlias(true);

        // 设置绘制文字的paint
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(DEFAULT_COLOR_TEXT);
        textPaint.setTextSize(dipToPixels(DEFAULT_SIZE_TEXT));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textFontMetrics = textPaint.getFontMetrics();

        String textCount = String.valueOf(count);
        Rect bounds = new Rect();
        textPaint.getTextBounds(textCount, 0, textCount.length(), bounds);
        textWidth = bounds.width();
        textHeight = bounds.height();
    }

    public void setText(int count){
        this.count = count;
        if (count <= 0){
            this.setVisibility(INVISIBLE);
        }else {
            this.setVisibility(VISIBLE);
        }
//        SharedPreferencesUtils.setValue(context, SHAREDPREFERENCE_CONFIG, NEWSCHEDULECOUNT, count + "");
        this.postInvalidate();
    }

    public void setTextSize(int textSize){
        textPaint.setTextSize(dipToPixels(textSize));
        this.postInvalidate();
    }
    /**
     * 设置显示隐藏
     * @param visible
     */
    public void setVisible(boolean visible){
        if (!visible) {
            count = 0;
            //同时修改本地数据
//            SharedPreferencesUtils.setVal/ue(context,SHAREDPREFERENCE_CONFIG,NEWSCHEDULECOUNT,count+"");
        }
        this.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景透明
        canvas.drawColor(DEFAULT_COLOR_BG);
        ViewGroup.LayoutParams params = this.getLayoutParams();
        w = params.width;
        h = params.height;
        //半径
        R = w > h ? h/2 : w/2;
        canvas.drawCircle(w / 2, h / 2, R, paint);
        if (count > 99){
            canvas.drawText("…", w / 2, h / 2 + textHeight / 2, textPaint);
        }else {
            canvas.drawText(count + "", w / 2, h / 2 + textHeight / 2, textPaint);
        }
        if (count <= 0){
            this.setVisibility(View.GONE);
        }
    }

    /**
     * dip 转换成像素点
     * @param dip
     * @return
     */
    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                r.getDisplayMetrics());
        return (int) px;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //单击了就进行隐藏
////        setVisible(false);
////        return super.onTouchEvent(event);
//        return true;
//    }
}
