package com.funnco.funnco.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomerConventation;
import com.funnco.funnco.bean.MyCustomerInfo;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.MyGestureListener;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 顾客的个人信息（更多）
 * Created by user on 2015/8/24.
 */
public class CustomerInfoActivity extends BaseActivity {

    private View parentView;
    private String FORMAT_1 = "yyyy-MM-dd";
    private String FORMAT_2 = "yyyy年MM月dd日";

    private TextView tvConventationAll_1,tvConventationAll_2,tvConventationThismonth_1,tvConventationThismonth_2;
    private TextView tvReadmore;
    private ImageView ivTop;
    private TextView tvHeadTop;
    private CircleImageView civIcon;

    private ViewGroup.LayoutParams params;
    private long headTopTime = 0;
    private TextView tvBace, tvEdit;
    private TextView tvTruename, tvRemarkname, tvBirthday, tvWorkphone, tvPersonaldesc;

    private ViewFlipper vf;
    private XListView xlvList;
    private CommonAdapter<MyCustomerConventation> adapter;
    private List<MyCustomerConventation> customerConventationList = new ArrayList<>();

    private int pageIndex = 1;
    private int pageSize = 8;
    private int firstItemPosition = 0;

    private String userId;
    private Intent intent;
//    private boolean isCusD;
    private boolean hasUpdated = false;//有修改信息
//    private MyCustomer customer;
//    private MyCustomerD customerD;
    private MyCustomerInfo customerInfo;//顾客详细信息
    private int customerType = 0;//MyCustomer 表示0 MyCustomerD 表示1 MyCustomerDate表示为1
    //手势监听
    private GestureDetector detector;
    private static int REQUEST_CODE_EDIT = 0xf701;
    private static int RESULT_CODE_EDIT = 0xf702;//0xf702
    private static int REQUEST_CODE_EDIT_CONVENTATION = 0xf703;
    private static int RESULT_CODE_EDIT_CONCENTATION = 0xf704;
    private static int RESULT_COE_UPDATED_MYC = 0xf802;

