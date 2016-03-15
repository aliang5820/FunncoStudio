package com.funnco.funnco.task;

import android.os.AsyncTask;

import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.http.VolleyUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by user on 2015/7/14.
 */
public class ClearDateAsyncTask extends AsyncTask<Class[],Integer,Boolean> {
    private DbUtils dbUtils;
    private Post post;
    private VolleyUtils volleyUtils;
    private ImageLoader imageLoader;

    public ClearDateAsyncTask(DbUtils dbUtils,VolleyUtils volleyUtils,ImageLoader imageLoader,Post post){
        this.dbUtils = dbUtils;
        this.post = post;
        this.volleyUtils = volleyUtils;
        this.imageLoader = imageLoader;
    }

    @Override
    protected Boolean doInBackground(Class[]... params) {
        Class[] c = params[0];
        if (volleyUtils != null){
            volleyUtils.clearCache();
        }
        if (imageLoader != null){
            imageLoader.clearDiscCache();
            imageLoader.clearMemoryCache();
        }
        for (Class cc : c){
            if (dbUtils != null) {
                try {
                    if (dbUtils.tableIsExist(cc))
                        SQliteAsynchTask.deleteAll(dbUtils,cc);
//                        SQliteAsynchTask.dropTable(dbUtils, cc);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (post != null)
            post.post(aBoolean ? 0 : -1);
    }
}
