package com.funnco.funnco.wukong.model;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.alibaba.doraemon.Doraemon;
import com.alibaba.doraemon.image.ImageMagician;
import com.alibaba.wukong.im.MessageContent;
import com.funnco.funnco.wukong.controller.ImageShowerActivity;
import com.funnco.funnco.wukong.route.Router;
import com.funnco.funnco.wukong.viewholder.ImageReceiveViewHolder;
import com.funnco.funnco.wukong.viewholder.ViewHolder;

/**
 * Created by sifeier on 15/1/13.
 */
@Router({ImageReceiveViewHolder.class})
public class ImageReceiveMessage extends ReceiveMessage {
    private static ImageMagician imageMagician = null;
    private static final String LAST_MESSAGE_SHOW_CONTENT = "[图片]";

    public ImageReceiveMessage() {
        if(imageMagician == null) {
            imageMagician = (ImageMagician) Doraemon.getArtifact(ImageMagician.IMAGE_ARTIFACT);
        }
    }

    @Override
    public void showChatMessage(Context context, ViewHolder holder) {
        super.showChatMessage(context,holder);
        displayImageContent(context,(ImageReceiveViewHolder) holder);
    }

    /**
     * 显示图片消息内容
     * @param context
     * @param viewHolder
     */
    private void displayImageContent(final Context context,ImageReceiveViewHolder viewHolder) {
        MessageContent.ImageContent messageContent = (MessageContent.ImageContent)mMessage.messageContent();
        imageMagician.setImageBackground(viewHolder.chatting_content_iv,messageContent.url(),(ListView)(viewHolder.parentView));
        viewHolder.chatting_content_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageShowerActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ImageShowerActivity.CONV_INTENT_KKEY,mMessage.conversation());
                intent.putExtra(ImageShowerActivity.MID_INTENT_KKEY,mMessage.messageId());
                context.startActivity(intent);
            }
        });
    }

    public String getMessageContent() {
        return LAST_MESSAGE_SHOW_CONTENT;
    }
}
