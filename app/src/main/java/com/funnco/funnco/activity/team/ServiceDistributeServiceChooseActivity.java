package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.bean.TeamMemberMy;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;
import com.funnco.funnco.view.switcher.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队服务分配-- 服务选择
 * Created by user on 2015/9/29.
 */
public class ServiceDistributeServiceChooseActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private View vInfo;
    private XListView xListView;
    private CommonAdapter<Serve> adapter;
    private List<Serve> list = new ArrayList<>();
    private TeamMemberMy teamMemberMy;
    private Intent intent;
    private String team_id;
    private String team_uid;
    private String ids;//已有的服务和课程 的ids
    private String names;
    private Map<String, Object> map = new HashMap<>();
    private Map<String, String> idMap = new HashMap<>();
    private Map<String, String> nameMap = new HashMap<>();
    private Map<String, String> priceMap = new HashMap<>();
    private static final int RESULT_CODE_SERVICE_CHOOSE = 0xf701;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (xListView != null) {
                    xListView.stopLoadMore();
                    xListView.stopRefresh();
                }
            }
        }
    };

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null) {
            names = intent.getStringExtra("names");
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)) {
                teamMemberMy = (TeamMemberMy) BaseApplication.getInstance().getT(key);
                if (teamMemberMy == null) {
                    finishOk();
                    return;
                }
                BaseApplication.getInstance().removeT(key);
            }
            nameMap.clear();
            if (!TextUtils.isNull(names)) {
                String[] namess = names.split("===");
                for (String name : namess) {
                    if (!TextUtils.isNull(name)) {
                        nameMap.put(name, "");
                    }
                }
            }
            team_id = intent.getStringExtra("team_id");
            team_uid = intent.getStringExtra("team_uid");
            LogUtils.e("funnco------", "获得的数据是：names=" + names + " team_id=" + team_id + " team_uid=" + team_uid);
        }
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_service_distribute);
        ((TextView) findViewById(R.id.llayout_foot)).setText(R.string.ok);

        vInfo = getLayoutInflater().inflate(R.layout.layout_item_teammember_my, null);
        vInfo.findViewById(R.id.tip).setVisibility(View.GONE);
        ((SwipeLayout) vInfo.findViewById(R.id.swip)).setSwipeEnabled(false);
        CircleImageView civ = (CircleImageView) vInfo.findViewById(R.id.id_imageview);
        TextView tvName = (TextView) vInfo.findViewById(R.id.id_textview);
        imageLoader.displayImage(teamMemberMy.getHeadpic(), civ, options);
        tvName.setText(teamMemberMy.getNickname() + "");


        container = (FrameLayout) findViewById(R.id.layout_container);
        container.addView(vInfo);
        xListView = new XListView(mContext);
        xListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);//多选模式
        xListView.setFooterVisibleState(false);
        xListView.setHeaderVisibleState(false);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(false);
        xListView.setDividerHeight(1);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, FunncoUtils.getViewMesureSpec(vInfo)[1], 0, 0);
        xListView.setLayoutParams(params);

        container.addView(xListView);
        adapter = new CommonAdapter<Serve>(mContext, list, R.layout.layout_item_member_4) {
            @Override
            public void convert(ViewHolder helper, final Serve item, int position) {
                TextView tip = helper.getView(R.id.tip);
                String letter = item.getService_type();
                if (position == getPositionLetter(letter)) {
                    tip.setVisibility(View.VISIBLE);
                    if (letter.equals("0")) {
                        tip.setText(R.string.str_service_hisservice);
                    } else if (letter.equals("1")) {
                        tip.setText(R.string.str_service_hiscourses);
                    }
                } else {
                    tip.setVisibility(View.GONE);
                }
                helper.setText(R.id.id_title_0, item.getService_name());
                if (letter.equals("0")) {// 服务
                    helper.getView(R.id.id_title_1).setVisibility(View.VISIBLE);
                    helper.getView(R.id.id_textview).setVisibility(View.GONE);
                    helper.getView(R.id.id_linearlayout).setVisibility(View.VISIBLE);
                    int duration = Integer.valueOf(item.getDuration());
                    helper.setText(R.id.id_title_1, "耗时" + DateUtils.getTime4Minutes2(duration));
                    final String price = priceMap.get(item.getId());
                    helper.getView(R.id.price_layout).setVisibility(View.VISIBLE);
                    helper.setText(R.id.price, price);
                    helper.getView(R.id.price).setTag(item);
                    final EditText priceEdit = helper.getView(R.id.price);
                    priceEdit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            Serve serve = (Serve) priceEdit.getTag();
                            priceMap.put(serve.getId(), editable.toString());
                        }
                    });
                } else {// 课程
                    helper.getView(R.id.price_layout).setVisibility(View.GONE);
                    helper.getView(R.id.id_title_1).setVisibility(View.GONE);
                    helper.getView(R.id.id_textview).setVisibility(View.VISIBLE);
                    int stime = Integer.valueOf(item.getStarttime());
                    int etime = Integer.valueOf(item.getEndtime());
                    helper.setText(R.id.id_textview, DateUtils.getTime4Minutes(stime) + " ~ " + DateUtils.getTime4Minutes(etime));
                }

                final CheckBox checkBox = helper.getView(R.id.id_checkbox);
                checkBox.setTag(item);
                if (nameMap.containsKey(item.getService_name())) {
                    idMap.put(item.getId(), "");
                    checkBox.setChecked(true);
                } else {
                    idMap.remove(item.getId());
                    checkBox.setChecked(false);
                }

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        Serve serve = (Serve) checkBox.getTag();
                        LogUtils.e("------", "选中的项是：" + serve.getService_name() + " 是否选中：" + isChecked);
                        if (isChecked) {
                            idMap.put(serve.getId(), "");
                        } else {
                            idMap.remove(serve.getId());
                        }
                    }
                });

            }
        };
        xListView.setAdapter(adapter);
        getData();
    }

    private int getPositionLetter(String letter) {
        int count = 0;
        for (Serve s : list) {
            if (s.getService_type().equals(letter)) {
                return count;
            }
            count++;
        }
        return -1;
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0, 2000);
            }

            @Override
            public void onLoadMore() {
                clearAsyncTask();
                handler.sendEmptyMessageDelayed(0, 2000);
            }
        });
        /*xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("------", "选中的项是：" + position);
                boolean isChecked = ((CheckableFrameLayout) view).isChecked();
                LogUtils.e("------", "选中的项是：" + position + " 是否选中：" + isChecked);
                if (isChecked) {
                    idMap.put(list.get(position - 1).getId(), "");
                } else {
                    idMap.remove(list.get(position - 1).getId());
                }
            }
        });*/
    }

    //请求的接口。。。。。。
    private void getData() {
        map.clear();
        map.put("team_id", team_id + "");
//        showLoading(parentView);
        FunncoUtils.showProgressDialog(mContext, "下载数据");
        postData2(map, FunncoUrls.getServiceListUrl(), false);
    }

    private void getIds() {
        if (idMap != null) {
            ids = "";
            for (String key : idMap.keySet()) {
                ids += key + ",";
            }
            if (ids.length() > 0) {
                ids = ids.substring(0, ids.length() - 1);
            }
        }
    }

    /**
     * 获得价格
     */
    private String getPriceArray() {
        String priceArray = "";
        if (idMap != null) {
            for (String key : idMap.keySet()) {
                String price = priceMap.get(key);
                if (!TextUtils.isNull(price)) {
                    priceArray += price + ",";
                } else {
                    priceArray += "0,";
                }
            }
            if (priceArray.length() > 0) {
                priceArray = priceArray.substring(0, priceArray.length() - 1);
            }
        }
        return priceArray;
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        FunncoUtils.dismissProgressDialog();
        dismissLoading();
        if (url.equals(FunncoUrls.getServiceListUrl())) {
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null) {
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null) {
                    List<Serve> ls = JsonUtils.getObjectArray(listJSONArray.toString(), Serve.class);
                    if (ls != null && ls.size() > 0) {
                        list.clear();
                        list.addAll(ls);
                        for (Serve serve : ls) {
                            priceMap.put(serve.getId(), serve.getPrice());
                        }
                        sortData();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (url.equals(FunncoUrls.getTeamServiceDistributeUrl())) {
            showToast(R.string.success);
            setResult(RESULT_CODE_SERVICE_CHOOSE);
            finishOk();
        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
        FunncoUtils.dismissProgressDialog();
    }

    private void sortData() {
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<Serve>() {
                @Override
                public int compare(Serve lhs, Serve rhs) {
                    return lhs.getService_type().compareTo(rhs.getService_type());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.llayout_foot://进行保存
                getIds();
                map.clear();
                map.put("team_id", team_id);
                map.put("team_uid", team_uid);
                map.put("service_ids", ids);
                map.put("service_price", getPriceArray());
                postData2(map, FunncoUrls.getTeamServiceDistributeUrl(), false);
                break;
        }
    }
}
