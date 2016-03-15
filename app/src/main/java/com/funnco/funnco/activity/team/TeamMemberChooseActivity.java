package com.funnco.funnco.activity.team;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Team;
import com.funnco.funnco.bean.TeamMember;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成员选择 (单团队  多选)
 * Created by user on 2015/9/21.
 */
public class TeamMemberChooseActivity extends BaseActivity {

    private View parentView;
    private FrameLayout fl;
    private TextView tvMySelf;
//    private View headView;
    private ExpandableListView expandableListView;
    private List<Team> list = new ArrayList<>();
    private List<TeamMember> memberList = new ArrayList<>();
    private MyAdapter adapter;
    private UserLoginInfo user;
    //选中的Id 的集合<position,id>
    private Map<String,Map<String,String>> idMap = new HashMap<>();
    private Button btOk;
    private String ids = "";
    private String team_id = "";
    private String team_name = "";
//    private CheckBox cbSelf;
    private boolean isSingleChoice = false;
    private boolean isChat = false;
    private static final int RESULT_CODE_MEMBERCHOOSE = 0xf16;
    private Intent intent;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
            }
        }
    };
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teammemberchoose, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null){
            ids = intent.getStringExtra("ids");
            team_id = intent.getStringExtra("team_id");
            isSingleChoice = intent.getBooleanExtra("chooseMode", false);
            isChat = intent.getBooleanExtra("isChat", false);
            if (!TextUtils.isNull(ids) && !TextUtils.isNull(team_id)){
                String[] idss = ids.split(",");
                Map<String,String> map = new HashMap<>();
                for (String id : idss){
                    if (!TextUtils.isNull(id)){
                        map.put(id,id);
                    }
                }
                idMap.put(team_id,map);
                if (isSingleChoice){
                    idMap.clear();
                    ids = "";
                }
            }
        }
        user = BaseApplication.getInstance().getUser();
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_member_choose);
        btOk = (Button) findViewById(R.id.llayout_foot);
        btOk.setText(R.string.ok);
        if (isSingleChoice){
            btOk.setVisibility(View.GONE);
        }
        tvMySelf = new TextView(mContext);
        tvMySelf.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FunncoUtils.dp2px(this, 50)));
        tvMySelf.setPadding(60, 0, 0, 0);
        tvMySelf.setBackgroundColor(Color.WHITE);
        tvMySelf.setText(R.string.str_myself);
        tvMySelf.setTextSize(20);
