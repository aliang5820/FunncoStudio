package com.funnco.funnco.activity.login;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.MyLoginAsynchTask;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.AnalyticsConfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//import com.umeng.message.UmengRegistrar;

/**
 * Created by user on 2015/5/13.
 * @author Shawn
 */
public class ForeActivity extends BaseActivity {
    private static final String TAG = ForeActivity.class.getSimpleName();
    private Intent intent = null;
    private String token = null;//授权令牌
    private String uid = null;//用户id
    //是否是第一次登录  默认值为true
    private boolean isFirstLogin = true;
    private MyLoginAsynchTask task = null;
    private static final String VERSION_CODE = "version_code";
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_fore,null);
        setContentView(parentView);
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        AnalyticsConfig.enableEncrypt(true);
//        pushAgent.enable();
        //需要做两件事情：第一判断是否是第一次下载安装；第二判断是否有账号
        // 接下来有个判断，是否是是第一次登录
        //此处为了便于测试先使用true
        isFirstLogin = SharedPreferencesUtils.isFirstLogin(this);
        token = SharedPreferencesUtils.getValue(this, Constants.TOKEN);
        uid = SharedPreferencesUtils.getValue(this, Constants.UID);
        intent = new Intent();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                if(checkLogin()){
                    jumpActivity();
                }else {
                    autoLogin();
                }
            }
        };
        new Handler().postDelayed(task, 3000);
        String appKey = getString(R.string.baidu_appkey);
        PushManager.startWork(mContext, PushConstants.LOGIN_TYPE_API_KEY, appKey);
    }
    @Override
    public void onClick(View v) {
    }
    private void autoLogin() {
        if (!NetUtils.isConnection(mContext)){
            try {
                if (dbUtils != null && dbUtils.tableIsExist(UserLoginInfo.class)) {
//                    UserLoginInfo user2 = dbUtils.findById(UserLoginInfo.class,uid);
                    UserLoginInfo user2 = (UserLoginInfo) new SQliteAsynchTask<UserLoginInfo>().selectT(dbUtils,UserLoginInfo.class,uid);
                    if (user2 != null) {
                        BaseApplication.getInstance().setUser(user2);
                    }
                }
                Intent it = new Intent(mContext,MainActivity.class);
                startActivity(it);
                finishOk();
            } catch (DbException e) {
                e.printStackTrace();
            }
            return;
        }
        sendBroadcast(new Intent("showLoadingDialog"));//发送一个广播用于打开进度对话框
        Map<String ,Object> map = new HashMap<>();
        map.put(Constants.TOKEN,token);
        map.put(Constants.UID,uid);
        map.put(Constants.DEVICE_TOKEN,device_token);
        task = new MyLoginAsynchTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                String msg = JsonUtils.getResponseMsg(result);
                if(JsonUtils.getResponseCode(result) == 0){
                    UserLoginInfo user = JsonUtils.getObject(JsonUtils.getJObt(result, "params").toString(), UserLoginInfo.class);
                    if (user != null && dbUtils != null){
                        try {
                            dbUtils.saveOrUpdate(user);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    BaseApplication.getInstance().setUser(user);
                }else{
                    showSimpleMessageDialog(msg+"");
                    intent.setClass(ForeActivity.this, LoginActivity.class);
                }
                dismissLoadingDialog();//关闭登录对话框
                jumpActivity();
            }
            @Override
            public void getBitmap(String rul,Bitmap bitmap) {
                dismissLoading();
            }
        },false);
        putAsyncTask(task);
        task.execute(FunncoUrls.getAutoLoginUrl());
    }
    @Override
    protected void initView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新数据库
        if (BaseApplication.getInstance().isUpdateDB()){
             //"FunncoEvent" ----- 第一版本之后的
            //MyCustomer ----- 版本1.5
            //MyCustomerConventation ----- 测试用
            //Serve ----- 2.1版本新增字段
            for (String beanName : new String[]{"FunncoEvent","MyCustomer",
                    "MyCustomerD","MyCustomerConventation","Serve"}){
                updateDb(dbUtils,beanName);
            }
        }
    }

    @Override
    protected void initEvents() {}
    private boolean checkLogin(){
        device_token = SharedPreferencesUtils.getValue(mContext, Constants.DEVICE_TOKEN);
        String version_code = SharedPreferencesUtils.getValue(mContext, VERSION_CODE)+"";
        if(!TextUtils.isEmpty(device_token)){
            LogUtils.e(TAG, "该设备的device_token：" + device_token);
        }

        if(!TextUtils.equals(version_code,""+FunncoUtils.getVersionCode(mContext))) {//是第一次登录则进入欢迎页面
            SharedPreferencesUtils.setValue(mContext,VERSION_CODE, ""+FunncoUtils.getVersionCode(mContext));
            intent.setClass(this, WelcomeActivity.class);
            BaseApplication.setIsFirstUser(true);
            //isFirstLogin值修改为false
            SharedPreferencesUtils.setValue(this, Constants.ISFIRSTLOGIN, false);
            return true;
        }else if(TextUtils.isEmpty(device_token) || TextUtils.isEmpty(token) || TextUtils.isEmpty(uid)){
        //判断是否有用户信息,没有保存用户信息则进入登录页面
            intent.setClass(this,LoginActivity.class);
            BaseApplication.setIsFirstUser(true);
            return true;
        }else{//有用户信息，进行自动登录
            //修改不是第一次使用App
            BaseApplication.setIsFirstUser(false);
            intent.setClass(this, MainActivity.class);
            return false;
        }
    }

    private void jumpActivity(){
        dismissSimpleMessageDialog();
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        finishOk();
    }

    private void updateDb(DbUtils db, String tableName) {
        try {
            //// 把要使用的类加载到内存中,并且把有关这个类的所有信息都存放到对象c中
            Class c = Class.forName("com.funnco.funnco.bean." + tableName);
            if (db.tableIsExist(c)) {
                List<String> dbFildsList = new ArrayList<>();
                String str = "select * from " + tableName;
                Cursor cursor = db.execQuery(str);//先从c表中拿到所有数据
                int count = cursor.getColumnCount();//计算字段数
                for (int i = 0; i < count; i++) {
                    dbFildsList.add(cursor.getColumnName(i));//把字段分别放入集合中
                }
                cursor.close();
                // 把属性的信息提取出来，并且存放到field类的对象中，因为每个field的对象只能存放一个属性的信息所以要用数组去接收
                Field f[] = c.getDeclaredFields();//获取该类的字段类型
                for (int i = 0; i < f.length; i++) {
                    String fildName = f[i].getName();
                    if (fildName.equals("serialVersionUID")) {
                        continue;
                    }
                    String fildType = f[i].getType().toString();
                    if (!isExist(dbFildsList, fildName)) {
                        if (fildType.equals("class java.lang.String")) {
                            db.execNonQuery("alter table " + tableName + " add " + fildName + " TEXT ");
                        } else if (fildType.equals("int") || fildType.equals("long") || fildType.equals("boolean") || fildType.equals("double")) {
                            db.execNonQuery("alter table " + tableName + " add " + fildName + " INTEGER ");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private  boolean isExist(List<String> list,String fildName){
        boolean flag = false;
        for (String s : list){
            if (s.equals(fildName)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
