package com.funnco.funnco.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.ScheduleNewStat;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.MyLoginAsynchTask;
import com.funnco.funnco.utils.date.DateUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.date.TimeUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/6/30.
 */
public class ScheduleDataStatService extends Service {
    private DbUtils dbUtils;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbUtils = BaseApplication.getInstance().getDbUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String dates = DateUtils.getCurrentDate();
        downScheduleNew(TimeUtils.getNextDate(dates));
        downScheduleNew(dates);
        downScheduleNew(TimeUtils.getPreDate(dates));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void downScheduleNew(String dates) {
        final ArrayList<ScheduleNewStat> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("dates", dates);
        MyLoginAsynchTask task = new MyLoginAsynchTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                LogUtils.e("服务 下载的 月的数据列表是：", "" + result);
                JSONArray paramsJSONObject = JsonUtils.getJAry(result, "params");
                try {
                    if (paramsJSONObject == null){
                        return;
                    }
                    for (int i = 0; i <paramsJSONObject.length();i ++){
                        String value = (String)paramsJSONObject.get(i);
                        ScheduleNewStat stat = new ScheduleNewStat();
                        stat.setDate(value);
                        list.add(stat);
//                        scheduleNewList.put(value,value);
                    }
                    if (dbUtils != null){
                        dbUtils.saveAll(list);
                        sendBroadcast(new Intent(FunncoUrls.getScheduleNew()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        },false);
        task.execute(FunncoUrls.getScheduleNew());
    }
}
