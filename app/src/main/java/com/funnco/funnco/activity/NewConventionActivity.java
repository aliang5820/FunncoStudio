package com.funnco.funnco.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.SortAdapterD_future;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.bean.MyCustomerDParent;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.fragment.MyCustomerFragment;
import com.funnco.funnco.impl.ComparatorByDate;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.MyLoginAsynchTask;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
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
 * 新增预约 (也就是未来预约)
 * Created by user on 2015/9/9.
 */
public class NewConventionActivity extends BaseActivity {

    private View parentView;
    private ListView lv;
    private SortAdapterD_future adapter;
    //解析数据loading
    private ProgressDialog progressDialog;
    //当前日期
    private String dates = DateUtils.getCurrentDate();
    //预约客户集合
    private List<MyCustomerD> list = new ArrayList<>();
    private List<MyCustomerDParent> listParent = new CopyOnWriteArrayList<>();

    private static final int REQUEST_EDIT_CUSTOMER_CODE = 0xff04;
    private static final int RESULT_DELETE_CODE = 4202;
    private static final int RESULT_EDIT_CODE = 4201;

    private static int RESULT_COE_UPDATED_MYC = 0xf802;//有修改信息
    private static final String FORMAT_1 = "yyyy年MM月dd日";
    //传递对象的position
    private int sendPosition = 0;
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_newconvention, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.newconvention);
        progressDialog = new ProgressDialog(mContext);
        lv = (ListView) findViewById(R.id.lv_newconvention_list);
        adapter = new SortAdapterD_future(mContext, list, new Post() {
            @Override
            public void post(int...position) {
                if (position[0] == 0) {//0 代表确认预约
//                    String tel = list.get(position[1]).getMobile();
//                    FunncoUtils.callPhone(mContext, tel);
                    int index = position[2];
                    // 执行预约确认操作


                }else if (position[0] == 1){//1 代表删除
                    //具体实现 删除逻辑
                    showToast("点击了删除。。。"+position[1]);
                }
            }
        },imageLoader,options);
        lv.setAdapter(adapter);
        getData();
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendPosition = position;
                MyCustomerD customerD = (MyCustomerD) adapter.getItem(position);
                Intent intent = new Intent(mContext, CustomerInfoActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("mycustomer", customerD);
                startActivityForResult(intent, REQUEST_EDIT_CUSTOMER_CODE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
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
                if (dbUtils != null && dbUtils.tableIsExist(MyCustomerD.class)) {
                    List<MyCustomerD> ls = dbUtils.findAll(MyCustomerD.class);
                    if (ls != null) {
                        list.clear();
                    }
                    AsyncTask task = ParaserCollectionUtils.paraserMyCustomerD(true, FORMAT_1, new MyCustomerFragment.PostList() {
                        @Override
                        public void postList(List list2) {
                            List<MyCustomerD> ls = (List<MyCustomerD>) list2;
                            if (ls != null && ls.size() > 0) {
                                findViewById(R.id.tv_newconvention_notify).setVisibility(View.GONE);
                                list.addAll(ls);
                                //对时间进行排序
                                Collections.sort(list, new ComparatorByDate());
                                adapter.updateListView(list);
                            }else if (ls != null && ls.size() == 0){
                                findViewById(R.id.tv_newconvention_notify).setVisibility(View.VISIBLE);
                                showSimpleMessageDialog(R.string.str_newconvention_null);
                            }
                        }
                    }, ls, progressDialog);
                    putAsyncTask(task);
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
        MyLoginAsynchTask task = new MyLoginAsynchTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                LogUtils.e("MyCustomerFragment返回的数据是：", "---------" + result);
                if (JsonUtils.getResponseCode(result) == 0) {
                    SQliteAsynchTask.deleteAll(dbUtils, MyCustomerD.class);
                    //进行解析
                    JSONArray paramsJSONArray = JsonUtils.getJAry(result, "params");
                    List<MyCustomerDParent> ls = JsonUtils.getObjectArray(paramsJSONArray.toString(), MyCustomerDParent.class);
                    if (ls != null && ls.size() > 0) {
                        findViewById(R.id.tv_newconvention_notify).setVisibility(View.GONE);
                        if (list.size() > 0) {
                            list.clear();
                        }
                        for (MyCustomerDParent m : ls) {
                            final List<MyCustomerD> l = m.getList();
                            for (MyCustomerD mc : l) {
                                mc.setDate(m.getDate());
                                mc.setTip(m.getTip());
                                mc.setWeek(m.getWeek());
                                //保存时将日期转换为毫秒数
                                mc.setDate2(DateUtils.getMills4String(m.getDate(), FORMAT_1) + "");
                            }
                            list.addAll(l);
                            SQliteAsynchTask.saveOrUpdate(dbUtils, l);
                        }
                    }else if (ls != null && ls.size() == 0){
                        findViewById(R.id.tv_newconvention_notify).setVisibility(View.VISIBLE);
                        showSimpleMessageDialog(R.string.str_newconvention_null);
                    }
                    if (listParent.size() > 0) {
                        listParent.clear();
                    }
                    listParent.addAll(ls);
                    adapter.updateListView(list);
                }
            }

            @Override
            public void getBitmap(String rul, Bitmap bitmap) {
                dismissLoading();
            }
        }, false);
        task.execute(FunncoUrls.getMyCustomersUrl());
        putAsyncTask(task);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_COE_UPDATED_MYC) {
            getData(true);
        }
    }
}
