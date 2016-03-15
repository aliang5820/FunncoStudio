package com.funnco.funnco.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.funnco.funnco.utils.http.LoginUtils;
import com.funnco.funnco.utils.log.LogUtils;

/**
 * 用于重新登录的广播
 * Created by user on 2015/10/22.
 */
public class ReloginReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("funnco","触发了广播，进行自动登录。。。");
        LoginUtils.reLogin(context, null);
    }
}
