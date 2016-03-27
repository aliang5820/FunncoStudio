package com.funnco.funnco.view.circleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.funnco.funnco.R;

/**
 * Created by EdisonZhao on 16/3/27.
 */
public class CircleLeftImageView extends BaseCircleView {
    public CircleLeftImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleLeftImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleLeftImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        mBgPaint.setStrokeWidth(4);
        RectF rectF = new RectF(2, 2, mWidth - 2, mHeight - 2);
        mBgPaint.setColor(getResources().getColor(R.color.circle_line_color_2));
        canvas.drawArc(rectF, 270, -180, false, mBgPaint);

        //画图片，就是贴图
        RectF rectBlackBg = new RectF(16, 16, mWidth - 16, mHeight - 16);
        mBgPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(rectBlackBg, 0, 360, false, mBgPaint);
    }
}
