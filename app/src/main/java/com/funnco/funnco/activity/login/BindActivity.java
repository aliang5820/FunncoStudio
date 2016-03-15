package com.funnco.funnco.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/7/6.
 */
public class BindActivity extends BaseActivity {

    private View parentView;
    private EditText etPhone,etPw;
    //忘记密码
    private TextView tvLostpw;
    private String mobile;
    private String pwd;

    //微信登录的值
    private Intent intent;
    private String token;//从微信获取的token
    private String openid;//用户openid
    private String type = "login";//固定值

    private boolean isLogining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
            token = intent.getStringExtra(Constants.TOKEN);
            openid = intent.getStringExtra("openid");
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_bind,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        etPhone = (EditText) findViewById(R.id.et_bind_mobile);
        etPw = (EditText) findViewById(R.id.et_bind_passwd);
        tvLostpw = (TextView) findViewById(R.id.tv_bind_lostpw);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.tv_bind_cancle).setOnClickListener(this);
        findViewById(R.id.tv_bind_ok).setOnClickListener(this);
        tvLostpw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回键
            case R.id.tv_bind_cancle://取消
                finishOk();
                break;
            case R.id.tv_bind_ok://确定
                if (isLogining){
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                //进行注册
                mobile = etPhone.getText()+"";
                pwd = etPw.getText()+"";
                if (!checkData()){
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                showLoading(parentView);
                Map<String,Object> map = new HashMap<>();
                map.put(Constants.TOKEN,token);
                map.put("openid",openid);
                map.put("type",type);
                map.put("mobile",mobile);
                map.put("pwd", pwd);
                postData2(map, FunncoUrls.getBindWeCharUrl(), false);
                break;
            case R.id.tv_bind_lostpw:
                //进行跳转
                Intent intent = new Intent(mContext,GetPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        UserLoginInfo user = JsonUtils.getObject(JsonUtils.getJObt(result, "params").toString(), UserLoginInfo.class);
        //用户信息进行存储
        new SQliteAsynchTask<UserLoginInfo>().saveOrUpdate(dbUtils,user);
        BaseApplication.getInstance().setUser(user);//设置全局user
        SharedPreferencesUtils.setValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.USER_PWD, pwd);
        SharedPreferencesUtils.setUserValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, user);
        startActivity(MainActivity.class);
        finish();
    }

    private boolean checkData(){
        boolean isOK;
        if (TextUtils.isPhoneNumber(mobile) && !TextUtils.isNull(pwd)){
            isOK = true;
        }else{
            isOK = false;
        }
        return isOK;
    }
}
