package com.funnco.funnco.utils.json;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by user on 2015/5/21.
 * @author Shawn
 */
public class JsonUtils {

    /**
     * 根据下载数据获得返回码
     * @param jsonString
     * @return
     */
    public static int getResponseCode(String jsonString){
        JSONObject jsonObject = getJSONObject(jsonString);
        if (jsonObject != null){
            return jsonObject.optInt("resp");
        }
        return -3;
    }
    /**
     * 判断传进来的字符串是否是json字符串
     * @param jsonString
     * @return
     */
    private static boolean isJsonObjectString(String jsonString){
        if (TextUtils.isEmpty(jsonString))
            return false;
        return jsonString.startsWith("{") && jsonString.endsWith("}");
    }

    /**
     * 返回当前传入字符串的JSONObject对象
     * @param jsonString
     * @return
     */
    private static JSONObject getJSONObject(String jsonString){
        JSONObject jsonObject = null;
        if (isJsonObjectString(jsonString)){
            try {
                jsonObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * 返回传入json字符串的msg信息
     * @param jsonString
     * @return
     */
    public static String getResponseMsg(String jsonString){
        String msg = "";
        JSONObject jsonObject = getJSONObject(jsonString);
        if (jsonObject != null) {
            msg = jsonObject.optString("msg");
        }
        return msg;
    }

    /**
     * 根据传入的JSONObject返回指定key对应的String字段
     * @param jobjString
     * @param key
     * @return
     */
    public static String getStringByKey4JOb(String jobjString, String key){
        String value = "";
        JSONObject jsonObject = getJSONObject(jobjString);
        if (jsonObject != null){
            value = jsonObject.optString(key);
        }
        return value;
    }

    public static int getIntByKey4JOb(String jobjString, String key){
        int value = 0;
        JSONObject jsonObject = getJSONObject(jobjString);
        if (jsonObject != null){
            try {
                value = jsonObject.getInt(key);
            } catch (JSONException e) {
                return 0;
            }
        }
        return value;
    }

    /**
     * 给定指定的Key获得JSONObject
     * @param paramJson
     * @param key
     * @return
     */
    public static JSONObject getJObt(String paramJson, String key){
        JSONObject t;
        t = getJSONObject(paramJson);
        if (t != null){
            return t.optJSONObject(key);
        }
        return null;
    }

    /**
     * 根据指定的key获得JSONArray
     * @param paramJson
     * @param key
     * @return
     */
    public static JSONArray getJAry(String paramJson, String key){
        JSONObject jsonObject = null;
        jsonObject = getJSONObject(paramJson);
        if (jsonObject != null){
            return jsonObject.optJSONArray(key);
        }
        return null;
    }

    /**
     * 对单个Bean进行解析
     * @param paramJson
     * @param paramCls
     * @param <T>
     * @return
     */
    public static <T> T getObject(String paramJson, Class<T> paramCls){
        T t = null;
        try {
            if (!TextUtils.isEmpty(paramJson)) {
                t = JSON.parseObject(paramJson, paramCls);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 解析一个Bean集合
     * @param paramJson
     * @param paramCls
     * @param <T>
     * @return
     */
    public static<T> List<T> getObjectArray(String paramJson, Class<T> paramCls){
        try {
            if (!TextUtils.isEmpty(paramJson)) {
                return JSON.parseArray(paramJson, paramCls);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
