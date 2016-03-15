/*
 * Project: Laiwang
 * 
 * File Created at 2015-01-26
 * 
 * Copyright 2013 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.funnco.funnco.wukong.model;

import android.content.Context;

import com.alibaba.wukong.im.MessageContent;
import com.funnco.funnco.wukong.route.Router;
import com.funnco.funnco.wukong.viewholder.SysmsgViewHolder;
import com.funnco.funnco.wukong.viewholder.ViewHolder;

/**
 * Description.
 *
 * @author zhongqian.wzq
 */
@Router({SysmsgViewHolder.class})
public class SysmsgMessage extends ChatMessage{

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        SysmsgViewHolder viewHolder = (SysmsgViewHolder)holder;
        viewHolder.chatting_sysmsg_tv.setText(getMessageContent());
    }

    public String getMessageContent() {
        MessageContent.TextContent msgContent = (MessageContent.TextContent) mMessage.messageContent();
        return msgContent.text();
    }
}
