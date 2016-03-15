package com.funnco.funnco.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.lidroid.xutils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class GetPasswordActivity extends BaseActivity {
    private final static String TAG = GetPasswordActivity.class.getSimpleName();
    private EditText etPhonenumber = null;
    private String strPhonenumber = null;
    private ImageView ivSend = null;
    private HttpUtils utils = null;
    private Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_get_password);
    }

    @Override
    protected void initView() {
        etPhonenumber = (EditText) findViewById(R.id.et_getpassword_phonenumber);
        ivSend = (ImageView) findViewById(R.id.iv_getpassword_send);
        utils = new HttpUtils();
        intent = getIntent();
    }
    @Override
    protected void initEvents() {}

    public void btnClick(View view){
        switch (view.getId()){
            case R.id.iv_getpassword_send:
                ivSend.setEnabled(false);
                //进行提交数据发送新密码
                strPhonenumber = etPhonenumber.getText()+"";
                //进行验证
                //网络提交
                if(TextUtils.isEmpty(strPhonenumber) || strPhonenumber.length()<11 || !strPhonenumber.startsWith("1")){
                    showToast("请正确填写手机号");
                    return;
                }
                Map<String ,Object> map = new HashMap<>();
                map.put(Constants.PHONE_NUMBER, strPhonenumber);
                postData2(map,FunncoUrls.getFindPasswordUrl(),false);
                break;
            case R.id.iv_getpassword_cancle:
                //啥事不做直接finish()
                finishOk();
                break;
            default:
                break;
        }

    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        intent.putExtra(Constants.PHONE_NUMBER, strPhonenumber);
        showToast(R.string.p_fillout_sendok);
        setResult(RESULT_OK, intent);
        GetPasswordActivity.this.finish();
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        ivSend.setEnabled(true);//密码获取失败设置可以点击
//        showSimpleMessageDialog(msg+"");
    }

    @Override
    public void onClick(View v) {}
}
