/*
 * Project: Laiwang
 * 
 * File Created at 2015-01-28
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.funnco.funnco.R;

/**
 * Description.
 *
 * @author zhongqian.wzq
 */
public class AudioSendViewHolder extends SendViewHolder {
    public TextView chatting_audio_length;      //语音长度
    public ImageButton chatting_play_pause_btn; //语音播放/暂停按钮

    @Override
    protected void initChatView(View view) {
        chatting_play_pause_btn = (ImageButton) view.findViewById(R.id.btn_play_pause);
        chatting_audio_length = (TextView) view.findViewById(R.id.tv_audio_length);
    }

    /**
     * 设置当前的layout资源
     */
    @Override
    protected int getLayoutId() {
        return R.layout.chat_item_audio_send;
    }
}
