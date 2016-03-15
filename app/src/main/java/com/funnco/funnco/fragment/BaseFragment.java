package com.funnco.funnco.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.view.DialogUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2015/5/14.
 * @author Shawn
 */
public abstract class BaseFragment extends Fragment implements MainActivity.FunncoMainListener {

    private static final String TAG = "BaseFragment";

    protected BaseApplication mApplication;
    protected BaseActivity mActivity;
    protected Context mContext;
    protected Dialog dialog;
    protected ImageView ivLoading;
    protected PopupWindow pwLoading;
    protected ProgressBar pb;


    protected static String STR_LAN = BaseApplication.getInstance().getLan();;

    private Dialog smDialog;


    /**
     * 搜索页面List列表
     */
    protected View parentView;

    /**
     * 屏幕的宽度、高度、密度
     */
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected float mDensity;
    /**
     * 数据库工具类
     */
    protected DbUtils dbUtils = BaseApplication.getInstance().getDbUtils();
    /**
     * List集合--异步任务
     */
    protected List<AsyncTask> mAsyncTasks = new ArrayList<>();

    public BaseFragment() {
        super();
    }

    public BaseFragment(BaseApplication application, Activity activity,
                        Context context) {
        /**
         * 获取屏幕宽度、高度、密度
         */
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mDensity = metric.density;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mContext == null){
            mContext = this.getActivity();
        }
        if (mApplication == null){
            mApplication = BaseApplication.getInstance();
        }
        if (mActivity == null){
            mActivity = (BaseActivity)getActivity();
        }
        //网络提交进度条
        pb = new ProgressBar(mContext);
        pwLoading = new PopupWindow(pb, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        pwLoading.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViews();
        initEvents();
        init();
        return parentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!NetUtils.isConnection(mActivity)){
            showNetErr();
        }
    }

    protected void showNetErr(){
        Toast toast = new Toast(BaseApplication.getInstance());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        View view = mActivity.getLayoutInflater().inflate(R.layout.layout_popup_header_netinfo, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.findViewById(R.id.tv_pw_netinfo).setLayoutParams(params);
        toast.setView(view);
        toast.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen_F");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen_F");
    }

    @Override
    public void onDestroyView() {
        clearAsyncTask();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract void initViews();

    protected abstract void initEvents();

    protected abstract void init();

    public View findViewById(int id) {
        return parentView.findViewById(id);
    }

    /**List增加异步事件
     * @param asyncTask
     */
    protected void putAsyncTask(AsyncTask asyncTask) {
        mAsyncTasks.add(asyncTask);
    }

    /**
     * 终止线程中事件
     */
    protected void clearAsyncTask() {
        Iterator<AsyncTask> iterator = mAsyncTasks
                .iterator();
        while (iterator.hasNext()) {
            AsyncTask asyncTask = iterator.next();
            if (asyncTask != null && !asyncTask.isCancelled()) {
                asyncTask.cancel(true);
            }
        }
        mAsyncTasks.clear();
    }

    /** 通过Class跳转界面 **/
    /**
     * @param cls
     */
    protected void startActivity(Class<?> cls){
        startActivity(cls, null);
    }

    protected void startActivity(Class<?> cls,Bundle bundle){
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    protected void startActivity(String action){
        startActivity(action, null);
    }
    protected void startActivity(String action,Bundle bundle){
        Intent intent = new Intent();
        intent.setAction(action);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    protected void startActivityForResult(String action,Bundle bundle,int requestCode){
        Intent intent = new Intent();
        intent.setAction(action);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    protected void startActivityForResult(Class<?> cls,Bundle bundle,int requestCode){
        Intent intent = new Intent();
        intent.setClass(mContext, cls);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent,requestCode);
    }


    protected void postData2(Map<String,Object> map,final String url, boolean isGet){
        UserLoginInfo user = BaseApplication.getInstance().getUser();
        if (user == null){
            try {
                user = dbUtils.findById(UserLoginInfo.class, SharedPreferencesUtils.getValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.UID));

            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        if (user == null){
            showSimpleMessageDialog(R.string.user_err);
            return;
        }
        if (!NetUtils.isConnection(mContext)){
            showNetErr();
            return;
        }
        putAsyncTask(AsyncTaskUtils.requestPost(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                String msg = JsonUtils.getResponseMsg(result);
                if (JsonUtils.getResponseCode(result) == 0){
                    dataPostBack(result,url);
                }else{
                    dataPostBackF(result, url);
                    showToast(msg + "");
                }
            }
            @Override
            public void getBitmap(String url, Bitmap bitmap) {
            }
        }, isGet, url));
    }
    protected void dataPostBack(String result, String url){}
    protected void dataPostBackF(String result, String url){}
    //
    protected void showSimpleMessageDialog(String message){
        if (TextUtils.isNull(message) || TextUtils.equals("null", message)){
            message += "数据异常！";
        }
        smDialog = DialogUtils.getSimpleMessageDialog(getActivity(), message);
        smDialog.show();
    }
    protected void showSimpleMessageDialog(int resourceId){
        showSimpleMessageDialog(mContext.getResources().getString(resourceId));
    }
    protected void dismissSimpleMessageDialog(){
        if (smDialog != null && smDialog.isShowing()){
            smDialog.dismiss();
        }
    }

    /**
     * 弹出Loading对话框
     */
    public void showLoading(View parentView){
        if (pwLoading != null && !pwLoading.isShowing()){
            if (parentView != null && parentView.hasWindowFocus() && parentView.isShown()) {
                pwLoading.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }
    public void dismissLoading(){
        if (pwLoading != null && pwLoading.isShowing()){
            pwLoading.dismiss();
        }
    }
    protected void dismissLoadingDialog(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
    /**
     * 用于测试时 吐司信息
     */
    protected void showToast(String str){
        FunncoUtils.showToast(mContext, str);
    }
    protected void showToast(int resId){
        FunncoUtils.showToast(mContext, resId);
    }

    protected void deleteById(Class c,String id){
        SQliteAsynchTask.deleteById(dbUtils, c, id);
    }

    /**
     * 用于同步宿主Activity的onKeyDown监听事件
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            LogUtils.e("BaseFragmet事件", "OK");
        }
        return false;
    }
}
