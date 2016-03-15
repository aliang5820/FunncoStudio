package com.funnco.funnco.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.fragment.MyCustomerFragment;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.date.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 对我的预约客户进行重新解析
 * Created by user on 2015/7/9.
 */
public class ReParaserMyCustomerDAsynchTask extends AsyncTask<List<MyCustomerD>,Integer,List<MyCustomerD>> {

    private boolean isFuture = true;//是否是未来预约
    private String format;//date 数据格式
    private MyCustomerFragment.PostList postList;//接口回调对象
    private ProgressDialog dialog;//旋转的Dialog

    public ReParaserMyCustomerDAsynchTask(){}

    public ReParaserMyCustomerDAsynchTask(boolean isFuture,String format,MyCustomerFragment.PostList postList,ProgressDialog dialog){
        this.isFuture = isFuture;
        this.format = format;
        this.postList = postList;
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
    protected List<MyCustomerD> doInBackground(List<MyCustomerD> ... params) {
        List<MyCustomerD> src = params[0];
        List<MyCustomerD> dest  =   new ArrayList(Arrays.asList(new MyCustomerD[src.size()]));
        List<MyCustomerD> back = new CopyOnWriteArrayList<>();
//        List<MyCustomerD> dest  =   new CopyOnWriteArrayList<>();
        Collections.copy(dest,src);
        src = null;
//        List<MyCustomerD> list2 = Collections.copy
        if (dest == null){
            return null;
        }
        Iterator<MyCustomerD> it = dest.iterator();
        while (it.hasNext()){
            MyCustomerD myCustomerD = it.next();
            String strDate = TimeUtils.timePast(myCustomerD.getDate(), format);
            myCustomerD.setTip(strDate);
            if (isFuture && !DateUtils.isHistory(myCustomerD.getDate(), format)){
                back.add(myCustomerD);
            }else if (!isFuture && DateUtils.isHistory(myCustomerD.getDate(),format)){
                back.add(myCustomerD);
            }
        }
        try {
            //睡眠0.5秒  是用户体验更好！！
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return back;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<MyCustomerD> list) {
        super.onPostExecute(list);
        if (dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
        if (postList != null){
            postList.postList(list);
        }
    }
}
