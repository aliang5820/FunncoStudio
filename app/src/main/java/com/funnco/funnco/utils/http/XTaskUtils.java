package com.funnco.funnco.utils.http;

import android.content.Context;
import android.content.Intent;

import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.file.FileTypeUtils;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.string.TextUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用第三方工具 XUtils 进行网络数据提交
 * Created by user on 2015/10/22.
 */
public class XTaskUtils {

    public static boolean requestPost(Context context,HttpUtils httpUtils,String url,String[]keys, Object[]values,String[] fileKeys,String[] filePahts, final DataBack post){
        if (httpUtils == null || TextUtils.isNull(url)){
            return false;
        }
        RequestParams params = new RequestParams();
        CookieStore cookieStore = BaseApplication.getInstance().getCookieStore();
        if (cookieStore == null) {
            context.sendBroadcast(new Intent().setAction(Actions.ACTION_CHOOSE_AHEADTIME));
            return false;
        }
        //添加cookie
        StringBuilder sb = new StringBuilder();
        List<Cookie> list = cookieStore.getCookies();
        for (int i = 0; i < list.size(); i++) {
            Cookie c = list.get(i);
            sb.append(c.getName() + "=" + c.getValue() + ";");
        }
        sb.deleteCharAt(sb.length() - 1);
        params.addHeader("Cookie", sb.toString());
        //添加字段
        ArrayList<NameValuePair> listParams = new ArrayList<>();
        if (keys != null && values != null && keys.length == values.length) {
            for (int i = 0; i < keys.length; i++) {
                listParams.add(new BasicNameValuePair(keys[i], String.valueOf(values[i])));
            }
        }else{
            return false;
        }
        params.addBodyParameter(listParams);
        //添加上传文件
        if (fileKeys != null && filePahts != null && fileKeys.length == filePahts.length){
            for (int i = 0 ;i < fileKeys.length; i ++) {
                if (TextUtils.isNull(fileKeys[i]) || TextUtils.isNull(filePahts[i])){
                    continue;
                }
                params.addBodyParameter(fileKeys[i],new File(filePahts[i]), FileTypeUtils.getType(filePahts[i]));
            }
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                post.getString(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                post.getString(s);
            }
        });
        return true;
    }
}
