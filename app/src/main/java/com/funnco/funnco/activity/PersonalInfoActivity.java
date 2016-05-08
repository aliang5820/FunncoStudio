package com.funnco.funnco.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.QR_CodeActivity;
import com.funnco.funnco.activity.myinfo.UpdateInfoActivity;
import com.funnco.funnco.activity.notification.WebViewDetailActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.MyCustomerInfo;
import com.funnco.funnco.bean.TeamMemberMy;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人资料信息（技师本人）
 * Created by user on 2015/8/24.
 */
public class PersonalInfoActivity extends BaseActivity {

    private View parentView;
    private TextView tvEdit;
    private CircleImageView civIcon;
    private TextView tvNickname;
    private TextView tvCareer;
    private TextView tvConventationcount;
    private TextView tvWorkphone;
    private TextView tvAddress;
    private TextView tvDescripation;

    private UserLoginInfo user;
    private final static int REQUEST_CODE_EDIT = 0xf081;
    private static final int RESULT_CODE_PERSONALINFO = 0xf042;
    private final static int RESULT_CODE_EDIT = 0xf082;

    private static final int RESULT_CODE_MEMBERINFO = 0xf222;
    private boolean hasEdit = false;
    private boolean isTeamMember = false;
    private TeamMemberMy member;
    private String team_id;
    private MyCustomerInfo memberInfo;
    private Map<String, Object> map = new HashMap<>();
    private Intent intent;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_personalinfo, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_personalinfo);
        tvEdit = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvEdit.setText(R.string.edit);
        civIcon = (CircleImageView) findViewById(R.id.civ_personalinfo_icon);
        tvNickname = (TextView) findViewById(R.id.tv_personalinfo_name);
        tvCareer = (TextView) findViewById(R.id.tv_personalinfo_career);
        tvConventationcount = (TextView) findViewById(R.id.tv_personalinfo_conventationcount);
        tvWorkphone = (TextView) findViewById(R.id.tv_personalinfo_workphone);
        tvAddress = (TextView) findViewById(R.id.tv_personalinfo_address);
        tvDescripation = (TextView) findViewById(R.id.tv_personalinfo_instroducation);

        intent = getIntent();
        if (intent != null){
            String key = intent.getStringExtra(KEY);
            isTeamMember = intent.getBooleanExtra("isTeamMember", false);
            team_id = intent.getStringExtra("team_id");
            if (!TextUtils.isNull(key)){
                member = (TeamMemberMy) BaseApplication.getInstance().getT(key);
            }
        }
        if (isTeamMember && member != null){
            findViewById(R.id.id_layout_2).setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
            findViewById(R.id.id_layout_3).setVisibility(View.VISIBLE);
            getMemberInfo(member.getUid());
        }else {
            findViewById(R.id.id_layout_2).setVisibility(View.VISIBLE);
            findViewById(R.id.id_layout_3).setVisibility(View.GONE);
            user = BaseApplication.getInstance().getUser();
            if (user != null) {
                initUIData(user);
            }
        }
    }

    private void initUIData(UserLoginInfo user) {
        if (user == null){
            return;
        }
        imageLoader.displayImage(user.getHeadpic() + "", civIcon, options);
        if (!TextUtils.isNull(user.getNickname())) {
            tvNickname.setText(user.getNickname() + "");
        }else{
            tvNickname.setText("");
        }
        if (!TextUtils.isNull(user.getCareer_name())) {
            tvCareer.setText(user.getCareer_name() + "");
        }else{
            tvCareer.setText("");
        }
        if (!TextUtils.isNull(user.getAddress())) {
            tvAddress.setText(user.getAddress() + "");
        }else{
            tvAddress.setText("");
        }
        if (!TextUtils.isNull(user.getWork_phone())) {
            tvWorkphone.setText(user.getWork_phone() + "");
        }else{
            tvWorkphone.setText("");
        }
        if (!TextUtils.isNull(user.getIntro())) {
            tvDescripation.setText(user.getIntro() + "");
        }else{
            tvDescripation.setText("");
        }
        if (!TextUtils.isNull(user.getBookings())) {
            tvConventationcount.setText(user.getBookings() + "次");
        }else{
            tvConventationcount.setText("0" + "次");
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvEdit.setOnClickListener(this);
    }

    private void getMemberInfo(String id){
        map.clear();
        map.put("uid", id + "");
        map.put("team_id", team_id + "");
        postData2(map, FunncoUrls.getUserInfoUrl(), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_CODE_EDIT){
            hasEdit = true;
            if (member != null && isTeamMember){
                initUIData(user);
            }else {
                user = BaseApplication.getInstance().getUser();
                initUIData(user);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.tv_headcommon_headr://编辑
                startActivityForResult(new Intent(mContext,UpdateInfoActivity.class),REQUEST_CODE_EDIT);
                break;
        }
    }
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.tv_personalinfo_qrcode://二维码
                startActivity(QR_CodeActivity.class);
                break;
            case R.id.tv_personalinfo_preface://预览
                startActivity(PersonalInfoPrefaceActivity.class);
                break;
            case R.id.id_title_0://提升管理员
                showLoading(parentView);
                map.clear();
                map.put("team_id",team_id);
                map.put("team_uid",member.getUid());
                map.put("role", "1");
                postData2(map,FunncoUrls.getTeamMemberSetRoleUrl(),false);
                break;
            case R.id.id_title_1://加入成员
                showLoading(parentView);
                map.clear();
                map.put("team_id", team_id);
                map.put("team_uid",member.getUid());
                map.put("role", "0");
                postData2(map, FunncoUrls.getTeamMemberSetRoleUrl(), false);
                break;
            case R.id.id_title_2://移除团队
                showLoading(parentView);
                map.clear();
                map.put("team_id",team_id);
                map.put("team_uid",member.getUid());
                postData2(map, FunncoUrls.getTeamMemberRemoveUrl(), false);
                break;
        }
    }

    protected void finishOk(){
        if (hasEdit){
            setResult(RESULT_CODE_PERSONALINFO);
        }
        super.finishOk();
    }


    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        if (url.equals(FunncoUrls.getTeamMemberRemoveUrl())){
            showToast(R.string.success);
            setResult(RESULT_CODE_MEMBERINFO);
            finishOk();
        }else if (url.equals(FunncoUrls.getTeamMemberSetRoleUrl())){
            showToast(R.string.success);
            setResult(RESULT_CODE_MEMBERINFO);
            finishOk();
        }else if (url.equals(FunncoUrls.getUserInfoUrl())){//获得团队成员的信息
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null){
                user = JsonUtils.getObject(paramsJSONObject.toString(), UserLoginInfo.class);
                if (user != null){
                    initUIData(user);
                }
            }

        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
    }

}
