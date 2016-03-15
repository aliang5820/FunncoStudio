/*
 * Project: Laiwang
 * 
 * File Created at 2015-02-05
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
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.funnco.funnco.wukong.impl.AvatarMagicianImpl;
import com.funnco.funnco.wukong.user.UserProfileActivity;
import com.funnco.funnco.wukong.viewholder.ReceiveViewHolder;
import com.funnco.funnco.wukong.viewholder.ViewHolder;

/**
 * Description.
 *
 * @author zhongqian.wzq
 */
public class ReceiveMessage extends ChatMessage{

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        //显示头像
        showAvatar(context,(ReceiveViewHolder)holder);

        //置未读消息为读状态
        if(!mMessage.iHaveRead()) {
            readMessage();
        }
    }

    /**
     * 显示消息发送者头像
     * @param context
     * @param holder
     */
    public void showAvatar(final Context context,ReceiveViewHolder holder){
        AvatarMagicianImpl.getInstance().setUserAvatar(holder.chatting_avatar,mMessage.senderId(),(ListView)(holder.parentView));
        holder.chatting_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user_open_id",mMessage.senderId());
                context.startActivity(intent);
            }
        });
    }
}
