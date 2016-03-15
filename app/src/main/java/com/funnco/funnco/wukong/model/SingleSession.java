package com.funnco.funnco.wukong.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.utils.Utils;
import com.funnco.funnco.view.gridview.CustomGridView;
import com.funnco.funnco.wukong.base.ItemClick;
import com.funnco.funnco.wukong.impl.AvatarMagicianImpl;
import com.funnco.funnco.wukong.route.Router;
import com.funnco.funnco.wukong.user.SingleChatActivity;
import com.funnco.funnco.wukong.viewholder.SessionViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zijunlzj on 14/12/12.
 */

@Router({SessionViewHolder.class})
public class SingleSession extends Session implements ItemClick.OnItemClickListener {

    public SingleSession(Conversation conversation) {
        super(conversation);
    }

    @Override
    public void showAvatar(Context context, String mediaIds, View view,ListView itemParent) {
        List<Long> openId = new ArrayList<Long>(1);
        try {
            openId.add(Long.parseLong(mediaIds));
        } catch (NumberFormatException e) {
            Log.e("SingleSession", "NumberFormatException");
        }

        AvatarMagicianImpl.getInstance().setConversationAvatar((CustomGridView) view, openId,itemParent);
    }

    @Override
    public void onClick(Context sender, View view, int position) {
        resetUnreadCount();
        Intent intent = new Intent(sender, SingleChatActivity.class);
        intent.putExtra(SESSION_INTENT_KEY, this);
        sender.startActivity(intent);
    }

    protected void refreshTitle(final SessionViewHolder viewHolder) {
        mServiceFacade.getUserByOpenId(new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                if (TextUtils.isEmpty(user.nickname())) {
                    viewHolder.sessionTitleTxt.setText(title());
                } else {
                    viewHolder.sessionTitleTxt.setText(user.nickname());
                }
            }

            @Override
            public void onException(String code, String reason) {
                Log.e("SingleSession", "Get user error.code=" + code + " reason=" + reason);
            }

            @Override
            public void onProgress(User user, int progress) {
            }
        }, Utils.toLong(mConversation.title()));
    }

    public void setSessionContent(final TextView contentView) {
        if (latestMessage() == null) {
            contentView.setText("");
        } else {
            contentView.setText(mServiceFacade.getSessionContent(this));
        }
    }
}
