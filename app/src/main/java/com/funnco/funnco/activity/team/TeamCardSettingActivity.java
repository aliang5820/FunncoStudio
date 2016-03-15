package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMemberSearch;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.file.FileTypeUtils;
import com.funnco.funnco.utils.http.LoginUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.switcher.SwitchView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 团队名片设置(团队创建)
 * Created by user on 2015/8/22.
 */
public class TeamCardSettingActivity extends BaseActivity {

    private View parentView;
    private Button btnNext;
    private SwitchView svSearchable;
    private GridView gvMember;
    private EditText etMobitl,etAddress,etIntroducation;

//    private CommonAdapter<TeamMemberSearch> adatper;
    private List<TeamMemberSearch> list = new ArrayList<>();
    private HttpUtils xUtils;

    private boolean searchAble = true;//是否允许搜索到
    private boolean isSubmiting = false;
    //上一页传递过来的数据
    private String teamName;
    private String filePath;
    private Intent intent;
    //本页应有数据
    private String mobile;
    private String address;
    private String desc;

    private PopupWindow popupWindow;
    private View teamCreatedSuccessPw;
    private Button btCreateSuccess;

    //邀请的成员ids
    private String ids;

    private String msg = "";
    private static final int RESULT_CODE_COMMON = 0xf11;
    private static final int REQUEST_CODE_SEARCH_TEAMMEMBER = 0xf02;
    private static final int RESULT_CODE_SEARCH_TEAMMEMBER = 0xf12;
    private int second = 10;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                if (btCreateSuccess != null){
                    btCreateSuccess.setText("确定 ("+second+")");
                    btCreateSuccess.setTextColor(getResources().getColor(R.color.color_tangerine_2));
                }
                second --;
                if (second < 0){
                    btCreateSuccess.setEnabled(true);
                    btCreateSuccess.setText(R.string.ok);
                    btCreateSuccess.setTextColor(getResources().getColor(R.color.color_black_2));
                }
            }
        }
    };
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teamcardsetting, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null){
            teamName = intent.getStringExtra("teamName");
            filePath = intent.getStringExtra("filePath");
            if (!TextUtils.isNull(filePath)){
                currentfile = new File(filePath);
            }
        }
        xUtils = new HttpUtils(10000);
        btnNext = (Button) findViewById(R.id.bt_save);
        btnNext.setText(R.string.complete);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_teamcard_setting);
        svSearchable = (SwitchView) findViewById(R.id.sv_teamcardsetting_searchable);
        svSearchable.setState(searchAble);
        gvMember = (GridView) findViewById(R.id.gv_teamcardsetting_memberlist);
