package com.funnco.funnco.activity.myinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/6/13.
 */
public class MyPhoneActivity extends BaseActivity {

    private EditText etTels;
    private Intent intent;
    private String title;
    private String phones;
    private boolean isSubmiting = false;

    private UserLoginInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
            title = intent.getStringExtra("info");
        }
        setContentView(R.layout.layout_activity_myphone);

    }

    @Override
    protected void initView() {
        user = BaseApplication.getInstance().getUser();
        etTels = (EditText) findViewById(R.id.et_myphone_tels);
        if (title != null) {
            ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(title);
        }
        if (user != null){
            phones = user.getWork_phone()+"";
            if (!TextUtils.isNull(phones)) {
                etTels.setText(phones);
            }
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.llayout_bottom_save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                isSubmiting = false;
                finishOk();
                break;
            case R.id.llayout_bottom_save://进行保存
                if (!NetUtils.isConnection(mContext)){
                    showNetInfo();
                    return;
                }
                //执行上传保存电话的操作
                phones = etTels.getText()+"";
                if (phones.contains("，")){
                    showSimpleMessageDialog(R.string.p_fillout_char_en);
                    return;
                }
                if (!TextUtils.isPhoneNumber(phones, ",")){
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                if (isSubmiting){
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                isSubmiting = true;
                Map<String, Object> map = new HashMap<>();
                map.put("work_phone",phones);
                savePhone(map,false);
                break;
        }
    }

    private void savePhone(Map<String, Object> map,boolean isGet){
        AsyTask task = new AsyTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                //数据上传成功处理逻辑
                String msg = JsonUtils.getResponseMsg(result);
                    if (JsonUtils.getResponseCode(result) == 0){
                        isSubmiting = false;
                        showToast(R.string.success);
                        if (BaseApplication.getInstance().getUser() != null) {
                            BaseApplication.getInstance().getUser().setWork_phone(phones);
                        }
                        user.setWork_phone(phones);
                        if (dbUtils != null){
                            try {
                                dbUtils.saveOrUpdate(user);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                        finish();
                    }else {
                        showSimpleMessageDialog(msg+"");
                    }
            }

            @Override
            public void getBitmap(String rul,Bitmap bitmap) {
                isSubmiting = false;
            }
        },isGet);
        task.execute(FunncoUrls.getAddMobile());
        putAsyncTask(task);
    }
}
