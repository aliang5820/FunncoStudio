package com.funnco.funnco.wukong.business;

import com.alibaba.wukong.im.Message;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.wukong.model.ChatMessage;
import com.funnco.funnco.wukong.model.SysmsgMessage;
import com.funnco.funnco.wukong.model.TextReceiveMessage;
import com.funnco.funnco.wukong.model.TextSendMessage;

/**
 * Created by zijunlzj on 14/12/25.
 */
public class TextMessageCreator  {

    public ChatMessage onCreate(Message message) {
        ChatMessage chatMessage = null;
        if(Message.CreatorType.SELF == message.creatorType()){
            if (message.senderId() == FunncoUtils.currentOpenId()) {
                chatMessage = new TextSendMessage();
            }else {
                chatMessage = new TextReceiveMessage();
            }
        }else if(Message.CreatorType.SYSTEM == message.creatorType()){
            chatMessage = new SysmsgMessage();
        }

        chatMessage.setMessage(message);
        return chatMessage;
    }
}
