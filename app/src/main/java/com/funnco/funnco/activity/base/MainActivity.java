package com.funnco.funnco.activity.base;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.wukong.Callback;
import com.alibaba.wukong.auth.AuthInfo;
import com.alibaba.wukong.auth.AuthService;
import com.funnco.funnco.R;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.ScheduleNew;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.com.funnco.funnco.callback.OnFragmentInteractionListener;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.fragment.desk.MessageFragment;
import com.funnco.funnco.fragment.desk.MyFragment;
import com.funnco.funnco.fragment.desk.ScheduleFragment;
import com.funnco.funnco.fragment.desk.ServiceFragment;
import com.funnco.funnco.impl.PostObt;
import com.funnco.funnco.support.FragmentTabHost;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.file.FileUtils;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.MyHttpUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.media.MediaPlayerUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.ActivityCollectorUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.wukong.bean.ALoginParam;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shawn
 */
public class MainActivity extends BaseActivity implements OnFragmentInteractionListener, PostObt {

    private FragmentTabHost fragmentTabHost;
    private final Class<?>[] classes = {ScheduleFragment.class, MessageFragment.class, ServiceFragment.class, MyFragment.class};
    private final String[] tag = {"ScheduleFragment", "MessageFragment", "ServiceFragment", "MyFragment"};
    private final int[] menu_layout = {
            R.layout.layout_table_schedule,
            R.layout.layout_table_message,
            R.layout.layout_table_service,
            R.layout.layout_table_my};
    //用于存放新下载的新预约的日期 每次启动都要新下载  dates count
    private Map<String, String> schedule_new = new HashMap<>();
    private Map<String, Object> map = new HashMap<>();
    //新预约日期对象的集合
    private List<ScheduleNew> scheduleNewList = new ArrayList<>();
    //未读消息小圆点
//    private DesignTextView designTextView;
    private RelativeLayout rlayoutHelpnotify;
    private TextView tvHelp;
    //未读消息数
    private static int msgCount = 0;
    private boolean isLoaddingnewschedule = false;
    private boolean ischeckUpdate = false;
    //是否读取了本地数据
    private boolean hasReadLocationData = false;
    private int currentVersionCode = 0;
    private int updateVersionCode = 0;
    private String newApkUrl;
    private String target;//Apk存放目录
    private boolean hasSD = true;
    //是否下载对话框
    private AlertDialog.Builder builder;
    private ProgressBar pb;
    private UserLoginInfo user;
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int pro = msg.what;
            pb.setProgress(pro);
            pb.setVisibility(pro == 100 ? View.GONE : View.VISIBLE);
            if (pro == 101) {
                pb.setVisibility(View.GONE);
                Toast.makeText(mContext, "下载失败！", Toast.LENGTH_LONG).show();
            }
        }
    };
    private static FragmentManager fragmentManager = null;
    private Fragment scheduleFragment, messageFragment, serviceFragment, myFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (myBroadcastReceiver == null) {
            myBroadcastReceiver = new MyBroadcastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.funnco.funnco.BaiduPush.NotificationArrived");
        registerReceiver(myBroadcastReceiver, filter);
    }
    @Override
    protected void initView() {
        target = FileUtils.getRootPath();
        if (!TextUtils.isNull(target)) {
            hasSD = true;
            target += "/Funnco/funnco.apk";
            FileUtils.deleFile(target);//对之前存储过APK的进行删除
        } else {
            hasSD = false;
        }
        user = BaseApplication.getInstance().getUser();
        currentVersionCode = FunncoUtils.getVersionCode(mContext);
//        designTextView = (DesignTextView) findViewById(R.id.dtv_main_design_schedule);
        tvHelp = (TextView) findViewById(R.id.tv_main_help);
        rlayoutHelpnotify = (RelativeLayout) findViewById(R.id.rlayout_main_notify);
        if (BaseApplication.isFirstUser()){
            rlayoutHelpnotify.setVisibility(View.VISIBLE);
        }else{
            rlayoutHelpnotify.setVisibility(View.GONE);
        }
        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        pb = (ProgressBar) findViewById(R.id.pb_main_download);
        fragmentManager = getSupportFragmentManager();
        fragmentTabHost.setup(this, fragmentManager, R.id.realtabcontent);
        for (int i = 0; i < classes.length; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tab = fragmentTabHost.newTabSpec(tag[i]).setIndicator(getTabView(i));
            // 将对应Fragment添加到TabHost中
            BaseFragment bf = (BaseFragment) fragmentTabHost.addTab(tab, classes[i], null);//bundle 为传递到Fragment中的数据
            if (bf != null) {
                LogUtils.e("funnco", "Main添加的碎片是： " + bf.getClass().getSimpleName());
            }else {
                LogUtils.e("funnco", "Main添加的碎片是：null ：" + bf);
            }
            BaseFragment bf_2 = (BaseFragment) fragmentManager.findFragmentByTag(tag[i]);
            if (bf_2 != null){
                LogUtils.e("funnco", "Main添加的碎片是bf_2： " + bf_2.getClass().getSimpleName());
            }else {
                LogUtils.e("funnco", "Main添加的碎片是bf_2：null ：" + bf_2);
            }
        }

        fragmentTabHost.getTabWidget().setDividerDrawable(null);

        fragmentTabHost.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fragmentTabHost.setCurrentTab(0);
                //在该处处理点击选中后的单击触发的时间操作
                if (funncoListener != null) {
                    funncoListener.onMainAction("MainActivity-FunncoMainListener");
                }
                MediaPlayerUtils.mediaPlayMusic(mContext, R.raw.common_voice_menu_2);//播放音乐
            }
        });
        fragmentTabHost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fragmentTabHost.setCurrentTab(1);
                rlayoutHelpnotify.setVisibility(View.GONE);
                MediaPlayerUtils.mediaPlayMusic(mContext, R.raw.common_voice_menu_2);//播放音乐
            }
        });
        fragmentTabHost.getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fragmentTabHost.setCurrentTab(2);
                rlayoutHelpnotify.setVisibility(View.GONE);
                MediaPlayerUtils.mediaPlayMusic(mContext, R.raw.common_voice_menu_2);//播放音乐
            }
        });
        fragmentTabHost.getTabWidget().getChildAt(3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fragmentTabHost.setCurrentTab(3);
                rlayoutHelpnotify.setVisibility(View.GONE);
                MediaPlayerUtils.mediaPlayMusic(mContext, R.raw.common_voice_menu_2);//播放音乐
            }
        });
        builder = new AlertDialog.Builder(mContext, R.style.dialog_themen);
        builder.setMessage(R.string.update_version);
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(R.string.downloading, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (hasSD) {
                    MyHttpUtils.downLoad(newApkUrl, target, handler2, mContext);
                } else {
                    showSimpleMessageDialog(R.string.sd_no);
                }
            }
        });

        initWukong();
    }

    private void initWukong() {
        AuthInfo authInfo = AuthService.getInstance().latestAuthInfo();
        if (authInfo == null || authInfo.getOpenId() == 0){
            postData2(null, FunncoUrls.getSignatureUrl(), false);
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (url.equals(FunncoUrls.getSignatureUrl())){
            JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
            if (paramsJSONObject != null){
                ALoginParam param = JsonUtils.getObject(paramsJSONObject.toString(), ALoginParam.class);
                if (param != null){
                    com.alibaba.wukong.auth.ALoginParam aLoginParam = new com.alibaba.wukong.auth.ALoginParam();
                    aLoginParam.openId = param.getOpenId();
                    aLoginParam.domain = param.getDomain();
                    aLoginParam.nonce = param.getNonce();
                    aLoginParam.signature = param.getSignature();
                    aLoginParam.timestamp = param.getTimestamp();
                    wukongLogin(aLoginParam);
                }
            }
        }
    }

    private void wukongLogin(com.alibaba.wukong.auth.ALoginParam aLoginParam) {
        FunncoUtils.showProgressDialog(mContext, R.string.information, R.string.logining);
        AuthService.getInstance().login(aLoginParam, new Callback<AuthInfo>() {

            @Override
            public void onSuccess(AuthInfo authInfo) {
                FunncoUtils.dismissProgressDialog();
                showToast(R.string.str_login_success);

                AuthService.getInstance().setNickname(user.getNickname());
                LogUtils.e("funnco", "登录成功的authInfo 信息是：id:" + authInfo.getOpenId()
                        + "  nickname:" + authInfo.getNickname() + "  status:" + authInfo.getStatus()
                        + "  mobile:" + authInfo.getMobile() + "  domain:" + authInfo.getDomain());
//                startActivity(new Intent(mActivity, MainActivity.class));
//                mActivity.finish();
            }

            @Override
            public void onException(String s, String s1) {
                FunncoUtils.dismissProgressDialog();
                showToast(R.string.str_login_failure);
                LogUtils.e("funnco", "悟空登录失败， s:" + s + "  s1:" + s1);
            }

            @Override
            public void onProgress(AuthInfo authInfo, int i) {

            }
        });
    }

    /**
     * 从本地数据库获取未读的消息日期集合
     */
    private void getScheduleNewDate4Location() {
        hasReadLocationData = true;
        try {
            if (dbUtils != null && dbUtils.tableIsExist(ScheduleNew.class)) {
                if (scheduleNewList != null) {
                    scheduleNewList.clear();
                }
                List<ScheduleNew> ls = dbUtils.findAll(ScheduleNew.class);
                if (ls != null) {
                    for (ScheduleNew sn : ls) {
                        if (!schedule_new.containsKey(sn.getDates())) {
                            schedule_new.put(sn.getDates(), sn.getCounts());
                            LogUtils.e("" + sn.getDates(), ":::" + sn.getCounts());
                            msgCount += Integer.valueOf(sn.getCounts());
                        }
                    }
                    scheduleNewList.addAll(ls);
                    if (funncoListener != null) {
                        funncoListener.onMainData(scheduleNewList);
                        scheduleNewList.clear();
//                        posted = false;
                    }
                }
            }
            //第一步显示本地未读数
            setDesignMessageCount(msgCount);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获得最新预约日期数据集
     */
    private void getScheduleNewDate() {
        isLoaddingnewschedule = true;
        AsyncTaskUtils.requestPost(null, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0) {
                    JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                    if (paramsJSONObject != null) {
                        String total_count_2 = JsonUtils.getStringByKey4JOb(paramsJSONObject.toString(), "total_count");
                        if (scheduleFragment != null) {//将未读的消息传到schedulefragment
                            List<Integer> ls = new ArrayList<>();
                            ls.add(Integer.valueOf(total_count_2));
                            ((BaseFragment) scheduleFragment).onMainData(null, ls);
                        }
                        msgCount = Integer.parseInt(total_count_2);
                        JSONArray new_listJArray = JsonUtils.getJAry(paramsJSONObject.toString(), "new_list");
                        List<ScheduleNew> ls = JsonUtils.getObjectArray(new_listJArray.toString(), ScheduleNew.class);
                        if (ls != null) {
                            scheduleNewList.clear();//只要有心数据下载  之前的所有都清空
                            SQliteAsynchTask.deleteAll(dbUtils, ScheduleNew.class);
                            scheduleNewList.addAll(ls);//全部添加近集合
                            SQliteAsynchTask.saveOrUpdate(dbUtils, scheduleNewList);
                        }
                    }
                }
                setDesignMessageCount(msgCount);
                FunncoUtils.sendBadgeNumber(mContext, msgCount ==  0 ? null :  msgCount+ "");//发送脚标
                LogUtils.e("//将值传到ScheduleFragment", "---null");
                if (funncoListener != null && scheduleNewList.size() > 0) {
                    LogUtils.e("//将值传到ScheduleFragment", "---");
                    funncoListener.onMainData(scheduleNewList);//将值传到ScheduleFragment
                    scheduleNewList.clear();//清楚。。。传递完后进行清除
                }
                isLoaddingnewschedule = false;
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {
                isLoaddingnewschedule = false;
            }
        }, false, FunncoUrls.getScheduleNew());
    }
    private View getTabView(int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        return layoutInflater.inflate(menu_layout[position], null);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e(TAG, "MainActivity---onRestart");
    }
    @Override
    protected void initEvents() {
        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
//                MediaPlayerUtils.mediaPlayMusic(mContext, R.raw.common_voice_menu_1);//播放音乐
            }
        });
        tvHelp.setOnClickListener(this);
        findViewById(R.id.ib_main_gone).setOnClickListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(TAG, "MainActivity---触发了onActivityResult--requestCode:" + Integer.toHexString(requestCode) + "  resultCode:" + Integer.toHexString(resultCode));
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.e("MainActivity调用了---", "protected void onSaveInstanceState");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_main_help:
                startActivity(HelpActivity.class);
            case R.id.ib_main_gone:
                findViewById(R.id.rlayout_main_notify).setVisibility(View.GONE);
                break;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
