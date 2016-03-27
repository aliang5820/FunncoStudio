package com.funnco.funnco.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.http.MyHttpUtils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/5/24.
 *
 * @author Shawn
 */
public class AsyTask extends AsyncTask<String, Integer, String> {
    private Map<String, Object> map = new HashMap<>();
    private DataBack dataBack = null;
    boolean isGet = false;

    public AsyTask() {

    }

    public AsyTask(Map<String, Object> map, DataBack dataBack, boolean isGet) {
        this.map.clear();
        if (map != null) {
            this.map.putAll(map);
        }
        this.map.put("client", "android");
        this.map.put("lan", BaseApplication.getInstance().getLan());
        //打印上传数据数据
        for (Map.Entry<String, Object> m : this.map.entrySet()) {
            LogUtils.e("提交数据是：", "::" + m.getKey() + "-" + m.getValue());
        }
        this.dataBack = dataBack;
        this.isGet = isGet;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            if (isGet) {
                String url;
                if(params[0].endsWith("?")) {
                    url = params[0] + getRequestData(map);
                } else {
                    url = params[0] + "&" + getRequestData(map);
                }
                return MyHttpUtils.httpGet(url);
            } else {
                return MyHttpUtils.httpPost(params[0], map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        LogUtils.e("funnco下载的数据是：", "" + s);
        if (dataBack != null) {
            dataBack.getString(s);
        }
    }

    /**
     * Function  :   封装请求体信息
     * @param params 请求体内容
     * @return
     */
    public static String getRequestData(Map<String, Object> params) {
        StringBuilder stringBuffer = new StringBuilder();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                stringBuffer.append(entry.toString()).append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
