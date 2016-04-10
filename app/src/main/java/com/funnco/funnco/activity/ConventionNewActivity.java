package com.funnco.funnco.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.EnableTime;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.layout.CheckableFrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加新预约
 * Created by user on 2015/9/18.
 */
public class ConventionNewActivity extends BaseActivity {

    private View parentView;
    private Button btAddconvention;
    private TextView tvTeamtype;
    private TextView tvServicename;
    private TextView tvTime;
    private TextView tv_conventionnew_timepoint;
    private EditText etCustomerName;
    private EditText etCustomerMobile;
    private EditText etCustomerDesc;
    private GridView gridView;
    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<>();

    private PopupWindow popupWindow;
    private View viewDate;
    private DatePicker datePicker;
    private Button btnTitle;
    private Button btnCancle_Date;
    private Button btnOk_Date;
    private String FORMAT_0 = "yyyy-MM-dd";
    private String FORMAT_1 = "yyyy年MM月dd日";
    private StringBuilder sbDate;
    private String sbDate_S = "";
    private String SPILATE = "-";

    private Serve serveSelected;
    private String team_id = "";
    private String time;
    private String ids = "";
    private String customerName;
    private String customerMobile;
    private String customerDesc;
    private Map<String, Object> map = new HashMap<>();
    private String msg = "";
    //    private Serve serve;
    //成员选择
    private static final int REQUEST_CODE_MEMBERCHOOSE = 0xf06;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;

