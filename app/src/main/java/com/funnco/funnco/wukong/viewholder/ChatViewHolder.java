package com.funnco.funnco.wukong.viewholder;

import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.wukong.viewholder.ViewHolder;

/**
 * Created by zijunlzj on 14/12/29.
 */
public abstract class ChatViewHolder extends ViewHolder {
    public View tv_overlay;
    public TextView chatting_time_tv;

    @Override
    protected void initView(View view) {
        tv_overlay=view.findViewById(R.id.tv_overlay);
        chatting_time_tv = (TextView) view.findViewById(R.id.chatting_time_tv);
//        initChatView(view);
    }

//    protected abstract void initChatView(View view);
}
