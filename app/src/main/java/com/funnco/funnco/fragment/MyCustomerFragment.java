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
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.adapter.SortAdapterD;
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
 * 我的预约 （历史）
 * Created by user on 2015/6/14.
 */
@SuppressLint("ValidFragment")
public class MyCustomerFragment extends BaseFragment {

    private TextView tvNotify;
    private XListView xListView;
    private SortAdapterD adapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
//    private VolleyUtils util;
    //解析数据loading
    private ProgressDialog progressDialog;
    private int page = 1;
    private int pagesize = 15;
    private boolean first = true;
    //当前日期
    private String dates = DateUtils.getCurrentDate();
    //排序方式
    //预约客户集合
    private List<MyCustomerD> list = new ArrayList<>();
//    private List<MyCustomerDParent> listParent = new CopyOnWriteArrayList<>();

    private static final int REQUEST_EDIT_CUSTOMER_CODE = 0xff04;
    private static final int RESULT_DELETE_CODE = 4202;
    private static final int RESULT_EDIT_CODE = 4201;

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
    public MyCustomerFragment(ImageLoader imageLoader,DisplayImageOptions options){
        this.imageLoader = imageLoader;
        this.options = options;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_fragment_mycustomer, container, false);
        initViews();
        initEvents();
        return parentView;
    }

    @Override
    protected void initViews() {
        if (mContext == null) {
            mContext = getActivity();
            mActivity = (MainActivity) mContext;
        }
        tvNotify = (TextView) findViewById(R.id.tv_fragment_mycustomer_notify);
        progressDialog = new ProgressDialog(mContext);
//        util = new VolleyUtils(mContext);

        xListView = (XListView) findViewById(R.id.xlv_fragment_myfragment_list);
        adapter = new SortAdapterD(mContext, list, new Post() {
            @Override
            public void post(int ...position) {
                String tel = list.get(position[0] - 1).getMobile();
                FunncoUtils.callPhone(mContext, tel);
            }
        },imageLoader,options);
        xListView.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_COE_UPDATED_MYC) {
//            getData(true);
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
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0,2000);
        if (fromDb) {
            try {
                if (dbUtils != null && dbUtils.tableIsExist(MyCustomerD.class)) {
                    List<MyCustomerD> ls = dbUtils.findAll(MyCustomerD.class);
                    if (ls != null && ls.size() > 0) {

                        AsyncTask task = ParaserCollectionUtils.paraserMyCustomerD(false, FORMAT_1, new PostList() {
                            @Override
                            public void postList(List list2) {
                                List<MyCustomerD> ls = (List<MyCustomerD>) list2;
                                if (ls != null && ls.size() > 0) {
                                    LogUtils.e("----","数据库数据获取前的数据集合大小 ："+list.size());
                                    LogUtils.e("----","数据库数据获取的数据集合大小 ："+ls.size());
                                    list.clear();
                                    list.addAll(ls);
                                    LogUtils.e("----", "数据库数据获取后的数据集合大小 ：" + list.size());
                                    //对时间进行排序
//                                Collections.sort(list, new ComparatorByDate2());
                                    Collections.sort(list);
                                    LogUtils.e("----", "数据库数据获取后 排序后的数据集合大小 ：" + list.size());
                                    adapter.updateListView(list);
                                }
                            }
                        }, ls, progressDialog);
                        putAsyncTask(task);
                    }
                }
                if (list.size() > 0){
                    tvNotify.setVisibility(View.GONE);
                }else{
                    tvNotify.setText(R.string.str_convention_null_notify_2);
                    tvNotify.setVisibility(View.VISIBLE);
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
        map.put("type", "2");
        map.put("page",page+"");
        map.put("pagesize", pagesize+"");
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
                        try {
                            if (first) {
                                dbUtils.deleteAll(MyCustomerD.class);
                                first = false;
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        LogUtils.e("----", "数据保存状态：" + SQliteAsynchTask.saveOrUpdate(dbUtils, list));
//                        listParent.addAll(ls);
                    }
//                    listParent.clear();
                    adapter.updateListView(list);

                    if (list != null && list.size() > 0) {
                        tvNotify.setVisibility(View.GONE);
                        if (list.size() >=15){
                            xListView.setFooterVisibleState(true);
                        }else{
                            xListView.setFooterVisibleState(false);
                        }
                    } else {
                        tvNotify.setText(R.string.str_convention_null_notify_2);
                        tvNotify.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {
            }
        }, false, FunncoUrls.getMyConventionUrl()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (list != null && list.size() == 0) {
            getData();
        }
    }

    @Override
    public void onMainAction(String data) {
    }

    @Override
    public void onMainData(List<?>... list) {

    }
    public interface PostList<T> {
        void postList(List<T> list);
    }
}
