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
import android.widget.TextView;

import com.funnco.funnco.R;

/**
 * Description.
 * 接收的消息类型需继承该SendViewHolder
 * @author zhongqian.wzq
 */
public abstract class ReceiveViewHolder extends ChatViewHolder {
    public ImageView chatting_avatar;  //发送者头像
    public TextView chatting_title;    //消息标题(预留备用)

    /**
     * 初始化试图组件
     */
    @Override
    protected void initView(View view) {
        super.initView(view);
        chatting_title = (TextView) view.findViewById(R.id.chatting_title);
        chatting_avatar = (ImageView) view.findViewById(R.id.chatting_avatar);

        initChatView(view);
    }

    protected abstract void initChatView(View view);
}
