package com.funnco.funnco.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.lidroid.xutils.exception.DbException;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信绑定快速注册---注册新账号
 * Created by user on 2015/7/6.
 */
public class RegisterFasterActivity extends BaseActivity {

    private View parentView;
    private EditText etPhone;
    private EditText etAuthorcode;
    private EditText etPWD_1;
    private EditText etPWD_2;
    private TextView tvAuthorcode;
    private CheckBox cbPro;

    //需要上传的值
    private String token;//从微信获取的token
    private String openid;//用户openid
    private String type = "reg";
    private String mobile;
    private String pwd;
    private String vcode;
    private Intent intent;

    private boolean agreen;
    private boolean isLogining;
    //信息发送成功
    private boolean isSending = false;
    private int seconds = 60;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    tvAuthorcode.setText("再次获取"+(--seconds));
                    if(seconds <=0){
                        isSending = false;
                        tvAuthorcode.setEnabled(true);
                        tvAuthorcode.setText("获取验证码");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
            token = intent.getStringExtra(Constants.TOKEN);
            openid = intent.getStringExtra("openid");
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_registfaster, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        etPhone = (EditText) findViewById(R.id.et_registfaster_phonenumber);
        etAuthorcode = (EditText) findViewById(R.id.et_registfaster_authorcode);
        etPWD_1 = (EditText) findViewById(R.id.et_registfaster_pw);
        etPWD_2 = (EditText) findViewById(R.id.et_registfaster_pw_2);
        tvAuthorcode = (TextView) findViewById(R.id.tv_registfaster_authorcode);
        cbPro = (CheckBox) findViewById(R.id.cb_registfaster_agreen);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvAuthorcode.setOnClickListener(this);
        findViewById(R.id.tv_registfaster_cancle).setOnClickListener(this);
        findViewById(R.id.tv_registfaster_ok).setOnClickListener(this);
        cbPro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                agreen = isChecked;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
            case R.id.tv_registfaster_cancle:
                clearReset();
                finishOk();
                break;
            case R.id.tv_registfaster_authorcode://获得手机验证码
                mobile = etPhone.getText()+"";
                if (!TextUtils.isPhoneNumber(mobile)){
                    showSimpleMessageDialog(R.string.mobile_err);
                    return;
                }
                Map<String ,Object> map = new HashMap<>();
                map.put(Constants.PHONE_NUMBER, mobile);
                postData2(map, FunncoUrls.getAuthorCode(),false);//使用父类的方法需要注册广播进行监听
                break;
            case R.id.tv_registfaster_ok://进行快速注册
                if (isLogining){
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                mobile = etPhone.getText()+"";
                pwd = etPWD_1.getText()+"";
                vcode = etAuthorcode.getText()+"";
                if (!checkData()){
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                if (!TextUtils.equals(pwd, etPWD_2.getText() + "")){
                    showSimpleMessageDialog(R.string.secondps_unsame);
                    return;
                }
                if (!agreen){
                    showSimpleMessageDialog(R.string.weicat_registfaster_agreePro);
                    return ;
                }
                showLoading(parentView);
                //进行网络数据提交
                Map<String,Object> map2 = new HashMap<>();
                map2.put(Constants.TOKEN,token);
                map2.put("openid",openid);
                map2.put("type",type);
                map2.put("mobile",mobile);
                map2.put("pwd",pwd);
                map2.put("vcode",vcode);
                isLogining = true;
                postData2(map2, FunncoUrls.getBindWeCharUrl(), false);
                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getAuthorCode())){
            isSending = true;
            timeStart();
            return;
        }
        dismissLoading();
        isLogining = false;
        UserLoginInfo user = JsonUtils.getObject(JsonUtils.getJObt(result, "params").toString(), UserLoginInfo.class);
        //用户信息进行存储
        try {
            dbUtils.saveOrUpdate(user);
        } catch (DbException e) {
            e.printStackTrace();
        }
        BaseApplication.getInstance().setUser(user);//设置全局user
        SharedPreferencesUtils.setValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.USER_PWD, pwd);
        SharedPreferencesUtils.setUserValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, user);
        startActivity(MainActivity.class);
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        startActivity(LoginActivity.class);
        finishOk();
    }
    private boolean checkData(){
        boolean isOK;
        if (TextUtils.isPhoneNumber(mobile) && !TextUtils.isNull(vcode) && !TextUtils.isNull(pwd)){
            isOK = true;
        }else{
            isOK = false;
        }
        return isOK;
    }

    //重置并清楚所有值
    private void clearReset(){
        mobile = "";
        pwd = "";
        vcode = "";
        etAuthorcode.setText("");
        etPWD_2.setText("");
        etPWD_1.setText("");
        etPhone.setText("");
        cbPro.setChecked(false);
    }

    private void timeStart() {
        tvAuthorcode.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSending){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }
}
