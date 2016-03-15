package com.funnco.funnco.activity.team;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.bean.Team;
import com.funnco.funnco.bean.TeamManager;
import com.funnco.funnco.bean.TeamMember;

import java.util.ArrayList;
import java.util.List;

/**
 * 团队详情
 * Created by user on 2015/8/21.
 */
public class TeamInfoActivity extends BaseActivity {


    private TextView tvSetting;

    private View parentView;
    private ViewPager vpPiclist;
    private LinearLayout dotContainer;
    private Team team;
    private List<String> picList = new ArrayList<>();
    private List<TeamManager> managerList = new ArrayList<>();
    private List<TeamMember> memberList = new ArrayList<>();

    private GridView gvManager,gvMember;
    private CommonAdapter<TeamManager> managerAdatper;
    private CommonAdapter<TeamMember> memberAdatper;


    @Override
    protected void loadLayout() {
        super.loadLayout();

        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teaminfo,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        dotContainer = (LinearLayout) findViewById(R.id.llayout_teaminfo_dotcontainer);
        vpPiclist = (ViewPager) findViewById(R.id.vp_teaminfo_gallery);
        gvManager = (GridView) findViewById(R.id.gv_teaminfo_managerlist);
        gvMember = (GridView) findViewById(R.id.gv_teaminfo_memberlist);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_my);
        tvSetting = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvSetting.setText(R.string.setting);
        initDot();
        initViewPagerAdapter();
        initGVAdapter();
    }

    private void initGVAdapter() {
        managerAdatper = new CommonAdapter<TeamManager>(mContext,managerList, R.layout.layout_item_team_sub) {
            @Override
            public void convert(ViewHolder helper, TeamManager item, int position) {

            }
        };
        memberAdatper = new CommonAdapter<TeamMember>(mContext,memberList, R.layout.layout_item_team_sub) {
            @Override
            public void convert(ViewHolder helper, TeamMember item, int position) {

            }
        };
        gvManager.setAdapter(managerAdatper);
        gvMember.setAdapter(memberAdatper);
    }

    private void initViewPagerAdapter() {
    }

    private void initDot() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (String url : picList){
            RadioButton rb = new RadioButton(mContext);
            rb.setLayoutParams(params);
            rb.setBackgroundResource(R.drawable.selector_ok);
            rb.setButtonDrawable(new BitmapDrawable());
            rb.setPadding(5, 5, 5, 5);
//            rgContiner.addView(rb,params);
        }
    }

    @Override
    protected void initEvents() {
        tvSetting.setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        vpPiclist.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (rgContiner.getChildCount() - 1 > position) {
//                    ((RadioButton)rgContiner.getChildAt(position)).setChecked(true);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        rgContiner.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.tv_headcommon_headr://进入团队设置

                startActivity(TeamSettingActivity.class);
                break;

        }
    }
}
