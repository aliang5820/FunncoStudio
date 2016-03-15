package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.PersonalInfoPrefaceActivity;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.QR_CodeActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 我的团队（设置菜单）
 * Created by user on 2015/9/29.
 */
public class TeamMenuActivity extends BaseActivity {

    private View parentView;
//    private ViewPager viewPager;
    private ImageView ivConver;
//    private RadioGroup rgContainer;
    private ListView listView;
    private TextView tvTeamname;
    private TextView tvSetting;
    private CommonAdapter<String> adapter;
    private List<String> list = new ArrayList<>();
    private TeamMy team;
    private Intent intent;

    private static final int REQUEST_CODE_TEAM_SETTING = 0xf500;
    private static final int REQUEST_CODE_TEAM_TEMAMEMBER = 0xf510;
    private static final int REQUEST_CODE_TEAM_SERVICE = 0xf520;
    private static final int REQUEST_CODE_TEAM_WORK = 0xf530;
    private static final int REQUEST_CODE_TEAM_QR = 0xf540;

    private static final int RESULT_CODE_TEAM_EDIT = 0xf501;
    private static final int RESULT_CODE_TEAM_BREAKUP = 0xf502;
    private static final int RESULT_CODE_TEAM_OUT = 0xf503;

    private static Handler handler = new Handler(){
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
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teammenu, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_my);
//        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        ivConver = (ImageView) findViewById(R.id.id_imageview);
//        rgContainer = (RadioGroup) findViewById(R.id.id_radiogroup);
        listView = (ListView) findViewById(R.id.id_listView);
        tvSetting = (TextView) findViewById(R.id.id_textview);
        tvTeamname = (TextView) findViewById(R.id.tv_teammenu_teamname);
        String[] arr = getResources().getStringArray(R.array.array_team_menu_list);
        list = Arrays.asList(arr);

        intent = getIntent();
        if (intent != null){
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)){
                team = (TeamMy) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
                initUI();
            }
        }
        adapter = new CommonAdapter<String>(mContext,list,R.layout.layout_item_textview_c) {
            @Override
            public void convert(ViewHolder helper, String item, int position) {
                helper.setText(R.id.id_textview, item);
                if (position == list.size() - 1){
                    helper.getView(R.id.tip).setVisibility(View.VISIBLE);
                }else{
                    helper.getView(R.id.tip).setVisibility(View.GONE);
                }
            }
        };
        listView.setAdapter(adapter);
    }

    private void initUI() {
        if (team != null){
            String teamName = team.getTeam_name();
            if (!TextUtils.isNull(teamName)){
                tvTeamname.setText(teamName);
            }else{
                tvTeamname.setText("");
            }
            String converPicUrl = team.getCover_pic();
            if (!TextUtils.isNull(converPicUrl)){
                imageLoader.displayImage(converPicUrl, ivConver, options);
            }
        }
    }
    Intent intent2 = new Intent();
    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("------","点击的位置是："+position);
                int code = 0;
                switch (position){
                    case 0://团队成员
//                        intent2.setAction(Actions.ACTION_TEAMMEMBER);
                        code = REQUEST_CODE_TEAM_TEMAMEMBER;
                        intent2.setClass(mContext, TeamMemberActivity.class);
                        break;
                    case 1://团队服务
                        code = REQUEST_CODE_TEAM_SERVICE;
                        intent2.setClass(mContext, TeamServiceActivity.class);
                        break;
                    case 2://团队照片
                        code = REQUEST_CODE_TEAM_WORK;
                        intent2.setClass(mContext, TeamWorkActivity.class);
                        break;
                    case 3://团队二维码
                        code = REQUEST_CODE_TEAM_QR;
                        intent2.setClass(mContext, QR_CodeActivity.class);
                        break;
                    case 4://名片预览
                        code = 0;
                        intent2.setClass(mContext, PersonalInfoPrefaceActivity.class);
                        break;
                    case 5://设置
                        code = REQUEST_CODE_TEAM_SETTING;
                        intent2.setClass(mContext,TeamSettingActivity.class);
                        break;
                }
                intent2.putExtra(KEY,"teamMy");
                BaseApplication.getInstance().setT("teamMy",team);
                startActivityForResult(intent2, code);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TEAM_SETTING && resultCode == RESULT_CODE_TEAM_BREAKUP){
            setResult(RESULT_CODE_TEAM_BREAKUP, new Intent().putExtra("team_id", team.getTeam_id()));
        }else if (requestCode == REQUEST_CODE_TEAM_SETTING && resultCode == RESULT_CODE_TEAM_OUT){
            setResult(RESULT_CODE_TEAM_BREAKUP, new Intent().putExtra("team_id", team.getTeam_id()));
        }
        finishOk();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
