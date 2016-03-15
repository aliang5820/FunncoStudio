package com.funnco.funnco.task;

import android.os.AsyncTask;


import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

/**
 * Created by user on 2015/7/10.
 */
public class SaveAsyncTask<T> extends AsyncTask<List<T>, Integer,Boolean> {
    private DbUtils dbUtils;//数据库操作工具类
    private boolean isDeleteAll;//保存前是否删除之前数据
    private Class<T> t;//所要操作的实体类
    public SaveAsyncTask(DbUtils dbUtils,Class<T> t,boolean isDeleteAll){
        this.dbUtils = dbUtils;
        this.t = t;
        this.isDeleteAll = isDeleteAll;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(List<T>... params) {
        List<T> list = params[0];
        if (dbUtils != null && t != null){
            if (isDeleteAll){
                try {
                    dbUtils.deleteAll(t);
                } catch (DbException e) {
                    e.printStackTrace();
                    return false;
                }
                try {
                    dbUtils.saveOrUpdateAll(list);
                } catch (DbException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
