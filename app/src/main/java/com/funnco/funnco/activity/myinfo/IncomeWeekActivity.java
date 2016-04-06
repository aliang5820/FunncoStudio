package com.funnco.funnco.activity.myinfo;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.bean.InComeDetailInfo;
import com.funnco.funnco.bean.InComeInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 收入
 * Created by user on 2015/10/28.
 */
public class IncomeWeekActivity extends BaseActivity {

    private View parentView;
    private TextView allIn, weekIn, monthIn, monthOut, currentMonth;
    private XListView xListView;
    private CommonAdapter adapter;
    private List<InComeDetailInfo> list = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (xListView != null) {
                    xListView.stopRefresh();
                    xListView.stopLoadMore();
                }
            }
        }
    };

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_income_week, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_income);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.add_record).setOnClickListener(this);
        allIn = (TextView) findViewById(R.id.allIn);
        weekIn = (TextView) findViewById(R.id.weekIn);
        monthIn = (TextView) findViewById(R.id.monthIn);
        monthOut = (TextView) findViewById(R.id.monthOut);
        currentMonth = (TextView) findViewById(R.id.currentMonth);
        imageLoader = getImageLoader();
        options = getOptions();
        xListView = (XListView) findViewById(R.id.id_listView);
        xListView.setPullLoadEnable(true);
        xListView.setPullRefreshEnable(true);
        xListView.setHeaderVisibleState(true);
        xListView.setFooterVisibleState(false);

        adapter = new CommonAdapter<InComeDetailInfo>(mContext, list, R.layout.layout_income_info_item) {
            @Override
            public void convert(ViewHolder helper, final InComeDetailInfo item, int position) {
                TextView title = helper.getView(R.id.title);
                TextView desc = helper.getView(R.id.desc);
                TextView time = helper.getView(R.id.time);
                TextView price = helper.getView(R.id.price);
                CircleImageView circleImageView = helper.getView(R.id.img);

                //imageLoader.displayImage(memberMap.get(peerId).getHeadpic(), circleImageView);
                if (TextUtils.isNull(item.getTitle())) {
                    title.setText("没有标题");
                } else {
                    title.setText(item.getTitle());
                }
                if (TextUtils.isNull(item.getDesc())) {
                    desc.setText("没有描述");
                } else {
                    desc.setText(item.getDesc());
                }
                time.setText(DateUtils.getDate(item.getAddtime(), "MM月dd日 HH:mm"));
                if (item.getType() == 2) {
                    //2为支出
                    price.setTextColor(getResources().getColor(R.color.color_green_ok));
                    price.setText(item.getNum() + "元");
                } else {
                    //1为收入
                    price.setTextColor(getResources().getColor(R.color.color_hint_gray));
                    price.setText("+" + item.getNum() + "元");
                }
            }
        };
        xListView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        doTask();
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0, 3000);
                doTask();
            }

            @Override
            public void onLoadMore() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0, 3000);
            }
        });
        currentMonth.setText(DateUtils.getCurrentDate("yyyy年MM月"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.add_record:
                startActivity(IncomeWeekAddActivity.class);
                break;
        }
    }

    //获取收支信息
    private void doTask() {
        putAsyncTask(AsyncTaskUtils.requestGet(new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                if (JsonUtils.getResponseCode(result) == 0) {
                    //进行解析
                    JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                    InComeInfo datas = JsonUtils.getObject(paramsJSONObject.toString(), InComeInfo.class);
                    allIn.setText(getResources().getString(R.string.in_out_money, datas.getAllIncome()));
                    weekIn.setText(getResources().getString(R.string.in_out_money, datas.getWeekAccountIncome()));
                    monthIn.setText(getResources().getString(R.string.in_out_money, datas.getMonthAccountIncome()));
                    monthOut.setText(getResources().getString(R.string.in_out_money, datas.getMonthAccountPay()));

                    list.clear();
                    list.addAll(datas.getAccountDetail());
                    xListView.setRefreshTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
                    xListView.stopLoadMore();
                    xListView.stopRefresh();
                    adapter.setmDatas(list);
                } else {
                    showToast(JsonUtils.getResponseMsg(result) + "");
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        }, FunncoUrls.getAccountsUrl(), true));
    }
}
