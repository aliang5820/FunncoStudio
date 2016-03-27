package com.funnco.funnco.activity.work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.bean.WorkItem;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.layout.PullToRefreshView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 2.1新加 我的照片（WorkFragment）
 * Created by user on 2015/10/13.
 */
public class MyWorkActivity extends BaseActivity implements PullToRefreshView.OnHeaderRefreshListener,
        PullToRefreshView.OnFooterRefreshListener{
    private static final int REQUEST_EDIT_WORK = 3001;
    private static final int REQUEST_GALLEY_WORK = 3002;
    private static final int REQUEST_CODE_ADDWORK = 0xff30;
    private static final int RESULT_CODE_ADDWORK = 0xff31;
    private static final String ACTION_WORKUP_SUCCESS = "funnco.upwork.success";
    private TextView tvAddwork;
    private TextView tvEdit;
    private Intent intent;
    private GridView gridView;
    //页数 和容量
    private int pageSize = 8;
    private int pageIndex = 1;
    //作品对象集合
    private List<WorkItem> list = new CopyOnWriteArrayList<>();
    private HttpUtils utils;
    private RequestParams params;
    private CommonAdapter<WorkItem> adapter;
    private boolean isRefresh;//刷新
    private boolean isLoadMore;//加载更多
    //是否在下载网络数据
    private  boolean isNetWorking = false;
    //是否是第一次下载
    private boolean isFirstLoading = true;
    //作品列表 是否下载完毕
    private boolean isworkEnd = false;
    //是否删除对话框
    private AlertDialog.Builder builder;
    //删除的index
    private int deleteIndex;
    //编辑的index
    private int editIndex;
    PullToRefreshView pullToRefreshView;
    //是否修改
    private boolean isEdit = false;
    UserLoginInfo user = null;
    private View parentView;
    //用于记录刷新和下拉的次数
    private int refreshCount,loadmoreCount;
    //用于关闭
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 0){
                if (pullToRefreshView != null) {
                    pullToRefreshView.onFooterRefreshComplete();
                    pullToRefreshView.onHeaderRefreshComplete();
                    isRefresh = false;
                    isLoadMore = false;
                }
            }else if (what == 100){//下载数据
                if (list.size() == 0) {
                    isRefresh = true;
                    isLoadMore = false;
                    pageIndex = 1;
                    getData();
                }
            }
        }
    };
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_fragment_work,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        user = BaseApplication.getInstance().getUser();
        intent = new Intent();
        tvAddwork = (TextView) parentView.findViewById(R.id.tv_work_headr);
        tvEdit = (TextView) parentView.findViewById(R.id.tv_work_headl);
        gridView = (GridView) parentView.findViewById(R.id.gv_work_list);
        pullToRefreshView = (PullToRefreshView) parentView.findViewById(R.id.prv_fragment_work_pullrefresh);

        adapter = new CommonAdapter<WorkItem>(mContext, list, R.layout.layout_item_work) {
            @Override
            public void convert(ViewHolder helper,WorkItem item, int position) {
                helper.setText(R.id.tv_item_work_title, item.getTitle());
                helper.setText(R.id.tv_item_work_date, item.getCreatetime());
                ImageView iv = helper.getView(R.id.iv_item_work_image);
//                iv.setTag(item.getPic_sm());//打上Tag
//                volleyUtils.imageLoader(item.getPic_sm(),iv);//最近使用地方法
                imageLoader.displayImage(item.getPic_sm(), iv, options);
//                helper.setImageByUrl2(R.id.iv_item_work_image, item.getPic_sm());
                ImageButton ib = helper.getView(R.id.ib_item_work_delete);
//                if (!ibList.contains(ib)) {
//                    ibList.add(ib);
//                }
                //新添加。。。 控制删除按钮是否删除？
                helper.getView(R.id.ib_item_work_delete).setVisibility(isEdit ? View.VISIBLE : View.GONE);
                //给删除按钮设置监听
                helper.setListener(R.id.ib_item_work_delete, new Post() {
                    @Override
                    public void post(int ...position) {
                        deleteIndex = position[0];
                        if (builder != null) {
                            builder.create().show();
                        }
                    }
                });
                //给ImageView设置监听
                helper.setListener(R.id.iv_item_work_image, new Post() {
                    @Override
                    public void post(int ...position) {
                        Intent intent = new Intent();
                        intent.putExtra("isTeamwork",false);
                        intent.putExtra(KEY, "workItem");
                        if (isEdit){
                            editIndex = position[0];
//                            intent.putExtra("item",list.get(position[0]));
                            BaseApplication.getInstance().setT("workItem", list.get(editIndex));
                            intent.setClass(mContext, UpdateWorkActivity.class);
                            startActivityForResult(intent,REQUEST_EDIT_WORK);//用于返回修改结果
                        }else {
                            intent.setClass(mContext, GalleryActivity.class);
                            intent.putExtra("position", position[0]);
                            if (BaseApplication.list.size() > 0) {
                                BaseApplication.list.clear();
                            }
                            BaseApplication.list.addAll(list);
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        //给删除按钮打标记
        adapter.isTag(true, new int[]{R.id.ib_item_work_delete});
        gridView.setAdapter(adapter);

        utils = new HttpUtils();
        utils.configCookieStore(BaseApplication.getInstance().getCookieStore());
        utils.configRequestThreadPoolSize(10);
        utils.configResponseTextCharset("utf-8");
        params = new RequestParams("utf-8");
        params.addBodyParameter(Constants.ClIENT, Constants.STR_CLIENT);
        params.addBodyParameter(Constants.LAN, STR_LAN);

        builder = new AlertDialog.Builder(mContext,R.style.dialog_themen);
        builder.setMessage(R.string.delete_y_n);
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!NetUtils.isConnection(mContext)){//无网络条件下，停止删除操作
                    showNetInfo();
                    return;
                }
                //进行删除操作
                if (list.size() > deleteIndex) {
                    WorkItem item = list.get(deleteIndex);
                    if (dbUtils != null){
                        try {
                            dbUtils.deleteById(WorkItem.class, list.get(deleteIndex).getId());
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    list.remove(deleteIndex);
                    adapter.notifyDataSetChanged();

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    deleteWOrk(map, FunncoUrls.getDeleteWorkUrl(),false);
//                    postData2(map, FunncoUrls.getDeleteWorkUrl(), false);
                }
            }
        });


        if (user != null && !TextUtils.isEmpty(user.getWork_count()) && user.getWork_count().equals("0")){
            if (user.getWork_count().equals("0")) {
                parentView.findViewById(R.id.iv_notify).setVisibility(View.VISIBLE);
            }else{
                parentView.findViewById(R.id.iv_notify).setVisibility(View.GONE);
            }
        }else{//逻辑处理错误。。。//修改
            String uid = SharedPreferencesUtils.getValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.UID);
            if (dbUtils == null){
                dbUtils = BaseApplication.getInstance().getDbUtils();
            }
            try {
                if (!TextUtils.isEmpty(uid) && dbUtils != null) {
                    user = dbUtils.findById(UserLoginInfo.class, uid);
                    if (user != null){
                        BaseApplication.getInstance().setUser(user);
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return;
        }
    }
    private void deleteWOrk(Map<String, Object> map,String url,boolean isGet){
        AsyTask task = new AsyTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0){
                    if (list != null && list.size() <= 2){
                        pageIndex = 1;
                        isRefresh = true;
                        isLoadMore = false;
                        getData();
                    }
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        },isGet);
        task.execute(url);
        putAsyncTask(task);
    }

    @Override
    protected void initEvents() {
        tvAddwork.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        pullToRefreshView.setOnFooterRefreshListener(this);
        pullToRefreshView.setOnHeaderRefreshListener(this);
    }
    /**
     * 拿到服务数据
     */
    private void getData() {
        try {
            //从本地获取数据
            if (BaseApplication.getInstance().getUser() == null || !NetUtils.isConnection(mContext)) {
                if (dbUtils.tableIsExist(WorkItem.class)) {
                    List<WorkItem> ls = dbUtils.findAll(WorkItem.class);
                    if (ls != null && ls.size() > 0) {
                        if (list != null && ls!=null && ls.size() >0) {
                            list.clear();
                        }
                        list.addAll(ls);
                        dismissLoading();
                        adapter.notifyDataSetChanged();
                    }
                }
            }else{
                //从网络获取数据
                downWorkList(pageIndex, pageSize);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    private void downWorkList(int pageIndex,int pageSize) {
        isNetWorking = true;
        clearAsyncTask();//清楚其他异步任务
        Map<String,Object> map = new HashMap<>();
        map.put("pagesize", pageSize + "");
        map.put("page", pageIndex + "");
        AsyTask task = new AsyTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                isNetWorking = false;
                if (result != null){
                    if (JsonUtils.getResponseCode(result) == 0){
                        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                        JSONArray listArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                        ArrayList<WorkItem> ls = (ArrayList<WorkItem>) JsonUtils.getObjectArray(listArray.toString(), WorkItem.class);
                        if (ls != null && ls.size() == 0 && list.size() == 0){
                            parentView.findViewById(R.id.iv_notify).setVisibility(View.VISIBLE);
                        }else{
                            if (ls.size() > 0) {
                                user.setWork_count(""+ls.size());
                            }
                            parentView.findViewById(R.id.iv_notify).setVisibility(View.GONE);
                        }
                        isFirstLoading = false;
                        if (isRefresh && !isLoadMore && ls != null && ls.size() > 0) {
                            list.clear();
                            isworkEnd = false;
                        }else if (isRefresh && ls != null && ls.size() == 0){
                            list.clear();
                            isworkEnd = true;
                            try {
                                if (dbUtils != null && dbUtils.tableIsExist(WorkItem.class)) {
                                    dbUtils.deleteAll(WorkItem.class);
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                        list.addAll(ls);
                        try {
                            dbUtils.saveOrUpdateAll(list);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                    if (pullToRefreshView != null){
                        if (isLoadMore)
                            pullToRefreshView.onFooterRefreshComplete();
                        if (isRefresh)
                            pullToRefreshView.onHeaderRefreshComplete();
                    }
                    isRefresh = false;
                    isLoadMore = false;
                    if (isRefresh){
                        pullToRefreshView.onHeaderRefreshComplete(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }

            @Override
            public void getBitmap(String rul,Bitmap bitmap) {
                dismissLoading();
                isNetWorking = false;
                isRefresh = false;
                isLoadMore = false;
            }
        },false);
        task.execute(FunncoUrls.getWorkListUrl());
        putAsyncTask(task);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_work_headr://添加按钮
                intent.setClass(mContext, AddWorkActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADDWORK);
                break;
            case R.id.tv_work_headl://修改按钮
//                for (ImageButton ib : ibList){
//                    ib.setVisibility(isEdit ? View.GONE:View.VISIBLE);
//                }
                isEdit = !isEdit;
                adapter.notifyDataSetChanged();
                tvEdit.setText(getString(isEdit ? R.string.complete : R.string.edit));
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtils.e("WorkFragment 接收到了出递过来的数据", requestCode + "," + resultCode);
        if (requestCode == REQUEST_EDIT_WORK &&resultCode == 103){
            if(data != null) {
                LogUtils.e(TAG, "workFragment 数据有返回，，，" + data.getParcelableExtra("item"));
                WorkItem item = data.getParcelableExtra("item");
                if (item != null) {
                    list.set(editIndex, item);
                }
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == REQUEST_CODE_ADDWORK){
            LogUtils.e("WorkFragment 接收到了出递过来的数据-------RESULT_CODE_ADDWORK",requestCode+","+resultCode);
            isRefresh = true;
            isLoadMore = false;
            pageIndex = 1;
            getData();
            if (handler != null) {
                handler.sendEmptyMessageDelayed(100, 4000);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (list != null && list.size() == 0){
            //初始化数据
            getData();
        }
        if (gridView != null){
            gridView.setSelection(0);
        }
    }
    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(0, 2000);
        }
        if (isNetWorking){
            return ;
        }
        if (isworkEnd){
            pullToRefreshView.onFooterRefreshComplete();
            showToast(R.string.work_isall);
//            showSimpleMessageDialog("已加载全部作品");
            return;
        }
        pageIndex++;
        getData();
        isRefresh = false;
        isLoadMore = true;
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {

        if (handler != null) {
            handler.sendEmptyMessageDelayed(0, 2000);
        }
        if (list.size() > 0) {
            init2();
        }else{
            getData();
        }
    }
    private void init2(){
        int a = pageIndex;
        pageIndex = 1;//临时值
        pageSize = list.size();
        getData();
        pageIndex = a;//恢复
        pageSize = 8;
        isRefresh = true;
        isLoadMore = false;
    }
}
