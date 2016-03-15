package com.funnco.funnco.task;

import android.os.AsyncTask;

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
public class MyLoginAsynchTask extends AsyncTask<String, Integer, String> {
    private Map<String, Object> map = new HashMap<>();
    private DataBack dataBack = null;
    boolean isGet = false;

    public MyLoginAsynchTask() {

    }

    public MyLoginAsynchTask(Map<String, Object> map, DataBack dataBack, boolean isGet) {
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
        LogUtils.e("接口地址：",""+params[0]);

        try {
//            if (isGet)
//                return MyHttpUtils.httpGet(params[0]);
//            Thread.sleep(300);
            return MyHttpUtils.httpPost(params[0], map);
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
     * @param encode 编码格式
     * @return
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
