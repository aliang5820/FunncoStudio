package com.funnco.funnco.fragment.desk;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.service.AddCoursesActivity;
import com.funnco.funnco.activity.service.AddServiceActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.fragment.desk.service.ServiceFragment_Course;
import com.funnco.funnco.fragment.desk.service.ServiceFragment_Service;
import com.funnco.funnco.support.FragmentTabHost;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.log.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by 2015-05-13
 */
public class ServiceFragment extends BaseFragment implements View.OnClickListener{

    //滚动的line
    private LinearLayout lScrollLayout;
    private ImageView ivLine;
    int currIndex = 0;// 当前页卡编号
    int bmpW;// 图片宽度
    int offset;// 动画图片偏移量

    private PopupWindow pwAddnotify;
    private View vAddnotify;

//    private CircleImageView civTeamChoose;
    private ImageView ivAdd;
    private FragmentTabHost tbh;
    private final Class<?>[] classes = {ServiceFragment_Service.class,ServiceFragment_Course.class};
    private final String[] tag = {"ServiceFragment_Service","ServiceFragment_Course"};
    private final int[] menu_layout = {R.layout.layout_table_service_service
            ,R.layout.layout_table_service_course};
    private Map<String,BaseFragment> map = new HashMap<>();
    private Intent intent;
    private FragmentManager fm;
    private UserLoginInfo user;

