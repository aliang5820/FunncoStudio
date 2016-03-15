package com.funnco.funnco.wukong.route;

import com.funnco.funnco.wukong.base.DisplayListItem;
import com.funnco.funnco.wukong.model.AudioReceiveMessage;
import com.funnco.funnco.wukong.model.AudioSendMessage;
import com.funnco.funnco.wukong.model.ChatMessage;
import com.funnco.funnco.wukong.model.GroupSession;
import com.funnco.funnco.wukong.model.ImageReceiveMessage;
import com.funnco.funnco.wukong.model.ImageSendMessage;
import com.funnco.funnco.wukong.model.Session;
import com.funnco.funnco.wukong.model.SingleSession;
import com.funnco.funnco.wukong.model.SysmsgMessage;
import com.funnco.funnco.wukong.model.TextReceiveMessage;
import com.funnco.funnco.wukong.model.TextSendMessage;

/**
 * Created by zijunlzj on 14/12/16.
 */
public class RouteRegister {

    public static void bootwrapped() {
        reg(GroupSession.class, Session.DOMAIN_CATEGORY);
        reg(SingleSession.class,Session.DOMAIN_CATEGORY);
        reg(TextReceiveMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(TextSendMessage.class,ChatMessage.DOMAIN_CATEGORY);
        reg(SysmsgMessage.class, ChatMessage.DOMAIN_CATEGORY);
        reg(ImageSendMessage.class,ChatMessage.DOMAIN_CATEGORY);
        reg(ImageReceiveMessage.class,ChatMessage.DOMAIN_CATEGORY);
        reg(AudioSendMessage.class,ChatMessage.DOMAIN_CATEGORY);
        reg(AudioReceiveMessage.class,ChatMessage.DOMAIN_CATEGORY);
    }


    private static void reg(Class<? extends DisplayListItem> domain,String category) {
        RouteProcessor.registRouter(domain,category);
    }

}
