package com.funnco.funnco.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.BucketChooseAdatper;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.ImageBucket;
import com.funnco.funnco.utils.file.AlbumHelper;

import java.util.List;

/**
 * Created by user on 2015/6/1.
 */
public class BucketChooseActivity extends BaseActivity {


    //头部
    private TextView tvBack;
    private TextView tvTitle;
    private TextView tvCancle;

    private ListView listView;

    private AlbumHelper helper;
    //所有相册的集合
    private List<ImageBucket> contentList;
    private BucketChooseAdatper bucketChooseAdatper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_bucketchoose);

    }

    @Override
    protected void initView() {
        tvBack = (TextView) findViewById(R.id.tv_headcommon_headl);
        tvBack.setVisibility(View.GONE);
        tvTitle = (TextView) findViewById(R.id.tv_headcommon_headm);
        tvTitle.setText(getResources().getString(R.string.imagebucket));
        tvCancle = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvCancle.setText(getResources().getString(R.string.cancle));


        listView = (ListView) findViewById(R.id.lv_bucketchoose_list);
        helper = AlbumHelper.getHelper();
        helper.init(BaseApplication.getInstance());
        contentList = helper.getImagesBucketList(false);

        bucketChooseAdatper = new BucketChooseAdatper(mContext, contentList);
        listView.setAdapter(bucketChooseAdatper);
    }

    @Override
    protected void initEvents() {
        tvCancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headr:
                finishOk();
                break;
        }
    }
}
