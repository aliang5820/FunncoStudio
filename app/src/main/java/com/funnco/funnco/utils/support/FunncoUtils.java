package com.funnco.funnco.utils.support;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.wukong.auth.AuthInfo;
import com.alibaba.wukong.auth.AuthService;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.activity.login.ForeActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.utils.bimp.CacheManager;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 2015/6/16.
 */
public class FunncoUtils {
    private static ProgressDialog mProgressDialog;
    private static String mNotifyTitle;
    private static PendingIntent mPendIntent;
    private static Notification mNotification;
    private static NotificationManager mNotificationManager;

    /**
     * 显示进度对话框
     * @param context
     * @param title
     */
    public synchronized static void showProgressDialog(Context context,String  title){
        showProgressDialog(context, title, "Please wait for a moment...");
    }
    public synchronized static void showProgressDialog(Context context, String title,String message){

        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(context);
        }
        if (mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        if(!TextUtils.isNull(title))mProgressDialog.setTitle(title);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }
    public synchronized static void showProgressDialog(Context context, int titleId, int messageId){
        String title = context.getString(titleId);
        String message = context.getString(messageId);
        showProgressDialog(context,title,message);
    }

    /**
     * 关闭进度对话框
     */
    public synchronized static void dismissProgressDialog(){
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    /**
     * 创建并显示一个只包含“是”与“否”按钮简单对话框
     * @param context
     * @param title
     * @param callback
     */
    public static void showAlertDialog(final Context context,final String title,final DialogCallback callback){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositive();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    public static void showAlertDialog(final Context context,final int titleId,final DialogCallback callback){
        String title = context.getString(titleId);
        showAlertDialog(context, title, callback);
    }

    /**
     * 点击对话框确定按钮后的回调接口
     */
    public interface DialogCallback{
        void onPositive();
    }

    /**
     * 检测当前App是否在前台运行
     * @param context
     * @return true 前台运行，false 后台运行
     */
    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        // 正在运行的应用
        ActivityManager.RunningTaskInfo foregroundTask = runningTasks.get(0);
        String packageName = foregroundTask.topActivity.getPackageName();
        String myPackageName = context.getPackageName();

        // 比较包名
        return packageName.equals(myPackageName);
    }
    /**
     * 发出有新消息通知
     * @param unReadCount 新消息数
     */
    public static void sendNotification(int unReadCount) {
        Context ctx = BaseApplication.getInstance();

        if(mNotificationManager == null) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Service.NOTIFICATION_SERVICE);
        }

        if(mNotification == null) {
            NotificationManager notificationManager = (NotificationManager) ctx
                    .getSystemService(Service.NOTIFICATION_SERVICE);
            mNotification = new Notification();
            mNotification.icon = R.mipmap.launch_icon; // 设置图标，公用图标
            mNotification.tickerText = ctx.getString(R.string.app_name);
            mNotification.defaults = Notification.DEFAULT_ALL;   // 提示音
            // 手机震动 -- 权限： <uses-permission android:name="android.permission.VIBRATE"/>
//        notification.defaults = Notification.DEFAULT_VIBRATE;
//        long[] vibrate = {0,100,200,300};
//        notification.vibrate = vibrate;

            // LED 灯闪烁
//        notification.defaults = Notification.DEFAULT_LIGHTS;
//        notification.ledARGB = 0xff00ff00;
//        notification.ledOffMS = 1000;
//        notification.ledOnMS = 300; // 闪光时间，毫秒

        /*
         * 设置Flag的值：说明
         * FLAG_AUTO_CANCEL : 通知能被状态按钮清除掉
         * FLAG_NO_CLEAR : 点击清除按钮，不清除
         * FLAG_ONGOING_EVENT:  该通知放置在正在运行组中
         * FLAG_INSISTENT : 是否一直进行，比如播放音乐，直到用户响应
         */
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        }

        if(TextUtils.isNull(mNotifyTitle)) {
            mNotifyTitle = ctx.getString(R.string.app_name);
        }

