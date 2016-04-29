package com.funnco.funnco.activity.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.login.ForeActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.DebugUtil;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.LoginUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.http.VolleyUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.ActivityCollectorUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.view.DialogUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Shawn
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected String TAG = this.getClass().getSimpleName();

    protected BaseApplication mApplication;
    protected Context mContext;
    protected Activity mActivity;
    protected Dialog dialog;
    private Dialog smDialog;
    protected PopupWindow pwLoading;
    protected ProgressBar pb;
    //用于检查网络连接的PopupWindow
    private PopupWindow checkNetPopupWindow;
    private View pwView;
    //用于存放异步任务 统一管理
    private List<AsyncTask> mAsyncTasks = new ArrayList<>();

    /**
     * 屏幕的高、宽、密度、status高度
     */
    protected static int mScreenWidth;
    protected static int mScreenHeight;
    protected static float mDensity;
    protected static int mStatusBarHeight;
    /**
     * 用于数据传递key时的常量
     */
    protected static final String KEY = "key";
    protected static String STR_LAN = "cn";
    private String msg;

    //是否登录，默认值为false
    protected static boolean isLogin = false;
    private boolean initView = true;
    /**
     * 数据库操作
     */
    protected DbUtils dbUtils;
    /**
     * 表单数据上传
     */
    protected VolleyUtils utils;
    /**
     * 图片下载
     */
    protected static ImageLoader imageLoader;
    /**
     * 图片下载参数
     */
    protected static DisplayImageOptions options;
    public static String device_token;

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public static DisplayImageOptions getOptions() {
        return options;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = BaseApplication.getInstance();
        mContext = this;
        mActivity = this;
        dbUtils = BaseApplication.getInstance().getDbUtils();
        utils = new VolleyUtils(mContext);
        if (mScreenWidth == 0 || mScreenHeight == 0 || mDensity == 0) {
            LogUtils.e(TAG, "有为0 前  BaseActivity  mScreenWidth：" + mScreenWidth + " ,mScreenHeight:" + mScreenHeight + " ,mDensity:" + mDensity + " ,mStatusBarHeight:" + mStatusBarHeight);
            DisplayMetrics outMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            mScreenHeight = outMetrics.heightPixels;
            mScreenWidth = outMetrics.widthPixels;
            mDensity = outMetrics.density;
        }
        if (TextUtils.isEmpty(device_token) || TextUtils.equals("null", device_token)) {
            device_token = SharedPreferencesUtils.getValue(mContext, Constants.DEVICE_TOKEN);
        }
        DebugUtil.traceLog("BaseActivity:device_token:" + device_token);
        LogUtils.e(TAG, "百度推送是否可用：" + PushManager.isPushEnabled(mContext));
        if (!PushManager.isPushEnabled(mContext) || TextUtils.isEmpty(device_token) || TextUtils.equals("null", device_token)) {
            String appiKey = getString(R.string.baidu_appkey);
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, appiKey);
        }

        STR_LAN = BaseApplication.getInstance().getLan();
        LogUtils.e("------", "本机的语言环境是：" + STR_LAN);
        //
        ActivityCollectorUtils.addActivity(this);
        pwView = getLayoutInflater().inflate(R.layout.layout_popup_header_netinfo, null);
        checkNetPopupWindow = new PopupWindow(pwView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        checkNetPopupWindow.setClippingEnabled(true);
        checkNetPopupWindow.setOutsideTouchable(true);
        checkNetPopupWindow.setSplitTouchEnabled(true);

        //网络提交进度条
        pb = new ProgressBar(mContext);
        pwLoading = new PopupWindow(pb, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pwLoading.setBackgroundDrawable(new BitmapDrawable());

        //imageLoader
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.icon_edit_profile_default_2x)
                .showImageForEmptyUri(R.mipmap.icon_edit_profile_default_2x)
                .showImageOnFail(R.mipmap.icon_edit_profile_default_2x)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        loadLayout();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtils.e(TAG, "调用了onWindowFocusChanged");
        if (mStatusBarHeight == 0) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            // 状态栏高度
            mStatusBarHeight = frame.top;
            LogUtils.e(TAG, "有为0 后  BaseActivity  ,mStatusBarHeight:" + mStatusBarHeight);
        }
    }

    protected void loadLayout() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        PushManager.resumeWork(mContext);
        //这两个调用将不会阻塞应用程序的主线程，也不会影响应用程序的性能。
        MobclickAgent.onResume(this);
        if (!NetUtils.isConnection(this)) {
            showNetInfo();
        }
        String uid = SharedPreferencesUtils.getValue(mContext, Constants.UID) + "";
        if (!TextUtils.isEmpty(uid) && !TextUtils.equals("null", uid) && BaseApplication.getInstance().getCookieStore() == null && !TAG.equals(ForeActivity.class.getSimpleName())) {
            LogUtils.e("666666", "6666666 uid:" + uid);
            LoginUtils.reLogin(mContext, new Post() {
                @Override
                public void post(int... position) {

                }
            });
        }
    }

    public void showNetInfo() {
//            checkNetPopupWindow.showAtLocation(view,Gravity.TOP,0,0);
        Toast toast = new Toast(BaseApplication.getInstance());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        View view = getLayoutInflater().inflate(R.layout.layout_popup_header_netinfo, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.findViewById(R.id.tv_pw_netinfo).setLayoutParams(params);
        toast.setView(view);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        clearAsyncTask();
        ActivityCollectorUtils.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initEvents();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        if (initView) {
            initView();
            initEvents();
        }
    }

    protected void setContentView(View view, boolean initView) {
        this.initView = initView;
        setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        if (!NetUtils.isConnection(this)) {
            checkNetPopupWindow.showAtLocation(view, Gravity.TOP, 0, 0);
        }
        initView();
        initEvents();
    }

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化监听
     */
    protected abstract void initEvents();

    /**
     * 添加异步任务
     *
     * @param asyncTask 需要添加的AsyncTask
     */
    protected void putAsyncTask(AsyncTask asyncTask) {
        mAsyncTasks.add(asyncTask);
    }

    /**
     * 清除所有的异步任务
     */
    protected void clearAsyncTask() {
        Iterator<AsyncTask> iterator = mAsyncTasks.iterator();
        LogUtils.e("执行了BaseActivity clearAsyncTask方法：", "异步任务的大小是：" + mAsyncTasks.size());
        while (iterator.hasNext()) {
            AsyncTask asyncTask = iterator.next();
            if (asyncTask != null && !asyncTask.isCancelled()) {
                asyncTask.cancel(true);
                LogUtils.e("BaseActivity 销毁了一个异步任务：", "" + asyncTask);
            }
        }
        mAsyncTasks.clear();
    }

    /**
     * @param cls
     */
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(String action) {
        startActivity(action, null);
    }

    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 打印Log
     */
    protected void showLogDebug(String tag, String msg) {
        LogUtils.d(tag, msg);
    }

    protected void showLogInfo(String tag, String msg) {
        LogUtils.i(tag, msg);
    }

    protected void showLogErr(String tag, String msg) {
        LogUtils.e(tag, msg);
    }

    /**
     * 用于测试时 吐司信息
     */
    protected void showToast(String str) {
        FunncoUtils.showToast(mContext, str);
    }

    protected void showToast(int resId) {
        FunncoUtils.showToast(mContext, resId);
    }

    /**
     * 弹出Loading对话框
     */
    public void showLoading(View parentView) {
        if (pwLoading != null && !pwLoading.isShowing()) {
            if (parentView != null && parentView.hasWindowFocus() && parentView.isShown()) {
                pwLoading.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }

    public void dismissLoading() {
        if (pwLoading != null && pwLoading.isShowing()) {
            pwLoading.dismiss();
        }
    }

    protected void dismissLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected void showSimpleMessageDialog(String message) {
        if (TextUtils.isEmpty(message) || TextUtils.equals("null", message)) {
            message += "数据异常！";
        }
        smDialog = DialogUtils.getSimpleMessageDialog(mContext, message);
        smDialog.setCanceledOnTouchOutside(true);
        smDialog.show();
    }

    protected void showSimpleMessageDialog(int resourceId) {
        showSimpleMessageDialog(mContext.getResources().getString(resourceId));
    }

    protected void dismissSimpleMessageDialog() {
        if (smDialog != null && smDialog.isShowing()) {
            smDialog.dismiss();
        }
    }

    protected void postData2(Map<String, Object> map, final String url, boolean isGet) {
        UserLoginInfo user = BaseApplication.getInstance().getUser();
        if (user == null) {
            try {
                user = dbUtils.findById(UserLoginInfo.class, SharedPreferencesUtils.getValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.UID));
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        if (!NetUtils.isConnection(mContext)) {
            showNetInfo();
            return;
        }
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoadingDialog();
                String msg = JsonUtils.getResponseMsg(result) + "";
                if (JsonUtils.getResponseCode(result) == 0) {
                    dataPostBack(result, url);
                } else {
                    dataPostBackF(result, url);
                    showSimpleMessageDialog("" + msg);
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {
            }
        }, isGet, url));
    }

    protected void dataPostBack(String result, String url) {
    }

    protected void dataPostBackF(String result, String url) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.e("触发了BaseActivity的FinishAllActivity方法", "");
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            ActivityCollectorUtils.finishAllActivities();
        }
        return super.onKeyDown(keyCode, event);

    }

    protected void finishOk() {
        clearAsyncTask();
        finish();
    }
}
