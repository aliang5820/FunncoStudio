package com.funnco.funnco.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.switcher.SwitchView;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务循环模式
 * Created by user on 2015/6/14.
 */
public class RepeadModeActivity extends BaseActivity implements CheckBox.OnCheckedChangeListener, NumberPicker.OnValueChangeListener {

    //底部
    private Button btPrevious, btSave;
    private View parentView;
    private SwitchView sv;
    private CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7;
    //是否有可选同时预约的服务
    private boolean isSameService = false;
    //是否是修改服务
    private boolean isEditService = false;
    //是否是团队服务
    private boolean isTeamService = false;
    private String teamId ;

    private UserLoginInfo user;

    //本页数据
    private String repeat_type = "0";
    private Map<String, String> weekMap;
    private StringBuilder weeks = new StringBuilder();
    private String startdate;
    private String enddate;
    //分钟标识
    private int starttime = 9 * 60;
    private int endtime = 18 * 60;

    //上一步传入的数据
    private Intent intent;
    private Serve serve;

    private PopupWindow pwDate;
    private PopupWindow pwTime;
    private Button btnTitle;
    private View timeView, dateView;
    private TextView tvStartDate, tvEndDate, tvWorkTime;
    private NumberPicker np1, np2, np3, np4;
    private String stH = "09", stM = "00", edH = "18", edM = "00";//工作时间段
    private StringBuilder stD = new StringBuilder(DateUtils.getCurrentDate());//开始日期
    private StringBuilder edD = new StringBuilder(DateUtils.getCurrentDate());//结束日期
    private DatePicker dp;
    private boolean isStartDate = false;
    private boolean isEndDate = false;
    private boolean isDurationTime = false;
    //开始结束日期是否合法
    private boolean isDateNormal = true;
    //工作时间段是否合法
    private boolean isTimeNuomal = true;
    //是否是永久服务
    private boolean isForeverService = false;
    //是否正在提交
    private boolean isUploading = false;

