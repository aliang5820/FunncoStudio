package com.funnco.funnco.activity.team;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.view.listview.MyListview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队邀请时的权限设置
 * Created by user on 2015/9/7.
 */
public class TeamInviteActivity extends BaseActivity {

    private View parentView;
    private Button btJoin;
    private ImageView ivPic;
    private TextView tvInvitetitle;
    private MyListview mlvPermissionlist;
    private CommonAdapter<String> adapter;
    private List<String> permissions = new ArrayList<>();

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teaminvite, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        btJoin = (Button) findViewById(R.id.bt_save);
        btJoin.setText(R.string.str_join);
        ivPic = (ImageView) findViewById(R.id.iv_teaminvite_pic);
        tvInvitetitle = (TextView) findViewById(R.id.tv_teaminvite_title);
        mlvPermissionlist = (MyListview) findViewById(R.id.mlv_teaminvite_permission);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_invite_team);
        initAdapter();
    }

    private void initAdapter() {
        adapter = new CommonAdapter<String>(mContext,permissions, R.layout.layout_item_teampermission) {
            @Override
            public void convert(ViewHolder helper, String item, int position) {
                helper.setText(R.id.tv_item_teaminvite_permission,item);
            }
        };
        mlvPermissionlist.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        btJoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.bt_save://加入

                break;
        }
    }

    /**
     * 加入或者忽略网络对接
     */
    private void post(){
        Map<String,Object> map = new HashMap<>();
        AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {

            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        }, false, "");
    }
}
