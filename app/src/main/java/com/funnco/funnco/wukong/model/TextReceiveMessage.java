package com.funnco.funnco.wukong.model;

import android.content.Context;

import com.alibaba.wukong.im.MessageContent;
import com.funnco.funnco.wukong.route.Router;
import com.funnco.funnco.wukong.viewholder.TextReceiveViewHolder;
import com.funnco.funnco.wukong.viewholder.ViewHolder;

/**
 * Created by zijunlzj on 14/12/25.
 */
@Router({TextReceiveViewHolder.class})
public class TextReceiveMessage extends ReceiveMessage {

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        super.showChatMessage(context,holder);
        TextReceiveViewHolder viewHolder = (TextReceiveViewHolder) holder;
        viewHolder.chatting_content_tv.setText(getMessageContent());
    }

    public String getMessageContent() {
        MessageContent.TextContent msgContent = (MessageContent.TextContent) mMessage.messageContent();
        return msgContent.text();
    }
}