//        gvMember.setMinimumWidth(FunncoUtils.getScreenWidth(mContext) / 4 - FunncoUtils.dp2px(this, 5));
//        gvMember.setMinimumHeight(FunncoUtils.getScreenWidth(mContext) / 4 - FunncoUtils.dp2px(this, 5));

        teamCreatedSuccessPw = getLayoutInflater().inflate(R.layout.layout_popupwindow_teamcreated, null);
        btCreateSuccess = (Button) teamCreatedSuccessPw.findViewById(R.id.id_button);
        btCreateSuccess.setEnabled(false);
        etMobitl = (EditText) findViewById(R.id.et_teamcardsetting_mobile);
        etAddress = (EditText) findViewById(R.id.et_teamcardsetting_address);
        etIntroducation = (EditText) findViewById(R.id.et_teamcardsetting_introducation);

        initMemberAdapter();
    }
    MyAdapter adapter;
    private void initMemberAdapter() {
        adapter = new MyAdapter();
        gvMember.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        btnNext.setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        btCreateSuccess.setOnClickListener(this);
        svSearchable.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                svSearchable.toggleSwitch(true);
                searchAble = true;
            }

            @Override
            public void toggleToOff() {
                svSearchable.toggleSwitch(false);
                searchAble = false;
            }
        });

        gvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == list.size()) {
                    if (list.size() < 9) {
                        startActivityForResult(new Intent(Actions.ACTION_TEAMMEMBER_SEARCH), REQUEST_CODE_SEARCH_TEAMMEMBER);
                    } else {
                        showSimpleMessageDialog(R.string.str_team_member_overrun);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_save://完成 提交
                mobile = etMobitl.getText()+"";
                address = etAddress.getText()+"";
                desc = etIntroducation.getText()+"";
                if (!checkData()) {
                    showSimpleMessageDialog(msg + "");
                    return;
                }
                if (isSubmiting) {
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                xUtilPostData();
                break;
            case R.id.tv_headcommon_headl://上一步
                finishOk();
                break;
            case R.id.id_button://创建成功的确定按钮
                dismissPopupwindow();
                setResult(RESULT_CODE_COMMON);
                finishOk();
                break;
        }
    }
    private void showPopupwindow(View view){
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setClippingEnabled(true);
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0,0);
        if (view == teamCreatedSuccessPw){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (second>=0){
                        try {
                            handler.sendEmptyMessage(1);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    private boolean dismissPopupwindow() {
        boolean flag = false;
        for (PopupWindow pw : new PopupWindow[]{popupWindow}){
            if (pw != null && pw.isShowing()){
                flag = true;
                pw.dismiss();
            }
        }
        return flag;
    }

    private File currentfile;
    private RequestParams params;
    private void xUtilPostData() {
        FunncoUtils.showProgressDialog(mContext, "信息", "正在为您创建团队，请稍等...");
        isSubmiting = true;
        if (params != null && xUtils != null) {
            xUtils.send(HttpRequest.HttpMethod.POST, FunncoUrls.getTeamCreateUrl(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    isSubmiting = false;
                    FunncoUtils.dismissProgressDialog();
                    String result = responseInfo.result;
                    LogUtils.e("修改资料上传成功！！返回的数据是：", "result----" + result);
                    if (!result.startsWith("{") || !result.endsWith("}")) {
                        result = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
                    }
                    if (JsonUtils.getResponseCode(result) == 0) {
                        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");

                    }
                    showPopupwindow(teamCreatedSuccessPw);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    dismissLoading();
                    isSubmiting = false;
                    showSimpleMessageDialog(s + "");
                    LogUtils.e("修改资料上传失败！！返回的数据是：", "result----" + s);
                }
            });
        } else {
            isSubmiting = false;
            FunncoUtils.dismissProgressDialog();
        }
    }

    private boolean checkData() {
        ids = getIds();
        params = new RequestParams("utf-8");
        CookieStore cookieStore = BaseApplication.getInstance().getCookieStore();
        if (cookieStore == null) {
            LogUtils.e("全局Cookie为空！！！", "");
            msg = getString(R.string.data_err);
            LoginUtils.reLogin(mContext, new Post() {
                @Override
                public void post(int... position) {}
            });
            return false;
        }
        ArrayList<NameValuePair> listParams = new ArrayList<>();

        listParams.add(new BasicNameValuePair("team_name", teamName + ""));
        listParams.add(new BasicNameValuePair("allow_search",searchAble ? "1" : "0"));
        listParams.add(new BasicNameValuePair("phone", mobile + ""));
        listParams.add(new BasicNameValuePair("address", address + ""));
        listParams.add(new BasicNameValuePair("intro", desc + ""));
        listParams.add(new BasicNameValuePair("team_uid", ids + ""));
        listParams.add(new BasicNameValuePair(Constants.ClIENT, Constants.STR_CLIENT));
        listParams.add(new BasicNameValuePair(Constants.LAN, STR_LAN));

        for (NameValuePair nvp : listParams) {
            LogUtils.e("提交的数据是：key:" + nvp.getName(), ",value:" + nvp.getValue());
        }

        StringBuilder sb = new StringBuilder();
        List<Cookie> list = cookieStore.getCookies();
        for (int i = 0; i < list.size(); i++) {
            Cookie c = list.get(i);
            sb.append(c.getName() + "=" + c.getValue() + ";");
        }
        sb.deleteCharAt(sb.length() - 1);
        params.addHeader("Cookie", sb.toString());
        params.addBodyParameter(listParams);
        if (currentfile != null && currentfile.exists() && currentfile.length() > 0) {
            params.addBodyParameter("cover_pic", currentfile, FileTypeUtils.getType(currentfile.getAbsolutePath()));
        }else{
            msg = "团队封面图片选择异常，请重新选择！";
        }
        return true;
    }

    private String getIds(){
        ids = "";
        for (TeamMemberSearch t : list){
            String id = t.getId();
            LogUtils.e("funnco","选中的id 是："+id);
            if (t != null && !TextUtils.isNull(id)){
                ids += id + ",";
            }
        }
        if (!TextUtils.isNull(ids) && ids.length() > 0) {
            ids = ids.substring(0, ids.length() - 1);
        }
        return ids;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SEARCH_TEAMMEMBER && resultCode == RESULT_CODE_SEARCH_TEAMMEMBER){
            if (data != null){
                ids = data.getStringExtra("ids");
                String key = data.getStringExtra(KEY);
                if (!TextUtils.isNull(key)){
                    TeamMemberSearch tms = (TeamMemberSearch) BaseApplication.getInstance().getT(key);
                    BaseApplication.getInstance().removeT(key);
                    list.add(tms);
                }
                adapter.notifyDataSetChanged();
                gvMember.invalidate();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int lines = list.size() / 4 + 1;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    lines * (FunncoUtils.getScreenWidth(mContext) / 4));
            gvMember.setLayoutParams(params);
        }

        @Override
        public int getCount() {
            return list.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        MyHolder holder;
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.layout_item_team_sub, null);
                holder = new MyHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (MyHolder) convertView.getTag();
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    FunncoUtils.getScreenWidth(mContext) / 4 - FunncoUtils.dp2px(mContext, 13),
                    FunncoUtils.getScreenWidth(mContext) / 4 - FunncoUtils.dp2px(mContext, 13));
            holder.civ.setLayoutParams(params);

            holder.iv.setTag(position);
            holder.civ.setTag(position);
            if (position < list.size()){
                holder.iv.setVisibility(View.VISIBLE);
                imageLoader.displayImage(list.get(position).getHeadpic(), holder.civ, options);
            }
            if (position == list.size()){
                holder.civ.setImageResource(R.mipmap.chat_setting_plus);
                holder.civ.setBackgroundDrawable(new BitmapDrawable());
                holder.iv.setVisibility(View.INVISIBLE);
            }
            holder.iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    LogUtils.e("funnco", "点击的位置是：" + index);
                    if (list.size() > 0 && index < list.size()) {
                        list.remove(index);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
        class MyHolder{
            private ImageView iv;
            private CircleImageView civ;
            public MyHolder(View v){
                this.iv = (ImageView) v.findViewById(R.id.iv_item_team_sub_del);
                this.civ = (CircleImageView) v.findViewById(R.id.civ_item_team_sub_icon);
            }
        }
    }
}
