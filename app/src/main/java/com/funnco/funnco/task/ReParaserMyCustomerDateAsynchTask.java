package com.funnco.funnco.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.funnco.funnco.bean.MyCustomerDate;
import com.funnco.funnco.fragment.MyCustomerFragment;
import com.funnco.funnco.utils.log.LogUtils;
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
public class ReParaserMyCustomerDateAsynchTask extends AsyncTask<List<MyCustomerDate>,Integer,List<MyCustomerDate>> {

    private boolean isFuture = true;//是否是未来预约
    private String format;//date 数据格式
    private MyCustomerFragment.PostList postList;//接口回调对象
    private ProgressDialog dialog;//旋转的Dialog

    public ReParaserMyCustomerDateAsynchTask(){}

    public ReParaserMyCustomerDateAsynchTask(String format, MyCustomerFragment.PostList postList, ProgressDialog dialog){
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
    protected List<MyCustomerDate> doInBackground(List<MyCustomerDate>... params) {
        List<MyCustomerDate> src = params[0];
        List<MyCustomerDate> dest  =   new ArrayList(Arrays.asList(new MyCustomerDate[src.size()]));
        List<MyCustomerDate> back = new CopyOnWriteArrayList<>();
//        List<MyCustomerD> dest  =   new CopyOnWriteArrayList<>();
        Collections.copy(dest,src);
        src = null;
//        List<MyCustomerD> list2 = Collections.copy
        if (dest == null){
            return null;
        }
        Iterator<MyCustomerDate> it = dest.iterator();
        while (it.hasNext()){
            MyCustomerDate myCustomer = it.next();
            String strDate = TimeUtils.timePast(myCustomer.getDate(), format);
            myCustomer.setTip(strDate);
//            if (isFuture && !DateUtils.isHistory(myCustomer.getDate(), format)){
            back.add(myCustomer);
//            }else if (!isFuture && DateUtils.isHistory(myCustomer.getDate(),format)){
//                back.add(myCustomer);
//            }
        }
        try {
            //睡眠0.5秒  是用户体验更好！！
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.e("----","数据库获得的数据集合chonggouhou的大小是："+back.size());
        return back;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<MyCustomerDate> list) {
        super.onPostExecute(list);
        if (dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
        if (postList != null){
            postList.postList(list);
        }
    }
}
