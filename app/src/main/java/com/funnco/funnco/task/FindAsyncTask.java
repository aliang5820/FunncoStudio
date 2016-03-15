package com.funnco.funnco.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.funnco.funnco.impl.PostObt;
import com.funnco.funnco.utils.string.TextUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
//import com.umeng.message.proguard.T;

import java.util.List;

/**
 * Created by user on 2015/7/10.
 */
public class FindAsyncTask<T> extends AsyncTask<Class,Integer,List<T>> {

    private DbUtils dbUtils;
    private String id;
    private PostObt postObt;
    private ProgressDialog dialog;

    public FindAsyncTask(DbUtils dbUtils,String id,PostObt postObt,ProgressDialog dialog){
        this.dbUtils = dbUtils;
        this.id = id;
        this.postObt = postObt;
        this.dialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
    }

    @Override
    protected List<T> doInBackground(Class... params) {
        Class c = params[0];
        List<T> list = null;
        try {
            if (dbUtils != null && c != null && dbUtils.tableIsExist(c)){
                if (TextUtils.isNull(id)){
                    try {
                        list = dbUtils.findAll(c);
                    } catch (DbException e) {
                        e.printStackTrace();
                        return null;
                    }
                }else{
                    try {
                        list = (List<T>) dbUtils.findById(c,id);
                    } catch (DbException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<T> list) {
        super.onPostExecute(list);
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        if (postObt != null){
            postObt.postObt(list);
        }
    }
}
