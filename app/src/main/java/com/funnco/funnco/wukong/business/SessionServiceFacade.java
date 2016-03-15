package com.funnco.funnco.wukong.business;

import android.text.TextUtils;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationListener;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.UserService;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.wukong.base.Functional;
import com.funnco.funnco.wukong.base.Functional.Action;
import com.funnco.funnco.wukong.listener.ConversationUpdateListener;
import com.funnco.funnco.wukong.model.ChatMessage;
import com.funnco.funnco.wukong.model.GroupSession;
import com.funnco.funnco.wukong.model.Session;
import com.funnco.funnco.wukong.model.SingleSession;

import java.util.List;

/**
 * Created by zijunlzj on 15/1/7.
 */
public class SessionServiceFacade {

    ConversationService mConversationService = IMEngine.getIMService(ConversationService.class);

    ChatMessageFactory mChatMessageFactory = new ChatMessageFactory();

    UserService mUserService = IMEngine.getIMService(UserService.class);

    public volatile static String mCurrentConversationId;//TODO 需要修改设计

    public static boolean isInConversation(String id){
        if( TextUtils.isEmpty(id)){
            return false;
        }
       return id.equals(mCurrentConversationId);

    }

    public Session buildSession(Conversation source){
        Session result=null;
        switch (source.type()){
            case Conversation.ConversationType.CHAT:result=new SingleSession(source);break;
            case Conversation.ConversationType.GROUP:result=new GroupSession(source);break;
        }
        if(result!=null){
            result.setServiceFacade(this);
            result.setCurrentUserId(FunncoUtils.currentOpenId());
        }
        return result;
    }

    private List<Session> buildSessions(List<Conversation> conversations){
        return Functional.each(conversations, new Functional.Func<Conversation, Session>() {

            @Override
            public Session func(Conversation source) {
                return buildSession(source);
            }
        });
    }

    public SessionServiceFacade listSessions(int size, final Callback<List<Session>> callback) {
        mConversationService.listConversations(new Callback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                List<Session> sessions = buildSessions(conversations);
                callback.onSuccess(sessions);
            }

            @Override
            public void onException(String code, String reason) {
                callback.onException(code, reason);
            }

            @Override
            public void onProgress(List<Conversation> conversations, int i) {
                callback.onProgress(null, i);
            }


        }, size, Conversation.ConversationType.CHAT | Conversation.ConversationType.GROUP);
        return this;
    }

    public void getUserByOpenId(Callback callback,long openId){
        mUserService.getUser(callback,openId);
    }

    public String getSessionContent(Conversation conversation) {
        Message lastMessage = conversation.latestMessage();
        if (lastMessage == null) {
            return "";
        }
        ChatMessage chatMessage = mChatMessageFactory.create(lastMessage);
        if (chatMessage == null) {
            return "";
        }
        return chatMessage.getMessageContent();
    }

    public SessionServiceFacade remove(String id) {
        mConversationService.removeConversations(new Callback<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onException(String s, String s2) {

            }

            @Override
            public void onProgress(Void aVoid, int i) {

            }
        }, id);
        return this;
    }

    public SessionServiceFacade onRemoved(final Functional.Action<String> action) {
        mConversationService.addConversationListener(new ConversationListener() {
            @Override
            public void onAdded(List<Conversation> list) {
            }

            @Override
            public void onRemoved(List<Conversation> list) {
                Conversation conversation = list.get(0);
                if (action != null && conversation != null) {
                    action.action(conversation.conversationId());
                }
            }
        });
        return this;
    }

    public SessionServiceFacade onCreated(final Functional.Action<List<Session>> action) {
        mConversationService.addConversationListener(new ConversationListener() {
            @Override
            public void onAdded(List<Conversation> list) {
               doActionForSessionList(list,action);
            }

            /**
             * 会话删除
             */
            @Override
            public void onRemoved(List<Conversation> list) {

            }
        });
        return this;
    }

//    public SessionServiceFacade onReceiveMessage(final Action<List<Session>> action) {
//        mMessageService.addMessageListener(new MessageListener() {
//            @Override
//            public void onAdded(List<Message> messages) {
//                List<Conversation> conversations = new ArrayList<Conversation>();
//                for (Message msg : messages) {
//                    if(msg.senderId()==getCurrentUserId()){
//                        continue;
//                    }
//                    Conversation conversation = msg.conversation();
//                    if(conversation==null){
//                        return;
//                    }
//                    if(!isInConversation(conversation.conversationId())) {
//                        conversation.addUnreadCount(1);//新消息会话未读需要加一
//                    }
//                    conversations.add(conversation);
//                }
//                doActionForSessionList(conversations,action);
//            }
//
//            @Override
//            public void onRemoved(List<Message> messages) {
//
//            }
//
//            @Override
//            public void onChanged(List<Message> messages) {
//
//            }
//        });
//        return this;
//    }


    public SessionServiceFacade onUnreadCountChange(final Action<List<Session>> action) {
        mConversationService.addConversationChangeListener(new ConversationUpdateListener() {
            @Override
            public void onUnreadCountChanged(List<Conversation> list) {
                doActionForSessionList(list, action);
            }
        });
        return this;
    }

    public SessionServiceFacade onContentChange(final Action<List<Session>> action){
        mConversationService.addConversationChangeListener(new ConversationUpdateListener(){
            @Override
            public void onLatestMessageChanged(List<Conversation> list) {
              doActionForSessionList(list,action);
            }
        });
        return this;
    }

    //todo:会调用两次：回调一次，push一次
    public SessionServiceFacade onTopChange(final Action<List<Session>> action){
        mConversationService.addConversationChangeListener(new ConversationUpdateListener(){
            @Override
            public void onTopChanged(List<Conversation> list) {
                doActionForSessionList(list, action);
            }
        });
        return this;
    }

    private void doActionForSessionList(List<Conversation> list, Action<List<Session>> action){
        List<Session> sessions = buildSessions(list);
        if (sessions.size() > 0 && action != null) {
            action.action(sessions);
        }
    }
}
