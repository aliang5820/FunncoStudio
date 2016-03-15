package com.funnco.funnco.wukong.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.wukong.base.BaseFragmentActivity;
import com.funnco.funnco.wukong.controller.ChatWindowManager;
import com.funnco.funnco.wukong.model.Session;

/**
 * 群聊
 * Created by zijunlzj on 14/12/24.
 */
public class GroupChatActivity extends BaseFragmentActivity implements View.OnClickListener{

    private Session mCurrentSession;
    private ImageButton tvSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.chat_layout);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        mCurrentSession = (Session) getIntent().getSerializableExtra(Session.SESSION_INTENT_KEY);
        ((TextView)findViewById(R.id.id_mview)).setText(mCurrentSession.title());
        tvSetting = (ImageButton) findViewById(R.id.id_rview);
        tvSetting.setImageResource(R.mipmap.common_chat_settingicon);
        tvSetting.setOnClickListener(this);
        mCurrentSession.sync();
        initActionBar(mCurrentSession.title());
        initSystemStatusBar();
        ChatFragment fragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        fragment.setCurrentConversation(mCurrentSession);
        ChatWindowManager.getInstance().setCurrentChatCid(mCurrentSession.conversationId());
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
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_CONVERSATION, mCurrentSession.mConversation);
                intent.setClass(GroupChatActivity.this, ChatSettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                intent.putExtra(Constants.KEY_CONVERSATION, mCurrentSession.mConversation);
                intent.setClass(GroupChatActivity.this, ChatSettingActivity.class);
                startActivity(intent);
                break;
        }
    }
}