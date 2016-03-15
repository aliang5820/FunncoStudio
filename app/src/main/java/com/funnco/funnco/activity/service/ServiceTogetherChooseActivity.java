package com.funnco.funnco.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.layout.CheckableFrameLayout;
import com.funnco.funnco.view.listview.XListView;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by user on 2015/6/14.
 */
public class ServiceTogetherChooseActivity extends BaseActivity {

    //底部
    private Button btComplete;
    private View parentView ;
    private XListView xlv;
    private List<Serve> list = new CopyOnWriteArrayList<>();
    private Map<String,String> idMap = new HashMap<>();
    //可同时预约的服务的id 多个用英文逗号隔开
    private StringBuilder relation = new StringBuilder();
    private CommonAdapter<Serve> adapter;
    private Serve serve;
//    private DbUtils dbUtils;
    //左上返回键的返回码
    private static final int RESULT_CODE_CANCLE = 0xff02;
    private static final String SPLITE = ",";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null){
            serve = intent.getParcelableExtra("serve");
        }
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_servicetogether, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
                ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.lanuch_service);
        btComplete = (Button) findViewById(R.id.llayout_foot);
        btComplete.setText(R.string.complete);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);

        xlv = (XListView) findViewById(R.id.xlv_servicetogether_list);
        xlv.setPullRefreshEnable(false);//禁止上拉加载和下拉刷新
        xlv.setPullLoadEnable(false);
        adapter = new CommonAdapter<Serve>(mContext,list,R.layout.layout_item_servicetogether) {
            @Override
            public void convert(ViewHolder helper, final Serve item,int position) {
                helper.setText(R.id.tv_item_servicetogether_servicename, item.getService_name());
                if (idMap.containsKey(item.getId())) {
                    ((CheckBox)helper.getView(R.id.cb_item_servicetogether_ischecked)).setChecked(true);
                }else{
                    ((CheckBox)helper.getView(R.id.cb_item_servicetogether_ischecked)).setChecked(false);
                }
            }
        };
        adapter.isTag(true, new int[]{R.id.cb_item_servicetogether_ischecked});
        xlv.setAdapter(adapter);

        if (list != null && list.size() ==0){
            getServeData();
        }

    }

    @Override
    protected void initEvents() {
        btComplete.setOnClickListener(this);
        xlv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                clearAsyncTask();
                postData2(null, FunncoUrls.getServiceListUrl(), false);
            }
            @Override
            public void onLoadMore() {}
        });
        xlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckableFrameLayout cfl = (CheckableFrameLayout) view;
                boolean isChecked = cfl.isChecked();
                LogUtils.e("------","点击了item  isChecked?"+isChecked);
                if (isChecked) {
                    idMap.put(list.get(position - 1).getId() + "", "");
                } else {
                    idMap.remove(list.get(position - 1).getId() + "");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                idMap.clear();
                relation.setLength(0);
                setResult(RESULT_CODE_CANCLE);
                break;
            case R.id.llayout_foot:
                //完成进行返回
                getRelation();
                Intent intent = new Intent();
                intent.putExtra("relation", relation + "");
                setResult(RESULT_OK, intent);
                break;
        }
        finishOk();
    }

    protected void finishOk() {
        super.finishOk();
        list.clear();
        idMap.clear();
        //可同时预约的服务的id 多个用英文逗号隔开
        relation.setLength(0);
        serve = null;
        intent = null;

    }

    private void getRelation() {
        relation.setLength(0);
        if (idMap == null || idMap.size() <=0){
            return;
        }
        for (String k : idMap.keySet()){
            relation.append(idMap.get(k)+SPLITE);
        }
        relation.deleteCharAt(relation.length() - 1);
    }

    private void getServeData() {
        try {
            if (dbUtils.tableIsExist(Serve.class)){
                List<Serve> ls = dbUtils.findAll(Serve.class);
                if (ls != null && ls.size() > 0){
                    if (list != null && list.size()>0){
                        list.clear();
                    }
                    list.addAll(ls);
                    if (serve != null && !TextUtils.isNull(serve.getId())){
                        for (Serve s : list){
                            if (TextUtils.equals(serve.getId(), s.getId()+"")){
                                list.remove(s);
                            }
                        }
                        String rl = serve.getRelations();
                        if (!TextUtils.isNull(rl)){
                            relation.append(rl);
                            String []ids = rl.split(SPLITE);
                            for (String s : ids) {
                                if (!TextUtils.isNull(s)){
                                    idMap.put(s,s);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }else{
                postData2(null, FunncoUrls.getServiceListUrl(),false);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
        JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
        List<Serve> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Serve.class);
        if (list.size() == 0 && ls != null && ls.size() == 0) {
            BaseApplication.getInstance().setIsFirstLaunchService(true);
        } else if (ls != null && ls.size() > 0) {
            BaseApplication.getInstance().setIsFirstLaunchService(false);
            list.clear();
            list.addAll(ls);
            SQliteAsynchTask.saveOrUpdate(dbUtils,list);
        }
        adapter.notifyDataSetChanged();
    }
}
