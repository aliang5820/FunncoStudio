package com.funnco.funnco.wukong.viewholder;

import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;


/**
 * Created by zijunlzj on 14/12/25.
 */
public class TextSendViewHolder extends SendViewHolder {
    //文本消息内容
    public TextView chatting_content_tv;

    /**
     * 初始化视图组件
     *
     * @param view
     */
    @Override
    protected void initChatView(View view) {
        chatting_content_tv= (TextView) view.findViewById(R.id.chatting_content_tv);
    }

    /**
     * 设置当前的layout资源
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.chat_item_text_send;
    }
}
