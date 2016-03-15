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
package com.funnco.funnco.wukong.business;

import com.alibaba.wukong.im.Message;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.wukong.model.ChatMessage;
import com.funnco.funnco.wukong.model.ImageReceiveMessage;
import com.funnco.funnco.wukong.model.ImageSendMessage;
import com.funnco.funnco.wukong.model.SysmsgMessage;

/**
 * Description.
 *
 * @author zhongqian.wzq
 */
public class ImageMessageCreator {
    public ChatMessage onCreate(Message message) {
        ChatMessage chatMessage = null;
        if(Message.CreatorType.SELF == message.creatorType()){
            if (message.senderId() == FunncoUtils.currentOpenId()) {
                chatMessage=new ImageSendMessage();
            }else {
                chatMessage = new ImageReceiveMessage();
            }
        }else if(Message.CreatorType.SYSTEM == message.creatorType()){
            chatMessage = new SysmsgMessage();
        }

        chatMessage.setMessage(message);
        return chatMessage;
    }
}
