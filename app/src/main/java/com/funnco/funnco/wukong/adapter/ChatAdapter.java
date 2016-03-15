package com.funnco.funnco.wukong.adapter;

import android.content.Context;

import com.funnco.funnco.wukong.base.ListAdapter;
import com.funnco.funnco.wukong.model.ChatMessage;
import com.funnco.funnco.wukong.viewholder.ViewHolder;

import java.util.List;

/**
 * 聊天信息列表适配器
 * Created by shawn on 15/11/1.
 */
public class ChatAdapter extends ListAdapter<ChatMessage> {


    public ChatAdapter(Context context) {
        super(context);
    }

    public void removeChatMessage(long messageId) {
        for (int i = 0; i < mList.size(); i++) {
            ChatMessage message = mList.get(i);
            if (message.getMessageId() == messageId) {
                mList.remove(message);
                break;
            }
        }
        notifyDataSetChanged();
    }


    public void addChatMessage(ChatMessage chatMessage){
        mList.add(chatMessage);
        notifyDataSetChanged();
    }

    public void addChatMessage(List<ChatMessage> list){
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addChatMessageFront(List<ChatMessage> list){
        mList.addAll(0,list);
        notifyDataSetChanged();
    }

    public void updateChatMessage(List<ChatMessage> list){
        notifyDataSetChanged(list,"");
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getMessageId();
    }

    @Override
    protected String getDomainCategory() {
        return ChatMessage.DOMAIN_CATEGORY;
    }

    @Override
    protected void onBindView(ViewHolder viewHolder, ChatMessage item,int position) {
        int targetPosition = viewHolder.position-1;
        if (targetPosition - 1 >= 0 && targetPosition - 1 < getCount()) {
            ChatMessage preChat = (ChatMessage) getItem(targetPosition);
            item.setPreMessage(preChat);
        }
        super.onBindView(viewHolder, item, position);
    }
}
