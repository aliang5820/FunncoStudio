/*
 * Project: Laiwang
 * 
 * File Created at 2015-02-06
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
package com.funnco.funnco.wukong.listener;

import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationChangeListener;

import java.util.List;

/**
 * Description.
 *
 * @author zhongqian.wzq
 */
public class ConversationUpdateListener extends ConversationChangeListener {
    /**
     * 会话标题变更
     */
    @Override
    public void onTitleChanged(List<Conversation> list) {

    }

    /**
     * 会话icon变更
     */
    @Override
    public void onIconChanged(List<Conversation> list) {

    }

    /**
     * 会话状态变更
     */
    @Override
    public void onStatusChanged(List<Conversation> list) {

    }

    /**
     * 会话标题变更，如
     */
    @Override
    public void onLatestMessageChanged(List<Conversation> list) {

    }

    private static int lastUnreadCount = 0;
    /**
     * 会话未读消息数变更
     */
    @Override
    public void onUnreadCountChanged(List<Conversation> list) {

    }

    /**
     * 会话草稿变更
     */
    @Override
    public void onDraftChanged(List<Conversation> list) {

    }

    /**
     * 会话tag变更
     */
    @Override
    public void onTagChanged(List<Conversation> list) {

    }

    /**
     * 会话extension变更
     */
    @Override
    public void onExtensionChanged(List<Conversation> list) {

    }

    /**
     * 会话@状态变更
     */
    @Override
    public void onAtMeStatusChanged(List<Conversation> list) {

    }

    @Override
    public void onLocalExtrasChanged(List<Conversation> list) {

    }


    /**
     * 会话是否通知的状态变更
     */
    @Override
    public void onNotificationChanged(List<Conversation> list) {

    }

    /**
     * 会话置顶状态变更
     */
    @Override
    public void onTopChanged(List<Conversation> list) {

    }

    /**
     * 会话成员数变更
     */
    @Override
    public void onMemberCountChanged(List<Conversation> list) {

    }
}
