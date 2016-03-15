package com.funnco.funnco.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;

/**
 * 提前提醒时间
 * Created by user on 2015/9/21.
 */
public class AheadTimeChooseActivity extends BaseActivity {

    private ListView listView;
    private View parentView;
    private static final int RESULT_CODE_AHEADTIME = 0xf611;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_aheadtimechoose, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.lv_aheadtimechoose_listview);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_service_addcourses);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast("选中的是："+(position));
                Intent intent = new Intent();
                String remind_time = "15";
                switch (position){
                    case 0:
                        remind_time = "15";
                        break;
                    case 1:
                        remind_time = "30";
                        break;
                    case 2:
                        remind_time = "60";
                        break;
                    case 3:
                        remind_time = "120";
                        break;
                }
                intent.putExtra("remind_time",remind_time);
                intent.putExtra("index",position);
                setResult(RESULT_CODE_AHEADTIME,intent);
                finishOk();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }
}
