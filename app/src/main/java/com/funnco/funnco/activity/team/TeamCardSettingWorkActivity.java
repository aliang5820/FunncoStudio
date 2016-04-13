package com.funnco.funnco.activity.team;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.json.JsonUtils;

import java.util.Map;

/**
 * 团队作品添加
 * Created by user on 2015/8/23.
 */
public class TeamCardSettingWorkActivity extends BaseActivity {

    private View parentView;
    private TextView tvJump;
    private TextView tvWorkaddnotify;//不添加图片不影响的提示
    private Button btnComplate,btnAddpic;
    private EditText etPicdesc;
    private GridView gvPic;

    private String strPicdesc;//作品统一描述

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teamcardsettingwork, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_teamcard_setting);
        tvJump = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvJump.setText(R.string.str_pass);
        btnComplate = (Button) findViewById(R.id.bt_save);
        btnComplate.setText(R.string.complete);
        btnAddpic = (Button) findViewById(R.id.bt_teamcardsettingwork_addpic);
        etPicdesc = (EditText) findViewById(R.id.et_teamcardsettingwork_introducation);
        gvPic = (GridView) findViewById(R.id.gv_teamcardsettingwork_memberlist);
        tvWorkaddnotify = (TextView) findViewById(R.id.tv_teamcardsettingwork_notify);

        initAdapter();
    }

    private void initAdapter() {

    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvJump.setOnClickListener(this);
        btnComplate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.tv_headcommon_headr://跳过

                break;
            case R.id.bt_save://保存

                break;
        }
    }
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.bt_teamcardsettingwork_addpic:
                //添加作品

                break;
        }
    }

    private void post(Map<String,Object> map,String url){
        putAsyncTask((AsyTask) AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0) {
                    //数据上传成功！
                    //进行解析处理
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        }, false, url));
    }

}
