package com.funnco.funnco.view.circleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by EdisonZhao on 16/3/24.
 */
public class CircleProgressRightView extends View {
    private int progress;
    private int max;
    private Paint paint;
    private RectF oval;
    private float mRadius;
    private int mThumbRadius = 20;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public CircleProgressRightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        oval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);// 设置是否抗锯齿
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);// 帮助消除锯齿
        paint.setColor(Color.GRAY);// 设置画笔灰色
        paint.setStrokeWidth(10);// 设置画笔宽度
        paint.setStyle(Paint.Style.STROKE);// 设置中空的样式

        canvas.drawCircle(mRadius, mRadius, mRadius, paint);// 在中心为（100,100）的地方画个半径为55的圆，宽度为setStrokeWidth：10，也就是灰色的底边
        paint.setColor(Color.GREEN);// 设置画笔为绿色
        oval.set(mRadius - mRadius, mRadius - mRadius, mRadius + mRadius, mRadius
                + mRadius);// 设置类似于左上角坐标（45,45），右下角坐标（155,155），这样也就保证了半径为55
        canvas.drawArc(oval, 270, -((float) 130 / max) * 360, false,
                paint);// 画圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
        float mThumbPosX = (float) (mRadius + mRadius
                * Math.cos((360 - 130 - 90) * Math.PI / 180));
        float mThumbPosY = (float) (mRadius + mRadius
                * Math.sin((360 - 130 - 90) * Math.PI / 180));

        Log.v("mThumbPosX=====", mThumbPosX + "");
        Log.v("mThumbPosY=====", mThumbPosY + "");
        canvas.drawCircle(mThumbPosX, mThumbPosY, 6, paint);// (Rcosθ+X,Rsinθ+Y)
        paint.reset();// 将画笔重置
        paint.setStrokeWidth(5);// 再次设置画笔的宽度
        paint.setTextSize(35);// 设置文字的大小
        paint.setColor(Color.BLACK);// 设置画笔颜色

    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, height);

        final float halfWidth = min * 0.5f;
        mRadius = halfWidth - mThumbRadius;
    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
