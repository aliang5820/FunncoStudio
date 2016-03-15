package com.funnco.funnco.activity.team;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.edittext.ClearEditText;
import com.funnco.funnco.view.listview.MyListview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索团队进行加入
 * Created by user on 2015/9/7.
 */
public class TeamAddActivity extends BaseActivity {

    private View parentView;
    private ClearEditText cetSearch;
    private TextView tvSearchnotify;
    private MyListview mlvList;
    private CommonAdapter<TeamMy> adapter;
    private List<TeamMy> list = new ArrayList<>();
    private Map<String,Object> map = new HashMap<>();

    private String keywork;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teamadd, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_team_add);
        cetSearch = (ClearEditText) findViewById(R.id.id_edittextview);
        tvSearchnotify = (TextView) findViewById(R.id.id_textview);
        mlvList = (MyListview) findViewById(R.id.id_listView);

    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        cetSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                keywork = s.toString();
                if (TextUtils.isNull(keywork)) {
                    return;
                }
                map.clear();
                map.put("keyword", keywork + "");
                clearAsyncTask();
                postData2(map, FunncoUrls.getTeamSearchUrl(),false);
            }
        });

        initAdapter();
    }

    private void initAdapter() {
        adapter = new CommonAdapter<TeamMy>(mContext, list, R.layout.layout_item_teamadd) {
            @Override
            public void convert(ViewHolder helper, TeamMy item, int position) {
                helper.getView(R.id.id_title_0).setVisibility(View.GONE);
                helper.getView(R.id.id_title_1).setVisibility(View.VISIBLE);
                ImageView ivIcon = helper.getView(R.id.id_title_1);
                String pic = item.getCover_pic();
                if (!TextUtils.isNull(pic)){
                    imageLoader.displayImage(pic, ivIcon, options);
                }
                helper.setText(R.id.id_title_2, item.getTeam_name());
                helper.setText(R.id.id_title_3, item.getIntro());
                helper.setText(R.id.id_title_4, R.string.str_join);
                helper.setCommonListener(R.id.id_title_4, new Post() {

                    @Override
                    public void post(int ...position) {
                        int index = position[0];
                        LogUtils.e(TAG, "选中的位置是i：" + index);
                        if (index >=0 && index <list.size()){
                            TeamMy teamMy = list.get(index);
                            if (teamMy != null){
                                map.clear();
                                map.put("team_id",teamMy.getTeam_id()+"");
                                postData2(map, FunncoUrls.getTeamApplyUrl(), false);
                            }
                        }
                    }
                });
            }
        };
        adapter.isTag(true, new int[]{R.id.id_title_4});
        mlvList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.tv_headcommon_headr:

                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getTeamApplyUrl())) {
            showToast(R.string.str_team_invite_success);
            finishOk();
        }else if (url.equals(FunncoUrls.getTeamSearchUrl())){
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null){
                JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "list");
                if (listJSONArray != null){
                    List<TeamMy> ls = JsonUtils.getObjectArray(listJSONArray.toString(), TeamMy.class);
                    if (ls != null && ls.size() > 0){
                        tvSearchnotify.setVisibility(View.GONE);
                        list.clear();
                        list.addAll(ls);
                    }else{
                        list.clear();
                        tvSearchnotify.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