        if(mPendIntent == null) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(ctx.getPackageName(), MainActivity.class.getName());
            mPendIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mNotification.when = System.currentTimeMillis(); // 当前时间 ，通知时间
        String notifyContent = ctx.getString(R.string.new_message_notify_content, unReadCount);
        mNotification.setLatestEventInfo(ctx, mNotifyTitle, notifyContent, mPendIntent);
        mNotificationManager.notify(1, mNotification);
    }
    /**
     * 获得屏幕宽度
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context context) {
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
    }

    /**
     * 获得屏幕高度
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context context){
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getHeight();
    }

    /**
     * 获取屏幕的密度
     * @param activity
     * @return
     */
    @SuppressWarnings("deprecation")
    public static float getScreenDensity(Activity activity){
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//        mScreenHeight = outMetrics.heightPixels;
//        mScreenWidth = outMetrics.widthPixels;
//        int desity = (int) activity.getResources().getDisplayMetrics().density;
        return outMetrics.density;
    }

    public static int dp2px(Activity activity,float dp){
        float density = getScreenDensity(activity);
        return dp2px(density, dp);
    }

    public static int dp2px(float density,float dp){
        return (int) (dp * density + 0.5f);
    }
    public static int dp2px(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }
    public static int dp2px(Context context, float dp){
        if(null != context && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        return (int)dp;
    }
    public static int px2dip(Context context, float pxValue) {

        if(null != context && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        return (int)pxValue;
    }
    /**
     * 获得当前的版本名
     * @param context 当前上下文
     * @return
     */
    public static String getVersionName(Context context){
        String version = "0";
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }

    /**
     * 获得当前版本号
     * @param context 当前上下文
     * @return
     */
    public  static int getVersionCode(Context context){
        int versionCode = 0;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionCode;
    }

    /**
     * 拨打电话
     * @param context 当前上下文
     * @param mobile 电话号码
     */
    public static void callPhone(Context context, String mobile){
        Uri uri = Uri.parse("tel:"+mobile);
        Intent intent = new Intent(Intent.ACTION_CALL,uri);
        context.startActivity(intent);
    }

    /**
     * 是否运行于后台
     * @param context
     * @return
     */
    public static boolean isBackGroundRunning(Context context){
        String packageName = "com.funnco.funnco";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (activityManager == null){
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        if (list == null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo rap : list){
            if (rap.processName.startsWith(packageName)) {
                boolean isBackGround = rap.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && rap.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackGround || isLockedState){
                    LogUtils.e("-----", "应用运行于后台：isLockedState=" + isLockedState + " , isBackGround=" + isBackGround);
                    return true;
                }else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获得当前语言环境
     * @param resources
     * @return
     */
    public static String getLocateLanguage(Resources resources){
        Locale locale = resources.getConfiguration().locale;
        return locale.getLanguage();
    }

    /**
     * 设置当前语言环境
     * @param resources
     * @param language
     */
    public static String setLocateLanguage(Resources resources,String language){
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(language);
        // 获得屏幕参数：主要是分辨率，像素等
        DisplayMetrics dm = resources.getDisplayMetrics();
        // 语言
        config.locale = locale;
        resources.updateConfiguration(config, dm);
        return language;
    }

    /**
     * 设置当前语言环境
     * @param resources
     * @param locale
     * @return
     */
    public static String setLocateLanguage(Resources resources,Locale locale){
        return setLocateLanguage(resources, locale.getLanguage());
    }


    public static String getMCC(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!android.text.TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

    /**
     * 获得view 的宽高
     * @param view
     * @return
     */
    public static int[] getViewMesureSpec(View view){
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width,height);
        height=view.getMeasuredHeight();
        width=view.getMeasuredWidth();
        return new int[]{width,height};
    }

    /**
     * 获得缓存的总大小
     * @param context
     * @return
     */
    public static String getTotalCacheSize(Context context){
        try {
            return CacheManager.getTotalCacheSize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00B";
    }

    /**
     * 获得缓存的总大小
     * @param context
     * @return
     */
    public static long getTotalCacheSize2(Context context){
        long cacheSize = 0;
        try {
            cacheSize = CacheManager.getFolderSize(context.getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cacheSize += CacheManager.getFolderSize(context.getExternalCacheDir());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cacheSize;
    }

    /**
     * 清除所有缓存
     * @param context
     */
    public static void clearAllCache(Context context){
        CacheManager.clearAllCache(context);
    }

    public static void showToast(Context context, int resId){
        String content = context.getString(resId);
        showToast(context, content);
    }

    public static void showToast(Context context, String content){
        if (!TextUtils.isNull(content)){
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取当前用户的openId
     */
    public static long currentOpenId(){
        AuthInfo info = AuthService.getInstance().latestAuthInfo();
        return info == null?0L : info.getOpenId();
    }

    /**
     * 获取当前用户的昵称
     */
    public static String currentNickname(){
        AuthInfo info = AuthService.getInstance().latestAuthInfo();
        return info == null?null : info.getNickname();
    }
    public static void sendBadgeNumber(Context context, String number) {
        if (TextUtils.isNull(number)) {
            number = "";
        } else {
            int numInt = Integer.valueOf(number);
            number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(context, number);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendToSony(context, number);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendToSamsumg(context, number);
        } else {
//            Toast.makeText(this, "Not Support", Toast.LENGTH_LONG).show();
            LogUtils.e("funnco","桌面脚标目前只支持xiaomi/samsung/sony");
        }
    }
    private static final String lancherActivityClassName = ForeActivity.class.getName();
    public static void sendToXiaoMi(Context context, String number) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有"+number+"未读消息");
            builder.setTicker("您有"+number+"未读消息");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.common_circle_click);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, number);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);
//            Toast.makeText(this, "Xiaomi=>isSendOk=>1", Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name",
                    context.getPackageName() + "/"+ lancherActivityClassName );
            localIntent.putExtra("android.intent.extra.update_application_message_text",number);
            context.sendBroadcast(localIntent);
        }
        finally
        {
            if(notification!=null && isMiUIV6 )
            {
                //miui6以上版本需要使用通知发送
                nm.notify(101010, notification);
            }
        }

    }

    public static void sendToSony(Context context,String number) {
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",lancherActivityClassName );//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
        context.sendBroadcast(localIntent);

//        Toast.makeText(this, "Sony," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    public static void sendToSamsumg(Context context, String number)
    {
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", number);//数字
        localIntent.putExtra("badge_count_package_name", context.getPackageName());//包名
        localIntent.putExtra("badge_count_class_name", lancherActivityClassName); //启动页
        context.sendBroadcast(localIntent);
//        Toast.makeText(this, "Samsumg," + "isSendOk", Toast.LENGTH_LONG).show();
    }
}
