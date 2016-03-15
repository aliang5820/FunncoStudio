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
package com.funnco.funnco.wukong.viewholder;


import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;

/**
 * Description.
 *
 * @author zhongqian.wzq
 */
public class SysmsgViewHolder extends ChatViewHolder {
    public TextView chatting_sysmsg_tv; //系统消息内容

    /**
     * 初始化视图组件
     */
    @Override
    protected void initView(View view) {
        super.initView(view);
        chatting_sysmsg_tv = (TextView) view.findViewById(R.id.chatting_sysmsg_tv);
    }

    /**
     * 设置当前的layout资源
     */
    @Override
    protected int getLayoutId() {
        return R.layout.chat_item_text_sysmsg;
    }
}
