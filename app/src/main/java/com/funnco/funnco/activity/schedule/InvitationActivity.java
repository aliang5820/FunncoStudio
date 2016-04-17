package com.funnco.funnco.activity.schedule;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.TimeCheckAdapter;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.EnableTime;
import com.funnco.funnco.bean.ScheduleTimeInfo;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.thrid.WeicatUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;

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
    private TextView tvMember;
    private View parentView;
    private Button btSendinvitation;
    private TextView tvService;
    private TextView tvDate;
    private TextView tvTime;
    private EditText etRemark;
    private View viewDate;
    private DatePicker datePicker;
    private Button btnTitle;
    private String FORMAT_0 = "yyyy-MM-dd";
    private String FORMAT_1 = "yyyy年MM月dd日";
    private StringBuilder sbDate;
    private String sbDate_S = "";
    private String SPILATE = "-";
    private GridView gridView;
    private TimeCheckAdapter adapter;
    private List<ScheduleTimeInfo> list = new ArrayList<>();
    private TextView tvKecheng;
    private Serve serveSelected = null;
    private Serve kechengSelected = null;
    private String team_id = "";
    private ScheduleTimeInfo scheduleTimeInfo;
    private String ids = "";
    private Map<String, Object> map = new HashMap<>();
    private PopupWindow popupWindow;
    private UMSocialService mController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);
    //成员选择
    private static final int REQUEST_CODE_MEMBERCHOOSE = 0xf06;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;
    //服务选择
    private static final int REQUEST_CODE_SERVICECHOOSE = 0xf07;
    private static final int RESULT_CODE_SERVICECHOOSE = 0xf17;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_invitation, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        tvMember = (TextView) findViewById(R.id.tv_invitation_member);
        btSendinvitation = (Button) findViewById(R.id.bt_save);
        btSendinvitation.setText(R.string.str_send_invitation);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_convention_invite);
        tvService = (TextView) findViewById(R.id.tv_invitation_service);
        tvDate = (TextView) findViewById(R.id.tv_invitation_date);
        tvTime = (TextView) findViewById(R.id.tv_invitation_time);
        etRemark = (EditText) findViewById(R.id.tv_invitation_remark);
        tvKecheng = (TextView) findViewById(R.id.tv_invitation_kecheng);
        gridView = (GridView) findViewById(R.id.id_gridview);
        gridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        //此处好药处理日期问题
        viewDate = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow_date, null);
        viewDate.findViewById(R.id.np_1).setVisibility(View.GONE);
        viewDate.findViewById(R.id.tv_time_splitor).setVisibility(View.GONE);
        viewDate.findViewById(R.id.np_2).setVisibility(View.GONE);

        datePicker = (DatePicker) viewDate.findViewById(R.id.dp_popupwindow_date);
        btnTitle = (Button) viewDate.findViewById(R.id.bt_popupwindow_title);
        adapter = new TimeCheckAdapter(list);
        gridView.setAdapter(adapter);
        tvService.setOnClickListener(this);
        tvKecheng.setOnClickListener(this);
        btSendinvitation.setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        sbDate = new StringBuilder(DateUtils.getCurrentDate(FORMAT_0));
        initDatePicker();
    }

    @Override
    protected void initEvents() {
        if (viewDate != null) {
            viewDate.findViewById(R.id.bt_popupwindow_ok).setOnClickListener(this);
            viewDate.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                scheduleTimeInfo = list.get(position);
                if(scheduleTimeInfo.isEnable()) {
                    tvTime.setText(scheduleTimeInfo.getFormatTime() + "-" + scheduleTimeInfo.getDurationTime());
                    gridView.setVisibility(View.GONE);
                    LogUtils.e("funnco-----", "选中了。。。position:" + position + "  scheduleTimeInfo:" + scheduleTimeInfo);
                } else {
                    showToast("该时间段已排满");
                }
            }
        });
    }

    //初始化时间选择器
    private void initEnableTime(JSONObject paramsJSONObject, List<EnableTime> ls) {
        if (serveSelected == null && kechengSelected != null) {
            //课程直接使用开始时间
            int startTime = JsonUtils.getIntByKey4JOb(paramsJSONObject.toString(), "starttime");
            kechengSelected.getDuration();
            tvTime.setText(DateUtils.getTime4Minutes(startTime));
        } else {
            list.clear();
            int duration = Integer.valueOf(serveSelected.getDuration());//服务所用时长
            int sTime = Integer.valueOf(serveSelected.getStarttime());//服务开始时间
            int eTime = Integer.valueOf(serveSelected.getEndtime());//服务结束时间
            int num = (eTime - sTime) / duration;
            int[] times = new int[num];
            for (int i = 0; i < num; i++) {
                int dt = sTime + duration * i;
                times[i] = dt;
                ScheduleTimeInfo info = new ScheduleTimeInfo();
                info.setFormatTime(DateUtils.getTime4Minutes(times[i]));
                info.setDurationTime(DateUtils.getTime4Minutes(times[i] + duration));
                for (EnableTime enableTime : ls) {
                    int st = enableTime.getStarttime();
                    int et = enableTime.getEndtime();
                    if (Integer.valueOf(enableTime.getNumbers()) == enableTime.getCounts()) {
                        if (dt <= st - duration || dt >= et) {
                            times[i] = dt;
                        } else {
                            times[i] = -1;
                        }
                    }
                }
                info.setEnable(times[i] >= 0);
                list.add(info);
            }
            adapter = new TimeCheckAdapter(list);
            gridView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(gridView);
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

    private void getTeamMembersData() {
        Intent intent = new Intent(Actions.ACTION_CHOOSE_MEMBER);
        if (!TextUtils.isNull(team_id) && !TextUtils.isNull(ids)) {
            intent.putExtra("ids", ids);
            intent.putExtra("team_id", team_id);
        }
        intent.putExtra("chooseMode", true);
        startActivityForResult(intent, REQUEST_CODE_MEMBERCHOOSE);
    }

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

    private boolean disPopupWindow() {
        boolean hasPopupWindow = false;
        for (PopupWindow pw : new PopupWindow[]{popupWindow}) {
            if (pw != null && pw.isShowing()) {
                pw.dismiss();
                hasPopupWindow = true;
            }
        }
        return hasPopupWindow;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_invitation_kecheng:
                //选择课程
                if (TextUtils.isNull(ids)) {
                    showSimpleMessageDialog(R.string.p_fillout_member_choose);
                    return;
                }
                startActivityForResult(new Intent(Actions.ACTION_CHOOSE_SERVICE)
                        .putExtra("isConvertionService", true)
                        .putExtra("service_type", 1)
                        .putExtra("team_id", team_id)
                        .putExtra("team_uid", ids), REQUEST_CODE_SERVICECHOOSE);
                break;
            case R.id.tv_invitation_service:
                if (TextUtils.isNull(ids)) {
                    showSimpleMessageDialog(R.string.p_fillout_member_choose);
                    return;
                }
                startActivityForResult(new Intent(Actions.ACTION_CHOOSE_SERVICE)
                        .putExtra("isConvertionService", true)
                        .putExtra("team_id", team_id)
                        .putExtra("team_uid", ids), REQUEST_CODE_SERVICECHOOSE);
                break;
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.bt_save://添加邀请，网络对接
                if (tvDate.length() <= 0 || tvTime.length() <= 0) {
                    showToast("请选择时间");
                } else if(serveSelected == null && kechengSelected == null) {
                    showToast("请选择服务或者课程");
                }
                //发出邀请
                map.clear();
                String serverId = "";
                if (serveSelected != null) {
                    serverId = serveSelected.getId();
                } else if(kechengSelected != null) {
                    serverId = kechengSelected.getId();
                }
                map.put("service_id", serverId);
                String time = tvTime.getText().toString();
                map.put("booktime", time.contains("-") ? time.split("-")[0] : time);
                map.put("dates", sbDate.toString());
                map.put("remark", etRemark.length() > 0 ? etRemark.getText().toString() : "");
                map.put("uid", ids);
                FunncoUtils.showProgressDialog(mContext, "信息", "正在添加邀请");
                postData2(map, FunncoUrls.getInvitationUrl(), false);
                break;
            case R.id.bt_popupwindow_ok:
                sbDate_S = DateUtils.getDate(sbDate.toString(), FORMAT_0, FORMAT_1);
                tvDate.setText(sbDate_S + " " + DateUtils.getDayInWeek(sbDate_S, FORMAT_1));
                tvTime.setText("");
                gridView.setVisibility(View.GONE);
                disPopupWindow();
                break;
            case R.id.bt_popupwindow_cancle:
                disPopupWindow();
                break;
        }
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_invitation_member://选择成员
                getTeamMembersData();
                break;
            case R.id.tv_invitation_date://选择日期
                showPopupWindow(viewDate);
                break;
            case R.id.tv_invitation_time://选择时间
                if ((serveSelected != null || kechengSelected != null) && !TextUtils.equals(tvDate.getText(), "日期")) {
                    getScheduleTime();
                } else {
                    showToast("请先选择服务和日期");
                }
                break;
        }
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
                    initEnableTime(paramsJSONObject, ls);
                }
                adapter.notifyDataSetChanged();
            } else {
                showToast("当前日期不可用，请切换其他时间");
            }
        } else if (url.equals(FunncoUrls.getInvitationUrl())) {
            showToast(R.string.str_launch_success_invitation);
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            String shareUrl = JsonUtils.getStringByKey4JOb(paramsJSONObject.toString(), "link");
            WeicatUtils.setShareContent(mContext, mController, shareUrl, R.mipmap.common_logo_rectangle);
            //开始分享
            mController.postShare(mContext, SHARE_MEDIA.WEIXIN, new SocializeListeners.SnsPostListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
                    if (i == 200) {
                        FunncoUtils.showToast(mContext, R.string.success);
                        finishOk();
                    } else {
                        String eMsg = "";
                        if (i == -101) {
                            eMsg = "没有授权";
                        }
                        Toast.makeText(mContext, "邀请失败[" + i + "] " + eMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        FunncoUtils.dismissProgressDialog();
    }

    /**
     * 生成时间列表
     */
    private void getScheduleTime() {
        FunncoUtils.showProgressDialog(mContext, "信息", "正在生成时间列表");
        map.clear();
        map.put("team_uid", ids);
        map.put("dates", sbDate.toString());
        String serverId = "";
        if (serveSelected != null) {
            serverId = serveSelected.getId();
        } else if(kechengSelected != null) {
            serverId = kechengSelected.getId();
        }
        map.put("service_id", serverId);
        postData2(map, FunncoUrls.getCustomerScheduleTimesUrl(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEMBERCHOOSE && resultCode == RESULT_CODE_MEMBERCHOOSE) {
            //团队成员选择完毕
            if (data != null) {
                ids = data.getStringExtra("ids");
            }
            team_id = data.getStringExtra("team_id");
            String team_name = data.getStringExtra("team_name");
            String member_name = data.getStringExtra("member_name");
            LogUtils.e("------", "成员选择后的到的数据shi：" + ids + " team_id:" + team_id + "  team_name:" + team_name);
            if (!TextUtils.isNull(ids) && !TextUtils.isNull(team_name) && !TextUtils.isNull(member_name)) {
                ids = ids.trim();
                tvMember.setText(member_name + " (" + team_name + ")");
            } else {
                tvMember.setText("自己");
            }
            kechengSelected = null;
            serveSelected = null;
            tvService.setText("服务");
            tvKecheng.setText("课程");
            tvDate.setText("");
            tvTime.setText("");
            gridView.setVisibility(View.GONE);
        } else if (requestCode == REQUEST_CODE_SERVICECHOOSE && resultCode == RESULT_CODE_SERVICECHOOSE) {
            if (data != null) {
                String key = data.getStringExtra(KEY);
                int service_type = data.getIntExtra("service_type", 0);
                if (!TextUtils.isNull(key)) {
                    if (service_type == 0) {
                        kechengSelected = null;
                        serveSelected = (Serve) BaseApplication.getInstance().getT(key);
                        tvService.setText(serveSelected.getService_name() + "");
                        tvService.setBackgroundResource(R.color.color_hint_tangerine);
                        tvKecheng.setText("课程");
                        tvKecheng.setBackgroundResource(R.color.white);
                        tvDate.setText("");
                        tvTime.setText("");
                        LogUtils.e("------", "选中的服务是：" + serveSelected);
                    } else if (service_type == 1) {
                        serveSelected = null;
                        kechengSelected = (Serve) BaseApplication.getInstance().getT(key);
                        tvKecheng.setText(kechengSelected.getService_name() + "");
                        tvKecheng.setBackgroundResource(R.color.color_hint_tangerine);
                        tvService.setText("服务");
                        tvService.setBackgroundResource(R.color.white);
                        tvDate.setText("");
                        tvTime.setText("");
                        LogUtils.e("------", "选中的课程是：" + kechengSelected);
                    }
                    gridView.setVisibility(View.GONE);
                    BaseApplication.getInstance().removeT(key);
                }
            }
        }
    }
}
