package com.funnco.funnco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.wukong.auth.AuthService;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.AboutActivity;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.login.LoginActivity;
import com.funnco.funnco.activity.myinfo.UpdatePasswordActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.support.ActivityCollectorUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/6/14.
 */
public class SettingActivity extends BaseActivity {

    private String[] menulist;
    private int[] imgs = {
            R.mipmap.common_my_password_icon_2x,
        R.mipmap.common_my_feedback_icon_2x,
        R.mipmap.common_my_about_us_icon_2x,
        R.mipmap.common_my_history_icon_2x};

    private ListView lv;
    private MyBaseAdapter adapter;
    private View parentView;

    //退出账号的PopupWindow
    private PopupWindow pwLogout;
    private Map<String,Object> map = new HashMap<>();
    //用于跳转的控制
    private boolean isIntent = true;
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        versionName = FunncoUtils.getVersionName(mContext);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_setting,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.setting);
        findViewById(R.id.iv_setting_logout).setOnClickListener(this);
        menulist = getResources().getStringArray(R.array.array_setting_menulist);
        lv = (ListView) findViewById(R.id.lv_setting_list);
        adapter = new MyBaseAdapter();
        lv.setAdapter(adapter);

        View pwView = LayoutInflater.from(mContext).inflate(R.layout.layout_popupwindow_logout, null);
        pwView.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
        pwView.findViewById(R.id.bt_popupwindow_delete).setOnClickListener(this);
        pwLogout = new PopupWindow(pwView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pwLogout.setClippingEnabled(true);
        pwLogout.setOutsideTouchable(true);
    }

    @Override
    protected void initEvents() {
        final Intent intent = new Intent();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((int) id) {
                    case 0:
                        isIntent = true;
                        intent.setClass(SettingActivity.this, UpdatePasswordActivity.class);
                        break;
                    case 1:
                        isIntent = true;
                        intent.setClass(SettingActivity.this, FeedBackActivity.class);
                        break;
                    case 2:
                        isIntent = true;
                        intent.setClass(SettingActivity.this, AboutActivity.class);
                        break;
                    case 3:
                        isIntent = false;
                        Toast.makeText(SettingActivity.this, "当前版本是：" + FunncoUtils.getVersionName(mContext), Toast.LENGTH_LONG).show();
                        break;
                }
                if (isIntent) {
                    intent.putExtra("info", menulist[position]);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.bt_popupwindow_cancle:
                dismissPw();
                break;
            case R.id.bt_popupwindow_delete:
                //账号退出需要完成的事情
                //不是第一次登陆
                //进入到登录界面
                dismissPw();
                logOut();

                break;
            case R.id.iv_setting_logout:
                if (null != pwLogout && !pwLogout.isShowing()){
                    pwLogout.showAtLocation(parentView, Gravity.BOTTOM,0,0);
                }
                break;
        }
    }
    private void dismissPw(){
        if (pwLogout != null && pwLogout.isShowing()) {
            pwLogout.dismiss();
        }
    }
    private void logOut() {
        UserLoginInfo user = BaseApplication.getInstance().getUser();
        if (user == null){
            showSimpleMessageDialog(R.string.user_err);
            return;
        }
        FunncoUtils.showProgressDialog(mContext, R.string.information, R.string.str_logout_account);
        /**
         * 注销接口，用于注销当前的登录状态
         */
        AuthService.getInstance().logout();
        map.clear();
        map.put("uid", user.getId());
        map.put("token", user.getToken());
        postData2(map, FunncoUrls.getLoginOutUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        FunncoUtils.dismissProgressDialog();
        if (url.equals(FunncoUrls.getLoginOutUrl())){
            Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
            if (NetUtils.isConnection(mContext)) {
                SharedPreferencesUtils.removeValue(SettingActivity.this, Constants.SHAREDPREFERENCE_CONFIG, Constants.UID);
            }
            ActivityCollectorUtils.removeActivity(SettingActivity.this);
            ActivityCollectorUtils.finishAllActivities();
            startActivity(intent);
            finishOk();
        }
    }

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (menulist != null)
                return menulist.length;
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.layout_item_my,parent,false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }
            holder.ivIcon.setImageResource(imgs[position]);
            if (position != menulist.length - 1) {
                holder.tvTitle.setText(menulist[position]);
            }else{
                holder.tvTitle.setText(menulist[position]+versionName);
            }
            return convertView;
        }

        class Holder{
            TextView tvTitle = null;
            ImageView ivIcon = null;
            public Holder(View convertView){
                tvTitle = (TextView) convertView.findViewById(R.id.tv_item_my_title);
                ivIcon = (ImageView) convertView.findViewById(R.id.iv_item_my_icon);
            }
        }
    }
}
