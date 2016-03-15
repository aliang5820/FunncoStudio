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
package com.funnco.funnco.wukong.viewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.funnco.funnco.R;

/**
 * Description.
 * 发送的消息类型需继承该SendViewHolder
 * @author zhongqian.wzq
 */
public abstract class SendViewHolder extends ChatViewHolder {
    public TextView chatting_unreadcount_tv;        //消息未读数
    public ImageView chatting_unread_icon_iv;       //未读标志
    public ImageView chatting_notsuccess_iv;        //发送出去的消息的状态
    public ProgressBar chatting_status_progress;    //消息发送中圆形进度条

    /**
     * 初始化试图组件
     */
    @Override
    protected void initView(View view) {
        super.initView(view);
        chatting_unread_icon_iv = (ImageView) view.findViewById(R.id.chatting_unread_icon_iv);
        chatting_unreadcount_tv = (TextView) view.findViewById(R.id.chatting_unreadcount_tv);
        chatting_notsuccess_iv = (ImageView) view.findViewById(R.id.chatting_notsuccess_iv);
        chatting_status_progress = (ProgressBar) view.findViewById(R.id.chatting_status_progress);

        initChatView(view);
    }

    protected abstract void initChatView(View view);
}
