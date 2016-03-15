package com.funnco.funnco.activity.work;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.WorkItem;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.http.VolleyUtils;
import com.lidroid.xutils.BitmapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 编辑作品的类
 * Created by user on 2015/5/29.
 */
public class UpdateWorkActivity extends BaseActivity {

    private Intent intent;
    private WorkItem item;
    private boolean isTeamwork = false;
    private String team_id;
    private ImageView imageView;
    private EditText etTitle;
    private EditText etDesc;
    //需要提交的信息i
    private String strTitle;
    private String strDesc;

    //
    private VolleyUtils util;
    BitmapUtils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
//            item = intent.getParcelableExtra("item");
            String key = intent.getStringExtra(KEY);
            if (!com.funnco.funnco.utils.string.TextUtils.isNull(key)){
                item = (WorkItem) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
            }
            isTeamwork = intent.getBooleanExtra("isTeamwork", false);
            if (isTeamwork){
                team_id = intent.getStringExtra("team_id");
            }
            LogUtils.e("updateWorkActivity，接收到的Itmet:",""+item);
        }
        util = new VolleyUtils(this);
        utils = new BitmapUtils(this, Environment.getExternalStorageDirectory().getAbsolutePath()+"/Funnco");
        LogUtils.e("", "  Runtime.getRuntime().freeMemory()/4===");
        setContentView(R.layout.layout_activity_updatework);
    }

    @Override
    protected void initView() {
        etTitle = (EditText) findViewById(R.id.et_updatework_title);
        etDesc = (EditText) findViewById(R.id.et_updatework_description);
        imageView = (ImageView) findViewById(R.id.iv_updatework_image);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.updatework);

        findViewById(R.id.bt_save).setOnClickListener(this);
        if (item != null){
            strTitle = item.getTitle()+"";
            strDesc = item.getDescription()+"";
            if (TextUtils.isEmpty(strTitle) || TextUtils.equals("null",strTitle)){
                strTitle = "";
            }
            if (TextUtils.isEmpty(strDesc) || TextUtils.equals("null",strDesc)){
                strDesc = "";
            }
            etTitle.setText(strTitle);
            etDesc.setText(strDesc);
            utils.display(imageView, item.getPic_sm());
        }
    }

    @Override
    protected void initEvents() {
    }
    private Map<String,Object> map = new HashMap<>();
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回按钮
                finish();
                break;
            case R.id.bt_save://提交按钮
                if (!NetUtils.isConnection(mContext)){
                    showNetInfo();
                    return;
                }
                strDesc = etDesc.getText()+"";
                strTitle = etTitle.getText()+"";
                map.clear();
                map.put("id", item.getId());
                if (isTeamwork){
                    map.put("team_id", team_id);
                }
                map.put("title", strTitle);
                map.put("description", strDesc);
                postData2(map, FunncoUrls.getEditWorkUrl(), false);

                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getEditWorkUrl())){
            //修改返回的对象的值
            item.setTitle(strTitle);
            item.setDescription(strDesc);
            intent.putExtra(KEY, "item");
            BaseApplication.getInstance().setT("item",item);
//                intent.putExtra("item",item);
            setResult(103, intent);
            finishOk();
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
    }
}
