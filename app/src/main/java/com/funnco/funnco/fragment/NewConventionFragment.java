package com.funnco.funnco.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.CustomerInfoActivity;
import com.funnco.funnco.activity.schedule.ReasonCancleActivity;
import com.funnco.funnco.adapter.SortAdapterD_future;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.bean.MyCustomerDParent;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.support.ParaserCollectionUtils;
import com.funnco.funnco.view.listview.XListView;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的预约 未来
 * Created by user on 2015/6/14.
 */
@SuppressLint("ValidFragment")
public class NewConventionFragment extends BaseFragment {

    private View parentView;
    private XListView xListView;
    private TextView tvNotify;
    private SortAdapterD_future adapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int page = 1;
    private int pagesize = 30;
    private int canclePosition = 0;
    //解析数据loading
    private ProgressDialog progressDialog;
    //当前日期
    private String dates = DateUtils.getCurrentDate();
    //预约客户集合
    private List<MyCustomerD> list = new ArrayList<>();
//    private List<MyCustomerDParent> listParent = new CopyOnWriteArrayList<>();

    private static final int REQUEST_EDIT_CUSTOMER_CODE = 0xff04;
    private static final int REQUEST_CODE_CANCLE_REASON = 0xff14;
    private static final int RESULT_DELETE_CODE = 4202;
    private static final int RESULT_EDIT_CODE = 4201;
    private static final int RESULT_CODE_CANCLE_REASON = 0x1ff000;

