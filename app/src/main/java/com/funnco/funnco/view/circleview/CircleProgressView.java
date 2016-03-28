package com.funnco.funnco.view.circleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

/**
 * Created by EdisonZhao on 16/3/21.
 */
public class CircleProgressView extends BaseCircleView {

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleProgressView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        RectF rectBlackBg = new RectF(8, 8, mWidth - 8, mHeight - 8);
        canvas.drawArc(rectBlackBg, 0, 360, false, mBgPaint);
        mPaint.setColor(Color.BLACK);

        Paint.FontMetricsInt fontMetrics = mTopTextPaint.getFontMetricsInt();
        int baseline = (int) (rectBlackBg.bottom + rectBlackBg.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        // mTopTextPaint.setTextAlign(Paint.Align.CENTER);之前已经设置过了
        canvas.drawText("你的账户还剩", rectBlackBg.centerX(), baseline - (getStringHeight(mBotomTextPaint) / 2), mTopTextPaint);
        canvas.drawText((int)getCurrentCount() + "天", rectBlackBg.centerX(), baseline + (getStringHeight(mBotomTextPaint) / 2), mBotomTextPaint);


        float section = getCurrentCount() / getMaxCount();
        if (section <= 1.0f / 3.0f) {
            if (section != 0.0f) {
                mPaint.setColor(SECTION_COLORS[0]);
            } else {
                mPaint.setColor(Color.TRANSPARENT);
            }
        } else {
            int count = (section <= 1.0f / 3.0f * 2) ? 2 : 3;
            int[] colors = new int[count];
            System.arraycopy(SECTION_COLORS, 0, colors, 0, count);
            float[] positions = new float[count];
            if (count == 2) {
                positions[0] = 0.0f;
                positions[1] = 1.0f - positions[0];
            } else {
                positions[0] = 0.0f;
                positions[1] = (getMaxCount() / 4) / getCurrentCount();
                positions[2] = 1.0f - positions[0] * 2;
            }
            positions[positions.length - 1] = 1.0f;
            LinearGradient shader = new LinearGradient(3, 3, (mWidth - 3) * section, mHeight - 3, colors, null, Shader.TileMode.MIRROR);
            mPaint.setShader(shader);
        }
        canvas.drawArc(rectBlackBg, 270, section * 360, false, mPaint);
    }
}