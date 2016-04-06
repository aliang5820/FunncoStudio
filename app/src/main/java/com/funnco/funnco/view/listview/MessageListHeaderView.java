package com.funnco.funnco.view.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.utils.date.DateUtils;

/**
 * Created by EdisonZhao on 16/4/2.
 */
public class MessageListHeaderView extends RelativeLayout {
    private Context mContext;
    private ImageView imageView;
    private TextView titleView, messageView, timeView;

    public MessageListHeaderView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MessageListHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_message_header, this,  true);
        imageView = (ImageView) view.findViewById(R.id.id_imageview);
        titleView = (TextView) view.findViewById(R.id.id_title_4);
        messageView = (TextView) view.findViewById(R.id.id_title_5);
        timeView = (TextView) view.findViewById(R.id.id_title_6);
    }

    public void setIcon(int resId) {
        imageView.setImageResource(resId);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setMessage(String msg) {
        messageView.setText(msg);
    }

    public void setTime(long time) {
        timeView.setText(DateUtils.formatHHMM(time));
    }
}
