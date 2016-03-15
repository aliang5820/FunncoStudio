package com.funnco.funnco.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.FunncoEvent;
import com.funnco.funnco.bean.FunncoEventCustomer;
import com.funnco.funnco.bean.MyCustomer;
import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.bean.ScheduleNew;
import com.funnco.funnco.bean.ScheduleNewStat;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.bean.WorkItem;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.ClearDateAsyncTask;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.thrid.WeicatUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 登录
 * @author Shawn
 */
@ContentView(R.layout.layout_activity_login)
public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    protected Intent intent = new Intent();
    @ViewInject(R.id.iv_login_loginbtn)
    private ImageView ivLoginBtn ;
    @ViewInject(R.id.et_login_phonenumber)
    private EditText etPhoneNumber ;
    @ViewInject(R.id.et_login_password)
    private EditText etPassword ;
    @ViewInject(R.id.tv_login_lostpassword)
    private TextView tvLostPassword ;
    @ViewInject(R.id.tv_login_newaccount)
    private TextView tvNewAccount ;
    @ViewInject(R.id.tv_login_wechat)
    private TextView tvWechat ;

    //账号/密码
    private String phoneNumber = null;
    private String password = null;

    private String mobileOld = "";
    //本地已保存的Uid
    private String uid;

    //是否有跳转的标记 默认是不跳转
    protected static boolean isJump = false;
    private boolean isLogining = false;
    //存放登陆信息
    private RequestParams params = null;
    //用于接收修改密码传递回来的数据
    private Intent intent2;
    private String newPw;
    //微信登录
    private boolean isWeicatLogin = false;
    UMSocialService mController;
    private String token;//即微信授权成功后得到的 access_token
    private String openid;//微信授权成功返回信息