    private static int RESULT_COE_UPDATED_MYC = 0xf802;//有修改信息
    private static final String FORMAT_1 = "yyyy年MM月dd日";
    private static final String FORMAT_2 = "yyyy年MM月dd日 HH:mm:ss";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                xListView.stopLoadMore();
            }
        }
    };
    public NewConventionFragment(ImageLoader imageLoader, DisplayImageOptions options){
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_fragment_newconvention, container, false);
        initViews();
        initEvents();
        return parentView;
    }

    @Override
    protected void initViews() {
        xListView = (XListView) parentView.findViewById(R.id.xlv_fragment_newconvention_list);
        tvNotify = (TextView) parentView.findViewById(R.id.tv_fragment_newconvention_notify);
        progressDialog = new ProgressDialog(mContext);
        adapter = new SortAdapterD_future(mContext, list, new Post() {
            @Override
            public void post(int...position) {
                if (position[0] == 0) {//0 代表打电话
                    String tel = list.get(position[1]).getMobile();
                    FunncoUtils.callPhone(mContext, tel);
                }else if (position[0] == 1){//1 代表删除/取消
                    //具体实现 删除逻辑
                    canclePosition = position[1] - 1;
//                    showToast("点击了删除。。。"+position[1]);
                    deleteCustomer(list.get(canclePosition).getId());
                }
            }
        },imageLoader,options);
        xListView.setAdapter(adapter);
        getData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_COE_UPDATED_MYC) {
//            LogUtils.e("----","从数据库中获取。。。");
//            getData(true);
        }else if (requestCode == REQUEST_CODE_CANCLE_REASON && resultCode == RESULT_CODE_CANCLE_REASON){//取消成功
            LogUtils.e("----","移除删除数据。。。");
            try {
                dbUtils.delete(list.get(canclePosition));
                list.remove(canclePosition);
                adapter.updateListView(list);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initEvents() {
        xListView.setPullRefreshEnable(false);
        xListView.setPullLoadEnable(true);
        xListView.setFooterVisibleState(false);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {}

            @Override
            public void onLoadMore() {
                clearAsyncTask();
                handler.removeMessages(0);
                handler.sendEmptyMessageDelayed(0, 2000);
                getData();
            }
        });
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCustomerD customerD = list.get(position - 1);
                Intent intent = new Intent(mContext, CustomerInfoActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("userid",customerD.getC_uid());
//                intent.putExtra("mycustomer", customerD);
                startActivityForResult(intent, REQUEST_EDIT_CUSTOMER_CODE);
            }
        });
    }

    @Override
    protected void init() {
    }
    private void getData() {
        if (BaseApplication.getInstance().getUser() == null || !NetUtils.isConnection(mContext)) {
            if (!NetUtils.isConnection(mContext)) {
                ((BaseActivity) mContext).showNetInfo();
            }
            getData(true);
        }else{
            getData(false);
        }
    }
    private void getData(boolean fromDb) {
        if (fromDb) {
            try {
                if (dbUtils != null && dbUtils.tableIsExist(MyCustomerD.class)) {
                    List<MyCustomerD> ls = dbUtils.findAll(MyCustomerD.class);
                    if (ls != null && ls.size() > 0) {
                        list.clear();
                        AsyncTask task = ParaserCollectionUtils.paraserMyCustomerD(true, FORMAT_1, new MyCustomerFragment.PostList() {
                            @Override
                            public void postList(List list2) {
                                List<MyCustomerD> ls = (List<MyCustomerD>) list2;
                                if (ls != null && ls.size() > 0) {
                                    tvNotify.setVisibility(View.GONE);
                                    list.addAll(ls);
                                    //对时间进行排序
//                                Collections.sort(list, new ComparatorByDate());
                                    Collections.sort(list);
                                    adapter.updateListView(list);
                                } else if (ls != null && ls.size() == 0) {
                                    tvNotify.setVisibility(View.VISIBLE);
                                }
                            }
                        }, ls, progressDialog);
                        putAsyncTask(task);
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            getData4Net();
        }
    }
    private void getData4Net() {
        Map<String, Object> map = new HashMap<>();
        map.put("sort", "time");
        map.put("type", "1");
        map.put("page", page + "");
        map.put("pagesize", pagesize + "");
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                if (JsonUtils.getResponseCode(result) == 0) {
                    SQliteAsynchTask.deleteAll(dbUtils, MyCustomerD.class);
                    //进行解析
                    JSONArray paramsJSONArray = JsonUtils.getJAry(result, "params");
                    List<MyCustomerDParent> ls = JsonUtils.getObjectArray(paramsJSONArray.toString(), MyCustomerDParent.class);
                    if (ls != null && ls.size() > 0) {
                        page++;
                        xListView.setRefreshTime(DateUtils.getCurrentDate(FORMAT_2));
                        for (MyCustomerDParent m : ls) {
                            List<MyCustomerD> l = m.getList();
                            for (MyCustomerD mc : l) {
                                mc.setDate(m.getDate());
                                mc.setTip(m.getTip());
                                mc.setWeek(m.getWeek());
                                //保存时将日期转换为毫秒数
                                mc.setDate2(DateUtils.getMills4String(m.getDate(), FORMAT_1) + "");
                            }
                            list.addAll(l);
                        }
                        LogUtils.e("-----","数据保存是否成功："+SQliteAsynchTask.saveOrUpdate(dbUtils, list));
//                        listParent.addAll(ls);
                    } else if (ls != null && ls.size() == 0) {
                    }
//                    listParent.clear();
                    adapter.updateListView(list);
                }
                if (list != null && list.size() > 0){
                    tvNotify.setVisibility(View.GONE);
                    if (list.size() < 20){
                        xListView.setFooterVisibleState(false);
                    }else{
                        xListView.setFooterVisibleState(true);
                    }
                }else{
                    tvNotify.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        }, false, FunncoUrls.getMyConventionUrl()));
    }

    //删除我的预约客户
    private void deleteCustomer(String id) {
        LogUtils.e("进行取消预约：", "id:" + id);
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.putExtra("convention", true);
        intent.setClass(getActivity(), ReasonCancleActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CANCLE_REASON);
    }

    @Override
    public void onMainAction(String data) {}

    @Override
    public void onMainData(List<?>... list) {}
}
