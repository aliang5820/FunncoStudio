package com.funnco.funnco.view.circleview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by EdisonZhao on 16/3/24.
 */
public class BaseCircleView extends View {
    //分段颜色
    protected static final int[] SECTION_COLORS = {0xff1d66bd, 0xff42cbb3, 0xff39acdd};
    protected Context mContext;
    private float maxCount;
    private float currentCount;
    protected Paint mPaint;
    protected Paint mBgPaint;
    protected Paint mBotomTextPaint;
    protected Paint mTopTextPaint;
    protected int mWidth, mHeight;

    public BaseCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BaseCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseCircleView(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mBgPaint = new Paint();
        mTopTextPaint = new Paint();
        mBotomTextPaint = new Paint();
    }

    protected void initPaint() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth((float) 15.0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.TRANSPARENT);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth((float) 5.0);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setColor(0xffC7D0D8);
        mTopTextPaint.setAntiAlias(true);
        mTopTextPaint.setStrokeWidth((float) 3.0);
        mTopTextPaint.setTextAlign(Paint.Align.CENTER);
        mTopTextPaint.setTextSize(25);
        mTopTextPaint.setColor(Color.GRAY);
        mBotomTextPaint.setAntiAlias(true);
        mBotomTextPaint.setStrokeWidth((float) 3.0);
        mBotomTextPaint.setTextAlign(Paint.Align.CENTER);
        mBotomTextPaint.setTextSize(50);
        mBotomTextPaint.setColor(Color.BLACK);
    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public float getMaxCount() {
        return maxCount;
    }

    public float getCurrentCount() {
        return currentCount;
    }

    /***
     * 设置最大的进度值
     *
     * @param maxCount
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /***
     * 设置当前的进度值
     *
     * @param currentCount
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == View.MeasureSpec.EXACTLY
                || widthSpecMode == View.MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == View.MeasureSpec.AT_MOST
                || heightSpecMode == View.MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(15);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    //获取文字宽度
    protected int getStringWidth(Paint paint, String str) {
        return (int) paint.measureText(str);
    }

    //获取文字高度
    protected int getStringHeight(Paint paint) {
        Paint.FontMetrics fr = paint.getFontMetrics();
        return (int) Math.ceil(fr.descent - fr.top) + 2;  //ceil() 函数向上舍入为最接近的整数。
    }
}