    private int editPosition;
    private static final String key = "myCustomerConventation";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                getData4Net(userId, false, FunncoUrls.getCustomerScheduleList());
            }
        }
    };

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_customerinfo, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null) {
            customerType = intent.getIntExtra("type",0);
            userId = intent.getStringExtra("userid");
//            Object obt = intent.getParcelableExtra("mycustomer");
//            if (obt instanceof MyCustomer) {
//                isCusD = false;
//                customer = (MyCustomer) obt;
//                userId = customer.getC_uid();
//            } else if (obt instanceof MyCustomerD) {
//                isCusD = true;
//                customerD = (MyCustomerD) obt;
//                userId = customerD.getC_uid();
//            }
//            if (customerType ==100){
//                customerType = isCusD ? 1:0;
//            }
        }

        vf = (ViewFlipper) findViewById(R.id.vf_customer_vf);
        ivTop = (ImageView) findViewById(R.id.iv_customerinfo_movetop);
        civIcon = (CircleImageView) findViewById(R.id.civ_customerinfo_icon);
        xlvList = (XListView) findViewById(R.id.xlv_customerinfo_list);
        xlvList.setPullLoadEnable(true);
        xlvList.setPullRefreshEnable(true);
        xlvList.setHeaderVisibleState(false);//隐藏头部
        xlvList.setFooterVisibleState(false);//隐藏底部
        tvBace = (TextView) findViewById(R.id.tv_headcommon_headl);
        tvEdit = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvEdit.setText(R.string.edit);
        tvHeadTop = (TextView)findViewById(R.id.tv_headcommon_headm);
        tvHeadTop.setText(R.string.str_personalinfo_2);
        tvConventationAll_1 = (TextView) findViewById(R.id.tv_customerinfo_conventation_all_1);
        tvConventationAll_2 = (TextView) findViewById(R.id.tv_customerinfo_conventation_all_2);
        tvConventationThismonth_1 = (TextView) findViewById(R.id.tv_customerinfo_conventation_thismonth_1);
        tvConventationThismonth_2 = (TextView) findViewById(R.id.tv_customerinfo_conventation_thismonth_2);
        tvReadmore = (TextView) findViewById(R.id.tv_customerinfo_readmore);
        tvTruename = (TextView) findViewById(R.id.tv_customerinfo_truename);
        tvRemarkname = (TextView) findViewById(R.id.tv_customerinfo_nickname);
        tvBirthday = (TextView) findViewById(R.id.tv_customerinfo_birthday);
        tvWorkphone = (TextView) findViewById(R.id.tv_customerinfo_phone);
        tvPersonaldesc = (TextView) findViewById(R.id.tv_customerinfo_remark);
        initAdapter();
        getData();

    }

    private void getData() {
        if (!NetUtils.isConnection(mContext)){
//            customerInfo = (MyCustomerInfo) SQliteAsynchTask.selectT(dbUtils,MyCustomerInfo.class,userId);
            try {
                customerInfo = dbUtils.findById(MyCustomerInfo.class,userId);
                List<MyCustomerConventation> ls = dbUtils.findAll(MyCustomerConventation.class);
                if (ls != null && ls.size() > 0){
                    customerConventationList.clear();
                    customerConventationList.addAll(ls);
                }
                adapter.notifyDataSetChanged();
            } catch (DbException e) {
                e.printStackTrace();
            }
            initUIData();
        }else{
            getData4Net(userId, true, FunncoUrls.getCustomerInfo());
        }
    }

    private void initUIData() {
        if (customerInfo != null){
            tvTruename.setText(customerInfo.getTruename() + "");
            String remarkName = customerInfo.getRemark_name();
            tvConventationAll_1.setText(customerInfo.getTotalCount()+"次");
            tvConventationAll_2.setText(customerInfo.getTotalCount()+"次");
            tvConventationThismonth_1.setText(customerInfo.getCurMonthCount()+"次");
            tvConventationThismonth_2.setText(customerInfo.getCurMonthCount()+"次");
            if (!TextUtils.isNull(remarkName) && !TextUtils.equals("null",remarkName)) {
                tvRemarkname.setText(remarkName);
            }
            String birthday = customerInfo.getBirthday();
            if (!TextUtils.isNull(birthday) && birthday.length() == 10){
                tvBirthday.setText(DateUtils.getDate(birthday,FORMAT_1,FORMAT_2));
            }
            String phone = customerInfo.getMobile();
            if (!TextUtils.isNull(phone) && !TextUtils.equals("null", phone)){
                tvWorkphone.setText(phone);
            }
            String desc = customerInfo.getDescription();
            if (!TextUtils.isNull(desc) && !TextUtils.equals("null",desc)){
                tvPersonaldesc.setText(desc);
            }
            imageLoader.displayImage(customerInfo.getHeadpic(),civIcon,options);
        }
    }


    private void initAdapter() {
        adapter = new CommonAdapter<MyCustomerConventation>(mContext, customerConventationList, R.layout.layout_item_customerinfo) {
            @Override
            public void convert(ViewHolder helper, MyCustomerConventation item, int position) {
                helper.setText(R.id.tv_item_customerinfo_servicename, item.getService_name());
                String date = DateUtils.getDate(item.getDates(),FORMAT_1,FORMAT_2);
                helper.setText(R.id.tv_item_customerinfo_conventationdate, date);
                helper.setText(R.id.tv_item_customerinfo_conventationdesc, item.getRemark());
            }
        };
        xlvList.setAdapter(adapter);
    }

    private int currentViewIndex = 0;
    @Override
    protected void initEvents() {
        tvReadmore.setOnClickListener(this);
        tvBace.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        tvHeadTop.setOnClickListener(this);
        ivTop.setOnClickListener(this);
        detector = new GestureDetector(mContext,new MyGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                LogUtils.e("hahah...","手势监听了。。。");
                if (e1.getY() - e2.getY() > 100 && currentViewIndex == 0){
                    currentViewIndex = 1;
                    vf.setInAnimation(mContext, R.anim.set_bottom_in);
                    vf.setOutAnimation(mContext,R.anim.set_top_out);
                    vf.showNext();
                }else if (e1.getY() - e2.getY() < -100 && currentViewIndex == 1){
                    currentViewIndex = 0;
                    vf.setInAnimation(mContext, R.anim.set_top_in);
                    vf.setOutAnimation(mContext,R.anim.set_bottom_out);
                    vf.showPrevious();
                }
                return false;
            }
        });

        xlvList.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                currentViewIndex = 0;
                xlvList.stopRefresh();
                vf.setInAnimation(mContext, R.anim.set_top_in);
                vf.setOutAnimation(mContext, R.anim.set_bottom_out);
                vf.showPrevious();
            }

            @Override
            public void onLoadMore() {
                //加载下一页
                getData4Net(userId,false,FunncoUrls.getCustomerScheduleList());
            }
        });
        xlvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                LogUtils.e(TAG,"XListView滚动监听："+firstVisibleItem);
                if (firstVisibleItem > 0){
                    ivTop.setVisibility(View.VISIBLE);
                }else{
                    ivTop.setVisibility(View.GONE);
                }
            }
        });
        xlvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editPosition = position;
                Intent intent = new Intent();
                intent.setClass(mContext, ConventationInfoActivity.class);
                BaseApplication.getInstance().setT(key, customerConventationList.get(position - 1));
                intent.putExtra("key", key);
                startActivityForResult(intent,REQUEST_CODE_EDIT_CONVENTATION);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.tv_customerinfo_readmore://加载更多
                currentViewIndex = 1;
                vf.setInAnimation(mContext, R.anim.set_bottom_in);
                vf.setOutAnimation(mContext,R.anim.set_top_out);
                vf.showNext();
                break;
            case R.id.tv_headcommon_headr://编辑
                Intent intent = new Intent(mContext, RemarkActivity.class);
