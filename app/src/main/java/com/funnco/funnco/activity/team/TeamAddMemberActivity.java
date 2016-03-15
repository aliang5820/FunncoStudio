package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMemberSearch;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.edittext.ClearEditText;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.XListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成员搜索进行添加
 * Created by user on 2015/8/22.
 */
public class TeamAddMemberActivity extends BaseActivity {

    private TextView tvSearchnotify;//搜索提示 有所搜数据de时候需要隐藏
    private TextView tvSearchnull;//搜索为空时提示 有数据是需要隐藏
    private View parentView;
    private ClearEditText cetSearch;
    private XListView xListView;
    private CommonAdapter adapter;
    private List<TeamMemberSearch> list = new ArrayList<>();
    private Map<String, Object> map = new HashMap<>();


    private int page = 1;
    private int pagesize = 15;
    private String keywork = "";
    private Intent intent;
    //需要返回的数据
    private String ids;
    private Map<String,String> idMap = new HashMap<>();
    private static final int RESULT_CODE_SEARCH_TEAMMEMBER = 0xf12;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                post2();
            }
        }
    };
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teamaddmember, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {

        cetSearch = (ClearEditText) findViewById(R.id.id_edittextview);
        xListView = (XListView) findViewById(R.id.id_listView);
        xListView.setPullRefreshEnable(false);
        xListView.setPullLoadEnable(true);
        xListView.setFooterVisibleState(true);
        xListView.setHeaderVisibleState(false);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_member_add);
        tvSearchnotify = (TextView) findViewById(R.id.id_title_0);
        tvSearchnull = (TextView) findViewById(R.id.id_title_1);
        intent = getIntent();
        if (intent != null){
            ids = intent.getStringExtra("ids");
            if (!TextUtils.isNull(ids)){
                String[]id = ids.split(",");
                idMap.clear();
                for (String i : id){
                    idMap.put(i,i);
                }
            }
        }
        initMemberAdapter();
    }

    private void initMemberAdapter() {
        adapter = new CommonAdapter<TeamMemberSearch>(mContext,list, R.layout.layout_item_teamadd) {
            @Override
            public void convert(ViewHolder helper, TeamMemberSearch item, int position) {
                //具体显示信息
                if (idMap.containsKey(item.getId())){
                    helper.getView(R.id.id_title_4).setEnabled(false);
                }else{
                    helper.getView(R.id.id_title_4).setEnabled(true);
                }
                helper.setText(R.id.id_title_2, item.getNickname());
                helper.setText(R.id.id_title_3, item.getCareer_name());
                CircleImageView civ = helper.getView(R.id.id_title_0);
                imageLoader.displayImage(item.getHeadpic(),civ, options);
                helper.setCommonListener(R.id.id_title_4, new Post() {
                    @Override
                    public void post(int... position) {
                        int index = position[0];
                        LogUtils.e("funnco", "选中的像是：" + index + "  :::" + list.get(index));

                        if (!TextUtils.isNull(ids)){
                            ids +="," + (list.get(index).getId());
                        }else{
                            ids = list.get(index).getId();
                        }
                        intent.putExtra("ids",ids + "");
                        intent.putExtra(KEY,"team_search");
                        BaseApplication.getInstance().setT("team_search",list.get(index));
                        setResult(RESULT_CODE_SEARCH_TEAMMEMBER, intent);
                        finishOk();
                    }
                });
            }
        };
        adapter.isTag(true, new int[]{R.id.id_title_4});
        xListView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        cetSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                page = 1;
                keywork = s + "";
                if (TextUtils.isNull(keywork)) {
                    return;
                }
                dismissLoading();
                clearAsyncTask();
//                post2();
                handler.removeMessages(0);
                handler.sendEmptyMessageDelayed(0,1500);
            }
        });
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                post2();
            }

            @Override
            public void onLoadMore() {
                page++;
                post2();
            }
        });
    }
    private void post2(){
        map.clear();
        map.put("keyword", "" + keywork);
        map.put("page", page + "");
        map.put("pagesize", pagesize + "");
        if (TextUtils.isNull(keywork)){
            showSimpleMessageDialog(R.string.p_fillout_keywork);
            return ;
        }
        showLoading(parentView);
        postData2(map,FunncoUrls.getTeamSearchMemberUrl(),false);
    }
    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
        JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
        List<TeamMemberSearch> ls = JsonUtils.getObjectArray(listJSONArray.toString(), TeamMemberSearch.class);
        if (ls != null && ls.size() > 0) {
            tvSearchnotify.setVisibility(View.GONE);
            tvSearchnull.setVisibility(View.GONE);
            list.clear();
            list.addAll(ls);
        }else{
            list.clear();
            tvSearchnull.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
        }
    }
}
