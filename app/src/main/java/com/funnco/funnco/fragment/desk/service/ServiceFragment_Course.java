package com.funnco.funnco.fragment.desk.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

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
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.switcher.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程
 * Created by user on 2015/9/20.
 */
public class ServiceFragment_Course extends BaseFragment implements Post, View.OnClickListener {

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
        xListView = new XListView(mContext);
        xListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        init();
        initViews();
        initEvents();
        return xListView;
    }

    @Override
    protected void initViews() {
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
                if (team_id.equals("0")) {
                    helper.getView(R.id.iv_item_courses_teamicon).setVisibility(View.INVISIBLE);
                } else {
                    helper.getView(R.id.iv_item_courses_teamicon).setVisibility(View.VISIBLE);
                }
                //开课人数-课程人数
                helper.setText(R.id.tv_item_courses_number, item.getMin_numbers() + "-" + item.getNumbers() + "人");
                if (position == getPositionForSection(item.getWeek_index())) {
                    helper.getView(R.id.tv_item_courses_weektip).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_item_courses_weektip, DateUtils.getWeek(Integer.valueOf(item.getWeek_index()), "周"));
                } else {
                    helper.getView(R.id.tv_item_courses_weektip).setVisibility(View.GONE);
                }

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

    public int getPositionForSection(String section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getWeek_index();
//            char firstChar = sortStr.toUpperCase().charAt(0);
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }

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
        if (data != null && data.size() > 0) {
            list.clear();
            for (Serve s : data) {
                if (s != null) {
                    String[] weeks = s.getWeeks().split(",");
                    for (String week : weeks) {
                        if (!TextUtils.isNull(week)) {
                            Serve s_2 = (Serve) s.clone();
                            if (week.equals("0")) {
                                s_2.setWeek_index("7");
                            } else {
                                s_2.setWeek_index(week);
                            }
                            list.add(s_2);
                        }
                    }
                }
            }
//                        list.addAll(ls);
            Collections.sort(list, new Comparator<Serve>() {
                @Override
                public int compare(Serve lhs, Serve rhs) {
                    int index = lhs.getWeek_index().compareTo(rhs.getWeek_index());
                    if (index == 0) {
                        return lhs.getService_name().compareTo(rhs.getService_name());
                    }
                    return index;
                }
            });

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
}
