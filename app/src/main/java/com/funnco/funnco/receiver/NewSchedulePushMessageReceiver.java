package com.funnco.funnco.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 系统发送消息触发的广播接收器
 * 用于保存未读信息数
 * Created by user on 2015/7/9.
 * @author Shawn
 */
public class NewSchedulePushMessageReceiver extends BroadcastReceiver {
    private static String SHAREDPREFERENCE_CONFIG = "config";
    private static String NEWSCHEDULECOUNT = "newScheduleCount";
    @Override
    public void onReceive(Context context, Intent intent) {

//        //处理逻辑步骤
//        String count = SharedPreferencesUtils.getValue(context, SHAREDPREFERENCE_CONFIG, NEWSCHEDULECOUNT);
//        if (TextUtils.isNull(count)){
//            count = "0";
//        }
//        LogUtils.e("自定义广播接收器接收到了 消息", "，，，未读信息是："+count);
//        int count2 = Integer.parseInt(count);
//        count2++;
//        SharedPreferencesUtils.setValue(context,SHAREDPREFERENCE_CONFIG,NEWSCHEDULECOUNT,count2+"");
    }
}
