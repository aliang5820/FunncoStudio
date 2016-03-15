package com.funnco.funnco.wukong.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;
import com.funnco.funnco.view.gridview.CustomGridView;
import com.funnco.funnco.wukong.base.ItemClick;
import com.funnco.funnco.wukong.impl.AvatarMagicianImpl;
import com.funnco.funnco.wukong.route.Router;
import com.funnco.funnco.wukong.user.SingleChatActivity;
import com.funnco.funnco.wukong.viewholder.SessionViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zijunlzj on 14/12/12.
 */
@Router({SessionViewHolder.class})
public class GroupSession extends Session implements ItemClick.OnItemClickListener {

    public GroupSession(Conversation conversation) {
        super(conversation);
    }

    @Override
    public void showAvatar(Context context, String mediaId, View view,ListView itemParent) {
        String[] openIdArr = mediaId.split(":");
        List<Long> openIds = new ArrayList<Long>();
        for (int i = 0; i < openIdArr.length; ++i) {
            try {
                openIds.add(Long.parseLong(openIdArr[i]));
            } catch (NumberFormatException e) {
//                Log.e("GroupSession", "NumberFormatException:请查看群聊icon是否由openid和:构成的");
            }
        }
        AvatarMagicianImpl.getInstance().setConversationAvatar((CustomGridView) view, openIds,itemParent);
    }

    @Override
    public void onClick(Context sender, View view, int position) {
        resetUnreadCount();
        Intent intent = new Intent(sender, SingleChatActivity.class);
        intent.putExtra(SESSION_INTENT_KEY, this);
        sender.startActivity(intent);
    }

    public void setSessionContent(final TextView contentView){
        Message message = latestMessage();
        if (message == null) {
            contentView.setText("");
        }else{
            //系统消息直接显示
            if(Message.CreatorType.SYSTEM == message.creatorType()){
                contentView.setText(mServiceFacade.getSessionContent(this));
                return;
            }

            //获取消息发送者昵称
            IMEngine.getIMService(UserService.class).getUser(new Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    if(TextUtils.isEmpty(user.nickname())){
                        contentView.setText( mServiceFacade.getSessionContent(GroupSession.this));
                    }else{
                        contentView.setText(user.nickname() + "：" + mServiceFacade
                                .getSessionContent(GroupSession.this));
                    }
                }

                @Override
                public void onException(String s, String s2) {
                    contentView.setText(mServiceFacade.getSessionContent(GroupSession.this));
                }

                @Override
                public void onProgress(User user, int i) {
                }
            }, latestMessage().senderId());
        }
    }
}
