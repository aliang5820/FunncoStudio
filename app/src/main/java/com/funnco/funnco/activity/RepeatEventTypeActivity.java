package com.funnco.funnco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;

/**
 * Created by user on 2015/5/24.
 * @author Shawn
 */
public class RepeatEventTypeActivity extends BaseActivity implements View.OnClickListener{

    private String[] repeattylelist = null;
    private ArrayAdapter<String> adapter = null;
    private ListView listView = null;
    private Intent intent = null;
    private TextView tvTitle = null;
    private TextView tvBack = null;
    private static final int RESULT_CODE_SCHEDULE_REPEADTYPE = 0xff15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.layout_activity_repeattype);
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.lv_repead_repead);
        tvTitle = (TextView) findViewById(R.id.tv_headcommon_headm);
        tvBack = (TextView) findViewById(R.id.tv_headcommon_headl);
        tvTitle.setText(getResources().getString(R.string.event_repeat));
        repeattylelist = getResources().getStringArray(R.array.array_type_repeat);
        adapter = new ArrayAdapter<String>(this,R.layout.layout_item_repeat, R.id.tv_item_repeat_repeattype,repeattylelist);
        listView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("repeat_type",position+"");
                intent.putExtra("repeat_name",repeattylelist[position]);
                setResult(RESULT_CODE_SCHEDULE_REPEADTYPE, intent);
                RepeatEventTypeActivity.this.finish();
            }
        });
        tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }
}