//        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) designTextView.getLayoutParams();
//        params.setMargins(mScreenWidth / 6, 0, 0, 0);
//        designTextView.setLayoutParams(params);
    }

    /**
     * 设置未读消息小圆点是否显示
     * 若设置消失 自动清空未读消息数据
     *
     * @param isShow
     */
    private void setDesignVisiable(boolean isShow) {
//        if (designTextView != null) {
//            designTextView.setVisible(isShow);
//        }
    }
    /**
     * 设置未读消息的数量
     *
     * @param count
     */
    private void setDesignMessageCount(int count) {
//        if (designTextView != null) {
//            if (count > 0) {
//                designTextView.setText(count);
//                designTextView.setVisible(true);
//            } else {
//                designTextView.setVisible(false);
//            }
//        }
    }
    /**
     * 显示小圆点
     * @param count
     */
    private void showDesignMessage(int count) {
        setDesignMessageCount(count);
        setDesignVisiable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "MainActivity---触发了onDestroy  tabUtils");
        //主的Activitydestory 所有的都要销毁
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
        }
        ActivityCollectorUtils.finishAllActivities();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetUtils.isConnection(mContext) && !isLoaddingnewschedule && scheduleNewList.size() == 0) {
            handler.sendEmptyMessageDelayed(0, 1500);
        } else if (!NetUtils.isConnection(mContext) && !hasReadLocationData) {
            //获取未读消息数据
            handler.sendEmptyMessageDelayed(1, 1500);
        }
        //进行检测版本
        if (!ischeckUpdate) {
            checkUpdate();
            ischeckUpdate = true;
        }
        for (int i = 0 ; i < classes.length; i ++){
            BaseFragment bf_3 = (BaseFragment) fragmentManager.findFragmentByTag(tag[i]);
            if (bf_3 != null){
                LogUtils.e("funnco", "Main添加的碎片是bf_3： " + bf_3.getClass().getSimpleName());
            }else {
                LogUtils.e("funnco", "Main添加的碎片是bf_3：null ：" + bf_3);
            }
        }
        LogUtils.e(TAG, "MainActivity---触发了onResume 屏幕的高宽是mScreenHeight：" + mScreenHeight + " ,mScreenWidth" + mScreenWidth + " , desity:" + mDensity);
        FunncoUtils.sendBadgeNumber(mContext, null);
    }

    private void checkUpdate() {
        putAsyncTask(AsyncTaskUtils.requestPost(null, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0) {
                    JSONObject paramsJObject = JsonUtils.getJObt(result, "params");
                    ;
                    JSONObject versionJObject = JsonUtils.getJObt(paramsJObject.toString(), "version");
                    String cd = versionJObject.optString("code");
                    newApkUrl = versionJObject.optString("url");
                    if (!TextUtils.isNull(cd)) {
                        updateVersionCode = Integer.valueOf(cd);
                        if (currentVersionCode < updateVersionCode) {//当前的版本号小于数据返回的版本号则弹出对话框 进行下载选择
                            LogUtils.e("有新更新。。。currentVersionCode:" + currentVersionCode, ",,updateVersionCode:" + updateVersionCode + ",newApkUrl:" + newApkUrl);
                            builder.create().show();
                        }
                    }
                }
            }
            @Override
            public void getBitmap(String url, Bitmap bitmap) {}
        }, true, FunncoUrls.getUpdateVersion()));
    }
    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e(TAG, "MainActivity---触发了onPause  tabUtils");

    }
    private MyBroadcastReceiver myBroadcastReceiver;
    //用于Fragment 传递回来数据 决定小红点显示与隐藏
    @Override
    public void postObt(Object[] t) {//fragment 传递回来 需要减少的数量  以及移除当前日期对象
        if (scheduleNewList.size() == 0 && !TextUtils.isNull(t[1] + "")) {
            LogUtils.e("//用于Fragment 传递回来数据 决定小红点显示与隐藏", t[1] + "===" + t[0]);
            int count = Integer.valueOf((String) t[1]);
            String dates = (String) t[0];
            msgCount -= count;
            setDesignMessageCount(msgCount);
            FunncoUtils.sendBadgeNumber(mContext, msgCount == 0 ? null : msgCount+"");
            //同时删除数据库信息
            SQliteAsynchTask.deleteById(dbUtils, ScheduleNew.class, dates + "");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    LogUtils.e("接收到了广播消息--MyBroadcastReceiver", "---开始下载网络数据");
                    getScheduleNewDate();
                    break;
                case 1:
                    getScheduleNewDate4Location();
                    break;
            }
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.e("接收到了广播消息--MyBroadcastReceiver", "");
            if (intent.getAction().equals("com.funnco.funnco.BaiduPush.NotificationArrived")) {
                LogUtils.e("接收到了广播消息--MyBroadcastReceiver", "----org.agoo.android.intent.action.RECEIVE");
                if (handler!= null) {
                    handler.sendEmptyMessage(0);
                }
            }
        }
    }
    private FunncoMainListener funncoListener;

    //当前显示的Fragment 调用onAttach**时触发的方法
    @Override
    public void onAttachFragment(Fragment fragment) {
        LogUtils.e(TAG, "MainActivity---onAttachFragment" + fragment.getClass().getSimpleName());
        if (fragment instanceof ScheduleFragment) {
            scheduleFragment = fragment;
        }  else if (fragment instanceof MessageFragment) {
            messageFragment = fragment;
        } else if (fragment instanceof ServiceFragment) {
            serviceFragment = fragment;
        } else if (fragment instanceof MyFragment) {
            myFragment = fragment;
        }
        try {
            if (fragment instanceof ScheduleFragment) {
                funncoListener = (FunncoMainListener) fragment;
            }
        } catch (Exception e) {
            throw new ClassCastException(this.toString() + "this Fragment must implement OnMainListener");
        }
        super.onAttachFragment(fragment);
    }

    // 接口
    public interface FunncoMainListener {
        void onMainAction(String data);
        void onMainData(List<?>... list);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean hasPwdismiss = false;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (Fragment f : new Fragment[]{scheduleFragment, messageFragment, serviceFragment, myFragment}) {
                LogUtils.e(TAG, "current Fragment is NULL :" + (f == null) + "cFragment is Resumed :" + (f != null ?f.isResumed():"")+" cFragment is Visible :" + (f != null ?f.isVisible():""));
                if (f != null && f.isResumed() && f.isVisible()) {
                    if (((BaseFragment) f).onKeyDown(keyCode, event)){
                        hasPwdismiss = true;
                    }
                }
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        }
        return hasPwdismiss;
    }
}
