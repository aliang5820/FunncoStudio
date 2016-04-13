package com.funnco.funnco.activity.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.view.switcher.SwitchView;

import java.util.HashMap;
import java.util.Map;

/**
 * 课程循环模式
 * Created by user on 2015/9/25.
 */
public class RepeatCoursesTypeActivity extends BaseActivity implements CheckBox.OnCheckedChangeListener,NumberPicker.OnValueChangeListener{

    private View parentView;
    private Serve courses;
    private Intent intent;
    private String key;
    //底部
    private Button btPrevious, btSave;
    private SwitchView sv;
    private CheckBox cb1,cb2,cb3,cb4,cb5,cb6,cb7;
    //是否有可选同时预约的服务
    private boolean isSameService = false;
    private boolean isTeamService = false;
    private String team_id;
    //是否是修改服务
    private boolean isEditCourses = false;
    private UserLoginInfo user;
    //本页数据
    private String  repeat_type = "0";
    private Map<String ,String> weekMap = new HashMap<>();;
    private Map<String,Object> map = new HashMap<>();
    private StringBuilder weeks = new StringBuilder();
    private String startdate;
    private String enddate;
    //分钟标识
    private int starttime = 9 * 60;
    private int endtime = 18 * 60;

    private PopupWindow popupWindow;
    private Button btnTitle;
    private View timeView,dateView;
    private TextView tvStartDate,tvEndDate,tvWorkTime;
//    private TextView tvTeacherchoose;
    private NumberPicker np1,np2,np3,np4;
    private String stH="09",stM="00",edH="18",edM="00";//工作时间段
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

    private String[] minutes = {"00","30"};
    private String[] hours = new String[24];
    private String[] hours1 = {"09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","00","01","02","03","04","05","06","07","08"};
    private String[] hours2 = {"18","19","20","21","22","23","00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17"};
    private static final int REQUEST_CODE_REPEAD = 2002;

    private static final int REQUEST_CODE_SERVICE_COURSES_ADD = 0xf201;
    private static final int RESULT_CODE_SERVICE_COURSES_ADD = 0xf202;

    private static final int REQUEST_CODE_SERVICE_COURSES_EDIT = 0xf211;
    private static final int RESULT_CODE_SERVICE_COURSES_EDIT = 0xf212;

    private static final int RESULT_CODE_REPEADMODE_COURSES = 0xf612;

    private static final int REQUEST_CODE_COURSES_TEACHERCHOOSE = 0xf03;
    private static final int RESULT_CODE_COURSES_TEACHERCHOOSE = 0xf13;

    private static String FORMAT_1 = "yyyy-MM-dd";


    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_repeadmode, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null){
            key = intent.getStringExtra(KEY);
            isEditCourses = intent.getBooleanExtra("isEdit", false);
            isTeamService = intent.getBooleanExtra("isTeamService", false);
            if (isTeamService){
                team_id = intent.getStringExtra("team_id");
            }
            if (!TextUtils.isNull(key)){
                courses = (Serve) BaseApplication.getInstance().getT(key);
                if (courses != null){
                    BaseApplication.getInstance().removeT(key);
                    LogUtils.e("------", "数据接收正常，，，" + courses);
                }
            }
        }
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_service_addcourses);
        btPrevious = (Button) findViewById(R.id.bt_footer_submit);
        btSave = (Button) findViewById(R.id.bt_footer_cancle);
        btPrevious.setText(R.string.previous);
        btSave.setText(R.string.lanuch);



        user = BaseApplication.getInstance().getUser();
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
//        tvTeacherchoose = (TextView) findViewById(R.id.tv_repeadmode_teacherchoose);
//        tvTeacherchoose.setVisibility(View.VISIBLE);


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
        if (BaseApplication.getInstance().getUser() != null) {
            isSameService = !BaseApplication.getInstance().getUser().getService_count().equals("0");
        }
        sv = (SwitchView) findViewById(R.id.sv_repeadmode_forever);
//        sv.setState(false);

        btPrevious = (Button) findViewById(R.id.bt_footer_submit);
        btPrevious.setText(R.string.previous);
        btSave = (Button) findViewById(R.id.bt_footer_cancle);
        btSave.setText(R.string.lanuch);

        //根据传递过来的Serve对象，设置显示的数据
        //初始化默认值
        if (!isEditCourses){
            courses.setStarttime(starttime + "");
            courses.setEndtime(endtime + "");
            courses.setRepeat_type("0");
        }
        initViewData();
    }

    private void initViewData() {
        if (courses != null) {
            String weekss = courses.getWeeks();
            if (!TextUtils.isNull(weekss)) {
                weekMap.clear();
                weeks.append(weekss);
                String[] ids = weekss.split(",");

                for (int i = 0; i < ids.length; i++) {
                    int id = Integer.parseInt(ids[i]);
                    switch (id) {
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
                    weekMap.put(String.valueOf(id), String.valueOf(id));
                }
            }
            repeat_type = courses.getRepeat_type();
//            if (!TextUtils.isNull(repeat_type)) {
//                sv.setState(repeat_type.equals("1"));
//            }
            if (isEditCourses) {
                stD.setLength(0);
                stD.append(courses.getStartdate());//开始日期
            }
            if (!TextUtils.isNull(stD + "") && stD.length() >= 8) {
                tvStartDate.setText(stD + " " + DateUtils.getDayInWeek(stD + ""));
            }
            edD.setLength(0);
            edD.append(courses.getEnddate());//结束日期
            if (!TextUtils.isNull(edD + "") && edD.length() >= 8) {
                tvEndDate.setText(edD + " " + DateUtils.getDayInWeek(edD + ""));
            }
            String sttime = courses.getStarttime();
            String edtime = courses.getEndtime();
            if (!TextUtils.isNull(sttime) && !TextUtils.isNull(edtime)) {
                starttime = Integer.parseInt(sttime);
                endtime = Integer.parseInt(edtime);
            }
            tvWorkTime.setText(DateUtils.getTime4Minutes(starttime)+" - " + DateUtils.getTime4Minutes(endtime));
        }
    }
    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);//返回
        btPrevious.setOnClickListener(this);//上一步
        btSave.setOnClickListener(this);//数据提交
