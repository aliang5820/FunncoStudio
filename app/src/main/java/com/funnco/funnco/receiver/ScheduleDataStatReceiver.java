package com.funnco.funnco.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.funnco.funnco.service.ScheduleDataStatService;

/**
 * Created by user on 2015/6/30.
 */
public class ScheduleDataStatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //检查Service状态
        Intent intent2 = new Intent(context, ScheduleDataStatService.class);
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.funnco.funnco.service.ScheduleDataStatService".equals(service.service.getClassName())) {
                context.stopService(intent2);
            }
        }
        context.startService(intent2);
    }
}