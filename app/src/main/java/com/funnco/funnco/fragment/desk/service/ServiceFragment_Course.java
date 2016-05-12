package com.funnco.funnco.fragment.desk.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.service.AddCoursesActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.fragment.desk.ServiceFragment;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.calendar.DateAdapter;
import com.funnco.funnco.view.calendar.SpecialCalendar;
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.switcher.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程
 * Created by user on 2015/9/20.
 */
public class ServiceFragment_Course extends BaseFragment implements Post, View.OnClickListener, GestureDetector.OnGestureListener {

    private XListView xListView;

    private CommonAdapter<Serve> adapter;
    private List<Serve> listSrc = new ArrayList<>();
    private List<Serve> list = new ArrayList<>();
    private SwipeLayout swipeLayout;
    private int openSwipPosition = -1;

    //删除跳出的PopupWindow
    private PopupWindow popDelete;
    private Button btDelete;
    private Button btCancle;
    private View ll_popup;
    private int deletePosition;
    private String selectedDay;
    private static final int REQUEST_CODE_COURSES_EDIT = 0xf09;
    private static final int RESULT_CODE_COURSES_EDIT = 0xf19;
    private static final int RESULT_CODE_COURSES = 0xf17;//课程的添加和修改

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                closeXLixtViewFB();
            }
        }
    };

    private void closeXLixtViewFB() {
        if (xListView != null) {
            xListView.stopLoadMore();
            xListView.stopRefresh();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_course, container, false);
        xListView = (XListView) parentView.findViewById(R.id.xListView);
        init();
        initViews();
        initEvents();
        return parentView;
    }

    @Override
    protected void initViews() {
        /***************/
        gestureDetector = new GestureDetector(this);
        flipper1 = (ViewFlipper) parentView.findViewById(R.id.flipper1);
        selectedDay = year_c + "年" + month_c + "月" + day_c + "日";
        dateAdapter = new DateAdapter(mContext, getResources(), currentYear,
                currentMonth, currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false, selectedDay);
        addGridView();
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        selectPostion = dateAdapter.getTodayPosition();
        gridView.setSelection(selectPostion);
        flipper1.addView(gridView, 0);
        /***************/

        xListView.setFooterVisibleState(false);
        xListView.setHeaderVisibleState(false);
        xListView.setSelected(false);
        xListView.setDividerHeight(0);
//        xListView.setDivider(getResources().getDrawable(R.color.color_hint_gray_light));
        xListView.setFooterDividersEnabled(false);
        xListView.setHeaderDividersEnabled(false);
        xListView.setPullRefreshEnable(true);
        adapter = new CommonAdapter<Serve>(mContext, list, R.layout.layout_item_courses) {
            @Override
            public void convert(ViewHolder helper, Serve item, final int position) {
                //服务名称
                helper.setText(R.id.tv_item_courses_name, item.getService_name() + "");
                String team_id = item.getTeam_id();
                //服务描述
                String des = item.getDescription();
                if (!TextUtils.isNull(des)) {
                    helper.getView(R.id.tv_item_courses_desc).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_item_courses_desc, des);
                } else {
                    helper.getView(R.id.tv_item_courses_desc).setVisibility(View.GONE);
                }
                /*if (team_id.equals("0")) {
                    helper.getView(R.id.iv_item_courses_teamicon).setVisibility(View.INVISIBLE);
                } else {
                    helper.getView(R.id.iv_item_courses_teamicon).setVisibility(View.VISIBLE);
                }*/
                //开课人数-课程人数
                helper.setText(R.id.tv_item_courses_number, item.getMin_numbers() + "-" + item.getNumbers() + "人");
                helper.setText(R.id.tv_item_courses_price, item.getPrice() + "元/" + item.getNumbers() + "人");
                /*if (position == getPositionForSection(item.getWeek_index())) {
                    helper.getView(R.id.tv_item_courses_weektip).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_item_courses_weektip, DateUtils.getWeek(Integer.valueOf(item.getWeek_index()), "周"));
                } else {*/
                    helper.getView(R.id.tv_item_courses_weektip).setVisibility(View.GONE);
                //}

//                helper.setText(R.id.tv_item_courses_price, item.getPrice());
                String stime = item.getStarttime();
                String etime = item.getEndtime();
                int sstime = Integer.valueOf(stime);
                int eetime = Integer.valueOf(etime);
                if (!TextUtils.isNull(stime) && !TextUtils.isNull(etime)) {
                    helper.setText(R.id.tv_item_courses_time, DateUtils.getTime4Minutes(sstime) + "~" + DateUtils.getTime4Minutes(eetime));
                }

                final SwipeLayout swip = helper.getView(R.id.swip);
                if (position == openSwipPosition) {
                    swip.open();
                } else {
                    swip.close();
                }

                swip.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
                        super.onOpen(layout);
                        if (layout != swipeLayout) {
                            swipeLayout = layout;
                        }
                        openSwipPosition = position;
                    }

                    @Override
                    public void onClose(SwipeLayout layout) {
                        super.onClose(layout);
                        if (swipeLayout == layout) {
                            openSwipPosition = -1;
                        }
                    }

                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        super.onStartOpen(layout);
                        if (swipeLayout != null && swipeLayout != layout) {
                            swipeLayout.close();
                        }
                    }
                });
                helper.setCommonListener(R.id.delete, new Post() {
                    @Override
                    public void post(int... position) {
//                        showToast("点击了。。。" + position[0]);
                        //处理删除课程的逻辑
                        deletePosition = position[0];
                        showPopupWindow();
                        swip.close();
                    }
                });
            }
        };
        adapter.isTag(true, new int[]{R.id.delete});
        xListView.setAdapter(adapter);
        popDelete = new PopupWindow();
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popupwindow_delete_service, null);
        ll_popup = popView.findViewById(R.id.llayout_popupwindow);
        btDelete = (Button) popView.findViewById(R.id.bt_popupwindow_delete);
        btCancle = (Button) popView.findViewById(R.id.bt_popupwindow_cancle);
        popDelete.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popDelete.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popDelete.setBackgroundDrawable(new BitmapDrawable());
        popDelete.setFocusable(true);
        popDelete.setOutsideTouchable(true);
        popDelete.setClippingEnabled(true);
        popDelete.setContentView(popView);
        getData();
    }

    /*public int getPositionForSection(String section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getWeek_index();
//            char firstChar = sortStr.toUpperCase().charAt(0);
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }*/

    @Override
    public void post(int... position) {
        if (position[0] == 0) {
            LogUtils.e("------", "获取课程数据。。。");
            getData();
        }
    }

    private void getData() {
        if (BaseApplication.getInstance().getUser() != null && NetUtils.isConnection(mContext)) {
            getData4Net();
        } else {
            getData4Db();
        }
    }

    private void getData4Db() {
        List<Serve> ls = SQliteAsynchTask.selectTall(dbUtils, Serve.class, new String[]{"service_type"}, new String[]{"="}, new String[]{"1"});
        if (ls != null && ls.size() > 0) {
            listSrc.clear();
            list.clear();
            listSrc.addAll(ls);
            reCreateData(listSrc);
        }
    }

    private void getData4Net() {
        Map<String, Object> map = new HashMap<>();
        map.put("service_type", "1");
//        map.put("team_id","");
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0) {
                    LogUtils.e("------", "数据返回正常，进行解析");
                    JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                    JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                    List<Serve> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Serve.class);
                    if (ls != null && ls.size() > 0) {
                        listSrc.clear();
                        listSrc.addAll(ls);
                        reCreateData(listSrc);
                    } else {
                        reCreateData(null);
                    }
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {
            }
        }, false, FunncoUrls.getServiceListUrl()));
    }

    private void reCreateData(List<Serve> data) {
        int current = week_num;
        if(gridView.getTag() != null) {
            current = (int) gridView.getTag();
        }
        if (data != null && data.size() > 0) {
            list.clear();
            for (Serve s : data) {
                if (s != null) {
                    String[] weeks = s.getWeeks().split(",");
                    for (String week : weeks) {
                        if (!TextUtils.isNull(week) && Integer.valueOf(week) == current) {
                            /*Serve s_2 = (Serve) s.clone();
                            if (week.equals("0")) {
                                s_2.setWeek_index("7");
                            } else {
                                s_2.setWeek_index(week);
                            }
                            list.add(s_2);*/
                            list.add(s);
                        }
                    }
                }
            }
//                        list.addAll(ls);
            //目前无需排序
            /*Collections.sort(list, new Comparator<Serve>() {
                @Override
                public int compare(Serve lhs, Serve rhs) {
                    int index = lhs.getWeek_index().compareTo(rhs.getWeek_index());
                    if (index == 0) {
                        return lhs.getService_name().compareTo(rhs.getService_name());
                    }
                    return index;
                }
            });*/

            SQliteAsynchTask.saveOrUpdate(dbUtils, data);
        } else {
            listSrc.clear();
            list.clear();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initEvents() {
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0, 200);
                xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                clearAsyncTask();
                getData();
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(0, 200);
            }
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("------", "" + position);
                if (position >= 0 && position <= list.size()) {
                    Intent intent2 = new Intent();
                    intent2.setClass(mContext, AddCoursesActivity.class);
                    Serve s = list.get(position - 1);
                    BaseApplication.getInstance().setT("serve", s);
                    intent2.putExtra("key", "serve");
                    startActivityForResult(intent2, REQUEST_CODE_COURSES_EDIT);
                }
            }
        });
        btDelete.setOnClickListener(this);
        btCancle.setOnClickListener(this);
    }

    private void showPopupWindow() {
        if (popDelete != null && !popDelete.isShowing()) {
            ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.activity_translate_in));
            popDelete.showAtLocation(xListView, Gravity.BOTTOM, 0, 0);
        }
    }

    private boolean dismissPopupWindow() {
        boolean hasPw = false;
        for (PopupWindow pw : new PopupWindow[]{popDelete}) {
            if (pw != null && pw.isShowing()) {
                pw.dismiss();
                hasPw = true;
            }
        }
        return hasPw;
    }

    private void closeSwip() {
        if (swipeLayout != null) {
            swipeLayout.close();
        }
    }

    @Override
    protected void init() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
        currentYear = year_c;
        currentMonth = month_c;
        currentDay = day_c;
        sc = new SpecialCalendar();
        getCalendar(year_c, month_c);
        week_num = getWeeksOfMonth();
        currentNum = week_num;
        if (dayOfWeek == 7) {
            week_c = day_c / 7 + 1;
        } else {
            if (day_c <= (7 - dayOfWeek)) {
                week_c = 1;
            } else {
                if ((day_c - (7 - dayOfWeek)) % 7 == 0) {
                    week_c = (day_c - (7 - dayOfWeek)) / 7 + 1;
                } else {
                    week_c = (day_c - (7 - dayOfWeek)) / 7 + 2;
                }
            }
        }
        currentWeek = week_c;
        getCurrent();
    }

    @Override
    public void onMainAction(String data) {
    }

    @Override
    public void onMainData(List<?>... list) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.e("------", "ServiceFragment_Courses   requestCode : " + Integer.toHexString(requestCode) + "  resultCode : " + Integer.toHexString(resultCode));
        if (requestCode == REQUEST_CODE_COURSES_EDIT && resultCode == RESULT_CODE_COURSES) {
            getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_popupwindow_delete:
                dismissPopupWindow();
                if (!NetUtils.isConnection(mContext)) {//无网络  不进行网络操作
                    showNetErr();
                    return;
                }
                Map map = new HashMap();
                String id = list.get(deletePosition).getId() + "";
                map.put("id", id);
                postData2(map, FunncoUrls.getDeleteServicdeUrl(), false);
                SQliteAsynchTask.deleteById(dbUtils, Serve.class, list.get(deletePosition).getId());
                //移除顺序需要特别注意
//                list.remove(deletePosition);
                for (int i = 0; i < listSrc.size(); i++) {
                    Serve s = listSrc.get(i);
                    if (s.getId().equals(id)) {
                        listSrc.remove(s);
                    }
                }
                reCreateData(listSrc);
                break;
            case R.id.bt_popupwindow_cancle:
                dismissPopupWindow();
                break;
        }
        closeSwip();
    }

    /**
     *
     */
    private ViewFlipper flipper1 = null;
    // private ViewFlipper flipper2 = null;
    private static String TAG = "ZzL";
    private GridView gridView = null;
    private GestureDetector gestureDetector = null;
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private int week_c = 0;
    private int week_num = 0;
    private String currentDate = "";
    private static int jumpWeek = 0;
    private static int jumpMonth = 0;
    private static int jumpYear = 0;
    private DateAdapter dateAdapter;
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int weeksOfMonth = 0;
    private SpecialCalendar sc = null;
    private boolean isLeapyear = false; // 是否为闰年
    private int selectPostion = 0;
    private String dayNumbers[] = new String[7];
    private int currentYear;
    private int currentMonth;
    private int currentWeek;
    private int currentDay;
    private int currentNum;
    private boolean isStart;// 是否是交接的月初

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
        int days = sc.getDaysOfMonth(sc.isLeapYear(year), month);
        if (dayFirst != 7) {
            preMonthRelax = dayFirst;
        }
        if ((days + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (days + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (days + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;

    }

    /**
     * 判断某年某月的第一天为星期几
     *
     * @param year
     * @param month
     * @return
     */
    public int getWhichDayOfWeek(int year, int month) {
        return sc.getWeekdayOfMonth(year, month);

    }

    /**
     * @param year
     * @param month
     */
    public int getLastDayOfWeek(int year, int month) {
        return sc.getWeekDayOfLastMonth(year, month,
                sc.getDaysOfMonth(isLeapyear, month));
    }

    public void getCalendar(int year, int month) {
        isLeapyear = sc.isLeapYear(year); // 是否为闰年
        daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
    }

    public int getWeeksOfMonth() {
        // getCalendar(year, month);
        int preMonthRelax = 0;
        if (dayOfWeek != 7) {
            preMonthRelax = dayOfWeek;
        }
        if ((daysOfMonth + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;
    }

    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        gridView = new GridView(mContext);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //选中日期
                Log.i(TAG, "day:" + dayNumbers[position]);
                selectPostion = position;
                dateAdapter.setSeclection(position);
                dateAdapter.notifyDataSetChanged();
                selectedDay = dateAdapter.getCurrentYear(selectPostion) + "年"
                        + dateAdapter.getCurrentMonth(selectPostion) + "月"
                        + dayNumbers[position] + "日";

                Log.i(TAG, dateAdapter.getCurrentYear(selectPostion) + "年"
                        + dateAdapter.getCurrentMonth(selectPostion) + "月"
                        + dayNumbers[position] + "日");
                gridView.setTag(position);
                reCreateData(listSrc);
            }
        });
        gridView.setLayoutParams(params);
    }

    @Override
    public void onPause() {
        super.onPause();
        jumpWeek = 0;
    }

    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * 重新计算当前的年月
     */
    public void getCurrent() {
        if (currentWeek > currentNum) {
            if (currentMonth + 1 <= 12) {
                currentMonth++;
            } else {
                currentMonth = 1;
                currentYear++;
            }
            currentWeek = 1;
            currentNum = getWeeksOfMonth(currentYear, currentMonth);
        } else if (currentWeek == currentNum) {
            if (getLastDayOfWeek(currentYear, currentMonth) == 6) {
            } else {
                if (currentMonth + 1 <= 12) {
                    currentMonth++;
                } else {
                    currentMonth = 1;
                    currentYear++;
                }
                currentWeek = 1;
                currentNum = getWeeksOfMonth(currentYear, currentMonth);
            }

        } else if (currentWeek < 1) {
            if (currentMonth - 1 >= 1) {
                currentMonth--;
            } else {
                currentMonth = 12;
                currentYear--;
            }
            currentNum = getWeeksOfMonth(currentYear, currentMonth);
            currentWeek = currentNum - 1;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int gvFlag = 0;
        if (e1.getX() - e2.getX() > 80) {
            // 向左滑
            addGridView();
            currentWeek++;
            getCurrent();
            dateAdapter = new DateAdapter(mContext, getResources(), currentYear,
                    currentMonth, currentWeek, currentNum, selectPostion,
                    currentWeek == 1 ? true : false, selectedDay);
            dayNumbers = dateAdapter.getDayNumbers();
            gridView.setAdapter(dateAdapter);
            /*tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                    + dateAdapter.getCurrentMonth(selectPostion) + "月"
                    + dayNumbers[selectPostion] + "日");*/
            gvFlag++;
            flipper1.addView(gridView, gvFlag);
            //dateAdapter.setSeclection(selectPostion);
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out));
            this.flipper1.showNext();
            flipper1.removeViewAt(0);
            return true;

        } else if (e1.getX() - e2.getX() < -80) {
            addGridView();
            currentWeek--;
            getCurrent();
            dateAdapter = new DateAdapter(mContext, getResources(), currentYear,
                    currentMonth, currentWeek, currentNum, selectPostion,
                    currentWeek == 1 ? true : false, selectedDay);
            dayNumbers = dateAdapter.getDayNumbers();
            gridView.setAdapter(dateAdapter);
            /*tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                    + dateAdapter.getCurrentMonth(selectPostion) + "月"
                    + dayNumbers[selectPostion] + "日");*/
            gvFlag++;
            flipper1.addView(gridView, gvFlag);
            //dateAdapter.setSeclection(selectPostion);
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_out));
            this.flipper1.showPrevious();
            flipper1.removeViewAt(0);
            return true;
            // }
        }
        return false;
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }*/

}
