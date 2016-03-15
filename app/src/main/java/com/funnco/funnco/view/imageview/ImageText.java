package com.funnco.funnco.view.imageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.utils.support.Constant;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ImageText extends LinearLayout {
    private Context mContext = null;
    private ImageView mImageView = null;
    private TextView mTextView = null;
    private final static int DEFAULT_IMAGE_WIDTH = 64;
    private final static int DEFAULT_IMAGE_HEIGHT = 64;
    private final static int TEXT_SIZE = 13;
    //	  private int CHECKED_COLOR = Color.rgb(29, 118, 199);
// 选中颜色
    private int CHECKED_COLOR = getResources().getColor(R.color.color_hint_tangerine);
    private int UNCHECKED_COLOR = getResources().getColor(R.color.color_main_bottom_rb_unchecked);   //默认颜色

    public ImageText(Context context) {
        super(context);
        mContext = context;
    }

    public ImageText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View parentView = inflater.inflate(R.layout.layout_item_bootom, this, true);
        mImageView = (ImageView) findViewById(R.id.image_iamge_text);
        mTextView = (TextView) findViewById(R.id.text_iamge_text);
    }

    public void setImage(int id) {
        if (mImageView != null) {
            mImageView.setImageResource(id);
            setImageSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        }
    }

    public void setText(String s) {
        if (mTextView != null) {
            mTextView.setText(s);
            mTextView.setTextColor(UNCHECKED_COLOR);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    private void setImageSize(int w, int h) {
        if (mImageView != null) {
            ViewGroup.LayoutParams params = mImageView.getLayoutParams();
            params.width = w;
            params.height = h;
            mImageView.setLayoutParams(params);
        }
    }

    public void setChecked(int itemID) {
        if (mTextView != null) {
            mTextView.setTextColor(CHECKED_COLOR);
        }
        int checkDrawableId = -1;
        switch (itemID) {
            case Constant.BTN_FLAG_SCHEDULE:
                checkDrawableId = R.mipmap.common_schedule_select;
                break;
            case Constant.BTN_FLAG_SERVICE:
                checkDrawableId = R.mipmap.common_message_select;
                break;
            case Constant.BTN_FLAG_UPLOAD:
                checkDrawableId = R.mipmap.common_service_select;
                break;
            case Constant.BTN_FLAG_MY:
                checkDrawableId = R.mipmap.common_my_select;
                break;
            default:
                BufferedReader buf;
                buf=new BufferedReader(new InputStreamReader(System.in));
                break;
        }
        if (mImageView != null) {
            mImageView.setImageResource(checkDrawableId);
        }
    }
}
