package com.funnco.funnco.wukong.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.im.Conversation;
import com.alibaba.wukong.im.IMEngine;
import com.alibaba.wukong.im.User;
import com.alibaba.wukong.im.UserService;
import com.alibaba.wukong.im.utils.Utils;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.chat.ChatMessageTransmitter;
import com.funnco.funnco.bean.TeamMember;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.wukong.base.BaseFragmentActivity;
import com.funnco.funnco.wukong.controller.ChatWindowManager;
import com.funnco.funnco.wukong.model.ChatMessage;
import com.funnco.funnco.wukong.model.Session;

/**
 * 单聊
 * Created by zijunlzj on 14/12/24.
 */
public class SingleChatActivity extends BaseFragmentActivity implements View.OnClickListener{
    private static final String TAG = SingleChatActivity.class.getName();
    private int mSessionType;
    private  ChatFragment chat;
    private ChatMessageTransmitter transmitter;
    //private Conversation mConversation;
    private Session mCurrentSession;
    private ImageButton tvSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.chat_layout);
        findViewById(R.id.id_lview).setOnClickListener(this);
        mCurrentSession = (Session) getIntent().getSerializableExtra(Session.SESSION_INTENT_KEY);
        UserService userService = IMEngine.getIMService(UserService.class);
        userService.getUser(new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                ((TextView) findViewById(R.id.id_mview)).setText(user.nickname());
            }

            @Override
            public void onException(String s, String s1) {
                LogUtils.i(TAG, "s : " + s + "\n s1 : " + s1);
            }

            @Override
            public void onProgress(User user, int i) {

            }
        }, mCurrentSession.getOtherOpenId());
        /*String title = mConversation.title();
        if (title.equals(mConversation.getPeerId()+"")) {
            title = getIntent().getStringExtra("title");
        }
        ((TextView) findViewById(R.id.id_mview)).setText(title+"");*/

        tvSetting = (ImageButton) findViewById(R.id.id_rview);
        tvSetting.setImageResource(R.mipmap.common_chat_settingicon);
        tvSetting.setOnClickListener(this);
        mCurrentSession.sync();
        mSessionType = mCurrentSession.type();
        LogUtils.e("DemoLog","mSessionType1="+mSessionType);
        initSystemStatusBar(); //高版本上statusbar一体化
        //initActionBar(mConversation.title());
        setUpActionBar(mCurrentSession);
        chat =(ChatFragment)getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        chat.setCurrentConversation(mCurrentSession);
        ChatWindowManager.getInstance().setCurrentChatCid(mCurrentSession.conversationId());
        transmitter= (ChatMessageTransmitter) getSupportFragmentManager().findFragmentById(R.id.chat_transmitter);
        transmitter.setCurrentConeverstaion(mCurrentSession);
        /*transmitter.setOnTransmitted(new Callback<ChatMessage>() {
            @Override
            public void onSuccess(ChatMessage chatMessage) {
//                chat.onMessageSended( chatMessage );
            }

            @Override
            public void onException(String code, String reason) {

            }

            @Override
            public void onProgress(ChatMessage chatMessage, int i) {

            }
        });*/

        if (!NetUtils.isConnection(this)) {
            FunncoUtils.showToast(this, getString(R.string.net_err));
        }
    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_chat_setting:
                //TODO start ChatSettingActivity
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_CONVERSATION, mCurrentSession);
                intent.setClass(SingleChatActivity.this, ChatSettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //todo:群聊跳转到GroupChatActivity的话改方法需要修改
    public void setUpActionBar(final Conversation conversation){
        if(Conversation.ConversationType.GROUP == conversation.type()){
            initActionBar(conversation.title());
        }else{
            IMEngine.getIMService(UserService.class).getUser(new Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (TextUtils.isEmpty(user.nickname())) {
                        initActionBar(conversation.title());
                    } else {
                        initActionBar(user.nickname());
                    }
                }

                @Override
                public void onException(String code, String reason) {
                    LogUtils.e("DemoLog", "Get user error.code=" + code + " reason=" + reason);
                }

                @Override
                public void onProgress(User user, int progress) {
                }
            }, Utils.toLong(conversation.title()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_lview:
                finish();
                break;
            case R.id.id_rview:
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_CONVERSATION, mCurrentSession);
                intent.setClass(SingleChatActivity.this, ChatSettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}
