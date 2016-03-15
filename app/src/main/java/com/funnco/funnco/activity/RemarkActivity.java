package com.funnco.funnco.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomer;
import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.bean.MyCustomerDate;
import com.funnco.funnco.bean.MyCustomerInfo;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改我的联系人、预约客户信息
 * Created by user on 2015/6/13.
 */
public class RemarkActivity extends BaseActivity {

    //页面基本信息
    private CircleImageView civIcon;
    private EditText etRemarkname,etDesc;
    private TextView tvTruename,tvPhone,tvBirthday;
    //用于保存/初始UI的基本信息
    private String id;
    private String c_uid;
    private String trueName = "";
    private String remarkName = "";
    private String description = "";
    private String mobile = "";
    private String headPic = "";
    private String birthday = "";
    private String birthday2;//显示用

    private PopupWindow popupWindowBirthday;
    private View viewDate;
    private DatePicker dpBirthday;
    private Button btnOk_Date,btnCancle_Date;

    private static final String FORMAT_3 = "yyyy-MM-dd";
    private static final String FORMAT_4 = "yyyy年MM月dd日";
    private static final String SPILATE = "-";
    private StringBuilder sbDate = new StringBuilder((DateUtils.getCurrentYear() - 20)+"-01-01");

    private Intent intent;

    private SQliteAsynchTask task = new SQliteAsynchTask();

    private MyCustomerInfo customerInfo;

