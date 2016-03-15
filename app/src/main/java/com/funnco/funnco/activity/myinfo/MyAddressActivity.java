package com.funnco.funnco.activity.myinfo;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.funnco.funnco.bean.Province;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.lidroid.xutils.DbUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改我的地址
 * Created by user on 2015/5/18.
 * @author Shawn
 */
public class MyAddressActivity extends BaseActivity {

    //标题栏控件
    private Intent intent = null;
    private String title = null;
    //输入框
    private TextView tvCountry = null;
    private TextView tvCity = null;
    private EditText etInfo = null;
    //修改的信息
    private String strCountry = "中国";
    private String strProvinceId = "1";
    private String strProvinceName = "上海";
    private String strInfo = null;
    private DbUtils dbUtils = null;
    //城市数据列表
    private List<Province> list = null;
    //我的地址选择框
    private PopupWindow pwAddress;
    private NumberPicker np;
    private Button btCancle_pw,btOk_pw;
    private String[] citys;
    private View parentView;
    private UserLoginInfo user;

    private boolean isSubmiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_myaddress,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        if (intent != null){
            title = intent.getStringExtra("info");
        }
        user = BaseApplication.getInstance().getUser();
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        if (!TextUtils.isEmpty(title)){
            ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(title);
        }
        //获取控件
        tvCountry = (TextView) findViewById(R.id.tv_myaddress_country);
        tvCity = (TextView) findViewById(R.id.tv_myaddress_city);
        etInfo = (EditText) findViewById(R.id.et_myaddress_info);
        if (user != null){
            strProvinceId = user.getProvince_id()+"";
            strProvinceName = user.getProvince_name()+"";
            strInfo = user.getAddress()+"";
            tvCity.setText(strProvinceName);
            etInfo.setText(strInfo);
        }
        //用于选择城市的PopupWindow
        pwAddress = new PopupWindow(mContext);
        View viewCitys = getLayoutInflater().inflate(R.layout.layout_dialog_valuespicker, null);
        btCancle_pw = (Button) viewCitys.findViewById(R.id.bt_activity_pw_cancle);
        btOk_pw = (Button) viewCitys.findViewById(R.id.bt_activity_pw_ok);
        np = (NumberPicker) viewCitys.findViewById(R.id.np_activity_pw_career);
        pwAddress.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pwAddress.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwAddress.setBackgroundDrawable(new BitmapDrawable());
        pwAddress.setFocusable(true);
        pwAddress.setOutsideTouchable(true);
        pwAddress.setClippingEnabled(true);
        pwAddress.setContentView(viewCitys);

        list = new ArrayList<Province>();
        dbUtils = BaseApplication.getInstance().getDbUtils();
        getData();
    }

    private void initData() {
        citys = new String[list.size()];
        for(int i = 0 ; i < list.size(); i ++){
            citys[i] = list.get(i).getProvince_name();
            LogUtils.e("城市信息是：",""+list.get(i).getProvince_name()+"---"+list.get(i).getId());
        }
        np.setMinValue(0);
        np.setMaxValue(list.size() - 1);
        np.setDisplayedValues(citys);
    }

    @Override
    protected void initEvents() {
        btCancle_pw.setOnClickListener(this);
        btOk_pw.setOnClickListener(this);
        tvCity.setOnClickListener(this);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                strProvinceId = list.get(newVal).getId();
                strProvinceName = list.get(newVal).getProvince_name();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://单击了标题栏的返回按钮
                isSubmiting = false;
                finishOk();
                break;
            case R.id.llayout_foot:
                strInfo = etInfo.getText()+"";
                //接下来处理数据提交操作
                if (!NetUtils.isConnection(mContext)){
                    showNetInfo();
                    return;
                }
                if (isSubmiting){
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                //后续完成
                isSubmiting = true;
                postData();
                break;
            case R.id.bt_activity_pw_cancle:
                if (user != null) {
                    strProvinceId = user.getId();
                    strProvinceName = user.getProvince_name();
                }
                tvCity.setText(strProvinceName);
                pwAddress.dismiss();
                break;
            case R.id.bt_activity_pw_ok:
                user.setProvince_id(strProvinceId+"");
                user.setProvince_name(strProvinceName + "");
                BaseApplication.getInstance().setUser(user);
                tvCity.setText(strProvinceName+"");
                pwAddress.dismiss();
                break;
            case R.id.tv_myaddress_city:
                pwAddress.showAtLocation(parentView, Gravity.BOTTOM, 0 , 0);
                break;
        }
    }

    protected void finishOk() {
        strProvinceName = "";
        strInfo = "";
        strProvinceId = "";
        tvCity.setText("");
        etInfo.setText("");
        super.finishOk();
    }

    private void postData() {
        LogUtils.e("-----", "注册提交时答应的信息：" + strProvinceId + " ," + strInfo);
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.PROVINCE_ID, strProvinceId);
        map.put(Constants.ADDRESS, strInfo);
        postData2(map,FunncoUrls.getUpdateAddressUrl(),false);
    }

    private void getData(){
        try {
            if (dbUtils.tableIsExist(Province.class) && !TextUtils.isEmpty(dbUtils.findFirst(Province.class).getProvince_name())){
                list.addAll(dbUtils.findAll(Province.class));
                initData();
            }else {
                dbUtils.deleteAll(Province.class);
                postData2(null, FunncoUrls.getProvinceListUrl(), false);
            }
        } catch (Exception e){
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getProvinceListUrl())){
            JSONObject object = JsonUtils.getJObt(result, "params");
            JSONArray array = JsonUtils.getJAry(object.toString(), "list");
            list.clear();
            for (int i = 0; i <array.length() ; i++){
                LogUtils.e("","分别解析省份对象是："+array.optJSONObject(i).toString());
                JSONObject object1 = array.optJSONObject(i);
                JSONArray array1 = JsonUtils.getJAry(object1.toString(), "province_list");
                list.addAll(JsonUtils.getObjectArray(array1.toString(), Province.class));
            }
            SQliteAsynchTask.saveOrUpdate(dbUtils,list);
            initData();
        } else if (url.equals(FunncoUrls.getUpdateAddressUrl())){
            isSubmiting = false;
            LogUtils.e("//地址设置成功", "//地址设置成功");
            user.setProvince_id(strProvinceId + "");
            user.setProvince_name(strProvinceName);
            BaseApplication.getInstance().getUser().setProvince_name(strProvinceName);
            BaseApplication.getInstance().getUser().setProvince_id(strProvinceId);
            BaseApplication.getInstance().getUser().setAddress(strInfo);
            new SQliteAsynchTask<UserLoginInfo>().saveOrUpdate(dbUtils,user);
            finishOk();
        }
    }
}