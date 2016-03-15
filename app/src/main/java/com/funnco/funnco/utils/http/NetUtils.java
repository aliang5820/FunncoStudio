package com.funnco.funnco.utils.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by user on 2015/5/17.
 * 判断网络连接的类
 * @author Shawn
 */
public class NetUtils {
    private static ConnectivityManager manager = null;

    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isConnection(Context context){
        manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if((networkInfo != null && networkInfo.isConnected()) || (wifiInfo != null && wifiInfo.isConnected())){
            return true;
        }
        return false;
    }
}
