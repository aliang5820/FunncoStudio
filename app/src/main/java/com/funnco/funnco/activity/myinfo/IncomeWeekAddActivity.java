package com.funnco.funnco.activity.myinfo;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EdisonZhao on 16/3/31.
 */
public class IncomeWeekAddActivity extends BaseActivity {
    private int currentType = TYPE_IN;//当前类型
    private static final int TYPE_IN = 1;//收入
    private static final int TYPE_OUT = 2;//支出
    private View parentView, saveBtn, inTitle, outTitle, inSwitch, outSwitch;
    private EditText titleEdit, descEdit, numEdit;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_income_add_layout, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_income_add);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.bt_save).setOnClickListener(this);
        inTitle = findViewById(R.id.in_title);
        inSwitch = findViewById(R.id.in_switch);
        outTitle = findViewById(R.id.out_title);
        outSwitch = findViewById(R.id.out_switch);
        titleEdit = (EditText) findViewById(R.id.income_title);
        descEdit = (EditText) findViewById(R.id.income_desc);
        numEdit = (EditText) findViewById(R.id.income_num);
        inSwitch.setOnClickListener(this);
        outSwitch.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.bt_save:
                if (titleEdit.length() > 0 && numEdit.length() > 0 && descEdit.length() > 0) {
                    doTask(titleEdit.getText().toString(), descEdit.getText().toString(), numEdit.getText().toString());
                } else {
                    showToast("请填写完整");
                }
                break;
            case R.id.in_switch:
                //切换到支出
                currentType = TYPE_OUT;
                inTitle.setVisibility(View.GONE);
                inSwitch.setVisibility(View.GONE);
                outTitle.setVisibility(View.VISIBLE);
                outSwitch.setVisibility(View.VISIBLE);
                break;
            case R.id.out_switch:
                //切换到收入
                currentType = TYPE_IN;
                inTitle.setVisibility(View.VISIBLE);
                inSwitch.setVisibility(View.VISIBLE);
                outTitle.setVisibility(View.GONE);
                outSwitch.setVisibility(View.GONE);
                break;
        }
    }

    //新增记账信息
    private void doTask(String title, String desc, String num) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.IN_OUT_TYPE, currentType);
        map.put(Constants.IN_OUT_TITLE, title);
        map.put(Constants.IN_OUT_DESC, desc);
        map.put(Constants.IN_OUT_NUM, num);
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                if (JsonUtils.getResponseCode(result) == 0) {
                    //进行解析
                    //JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                    showSimpleMessageDialog(JsonUtils.getResponseMsg(result));
                } else {
                    showToast(JsonUtils.getResponseMsg(result) + "");
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        }, true, FunncoUrls.getAccountsUrl()));
    }
}