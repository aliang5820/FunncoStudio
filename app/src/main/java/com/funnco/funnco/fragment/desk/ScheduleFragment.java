package com.funnco.funnco.fragment.desk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.ConventionNewActivity;
import com.funnco.funnco.activity.MyConventionActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.activity.schedule.AddScheduleActivity;
import com.funnco.funnco.activity.schedule.InvitationActivity;
import com.funnco.funnco.activity.schedule.ReasonCancleActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.CommonBaseAdapter;
import com.funnco.funnco.adapter.ListViewAdapter;
import com.funnco.funnco.adapter.MonthCalendarAdapter;
import com.funnco.funnco.adapter.MyExpandListViewAdapter;
import com.funnco.funnco.adapter.TeamlistExpAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.adapter.WeekendCalendarAdapter;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.DateComparator;
import com.funnco.funnco.bean.FunncoEvent;
import com.funnco.funnco.bean.FunncoEventCustomer;
import com.funnco.funnco.bean.MonthCalendar;
import com.funnco.funnco.bean.ScheduleNew;
import com.funnco.funnco.bean.ScheduleNewStat;
import com.funnco.funnco.bean.ScheduleNewStat_2;
import com.funnco.funnco.bean.Team;
import com.funnco.funnco.bean.TeamMember;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.bean.WeekendCalendar;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.impl.MyGestureListener;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.bimp.BitmapUtils;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.date.TimeUtils;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.LoginUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.http.VolleyUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.thrid.WeicatUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.layout.CalendarPanel;
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.switcher.SwipeLayout;
import com.funnco.funnco.view.textview.DesignTextView;
import com.funnco.funnco.view.viewpager.MonthViewPager;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.sso.UMSsoHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScheduleFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    //Calendar
    private static final int GRID_ITEM_COUNT = 7;
    private static final int ORI_MONTH_POSITION = 500;
    private static final int MAX_MONTH_VALUE = 1000;
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String MONTHSTR = "月";
    private static final String DAYSTR = "日";
    private static final String SPLITSTR = "-";
    private DateComparator dateComparator = new DateComparator();
    //收缩展开的面板
    private CalendarPanel mPanelCtrl;
    private MonthViewPager mViewPager;
    private MonthPagerAdapter mPagerAdapter;
    private GridView mMonthGridView;
    private MonthCalendarAdapter mMonthAdapter;
    private GridView mCurrentGridView;
    public List<MonthCalendar> mCurrDateInfoList;
    private List<MonthCalendar> mDateInfoList;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<CheckBox> checkBoxList = new ArrayList<>();
    //新预约日期对象的集合
    private List<ScheduleNew> newscheduleNewList = new ArrayList<>();
    //未读消息的日期对象集合 key--2015-07-18(日期)  value - 3(当天预约人数)
    private Map<String, String> newScheduleNewDate = new HashMap<>();
    //未读消息数
    private int msgCount = 0;
    //有预约的日期集合 yyyy-MM-dd
    private Hashtable<String, String> scheduleNewList = new Hashtable<>();
    //月数据---数据库保存用  yyyy-MM-dd
    private List<ScheduleNewStat> scheduleNewList2 = new CopyOnWriteArrayList<>();
    //有事件的日期
    private Hashtable<String, String> scheduleNewList3 = new Hashtable<>();
    //有事件的日期集合
    private List<ScheduleNewStat_2> scheduleNewList4 = new CopyOnWriteArrayList<>();
    //private Map<String, String> repeadEveryDayEventMap = new HashMap<>();
    //存放已下载的月 yyyy-MM
    private Hashtable<String, String> scheduleNewStatMonth = new Hashtable<>();
    private boolean isFistLoadScheduleDate = true;//是否是第一次下载有预约的日程数据，用于第一次下载清空所有
    public String mLastSelectedDay = "";
    // 当前索引为第500页，前后各有500页可滑动
    private int mCurrPager = ORI_MONTH_POSITION;
    // 当前系统时间，月控件基数值
    private int sysYear;
    private int sysMonth;
    private int sysDay;
    // 周控件滑动过程中变化值
    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrDay;
    // Week Calendar
    private ViewFlipper mWeekFlipper = null;
    private GridView mWeekGridView = null;
    private GestureDetector mGestureDetector = null;
    private WeekendCalendarAdapter mDateAdapter;
    private int mDaysOfMonth = 0; // 某月的天数
    private int mDayOfWeek = 0; // 具体某一天是星期几
    private int mWeeksOfMonth = 0;
    private WeekendCalendar mWeekCalendar = null;
    private boolean isLeapyear = false; // 是否为闰年
    private int mCurrentWeek;
    private int mCurrentNum;
    //微信分享
    // 整个平台的Controller,负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory.getUMSocialService(Constants.DESCRIPTOR);
    private String appId, appSecret;
    private UserLoginInfo user;
    private ImageView ivToday;
    //分享面板
    private View vShareBoard;
    private ListView vShareListView;
    private CommonAdapter<Team> shareAdapter;

    //头部控件
    private CheckBox cbMonth = null;
    private RelativeLayout rlHeadl;
    private CircleImageView civTitleicon;
    private XListView xListView = null;
    //XListView头部
    private View vContent;
    private DesignTextView designTextView;
    //今天图标
    //判断是否正在加载
    private boolean isLoading = false;
    //本业内控制pw的显示
    private boolean isFirstuse = true;
    private ListViewAdapter adapter;
    private Intent intent = null;
    //返回的日程列表
    private ArrayList<FunncoEvent> eventList = new ArrayList<>();
    //当前打开/和点击的项的FunncoEvent对象
    private FunncoEvent currentFunncoEvent;
    //删除日程的popupwindow
    private PopupWindow popupWindow;
    //删除预约
    private View deletepwView;
    private Button deletecusDelete;
    private Button deletecusCancle;
    //切换 用户选择
    private View teammemberView;
    private ExpandableListView teammemberExpListView;
    private TeamlistExpAdapter teammemberExpAdapter;
    private String ids;
    private String team_id;
    //添加提醒
    private TextView tvAddNotify;
    private View addNotifyView;
    private ImageButton ibPwAddicon;
    private TextView tvPwAddevent;
    private TextView tvPwAddconvention;
    private TextView tvPwAddinvitation;

    //预约客户列表
    private PopupWindow pwCustomer;
    private View pwCustomerView;
    //修改成ExpandListView
    private ExpandableListView pwCustomerElv;
    private MyExpandListViewAdapter<FunncoEventCustomer> pwCustomerElvAdapter;

    //每次弹出的PopupWindow 显示我的预约客户列表集合
    private ArrayList<FunncoEventCustomer> customerList = new ArrayList<>();
    //展开的Item position
    private int oldGroupPosition = -1;
    private int indexCusDele;//popupwindow中删除想的position
    private VolleyUtils utils;
    private Button btPwCancle;
    private TextView tvPwTitle;
    private boolean isCusDele = false;
    private View pwView;
    private Button btDeleAll, btDeleCurr, btCancle;
    //点击删除传回来的position
    private int deletePosition;
    //xlistView headeritem信息
    private TextView tvScheduleDay;
    private TextView tvScheduleDate;
    //存放两个值  日程/预约客户
    private Map<String, SwipeLayout> swipMap = new HashMap<>();
    //
    private Map<String, Object> map = new HashMap<>();
    private List<Team> teamList = new ArrayList<>();
    //schedule 删除
    private static final int REQUEST_CODE_SDU_DEL = 0xff01;
    //popupwindow exl 删除
    private static final int REQUEST_CODE_EXL_DEL_REASON = 0xff02;
    //删除成功
    private static final int RESULT_CODE_OK = 0x1ff000;
    //删除失败
    private static final int RESULT_CODE_FAILURE = 0x1ff001;
    //取消原因
    private int REQUEST_REASON_CANCLE = 1004;
    //添加临时事件
    private static final int REQUEST_CODE_SCHEDULE_EDIT = 0xff03;
    private static final int RESULT_CODE_SCHEDULE_EDIT = 0xff13;
    //修改日程
    private static final int REQUEST_CODE_SCHEDULE_ADD = 0xff04;
    private static final int RESULT_CODE_SCHEDULE_ADD = 0xff14;
    //我的预约
    private static final int REQUEST_CODE_MYCONVENTION = 0xff05;
    //添加预约
    private static final int REQUEST_CODE_CONVENTION_ADD = 0xff06;
    private static final int RESULT_CODE_CONVENTION_ADD = 0xff16;
    //添加邀请
    private static final int REQUEST_CODE_INVITATION_ADD = 0xff07;
    private static final int RESULT_CODE_INVITATION_ADD = 0xff17;
    //成员选择
    private static final int REQUEST_CODE_MEMBERCHOOSE = 0xf06;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;

    //用于记录刷新和下拉的次数
    private int refreshCount, loadmoreCount;
    //用于关闭
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 0) {
                if (xListView != null) {
                    xListView.stopLoadMore();
                    xListView.stopRefresh();
                }
            } else if (what == 17) {//跳转到添加事件 为体现动画效果
                showPopupwindow(addNotifyView);
            }
        }
    };

    public ScheduleFragment() {
        // 启动预加载
        sysYear = TimeUtils.getCurrentYear();
        sysMonth = TimeUtils.getCurrentMonth();
        sysDay = TimeUtils.getCurrentDay();
        mCurrentYear = sysYear;
        mCurrentMonth = sysMonth;
        mCurrDay = sysDay;
        mWeekCalendar = new WeekendCalendar();
        mLastSelectedDay = TimeUtils.getFormatDate(mCurrentYear, mCurrentMonth,
                mCurrDay);
    }

    /**
     * 网络下载有预约的日期 以及 有事件的日期
     *
     * @param dates
     */
    private void downScheduleNew(final String dates) {
        if (BaseApplication.getInstance().getUser() == null || !NetUtils.isConnection(mContext)) {
            return;
        }
        if (BaseApplication.getInstance().getCookieStore() == null) {
            LoginUtils.reLogin(mContext, new Post() {
                @Override
                public void post(int... position) {
                    downScheduleNew(dates);
                }
            });
            return;
        }
        map.clear();
        map.put("dates", dates);
        postData2(map, FunncoUrls.getScheduleDateStat2(), false);
        AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) != 0) {
                    showSimpleMessageDialog(JsonUtils.getResponseMsg(result) + "");
                    return;
                }
                JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                JSONArray type0JSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "type0");
                JSONArray type1JSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "type1");
                if (isFistLoadScheduleDate) {
                    SQliteAsynchTask.deleteAll(dbUtils, ScheduleNewStat.class);
                    SQliteAsynchTask.deleteAll(dbUtils, ScheduleNewStat_2.class);
                    isFistLoadScheduleDate = false;//及时将值修改回来
                }
                //添加已下载的年月
                String sd = dates.split(SPLITSTR)[0] + SPLITSTR + dates.split(SPLITSTR)[1];
                scheduleNewStatMonth.put(sd, sd);
                List<ScheduleNewStat> ls = new ArrayList<>();
                List<ScheduleNewStat_2> ls2 = new ArrayList<>();
                try {
                    for (int i = 0; i < type1JSONArray.length(); i++) {
                        String value = null;
                        value = type1JSONArray.get(i) + "";
                        scheduleNewList.put(value, value);
                        ScheduleNewStat stat = new ScheduleNewStat();
                        stat.setDate(value);
                        scheduleNewList2.add(stat);
                        ls.add(stat);
                    }
                    for (int i = 0; i < type0JSONArray.length(); i++) {
                        String value = type0JSONArray.get(i) + "";
                        scheduleNewList3.put(value, value);
                        ScheduleNewStat_2 stat_2 = new ScheduleNewStat_2();
                        stat_2.setDate(value);
                        scheduleNewList4.add(stat_2);
                        ls2.add(stat_2);
                    }

                    updateAdapterNewStart(null);
                    if (ls != null && ls.size() > 0) {
                        dbUtils.saveOrUpdateAll(ls);
                    }
                    if (ls2 != null && ls2.size() > 0) {
                        dbUtils.saveOrUpdateAll(ls2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {
            }
        }, false, FunncoUrls.getScheduleDateStat2());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_fragment_shcedule, container, false);
        initViews();
        initEvents();
        return parentView;
    }

    Map<String, Boolean> open = new HashMap<>();

    @Override
    protected void initViews() {
        appId = getActivity().getString(R.string.appId);
        appSecret = getActivity().getString(R.string.appSecret);
        user = BaseApplication.getInstance().getUser();
        utils = new VolleyUtils(mContext);
        if (mContext == null) {
            mContext = getActivity();
            mActivity = (MainActivity) mContext;
        }
        imageLoader = mActivity.getImageLoader();
        options = mActivity.getOptions();
        //添加平台
        WeicatUtils.addWXPlatform(getActivity(), appId, appSecret);
        civTitleicon = (CircleImageView) findViewById(R.id.civ_schedule_headl);
        //添加分享内容
        if (user != null) {
            //WeicatUtils.setShareContent(getActivity(), mController, user, R.mipmap.common_logo_rectangle);
            String picUrl = user.getHeadpic();
            if (!TextUtils.isNull(picUrl)) {
                imageLoader.displayImage(picUrl, civTitleicon, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (bitmap != null) {
                            WeicatUtils.setShareContent(mContext, mController, user, R.mipmap.common_logo_rectangle, bitmap);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                    }
                });
            } else {
                WeicatUtils.setShareContent(mContext, mController, user, R.mipmap.common_logo_rectangle);
            }
        }
        //分享面板
        vShareBoard = LayoutInflater.from(mContext).inflate(R.layout.layout_popupwindow_shareboat, null);
        vShareListView = (ListView) vShareBoard.findViewById(R.id.id_listView);

        //获取有预约的日期 集合
        if (!NetUtils.isConnection(mContext)) {//没网的条件下  才加载本地数据
            initScheduleNewStar(mLastSelectedDay);
        }
        rlHeadl = (RelativeLayout) findViewById(R.id.rlayout_schedule_headl);

        cbMonth = (CheckBox) findViewById(R.id.cb_schedule_headm);
        xListView = (XListView) findViewById(R.id.xlv_schedule_list);//获取XListView
        xListView.setPullLoadEnable(false);//设置为上拉加载
        xListView.setPullRefreshEnable(true);//设置为可下拉刷新
        vContent = LayoutInflater.from(getActivity()).inflate(R.layout.layout_fragment_schedule_content, null);
        xListView.addHeaderView(vContent);