    private String[] minutes = {"00", "30"};
    private String[] hours = new String[24];
    private String[] hours1 = {"09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "00", "01", "02", "03", "04", "05", "06", "07", "08"};
    private String[] hours2 = {"18", "19", "20", "21", "22", "23", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17"};
    private static final int REQUEST_CODE_REPEAD = 2002;

    private static final int REQUEST_CODE_SERVICE_ADD = 0xf201;
    private static final int RESULT_CODE_SERVICE_ADD = 0xf202;

    private static final int REQUEST_CODE_SERVICE_EDIT = 0xf211;
    private static final int RESULT_CODE_SERVICE_EDIT = 0xf212;

    private static String FORMAT_1 = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null) {
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)){
                serve = (Serve) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
            }
            isEditService = intent.getBooleanExtra("isEditService", false);
            isTeamService = intent.getBooleanExtra("isTeamService", false);
            teamId = intent.getStringExtra("team_id");
            LogUtils.e("", "接收到的Serve:" + serve);
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_repeadmode, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        user = BaseApplication.getInstance().getUser();
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.lanuch_service);
        cb1 = (CheckBox) findViewById(R.id.cb_repeadmode_mon);
        cb2 = (CheckBox) findViewById(R.id.cb_repeadmode_tue);
        cb3 = (CheckBox) findViewById(R.id.cb_repeadmode_wed);
        cb4 = (CheckBox) findViewById(R.id.cb_repeadmode_thu);
        cb5 = (CheckBox) findViewById(R.id.cb_repeadmode_fri);
        cb6 = (CheckBox) findViewById(R.id.cb_repeadmode_sat);
        cb7 = (CheckBox) findViewById(R.id.cb_repeadmode_sun);

        tvStartDate = (TextView) findViewById(R.id.tv_repeadmode_begin_time);
        tvEndDate = (TextView) findViewById(R.id.tv_repeadmode_end_time);
        tvWorkTime = (TextView) findViewById(R.id.tv_repeadmode_durationtime);

        timeView = getLayoutInflater().inflate(R.layout.layout_popupwindow_time_duration, null);
        dateView = getLayoutInflater().inflate(R.layout.layout_popupwindow_date_common, null);
        btnTitle = (Button) dateView.findViewById(R.id.bt_popupwindow_title);
        pwDate = new PopupWindow(dateView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pwTime = new PopupWindow(timeView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pwDate.setOutsideTouchable(true);
        pwDate.setClippingEnabled(true);
        pwTime.setOutsideTouchable(true);
        pwTime.setClippingEnabled(true);

        timeView.findViewById(R.id.bt_popupwindow_ok).setOnClickListener(this);
        timeView.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
        dateView.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
        dateView.findViewById(R.id.bt_popupwindow_ok).setOnClickListener(this);

        np1 = (NumberPicker) timeView.findViewById(R.id.np_1);
        np2 = (NumberPicker) timeView.findViewById(R.id.np_2);
        np3 = (NumberPicker) timeView.findViewById(R.id.np_3);
        np4 = (NumberPicker) timeView.findViewById(R.id.np_4);
        dp = (DatePicker) dateView.findViewById(R.id.dp_popupwindow_date);
        initTimeData();


//        isSameService = !BaseApplication.getInstance().isFirstLaunchService() && BaseApplication.getInstance().getT("service_list") !=null;
        if (BaseApplication.getInstance().getUser() != null) {
            isSameService = !BaseApplication.getInstance().getUser().getService_count().equals("0");
        }

        weekMap = new HashMap<>();
        sv = (SwitchView) findViewById(R.id.sv_repeadmode_forever);
        sv.setState(false);
        btPrevious = (Button) findViewById(R.id.bt_footer_submit);
        btPrevious.setText(R.string.previous);
        btSave = (Button) findViewById(R.id.bt_footer_cancle);
        btSave.setText(R.string.lanuch);

        //根据传递过来的Serve对象，设置显示的数据
        initServeDate();
    }

    private void initServeDate() {
        if (serve != null) {
            LogUtils.e("重复类型选择获得的服务对象是：", "" + serve);
            if (weeks.length() > 0) {
                weeks.setLength(0);
                weekMap.clear();
            }
            String weekes = serve.getWeeks();//循环日期
            if (!TextUtils.isNull(weekes)) {
                weeks.append(weekes);
                String[] day = weekes.split(",");
                for (String d : day) {
                    weekMap.put(d, d);
                    if (!TextUtils.isNull(d)) {
                        switch (Integer.parseInt(d)) {
                            case 0:
                                cb7.setChecked(true);
                                break;
                            case 1:
                                cb1.setChecked(true);
                                break;
                            case 2:
                                cb2.setChecked(true);
                                break;
                            case 3:
                                cb3.setChecked(true);
                                break;
                            case 4:
                                cb4.setChecked(true);
                                break;
                            case 5:
                                cb5.setChecked(true);
                                break;
                            case 6:
                                cb6.setChecked(true);
                                break;
                        }
                    }
                }
            }
            repeat_type = serve.getRepeat_type();//重复类型
            if (!TextUtils.isNull(repeat_type)) {
                if(repeat_type.equals("0")){ //不重复
                    sv.setState(false);
                    tvStartDate.setEnabled(true);
                    tvEndDate.setEnabled(true);
                } else {//永久重复
                    sv.setState(true);
                    tvStartDate.setEnabled(false);
                    tvEndDate.setEnabled(false);
                }
            }
            //开始结束日期
            String startD = serve.getStartdate();
            String endD = serve.getEnddate();
            if (!TextUtils.isNull(startD)) {
                stD.setLength(0);
                stD.append(startD);
                tvStartDate.setText(startD + "" + DateUtils.getDayInWeek(startD));
            }
            if (!TextUtils.isNull(endD)) {
                edD.setLength(0);
                edD.append(endD);
                tvEndDate.setText(endD + "" + DateUtils.getDayInWeek(endD));
            }
            //工作区间
            String startT = serve.getStarttime();
            String endT = serve.getEndtime();
            String st = null, et = null;
            if (!TextUtils.isNull(startT)) {
                starttime = Integer.parseInt(startT);
                st = DateUtils.getTime4Minutes(starttime);
            }
            if (!TextUtils.isNull(endT)) {
                endtime = Integer.parseInt(endT);
                et = DateUtils.getTime4Minutes(endtime);
            }
            if (!TextUtils.isNull(st) && !TextUtils.isNull(et)) {
                tvWorkTime.setText(st + "-" + et);
            }
        } else {//服务对象为空时
            resetData();
        }
    }

    private void initTimeData() {
//        for (int i = 0; i < 24; i ++){
//            hours[i] = i<=9 ?"0"+i : i+"";
//        }
        np1.setMinValue(0);
        np1.setMaxValue(23);
        np1.setDisplayedValues(hours1);
        np2.setMinValue(0);
        np2.setMaxValue(1);
        np2.setDisplayedValues(minutes);
        np3.setMinValue(0);
        np3.setMaxValue(23);
        np3.setDisplayedValues(hours2);
        np4.setMinValue(0);
        np4.setMaxValue(1);
        np4.setDisplayedValues(minutes);
    }

    @Override
    protected void initEvents() {
        btPrevious.setOnClickListener(this);
        btSave.setOnClickListener(this);
        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        cb4.setOnCheckedChangeListener(this);
        cb5.setOnCheckedChangeListener(this);
        cb6.setOnCheckedChangeListener(this);
        cb7.setOnCheckedChangeListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        tvWorkTime.setOnClickListener(this);
        np1.setOnValueChangedListener(this);
        np2.setOnValueChangedListener(this);
        np3.setOnValueChangedListener(this);
        np4.setOnValueChangedListener(this);
        /**
         * 日期选项卡的监听事件
         */
        btnTitle.setText("" + DateUtils.getDayInWeek(DateUtils.getCurrentDate()));
        dp.init(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), DateUtils.getCurrentDay(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (isStartDate) {
                    if (stD.length() != 0) {
                        stD.setLength(0);
                    }
                    stD.append(year + "-");
                    stD.append((monthOfYear + 1) + "-");
                    stD.append(dayOfMonth + "");
                }
                if (isEndDate) {
                    if (edD.length() != 0) {
                        edD.setLength(0);
                    }
                    edD.append(year + "-");
                    edD.append((monthOfYear + 1) + "-");
                    edD.append(dayOfMonth + "");
                }

                btnTitle.setText("" + DateUtils.getDayInWeek(isStartDate ? stD.toString() : edD.toString()));
            }
        });
        dp.setMinDate(DateUtils.getDateAgo(499).getTime());
        dp.setMaxDate(DateUtils.getDateAfter(499).getTime());
        /**
         * 循环模式开关监听器
         */
        sv.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                sv.toggleSwitch(true);
                repeat_type = 1 + "";
                isForeverService = true;
                tvStartDate.setEnabled(false);
                tvEndDate.setEnabled(false);
            }

            @Override
            public void toggleToOff() {
                sv.toggleSwitch(false);
                repeat_type = 0 + "";
                isForeverService = false;
                tvStartDate.setEnabled(true);
                tvEndDate.setEnabled(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl://返回
                finish();
                break;
            case R.id.bt_footer_submit://发布
                finish();
                break;
            case R.id.tv_repeadmode_begin_time://开始日期
                isStartDate = true;
                pwDate.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.tv_repeadmode_end_time://结束日期
                isEndDate = true;
                pwDate.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.bt_popupwindow_cancle://日期popupwindow取消按钮
                dismissPopupWindow();
                break;
            case R.id.bt_popupwindow_ok://日期popupwindow确定按钮
                if (isStartDate) {
                    tvStartDate.setText(stD.toString() + "" + DateUtils.getDayInWeek(stD + ""));//追加周几标识
                    if (edD.length() > 1) {
                        if (!DateUtils.isNormalDate(stD + "", edD + "", FORMAT_1)) {
                            isDateNormal = false;
                            showToast(R.string.p_fillout_date_time_3);
                            resetColor(tvStartDate, R.color.color_hint_tangerine);
                        } else {
                            isDateNormal = true;
                            resetColor(tvStartDate, R.color.color_hint_gray_light);
                        }
                        resetColor(tvEndDate, R.color.color_hint_gray_light);
                    }
                }
                if (isEndDate) {
                    tvEndDate.setText(edD.toString() + "" + DateUtils.getDayInWeek(edD + ""));
                    if (stD.length() > 1) {
                        if (!DateUtils.isNormalDate(stD + "", edD + "", FORMAT_1)) {
                            isDateNormal = false;
                            showToast(R.string.p_fillout_date_time_3);
                            resetColor(tvEndDate, R.color.color_hint_tangerine);
                        } else {
                            isDateNormal = true;
                            resetColor(tvEndDate, R.color.color_hint_gray_light);
                        }
                        resetColor(tvStartDate, R.color.color_hint_gray_light);
                    }
                }
                if (isDurationTime) {
                    tvWorkTime.setText(stH + ":" + stM + " - " + edH + ":" + edM);
                    starttime = Integer.parseInt(stH) * 60 + (stM.equals("00") ? 0 : 30);
                    endtime = Integer.parseInt(edH) * 60 + (edM.equals("00") ? 0 : 30);
                    if (starttime >= endtime) {
                        isTimeNuomal = false;
                        showToast(R.string.p_fillout_date_time_2);
                        resetColor(tvWorkTime, R.color.color_hint_tangerine);
                    } else {
                        isTimeNuomal = true;
                        resetColor(tvWorkTime, R.color.color_hint_gray_light);
                    }
                }
                dismissPopupWindow();
                break;
            case R.id.tv_repeadmode_durationtime://时间段
                isDurationTime = true;
                pwTime.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.bt_footer_cancle://进行数据提交
                if (!NetUtils.isConnection(mContext)) {
                    showNetInfo();
                    return;
                }
                if (!getWeeks()) {
                    showSimpleMessageDialog(R.string.repead_mode_notnull);
                    return;
                }
                if (!checkData()) {
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                if (isUploading) {
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                //执行提交工作
                //第一步检测数据的合法性
                Map<String, Object> map = new HashMap<>();
                if (isEditService) {
                    map.put("id", serve.getId() + "");
                }
                map.put("service_name", serve.getService_name() + "");
                map.put("duration", serve.getDuration() + "");
                map.put("numbers", serve.getNumbers() + "");
                map.put("price", serve.getPrice() + "");
                map.put("description", serve.getDescription());
                map.put("weeks", weeks.toString());
                if (!isForeverService) {
//                        map.put("startdate", startdate);
//                        map.put("enddate", enddate);
                    map.put("startdate", stD + "");
                    map.put("enddate", edD + "");
                }
                map.put("repeat_type", String.valueOf(repeat_type));
                map.put("starttime", starttime + "");
                map.put("endtime", endtime + "");
                map.put("relation", serve.getRelations() + "");
                map.put("service_type", 0 + "");//课程1 服务0
                if (isTeamService){
                    map.put("team_id",teamId+"");
                }else{
                    map.remove("team_id");
                }
                isUploading = true;
                postData2(map, isEditService ? FunncoUrls.getEditServiceUrl() : FunncoUrls.getAddServiceUrl(), false);
                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        isUploading = false;
        //修改是否有数据
        if (user.getService_count().equals("0")) {
            if (BaseApplication.getInstance().getUser() != null) {
                BaseApplication.getInstance().getUser().setService_count("1");
            }
            user.setService_count("1");
        }
        showToast(R.string.success);
        setResult(isEditService ? RESULT_CODE_SERVICE_EDIT : RESULT_CODE_SERVICE_ADD);
        finishOk();
    }

    private void resetColor(View view, int color) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextColor(getResources().getColor(color));
        } else if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setTextColor(getResources().getColor(color));
        } else if (view instanceof Button) {
            Button bt = (Button) view;
            bt.setTextColor(getResources().getColor(color));
        }
    }

    private boolean checkData() {
        if (TextUtils.isNull(weeks.toString()) || !isDateNormal || !isTimeNuomal) {
            return false;
        }
        if (isForeverService) {
            return !TextUtils.isNull(stH) && !TextUtils.isNull(stM) && !TextUtils.isNull(edH) && !TextUtils.isNull(edM);
        } else {
            return !TextUtils.isNull(stD + "") && !TextUtils.isNull(edD + "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REPEAD && resultCode == RESULT_OK) {//标识提交成功
            setResult(RESULT_OK);
            finish();
        }
        //否则停留在该页面
    }

    /**
     * 关闭PopupWindow
     */
    private void dismissPopupWindow() {
        if (pwTime != null && pwTime.isShowing()) {
            pwTime.dismiss();
        }
        if (pwDate != null && pwDate.isShowing()) {
            pwDate.dismiss();
        }
        isStartDate = false;
        isEndDate = false;
        isDurationTime = false;
    }

    private boolean getWeeks() {
        weeks.setLength(0);
        if (weekMap.isEmpty()) {
            weeks.append("");
            return false;
        }
        for (String key : weekMap.keySet()) {
            weeks.append(key + ",");
        }
        weeks.deleteCharAt(weeks.length() - 1);
        return true;
    }

    protected void finishOk() {
        resetData();
        setEmptyDate();
        super.finishOk();
    }

    private void resetData() {
        tvEndDate.setText(R.string.enddate);
        tvStartDate.setText(R.string.begindate);
        tvWorkTime.setText("09:00-18:00");
        sv.setState(false);
        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);
        cb4.setChecked(false);
        cb5.setChecked(false);
        cb6.setChecked(false);
        cb7.setChecked(false);

    }

    private void setEmptyDate() {
        intent = null;
        isUploading = false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CheckBox cb = (CheckBox) buttonView;
        String key = "0";
        switch (cb.getId()) {
            case R.id.cb_repeadmode_mon:
                key = "1";
                break;
            case R.id.cb_repeadmode_tue:
                key = "2";
                break;
            case R.id.cb_repeadmode_wed:
                key = "3";
                break;
            case R.id.cb_repeadmode_thu:
                key = "4";
                break;
            case R.id.cb_repeadmode_fri:
                key = "5";
                break;
            case R.id.cb_repeadmode_sat:
                key = "6";
                break;
            case R.id.cb_repeadmode_sun:
                key = "0";
                break;
        }
        if (isChecked)
            weekMap.put(key, key);
        else
            weekMap.remove(key);
    }

    /**
     * 自定义事件的监听事件
     *
     * @param picker
     * @param oldVal
     * @param newVal
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker == np1) {
            stH = hours1[newVal];
        } else if (picker == np2) {
            stM = minutes[newVal];
        } else if (picker == np3) {
            edH = hours2[newVal];
        } else {
            edM = minutes[newVal];
        }
    }
}