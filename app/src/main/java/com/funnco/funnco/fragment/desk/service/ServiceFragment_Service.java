package com.funnco.funnco.fragment.desk.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.PopupWindow;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.activity.service.AddServiceActivity;
import com.funnco.funnco.adapter.ServiceListViewAdapter;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.fragment.desk.ServiceFragment;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.switcher.SwipeLayout;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务
 * Created by user on 2015/9/20.
 */
public class ServiceFragment_Service extends BaseFragment implements View.OnClickListener,Post{

    private View parentView;
    private XListView xListView;
    private ServiceListViewAdapter adapter;
    //数据集合
    private ArrayList<Serve> list = null;
    private Intent intent = null;
    //删除跳出的PopupWindow
    private PopupWindow popDelete;
    private Button btDelete;
    private Button btCancle;
    private View ll_popup;
    private int deletePosition;
    private DbUtils dbutil;
    private Context mContext;
    private UserLoginInfo user;
    private Map<String, Object> map = new HashMap<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                closeXLixtViewFB();
            }
        }
    };
    private void closeXLixtViewFB() {
        if (xListView != null){
            xListView.stopLoadMore();
            xListView.stopRefresh();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mContext == null) {
            mContext = getActivity();
            mActivity = (MainActivity) mContext;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        UserLoginInfo user = BaseApplication.getInstance().getUser();
        if (xListView != null){
            xListView.setSelection(0);
        }

        if (user != null && TextUtils.equals("0", user.getService_count())){
            if (list != null){
                list.clear();
                adapter.notifyDataSetChanged();
            }
            parentView.findViewById(R.id.iv_notify).setVisibility(View.VISIBLE);
        }else if (user != null && !TextUtils.equals("0",user.getService_count())){
            parentView.findViewById(R.id.iv_notify).setVisibility(View.GONE);
            if (list != null && list.size() == 0) {
                getData();
            }
        }
        closeSwip();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_fragment_service_service, container, false);
        init();
        initViews();
        initEvents();
        return parentView;
    }

//    Bundle bundle = new Bundle();
    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_popupwindow_delete:
                    dismissPopupWindow();
                    if (!NetUtils.isConnection(mContext)){//无网络  不进行网络操作
                        showNetErr();
                        return;
                    }
                    Map map = new HashMap();
                    map.put("id", list.get(deletePosition).getId()+"");
                    postData2(map, FunncoUrls.getDeleteServicdeUrl(), false);
                    SQliteAsynchTask.deleteById(dbUtils,Serve.class,list.get(deletePosition).getId());
                    //移除顺序需要特别注意
                    list.remove(deletePosition);
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.bt_popupwindow_cancle:
                    dismissPopupWindow();
                    break;
            }
            closeSwip();
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        xListView.stopLoadMore();
        xListView.stopRefresh();
        if (url.equals(FunncoUrls.getDeleteServicdeUrl())) {
            getData();
        }else if (url.equals(FunncoUrls.getServiceListUrl())){
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
            if (list.size() > 0){
                list.clear();
            }
            List<Serve> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Serve.class);
            if (ls != null && ls.size() > 0){
                list.addAll(ls);
                BaseApplication.getInstance().getUser().setService_count(list.size() + "");
                SQliteAsynchTask.deleteAll(dbUtils, Serve.class);
                SQliteAsynchTask.saveOrUpdate(dbutil,list);
            }
            parentView.findViewById(R.id.iv_notify).setVisibility(View.GONE);
            sortRefresh();
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
    }

    /**
     * 关闭最后一个打开的SwipLayout
     */
    private void closeSwip(){
        if (adapter != null){
            SwipeLayout swipeLayout = adapter.getSwip();
            if (swipeLayout != null){
                swipeLayout.close(true);
            }
        }
    }

    @Override
    public void post(int... position) {
        if (position[0] == 0){
            LogUtils.e("------", "获取服务数据。。。");
            getData();
        }
    }

    @Override
    protected void initViews() {
        user = BaseApplication.getInstance().getUser();
        if (user != null && android.text.TextUtils.equals("0",user.getService_count())){
            parentView.findViewById(R.id.iv_notify).setVisibility(View.VISIBLE);
        }else{
            parentView.findViewById(R.id.iv_notify).setVisibility(View.GONE);
        }
        intent = new Intent();
        dbutil = BaseApplication.getInstance().getDbUtils();
        list = new ArrayList<>();
        xListView = (XListView) parentView.findViewById(R.id.xlv_service_service_list);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(true);
        adapter = new ServiceListViewAdapter(getActivity(), list, new Post() {
            @Override
            public void post(int... position) {
                deletePosition = position[0];
                showPopupWindow();
            }
        });
        xListView.setAdapter(adapter);
        popDelete = new PopupWindow();
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popupwindow_delete_service,null);
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
    }

    @Override
    protected void initEvents() {
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                postData();
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(0,200);
            }
        });
        //设置监听
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Serve serve = list.get(position - 1);
                intent.putExtra("title",getResources().getString(R.string.updateservice));
                intent.putExtra("key", "serve");
                intent.putExtra("isEditService", true);
                BaseApplication.getInstance().setT("serve", serve);
//                bundle.putParcelable("serve", serve);
//                intent.putExtra("data", bundle);
                intent.setClass(getActivity(), AddServiceActivity.class);
                startActivityForResult(intent, 102);
            }
        });

        btCancle.setOnClickListener(this);
        btDelete.setOnClickListener(this);
    }

    @Override
    protected void init() {}
    private void getData(){
        try {
            if (user==null || !NetUtils.isConnection(mContext)) {
                user = BaseApplication.getInstance().getUser();
                if (dbutil.tableIsExist(Serve.class)) {
                    List<Serve> ls = dbutil.findAll(Serve.class);
                    if (ls != null && ls.size() > 0) {
                        list.addAll(ls);
                        parentView.findViewById(R.id.iv_notify).setVisibility(View.GONE);
                        dismissLoading();
                        sortRefresh();
                    }
                }
            }else{
                postData();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对服务列表进行排序，服务类型、服务Id、服务名称
     */
    private void sortRefresh(){
        Collections.sort(list, new Comparator<Serve>() {
            @Override
            public int compare(Serve lhs, Serve rhs) {
                int com = lhs.getService_type().compareTo(rhs.getService_type());
                if (com == 0) {
                    int comm = lhs.getTeam_id().compareTo(rhs.getTeam_id());
                    if (comm == 0) {
                        return (lhs.getService_name() + "").compareTo((rhs.getService_name() + ""));
                    }else{
                        return comm;
                    }
                }else{
                    return com;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }
    /**
     * 网络下载服务数据
     */
    private void postData() {
        showLoading(parentView);
        map.clear();
        map.put("service_type", "0");
        postData2(map, FunncoUrls.getServiceListUrl(), false);
    }
    private void dismissPopupWindow() {
        if (popDelete != null && popDelete.isShowing()){
            popDelete.dismiss();
            ll_popup.clearAnimation();
        }
    }
    private void showPopupWindow(){
        if (popDelete != null && !popDelete.isShowing()){
            ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.activity_translate_in));
            popDelete.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        }
    }
    @Override
    public void onMainAction(String data) { }

    @Override
    public void onMainData(List<?>... list) { }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.e("------","ServiceFragment_Service   requestCode : "+Integer.toHexString(requestCode) + "  resultCode : "+Integer.toHexString(resultCode));
        super.onActivityResult(requestCode, resultCode, data);
    }
}