//        tvAddSchedule = (TextView) vContent.findViewById(R.id.tv_schedule_addschedule);
//        tvShareSchedule = (TextView) vContent.findViewById(R.id.tv_schedule_shareschedule);
        tvScheduleDay = (TextView) vContent.findViewById(R.id.tv_schedule_day);
        tvScheduleDate = (TextView) vContent.findViewById(R.id.tv_schedule_date);
        designTextView = (DesignTextView) vContent.findViewById(R.id.dtv_schedule_myconventation_count);
//        designTextView.setTextSize(10);
        ivToday = (ImageView) vContent.findViewById(R.id.iv_today_icon);
        tvAddNotify = (TextView) vContent.findViewById(R.id.tv_schedule_addschedule);

        tvScheduleDate.setText(DateUtils.getMonth() + "月" + DateUtils.getCurrentDay() + "日");
        adapter = new ListViewAdapter(getActivity(), eventList, new Post() {
            @Override
            public void post(int... position) {
                if (position[0] >= 0 && position[0] < eventList.size()) {
                    isCusDele = false;
                    deletePosition = position[0];
                    //点击删除按钮时的项
                    currentFunncoEvent = eventList.get(position[0]);
                    //弹出对话框  执行删除操作
                    alertDialogDelete(deletePosition);
                }
            }
        }, imageLoader, options);
        xListView.setAdapter(adapter);
        pwView = LayoutInflater.from(mContext).inflate(R.layout.layout_popupwindow_delete, null);
        btDeleAll = (Button) pwView.findViewById(R.id.bt_pwdelete_deleteall);
        btDeleCurr = (Button) pwView.findViewById(R.id.bt_pwdelete_deletecurrent);
        btCancle = (Button) pwView.findViewById(R.id.bt_pwdelete_cancle);
        //customer 的PopupWindow
        pwCustomerView = getActivity().getLayoutInflater().inflate(R.layout.layout_activity_callcustomer, null, false);
        pwCustomer = new PopupWindow(pwCustomerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pwCustomerElv = (ExpandableListView) pwCustomerView.findViewById(R.id.elv_pw_schedule_list);
        btPwCancle = (Button) pwCustomerView.findViewById(R.id.bt_pw_schedule_cancle);
        tvPwTitle = (TextView) pwCustomerView.findViewById(R.id.tv_pw_schedule_title);
        /**
         * 设置父容器打开监听的方法
         */
        pwCustomerElv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //关闭其他分组
                pwCustomerElvAdapter.hindView(groupPosition);
                open.put(String.valueOf(groupPosition), true);
            }
        });
        pwCustomerElv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                pwCustomerElvAdapter.showView(groupPosition);
                open.remove(String.valueOf(groupPosition));
            }
        });
        pwCustomerElvAdapter = new MyExpandListViewAdapter<FunncoEventCustomer>(customerList) {
            @Override
            public View getGroutViewContent(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent, MyHolder holder) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.layout_item_callcustomer_pw, parent, false);
                    holder = new MyHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (MyHolder) convertView.getTag();
                }
                final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swip);
                swipeLayout.addSwipeListener(new SimpleSwipeListener() {

                    @Override
                    public void onOpen(SwipeLayout layout) {
                        //关闭指定的组
                        pwCustomerElv.collapseGroup(groupPosition);
                        SwipeLayout swip = swipMap.get("swip");
                        if (swip != null && swip != swipeLayout) {
                            swip.close(true);
                        }
                        swipMap.put("swip", swipeLayout);
                    }
                });
                TextView tvDele = (TextView) convertView.findViewById(R.id.delete);
                tvDele.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        swipeLayout.close();
                        isCusDele = true;
                        indexCusDele = groupPosition;
                        //弹出对话框进行删除
                        alertDialogDelete(groupPosition);
                    }
                });

                holder.ivCall.setTag(R.id.delete, customerList.get(groupPosition).getMobile());
                holder.ivCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        swipeLayout.close();
                        String phone = (String) v.getTag(R.id.delete);
                        Uri uri = Uri.parse("tel:" + v.getTag(R.id.delete));
                        Intent intent = new Intent(Intent.ACTION_CALL, uri);
                        if (!TextUtils.isNull(phone)) {
                            startActivity(intent);
                        }
                    }
                });
                final MyHolder finalHolder = holder;
                utils.imageRequest(customerList.get(groupPosition).getHeadpic(), 150, 150, new DataBack() {
                    @Override
                    public void getString(String result) {
                    }

                    @Override
                    public void getBitmap(String rul, Bitmap bitmap) {
                        if (bitmap != null) {
                            finalHolder.iv.setImageBitmap(BitmapUtils.getCircleBitmap2(bitmap, R.mipmap.icon_edit_profile_default_2x));
                        }
                    }
                });
                holder.tvTruename.setVisibility(View.VISIBLE);
                holder.tvLastyuyue.setVisibility(View.VISIBLE);
                if (open.containsKey(String.valueOf(groupPosition))) {
                    holder.tvLastyuyue.setVisibility(View.INVISIBLE);
                    holder.tvLastyuyue.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                } else {
                    holder.tvLastyuyue.setVisibility(View.VISIBLE);
                }
                holder.tvTruename.setText(customerList.get(groupPosition).getTruename() + "");
                holder.tvLastyuyue.setText("备注：" + customerList.get(groupPosition).getRemark());
                return convertView;
            }

            @Override
            public View getChildViewContent(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent, MyHolder2 holder2) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.layout_item_mycustomer_child, parent, false);
                    holder2 = new MyHolder2(convertView);
                    convertView.setTag(holder2);
                } else {
                    holder2 = (MyHolder2) convertView.getTag();
                }
                holder2.tv.setTag(String.valueOf(groupPosition));
                holder2.tv.setText("备注：" + customerList.get(groupPosition).getRemark());
                return convertView;
            }
        };
        pwCustomerElv.setAdapter(pwCustomerElvAdapter);
        //删除预约按钮
        deletepwView = getActivity().getLayoutInflater().inflate(R.layout.layout_popupwindow_delete_service, null);
        deletecusDelete = (Button) deletepwView.findViewById(R.id.bt_popupwindow_delete);
        deletecusCancle = (Button) deletepwView.findViewById(R.id.bt_popupwindow_cancle);

        teammemberView = getActivity().getLayoutInflater().inflate(R.layout.layout_popupwindow_teamlist, null);
        teammemberExpListView = (ExpandableListView) teammemberView.findViewById(R.id.elv_pw_schedule_teamlist);
        teammemberExpAdapter = new TeamlistExpAdapter(mContext, teamList, imageLoader, options);
        teammemberExpListView.setAdapter(teammemberExpAdapter);

        addNotifyView = mActivity.getLayoutInflater().inflate(R.layout.layout_popupwindow_add, null);
        addNotifyView.findViewById(R.id.id_relativelayout).setOnClickListener(this);
        ibPwAddicon = (ImageButton) addNotifyView.findViewById(R.id.ib_schedule_pw_icon);
        tvPwAddconvention = (TextView) addNotifyView.findViewById(R.id.tv_schedule_pw_convention);
        tvPwAddevent = (TextView) addNotifyView.findViewById(R.id.tv_schedule_pw_event);
        tvPwAddinvitation = (TextView) addNotifyView.findViewById(R.id.tv_schedule_pw_invitation);

        initData();
    }

    /**
     * 从本地获取有预约日期和有时间日期的缓存数据
     *
     * @param dates
     */
    private void initScheduleNewStar(final String dates) {
        String str = dates.substring(0, dates.lastIndexOf(SPLITSTR));
        try {
            if (dbUtils.tableIsExist(ScheduleNewStat.class)) {
                List<ScheduleNewStat> ls = dbUtils.findAll(ScheduleNewStat.class);
                if (ls != null) {
                    scheduleNewList2.addAll(ls);
                    for (ScheduleNewStat d : scheduleNewList2) {
                        scheduleNewList.put(d.getDate(), d.getDate());
                        String st = d.getDate();
                        String st2 = st.substring(0, dates.lastIndexOf(SPLITSTR));
                        if (!scheduleNewStatMonth.containsKey(st2)) {
                            scheduleNewStatMonth.put(st2, st2);
                        }
                    }
                }
            }
            if (dbUtils.tableIsExist(ScheduleNewStat_2.class)) {
                List<ScheduleNewStat_2> ls = dbUtils.findAll(ScheduleNewStat_2.class);
                if (ls != null) {
                    scheduleNewList4.addAll(ls);
                    for (ScheduleNewStat_2 ss : scheduleNewList4) {
                        scheduleNewList3.put(ss.getDate(), ss.getDate());
                        String st = ss.getDate();
                        String st2 = st.substring(0, dates.lastIndexOf(SPLITSTR));
                        if (!scheduleNewStatMonth.containsKey(st2)) {
                            scheduleNewStatMonth.put(st2, st2);
                        }
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载指定日期和前后三个月的数据
     *
     * @param dates
     */
    private void downScheduleNewFactory(String dates) {
        String PreStr = TimeUtils.getPreDate(dates);
        String NexStr = TimeUtils.getNextDate(dates);
        String tstr = dates.substring(0, dates.lastIndexOf(SPLITSTR));
        String prestr = PreStr.substring(0, PreStr.lastIndexOf(SPLITSTR));
        String nexstr = NexStr.substring(0, NexStr.lastIndexOf(SPLITSTR));
        if (!scheduleNewStatMonth.containsKey(tstr)) {
            scheduleNewStatMonth.put(tstr, tstr);
            downScheduleNew(dates);
        }
        if (!scheduleNewStatMonth.containsKey(prestr)) {
            scheduleNewStatMonth.put(prestr, prestr);
            downScheduleNew(PreStr);
        }
        if (!scheduleNewStatMonth.containsKey(nexstr)) {
            scheduleNewStatMonth.put(nexstr, nexstr);
            downScheduleNew(NexStr);
        }
    }

    private void getCalengarInfo() {
        //根据当前年/月初始化
        getCalendar(mCurrentYear, mCurrentMonth);
        mCurrentNum = getWeeksOfMonth();
        int currWeek = 0;
        if (mDayOfWeek == 7) {
            currWeek = mCurrDay / 7 + 1;
        } else {
            if (mCurrDay <= (7 - mDayOfWeek)) {
                currWeek = 1;
            } else {
                if ((mCurrDay - (7 - mDayOfWeek)) % 7 == 0) {
                    currWeek = (mCurrDay - (7 - mDayOfWeek)) / 7 + 1;
                } else {
                    currWeek = (mCurrDay - (7 - mDayOfWeek)) / 7 + 2;
                }
            }
        }
        mCurrentWeek = currWeek;
        getCurrent();
    }


    private void initWeekedView() {
        //实例化一个手势监听
        mGestureDetector = new GestureDetector(new MyGestureListener() {
            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                int gvFlag = 0;
                if (event1.getX() - event2.getX() > 80) {
                    // 向左滑
                    addGridView();
                    mCurrentWeek++;
                    getCurrent();
                    mDateAdapter = new WeekendCalendarAdapter(mActivity, mCurrentYear,
                            mCurrentMonth, mCurrentWeek, mCurrentWeek == 1 ? true
                            : false, scheduleNewList, scheduleNewList3);
                    mDateAdapter.setSelectedDay(mLastSelectedDay);
                    mWeekGridView.setAdapter(mDateAdapter);
                    gvFlag++;
                    mWeekFlipper.addView(mWeekGridView, gvFlag);
                    mWeekFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.push_left_in));
                    mWeekFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.push_left_out));
                    mWeekFlipper.showNext();
                    mWeekFlipper.removeViewAt(0);
                    return true;
                    //右划
                } else if (event1.getX() - event2.getX() < -80) {
                    addGridView();
                    mCurrentWeek--;
                    getCurrent();
                    mDateAdapter = new WeekendCalendarAdapter(mActivity, mCurrentYear,
                            mCurrentMonth, mCurrentWeek, mCurrentWeek == 1 ? true
                            : false, scheduleNewList, scheduleNewList3);
                    mDateAdapter.setSelectedDay(mLastSelectedDay);
                    mWeekGridView.setAdapter(mDateAdapter);
                    gvFlag++;
                    mWeekFlipper.addView(mWeekGridView, gvFlag);
                    mWeekFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.push_right_in));
                    mWeekFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.push_right_out));
                    mWeekFlipper.showPrevious();
                    mWeekFlipper.removeViewAt(0);
                    return true;
                }
                return false;
            }
        });
        //滑动显示的控件
        mWeekFlipper = (ViewFlipper) findViewById(R.id.week_flipper);
        mDateAdapter = new WeekendCalendarAdapter(getActivity(), mCurrentYear,
                mCurrentMonth, mCurrentWeek, mCurrentWeek == 1 ? true : false, scheduleNewList, scheduleNewList3);
        addGridView();
        mWeekGridView.setAdapter(mDateAdapter);
        mDateAdapter.setSelectedDay(mLastSelectedDay);
        mDateAdapter.notifyDataSetChanged();
        mWeekFlipper.addView(mWeekGridView, 0);
    }

    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        mWeekGridView = new GridView(mContext);
        mWeekGridView.setNumColumns(GRID_ITEM_COUNT);
        mWeekGridView.setGravity(Gravity.CENTER_VERTICAL);
        mWeekGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mWeekGridView.setBackgroundColor(Color.WHITE);
        mWeekGridView.setVerticalSpacing(1);
        mWeekGridView.setHorizontalSpacing(1);
        mWeekGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        mWeekGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mLastSelectedDay = view.findViewById(R.id.tv_calendar).getTag()
                        .toString();
                int year = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[0]);
                int month = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[1]);
                setNavigationBarDateText(String.valueOf(year),
                        TimeUtils.timeFormat(month));
                mDateAdapter.setSelectedDay(mLastSelectedDay);
                mDateAdapter.notifyDataSetChanged();
                timePast();
            }
        });
        mWeekGridView.setLayoutParams(params);
    }

    // 设置导航栏上的年月变化值
    private void setNavigationBarDateText(String year, String month) {
        cbMonth.setText(year + "年" + month + "月");
    }

    /**
     * 重新计算当前的年月
     */
    public void getCurrent() {
        if (mCurrentWeek > mCurrentNum) {
            if (mCurrentMonth + 1 <= 12) {
                mCurrentMonth++;
            } else {
                mCurrentMonth = 1;
                mCurrentYear++;
            }
            mCurrentWeek = 1;
            mCurrentNum = getWeeksOfMonth(mCurrentYear, mCurrentMonth);
        } else if (mCurrentWeek == mCurrentNum) {
            if (getLastDayOfWeek(mCurrentYear, mCurrentMonth) == 6) {
            } else {
                if (mCurrentMonth + 1 <= 12) {
                    mCurrentMonth++;
                } else {
                    mCurrentMonth = 1;
                    mCurrentYear++;
                }
                mCurrentWeek = 1;
                mCurrentNum = getWeeksOfMonth(mCurrentYear, mCurrentMonth);
            }
        } else if (mCurrentWeek < 1) {
            if (mCurrentMonth - 1 >= 1) {
                mCurrentMonth--;
            } else {
                mCurrentMonth = 12;
                mCurrentYear--;
            }
            mCurrentNum = getWeeksOfMonth(mCurrentYear, mCurrentMonth);
            mCurrentWeek = mCurrentNum - 1;
        }
        if (null != cbMonth) {//修改
            setNavigationBarDateText(String.valueOf(mCurrentYear),
                    TimeUtils.timeFormat(mCurrentMonth));
        }
        downScheduleNewFactory(mCurrentYear + SPLITSTR + mCurrentMonth + SPLITSTR + "01");
    }

    /**
     * 判断某年某月所有的星期数
     *
     * @param year
     * @param month
     */
    public int getWeeksOfMonth(int year, int month) {
        // 先判断某月的第一天为星期几
        int preMonthRelax = 0;
        int dayFirst = getWhichDayOfWeek(year, month);
        int days = mWeekCalendar.getDaysOfMonth(mWeekCalendar.isLeapYear(year),
                month);
        if (dayFirst != 7) {
            preMonthRelax = dayFirst;
        }
        if ((days + preMonthRelax) % 7 == 0) {
            mWeeksOfMonth = (days + preMonthRelax) / 7;
        } else {
            mWeeksOfMonth = (days + preMonthRelax) / 7 + 1;
        }
        return mWeeksOfMonth;

    }

    /**
     * 判断某年某月的第一天为星期几
     *
     * @param year
     * @param month
     * @return
     */
    public int getWhichDayOfWeek(int year, int month) {
        return mWeekCalendar.getWeekdayOfMonth(year, month);
    }

    /**
     * @param year
     * @param month
     */
    public int getLastDayOfWeek(int year, int month) {
        return mWeekCalendar.getWeekDayOfLastMonth(year, month,
                mWeekCalendar.getDaysOfMonth(isLeapyear, month));
    }

    /**
     * 初始化当前日期情况：是否是闰年，月天数，某月第一天是星期几
     *
     * @param year  当前年份
     * @param month 当前月
     */
    public void getCalendar(int year, int month) {
        isLeapyear = mWeekCalendar.isLeapYear(year);
        mDaysOfMonth = mWeekCalendar.getDaysOfMonth(isLeapyear, month);
        mDayOfWeek = mWeekCalendar.getWeekdayOfMonth(year, month);
    }

    //得到每月的周数
    public int getWeeksOfMonth() {
        int preMonthRelax = 0;
        if (mDayOfWeek != 7) {
            preMonthRelax = mDayOfWeek;
        }
        if ((mDaysOfMonth + preMonthRelax) % 7 == 0) {
            mWeeksOfMonth = (mDaysOfMonth + preMonthRelax) / 7;
        } else {
            mWeeksOfMonth = (mDaysOfMonth + preMonthRelax) / 7 + 1;
        }
        return mWeeksOfMonth;
    }


    /**
     * 初始化view
     */
    private void initMonthView() {
        mViewPager = (MonthViewPager) findViewById(R.id.viewpager);
        mPanelCtrl = (CalendarPanel) findViewById(R.id.month_panel);
        mPanelCtrl.setOpen(false, true);
        mPagerAdapter = new MonthPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrPager);// 500->mCurrPager
        mViewPager.setPageMargin(0);

        setNavigationBarDateText(String.valueOf(mCurrentYear), TimeUtils.timeFormat(mCurrentMonth));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int position) {
                if (position == 0) {
                    mCurrentGridView = (GridView) mViewPager.findViewById(mCurrPager);
                    if (mCurrentGridView != null) {
                        mMonthAdapter = (MonthCalendarAdapter) mCurrentGridView.getAdapter();
                        mCurrDateInfoList = mMonthAdapter.getList();
                        mMonthAdapter.setSelectedDay(mLastSelectedDay);
                        mMonthAdapter.notifyDataSetInvalidated();
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                int year = TimeUtils.getTimeByPosition(position, mCurrentYear, mCurrentMonth, YEAR);
                int month = TimeUtils.getTimeByPosition(position, mCurrentYear, mCurrentMonth, MONTH);
                setNavigationBarDateText(String.valueOf(year), TimeUtils.timeFormat(month));
                mCurrPager = position;
                String thisStr = year + SPLITSTR + month + SPLITSTR + "01";
                //下载月有预约数据
                downScheduleNewFactory(thisStr);
            }
        });
    }


    private void alertDialogDelete(int position) {
//        Toast.makeText(getActivity(),"删除的位置是："+position,Toast.LENGTH_LONG).show();
        if (isCusDele) {
            showPopupwindow(deletepwView);
            ((TextView) deletepwView.findViewById(R.id.tv_pw_delete_title)).setText(R.string.cancle_ornot_customer);
        } else {
            if (currentFunncoEvent != null && currentFunncoEvent.getNumbers().equals("0")) {
                //number为0 说明是临时事件 此处处理删除是否显示删除全部
                showPopupwindow(pwView);
                //判断是是单天的数据
//                if (TextUtils.equals(currentFunncoEvent.getRepeat_type() + "", "0") && DateUtils.isSameDate(currentFunncoEvent.getStarttime() + "", currentFunncoEvent.getEndtime() + "")) {//是否是同一天 如果是则隐藏
                if (TextUtils.equals(currentFunncoEvent.getRepeat_type() + "", "0")) {//是否是同一天 如果是则隐藏
                    btDeleAll.setVisibility(View.GONE);
                    btDeleCurr.setText(R.string.delete);
                } else {
                    btDeleAll.setVisibility(View.VISIBLE);
                    btDeleCurr.setText(R.string.deletecurrent);
                }
            } else if (currentFunncoEvent != null && !currentFunncoEvent.getNumbers().equals("0")) {
                showPopupwindow(deletepwView);
                ((TextView) deletepwView.findViewById(R.id.tv_pw_delete_title)).setText("是否取消该预约，此预约已有" + currentFunncoEvent.getList().size() + "人");
            }
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.empty_item).setOnClickListener(this);
        rlHeadl.setOnClickListener(this);
        ivToday.setOnClickListener(this);
        vContent.findViewById(R.id.tv_schedule_myconventation).setOnClickListener(this);
        tvAddNotify.setOnClickListener(this);

        vShareBoard.findViewById(R.id.id_title_0).setOnClickListener(this);//微信好友
        vShareBoard.findViewById(R.id.id_title_1).setOnClickListener(this);//微信朋友圈
        vShareBoard.findViewById(R.id.cancle).setOnClickListener(this);//取消

        btDeleAll.setOnClickListener(this);
        btDeleCurr.setOnClickListener(this);
        btCancle.setOnClickListener(this);
        btPwCancle.setOnClickListener(this);
        deletecusDelete.setOnClickListener(this);
        deletecusCancle.setOnClickListener(this);
        cbMonth.setOnCheckedChangeListener(this);

        tvPwAddinvitation.setOnClickListener(this);
        tvPwAddevent.setOnClickListener(this);
        tvPwAddconvention.setOnClickListener(this);

        findViewById(R.id.tv_schedule_headr).setOnClickListener(this);//右上角发布按钮

        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("--------", "点击的项的位置是：position:" + position + " id : " + id);
                if (position < 2) {
                    return;
                }
                deletePosition = position - 2;
                if (eventList.size() == 1 && TextUtils.isNull(eventList.get(0).getId())) {
                    return;
                }
                currentFunncoEvent = eventList.get(position - 2);
                if (currentFunncoEvent.getNumbers().equals("0")) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("schedule", currentFunncoEvent);
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), AddScheduleActivity.class);
                    startActivityForResult(intent, 101);
                } else {
                    //弹出对话框进行显示已预约客户
                    if (customerList != null && customerList.size() > 0) {
                        customerList.clear();
                    }
                    List<FunncoEventCustomer> ls = currentFunncoEvent.getList();
                    if (ls != null) {
                        customerList.addAll(ls);
                        tvPwTitle.setText("此服务有" + customerList.size() + "位客户，请选择拨打电话的对象");
                    }
//                    showPopupwindow(pwCustomerView);
                    pwCustomer.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                    pwCustomerElvAdapter.notifyDataSetChanged();
                }
            }
        });

        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0, 1500);
                if (refreshCount >= 3) {
                    clearAsyncTask();
                    isLoading = false;
                }
                refreshCount++;
                if (!isLoading) {
                    refreshCount = 0;
                    init();
                }
                //隐藏小圆点
                dismissDesign();
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(0, 1500);
                if (loadmoreCount >= 3) {
                    clearAsyncTask();
                    isLoading = false;
                }
                loadmoreCount++;
                if (!isLoading) {
                }
                //隐藏小圆点
                dismissDesign();
            }
        });
        teammemberView.findViewById(R.id.rlayout_bg).setOnClickListener(this);
        teammemberExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                LogUtils.e("------", "父容器点击的位置是：" + (groupPosition));
                return false;
            }
        });
        teammemberExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LogUtils.e("------", "子容器点击的位置是groupPosition：" + (groupPosition) + "   childPosition:" + (childPosition));
                return true;
            }
        });
    }

    @Override
    protected void init() {
        if (user == null) {
            user = BaseApplication.getInstance().getUser();
        }
        //有网络下载网络数据
        if (NetUtils.isConnection(getActivity()) && user != null) {
            //在从网络获取
            //删除小圆点
            if (newScheduleNewDate.containsKey(mLastSelectedDay)) {
                ((MainActivity) mActivity).postObt(new String[]{mLastSelectedDay + "", newScheduleNewDate.get(mLastSelectedDay) + ""});//减小显示数据
                newScheduleNewDate.remove(mLastSelectedDay);//Map中移除
                newscheduleNewList.remove(0);
            }
            map.clear();
            map.put("dates", mLastSelectedDay);
            if (!TextUtils.isNull(ids) && !TextUtils.isNull(team_id)) {
                map.put("team_uid", ids);
                map.put("team_id", team_id);
            }
            postData2(map, FunncoUrls.getSchedeleListUrl(), false);//下载日程数据
            if (teamList != null && teamList.size() == 0) {
                postData2(null, FunncoUrls.getTeamMemberUrl(), false);//下载团队数据
            }
        } else if (!NetUtils.isConnection(mContext)) {
            if (!NetUtils.isConnection(mContext)) {
                showNetErr();
            }
            //无网络从本地数据库加载数据
            try {
                if (dbUtils.tableIsExist(FunncoEvent.class)) {
                    Selector selector = Selector.from(FunncoEvent.class);
                    selector.where("date_manager", "=", mLastSelectedDay);
                    List<FunncoEvent> ls = dbUtils.findAll(selector);
                    if (ls != null) {
                        eventList.clear();//先清除
                    }
                    if (ls != null && dbUtils.tableIsExist(FunncoEventCustomer.class)) {
                        for (FunncoEvent f : ls) {
                            Selector selector2 = Selector.from(FunncoEventCustomer.class);
                            selector2.where("id2", "=", f.getId());
                            List<FunncoEventCustomer> ls2 = dbUtils.findAll(selector2);
                            if (ls2 != null) {
                                f.setList((ArrayList<FunncoEventCustomer>) ls2);
                            }
                        }
                    }
                    eventList.addAll(ls);
                    if (eventList.size() == 0) {
                        findViewById(R.id.id_textview).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.id_textview).setVisibility(View.GONE);
                    }
                    dismissLoading();
                    adapter.notifyDataSetChanged();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加所有的平台
     */
    private void shareSchedule() {
        showPopupwindow(vShareBoard);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_schedule_addschedule://添加日程
                handler.sendEmptyMessageDelayed(17, 250);
                break;
            case R.id.empty_item:
                handler.sendEmptyMessageDelayed(17, 250);
                break;
            case R.id.tv_schedule_myconventation://我的预约
                startActivityForResult(MyConventionActivity.class, null, REQUEST_CODE_MYCONVENTION);
                break;
            case R.id.tv_schedule_headr://分享日程 移动右上角
                if (user == null) {
                    user = BaseApplication.getInstance().getUser();
                }
                if (user == null) {
                    mActivity.finish();
                    return;
                }
                if (BaseApplication.isFirstUser() && user.getService_count().equals("0")) {
                    sharePwNotify();
                    isFirstuse = false;
                    return;
                }
                shareSchedule();
                return;
            case R.id.bt_pwdelete_cancle:
                break;
            case R.id.bt_pwdelete_deleteall://删除所有日程
//                popupWindow.dismiss();
                deleteSchedule("all");
                break;
            case R.id.bt_pwdelete_deletecurrent://删除当天日程
                if (currentFunncoEvent.getRepeat_type().equals("0")) {
                    deleteSchedule("all");
                } else {
                    deleteSchedule("one");
                }
                break;
            case R.id.bt_popupwindow_cancle:
                break;
            //执行取消已预约客户操作--schedule主界面上
            case R.id.bt_popupwindow_delete:
                if (isCusDele) {//删除pw弹出的预约客户
                    String id = customerList.get(indexCusDele).getId();
//                    String id = currentFunncoEvent.getId();
                    if (TextUtils.isNull(id)) {
                        showSimpleMessageDialog(R.string.user_err);
                        return;
                    }
                    deleteCustomer(id);
                } else {//删除日程列表中的预约客户
                    List<FunncoEventCustomer> ls = currentFunncoEvent.getList();
                    if (null == ls || ls.size() == 0) {
                        showSimpleMessageDialog(R.string.data_err_2fresh);
                        return;
                    }
                    String ids = "";
                    for (int i = 0; i < ls.size(); i++) {
                        ids += ls.get(i).getId() + ",";
                    }
                    if (ids.length() <= 0) {
                        showSimpleMessageDialog(R.string.data_err_2fresh);
                        return;
                    }
                    ids = ids.substring(0, ids.length() - 1);
                    deleteCustomer(ids);
                }

                break;
            //关闭我的预约客户的PopupWindow
            case R.id.bt_pw_schedule_cancle:
                if (pwCustomerElvAdapter != null) {
                    pwCustomerElvAdapter.clearMap();
                }
                break;
            //今天按钮事件
            case R.id.iv_today_icon:
                // 右上角“今天”点击事件
                mLastSelectedDay = TimeUtils.getCurrentDate();
                mCurrentYear = sysYear;
                mCurrentMonth = sysMonth;
                mCurrDay = sysDay;
                mCurrPager = ORI_MONTH_POSITION;// 500为起始加载位置
                mViewPager.setCurrentItem(mCurrPager);
                jumpTheDate(sysYear, sysMonth);
                break;
            case R.id.rlayout_schedule_headl://左上角用户切换
//                showPopupwindow(teammemberView);
                startActivityForResult(new Intent(Actions.ACTION_CHOOSE_MEMBER), REQUEST_CODE_MEMBERCHOOSE);
                return;
            case R.id.rlayout_bg:
                break;
            case R.id.tv_schedule_pw_convention://添加预约
                startActivityForResult(ConventionNewActivity.class, null, REQUEST_CODE_CONVENTION_ADD);
                break;
            case R.id.tv_schedule_pw_event://添加事件
                Bundle bundle = new Bundle();
                bundle.putParcelable("schedule", null);
                intent.putExtras(bundle);
                intent.setClass(getActivity(), AddScheduleActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE_ADD);
                break;
            case R.id.tv_schedule_pw_invitation://添加邀请
                startActivityForResult(InvitationActivity.class, null, REQUEST_CODE_INVITATION_ADD);
                break;
            case R.id.id_title_0://分享到微信好友
                int position = (int) vShareListView.getTag();
                if (position == 0) {
                    WeicatUtils.setShareContent(mContext, mController, user, R.mipmap.common_logo_rectangle);
                } else {
                    WeicatUtils.setShareContent(mContext, mController, teamList.get(position - 1), R.mipmap.common_logo_rectangle);
                }
                mController.postShare(mContext, SHARE_MEDIA.WEIXIN, new SocializeListeners.SnsPostListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
                        if (i == 200) {
                            FunncoUtils.showToast(mContext, R.string.success);
                        } else {
                            String eMsg = "";
                            if (i == -101) {
                                eMsg = "没有授权";
                            }
                            Toast.makeText(mContext, "分享失败[" + i + "] " + eMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.id_title_1://分享到微信朋友圈
                int position1 = (int) vShareListView.getTag();
                if (position1 == 0) {
                    WeicatUtils.setShareContent(mContext, mController, user, R.mipmap.common_logo_rectangle);
                } else {
                    WeicatUtils.setShareContent(mContext, mController, teamList.get(position1 - 1), R.mipmap.common_logo_rectangle);
                }
                mController.postShare(mContext, SHARE_MEDIA.WEIXIN_CIRCLE, new SocializeListeners.SnsPostListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
                        if (i == 200) {
                            FunncoUtils.showToast(mContext, R.string.success);
                        } else {
                            String eMsg = "";
                            if (i == -101) {
                                eMsg = "没有授权";
                            }
                            Toast.makeText(mContext, "分享失败[" + i + "] " + eMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
        dismissPopupwindow();
        closeSwip();
    }

    //删除我的预约客户
    private void deleteCustomer(String id) {
        LogUtils.e("进行取消预约：", "id:" + id);
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.setClass(getActivity(), ReasonCancleActivity.class);
        if (isCusDele) {
            startActivityForResult(intent, REQUEST_CODE_EXL_DEL_REASON);
        } else {
            startActivityForResult(intent, REQUEST_CODE_SDU_DEL);
        }
    }

    /**
     * 删除事件
     *
     * @param type
     */
    private void deleteSchedule(final String type) {
        if (BaseApplication.getInstance().getUser() == null || !NetUtils.isConnection(mContext)) {
            //后续需要处理无网络情况下的操作
            showNetErr();
            return;
        }
        final String id = eventList.get(deletePosition).getId() + "";
        map.clear();
        map.put("id", id);
        map.put("repeat", type);
//        if (type.equals("one")) {
//            map.put("dates", dates);//修改***
        map.put("dates", mLastSelectedDay);
//        }
        AsyTask task = new AsyTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                LogUtils.e("删除日程返回道德数据是：", "" + result);
                if (JsonUtils.getResponseCode(result) == 0) {
//                    if (!type.equals("one")) {//事件删除所有 则移除每天循环的事件集合中的id
//                        repeadEveryDayEventMap.remove(id);
//                        updateAdapterEventdot();
//                    }
                    if (eventList == null || eventList.size() == 0) {
                        scheduleNewList.remove(mLastSelectedDay);
                        updateAdapterNewStart(mLastSelectedDay);
                    }
                    scheduleNewStatMonth.clear();//清除已下载月份 和集合
                    scheduleNewList.clear();
                    scheduleNewList2.clear();
                    scheduleNewList3.clear();
                    scheduleNewList4.clear();
                    downScheduleNewFactory(mLastSelectedDay);//重新请求数据（预约、事件）
//                    showSimpleMessageDialog(R.string.success);
                } else {
//                    showSimpleMessageDialog(R.string.failue);
                }
                dismissLoadingDialog();
            }

            @Override
            public void getBitmap(String rul, Bitmap bitmap) {
                dismissLoadingDialog();
            }
        }, false);
        putAsyncTask(task);
        task.execute(FunncoUrls.getDeleteScheduleUrl());

        eventList.remove(deletePosition);
        deleteById(FunncoEvent.class, id);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //分别控制年份的选择和  月份的选择
        if (buttonView == cbMonth) {//单击的是月控件时
//                Toast.makeText(getActivity(), "处理Month的下拉框。。。", Toast.LENGTH_LONG).show();
            // 导航中月份点击事件
            int year = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[0]);
            mCurrentYear = year;
            int month = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[1]);
            mCurrentMonth = month;
            mCurrDay = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[2]);
            mCurrPager = ORI_MONTH_POSITION;// 500为起始加载位置
            mViewPager.setCurrentItem(mCurrPager);
            if (mPanelCtrl.isOpen()) {
                // 收缩为Week模式
                mPanelCtrl.setOpen(false, true);
                mPanelCtrl.setVisibility(View.GONE);
                mWeekFlipper.setVisibility(View.VISIBLE);
                getCalengarInfo();
                mDateAdapter = new WeekendCalendarAdapter(
                        getActivity(), mCurrentYear, mCurrentMonth,
                        mCurrentWeek, mCurrentWeek == 1 ? true : false, scheduleNewList, scheduleNewList3);
                mDateAdapter.setSelectedDay(mLastSelectedDay);
                mWeekGridView.setAdapter(mDateAdapter);
            } else {
                mWeekFlipper.setVisibility(View.GONE);
                mPanelCtrl.setVisibility(View.VISIBLE);
                mPanelCtrl.setOpen(true, true);
                initData();
                if (null != mMonthAdapter) {
                    mCurrDateInfoList = mDateInfoList;
                    mMonthAdapter.setSelectedDay(mLastSelectedDay);
                    mMonthAdapter.setDateInfoList(mDateInfoList);
                    mMonthAdapter.notifyDataSetInvalidated();
                }
            }
            if (null != cbMonth) {//修改
                setNavigationBarDateText(String.valueOf(year),
                        TimeUtils.timeFormat(month));
            }
        }
    }

    /**
     * 初始化选中年月的整月数据
     */
    private void initData() {
        //初始化当前日期情况：是否是闰年，月天数，某月第一天是星期几
        getCalengarInfo();
        initWeekedView();
        initMonthView();
        String formatDate = TimeUtils.getFormatDate(mCurrentYear, mCurrentMonth);
        try {
            mDateInfoList = TimeUtils.initCalendar(formatDate, mCurrentMonth);
        } catch (Exception e) {
            LogUtils.e("初始化日期数据失败。。。", "");
            getActivity().finish();
        }
    }

    /**
     * 添加服务提示
     */
    private void sharePwNotify() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.layout_schedule_share_guide, null);
        ImageView ivv = (ImageView) v.findViewById(R.id.iv);
        ivv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissPopupwindow();
            }
        });
        showPopupwindow(v);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isCusDele = false;
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("-----------", "Fragment接受到数据了。。。requestCode:" + Integer.toHexString(requestCode) + ",resultCode:" + Integer.toHexString(resultCode));
        /** 使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_CODE_SCHEDULE_ADD || resultCode == RESULT_CODE_SCHEDULE_EDIT) {//修改和添加事件都需要进行刷新
            showLoading(parentView);
            if (data != null) {
                String date = data.getStringExtra("date") + "";
                LogUtils.e("", "Fragment接收到的达特 1：" + date);
                if (!TextUtils.isNull(date) && date.length() == 10 && date.split(SPLITSTR).length == 3) {
                    mLastSelectedDay = date;
                    jump(mLastSelectedDay);
                } else {
                    init();
                }
            } else {
                init();
            }
            scheduleNewStatMonth.clear();//清除已下载月份 和集合
            scheduleNewList.clear();
            scheduleNewList2.clear();
            scheduleNewList3.clear();
            scheduleNewList4.clear();
            downScheduleNewFactory(mLastSelectedDay);//重新请求数据（预约、事件）
        } else if (requestCode == REQUEST_CODE_EXL_DEL_REASON && resultCode == RESULT_CODE_OK) {//ExpandableListView进行删除
            LogUtils.e("删除陈宫-----》AAAAA", "");
            //同时移除数据库中的记录
            SQliteAsynchTask.deleteById(dbUtils, FunncoEventCustomer.class, customerList.get(indexCusDele).getId());

            eventList.get(deletePosition).getList().remove(indexCusDele);
            currentFunncoEvent = eventList.get(deletePosition);
            customerList.clear();
            if (currentFunncoEvent != null) {
                List<FunncoEventCustomer> ls = currentFunncoEvent.getList();
                if (ls != null && ls.size() > 0) {
                    customerList.addAll(ls);
                } else {
                    eventList.remove(deletePosition);
                    String id = currentFunncoEvent.getId();
//                eventList.get(deletePosition).getList().remove(indexCusDele);//修改的地方进行--重新移除
                    SQliteAsynchTask.deleteById(dbUtils, FunncoEvent.class, id + "");
                }
            } else {
                eventList.remove(deletePosition);
                String id = currentFunncoEvent.getId();
                SQliteAsynchTask.deleteById(dbUtils, FunncoEvent.class, id + "");
            }
            if (eventList == null || eventList.size() == 0) {
                scheduleNewList.remove(mLastSelectedDay + "");
                SQliteAsynchTask.deleteById(dbUtils, FunncoEvent.class, currentFunncoEvent.getId() + "");
                SQliteAsynchTask.deleteById(dbUtils, ScheduleNew.class, mLastSelectedDay + "");

            } else if (eventList != null && eventList.size() > 0) {
                int count = 0;
                for (FunncoEvent fe : eventList) {
                    String number = fe.getNumbers();
                    if (!TextUtils.isNull(number) && fe.getList() != null) {
                        count += fe.getList().size();
                    }
                }
                if (count == 0) {
                    scheduleNewList.remove(mLastSelectedDay);
                    updateAdapterNewStart(mLastSelectedDay);
                    SQliteAsynchTask.deleteById(dbUtils, FunncoEvent.class, currentFunncoEvent.getId() + "");
                    SQliteAsynchTask.deleteById(dbUtils, ScheduleNew.class, mLastSelectedDay + "");
                }
            }
            updateAdapterNewStart(mLastSelectedDay);
            pwCustomerElvAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_REASON_CANCLE && resultCode == RESULT_CODE_FAILURE) {//pop删除失败，暂时不做处理

        } else if (requestCode == REQUEST_CODE_SDU_DEL && resultCode == RESULT_CODE_OK) {//Schedule主界面上删除我的预约客户
            LogUtils.e("Schedule主界面上删除我的预约客户成功接收到返回值", "requestCode:" + requestCode + ",resultCode:" + resultCode);
            String id2 = eventList.get(deletePosition).getId();
            SQliteAsynchTask.deleteByBuilder(dbUtils, FunncoEventCustomer.class, "id2", "=", id2 + "");
            SQliteAsynchTask.deleteById(dbUtils, FunncoEvent.class, id2);

            String date = currentFunncoEvent.getDates();
            String id = currentFunncoEvent.getId();
            eventList.remove(deletePosition);
            if (eventList == null || eventList.size() == 0) {//删除光了需要消失掉时间下方预约客户的小圆点
                scheduleNewList.remove(mLastSelectedDay + "");
                //需要刷新适配器
                updateAdapterNewStart(mLastSelectedDay);
                SQliteAsynchTask.deleteById(dbUtils, FunncoEvent.class, id + "");
                SQliteAsynchTask.deleteById(dbUtils, ScheduleNew.class, mLastSelectedDay + "");
            } else if (eventList != null && eventList.size() > 0) {
                int count = 0;
                for (FunncoEvent fe : eventList) {
                    String number = fe.getNumbers();
                    if (!TextUtils.isNull(number) && fe.getList() != null) {
                        count += fe.getList().size();
                    }
                }
                if (count == 0) {
                    scheduleNewList.remove(mLastSelectedDay);
                    updateAdapterNewStart(mLastSelectedDay);
                    SQliteAsynchTask.deleteById(dbUtils, FunncoEvent.class, id + "");
                    SQliteAsynchTask.deleteById(dbUtils, ScheduleNew.class, mLastSelectedDay + "");
                }
            }
            adapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_CODE_SDU_DEL && resultCode == RESULT_CODE_FAILURE) {//sche删除失败同样不做处理
            LogUtils.e("popupwindow删除我的预约客户失败接收到返回值", "requestCode:" + requestCode + ",resultCode:" + resultCode);
        } else if (requestCode == REQUEST_CODE_MYCONVENTION && BaseApplication.needRegresh) {
            BaseApplication.needRegresh = false;
            clearAsyncTask();
            init();
        } else if (requestCode == REQUEST_CODE_MEMBERCHOOSE && resultCode == RESULT_CODE_MEMBERCHOOSE) {
            //团队成员选择完毕,如果是多人,显示多人图标
            if (data != null) {
                ids = data.getStringExtra("ids");
                team_id = data.getStringExtra("team_id");
                String team_name = data.getStringExtra("team_name");
                String headpic = data.getStringExtra("headpic");
                if (TextUtils.isNull(headpic)) {
                    civTitleicon.setImageResource(R.mipmap.my_group);
                } else {
                    imageLoader.displayImage(headpic, civTitleicon);
                }
                LogUtils.e("funnco", "成员选择后的到的数据shi：" + ids + " team_id:" + team_id + "  team_name:" + team_name + " headpic:" + headpic);
                init();//获取日程表
            }
        }
    }

    /**
     * 刷新日历适配器
     *
     * @param dates 传入的日期 是删除预约后需要刷新适配器
     */
    private void updateAdapterNewStart(String dates) {

        if (!TextUtils.isNull(dates)) {
            for (int i = 0; i < scheduleNewList2.size(); i++) {
                String date = scheduleNewList2.get(i).getDate();
                if (!TextUtils.isNull(date) && TextUtils.equals(date, dates)) {
                    scheduleNewList2.remove(i);
                    break;//唯一 因此退出
                }
            }
        }
        //需要刷新适配器
        if (mDateAdapter != null) {
            mDateAdapter.setScheduleNewList(scheduleNewList, scheduleNewList3);
        }
        if (mMonthAdapter != null) {
            mMonthAdapter.setScheduleNewList(scheduleNewList, scheduleNewList3);
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        FunncoUtils.dismissProgressDialog();
        if (url.equals(FunncoUrls.getSchedeleListUrl())) {
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
            if (eventList != null) {
                eventList.clear();
            }
            List<FunncoEvent> ls = JsonUtils.getObjectArray(listJSONArray.toString(), FunncoEvent.class);
            if (ls != null && ls.size() > 0) {
                findViewById(R.id.empty_item).setVisibility(View.GONE);
                try {
                    //网络下载的数据进行添加一个日期字段
                    for (FunncoEvent f : ls) {
                        f.setDate_manager(mLastSelectedDay);
                        List<FunncoEventCustomer> lls = f.getList();
                        if (lls != null) {
                            for (FunncoEventCustomer ff : lls) {
                                ff.setId2(f.getId());
                            }
                            dbUtils.saveOrUpdateAll(lls);
                        }
                    }
                    eventList.addAll(ls);
                    dbUtils.saveOrUpdateAll(eventList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            } else if (ls != null && ls.size() == 0) {
                scheduleNewList.remove(mLastSelectedDay);
                updateAdapterNewStart(mLastSelectedDay);
                findViewById(R.id.empty_item).setVisibility(View.VISIBLE);
            }
            isLoading = false;
            Collections.sort(eventList, new Comparator<FunncoEvent>() {
                @Override
                public int compare(FunncoEvent lhs, FunncoEvent rhs) {
                    return lhs.getTimes().compareTo(rhs.getTimes());
                }
            });
            adapter.notifyDataSetChanged();
            xListView.stopRefresh();
            xListView.stopLoadMore();
            xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
        } else if (url.equals(FunncoUrls.getTeamMemberUrl())) {
            try {
                JSONArray paramsJSONArray = JsonUtils.getJAry(result, "params");
                List<Team> ls = JsonUtils.getObjectArray(paramsJSONArray.toString(), Team.class);
                if (ls != null && ls.size() > 0) {
                    BaseApplication.getInstance().setHasTeam(true);
                    SharedPreferencesUtils.setValue(mContext, "hasTeam", "1");
                    teamList.clear();
                    dbUtils.deleteAll(Team.class);
                    for (int i = 0; i < ls.size(); i++) {
                        Team team = ls.get(i);
                        team.setU_id(user.getId());//给每个Team 设置一个所属Id
                        List<TeamMember> lsTM = team.getList();
                        if (lsTM != null && lsTM.size() > 0) {
                            for (int j = 0; j < lsTM.size(); j++) {
                                lsTM.get(j).setTeam_id(team.getTeam_id());
                                lsTM.get(j).setU_id(user.getId());
                            }
                            SQliteAsynchTask.saveOrUpdate(dbUtils, lsTM);
                        }
                        teamList.add(team);
                    }
                    SQliteAsynchTask.saveOrUpdate(dbUtils, teamList);
                } else if (ls != null && ls.size() == 0) {
                    BaseApplication.getInstance().setHasTeam(false);
                    SharedPreferencesUtils.setValue(mContext, "hasTeam", "0");
                }
                initShareAdapter();
                adapter.notifyDataSetChanged();
                for (int i = 0; i < teamList.size(); i++) {
                    teammemberExpListView.expandGroup(i);
                }
            } catch (DbException e) {

            }
        }
        if (url.equals(FunncoUrls.getAddShceduleUrl())) {

        }
    }

    /**
     * 初始化 分享面板的分享列表
     */
    private void initShareAdapter() {
        vShareListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        vShareListView.setTag(0);
        shareAdapter = new CommonAdapter<Team>(mContext, teamList, R.layout.layout_share_item, new CommonBaseAdapter.CommonImp() {
            @Override
            public int getCommonCount() {
                return teamList.size() + 1;
            }
        }) {
            @Override
            public void convert(ViewHolder helper, Team item, final int position) {
                CheckBox checkBox = helper.getView(R.id.item_name);
                if (position == 0) {
                    helper.setText(R.id.item_name, "自己");
                    helper.setImageByUrl(R.id.item_img, user.getHeadpic());
                    checkBox.setChecked(true);
                } else {
                    Team t = teamList.get(position - 1);
                    helper.setText(R.id.item_name, t.getTeam_name());
                    helper.setImageByUrl(R.id.item_img, t.getCover_pic());
                    checkBox.setChecked(false);
                }
                if (!checkBoxList.contains(checkBox)) {
                    checkBoxList.add(checkBox);
                }
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox checkBox = (CheckBox) view;
                        checkBox.setChecked(true);
                        vShareListView.setTag(position);
                        for (CheckBox ch : checkBoxList) {
                            if (checkBox != ch) {
                                ch.setChecked(false);
                            }
                        }
                    }
                });
            }
        };
        vShareListView.setAdapter(shareAdapter);
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
        FunncoUtils.dismissProgressDialog();
        if (url.equals(FunncoUrls.getSchedeleListUrl())) {

        }
    }

    /**
     * 从第500页开始，最多支持0-1000页
     */
    private class MonthPagerAdapter extends PagerAdapter {
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mCurrentGridView = (GridView) object;
            mMonthAdapter = (MonthCalendarAdapter) mCurrentGridView.getAdapter();
        }

        @Override
        public int getCount() {
            return MAX_MONTH_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //初始化日历
            GridView gv = initCalendarView(position);
            gv.setId(position);
            container.addView(gv);
            return gv;
        }
    }

    /**
     * 初始化日历
     */
    private GridView initCalendarView(int position) {
        int year = TimeUtils.getTimeByPosition(position, mCurrentYear,
                mCurrentMonth, YEAR);
        int month = TimeUtils.getTimeByPosition(position, mCurrentYear,
                mCurrentMonth, MONTH);
        String formatDate = TimeUtils.getFormatDate(year, month);
        try {
            mDateInfoList = TimeUtils.initCalendar(formatDate, month);
        } catch (Exception e) {
            getActivity().finish();//修改
        }
        mMonthGridView = new GridView(mContext);//修改
        mMonthAdapter = new MonthCalendarAdapter(getActivity(), mDateInfoList, scheduleNewList, scheduleNewList3);//修改
        if (position == ORI_MONTH_POSITION) {
            mCurrDateInfoList = mDateInfoList;
            mMonthAdapter.setSelectedDay(mLastSelectedDay);
        }
        mMonthGridView.setAdapter(mMonthAdapter);
        mMonthGridView.setNumColumns(GRID_ITEM_COUNT);
        mMonthGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mMonthGridView.setGravity(Gravity.CENTER);
        mMonthGridView.setOnItemClickListener(new OnItemClickListenerImpl());
        return mMonthGridView;
    }

    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> gridView, View view,
                                int position, long id) {
            MonthCalendar monthCalendar = mCurrDateInfoList.get(position);
            if (monthCalendar.isThisMonth() == false) {
                return;
            }
            mMonthAdapter.setSelectedDay(monthCalendar.getEveryDay());
            mMonthAdapter.notifyDataSetInvalidated();
            mLastSelectedDay = monthCalendar.getEveryDay();
            timePast();
        }
    }

    // 计算选择的日期距离当前时间的差值
    private void timePast() {
        int year = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[0]);
        int month = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[1]);
        int day = Integer.valueOf(mLastSelectedDay.split(SPLITSTR)[2]);

        String _day = TimeUtils.timePast(year + SPLITSTR + month + SPLITSTR
                + day);
        tvScheduleDay.setText(_day);
        tvScheduleDate.setText(TimeUtils.timeFormat(month) + MONTHSTR + TimeUtils.timeFormat(day) + DAYSTR);
        // 1、 http request，自己扩展
        // 2、 ListView填充操作，自己扩展
        ivToday.setVisibility(mLastSelectedDay.equals(TimeUtils.getCurrentDate()) ? View.INVISIBLE : View.VISIBLE);
        //先清楚异步任务，再进行网络请求
        clearAsyncTask();
        showLoading(parentView);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (eventList != null && eventList.size() == 0) {
            init();
        }
        if (xListView != null) {
            xListView.setSelection(0);
        }
        closeSwip();
    }

    //关闭最后一个打开的SwipLayout
    private void closeSwip() {
        if (adapter != null) {
            SwipeLayout swipeLayout = adapter.getSwip();
            if (swipeLayout != null)
                swipeLayout.close(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissPopupwindow();
    }

    /**
     * 显示Popupwindow
     *
     * @param v
     */
    private void showPopupwindow(View v) {
        if (v == addNotifyView) {
            int[] wh = new int[2];
            tvAddNotify.getLocationOnScreen(wh);
            LogUtils.e("funnco", "添加按钮的位置是：" + wh[0] + " " + wh[1]);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvPwAddevent.getLayoutParams();
            params.setMargins(0, wh[1], 0, 0);
            tvPwAddevent.setLayoutParams(params);
        }
        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setClippingEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setSplitTouchEnabled(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 消失Popupwindow
     */
    private boolean dismissPopupwindow() {
        boolean hasPwdismiss = false;
        for (PopupWindow pw : new PopupWindow[]{popupWindow, pwCustomer}) {
            if (pw != null && pw.isShowing()) {
                pw.dismiss();
                hasPwdismiss = true;
            }
        }
        return hasPwdismiss;
    }

    /**
     * 对小圆点进行隐藏
     */
    private void dismissDesign() {
        if (mActivity instanceof Post) {
            ((Post) mActivity).post(0);
        }
    }

    //Activity 回掉的方法/进行中的跳转下载
    @Override
    public void onMainAction(String data) {
        LogUtils.e("主的UI单击了。。。onMainActiononMainActiononMain  33344  ActiononMainActiononMainAction", "null");
        if (newscheduleNewList != null && newscheduleNewList.size() > 0) {
            LogUtils.e("主的UI单击了。。。onMainActiononMainActiononMain  33344  ActiononMainActiononMainAction", "");
            ScheduleNew sn = newscheduleNewList.get(0);
            if (sn != null && !TextUtils.isNull(sn.getDates())) {
                String date = sn.getDates();
                mLastSelectedDay = date;
                jump(mLastSelectedDay);
            }
        }
    }

    private void jump(String date) {
        mCurrentYear = Integer.valueOf(date.split(SPLITSTR)[0]);
        mCurrentMonth = Integer.valueOf(date.split(SPLITSTR)[1]);
        mCurrDay = Integer.valueOf(date.split(SPLITSTR)[2]);
//                  mCurrPager = ORI_MONTH_POSITION;// 500为起始加载位置
        mCurrPager = ORI_MONTH_POSITION + DateUtils.getMonthsBetweenDates(DateUtils.getCurrentDate(), date);
        mViewPager.setCurrentItem(mCurrPager);
        jumpTheDate(mCurrentYear, mCurrentMonth);
    }

    @Override
    public void onMainData(List<?>... list) {
        scheduleNewStatMonth.clear();//有新预约，将原有预约月份的标识清空 以保证新预约的数据被下载
        downScheduleNewFactory(mLastSelectedDay);//启动下载。。。
//        LogUtils.e("", "//MainActivity传递回来的新预约的日期集合" + list[0].size());
        if (list[0] != null && list[0].size() > 0) {
            newScheduleNewDate.clear();
            newscheduleNewList.clear();//只要有传递进来的新数据，说明之前的数据全部失效即使是未读信息数据  也会被替代
            newscheduleNewList.addAll((List<ScheduleNew>) list[0]);
            for (ScheduleNew sn : newscheduleNewList) {
                LogUtils.e("//MainActivity传递回来的新预约的日期集合", "----" + sn);
                newScheduleNewDate.put(sn.getDates() + "", sn.getCounts() + "");
                //需同步没有预约日期的小圆点显示
                if (!scheduleNewList.containsKey(sn.getDates() + "")) {
                    scheduleNewList.put(sn.getDates() + "", sn.getDates() + "");
                }
            }
            updateAdapterNewStart(null);//同时刷新日历小圆点的显示
            Collections.sort(newscheduleNewList, dateComparator);
        } else if (list[0] == null && list[1] != null) {
            int count = (Integer) list[1].get(0);
            designTextView.setText(count);
        }


    }

    private void jumpTheDate(int year, int month) {
        if (mPanelCtrl.isOpen()) {
            LogUtils.e(",,,,,,,,", "处理Month 条件下的今天");
            mWeekFlipper.setVisibility(View.GONE);
            mPanelCtrl.setVisibility(View.VISIBLE);
            initData();
            if (null != mMonthAdapter) {
                mCurrDateInfoList = mDateInfoList;
                mMonthAdapter.setSelectedDay(mLastSelectedDay);
                mMonthAdapter.setDateInfoList(mDateInfoList);
                mMonthAdapter.notifyDataSetInvalidated();
            }
        } else {
            LogUtils.e(",,,,,,,,", "处理Week 条件下的今天");
            mPanelCtrl.setVisibility(View.GONE);
            mWeekFlipper.setVisibility(View.VISIBLE);
            getCalengarInfo();
            mDateAdapter = new WeekendCalendarAdapter(mActivity,
                    mCurrentYear, mCurrentMonth, mCurrentWeek,
                    mCurrentWeek == 1 ? true : false, scheduleNewList, scheduleNewList3);
            mDateAdapter.setSelectedDay(mLastSelectedDay);
            mWeekGridView.setAdapter(mDateAdapter);
        }
        setNavigationBarDateText(String.valueOf(year),
                TimeUtils.timeFormat(month));
        if (isLoading) {
            clearAsyncTask();
        }
        timePast();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return dismissPopupwindow();
    }
}