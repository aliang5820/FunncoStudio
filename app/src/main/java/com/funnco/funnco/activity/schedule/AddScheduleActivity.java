package com.funnco.funnco.activity.schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.RepeatEventTypeActivity;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.bean.FunncoEvent;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.dialog.DateTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 添加临时事件
 * Created by user on 2015/5/22.
 *
 * @author Shawn
 */
public class AddScheduleActivity extends BaseActivity {
    private TextView tvTeamtype;
    private EditText etEventContent = null;
    private CheckBox cbAllDay = null;
    private TextView tvRepeadType;
    private TextView tvBeginDate, tvEndDate;
    private EditText etRemark;
    private int tvFlag;

    //添加临时事件
    private static final int REQUEST_CODE_SCHEDULE_EDIT = 0xff03;
    private static final int RESULT_CODE_SCHEDULE_EDIT = 0xff13;
    //修改日程
    private static final int REQUEST_CODE_SCHEDULE_ADD = 0xff04;
    private static final int RESULT_CODE_SCHEDULE_ADD = 0xff14;
    //重复事件类型
    private static final int REQUEST_CODE_SCHEDULE_REPEADTYPE = 0xff05;
    private static final int RESULT_CODE_SCHEDULE_REPEADTYPE = 0xff15;
    //成员选择
    private static final int REQUEST_CODE_MEMBERCHOOSE = 0xf06;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;

    //是否是全天事件
    private boolean isAllDayEvent = false;
    private boolean isBeginDate = false;
    private boolean isEndDate = false;
    private boolean isDate = false;
    private boolean isNormal = true;

    private static final String FORMAT_1 = "yyyy年MM月dd日 HH:mm";
    private static final String FORMAT_2 = "yyyy-MM-dd HH:mm";
    private static final String FORMAT_3 = "yyyy-MM-dd";
    private static final String FORMAT_4 = "yyyy年MM月dd日";
    private static final String SPILATE = "-";
    private static final String SPILATE_2 = " ";

    //需要提交数据
    private String eventContent = null;

    //用于上传的时间  格式  yyyy-MM-dd HH:mm
    private String beginDate = DateUtils.getCurrentDate(FORMAT_3) + " 09:00";//默认开始结束的时间也是有值的
    private String endDate = DateUtils.getCurrentDate(FORMAT_3) + " 18:00";
    //用于显示的时间  格式  yyyy年MM月dd日 HH:mm
    private String beginDate_S = DateUtils.getDate(beginDate, FORMAT_2, FORMAT_1);//用于显示的开始时间
    private String endDate_S = DateUtils.getDate(endDate, FORMAT_2, FORMAT_1);//用于显示的结束时间
    private String remarkDesc;

    private NumberPicker np1, np2;
    private TextView tvsplitor;
    private String[] hours;
    private String[] minutes = {"00", "30"};
    private String hour = "00";
    private String minute = "00";
    private String repeadType = "0";
    private String team_id = "";
    private String ids = "";
    //重复事件的类型
    private String[] repeadArr;

    private PopupWindow popupWindowDate;
    private View viewDate;
    //PopupWindow按钮
    private Button btnTitle;
    private Button btnOk_Date = null;
    private Button btnCancle_Date = null;

    private DatePicker datePicker = null;
    //临时存储时间  如论是开始时间还是结束时间  格式 yyyy-MM-dd  一种10位
    private StringBuilder sbDate = new StringBuilder(beginDate.substring(0, 10));//默认有初始值的

