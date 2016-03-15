package com.funnco.funnco.utils.support;

import android.annotation.TargetApi;

import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.utils.log.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;

/**
 * Activity管理的类
 * Created by Shawn on 2015/5/14.
 * @author Shawn
 */
public class ActivityCollectorUtils {
    private static LinkedList<BaseActivity> queue = new LinkedList<BaseActivity>();

    /**
     */
    public static void addActivity(BaseActivity activity){
        queue.add(activity);
    }
    /**

     */
    public static void removeActivity(BaseActivity activity){
        queue.remove(activity);
    }

    /**
     */
    public static void finishAllActivities(){
        LogUtils.e("queue集合共村里多少个Activity", "  " + queue.size());
        for (BaseActivity activity : queue){
            if (!activity.isFinishing()){
                activity.finish();
                queue.remove(activity);
            }
        }
        MobclickAgent.onKillProcess(BaseApplication.getInstance());
    }
    public static void finishActivity(BaseActivity activity){
        if (queue.contains(activity)){
            finishActivity(activity.getClass().getSimpleName());
        }
    }

    public static void finishActivity(String activityName){
        for (BaseActivity ba : queue){
            if (ba.getClass().getSimpleName().equals(activityName)){
                ba.finish();
                queue.remove(ba);
            }
        }
    }
    @TargetApi(17)
    public static void finishActivity(Class cls){
        if (cls == null){
            return;
        }
        for (BaseActivity ba : queue){
            if (ba.getClass() == cls && !ba.isDestroyed()){
                ba.finish();
                queue.remove(ba);
            }
        }
    }
    public static void finishActivity(Class<?>...cls){
        for (Class mCls : cls){
            finishActivity(mCls);
        }
    }


    /**
     */
    public static int getActivitiesNum(){
        if(!queue.isEmpty()){
            return queue.size();
        }
        return 0;
    }

    /**
     */
    public static BaseActivity getLastActivity(){
        if(!queue.isEmpty()){
            return queue.getLast();
        }
        return null;
    }

}
