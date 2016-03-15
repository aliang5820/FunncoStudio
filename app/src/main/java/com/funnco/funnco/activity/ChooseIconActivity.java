package com.funnco.funnco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.CommonAdapter;
import com.funnco.funnco.adapter.ViewHolder;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.log.LogUtils;

import java.util.ArrayList;

/**
 * Created by user on 2015/5/27.
 * @author Shawn
 * @QQ 1206816341
 */
public class ChooseIconActivity extends BaseActivity {

    private TextView tvBack;//返回
    private TextView tvTitle;//标题
    private TextView tvBucket;//相册
    private GridView gridView;//显示手机所有图片

    //所有图片的集合
    public static ArrayList<ImageItem> dataList;

    private Intent intent;
    private CommonAdapter<ImageItem> adapter;
    private View parentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_chooseicon, null);
        setContentView(parentView);

    }
    @Override
    protected void initView() {
        intent = new Intent();
        dataList = new ArrayList<>();
        dataList.addAll(BaseApplication.imageBucket.imageList);
        BaseApplication.imageBucket = null;

        gridView = (GridView) findViewById(R.id.gv_addwork_imagelist);
        adapter = new CommonAdapter<ImageItem>(mContext, dataList,R.layout.layout_item_chooseicon) {
            @Override
            public void convert(ViewHolder helper, ImageItem item, int position) {
                helper.setImageBitmap(R.id.iv_item_chooseicon_icon, item.getBitmap());
            }
        };
        adapter.isTag(true, new int[]{R.id.iv_item_chooseicon_icon});
        gridView.setAdapter(adapter);

        tvBack = (TextView) findViewById(R.id.tv_headcommon_headl);
        tvTitle = (TextView) findViewById(R.id.tv_headcommon_headm);

        tvTitle.setText(getResources().getString(R.string.choose_icon));
    }

    @Override
    protected void initEvents() {
        tvBack.setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e("点击的itemt的tag:", "......" + view.findViewById(R.id.iv_item_chooseicon_icon).getTag() + ";position---"+position);
                intent.putExtra("position",position);
                intent.putExtra("path", dataList.get(position).getImagePath());
                setResult(RESULT_OK,intent);
                finishOk();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                //取消的时候将临时保存的图片集合清除
                Bimp.tempSelectBitmap.clear();
                finishOk();
                break;
            case R.id.tv_headcommon_headr://取消进行结束
                //取消的时候将临时保存的图片集合清除
                Bimp.tempSelectBitmap.clear();
                finishOk();
                break;
        }
    }
}
