package com.funnco.funnco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.SortAdapter;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomer;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.ComparatorByName;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.support.CharacterParser;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.view.edittext.ClearEditText;
import com.funnco.funnco.view.textview.SideBar;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 我的通讯录
 * Created by user on 2015/5/18.
 * @author Shawn
 */
public class MyCustomersActivity extends BaseActivity {
    //标题栏控件
    private Intent intent;
    private TextView tvNotify;

    private String title;
    private SortAdapter adapter;
    private ListView listView;
    private CharacterParser characterParser;
    private SideBar sideBar;
    private ClearEditText clearEditText;
    private TextView tvDialog;
    private List<MyCustomer> list = new ArrayList<>();
    //用于传递的MyCustomer
    private MyCustomer customer;
    //传递对象所在集合中的位置
    private int index;
    private ComparatorByName comparatorByName;
    private static final int REQUEST_EDIT_CUSTOMER_CODE = 4004;
    private static final int REQUEST_CUSTOMER_CODE = 4005;
    private static final int REQUEST_SORT_DATE = 0xf405;
    private static final int RESULT_DELETE_CODE = 4202;
    private static final int RESULT_EDIT_CODE = 4201;
    private static final int RESULT_SORT_DATE = 0xf415;

    private static int RESULT_COE_UPDATED_MYC = 0xf802;//有修改信息
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_mycustomer,null);
        setContentView(parentView);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CUSTOMER_CODE || requestCode == REQUEST_SORT_DATE) && (resultCode == RESULT_COE_UPDATED_MYC || resultCode == RESULT_SORT_DATE)) {
            try {
                List<MyCustomer> ls = dbUtils.findAll(MyCustomer.class);
                if (ls != null && ls.size() > 0){
                    list.clear();
                    list.addAll(ls);
                    Collections.sort(list);
                    adapter.updateListView(list);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initView() {
        characterParser = new CharacterParser();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                title = bundle.getString("info");
            }
        }
        if (!TextUtils.isEmpty(title)){
            ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(title);
        }
        tvNotify = (TextView) findViewById(R.id.tv_mycustomer_notify);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        clearEditText = (ClearEditText) findViewById(R.id.cet_mycustomer_search);
        tvDialog = (TextView) findViewById(R.id.tv_mycustomer_dialog);
        /*tvSort = (TextView)findViewById(R.id.tv_headcommon_headr);
        tvSort.setText(R.string.sort_by_time);*/
        listView = (ListView) findViewById(R.id.lv_mycustomer_list);
        sideBar = (SideBar) findViewById(R.id.sb_mycustomer_guide);
        sideBar.setTextView(tvDialog);
        comparatorByName = new ComparatorByName();
        //对数据集合进行排序
        Collections.sort(list, comparatorByName);
        adapter = new SortAdapter(mContext,list,imageLoader,options);
        listView.setAdapter(adapter);
        getData();
    }
    @Override
    protected void initEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                customer = (MyCustomer) adapter.getItem(position);
                index = position;
                Intent intent = new Intent();
                intent.putExtra("type",0);
                intent.putExtra("userid",customer.getC_uid());
//                intent.putExtra("mycustomer", customer);
                intent.setClass(mContext,CustomerInfoActivity.class);
                startActivityForResult(intent,REQUEST_CUSTOMER_CODE);
            }
        });
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
        //搜索框的监听事件
        clearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<MyCustomer> filterDateList = new ArrayList<>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = list;
        }else{
            filterDateList.clear();
            for(MyCustomer c : list){
                String name =c.getRemark_name();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(c);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, comparatorByName);
        adapter.updateListView(filterDateList);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_headcommon_headl://标题栏的返回按钮
                finishOk();
                break;
            case R.id.tv_headcommon_headr://时间排序
                //相关排序处理
                /*Intent intent = new Intent(mContext, MyCustomersSortByDateActivity.class);
                startActivityForResult(intent,REQUEST_SORT_DATE);*/
                break;
        }
    }

    /**
     * 获取预约客户数据
     */
    private void getData(){
        if (dbUtils != null){
            try {
                if (BaseApplication.getInstance().getUser() == null || !NetUtils.isConnection(mContext)) {
                    if (dbUtils.tableIsExist(MyCustomer.class)) {
                        List<MyCustomer> ls = dbUtils.findAll(MyCustomer.class);
                        if (ls != null) {
                            list.clear();
                            list.addAll(ls);
                        }
                        if (list.size() > 0){
                            tvNotify.setVisibility(View.GONE);
                        }else{
                            tvNotify.setVisibility(View.VISIBLE);
                        }
                        Collections.sort(list, comparatorByName);
                        adapter.updateListView(list);
                    }
                }else{
                    getData4Net();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从网络获取预约客户数据
     */
    private void getData4Net(){
        Map<String, Object> map = new HashMap<>();
        map.put("sort","letter");
        AsyTask task = new AsyTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                    if (JsonUtils.getResponseCode(result) == 0){
                        JSONObject paramsObt = JsonUtils.getJObt(result, "params");
                        if (list.size() > 0) {
                            list.clear();
                        }
                        Iterator it = paramsObt.keys();
                        //遍历key
                        while(it.hasNext()){
                            String key = (String) it.next();
                            JSONArray arr = JsonUtils.getJAry(paramsObt.toString(), key);
                            List<MyCustomer> ls  = JsonUtils.getObjectArray(arr.toString(), MyCustomer.class);
                            //再次遍历所有对象中的letter字段保证不为空
                            for(MyCustomer c : ls){
                                if (TextUtils.isEmpty(c.getLetter())){
                                    c.setLetter(key);
                                }
                            }
                            if (ls != null && ls.size() > 0){
                                list.addAll(ls);
                            }
                        }
                        if (list.size() > 0){
                            tvNotify.setVisibility(View.GONE);
                        }else{
                            tvNotify.setVisibility(View.VISIBLE);
                        }
                        try {
                            dbUtils.saveOrUpdateAll(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        Collections.sort(list,comparatorByName);
                        adapter.updateListView(list);
                    }
            }
            @Override
            public void getBitmap(String rul,Bitmap bitmap) {}
        },false);
        task.execute(FunncoUrls.getMyCustomersUrl());
        putAsyncTask(task);
    }


}