//                intent.putExtra("mycustomer", isCusD ? customerD : customer);
                intent.putExtra("type",customerType);
                intent.putExtra("key", "customerInfo");
                BaseApplication.getInstance().setT("customerInfo", customerInfo);
                startActivityForResult(intent, REQUEST_CODE_EDIT);
                break;
            case R.id.tv_headcommon_headm://头部双击移动到顶部
                long time = System.currentTimeMillis();
                if (time - headTopTime <= 1000){
                    xlvList.smoothScrollToPositionFromTop(0,0,400);
                    headTopTime = 0;
                }else{
                    headTopTime = time;
                }
                break;
            case R.id.iv_customerinfo_movetop:
                xlvList.smoothScrollToPositionFromTop(0,0,400);
                break;
        }
    }

    /**
     * 网络获取顾客详细信息/预约列表
     * @param userId 顾客关系Id
     * @param isCustomerInfo 是否是顾客的详细信息
     */
    private void getData4Net(String userId, final boolean isCustomerInfo,String url) {
        showLoading(parentView);
        Map<String,Object> map = new HashMap<>();
        map.put(Constants.CUID,userId);
        if (!isCustomerInfo) {
            map.put("pagesize", pageSize+"");
            map.put("page", pageIndex+"");
        }
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                xlvList.stopLoadMore();
                if (JsonUtils.getResponseCode(result) == 0) {
                    //数据返回正常  进行解析
                    dismissLoading();
                    if (isCustomerInfo) {//用户对象信息
                        handler.sendEmptyMessageDelayed(0, 500);
                        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                        MyCustomerInfo mci = JsonUtils.getObject(paramsJSONObject.toString(), MyCustomerInfo.class);
                        if (mci != null) {
                            customerInfo = mci;
                            initUIData();
                            try {
                                dbUtils.saveOrUpdate(customerInfo);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {//用户预约列表
                        pageIndex++;
                        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                        JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                        if (listJSONArray != null) {
                            List<MyCustomerConventation> mccList = JsonUtils.getObjectArray(listJSONArray.toString(), MyCustomerConventation.class);
                            if (mccList != null) {
                                LogUtils.e(TAG, "下载的客户预约列表  数据保存成功" + SQliteAsynchTask.saveOrUpdate(dbUtils, mccList));
                                customerConventationList.addAll(mccList);
                                adapter.notifyDataSetChanged();
                                xlvList.smoothScrollToPositionFromTop(customerConventationList.size() - mccList.size(), 0, 1000);
                            }
                        }
                    }
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {}
        }, false, url));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_CODE_EDIT){//修改个人信息成功
            customerInfo = (MyCustomerInfo) SQliteAsynchTask.selectT(dbUtils,MyCustomerInfo.class,customerInfo.getId());
            initUIData();
            hasUpdated = true;
        }else if (requestCode == REQUEST_CODE_EDIT_CONVENTATION && resultCode == RESULT_CODE_EDIT_CONCENTATION){//修改预约记录
            MyCustomerConventation mc = (MyCustomerConventation) BaseApplication.getInstance().getT(key);
            if (mc != null && customerConventationList.size()>0){
                customerConventationList.remove(editPosition - 1);
                customerConventationList.add(editPosition - 1, mc);
                adapter.notifyDataSetChanged();
            }
            BaseApplication.getInstance().removeT(key);
            hasUpdated = true;
        }
    }

    protected void finishOk() {
        customerConventationList.clear();
        setResult(RESULT_COE_UPDATED_MYC);
        super.finishOk();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }
}
