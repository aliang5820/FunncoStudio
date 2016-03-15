package com.funnco.funnco.utils.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.PostObt;
import com.funnco.funnco.task.FindAsyncTask;
import com.funnco.funnco.task.MyLoginAsynchTask;
import com.funnco.funnco.task.SaveAsyncTask;
import com.lidroid.xutils.DbUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 2015/7/10.
 */
public class AsyncTaskUtils {

    /**
     * 数据库保存数据
     * @param dbUtils
     * @param list
     * @param c
     * @param isDeleteAll
     * @return
     */
    public static <T>AsyncTask saveListBean(DbUtils dbUtils,List<T> list,Class c,boolean isDeleteAll){
        SaveAsyncTask<T> task = new SaveAsyncTask(dbUtils, c, isDeleteAll);
        task.execute(list);
        return task;
    }

    public static AsyncTask findListBean(DbUtils dbUtils,Class c, String id,PostObt postObt,ProgressDialog dialog){
        FindAsyncTask task = new FindAsyncTask(dbUtils,id,postObt,dialog);
        task.execute(c);
        return task;
    }

    public static AsyncTask requestGet(DataBack back,String url,boolean isGet){
        return requestPost(null,back,isGet,url);
    }

    public static AsyncTask requestPost(Map<String, Object> map,DataBack back,boolean isGet, String url){
        return new MyLoginAsynchTask(map,back,isGet).execute(url);
    }
}
