package com.funnco.funnco.utils.file;

import android.content.Context;
import android.content.SharedPreferences;

import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.log.LogUtils;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;

/**
 * Created by user on 2015/5/20.
 * @author Shawn
 */
public class SharedPreferencesUtils {

    private final static String SHAREDPREFERENCE_CONFIG = "config";

    /**
     * 判断是否是第一次登录
     * @param context
     * @param configName
     * @return
     */
    public static boolean isFirstLogin(Context context, String configName){
        SharedPreferences preferences = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        return preferences.getBoolean("isFirstLogin", true);
    }
    public static boolean isFirstLogin(Context context){
        return isFirstLogin(context, SHAREDPREFERENCE_CONFIG);
    }

    /**
     * 取值
     * @param context
     * @param configName
     * @param key
     * @return
     */
    public static String getValue(Context context, String configName, String key){
        SharedPreferences preferences = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public static String getValue(Context context,String key){
        return getValue(context, SHAREDPREFERENCE_CONFIG, key);
    }

    /**
     * 根据指定的Key 删除相应的键值对
     * @param context
     * @param configName
     * @param key
     */
    public static void removeValue(Context context , String configName, String key){
        SharedPreferences preferences = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
//            editor.remove(key);
            editor.putString(key,null);
        }catch (Exception e){
        }
        editor.commit();
    }
    public static void removeValue(Context context,String key){
        removeValue(context, SHAREDPREFERENCE_CONFIG, key);
    }

    /**
     * SharedPreferences保存数据
     * @param context
     * @param configName
     * @param key
     * @param value
     */
    public static void setValue(Context context, String configName, String key, Object value){
        SharedPreferences preferences = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof Boolean){
            editor.putBoolean(key,(boolean) value);
        }else if (value instanceof String){
            editor.putString(key,(String) value);
        }else if (value instanceof Integer){
            editor.putInt(key, (int) value);
        }else if (value instanceof Long){
            editor.putLong(key, (long) value);
        }else if (value instanceof Float){
            editor.putFloat(key,(float) value);
        }
        editor.commit();
    }

    public static void setValue(Context context,String key,Object value){
        setValue(context, SHAREDPREFERENCE_CONFIG, key, value);
    }

    /**
     * 根据传进来的UserLoginInfo 保存相应的属性值
     * @param context
     * @param configName
     * @param user
     */
    public static void setUserValue(Context context, String configName, UserLoginInfo user){
        SharedPreferences preferences = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uid", user.getId()+"");
        editor.putString("mobile", user.getMobile());
        editor.putString("token", user.getToken());
        editor.commit();
    }
    public static void setUserValue(Context context,UserLoginInfo user){
        setUserValue(context, SHAREDPREFERENCE_CONFIG, user);
    }

    public static void saveCookies(DefaultHttpClient client){
        List<Cookie> list = ((AbstractHttpClient)client).getCookieStore().getCookies();
        for (int i = 0; i < list.size(); i++) {
            //保存cookie
            Cookie cookie  = list.get(i);
            LogUtils.e("存储的Cookie", cookie.getName() + " = " + cookie.getValue());
            setValue(BaseApplication.getInstance(),cookie.getName(),cookie.getValue());
        }
    }
}
