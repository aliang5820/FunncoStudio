package com.funnco.funnco.activity.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Reason;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.url.FunncoUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约取消原因选择
 * Created by user on 2015/6/14.
 */
public class ReasonCancleActivity extends BaseActivity {

    private View parentView;
    private Button btSave;
    private EditText etReasonPerson;
    private ListView lv;
    private CommonAdapter<Reason> adapter;
    private List<Reason> list;
    private String reason_id;
    private String reason_content;
    private Intent intent;
    //取消的预约id
    private String id;
    private boolean conventionCancle = false;
    private static final int RESULT_CODE_OK = 0x1ff000;
    private static final int RESULT_CODE_FAILURE = 0x1ff001;
    private static final String FORMAT_1 = "yyyy年MM月dd日";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
            id = intent.getStringExtra("id");
            conventionCancle = intent.getBooleanExtra("convention", false);
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_reasoncancle, null);
        setContentView(parentView);

    }

    @Override
    protected void initView() {
        list = new ArrayList<>();
        btSave = (Button) findViewById(R.id.llayout_foot);
        btSave.setText(R.string.send);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.reason_cancle_customer);
        etReasonPerson = (EditText) this.findViewById(R.id.et_reasoncancle_personreason);
        lv = (ListView) findViewById(R.id.lv_reasoncancle_list);
        adapter = new CommonAdapter<Reason>(mContext,list,R.layout.layout_item_reasoncancle) {
            @Override
            public void convert(ViewHolder helper, Reason item, int position) {
                helper.setText(R.id.tv_item_reasoncancle_reson, item.getContents() + "");
            }
        };
        lv.setAdapter(adapter);
        getData();
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reason_id = list.get(position).getId();
                reason_content = list.get(position).getContents();
                etReasonPerson.setText(reason_content);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.llayout_foot:
                reason_content = etReasonPerson.getText() + "";
                //执行保存操作
                if(TextUtils.isEmpty(reason_id) || TextUtils.isEmpty(reason_content)){
                    showSimpleMessageDialog(R.string.choose_reason);
                    return;
                }
                Map<String,Object> map = new  HashMap<>();
                map.put("ids",id);
                map.put("reasion_id",reason_id+"");
                map.put("reason_contents",reason_content+"");
                postData2(map, FunncoUrls.getScheduleCancleUrl(),false);
                break;
        }
    }

    public void getData() {
        postData2(null, FunncoUrls.getScheduleCancleReason(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getScheduleCancleReason())){
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
            List<Reason> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Reason.class);
            if (ls != null && ls.size() > 0) {
                list.clear();
                list.addAll(ls);
                adapter.notifyDataSetChanged();
            }
        }else if (url.equals(FunncoUrls.getScheduleCancleUrl())){
            if (conventionCancle) {
                BaseApplication.needRegresh = true;
            }
            showToast(R.string.success);
            setResult(RESULT_CODE_OK);
            finishOk();
        }
    }
}
