package com.funnco.funnco.activity.chat;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.ConversationService;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.Message;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.chat.impl.MessageSender;

import java.util.Map;

/**
 * 聊天界面
 * Created by user on 2015/10/16.
 */
public class ChatActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private TextView tvBack;

    private String title;
    private String icon;
    private Message message;

    private TextView tvLaunch;
    private TextView tvVoiceRecord;
    private ImageView ivPhoto;

    private Conversation conversation;
    private MessageSender messageSender;
    private boolean mIsRecording = false;
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_chatting, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        container = (FrameLayout) findViewById(R.id.layout_container);
        tvBack = (TextView) findViewById(R.id.tv_headcommon_headl);
        tvBack.setText("Anna");
        ImageView iv = new ImageView(mContext);
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setImageResource(R.mipmap.chatting);
        findViewById(R.id.llayout_foot).setVisibility(View.GONE);
        container.addView(iv);

        tvLaunch = (TextView) findViewById(R.id.id_title_6);
        ivPhoto = (ImageView) findViewById(R.id.id_title_5);
        tvVoiceRecord = (TextView) findViewById(R.id.id_title_4);


    }

    private void createConversation(String title, String icon, Message message, int type, long tag, Map<String,String> extension, long uids){
        /**
         * title 群聊名称（单聊设置不会生效，只支持群聊）
         * icon 群聊头像（单聊设置不会生效，只支持群聊）
         * message 消息（参考创建消息）
         * type 设置单聊群聊类型（参考会话类型）
         * tag extension 可选参数，用于标志客户自定义信息
         * uids 聊天中其他人的openid
         */

        IMEngine.getIMService(ConversationService.class).createConversation(new Callback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {

            }

            @Override
            public void onException(String s, String s1) {

            }

            @Override
            public void onProgress(Conversation conversation, int i) {

            }
        },title, icon, message, type, tag, extension, uids);
    }

    @Override
    protected void initEvents() {
        tvBack.setOnClickListener(this);

        //长按录音进行录音
        tvVoiceRecord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIsRecording = true;
                Toast.makeText(mContext , R.string.str_chat_recording, Toast.LENGTH_SHORT).show();
                messageSender.benginAudioRecordAndSend(conversation);
                return true;
            }
        });
        tvVoiceRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                        // 手抬起，即录音结束
                        if (mIsRecording) {
                            mIsRecording = false;
                            messageSender.endAudioSend();
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }
}
