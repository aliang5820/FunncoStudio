package com.funnco.funnco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomerConventation;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.json.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 预约信息
 * Created by user on 2015/8/25.
 */
public class ConventationInfoActivity extends BaseActivity {

    private View parentView;
    private Button btSave;
    private TextView tvServicename;
    private TextView tvConventationtime;
    private TextView tvConventationduration;

    private EditText etConventationprice;
    private EditText etConventationdesc;

    private String conventationPrice;
    private String conventationDesc;

    private static final String key = "myCustomerConventation";
    private static int RESULT_CODE_EDIT_CONCENTATION = 0xf704;
    private Intent intent;

    private MyCustomerConventation myCustomerConventation;
    private boolean isUploading = false;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_conventationinfo, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_conventationinfo);
        btSave = (Button) findViewById(R.id.bt_save);
        tvServicename = (TextView) findViewById(R.id.tv_conventationinfo_servicename);
        tvConventationtime = (TextView) findViewById(R.id.tv_conventationinfo_conventationtime);
        tvConventationduration = (TextView) findViewById(R.id.tv_conventationinfo_conventationdurationtime);
        etConventationprice = (EditText) findViewById(R.id.et_conventationinfo_conventationprice);
        etConventationdesc = (EditText) findViewById(R.id.et_conventationinfo_remark);


        intent = getIntent();
        if (intent != null){
            String key = intent.getStringExtra("key");
            if (!TextUtils.isEmpty(key)){
                myCustomerConventation = (MyCustomerConventation) BaseApplication.getInstance().getT(key);
                initUIData();
            }
            BaseApplication.getInstance().removeT(key);
        }
    }

    private void initUIData() {
        if (myCustomerConventation != null){
            conventationPrice = myCustomerConventation.getPrice();
            conventationDesc = myCustomerConventation.getRemark();

            tvServicename.setText(myCustomerConventation.getService_name()+"");
            tvConventationduration.setText(myCustomerConventation.getDuration()+"小时");
            tvConventationtime.setText(myCustomerConventation.getDates()+"");
            if (!TextUtils.isEmpty(conventationPrice) && !TextUtils.equals("null",conventationPrice) ) {
                etConventationprice.setText(conventationPrice);
            }
            if (!TextUtils.isEmpty(conventationDesc) && !TextUtils.equals("null",conventationDesc) ) {
                etConventationdesc.setText(conventationDesc);
            }
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        btSave.setOnClickListener(this);
        tvConventationduration.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.bt_save://提交
                conventationDesc = etConventationdesc.getText()+"";
                conventationPrice = etConventationprice.getText()+"";
                if (isUploading){
                    showSimpleMessageDialog(R.string.uploading);
                    return;
                }
                Map<String,Object> map = new HashMap<>();
                map.put("id",myCustomerConventation.getId());
                map.put("price",conventationPrice+"");
                map.put("remark",conventationDesc+"");
                post(map);
                break;

        }
    }
    private void post(Map<String,Object> map){
        isUploading = true;
        showLoading(parentView);
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                isUploading = false;
                dismissLoading();
                if (JsonUtils.getResponseCode(result) == 0){
                    //数据上传成功 进行解析
                    showToast(JsonUtils.getResponseMsg(result) + "");
                    myCustomerConventation.setRemark(conventationDesc);
                    myCustomerConventation.setPrice(conventationPrice);
//                    myCustomerConventation.setDuration(Double.parseDouble(conventationDuration));
                    new SQliteAsynchTask().updateTbyId(dbUtils,MyCustomerConventation.class,myCustomerConventation.getId(),myCustomerConventation);//跟新数据库数据
                    BaseApplication.getInstance().setT(key, myCustomerConventation);
                    setResult(RESULT_CODE_EDIT_CONCENTATION);
                    finishOk();
                }else{
                    showToast(JsonUtils.getResponseMsg(result)+"");
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {
                isUploading = false;
                dismissLoading();
            }
        }, false, FunncoUrls.getCustomerConventationEdit()));
    }
    protected void finishOk(){
        super.finishOk();
    }
}