//        tvTeacherchoose.setOnClickListener(this);//老师选择
        tvWorkTime.setOnClickListener(this);//工作时间
        tvStartDate.setOnClickListener(this);//开始时间
        tvEndDate.setOnClickListener(this);//结束时间

        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        cb4.setOnCheckedChangeListener(this);
        cb5.setOnCheckedChangeListener(this);
        cb6.setOnCheckedChangeListener(this);
        cb7.setOnCheckedChangeListener(this);

        np1.setOnValueChangedListener(this);
        np2.setOnValueChangedListener(this);
        np3.setOnValueChangedListener(this);
        np4.setOnValueChangedListener(this);

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
                courses.setRepeat_type(repeat_type);
            }

            @Override
            public void toggleToOff() {
                sv.toggleSwitch(false);
                repeat_type = 0 + "";
                isForeverService = false;
                tvStartDate.setEnabled(true);
                tvEndDate.setEnabled(true);
                courses.setRepeat_type(repeat_type);
            }
        });
    }
    private void initTimeData() {
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
    private void showPopupWindow(View view){
        if (view != null && parentView != null) {
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setClippingEnabled(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        }
    }
    private boolean dismissPopupWindow(){
        boolean hasDismiss = false;
        for (PopupWindow pw : new PopupWindow[]{popupWindow}){
            if (pw != null && pw.isShowing()){
                pw.dismiss();
                hasDismiss = true;
            }
        }
        return hasDismiss;
    }
    private void resetColor(View view,int color){
        if (view instanceof TextView){
            TextView tv = (TextView) view;
            tv.setTextColor(getResources().getColor(color));
        }else if (view instanceof EditText){
            EditText et = (EditText) view;
            et.setTextColor(getResources().getColor(color));
        }else if (view instanceof Button){
            Button bt = (Button) view;
            bt.setTextColor(getResources().getColor(color));
        }
    }
    private boolean checkData() {
        if (android.text.TextUtils.isEmpty(weeks) || !isDateNormal || ! isTimeNuomal){
            return false;
        }
        if (isForeverService){
            return !android.text.TextUtils.isEmpty(stH) && !android.text.TextUtils.isEmpty(stM) && !android.text.TextUtils.isEmpty(edH) && !android.text.TextUtils.isEmpty(edM);
        }else{
            return !android.text.TextUtils.isEmpty(stD + "") && !android.text.TextUtils.isEmpty(edD + "");
        }
    }
    private boolean getWeeks() {
        weeks.setLength(0);
        if (weekMap.isEmpty()){
            weeks.append("");
            return false;
        }
        for (String key : weekMap.keySet()){
            weeks.append(key+",");
        }
        weeks.deleteCharAt(weeks.length() - 1);
        courses.setWeeks(weeks.toString());
        return true;
    }

    private void setValue(){
        if (courses != null) {
            courses.setStartdate(stD.toString());
            courses.setEnddate(edD.toString());
            courses.setStarttime(starttime + "");
            courses.setEndtime(endtime + "");
            if (getWeeks()) {
                courses.setWeeks(weeks.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
            case R.id.bt_footer_submit://上一步
                setValue();
                BaseApplication.getInstance().setT(key,courses);
                intent.putExtra(KEY,key);
                setResult(RESULT_CODE_REPEADMODE_COURSES, intent);
                finishOk();
                break;
            case R.id.tv_repeadmode_teacherchoose://选择老师
                startActivityForResult(new Intent(Actions.ACTION_TEAMMEMBER),REQUEST_CODE_COURSES_TEACHERCHOOSE);
                break;
            case R.id.tv_repeadmode_begin_time://开始日期
                isStartDate = true;
                isEndDate = false;
                showPopupWindow(dateView);
                break;
            case R.id.tv_repeadmode_end_time://结束日期
                isEndDate = true;
                isStartDate = false;
                showPopupWindow(dateView);
                break;
            case R.id.bt_popupwindow_cancle://日期popupwindow取消按钮
                dismissPopupWindow();
                break;
            case R.id.bt_popupwindow_ok://日期popupwindow确定按钮
                if (isStartDate){
                    tvStartDate.setText(stD.toString()+" "+DateUtils.getDayInWeek(stD+""));//追加周几标识
                    if (edD.length() > 1){
                        if (!DateUtils.isNormalDate(stD+"",edD+"",FORMAT_1)){
                            isDateNormal = false;
                            showToast(R.string.p_fillout_date_time_3);
                            resetColor(tvStartDate,R.color.color_hint_tangerine);
                        }else{
                            courses.setStartdate(stD.toString());
                            isDateNormal = true;
                            resetColor(tvStartDate,R.color.color_hint_gray_light);
                        }
                        resetColor(tvEndDate,R.color.color_hint_gray_light);
                    }
                }
                if (isEndDate){
                    tvEndDate.setText(edD.toString()+" "+DateUtils.getDayInWeek(edD+""));
                    if (stD.length() > 1){
                        if (!DateUtils.isNormalDate(stD+"",edD+"",FORMAT_1)){
                            isDateNormal = false;
                            showToast(R.string.p_fillout_date_time_3);
                            resetColor(tvEndDate,R.color.color_hint_tangerine);
                        }else{
                            courses.setStartdate(edD.toString());
                            isDateNormal = true;
                            resetColor(tvEndDate,R.color.color_hint_gray_light);
                        }
                        resetColor(tvStartDate,R.color.color_hint_gray_light);
                    }
                }
                if (isDurationTime) {
                    tvWorkTime.setText(stH+":"+stM+" - "+edH+":"+edM);
                    starttime = Integer.parseInt(stH) * 60 + (stM.equals("00") ? 0:30);
                    endtime = Integer.parseInt(edH) * 60 + (edM.equals("00") ? 0:30);
                    if (starttime >= endtime){
                        isTimeNuomal = false;
                        showToast(R.string.p_fillout_date_time_2);
                        resetColor(tvWorkTime,R.color.color_hint_tangerine);
                    }else{
                        courses.setStarttime(starttime + "");
                        courses.setEndtime(endtime + "");
                        isTimeNuomal = true;
                        resetColor(tvWorkTime,R.color.color_hint_gray_light);
                    }
                }
                dismissPopupWindow();
                break;
            case R.id.tv_repeadmode_durationtime://时间段
                isDurationTime = true;
                isStartDate = false;
                isEndDate = false;
                showPopupWindow(timeView);
                break;
            case R.id.bt_footer_cancle://进行数据提交
                if (!NetUtils.isConnection(mContext)){
                    showNetInfo();
                    return;
                }
                if (!getWeeks()){
                    showSimpleMessageDialog(R.string.repead_mode_notnull);
                    return;
                }
                if (!checkData()){
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                if (isUploading){
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                //执行提交工作
                //第一步检测数据的合法性
                setValue();

                map.clear();
                if (isEditCourses){
                    map.put("id",courses.getId()+"");
                }
                map.put("service_name",courses.getService_name() + "");
                map.put("numbers",courses.getNumbers() + "");
                map.put("min_numbers",courses.getMin_numbers() + "");
                map.put("remind_time",courses.getRemind_time() + "");
                map.put("description",courses.getDescription()+"");
                map.put("weeks",courses.getWeeks() + "");
                map.put("price", "0");//课程没有价格  为了测试
                if (!isForeverService) {
                    map.put("startdate",courses.getStartdate() + "");
                    map.put("enddate",courses.getEnddate() + "");
                }
                if (isTeamService){
                    map.put("team_id", team_id);
                }
                map.put("repeat_type",courses.getRepeat_type() + "");
                map.put("starttime",courses.getStarttime() + "");
                map.put("endtime",courses.getEndtime() + "");
                map.put("service_type","1");//课程1 服务0
//                map.put("relation",relation+"");
                postData(map);
                break;
        }
    }

    private void postData(Map<String ,Object> map){
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0){
                    LogUtils.e("------","数据上传成功，进行解析。。。");
                    finishOk();
                }else{
                    showSimpleMessageDialog(JsonUtils.getResponseMsg(result)+"");
                }
            }
            @Override
            public void getBitmap(String url, Bitmap bitmap) {}
        }, false,isEditCourses ? FunncoUrls.getEditServiceUrl() : FunncoUrls.getAddServiceUrl()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_COURSES_TEACHERCHOOSE && resultCode == RESULT_CODE_COURSES_TEACHERCHOOSE){
            if (data != null){

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CheckBox cb = (CheckBox) buttonView;
        String key = "0";
        switch (cb.getId()){
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
            weekMap.put(key,key);
        else
            weekMap.remove(key);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker == np1){
            stH = hours1[newVal];
        }else if(picker == np2){
            stM = minutes[newVal];
        }else if(picker == np3){
            edH = hours2[newVal];
        }else {
            edM = minutes[newVal];
        }
    }

    @Override
    protected void finishOk() {
        setResult(isEditCourses ? RESULT_CODE_SERVICE_COURSES_EDIT : RESULT_CODE_SERVICE_COURSES_ADD);
        super.finishOk();
    }
}