    private Intent intent = null;
    private FunncoEvent funncoEvent = null;
    private boolean isEditScheduleEvent = false;
    private boolean isUploading = false;
    //    private boolean isRepead = false;
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_addschedule, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null) {
            funncoEvent = intent.getParcelableExtra("schedule");
            LogUtils.e("-----", "添加到的日程数据是：" + funncoEvent);
        }
        tvTeamtype = (TextView) findViewById(R.id.tv_addschedule_teamtype);
        etEventContent = (EditText) findViewById(R.id.et_addschedule_eventcontent);
        cbAllDay = (CheckBox) findViewById(R.id.cb_addschedule_eventtype);
        tvRepeadType = (TextView) findViewById(R.id.tv_addschedule_repeattype);
        tvEndDate = (TextView) findViewById(R.id.tv_addschedule_endtime);
        tvBeginDate = (TextView) findViewById(R.id.tv_addschedule_begintime);
        tvFlag = tvBeginDate.getPaintFlags();
        etRemark = (EditText) findViewById(R.id.et_addschedule_remark);
        repeadArr = getResources().getStringArray(R.array.array_type_repeat);

        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(getResources().getString(funncoEvent == null ? R.string.addtempevent : R.string.scheduledetails));
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        //此处好药处理日期问题
        viewDate = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow_date, null);
        np1 = (NumberPicker) viewDate.findViewById(R.id.np_1);
        tvsplitor = (TextView) viewDate.findViewById(R.id.tv_time_splitor);
        np2 = (NumberPicker) viewDate.findViewById(R.id.np_2);
        hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = i < 10 ? "0" + i : i + "";
        }
        np1.setMinValue(0);
        np1.setMaxValue(23);
        np2.setMinValue(0);
        np2.setMaxValue(1);
        np1.setDisplayedValues(hours);
        np2.setDisplayedValues(minutes);

        datePicker = (DatePicker) viewDate.findViewById(R.id.dp_popupwindow_date);
        btnTitle = (Button) viewDate.findViewById(R.id.bt_popupwindow_title);
        btnOk_Date = (Button) viewDate.findViewById(R.id.bt_popupwindow_ok);
        btnCancle_Date = (Button) viewDate.findViewById(R.id.bt_popupwindow_cancle);
        popupWindowDate = new PopupWindow(viewDate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindowDate.setClippingEnabled(true);
        popupWindowDate.setOutsideTouchable(true);

        if (funncoEvent == null) {//添加事件
            isEditScheduleEvent = false;
            cbAllDay.setChecked(false);
            etEventContent.setText("");
            etRemark.setText("");
            tvBeginDate.setText(beginDate_S + "");
            tvEndDate.setText(endDate_S + "");
            tvTeamtype.setVisibility(View.VISIBLE);
        } else {//修改事件
            isEditScheduleEvent = true;
            etEventContent.setText(funncoEvent.getTitle() + "");
            beginDate = funncoEvent.getStarttime();
            endDate = funncoEvent.getEndtime();
            remarkDesc = funncoEvent.getRemark();
            repeadType = funncoEvent.getRepeat_type();
            tvTeamtype.setVisibility(View.GONE);
            //重复类型
            if (DateUtils.isBeginOfDay(beginDate) && DateUtils.isEndOfDay(endDate)) {
                cbAllDay.setChecked(true);
                isAllDayEvent = true;
                endDate_S = DateUtils.getDate(endDate.substring(0, 10), FORMAT_3, FORMAT_4) + " 24:00";
            } else {
                cbAllDay.setChecked(false);
                isAllDayEvent = false;
                endDate_S = DateUtils.getDate(endDate, FORMAT_2, FORMAT_1);
            }

            beginDate_S = DateUtils.getDate(beginDate, FORMAT_2, FORMAT_1);

            tvBeginDate.setText(beginDate_S + "" + DateUtils.getDayInWeek(beginDate_S, FORMAT_1));
            tvEndDate.setText(endDate_S + "" + DateUtils.getDayInWeek(endDate_S, FORMAT_1));
            etRemark.setText(remarkDesc + "");
            if (TextUtils.isEmpty(repeadType) || TextUtils.equals("null", repeadType + "")) {
                repeadType = "0";
                tvRepeadType.setText(repeadArr[0]);
            } else {
                if (repeadType.equals("1")) {
                    tvRepeadType.setText(repeadArr[1]);
                    datePicker.setVisibility(View.GONE);
                } else if (repeadType.equals("2")) {
                    tvRepeadType.setText(repeadArr[2]);
                    datePicker.setVisibility(View.GONE);
                } else {
                    tvRepeadType.setText(repeadArr[0]);
                    datePicker.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        cbAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAllDayEvent = isChecked;
                np1.setVisibility(isAllDayEvent ? View.GONE : View.VISIBLE);
                np2.setVisibility(isAllDayEvent ? View.GONE : View.VISIBLE);
                tvsplitor.setVisibility(isAllDayEvent ? View.GONE : View.VISIBLE);
                if (isAllDayEvent) {
                    endDate = beginDate.substring(0, 10) + SPILATE_2 + "23:59";
                    endDate_S = DateUtils.getDate(beginDate.substring(0, 10), FORMAT_3, FORMAT_4) + SPILATE_2 + "23:59";
                    tvEndDate.setText(endDate_S + DateUtils.getDayInWeek(endDate_S, FORMAT_1));

                    beginDate = beginDate.substring(0, 10) + SPILATE_2 + "00:00";
                    beginDate_S = DateUtils.getDate(beginDate.substring(0, 10), FORMAT_3, FORMAT_4) + SPILATE_2 + "00:00";
                    tvBeginDate.setText(beginDate_S +  DateUtils.getDayInWeek(beginDate_S, FORMAT_1));
                    //修改完日期 回复颜色 并且修改为合法数据
                    tvBeginDate.setTextColor(getResources().getColor(R.color.color_hint_gray_light));
                    tvEndDate.setTextColor(getResources().getColor(R.color.color_hint_gray_light));
                    tvBeginDate.setPaintFlags(tvFlag);
                    tvEndDate.setPaintFlags(tvFlag);
                    isNormal = true;
                }

            }
        });
        //初始化日期控件
        initDatePicker();
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hour = hours[newVal];
                LogUtils.e("YEAR+++++++", "hour:" + hour + " minute:" + minute);
            }
        });
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minute = minutes[newVal];
                LogUtils.e("YEAR+++++++", "hour:" + hour + " minute:" + minute);
            }
        });

        popupWindowDate.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isEndDate = false;
            }
        });
        btnCancle_Date.setOnClickListener(this);
        tvRepeadType.setOnClickListener(this);
        btnOk_Date.setOnClickListener(this);
        tvBeginDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);


    }

    private void check() {
        isBeginDate = false;
        isEndDate = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_popupwindow_ok://日期选择PopupWindow的确定按钮
                if (isBeginDate) {//开始时间
                    if (isAllDayEvent) {//全天事件
                        beginDate = sbDate.substring(0, 10) + SPILATE_2 + "00:00";
                        endDate = sbDate.substring(0, 10) + SPILATE_2 + "24:00";
                        endDate_S = DateUtils.getDate(sbDate.substring(0, 10), FORMAT_3, FORMAT_4) + SPILATE_2 + "24:00";
                    } else {
                        beginDate = sbDate.substring(0, 10) + SPILATE_2 + hour + ":" + minute;
                        if (DateUtils.compareDate(endDate, beginDate, FORMAT_2) <= 0) {
                            endDate = DateUtils.getDateNextHour(beginDate);
                            endDate_S = DateUtils.getDate(endDate, FORMAT_2, FORMAT_1);
                            tvEndDate.setText(endDate_S + "");
                        }
                    }
                } else if (isEndDate) {//结束时间
                    if (isAllDayEvent) {
                        endDate = sbDate.substring(0, 10) + SPILATE_2 + "24:00";
                        endDate_S = DateUtils.getDate(sbDate.substring(0, 10) + "", FORMAT_3, FORMAT_4) + SPILATE_2 + "24:00";///////
                    } else {
                        endDate = sbDate.substring(0, 10) + SPILATE_2 + hour + ":" + minute;
                        endDate_S = DateUtils.getDate(endDate + "", FORMAT_2, FORMAT_1);//日期格式进行转换yyyy-MM-dd转换成yyyy年MM月dd日
                    }
                }
                beginDate_S = DateUtils.getDate(beginDate, FORMAT_2, FORMAT_1);
                tvBeginDate.setText(beginDate_S + DateUtils.getDayInWeek(beginDate_S, FORMAT_1));
                tvEndDate.setText(endDate_S + DateUtils.getDayInWeek(endDate_S, FORMAT_1));
                if (DateUtils.compareDate(endDate, beginDate, FORMAT_2) <= 0) {
                    tvBeginDate.setTextColor(getResources().getColor(R.color.color_hint_tangerine));
                    tvEndDate.setTextColor(getResources().getColor(R.color.color_hint_tangerine));
                    isNormal = false;
                    tvBeginDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    tvEndDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    showToast(R.string.p_fillout_date_time_3);
                } else {
                    tvBeginDate.setTextColor(getResources().getColor(R.color.color_hint_gray_light));
                    tvEndDate.setTextColor(getResources().getColor(R.color.color_hint_gray_light));
                    isNormal = true;
                    tvBeginDate.setPaintFlags(tvFlag);
                    tvEndDate.setPaintFlags(tvFlag);
                }
                tvBeginDate.invalidate();
                tvEndDate.invalidate();
                dissPopupWindow();
                check();
                break;
            case R.id.bt_popupwindow_cancle:
//                initDatePicker();
                dissPopupWindow();
                break;
            case R.id.llayout_foot://进行上传
                if (!NetUtils.isConnection(mContext)) {
                    showNetInfo();
                    return;
                }
                if (!isNormal) {
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                if (isUploading) {
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                eventContent = etEventContent.getText() + "";
                //需要处理参数问题
                remarkDesc = etRemark.getText() + "";
                if (!checkPostData()) {
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                //上传
                isUploading = true;
                postData();
                break;
            case R.id.tv_headcommon_headl://点击了返回按钮
                finishOk();
                break;
            case R.id.tv_addschedule_begintime://开始日期
                /*datePicker.setVisibility(View.VISIBLE);
                popupWindowDate.showAtLocation(parentView, Gravity.BOTTOM,0 ,0);
                isBeginDate = true;
                isEndDate = false;*/

                /*showDateDialog();
                selectDate();*/
                showDateDialog(new IDateCallBack() {
                    @Override
                    public void callBack(Calendar calendar) {
                        if (isAllDayEvent) {
                            calendar.set(Calendar.HOUR_OF_DAY, 23);
                            calendar.set(Calendar.MINUTE, 59);
                            tvEndDate.setText(DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_1) + "星期" + DateUtils.getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1,  "星期"));
                            endDate = DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_2);
                        } else {
                            tvBeginDate.setText(DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_1) + "星期" + DateUtils.getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1,  "星期"));
                            beginDate = DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_2);
                        }
                        checkTime();
                    }
                });
                break;
            case R.id.tv_addschedule_endtime://结束日期
                /*if (repeadType.equals("1")){
                    datePicker.setVisibility(View.GONE);
                }else if (repeadType.equals("2")){
                    datePicker.setVisibility(View.GONE);
                }else{
                    datePicker.setVisibility(View.VISIBLE);
                }
                isEndDate = true;
                isBeginDate = false;
                if (!repeadType.equals("0") && isAllDayEvent){
                    return;
                }
                popupWindowDate.showAtLocation(parentView, Gravity.BOTTOM,0 ,0);*/
                showDateDialog(new IDateCallBack() {
                    @Override
                    public void callBack(Calendar calendar) {
                        if (isAllDayEvent) {
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            tvBeginDate.setText(DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_1) + "星期" + DateUtils.getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1,  "星期"));
                            beginDate = DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_2);
                            calendar.set(Calendar.HOUR_OF_DAY, 23);
                            calendar.set(Calendar.MINUTE, 59);
                            tvEndDate.setText(DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_1) + "星期" + DateUtils.getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1,  "星期"));
                            endDate = DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_2);
                        } else {
                            tvEndDate.setText(DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_1) + "星期" + DateUtils.getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1,  "星期"));
                            endDate = DateUtils.getDate(calendar.getTimeInMillis(), FORMAT_2);
                        }
                        checkTime();
                    }
                });
                break;
            case R.id.tv_addschedule_repeattype://重复类型
                Intent intent = new Intent(AddScheduleActivity.this, RepeatEventTypeActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE_REPEADTYPE);
                break;
        }
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_addschedule_teamtype:
                Intent intent = new Intent(Actions.ACTION_CHOOSE_MEMBER);
                intent.putExtra("ids", ids);
                intent.putExtra("team_id", team_id);
                startActivityForResult(intent, REQUEST_CODE_MEMBERCHOOSE);
                break;
        }
    }

    private void initDatePicker() {
        btnTitle.setText("" + DateUtils.getDayInWeek("" + DateUtils.getCurrentDate()));
        datePicker.init(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth(), DateUtils.getCurrentDay(), new DatePicker.OnDateChangedListener() {
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
                btnTitle.setText("" + DateUtils.getDayInWeek(sbDate + ""));
            }
        });
        datePicker.setMinDate(DateUtils.getDateAgo(1).getTime());
        datePicker.setMaxDate(DateUtils.getDateAfter(12).getTime());
    }

    private boolean checkPostData() {
        if (isAllDayEvent) {
            if (!TextUtils.isEmpty(eventContent)) {
                return true;
            }
        } else {
            if (!TextUtils.isEmpty(eventContent) && !TextUtils.isEmpty(beginDate) && !TextUtils.isEmpty(endDate)) {
                return true;
            }
        }
        return false;
    }

    private void dissPopupWindow() {
        if (popupWindowDate != null && popupWindowDate.isShowing()) {
            popupWindowDate.dismiss();
        }
    }

    private void postData() {
        Map<String, Object> map = new HashMap<>();
        map.put("contents", eventContent + "");
        map.put("starttime", beginDate);
        map.put("endtime", endDate);
        map.put("remark", remarkDesc + "");
        map.put("repeat_type", repeadType);//新添加的重复事件
        if (isEditScheduleEvent && funncoEvent != null) {
            map.put("id", funncoEvent.getId() + "");
        }
        if (!TextUtils.isEmpty(ids) && !TextUtils.isEmpty(team_id)) {
            map.put("team_uid", ids + "");
            map.put("team_id", team_id + "");
        }
        postData2(map, isEditScheduleEvent ? FunncoUrls.getEditScheduleUrl() : FunncoUrls.getAddShceduleUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        Intent intent2 = new Intent();
        if (!TextUtils.isEmpty(beginDate) && beginDate.length() == 16) {
            LogUtils.e("符合要求开始存入值", "date:" + String.valueOf(beginDate.split(SPILATE_2)[0]));
            intent2.putExtra("date", String.valueOf(beginDate.split(SPILATE_2)[0]));
        }
        if (url.equals(FunncoUrls.getEditScheduleUrl())) {
            AddScheduleActivity.this.setResult(RESULT_CODE_SCHEDULE_EDIT, intent2);
        } else if (url.equals(FunncoUrls.getAddShceduleUrl())) {
            AddScheduleActivity.this.setResult(RESULT_CODE_SCHEDULE_ADD, intent2);
        }
        finishOk();
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
    }


    protected void finishOk() {
        //需要处理的问题
        funncoEvent = null;
        etEventContent.setText("");
        etRemark.setText("");
        isUploading = false;
        super.finishOk();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCHEDULE_REPEADTYPE && resultCode == RESULT_CODE_SCHEDULE_REPEADTYPE) {
            if (data != null) {
                String repeadName = data.getStringExtra("repeat_name");
                repeadType = data.getStringExtra("repeat_type");
                if (!repeadType.equals("0")) {//有循环
                    //pw日期进行隐藏，并且修改需要上传的结束时间
                    if (endDate.length() == 16) {
//                        endDate = beginDate.substring(0, 10) + endDate.substring(11, 16);
                        endDate = beginDate.split(SPILATE_2)[0] + SPILATE_2 + endDate.split(SPILATE_2)[1];
                    }
                    endDate_S = DateUtils.getDate(beginDate.split(SPILATE_2)[0], FORMAT_3, FORMAT_4) + SPILATE_2 + endDate.split(SPILATE_2)[1];
                    tvEndDate.setText(endDate_S + "");
                }
                if (!TextUtils.isEmpty(repeadName)) {
                    tvRepeadType.setText(repeadName + "");
                }
            }
        } else if (requestCode == REQUEST_CODE_MEMBERCHOOSE && resultCode == RESULT_CODE_MEMBERCHOOSE) {
            //团队成员选择完毕
            if (data != null) {
                ids = data.getStringExtra("ids");
                team_id = data.getStringExtra("team_id");
                String team_name = data.getStringExtra("team_name");
                LogUtils.e("------", "成员选择后的到的数据shi：" + ids + " team_id:" + team_id + "  team_name:" + team_name);
                if (!TextUtils.isEmpty(ids)) {
                    ids = ids.trim();
                    tvTeamtype.setText(team_name + "(" + (ids.split(",").length) + "人)");
                } else {
                    tvTeamtype.setText("自己");
                }
            }
        }
    }

    private void checkTime() {
        if (DateUtils.compareDate(endDate, beginDate, FORMAT_2) <= 0) {
            tvBeginDate.setTextColor(getResources().getColor(R.color.color_hint_tangerine));
            tvEndDate.setTextColor(getResources().getColor(R.color.color_hint_tangerine));
            isNormal = false;
            tvBeginDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvEndDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            showToast(R.string.p_fillout_date_time_3);
        } else {
            tvBeginDate.setTextColor(getResources().getColor(R.color.color_hint_gray_light));
            tvEndDate.setTextColor(getResources().getColor(R.color.color_hint_gray_light));
            isNormal = true;
            tvBeginDate.setPaintFlags(tvFlag);
            tvEndDate.setPaintFlags(tvFlag);
        }
    }

    //选择日期
    private void selectDate(IDateCallBack callBack) {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR); //获取当前年份
        int mMonth = calendar.get(Calendar.MONTH);//获取当前月份
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
        new DatePickerDialog(this, new DateListener(callBack), mYear, mMonth, mDay).show();//调用
    }

    //选择时间
    private void selectTime(Calendar calendar, IDateCallBack callBack) {
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
        int mMinute = calendar.get(Calendar.MINUTE);//获取当前的分钟数
        new TimePickerDialog(this, new TimeListener(calendar, callBack), mHour, mMinute, true).show();//调用
    }

    private class DateListener implements DatePickerDialog.OnDateSetListener {

        private IDateCallBack callBack;

        public DateListener(IDateCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            //调用完日历控件点完成后干的事
            Calendar calendar = Calendar.getInstance();

            if (isAllDayEvent) {
                calendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                callBack.callBack(calendar);
            } else {
                calendar.set(year, monthOfYear, dayOfMonth);
                selectTime(calendar, callBack);
            }
        }
    }

    private class TimeListener implements TimePickerDialog.OnTimeSetListener {

        private Calendar calendar;
        private IDateCallBack callBack;

        public TimeListener(Calendar calendar, IDateCallBack callBack) {
            this.calendar = calendar;
            this.callBack = callBack;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //调用完日历控件点完成后干的事
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
            callBack.callBack(calendar);
        }
    }

    interface IDateCallBack {
        void callBack(Calendar calendar);
    }


    /**
     * 新的时间选择器
     */
    public void showDateDialog(final IDateCallBack dateCallBack) {
        DateTimePickerDialog dialog = new DateTimePickerDialog(this, System.currentTimeMillis());
        /**
         * 实现接口
         */
        dialog.setOnDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener() {
            public void OnDateTimeSet(AlertDialog dialog, long date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);
                LogUtils.e(TAG, "您输入的日期是：" + getStringDate(date));
                dateCallBack.callBack(calendar);
            }
        });
        dialog.show();
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     */
    public static String getStringDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

}
