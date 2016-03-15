package com.funnco.funnco.activity;

import android.content.Intent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;

/**
 * 个人资料预览（h5）
 * Created by user on 2015/8/31.
 */
public class PersonalInfoPrefaceActivity extends BaseActivity {
    private WebView wvPersonalinfo;
    private View parentView;
    private UserLoginInfo user;

    private Intent intent;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_about, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        LogUtils.e("----", "PersonalInfoPrefaceActivity 执行了 initView方法");
        intent = getIntent();
        user = BaseApplication.getInstance().getUser();
        wvPersonalinfo = (WebView) findViewById(R.id.wv_about_info);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.business_code);
        String token = SharedPreferencesUtils.getValue(mContext, Constants.TOKEN);
        String url = FunncoUrls.getShareScheduleUrl(user.getId());
        if (!TextUtils.isNull(token)) {
            url += "&token=" + token;
        }
        if (intent != null) {
//            intent.getStringExtra("")
        }

        //支持javascript
        wvPersonalinfo.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        wvPersonalinfo.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
//        wvPersonalinfo.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
//        wvPersonalinfo.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        wvPersonalinfo.getSettings().setUseWideViewPort(true);
        wvPersonalinfo.getSettings().setLoadWithOverviewMode(true);

//        Map<String, String> extraHeaders = new HashMap<>();
//        if (wvPersonalinfo.getUrl() != null) {
//            extraHeaders.put("Referer", wvPersonalinfo.getUrl());
//            wvPersonalinfo.loadUrl(url, extraHeaders);
//        }
        wvPersonalinfo.loadUrl(url);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
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
