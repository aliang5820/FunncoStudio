package com.funnco.funnco.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.view.imageview.CircleImageView;

/**
 * Created by user on 2015/7/6.
 */
public class BindNotifyActivity extends BaseActivity {

    private View parentView;
    private CircleImageView civIcon;
    private TextView tvNickname;


    private Intent intent;
    //微信用户昵称/头像地址
    private String nickname;
    private String headimgurl;

    //微信绑定需要的值
    private String token;//从微信获取的token
    private String openid;//用户openid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
            token = intent.getStringExtra("token");
            openid = intent.getStringExtra("openid");
            nickname = intent.getStringExtra("nickname");
            headimgurl = intent.getStringExtra("headimgurl");
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_bindnotify,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_bindnotify_title_1)).setText(nickname+"");
        civIcon = (CircleImageView) findViewById(R.id.civ_bindnotify_icon);
        if (utils != null && !TextUtils.isNull(headimgurl)){//微信头像进行显示
            utils.imageLoader(headimgurl,civIcon);
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_bindnotify_linknow).setOnClickListener(this);
        findViewById(R.id.tv_bindnotify_registfaster).setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.tv_bindnotify_linknow://立即关联
                intent.setClass(mContext,BindActivity.class);
                break;
            case R.id.tv_bindnotify_registfaster://快速注册
                intent.setClass(mContext, RegisterFasterActivity.class);
                break;
            case R.id.tv_headcommon_headl:
                finishOk();
                return;
        }
        intent.putExtra(Constants.TOKEN,token);
        intent.putExtra("openid",openid);
        startActivity(intent);
        finishOk();
    }
}
