package com.funnco.funnco.activity.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.login.LoginActivity;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/5/18.
 * @author Shawn
 */
public class UpdatePasswordActivity extends BaseActivity implements TextWatcher {

    //标题控件
    private Intent intent;
    private String title;

    //信息填写控件
    private EditText etOldpassword;
    private EditText etNewpassword;
    private EditText etNewpassword2;

    private String strOldpassword;
    private String strNewpassword;
    private String strNewpassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.layout_activity_updatepassword);
    }

    @Override
    protected void initView() {
        if (intent != null){
            title = intent.getStringExtra("info");
        }
        if (!TextUtils.isEmpty(title)){
            ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(title);
        }

        //对内容控件进行获取
        etOldpassword = (EditText) findViewById(R.id.et_updatepassword_oldpwd);
        etNewpassword = (EditText) findViewById(R.id.et_updatepassword_newpwd);
        etNewpassword2 = (EditText) findViewById(R.id.et_updatepassword_newpwd2);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headm).setOnClickListener(this);
        findViewById(R.id.llayout_foot).setOnClickListener(this);

        etOldpassword.addTextChangedListener(this);
        etNewpassword.addTextChangedListener(this);
        etNewpassword2.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llayout_foot://提交按钮
                strOldpassword = etOldpassword.getText()+"";
                strNewpassword2 = etNewpassword2.getText()+"";
                strNewpassword = etNewpassword.getText()+"";
                if (TextUtils.isEmpty(strOldpassword) || TextUtils.isEmpty(strNewpassword)){
                    showSimpleMessageDialog(R.string.p_fillout_passwd);
                    return;
                }
                if(!strNewpassword2.equals(strNewpassword)){
                    showSimpleMessageDialog(R.string.secondps_unsame);
                    etNewpassword2.setText("");
                    etNewpassword.setText("");
                    return;
                }
                Map<String ,Object> map = new HashMap<String ,Object>();
                map.put(Constants.OLD_PWD, strOldpassword);
                map.put(Constants.NEW_PWD, strNewpassword);
                postData2(map, FunncoUrls.getUpdatePassworddUrl(), false);
                break;
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        intent.setClass(UpdatePasswordActivity.this,LoginActivity.class);
        showToast(R.string.p_fillout_updateok_login);
        intent.putExtra("pw",strNewpassword);
        startActivity(intent);
        finishOk();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {}
}
