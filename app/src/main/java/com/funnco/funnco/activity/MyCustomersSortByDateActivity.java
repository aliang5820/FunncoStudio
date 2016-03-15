package com.funnco.funnco.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.SortAdapterDate;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomerDate;
import com.funnco.funnco.bean.MyCustomerDateParent;
import com.funnco.funnco.fragment.MyCustomerFragment;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.support.ParaserCollectionUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 通讯录 按时间排序（未来&历史）
 * Created by user on 2015/5/18.
 * @author Shawn
 */
public class MyCustomersSortByDateActivity extends BaseActivity {

    //标题栏控件
    private Intent intent = null;
    private String title;
    private View parentView;
    private TextView tvNotify;
    private ListView listView;
    private SortAdapterDate adapter;
    //解析数据loading
    private ProgressDialog progressDialog;
    private boolean hasChanged = false;

    //预约客户集合
    private List<MyCustomerDate> list = new ArrayList<>();
    private List<MyCustomerDateParent> listParent = new CopyOnWriteArrayList<>();
    private static final String FORMAT_1 = "yyyy年MM月dd日";
//    private Bundle bundle = new Bundle();
    private Map<String, Object> map = new HashMap<>();
    private static final int REQUEST_EDIT_CUSTOMER_CODE = 0xff04;
    private static int RESULT_COE_UPDATED_MYC = 0xf802;//有修改信息
    private static final int RESULT_SORT_DATE = 0xf415;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_mycustomer_bydate, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {

        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                title = bundle.getString("info");
            }
        }
        progressDialog = new ProgressDialog(mContext);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_mycustomer);
        tvNotify = (TextView) findViewById(R.id.tv_mycustomersortbydate_customernull_notify);
        listView = (ListView) findViewById(R.id.lv_mycustomersortbydate_list);
        adapter = new SortAdapterDate(mContext, list, new Post() {
            @Override
            public void post(int ...position) {
                String tel = list.get(position[0]).getMobile();
                FunncoUtils.callPhone(mContext, tel);
            }
        },imageLoader,options);
        listView.setAdapter(adapter);
        getData();
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCustomerDate customerD = (MyCustomerDate) adapter.getItem(position);
                Intent intent = new Intent(mContext, CustomerInfoActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("userid", customerD.getC_uid());
//                intent.putExtra("mycustomer", customerD);
                startActivityForResult(intent, REQUEST_EDIT_CUSTOMER_CODE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://标题栏的返回按钮
                finishOk();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_COE_UPDATED_MYC) {
            LogUtils.e("----","从数据库中获取数据 。。。。");
            hasChanged = true;
            getData(true);
        }else{
            hasChanged = false;
        }
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
                if (dbUtils != null && dbUtils.tableIsExist(MyCustomerDate.class)) {
                    List<MyCustomerDate> ls = dbUtils.findAll(MyCustomerDate.class);
                    if (ls != null && ls.size() > 0) {
                        AsyncTask task = ParaserCollectionUtils.paraserMyCustomerDate(FORMAT_1, new MyCustomerFragment.PostList() {
                            @Override
                            public void postList(List list2) {
                                List<MyCustomerDate> ls = (List<MyCustomerDate>) list2;
                                if (ls != null && ls.size() > 0) {
                                    LogUtils.e("----","对数据依据时间进行排序  排序前的数据大小是："+list.size());
                                    list.clear();
                                    LogUtils.e("----", "对数据依据时间进行排序  被排序的数据大小是：" + ls.size());
                                    list.addAll(ls);
                                    LogUtils.e("----", "对数据依据时间进行排序  排序后的数据大小是：" + list.size());
                                    //对时间进行排序
                                    Collections.sort(list);
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
                    tvNotify.setVisibility(View.VISIBLE);
                }
            } catch (DbException e) {
                LogUtils.e("----","数据库获取数据异常。。。。");
                e.printStackTrace();
            }
        } else {
            getData4Net();
        }
    }

    private void getData4Net() {
        map.clear();
        map.put("sort", "time");
        map.put("type", "3");
        postData2(map, FunncoUrls.getMyCustomersUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        if (url.equals(FunncoUrls.getMyCustomersUrl())){
            SQliteAsynchTask.deleteAll(dbUtils, MyCustomerDate.class);
            //进行解析
            JSONArray paramsJSONArray = JsonUtils.getJAry(result, "params");
            List<MyCustomerDateParent> ls = JsonUtils.getObjectArray(paramsJSONArray.toString(), MyCustomerDateParent.class);
            if (ls != null && ls.size() > 0) {
                if (list.size() > 0) {
                    list.clear();
                }
                for (MyCustomerDateParent m : ls) {
                    final List<MyCustomerDate> l = m.getList();
                    for (MyCustomerDate mc : l) {
                        mc.setDate(m.getDate());
                        mc.setTip(m.getTip());
                        mc.setWeek(m.getWeek());
                        //保存时将日期转换为毫秒数
                        mc.setDate2(DateUtils.getMills4String(m.getDate(), FORMAT_1) + "");
                    }
                    list.addAll(l);
                }
                LogUtils.e("----", "数据保存：" + SQliteAsynchTask.saveOrUpdate(dbUtils, list));
            }
            if (listParent.size() > 0) {
                listParent.clear();
            }
            listParent.addAll(ls);
            if (list.size() > 0){
                tvNotify.setVisibility(View.GONE);
            }else{
                tvNotify.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
    }

    @Override
    protected void finishOk() {
        if (hasChanged){
            setResult(RESULT_SORT_DATE);
        }
        super.finishOk();
    }
}
