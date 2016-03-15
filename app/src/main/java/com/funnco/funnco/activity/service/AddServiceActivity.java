package com.funnco.funnco.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.string.TextUtils;

/**
 * 添加服务
 * Created by user on 2015/5/20.
 * @author Shawn
 */
public class AddServiceActivity extends BaseActivity {
    private TextView tvTitle;
//    private TextView tvTeamtype;
    private Button btNext;
    private EditText etServicename = null;
    private TextView tvServicetime = null;
    private EditText etServicecount = null;
    private EditText etServiceprice = null;
    private EditText etServiceDescribe = null;
    //可同时进行的服务
    private TextView tvTogetherCount;
    private static final int REQUEST_CODE_TOGETHER = 0x2001;
    //可同时预约服务回退码
    private static final int RESULT_CODE_CANCLE = 0xff02;

    private static final int REQUEST_CODE_SERVICE_ADD = 0xf201;
    private static final int RESULT_CODE_SERVICE_ADD = 0xf202;

    private static final int REQUEST_CODE_SERVICE_EDIT = 0xf211;
    private static final int RESULT_CODE_SERVICE_EDIT = 0xf212;
    //成员选择
    private static final int REQUEST_CODE_MEMBERCHOOSE = 0xf06;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;

    private String strServicename = "";
    private String strServicetime = "";
    private String strServicecount = "";
    private String strServiceprice = "";
    private String strServicedescribe = "";
    //同时进行的服务
    private String relation = "";
    private String inputMsg = "";
    private Intent intent = null;
    private Serve serve = null;
    //是否是修改服务
    private boolean isEditService = false;
    //是否是团队服务
    private boolean isTeamService = false;
    //时间选择的PopupWindow
    private PopupWindow pwTime;
    private View viewTime;
    private Button btnOk_Time = null;
    private Button btnCancle_Time = null;
    private NumberPicker np1,np2;
    private String[] hours;
    private String[] minutes = {"00","30"};
    private String hour = "00";
    private String minute = "00";
    int duration = 0;
    private String team_id = "";
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        view = getLayoutInflater().inflate(R.layout.layout_activity_addservice, null);
        setContentView(view);
//        initView();
//        initEvents();
    }

    @Override
    protected void initView() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        btNext = (Button)findViewById(R.id.llayout_foot);
        btNext.setText(R.string.next);
        etServicename = (EditText) findViewById(R.id.et_addservice_servicename);
        tvServicetime = (TextView) findViewById(R.id.tv_addservice_servicetime);
        etServicecount = (EditText) findViewById(R.id.et_addservice_servicecount);
        etServiceprice = (EditText) findViewById(R.id.et_addservice_serviceprice);
        etServiceDescribe = (EditText) findViewById(R.id.et_addservice_servicedescribe);
        tvTogetherCount = (TextView) findViewById(R.id.tv_addservice_togethercount);
        //标题
        tvTitle = (TextView) findViewById(R.id.tv_headcommon_headm);
        tvTitle.setText(R.string.lanuch_service);
