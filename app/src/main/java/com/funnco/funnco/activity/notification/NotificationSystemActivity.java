package com.funnco.funnco.activity.notification;

import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.bean.NotificationSystemInfo;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.url.FunncoUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edison on 2016/4/19.
 */
public class NotificationSystemActivity extends NotificationBaseActivity {
    private List<NotificationSystemInfo> list = new ArrayList<>();

    @Override
    protected void initView() {
        super.initView();
        ((TextView) findViewById(R.id.id_mview)).setText("系统通知");
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        if (xListView != null) {
            xListView.stopLoadMore();
            xListView.stopRefresh();
        }

        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
        if (paramsJSONObject != null) {
            JSONArray listJSONArray = JsonUtils.getJAry(paramsJSONObject.toString(), "sysmsg");
            if (listJSONArray != null) {
                List<NotificationSystemInfo> ls = JsonUtils.getObjectArray(listJSONArray.toString(), NotificationSystemInfo.class);
                if (ls != null && ls.size() > 0) {
                    list.clear();
                    list.addAll(ls);
                } else {
                    list.clear();
                }
                adapter.notifyDataSetChanged();
            }
        }
        LogUtils.e(TAG, result);
    }

    //获取数据
    @Override
    protected void getData() {
        showLoading(parentView);
        postData2(null, FunncoUrls.getMessageSystemUrl(), true);
    }

    @Override
    public void initAdapter() {
        adapter = new CommonAdapter<NotificationSystemInfo>(mContext, list, R.layout.layout_notification_remind_item) {
            @Override
            public void convert(ViewHolder helper, NotificationSystemInfo item, int position) {
                TextView time = helper.getView(R.id.time);
                TextView content = helper.getView(R.id.content);
                time.setText(item.getCreate_time());
                content.setText(item.getContent());
            }
        };
        xListView.setAdapter(adapter);
    }
}
