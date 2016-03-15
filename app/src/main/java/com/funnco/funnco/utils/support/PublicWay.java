package com.funnco.funnco.utils.support;

import android.app.Activity;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
     * Created by user on 2015/5/27.
     * 存放所有的list在最后退出时一起关闭
     *
     * @author Shawn
     * @QQ:1206816341
     * @version 2015年5月27日17:46:33
     */
    public class PublicWay {
        public static List<Activity> activityList = new ArrayList<Activity>();
        public static int num = 10;

//        public static List<View> editWorkItemList = new ArrayList<View>();
        public static Map<String, View> editWorkItemList = new HashMap<String, View>();

    //用于存放title的值
        public static HashMap<String, String> hashMapTitle = new HashMap<String, String>();
    //用于存放desc信息
    public static HashMap<String, String> hashMapDesc = new HashMap<String, String>();

    }