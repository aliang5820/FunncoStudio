package com.funnco.funnco.activity.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.AlbumGridViewAdapter;
import com.funnco.funnco.adapter.BucketChooseAdatper;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.ImageBucket;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.file.AlbumHelper;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.PublicWay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/5/27.
 * @author Shawn
 * @QQ 1206816341
 */
public class AddWorkActivity extends BaseActivity {

    //返回---进入相册的按钮
    private TextView tvBack;//相册按钮
    private TextView tvTitle;//标题
    private TextView tvCancle;//取消按钮
    private Button btComplete;//完成按钮
    private LinearLayout lContainer;//预览框
    private GridView gridView;//显示手机所有图片
    private Button btBucketChoose;//选择相册

    private AlbumHelper helper;
    private AlbumGridViewAdapter gridImageAdapter;
    //所有相册的集合
    public static List<ImageBucket> contentList;
    //所有相册中图片
    public static ArrayList<ImageItem> dataList = new ArrayList<>();

    private Intent intent;
    private int number = 0;
    private boolean isTeamWork = false;
    private String team_id;

    private PopupWindow pwBucketChoose;
    private ListView bkListView;
    private LinearLayout ll_popup;
    private BucketChooseAdatper bucketChooseAdatper;//相册适配器
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_addwork, null);
        setContentView(parentView);

    }

    //用于刷新的广播
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //mContext.unregisterReceiver(this);
            gridImageAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null){
            number = intent.getIntExtra("number",0);
            if (number != 0){
                PublicWay.num = number;
            }
            isTeamWork = intent.getBooleanExtra("isTeamWork",false);
            if (isTeamWork){
                team_id = intent.getStringExtra("team_id");
            }
            LogUtils.e("funnco------","isTeamWork:"+isTeamWork+" team_id:"+team_id);
        }
        //初始化帮助类，获取信息
        helper = AlbumHelper.getHelper();
        helper.init(BaseApplication.getInstance());

        pwBucketChoose = new PopupWindow(mContext);
        View view = getLayoutInflater().inflate(R.layout.layout_popupwindwo_bucketchoose, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.llayout_popupwindow);
        bkListView = (ListView) view.findViewById(R.id.lv_popupwindow_bucketlist);


        pwBucketChoose.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pwBucketChoose.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwBucketChoose.setBackgroundDrawable(new BitmapDrawable());
        pwBucketChoose.setFocusable(true);
        pwBucketChoose.setOutsideTouchable(true);
        pwBucketChoose.setContentView(view);

        bucketChooseAdatper = new BucketChooseAdatper(mContext,helper.getImagesBucketList(false));
        bkListView.setAdapter(bucketChooseAdatper);
        bucketChooseAdatper.setPost(new Post() {
            @Override
            public void post(int ...position) {
                pwBucketChoose.dismiss();
                ll_popup.clearAnimation();
                if (position[0]>=0){
                    dataList.clear();
                    dataList.addAll(Bimp.tempCheckedBucketBitmap);
                    gridImageAdapter.notifyDataSetChanged();
                }
            }
        });

        //从初始化后的帮助类中获取
        if (Bimp.tempSelectBitmap != null){
            Bimp.tempSelectBitmap.clear();
        }
        contentList = helper.getImagesBucketList(false);
            //获取所有相册中图片
        for (int i = 0; i < contentList.size(); i++) {
            dataList.addAll(contentList.get(i).imageList);
        }
        gridView = (GridView) findViewById(R.id.gv_addwork_imagelist);
        tvBack = (TextView) findViewById(R.id.tv_headcommon_headl);
        tvCancle = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvTitle = (TextView) findViewById(R.id.tv_headcommon_headm);
        btComplete = (Button) findViewById(R.id.bt_addwork_complete);
        btBucketChoose = (Button) findViewById(R.id.bt_addwork_bucketchoose);

        tvTitle.setText(getResources().getString(R.string.album));
        tvCancle.setText(getResources().getString(R.string.cancle));

        //初始适配器
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap, new Post() {
            @Override
            public void post(int ...position) {
                //接受ImageView 传递回来的position
                Intent intent = new Intent(AddWorkActivity.this, Gallery2Activity.class);
                intent.putExtra("position",position[0]);
                //把当前的图片集合通过Application 传递过去---dataList
                BaseApplication.getInstance().setT("dataList",dataList);
                startActivity(intent);
            }
        });
        gridView.setAdapter(gridImageAdapter);
        //设置完成按钮的值
        btComplete.setText(getResources().getString(R.string.complete)+Bimp.tempSelectBitmap.size()+"/"+ PublicWay.num);
    }

    @Override
    protected void initEvents() {
        tvCancle.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        btComplete.setOnClickListener(this);
        btBucketChoose.setOnClickListener(this);

        gridImageAdapter.setOnItemCheckeBoxClickListener(new AlbumGridViewAdapter.OnItemCheckeBoxClickListener() {
            @Override
            public void onCheckedClick(ToggleButton view, int position, boolean isChecked, CheckBox chooseBx) {
                if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
//                    view.setChecked(false);
                    chooseBx.setChecked(false);
//                    chooseBt.setVisibility(View.GONE);
                    if (!removeOneData(dataList.get(position))) {
                        showSimpleMessageDialog(R.string.only_choose_num);
//                        Toast.makeText(AddWorkActivity.this, getResources().getString(R.string.only_choose_num), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                ImageItem item = dataList.get(position);
                LogUtils.e("------", "点击了的图片的地址是：" + item.getImagePath() + "  alumPath :" + item.getThumbnailPath());
                if (isChecked) {
//                    view.setVisibility(View.VISIBLE);
//                    view.setChecked(true);
//                    view.setEnabled(false);
                    if (!Bimp.tempSelectBitmap.contains(item)) {
                        Bimp.tempSelectBitmap.add(item);
                        btComplete.setText(getResources().getString(R.string.complete) + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num);
                    }
                } else {
//                    view.setVisibility(View.GONE);
                    Bimp.tempSelectBitmap.remove(item);
//                    chooseBt.setVisibility(View.GONE);
                    btComplete.setText(getResources().getString(R.string.complete) + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num);
                }
//                view.setChecked(isChecked);
                isShowOkBt();
            }
        });
    }
    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            btComplete.setText(getResources().getString(R.string.complete) + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num);
            return true;
        }
        return false;
    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            btComplete.setText(getResources().getString(R.string.complete) + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num);
            btComplete.setPressed(true);
            btComplete.setClickable(true);
        } else {
            btComplete.setText(getResources().getString(R.string.complete) + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num);
            btComplete.setPressed(false);
            btComplete.setClickable(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                //取消
                clearTem();
                finishOk();
                break;
            case R.id.tv_headcommon_headr://取消进行结束
                clearTem();
                finishOk();
                break;

            case R.id.bt_addwork_complete://完成 进行编辑标题和描述
                Intent i = new Intent();
                i.setClass(mContext, EditWorkActivity.class);
                i.putExtra("isTeamWork", isTeamWork);
                if (isTeamWork) {
                    i.putExtra("team_id", team_id);
                }
                startActivity(i);
                finishOk();
                break;

            case R.id.bt_addwork_bucketchoose://选择相册
                ll_popup.startAnimation(AnimationUtils.loadAnimation(AddWorkActivity.this, R.anim.activity_translate_in));
                pwBucketChoose.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
        }

    }
    private void clearTem(){
        if (Bimp.tempSelectBitmap!=null)
            Bimp.tempSelectBitmap.clear();
    }
    protected void finishOk(){
        dataList.clear();
        super.finishOk();
    }
}
