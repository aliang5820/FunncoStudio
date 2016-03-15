package com.funnco.funnco.utils.support;

import android.app.ProgressDialog;

import com.funnco.funnco.bean.MyCustomerD;
import com.funnco.funnco.bean.MyCustomerDate;
import com.funnco.funnco.fragment.MyCustomerFragment;
import com.funnco.funnco.task.ReParaserMyCustomerDAsynchTask;
import com.funnco.funnco.task.ReParaserMyCustomerDateAsynchTask;

import java.util.List;

/**
 * Created by user on 2015/7/10.
 */
public class ParaserCollectionUtils {
    private static String format_1 = "yyyy年MM月dd日";
    private static String format_2 = "yyyy-MM-dd";
    private static String format_3 = "yyyyMMdd";
    private static String format_4 = "yyyy/MM/dd";
    private static String format_5 = "yyyy-MM";

    /**
     * 对我的预约客户进行解析 ---- MyCustomer
     * @param isFuture
     * @param format
     * @param postList
     * @param list
     * @param dialog
     * @return 返回该异步任务对象
     */
    public static ReParaserMyCustomerDAsynchTask paraserMyCustomerD(boolean isFuture,String format,MyCustomerFragment.PostList postList ,List<MyCustomerD> list,ProgressDialog dialog){
        ReParaserMyCustomerDAsynchTask task = new ReParaserMyCustomerDAsynchTask(isFuture,format,postList,dialog);
        task.execute(list);
        return task;
    }

    /**
     * 联系人  时间排序
     * @param format
     * @param postList
     * @param list
     * @param dialog
     * @return
     */
    public static ReParaserMyCustomerDateAsynchTask paraserMyCustomerDate(String format,MyCustomerFragment.PostList postList ,List<MyCustomerDate> list,ProgressDialog dialog){
        ReParaserMyCustomerDateAsynchTask task = new ReParaserMyCustomerDateAsynchTask(format,postList,dialog);
        task.execute(list);
        return task;
    }

    /**
     * 对我的预约客户进行解析 ---- MyCustomer 默认解析格式是----yyyy年MM月dd日
     * @param isFuture
     * @param postList
     * @param list
     * @param dialog
     * @return 返回该异步任务对象
     */
    public static ReParaserMyCustomerDAsynchTask paraserMyCustomerD(boolean isFuture,MyCustomerFragment.PostList postList,List<MyCustomerD> list,ProgressDialog dialog){
        return paraserMyCustomerD(isFuture,format_1,postList,list,dialog);
    }

    /**
     * 对我的预约客户进行解析 ---- MyCustomer 默认解析格式是----yyyy年MM月dd日 并且没有进度Dialog
     * @param isFuture
     * @param postList
     * @param list
     * @return 返回该异步任务对象
     */
    public static ReParaserMyCustomerDAsynchTask paraserMyCustomerD(boolean isFuture,MyCustomerFragment.PostList postList,List<MyCustomerD> list){
        return paraserMyCustomerD(isFuture,postList,list,null);
    }
}