    //服务选择
    private static final int REQUEST_CODE_SERVICECHOOSE = 0xf07;
    private static final int RESULT_CODE_SERVICECHOOSE = 0xf17;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_conventionnew, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        btAddconvention = (Button) findViewById(R.id.bt_save);
        btAddconvention.setText(R.string.str_convention_add);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_convention_new);
        tv_conventionnew_timepoint = (TextView) findViewById(R.id.tv_conventionnew_timepoint);
        tvTeamtype = (TextView) findViewById(R.id.tv_conventionnew_teamtype);
        tvServicename = (TextView) findViewById(R.id.tv_conventionnew_servicename);
        tvTime = (TextView) findViewById(R.id.tv_conventionnew_time);
        etCustomerName = (EditText) findViewById(R.id.et_conventionnew_name);
        etCustomerMobile = (EditText) findViewById(R.id.et_conventionnew_mobile);
        etCustomerDesc = (EditText) findViewById(R.id.et_conventionnew_desc);
        gridView = (GridView) findViewById(R.id.id_gridview);
        gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        //此处好药处理日期问题
        viewDate = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow_date, null);
        viewDate.findViewById(R.id.np_1).setVisibility(View.GONE);
        viewDate.findViewById(R.id.tv_time_splitor).setVisibility(View.GONE);
        viewDate.findViewById(R.id.np_2).setVisibility(View.GONE);

        datePicker = (DatePicker) viewDate.findViewById(R.id.dp_popupwindow_date);
        btnTitle = (Button) viewDate.findViewById(R.id.bt_popupwindow_title);
        adapter = new ArrayAdapter<>(mContext, R.layout.layout_item_conventiontime, R.id.id_checkbox, list);
        gridView.setAdapter(adapter);

        sbDate = new StringBuilder(DateUtils.getCurrentDate(FORMAT_0));
        initDatePicker();
    }

    @Override
    protected void initEvents() {
        btAddconvention.setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.bt_save).setOnClickListener(this);
        if (viewDate != null) {
            viewDate.findViewById(R.id.bt_popupwindow_ok).setOnClickListener(this);
            viewDate.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckableFrameLayout cf = (CheckableFrameLayout) view;
                boolean state = cf.isChecked();
                //if (state) {
                time = list.get(position);
                //gridView.setVisibility(View.GONE);
                tv_conventionnew_timepoint.setText(time);
                LogUtils.e("funnco-----", "选中了。。。position:" + position + "  time:" + time);
                //}
            }
        });

        if (BaseApplication.getInstance().getUser() != null) {
            ids = BaseApplication.getInstance().getUser().getId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.bt_save://添加预约，网络对接
                customerName = etCustomerName.getText() + "";
                customerMobile = etCustomerMobile.getText() + "";
                customerDesc = etCustomerDesc.getText() + "";
                if (!checkData()) {
                    showSimpleMessageDialog(msg + "");
                    return;
                }
                postData();
                break;
            case R.id.bt_popupwindow_ok:
                sbDate_S = DateUtils.getDate(sbDate.toString(), FORMAT_0, FORMAT_1);
                tvTime.setText(sbDate_S + " " + DateUtils.getDayInWeek(sbDate_S, FORMAT_1));
                dissPopupWindow();
                break;
            case R.id.bt_popupwindow_cancle:
                dissPopupWindow();
                break;

        }
    }

    private void getScheduleTime() {
        FunncoUtils.showProgressDialog(mContext, "信息", "正在生成时间列表");
        map.clear();
        map.put("team_uid", ids);
        map.put("dates", sbDate.toString());
        map.put("service_id", serveSelected.getId());
        postData2(map, FunncoUrls.getCustomerScheduleTimesUrl(), false);
    }

    private boolean checkData() {
        if (TextUtils.isNull(customerName)) {
            msg = getString(R.string.p_fillout_nickname);
            return false;
        }
        if (TextUtils.isNull(customerMobile)) {
            msg = getString(R.string.p_fillout_phonenumber);
            return false;
        }
        if (TextUtils.isNull(ids)) {
            msg = getString(R.string.p_fillout_member_choose);
            return false;
        }
        if (serveSelected == null) {
            msg = getString(R.string.p_fillout_service_choose);
            return false;
        }
        if (TextUtils.equals(tv_conventionnew_timepoint.getText(), "时间")) {
            msg = getString(R.string.p_fillout_time_point_choose);
            return false;
        }
        if (TextUtils.equals(tvTime.getText(), "日期")) {
            msg = getString(R.string.p_fillout_time_choose);
            return false;
        }
        return true;
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

    private void showPopupWindow(View view) {
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setClippingEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }

    private boolean dissPopupWindow() {
        boolean hasPopupwindow = false;
        for (PopupWindow pw : new PopupWindow[]{popupWindow}) {
            if (pw != null && pw.isShowing()) {
                pw.dismiss();
                hasPopupwindow = true;
            }
        }
        return hasPopupwindow;
    }

    private void postData() {
        map.clear();
        map.put("dates", sbDate.toString());
        map.put("booktime", time);
        map.put("service_id", serveSelected.getId());//
        map.put("truename", customerName);
        map.put("mobile", customerMobile);
        if (!TextUtils.isNull(ids)) {
            map.put("team_id", team_id);
            map.put("team_uid", ids);
        }
        map.put("remark", customerDesc);
        FunncoUtils.showProgressDialog(mContext, "信息", "正在添加预约");
        postData2(map, FunncoUrls.getConventionNewUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        FunncoUtils.dismissProgressDialog();
        if (url.equals(FunncoUrls.getCustomerScheduleTimesUrl())) {//时间段的返回
            gridView.setVisibility(View.VISIBLE);
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            int enable = JsonUtils.getIntByKey4JOb(paramsJSONObject.toString(), "enable");
            if (enable == 1) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null) {
                    List<EnableTime> ls = JsonUtils.getObjectArray(listJSONArray.toString(), EnableTime.class);
                    initEnableTime(ls);
                }
                adapter.notifyDataSetChanged();
            } else {
                if (serveSelected != null)
                    showSimpleMessageDialog("服务的起始时间：" + serveSelected.getStartdate() + " 至 " + serveSelected.getEnddate());
            }

        } else if (url.equals(FunncoUrls.getConventionNewUrl())) {
            showToast(R.string.str_launch_success_conventionnew);
            finishOk();
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        FunncoUtils.dismissProgressDialog();
    }

    private void initEnableTime(List<EnableTime> ls) {
        if (serveSelected == null) {
            return;
        }
        list.clear();
//        listTimes.clear();
        int duration = Integer.valueOf(serveSelected.getDuration());//服务所用时长
        int stime = Integer.valueOf(serveSelected.getStarttime());//服务开始时间
        int etime = Integer.valueOf(serveSelected.getEndtime());//服务结束时间
        int num = (etime - stime) / duration;
        int[] times = new int[num];
        for (int i = 0; i < num; i++) {
            int dt = stime + duration * i;
            times[i] = dt;
            for (EnableTime time : ls) {
                int st = time.getStarttime();
                int et = time.getEndtime();
                if (Integer.valueOf(time.getNumbers()) == time.getCounts()) {
                    if (dt <= st - duration || dt >= et) {
                        times[i] = dt;
//                        list.add(String.valueOf(dt));
                    } else {
                        times[i] = -1;
                    }
                }
            }
            if (times[i] >= 0) {
//                listTimes.add(times[i]);
                list.add(DateUtils.getTime4Minutes(times[i]));
            }
        }

        setListViewHeightBasedOnChildren(gridView);
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_conventionnew_servicename://选择服务
                if (TextUtils.isNull(ids)) {
                    showSimpleMessageDialog(R.string.p_fillout_member_choose);
                    return;
                }
                startActivityForResult(new Intent(Actions.ACTION_CHOOSE_SERVICE)
                        .putExtra("isConvertionService", true)
                        .putExtra("id", ids)
                        .putExtra("team_id", team_id)
                        .putExtra("team_uid", ids), REQUEST_CODE_SERVICECHOOSE);
                //重置其他属性
                tvTime.setText("日期");
                tv_conventionnew_timepoint.setText("时间");
                gridView.setVisibility(View.GONE);
                break;
            case R.id.tv_conventionnew_teamtype://选择团队成员
                Intent intent = new Intent(Actions.ACTION_CHOOSE_MEMBER);
                intent.putExtra("ids", ids);
                intent.putExtra("team_id", team_id);
                intent.putExtra("chooseMode", true);
                startActivityForResult(intent, REQUEST_CODE_MEMBERCHOOSE);
                //重置其他属性
                serveSelected = null;
                tvTime.setText("日期");
                tvServicename.setText("服务");
                tv_conventionnew_timepoint.setText("时间");
                gridView.setVisibility(View.GONE);
                break;
            case R.id.tv_conventionnew_time://打开时间面板
                showPopupWindow(viewDate);
                break;
            case R.id.tv_conventionnew_timepoint:
                if (serveSelected != null && !TextUtils.equals(tvTime.getText(), "日期")) {
                    getScheduleTime();
                } else {
                    showToast("请先选择服务和日期");
                }
                break;
        }

    }

    //动态设置gridView高度
    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;//listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += (listItem.getMeasuredHeight() + 1);//加上分割线
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(0, 4, 4, 0);
        // 设置参数
        listView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MEMBERCHOOSE && resultCode == RESULT_CODE_MEMBERCHOOSE) {
            //团队成员选择完毕
            if (data != null) {
                ids = data.getStringExtra("ids");
                team_id = data.getStringExtra("team_id");
                String team_name = data.getStringExtra("team_name");
                String member_name = data.getStringExtra("member_name");
                LogUtils.e("------", "成员选择后的到的数据shi：" + ids + " team_id:" + team_id + "  team_name:" + team_name);
                if (!TextUtils.isNull(team_id)) {
                    ids = ids.trim();
                    tvTeamtype.setText(member_name + " (" + team_name + ")");
                } else {
                    tvTeamtype.setText("自己");
                }
            }
        } else if (requestCode == REQUEST_CODE_SERVICECHOOSE && resultCode == RESULT_CODE_SERVICECHOOSE) {
            if (data != null) {
                String key = data.getStringExtra(KEY);
                if (!TextUtils.isNull(key)) {
                    serveSelected = (Serve) BaseApplication.getInstance().getT(key);
                    BaseApplication.getInstance().removeT(key);
                    LogUtils.e("------", "选中的服务是：" + serveSelected);
                    if (serveSelected != null)
                        tvServicename.setText(serveSelected.getService_name() + "");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void finishOk() {
        serveSelected = null;
        super.finishOk();
    }
}
