package com.funnco.funnco.wukong.model;

import android.content.Context;

import com.alibaba.wukong.im.MessageContent;
import com.funnco.funnco.wukong.route.Router;
import com.funnco.funnco.wukong.viewholder.TextSendViewHolder;
import com.funnco.funnco.wukong.viewholder.ViewHolder;

/**
 * Created by zijunlzj on 14/12/23.
 */
@Router({TextSendViewHolder.class})
public class TextSendMessage extends SendMessage {

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        super.showChatMessage(context,holder);
        TextSendViewHolder viewHolder = (TextSendViewHolder) holder;
        viewHolder.chatting_content_tv.setText(getMessageContent());
    }

    public String getMessageContent() {
        MessageContent.TextContent msgContent = (MessageContent.TextContent) mMessage.messageContent();
        return msgContent.text();
    }
}