    private View parentView;
    //判断是修改还是删除
    private boolean isEdit;
    //是否是时间排序的预约客户
    //数据类型 0 表示我的通讯录(字母排序) 1 表示我的预约客户(时间排序) 2 表示日程传进来
    private int customerType = 0;
    //不填写信息时  提示是否提交
    private boolean isNullUp = false;
    //是否删除对话框
    private AlertDialog.Builder builder;
    //删除跳出的
    private PopupWindow popDelete;
    private static final int RESULT_DELETE_CODE = 0x4202;
    private static int RESULT_CODE_EDIT = 0xf702;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
            customerType = intent.getIntExtra("type",2);
            String key = intent.getStringExtra("key");
            if (!TextUtils.isEmpty(key)){
                customerInfo = (MyCustomerInfo) BaseApplication.getInstance().getT(key);
            }
            BaseApplication.getInstance().removeT(key);
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_remark,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.remark_info);
        civIcon = (CircleImageView) findViewById(R.id.civ_remark_icon);
        tvTruename = (TextView) findViewById(R.id.tv_remark_nickname);
        tvPhone = (TextView) findViewById(R.id.tv_remark_phone);
        tvBirthday = (TextView) findViewById(R.id.tv_remark_birthday);
        etDesc = (EditText) findViewById(R.id.et_remark_desc);
        etRemarkname = (EditText) findViewById(R.id.et_remark_remarkname);

        viewDate = getLayoutInflater().inflate(R.layout.layout_popupwindow_date,null);
        viewDate.findViewById(R.id.np_1).setVisibility(View.GONE);
        viewDate.findViewById(R.id.tv_time_splitor).setVisibility(View.GONE);
        viewDate.findViewById(R.id.np_2).setVisibility(View.GONE);

        dpBirthday = (DatePicker) viewDate.findViewById(R.id.dp_popupwindow_date);
        btnOk_Date = (Button) viewDate.findViewById(R.id.bt_popupwindow_ok);
        btnCancle_Date = (Button) viewDate.findViewById(R.id.bt_popupwindow_cancle);
        popupWindowBirthday = new PopupWindow(viewDate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindowBirthday.setClippingEnabled(true);
        popupWindowBirthday.setOutsideTouchable(true);

        if (customerInfo != null){
            id = customerInfo.getId();
            c_uid = customerInfo.getC_uid();
            trueName = customerInfo.getTruename();
            mobile = customerInfo.getMobile();
            birthday = customerInfo.getBirthday();
            if (!TextUtils.isEmpty(birthday) && birthday.length() == 10){
                birthday2 = DateUtils.getDate(birthday,FORMAT_3,FORMAT_4);
            }
            headPic = customerInfo.getHeadpic();
            remarkName = customerInfo.getRemark_name();
            description = customerInfo.getDescription();
        }
        if (TextUtils.isEmpty(trueName) || TextUtils.equals(trueName,"null")){
            trueName = "";
        }
        if (TextUtils.isEmpty(description) || TextUtils.equals(description,"null")){
            description = "";
        }
        initUI();

        builder = new AlertDialog.Builder(this,R.style.dialog_themen);
        builder.setMessage(R.string.null_up_y_n);
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //进行上传操作

            }
        });
        //对popupWindow进行初始化
        View popView = LayoutInflater.from(mContext).inflate(R.layout.layout_popupwindow_delete_service,null);
        popDelete = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popView.findViewById(R.id.bt_popupwindow_delete).setOnClickListener(this);
        popView.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
        popDelete.setBackgroundDrawable(new BitmapDrawable());
        popDelete.setFocusable(true);
        popDelete.setOutsideTouchable(true);
        popDelete.setClippingEnabled(true);
        //初始化日历控件
        initDatePicker();
    }
    private void initDatePicker(){
        dpBirthday.init(DateUtils.getCurrentYear() - 20, 0, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (sbDate.length() != 0) {
                    sbDate.setLength(0);
                }
                sbDate.append(year + SPILATE);
                if (monthOfYear < 9) {
                    sbDate.append("0" + (monthOfYear + 1) + SPILATE);
                } else {
                    sbDate.append((monthOfYear + 1) + SPILATE);
                }
                if (dayOfMonth < 10) {
                    sbDate.append("0" + dayOfMonth + "");
                } else {
                    sbDate.append(dayOfMonth + "");
                }
            }
        });
        dpBirthday.setMinDate(DateUtils.getDateAgo(840).getTime());
        dpBirthday.setMaxDate(DateUtils.getDateAfter(0).getTime());
    }
    private void initUI(){
        tvTruename.setText(trueName + "");
        tvPhone.setText(mobile + "");
        tvBirthday.setText(birthday2);
        etDesc.setText(description + "");
        etRemarkname.setText(remarkName + "");
        if (TextUtils.isEmpty(headPic)){
            return;
        }
        imageLoader.displayImage(headPic, civIcon, options);
    }
    @Override
    protected void initEvents() {
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        findViewById(R.id.iv_remark_delete_cus).setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvBirthday.setOnClickListener(this);
        btnOk_Date.setOnClickListener(this);
        btnCancle_Date.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.bt_popupwindow_ok://日期选择PopupWindow的确定按钮
                birthday = sbDate.toString();
                birthday2 = DateUtils.getDate(sbDate.toString(), FORMAT_3, FORMAT_4);
                tvBirthday.setText(birthday2);
                dismissPopupwindow();
                break;
            case R.id.bt_popupwindow_delete://删除已预约客户（类似删除好友）
                popDelete.dismiss();
                if (!NetUtils.isConnection(mContext)) {
                    showNetInfo();
                    return;
                }
                SQliteAsynchTask.deleteById(dbUtils, customerType == 0 ? MyCustomer.class : (customerType == 1 ? MyCustomerD.class : MyCustomerDate.class), id);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("id", id);
                postData2(map2, FunncoUrls.getCustomerDelete(), false);
                setResult(RESULT_DELETE_CODE);
                finishOk();
                break;
            case R.id.bt_popupwindow_cancle:
                dismissPopupwindow();
                break;
            case R.id.iv_remark_delete_cus:
                //执行删除操作
                isEdit = false;
                //弹出删除popupwindow
                popDelete.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.llayout_foot:
                //执行保存操作
                isEdit = true;
                remarkName = etRemarkname.getText() + "";
                description = etDesc.getText() + "";
                if (!checkData()) {
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("remark_name", remarkName);
                map.put("birthday", birthday);
                map.put("description", description);
                MyCustomer customer = new MyCustomer(id, "", "", "", remarkName, description, "", "", "");
                MyCustomerDate customerDate = new MyCustomerDate("", "", "", "", description, "", id, remarkName, "", "", "", "", "", "", "");
//                MyCustomerD customerD = new MyCustomerD(id,"","","",remarkName,description,"","","","","","","","");
                if (customerInfo != null) {
                    customerInfo.setRemark_name(remarkName);
                    customerInfo.setBirthday(birthday);
                    customerInfo.setDescription(description);
                }
                //同时修改三处
                task.updateTbyId(dbUtils, MyCustomer.class, id, new String[]{"remark_name", "description"}, customer);
//                task.updateTbyId(dbUtils,MyCustomerD.class,id,new String[]{"remark_name","description"},customerD);
                task.updateTbyId(dbUtils,MyCustomerInfo.class,id,new String[]{"remark_name","description","birthday"},customerInfo);
                task.updateTbyId(dbUtils,MyCustomerDate.class,id,new String[]{"remark_name","description"},customerDate);
                postData2(map, FunncoUrls.getCustomerEdit(), false);
                break;
            case R.id.tv_remark_birthday:
                popupWindowBirthday.showAtLocation(parentView,Gravity.BOTTOM,0,0);
                break;
        }
    }

    private void dismissPopupwindow() {
        for (PopupWindow pw : new PopupWindow[]{popupWindowBirthday,popDelete}){
            if (pw != null && pw.isShowing()){
                pw.dismiss();
            }
        }
    }

    private boolean checkData() {
        return !TextUtils.isEmpty(remarkName);
    }
    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (isEdit) {
            Intent intent2 = new Intent();
//                intent2.putExtra("mycustomer",isCusD ? customerD : customer);
            RemarkActivity.this.setResult(RESULT_CODE_EDIT, intent2);
        }else{
            RemarkActivity.this.setResult(RESULT_DELETE_CODE);
        }
        finishOk();
        if (url.equals(FunncoUrls.getCustomerDelete())){

        }else if (url.equals(FunncoUrls.getCustomerEdit())){

        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
    }
}