//    private String device_token;//消息推送获得的信息

    private String nickname;
    private String headimgurl;
    private View parentView;

    private Map<String,Object> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_login,null);
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        etPhoneNumber.setText(SharedPreferencesUtils.getValue(this, Constants.PHONE_NUMBER));

        intent2 = getIntent();
        if (intent2 != null){
            newPw = intent2.getStringExtra("pw");
            LogUtils.e("接收到的新密码是：",""+newPw);
            if (newPw != null){
                etPassword.setText(newPw);
            }else{
                etPassword.setText(SharedPreferencesUtils.getValue(this, Constants.USER_PWD));
            }
        }
        //添加微信平台
        WeicatUtils.addWXPlatform(mContext, getString(R.string.appId), getString(R.string.appSecret));
        params = new RequestParams();
        uid = SharedPreferencesUtils.getValue(mContext,Constants.UID);
        mobileOld = SharedPreferencesUtils.getValue(mContext,Constants.PHONE_NUMBER);

        String appKey = getString(R.string.baidu_appkey);
        PushManager.startWork(mContext, PushConstants.LOGIN_TYPE_API_KEY, appKey);
    }

    @OnClick({R.id.iv_login_loginbtn,
            R.id.tv_login_lostpassword,
            R.id.tv_login_newaccount,
            R.id.tv_login_wechat,
            R.id.tv_register_career
    })
    protected void btnClick(View view){
        switch (view.getId()){
            case R.id.iv_login_loginbtn://进行登录
                isWeicatLogin = false;
                if (isLogining){
                    showSimpleMessageDialog(R.string.logining);
                    return;
                }
                //获取填写的信息 进行登录
                phoneNumber = etPhoneNumber.getText()+"";
                password = etPassword.getText()+"";
                if (!NetUtils.isConnection(mContext)){
                    showSimpleMessageDialog(R.string.net_err);
                    return;
                }
                /**
                 * 如果之前有账号 获得完设备ID后需要重新保存
                 */
                //获取设备的Device Token
                //此处判断是否为空/同时做手机号验证
                if(TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)){
                    showSimpleMessageDialog(R.string.fillout_right);
                    etPassword.setText("");
                    return;
                }
//                Map<String ,Object> map = new HashMap<>();
                map.clear();
                map.put(Constants.PHONE_NUMBER, phoneNumber);
                map.put(Constants.USER_PWD, password);
                map.put(Constants.DEVICE_TOKEN, device_token);
                //显示对话框
//                showLoadingDialog();
                isLogining = true;
                showLoading(parentView);
               postData2(map,FunncoUrls.getLoginUrl(), false);
                break;
            case R.id.tv_login_lostpassword://获取新密码
                intent.setClass(mContext, GetPasswordActivity.class);
                isJump = true;
                break;
            case R.id.tv_login_newaccount://注册新账户
                intent.setClass(mContext, RegisterActivity.class);
                isJump = true;
                break;
            case R.id.tv_login_wechat://微信登录
                if (isLogining){
                    showSimpleMessageDialog(R.string.logining);
                    return;
                }
                isWeicatLogin = true;
                weiCatLoginListener();
                break;
            default:
                break;
        }
        if (isJump) {
            startActivityForResult(intent,0);
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            isJump = false;
        }
    }

    /**
     * 微信登陆授权监听
     * 授权接口
     */
    private void weiCatLoginListener() {
        showLoading(parentView);
        mController.doOauthVerify(mContext, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {}
            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                dismissLoading();
                if (bundle != null && !TextUtils.isEmpty(bundle.getString("uid"))) {
                    showToast("授权成功");
                    //获取相关授权信息
                    token = ""+bundle.get("access_token");
                    openid = ""+bundle.get("openid");
                    for (String key : bundle.keySet()){
                        LogUtils.e("微信授权信息key:"+key,", value:"+bundle.get(key));
                    }
                    //接下来进行登录
                    map.clear();
                    map.put(Constants.DEVICE_TOKEN,device_token);
                    map.put(Constants.TOKEN,token);
                    map.put("openid",openid);
                    //微信账号相关信息
                    getWeiCatLogingetPlatformInfo();
                } else {
                    showToast("授权失败");
                }
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {
                dismissLoading();
            }
            @Override
            public void onCancel(SHARE_MEDIA share_media) {}
        });
    }

    /**
     * 获取微信平台的信息
     */
    private void getWeiCatLogingetPlatformInfo(){
        if (mController == null){
            return;
        }
        showLoading(parentView);
        mController.getPlatformInfo(mContext, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {}

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                dismissLoading();
                if (status == 200 && info != null) {
                    StringBuilder sb = new StringBuilder();
                    Set<String> keys = info.keySet();
                    for (String key : keys) {
                        sb.append(key + "=" + info.get(key).toString() + "\r\n");
                    }
                    nickname = (String) info.get("nickname");
                    headimgurl = (String) info.get("headimgurl");
                    //进行服务器微信对接
                    isLogining = true;
                    showLoading(parentView);
                    postData2(map,FunncoUrls.getWeiCatLoginUrl(),false);
                    LogUtils.e("TestData", sb.toString());
                } else {
                    showSimpleMessageDialog(R.string.failue);
                    LogUtils.e("TestData", "发生错误：" + status);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 0 && resultCode == RESULT_OK){
                if(data != null){
                    phoneNumber = data.getStringExtra(Constants.PHONE_NUMBER);
                    etPhoneNumber.setText(phoneNumber);
                }
            }
    }
    @Override
    protected void initView() {

    }
    @Override
    protected void initEvents() {}
    @Override
    public void onClick(View v) {}

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        dismissLoading();
        isLogining = false;
        try {
            //登录成功需要处理：
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result);
            Object obj = jsonObject.get("params");
            if (obj == null || TextUtils.isEmpty(obj+"") || TextUtils.equals("null",obj+"") || obj instanceof com.alibaba.fastjson.JSONArray){//数据为空，跳转进行绑定
                Intent intent = new Intent(mContext,BindNotifyActivity.class);
                intent.putExtra("nickname",nickname);
                intent.putExtra("headimgurl",headimgurl);
                intent.putExtra(Constants.TOKEN,map.get("token")+"");
                intent.putExtra("openid",map.get("openid")+"");
                startActivity(intent);
            }else if (obj instanceof com.alibaba.fastjson.JSONObject){//数据不为空，说明之前已经绑定过
                final UserLoginInfo user = JsonUtils.getObject(obj+"", UserLoginInfo.class);
                if (!TextUtils.equals(uid,user.getId()) || !TextUtils.equals(mobileOld,phoneNumber)){
                    showLoading(parentView);
                    //启动一个异步任务删除个张表
                    ClearDateAsyncTask task1 = new ClearDateAsyncTask(dbUtils,utils,imageLoader, new Post() {
                        @Override
                        public void post(int ...position) {
                            dismissLoading();
                            //用户信息进行存储
                            try {
                                dbUtils.saveOrUpdate(user);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            BaseApplication.getInstance().setUser(user);//设置全局user
                            SharedPreferencesUtils.setValue(mContext, Constants.USER_PWD, password);
                            SharedPreferencesUtils.setUserValue(mContext, user);
                            startActivity(MainActivity.class);
                            finishOk();
                        }
                    });
                    task1.execute(new Class[]{UserLoginInfo.class, FunncoEvent.class,MyCustomer.class,MyCustomerD.class,FunncoEventCustomer.class,ScheduleNew.class,ScheduleNewStat.class,Serve.class,WorkItem.class});
                }else {
                    //用户信息进行存储
                    dbUtils.saveOrUpdate(user);
                    BaseApplication.getInstance().setUser(user);//设置全局user
                    SharedPreferencesUtils.setValue(LoginActivity.this, Constants.USER_PWD, password);
                    SharedPreferencesUtils.setUserValue(mContext, user);
                    startActivity(MainActivity.class);
                    finishOk();
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
        dismissLoading();
        isLogining = false;
//        showSimpleMessageDialog("" + msg);
        etPassword.setText("");
    }
}

