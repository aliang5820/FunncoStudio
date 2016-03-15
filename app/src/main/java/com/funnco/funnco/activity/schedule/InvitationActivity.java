package com.funnco.funnco.activity.schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.ListPickerAdapter;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.httpc.HttpClientUtils;
import com.funnco.funnco.model.ListPickerIMd;
import com.funnco.funnco.utils.ApiConfig;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.dialog.LoadingDialog;
import com.funnco.funnco.view.layout.CheckableFrameLayout;
import com.funnco.funnco.view.pop.ListPicker;
import com.funnco.funnco.view.pop.TimePicker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约邀请
 * Created by user on 2015/8/18.
 */
public class InvitationActivity extends BaseActivity {
    private TextView textView;
    ArrayList<ListPickerIMd> mChenyuanData;
    ArrayList<ListPickerIMd> mFuwuData;
    ArrayList<ListPickerIMd> mKechengData;

    private View parentView;
    private Button btSendinvitation;
    private TextView tvServicename;
    private TextView tvNumber;
    private TextView tvTime;
    private EditText etRemark;
    private ArrayAdapter<String> adapter;
    private View viewDate;
    private DatePicker datePicker;
    private Button btnTitle;
    private String FORMAT_0 = "yyyy-MM-dd";
    private String FORMAT_1 = "yyyy年MM月dd日";
    private StringBuilder sbDate = new StringBuilder(DateUtils.getCurrentDate(FORMAT_0));
    private String sbDate_S = "";
    private String SPILATE = "-";
    private List<String> list = new ArrayList<String>();
    LoadingDialog mDialog;
    private TextView mKecheng;
    private Serve serveSelected = null;
    private String team_id = "";
    private String time;
    private String ids = "";
    private String customerName;
    private String customerMobile;
    private String customerDesc;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_invitation, null);
        setContentView(parentView);
    }

    //成员选择
    private static final int REQUEST_CODE_MEMBERCHOOSE = 0xf06;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;

    @Override
    protected void initView() {
        mDialog = new LoadingDialog(this);
        textView = (TextView) findViewById(R.id.textView);
        btSendinvitation = (Button) findViewById(R.id.llayout_foot);
        btSendinvitation.setText(R.string.str_send_invitation);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_convention_invite);
        tvServicename = (TextView) findViewById(R.id.tv_invitation_service);
        tvNumber = (TextView) findViewById(R.id.tv_invitation_account);
        tvTime = (TextView) findViewById(R.id.tv_invitation_time);
        etRemark = (EditText) findViewById(R.id.tv_invitation_remark);
        buildData();
        mKecheng = (TextView) findViewById(R.id.tv_kecheng);
        //此处好药处理日期问题
        viewDate = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow_date, null);
        viewDate.findViewById(R.id.np_1).setVisibility(View.GONE);
        viewDate.findViewById(R.id.tv_time_splitor).setVisibility(View.GONE);
        viewDate.findViewById(R.id.np_2).setVisibility(View.GONE);

        datePicker = (DatePicker) viewDate.findViewById(R.id.dp_popupwindow_date);
        btnTitle = (Button) viewDate.findViewById(R.id.bt_popupwindow_title);
        adapter = new ArrayAdapter<>(mContext, R.layout.layout_item_conventiontime, R.id.id_checkbox, list);
        textView.setOnClickListener(this);
        tvServicename.setOnClickListener(this);

        mKecheng.setOnClickListener(this);
        initDatePicker();


    }

    private void getTeamMembersData() {

        //    ListPicker.getListPicker(textView, InvitationActivity.this, textView.getWidth(),mChenyuanData);

        Intent intent = new Intent(Actions.ACTION_CHOOSE_MEMBER);
        if (!TextUtils.isNull(team_id) && !TextUtils.isNull(ids)) {
            intent.putExtra("ids", ids);
            intent.putExtra("team_id", team_id);
        }
        intent.putExtra("chooseMode", true);
        startActivityForResult(intent, REQUEST_CODE_MEMBERCHOOSE);
//        if (mChenyuanData == null) {
//            mDialog.show();
//            Map<String, Object> maps = new HashMap<>();
//
//            String url = FunncoUrls.getTeamMemberList();
//
//            Log.i("test", "url:" + url);
//            postData2(maps, url, false);
//
//        } else {
//
//            ListPicker.getListPicker(textView, this, textView.getWidth(), mChenyuanData);
//        }

    }

    private PopupWindow popupWindow;

    private void showPopupWindow(View view) {
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setClippingEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
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

    private void buildData() {


    }


    @Override
    protected void initEvents() {
        btSendinvitation.setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        if (viewDate != null) {
            viewDate.findViewById(R.id.bt_popupwindow_ok).setOnClickListener(this);
            viewDate.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
        }

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

    //服务选择
    private static final int REQUEST_CODE_SERVICECHOOSE = 0xf07;
    private static final int RESULT_CODE_SERVICECHOOSE = 0xf17;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.textView:
                getTeamMembersData();

                break;
            case R.id.tv_kecheng:

                ListPicker.getListPicker(mKecheng, this, mKecheng.getWidth(), null);
                break;

            case R.id.tv_invitation_service:

                if (com.funnco.funnco.utils.string.TextUtils.isNull(ids)) {
                    showSimpleMessageDialog(R.string.p_fillout_member_choose);
                    return;
                }
                startActivityForResult(new Intent(Actions.ACTION_CHOOSE_SERVICE)
                        .putExtra("isConvertionService", true)
                        .putExtra("team_id", team_id)
                        .putExtra("team_uid", ids), REQUEST_CODE_SERVICECHOOSE);
                //    ListPicker.getListPicker(tvServicename, this, tvServicename.getWidth(),null);
                break;
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.llayout_foot://发出邀请

                break;
            case R.id.bt_popupwindow_ok:
                sbDate_S = DateUtils.getDate(sbDate.toString(), FORMAT_0, FORMAT_1);
                tvTime.setText(sbDate_S + " " + DateUtils.getDayInWeek(sbDate_S, FORMAT_1));
                dissPopupWindow();
                if (serveSelected != null && !TextUtils.isNull(sbDate.toString())) {
                    //    getScheduleTime();
                }
                break;
            case R.id.bt_popupwindow_cancle:
                dissPopupWindow();
                break;
        }
    }

    public View getListPicker(View parent, Context context) {
        Log.i("test", "onPicker");
        ListPickerAdapter adapter = new ListPickerAdapter();
        PopupWindow popupWindow = null;
        ListView lv_group = null;
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.pop_view_listpicker, null);
        lv_group = (ListView) view.findViewById(R.id.list_info);
        //      lv_group.setAdapter(groupAdapter);
        popupWindow = new PopupWindow(view, 200, 220);
        lv_group.setAdapter(adapter);
        return view;
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(true);
//        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.showAsDropDown(parent);
////            lv_group.setOnItemClickListener(new OnItemClickListener() {
////                @Override
////                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                }
////            });

    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_invitation_service://选择服务


                break;
            case R.id.tv_invitation_account://选择人数
                showPopupWindow(viewDate);
                break;
            case R.id.tv_invitation_time://选择时间

                getTimeList();
                TimePicker.getListPicker(tvTime, this, tvTime.getWidth(), null);
                break;

            case R.id.textView:

                break;
        }
    }

    private void getTimeList() {

        mDialog.show();
        Map<String, Object> maps = new HashMap<>();
        String url = FunncoUrls.getTimeList();

        Log.i("test", "url:" + url);
        postData2(maps, url, false);


    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        Log.w("test", "dataPostBack:" + result);
        mDialog.dismiss();
        if (url.equals(FunncoUrls.getTimeList()))
        {
            TimePicker.getListPicker(tvTime,this,tvTime.getWidth(),null);
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);

        mDialog.dismiss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEMBERCHOOSE && resultCode == RESULT_CODE_MEMBERCHOOSE) {
            //团队成员选择完毕
            if (data != null)
                ids = data.getStringExtra("ids");
            team_id = data.getStringExtra("team_id");
            String team_name = data.getStringExtra("team_name");
            String member_name = data.getStringExtra("member_name");
            LogUtils.e("------", "成员选择后的到的数据shi：" + ids + " team_id:" + team_id + "  team_name:" + team_name);
            if (!TextUtils.isNull(ids)) {
                ids = ids.trim();
                textView.setText(member_name + " (" + team_name + ")");
            } else {
                textView.setText("自己");
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
    }
}
