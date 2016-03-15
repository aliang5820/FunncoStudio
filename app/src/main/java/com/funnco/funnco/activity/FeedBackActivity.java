package com.funnco.funnco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;

import java.util.HashMap;
import java.util.Map;

/**
 * 意见反馈
 * Created by user on 2015/5/18.
 * @author Shawn
 */
public class FeedBackActivity extends BaseActivity {

    //标题控件
    private Intent intent = null;
    private String title = null;

    //修改内容控件
    private EditText etFeed = null;
    private String strFeed = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.layout_activity_feedback);
    }

    @Override
    protected void initView() {
        if (intent != null){
            title = intent.getStringExtra("info");
        }
        if (!TextUtils.isEmpty(title)){
            ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(title);
        }
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.bt_save).setOnClickListener(this);
        etFeed = (EditText) findViewById(R.id.et_feedback_content);
    }

    @Override
    protected void initEvents() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_save:
                strFeed = etFeed.getText()+"";
                if (TextUtils.isEmpty(strFeed)){
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                Map<String ,Object> map = new HashMap<>();
                map.put(Constants.CONTENTS, strFeed);
                postData2(map, FunncoUrls.getFeedBackUrl(), false);
                break;
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }
    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        showToast(R.string.success);
        finishOk();
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        showToast(R.string.failue);
    }
}
