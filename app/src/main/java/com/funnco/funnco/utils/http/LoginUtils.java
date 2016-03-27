package com.funnco.funnco.utils.http;

import android.content.Context;
import android.graphics.Bitmap;

import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于没有网络  重新登录获得Cookies
 * Created by user on 2015/7/16.
 */
public class LoginUtils {
    /**
     * 执行登录操作  目的得到Cookie
     * @param post
     */
    public static void reLogin(Context context,final Post post){
        String SHAREDPREFERENCE_CONFIG = "config";

        Map<String,Object> map = new HashMap<>();
        String mobile = SharedPreferencesUtils.getValue(context, SHAREDPREFERENCE_CONFIG, "mobile");
        String pwd = SharedPreferencesUtils.getValue(context,SHAREDPREFERENCE_CONFIG,"pwd");
        String device_token = SharedPreferencesUtils.getValue(context, SHAREDPREFERENCE_CONFIG, "device_token");
        LogUtils.e("666666", "获取cookies  ;：：mobile:" + mobile + " , pwd:" + pwd + " ,device_token:" + device_token);
        map.put("mobile",mobile);
        map.put("pwd", pwd);
        map.put("device_token",device_token);
        if (TextUtils.isNull(mobile) || TextUtils.isNull(pwd) || TextUtils.isNull(device_token)){
            return;
        }
        AsyTask task = new AsyTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0) {
                    if (post != null)
                        post.post(0);
                }
            }
            @Override
            public void getBitmap(String url, Bitmap bitmap) {}
        },false);
        task.execute(FunncoUrls.getLoginUrl());
    }
}