//        tvMySelf.setId(R.id.id_textview);
        tvMySelf.setGravity(Gravity.CENTER_VERTICAL);

        fl = (FrameLayout) findViewById(R.id.layout_container);
        fl.addView(tvMySelf,0);
        expandableListView = new ExpandableListView(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, FunncoUtils.dp2px(this, 65), 0, 0);
        expandableListView.setLayoutParams(lp);
        expandableListView.setDividerHeight(0);
        expandableListView.setGroupIndicator(null);
        fl.addView(expandableListView,1);
        adapter = new MyAdapter(mContext,list);
        expandableListView.setAdapter(adapter);
        getData();
    }
    public int getPositionForSection(String section) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getTeam_id();
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }
    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvMySelf.setOnClickListener(new View.OnClickListener() {//选中了自己
            @Override
            public void onClick(View v) {
                ids = "";
                team_id = "";
                team_name = "";
                LogUtils.e("------", "点击了确定， 选中了自己 选中的ids 是：" + ids);
                Intent intent2 = new Intent();
                if (!TextUtils.isNull(team_id) && !TextUtils.isNull(team_name)) {
                    intent2.putExtra("team_id", team_id);
                    intent2.putExtra("team_name", team_name);
                    intent2.putExtra("ids", "" + getIds());
                }
                setResult(RESULT_CODE_MEMBERCHOOSE,intent2);
                finishOk();
            }
        });
        btOk.setOnClickListener(this);
    }
    /**
     * 初始化数据
     */
    private void getData(){
        if (user != null && NetUtils.isConnection(mContext)){
            postData2(null, FunncoUrls.getTeamMemberUrl(), false);
        }else{
            getTeamMember4Db();
        }
    }
    /**
     * 数据库获取数据
     */
    private void getTeamMember4Db() {
        try {
            if (dbUtils.tableIsExist(Team.class)){
                List<Team> ls = dbUtils.findAll(Team.class);
                if (ls != null && ls.size() > 0){
                    list.clear();
                    list.addAll(ls);
                    adapter.notifyDataSetChanged();
                    findViewById(R.id.id_notify).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.id_notify).setVisibility(View.VISIBLE);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getTeamMemberUrl())){
            JSONArray paramsJSONArray = JsonUtils.getJAry(result, "params");
            List<Team> ls =  JsonUtils.getObjectArray(paramsJSONArray.toString(), Team.class);
            if (ls != null && ls.size() > 0){
                try {
                    dbUtils.deleteAll(Team.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < ls.size(); i ++){
                    Team team = ls.get(i);
                    team.setU_id(user.getId());//给每个Team 设置一个所属Id
                    List<TeamMember> lsTM = team.getList();
                    if (lsTM != null && lsTM.size() > 0){
                        for (int j = 0; j < lsTM.size(); j ++) {
                            lsTM.get(j).setTeam_id(team.getTeam_id());
                        }
                        AsyncTaskUtils.saveListBean(dbUtils,lsTM,TeamMember.class,false);
                    }
                }
                list.addAll(ls);
                AsyncTaskUtils.saveListBean(dbUtils, ls, Team.class, true);
                findViewById(R.id.id_notify).setVisibility(View.GONE);
            }else{
                findViewById(R.id.id_notify).setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.llayout_foot://确定
                LogUtils.e("------", "点击了确定， 选中的ids 是：" + ids);
                Intent intent = new Intent();
                if (!TextUtils.isNull(team_id) && !TextUtils.isNull(team_name)) {
                    intent.putExtra("team_id", team_id);
                    intent.putExtra("team_name", team_name);
                    intent.putExtra("ids", "" + getIds());
                }
                if (isChat){
                    if (!isSingleChoice){
                        intent.putExtra("title",team_name+"");
                    }
                    intent.putExtra(KEY, "memberList");
                    BaseApplication.getInstance().setT("memberList", memberList);
                }
                for (TeamMember tm : memberList){
                    LogUtils.e("funnco","选中的 团队成员数是："+tm);
                }
                setResult(RESULT_CODE_MEMBERCHOOSE,intent);
                finishOk();
                break;
        }
    }
    private String getIds(){
        ids = "";
        if (idMap == null || idMap.size() == 0){
            return ids;
        }
        for (String key : idMap.keySet()){
            team_id = key;
            Map<String,String> map = idMap.get(key);
            for (String k : map.keySet()) {
                ids += k+",";
                LogUtils.e("------","key==="+k);
            }
        }
        ids = ids.substring(0,ids.length()-1);
        LogUtils.e("------","ids==="+ids);
        return ids;
    }

    private class MyAdapter extends BaseExpandableListAdapter {
        private Context context;
        private List<Team> list;
        public MyAdapter(Context context,List<Team> list){
            this.context = context;
            this.list = list;
        }
        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return list.get(groupPosition).getList().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return list.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list.get(groupPosition).getList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
        MyHolder holder1;
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.layout_item_memberchoose, parent, false);
                holder1 = new MyHolder(convertView);
                convertView.setTag(holder1);
            }else{
                holder1 = (MyHolder) convertView.getTag();
            }
            if(isSingleChoice){
                holder1.cbState.setVisibility(View.GONE);
            }else {
                holder1.cbState.setVisibility(View.VISIBLE);
            }
            holder1.cbState.setTag(new int[]{groupPosition});
            Map<String,String> l = idMap.get(list.get(groupPosition).getTeam_id());
            if (l != null && l.size() == list.get(groupPosition).getList().size()){
                holder1.cbState.setChecked(true);
            }else{
                holder1.cbState.setChecked(false);
            }
            holder1.cbState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox cb = (CheckBox) buttonView;
                    int[] tag = (int[]) cb.getTag();
                    LogUtils.e("funnco------","CheckBox 的 tag 是：" + tag + "  ("+tag[0]+")");
                    LogUtils.e("funnco------","是否选中：" + isChecked);
                    Map<String, String> map;
                    map = idMap.get(list.get(tag[0]).getTeam_id());
                    if (isChecked) {
                        idMap.clear();
                        if (map == null) {
                            map = new HashMap<>();
                        }
                        List<TeamMember> ls = list.get(tag[0]).getList();
                        memberList.clear();
                        memberList.addAll(ls);
                        LogUtils.e("funnco------", "选中的父容器，对应的子项集合" + ls + "的大小是：" + (ls == null ? "0" : ls.size()));
                        if (ls != null && ls.size() > 0) {
                            LogUtils.e("funnco------", "集合不为空");
                            for (TeamMember t : ls) {
                                LogUtils.e("funnco------","团队成员的具体数据是："+t);
                                map.put(t.getMember_uid(), t.getMember_uid());
                            }
                        }
                        idMap.put(list.get(tag[0]).getTeam_id() + "", map);
                    } else {
                        memberList.clear();
                        idMap.remove(list.get(tag[0]).getTeam_id());
                    }
                    notifyDataSetChanged();
                }
            });
            holder1.tvname.setText(list.get(groupPosition).getTeam_name());
            holder1.civIcon.setImageResource(R.mipmap.common_schedule_conventiontype_icon);
            return convertView;
        }
        MyHolder holder2;
        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_item_memberchoose, parent, false);
                holder2 = new MyHolder(convertView);
                convertView.setTag(holder2);
            }else{
                holder2 = (MyHolder) convertView.getTag();
            }
            holder2.cbState.setVisibility(View.VISIBLE);
            holder2.cbState.setTag(new int[]{groupPosition,childPosition});
            String teamId = list.get(groupPosition).getTeam_id();
            Map<String,String> teamMap = idMap.get(teamId);
            String teamUid = list.get(groupPosition).getList().get(childPosition).getMember_uid();
            if (idMap.containsKey(teamId) && teamMap != null && teamMap.containsKey(teamUid)){
                holder2.cbState.setChecked(true);
            }else{
                holder2.cbState.setChecked(false);
            }
            holder2.cbState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    CheckBox cb = (CheckBox) buttonView;
                    int[] tag = (int[]) cb.getTag();
                    TeamMember teamMember = list.get(tag[0]).getList().get(tag[1]);
                    if(isChecked){
                        Map<String,String> map ;
                        team_id = list.get(tag[0]).getTeam_id();
                        team_name = list.get(tag[0]).getTeam_name();
                        map = idMap.get(list.get(tag[0]).getTeam_id());
                        if (map == null){
                            map = new HashMap<>();
                        }
                        map.put(teamMember.getMember_uid(), "");
                        memberList.add(teamMember);
                        idMap.clear();
                        idMap.put(list.get(tag[0]).getTeam_id(), map);
                        if (isSingleChoice){
                            Intent intent = new Intent();
                            if (!TextUtils.isNull(team_id) && !TextUtils.isNull(team_name)) {
                                intent.putExtra("team_id", team_id);
                                intent.putExtra("member_name",teamMember.getNickname());
                                intent.putExtra("headpic", teamMember.getHeadpic());
                                intent.putExtra("team_name", team_name);
                                intent.putExtra("ids", "" + getIds());
                            }
                            if (isChat){
                                intent.putExtra("title",teamMember.getNickname());
                                intent.putExtra(KEY,"memberList");
                                BaseApplication.getInstance().setT("memberList", memberList);
                            }
                            setResult(RESULT_CODE_MEMBERCHOOSE,intent);
                            finishOk();
                        }
                    }else{
                        memberList.remove(teamMember);
                        Map<String,String> map2 = idMap.get(list.get(tag[0]).getTeam_id());
                        if(map2 != null){
                            map2.remove(teamMember.getMember_uid());
                        }
                        idMap.put(list.get(tag[0]).getTeam_id(),map2);
                    }
                    notifyDataSetChanged();
                }
            });
            holder2.tvname.setText(list.get(groupPosition).getList().get(childPosition).getNickname());
            imageLoader.displayImage(list.get(groupPosition).getList().get(childPosition).getHeadpic() , holder2.civIcon, options);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private class MyHolder{
            private CircleImageView civIcon;
            private TextView tvname;
            private CheckBox cbState;
            public MyHolder(View view){
                this.civIcon = (CircleImageView) view.findViewById(R.id.civ_item_memberchoose_icon);
                this.tvname = (TextView) view.findViewById(R.id.tv_item_memberchoose_name);
                this.cbState = (CheckBox) view.findViewById(R.id.cb_item_memberchoose);
            }
        }
    }
}