//        tvTeamtype = (TextView) findViewById(R.id.tv_addservice_teamtype);
        viewTime = getLayoutInflater().inflate(R.layout.layout_popupwindow_time,null);
        pwTime = new PopupWindow(viewTime, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnOk_Time = (Button) viewTime.findViewById(R.id.bt_popupwindow_ok);
        btnCancle_Time = (Button) viewTime.findViewById(R.id.bt_popupwindow_cancle);
        pwTime.setClippingEnabled(true);
        pwTime.setOutsideTouchable(true);

        np1 = (NumberPicker) viewTime.findViewById(R.id.np_1);
        np2 = (NumberPicker) viewTime.findViewById(R.id.np_2);
        hours = new String[24];
        for(int i = 0 ;i < 24; i ++){
            hours[i] = i<10 ?"0"+i : i+"";
        }
        np1.setMinValue(0);
        np1.setMaxValue(23);
        np2.setMinValue(0);
        np2.setMaxValue(1);
        np1.setDisplayedValues(hours);
        np2.setDisplayedValues(minutes);

        if (intent != null){
            tvTitle.setText(intent.getStringExtra("title") + "");
            isTeamService = intent.getBooleanExtra("isTeamService", false);
            isEditService = intent.getBooleanExtra("isEditService", false);
            team_id = intent.getStringExtra("team_id");
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)){
                serve = (Serve) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
            }
            if (isEditService && serve != null){
                LogUtils.e("funnco","得到的服务对象信息是："+serve);
                initData();
            }else{
                serve = new Serve();
            }
        }
    }

    //修改数据时 先设置值
    private void initData() {
        if (serve == null){
            return;
        }
        etServicename.setText(serve.getService_name());
        duration = Integer.parseInt(serve.getDuration());
        tvServicetime.setText((duration / 60) + "时" + (duration % 60) + "分");
        etServicecount.setText(serve.getNumbers());
        etServiceDescribe.setText(serve.getDescription());
        etServiceprice.setText(serve.getPrice());
        relation = serve.getRelations();
        refreshData();
        tvTitle.setText(getResources().getString(R.string.updateservice));
//        tvTeamtype.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {
        tvServicetime.setOnClickListener(this);
        btnCancle_Time.setOnClickListener(this);
        btnOk_Time.setOnClickListener(this);
        btNext.setOnClickListener(this);
        tvTogetherCount.setOnClickListener(this);

        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hour = hours[newVal];
            }
        });
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minute = minutes[newVal];
            }
        });
    }

    private boolean checkData() {
        if (TextUtils.isNull(strServicename)){
            inputMsg = "服务名称不能为空！";
            return false;
        }
        if (duration == 0){
            inputMsg = "请正确选择时间！";
            return false;
        }
        if ((!TextUtils.isNull(strServiceprice) && Integer.valueOf(strServiceprice) <0) ||
                (!TextUtils.isNull(strServiceprice) && strServiceprice.length() >9)){
            inputMsg = "请正确填写价格！";
            return false;
        }
        if (TextUtils.isNull(strServicecount) || duration == 0 || TextUtils.isNull(strServicename)){
            return false;
        }
        if (serve != null){
            serve.setService_name(strServicename);
            serve.setDuration(duration+"");
            serve.setPrice(strServiceprice);
            serve.setNumbers(strServicecount);
            serve.setDescription(strServicedescribe+"");
            serve.setRelations(relation+"");
        }
        return true;
    }
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.tv_addschedule_teamtype:
//                Intent intent =
//                intent.putExtra("ids",ids);
//                intent.putExtra("team_id",team_id);
                startActivityForResult(new Intent(Actions.ACTION_CHOOSE_MEMBER), REQUEST_CODE_MEMBERCHOOSE);
                break;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.llayout_foot://下一步
                strServicename = etServicename.getText()+"";
                strServicecount = etServicecount.getText()+"";
                strServicedescribe = etServiceDescribe.getText()+"";
                strServiceprice  = etServiceprice.getText()+"";
                if(checkData()){
                    //此时的提交时间一直下一页  甚至下下一页
//                    postData();
                    Intent intent2 = new Intent(mContext, RepeadModeActivity.class);
//                    //此处要传递一系列的值
                    intent2.putExtra("isEditService",isEditService);//新增字段 判断是否是修改服务
                    intent2.putExtra("isTeamService",isTeamService);//2.1新增字段 是否是添加的团队服务
                    if (isTeamService) {
                        intent2.putExtra("team_id", team_id + "");
                    }
                    intent2.putExtra(KEY, "serve");
                    BaseApplication.getInstance().setT("serve", serve);
                    LogUtils.e("funnco","向下传递的serve 是："+serve);
                    startActivityForResult(intent2, isEditService ? REQUEST_CODE_SERVICE_EDIT : REQUEST_CODE_SERVICE_ADD);
                }else{
                    showSimpleMessageDialog(inputMsg+"");
                }
                break;
            case R.id.tv_addservice_servicetime:
                pwTime.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.bt_popupwindow_ok:
                dissPopupWindow();
                tvServicetime.setText(hour+"小时"+minute+"分钟");
                //计算出时长
                duration = Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
                break;
            case R.id.bt_popupwindow_cancle:
                dissPopupWindow();
                break;
            case R.id.tv_addservice_togethercount:
             //选择可同时进行的服务
                Intent intent = new Intent(AddServiceActivity.this, ServiceTogetherChooseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("serve", isEditService ? serve : null);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_TOGETHER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TOGETHER && resultCode == RESULT_OK){//选择同时预约服务返回的数据
            if (data != null){
                relation = data.getStringExtra("relation")+"";
                if (serve != null) {
                    serve.setRelations(relation + "");
                }
                refreshData();
                LogUtils.e("AddServiceActivity得到的可同时服务的项是：",""+relation);
            }
        } else if (requestCode == REQUEST_CODE_TOGETHER && resultCode == RESULT_CODE_CANCLE){
            if (serve != null) {
                relation = serve.getRelations();
            }else {
                relation = "";
            }
            refreshData();
        }else if (requestCode == REQUEST_CODE_SERVICE_ADD && resultCode == RESULT_CODE_SERVICE_ADD){
            setResult(RESULT_CODE_SERVICE_ADD);
            finishOk();
        }else if (requestCode == REQUEST_CODE_SERVICE_EDIT && resultCode == RESULT_CODE_SERVICE_EDIT){
            setResult(RESULT_CODE_SERVICE_EDIT);
            finishOk();
        }/*else if (requestCode == REQUEST_CODE_MEMBERCHOOSE && resultCode == RESULT_CODE_MEMBERCHOOSE){
            //团队成员选择完毕
            if (data != null){
                ids = data.getStringExtra("ids");
                team_id = data.getStringExtra("team_id");
                String team_name = data.getStringExtra("team_name");
                LogUtils.e("------","成员选择后的到的数据shi："+ids+" team_id:"+team_id+"  team_name:"+team_name);
//                if (!TextUtils.isEmpty(ids)) {
//                    ids = ids.trim();
//                    tvTeamtype.setText(team_name+"("+(ids.split(",").length)+"人)");
//                }else{
//                    tvTeamtype.setText("自己");
//                }
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshData() {
        if (!com.funnco.funnco.utils.string.TextUtils.isNull(relation)) {
            int index = relation.split(",").length;
            tvTogetherCount.setText("已选"+index+"项");
        }else{

            tvTogetherCount.setText("已选0项");
        }
    }

    private void dissPopupWindow() {
        if(pwTime != null && pwTime.isShowing()){
            pwTime.dismiss();
        }
    }

    protected void finishOk(){
        etServicename.setText("");
        tvServicetime.setText("");
        etServicecount.setText("");
        etServiceprice.setText("");
        etServiceDescribe.setText("");
        strServicename = "";
        duration = 0;
        strServicecount = "";
        strServiceprice = "";
        strServicedescribe = "";
        relation = "";
        intent = null;
        isEditService = false;
//        serve = new Serve();
        refreshData();
        super.finishOk();
    }
}