    private static final int REQUEST_CODE_COURSES_ADD = 0xf07;
    private static final int RESULT_CODE_COURSES_ADD = 0xf17;
    private static final int REQUEST_CODE_COURSES_EDIT = 0xf09;
    private static final int RESULT_CODE_COURSES_EDIT = 0xf19;
    private static final int REQUEST_CODE_SERVICE_ADD = 0xf201;
    private static final int RESULT_CODE_SERVICE_ADD = 0xf202;
    private static final int REQUEST_CODE_SERVICE_EDIT = 0xf211;
    private static final int RESULT_CODE_SERVICE_EDIT = 0xf212;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                if (fm != null){
                    List<Fragment> ls = fm.getFragments();
                    if (ls != null){
                        for(Fragment fb : ls){
                            if (fb instanceof ServiceFragment_Course){
                                map.put(tag[1], (BaseFragment) fb);
                            }else if(fb instanceof  ServiceFragment_Service){
                                map.put(tag[0], (BaseFragment) fb);
                            }
                        }
                        LogUtils.e("funnco------ServiceFragment","集合大小是："+ls.size());
                    }else{
                        LogUtils.e("funnco------ServiceFragment","集合为空");
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_fragment_service, container, false);
        init();
        initViews();
        initEvents();
        return parentView;
    }
    @Override
    protected void initViews() {
        if (mContext == null) {
            mContext = getActivity();
            mActivity = (MainActivity) mContext;
        }
        user = BaseApplication.getInstance().getUser();
        tbh = (FragmentTabHost) parentView.findViewById(android.R.id.tabhost);


        ivLine = (ImageView) parentView.findViewById(R.id.iv_scroll_line);
        bmpW = FunncoUtils.getScreenWidth(mContext) / 2;
        offset = bmpW;//此处特殊处理
        LogUtils.e("------", "滑动距离是 ：" + offset);
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        ivLine.setImageMatrix(matrix);// 设置动画初始位置

//        civTeamChoose = (CircleImageView) findViewById(R.id.id_lview);
        ivAdd = (ImageView) findViewById(R.id.id_rview);


        fm = getChildFragmentManager();
        tbh.setup(mContext, fm, R.id.realtabcontent);
        for(int i = 0 ;i < classes.length; i ++){
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tab = tbh.newTabSpec(tag[i]).setIndicator(getTabView(i));
            // 将对应Fragment添加到TabHost中
            BaseFragment bf =  (BaseFragment) tbh.addTab(tab, classes[i], null);
            //设置Fragment的背景
            if(bf != null) {
                map.put(tag[i], bf);
            }
            bf = (BaseFragment) fm.findFragmentByTag(tag[i]);
            if(bf != null) {
                map.put(tag[i], bf);
            }
        }
        tbh.getTabWidget().setDividerDrawable(null);
        lScrollLayout = (LinearLayout) parentView.findViewById(R.id.id_scrollview);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lScrollLayout.getLayoutParams();
        params.setMargins(0, FunncoUtils.getViewMesureSpec(tbh.getTabWidget().getChildAt(0))[1], 0, 0);
        lScrollLayout.setLayoutParams(params);

        vAddnotify = LayoutInflater.from(mContext).inflate(R.layout.layout_popupwindow_addservice, null);
        pwAddnotify = new PopupWindow(vAddnotify, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pwAddnotify.setBackgroundDrawable(new BitmapDrawable());
    }


    @Override
    protected void initEvents() {
        ivAdd.setOnClickListener(this);

        tbh.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll(0);
                tbh.setCurrentTab(0);
            }
        });
        tbh.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll(1);
                tbh.setCurrentTab(1);
            }
        });
        vAddnotify.findViewById(R.id.id_tview).setOnClickListener(this);
        vAddnotify.findViewById(R.id.bt_pw_addservice_service).setOnClickListener(this);
        vAddnotify.findViewById(R.id.bt_pw_addservice_courses).setOnClickListener(this);
        findViewById(R.id.id_rview).setOnClickListener(this);
    }

    @Override
    protected void init() {
        intent = new Intent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (map.size() < 2){
                    try {
                        Thread.sleep(1500);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fm != null){
            List<Fragment> ls = fm.getFragments();
            LogUtils.e("funnco------ServiceFragment 生命周期 onResume 阶段 获取的碎片集合是",""+ls.size());
        }
    }
    Bundle bundle = new Bundle();
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_rview://右上角弹出添加提示
                pwAddnotify.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                return;
            case R.id.bt_pw_addservice_service://添加服务
                intent.putExtra("title",getResources().getString(R.string.addservice));
                bundle.putParcelable("serve", null);
                intent.putExtra("data", bundle);
                intent.setClass(getActivity(), AddServiceActivity.class);
                startActivityForResult(intent, 102);
                break;
            case R.id.bt_pw_addservice_courses:
                startActivityForResult(AddCoursesActivity.class, null,REQUEST_CODE_COURSES_ADD);
                break;
        }
        dismissPopupWindow();
    }

    private boolean dismissPopupWindow(){
        boolean flag = false;
        for(PopupWindow pw : new PopupWindow[]{pwAddnotify}){
            if (pw != null && pw.isShowing()){
                pw.dismiss();
                flag = true;
            }
        }
        return flag;
    }

    private View getTabView(int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(menu_layout[position], null);
        return view;
    }
    private void scroll(final int index) {
        Animation animation = new TranslateAnimation(currIndex * bmpW, bmpW
                * index, 0, 0);
        currIndex = index;
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(100);
        ivLine.startAnimation(animation);
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtils.e("------","requestCode : "+Integer.toHexString(requestCode) + "  resultCode : "+Integer.toHexString(resultCode));
        if (resultCode == RESULT_CODE_COURSES_ADD || resultCode == RESULT_CODE_COURSES_EDIT){
            LogUtils.e("------", "刷新课程数据");
            ServiceFragment_Course sfc = (ServiceFragment_Course) map.get(tag[1]);
            if (sfc != null)
                sfc.post(0);
//            for (String key : map.keySet()){
//                BaseFragment bf = map.get(key);
//                if (bf instanceof ServiceFragment_Course){
//                    LogUtils.e("------","刷新课程数据。。。 ");
//                    ((ServiceFragment_Course) bf).post(0);
//                }
//            }
        }else if (resultCode == RESULT_CODE_SERVICE_ADD || resultCode == RESULT_CODE_SERVICE_EDIT){
            LogUtils.e("------","刷新服务数据");
            ServiceFragment_Service sfs = (ServiceFragment_Service) map.get(tag[0]);
            if (sfs != null)
                sfs.post(0);
//            for (String key : map.keySet()){
//                BaseFragment bf = map.get(key);
//                if (bf instanceof ServiceFragment_Service){
//                    LogUtils.e("------","刷新服务数据...");
//                    ((ServiceFragment_Service) bf).post(0);
//                }
//            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 消失Popupwindow
     */
    private boolean dismissPopupwindow() {
        boolean hasPwdismiss = false;
        for (PopupWindow pw : new PopupWindow[]{pwAddnotify}) {
            if (pw != null && pw.isShowing()) {
                pw.dismiss();
                hasPwdismiss = true;
            }
        }
        return hasPwdismiss;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMainAction(String data) {}

    @Override
    public void onMainData(List<?>... list) { }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return dismissPopupwindow();
    }
}
