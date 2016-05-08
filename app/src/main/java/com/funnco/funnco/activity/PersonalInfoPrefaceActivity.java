package com.funnco.funnco.activity;

import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;

/**
 * 个人资料预览（h5）
 * Created by user on 2015/8/31.
 */
public class PersonalInfoPrefaceActivity extends BaseActivity {
    private WebView webView;
    private View parentView;
    private UserLoginInfo user;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_about, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        user = BaseApplication.getInstance().getUser();
        webView = (WebView) findViewById(R.id.wv_about_info);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.business_code);
        String token = SharedPreferencesUtils.getValue(mContext, Constants.TOKEN);
        String url = FunncoUrls.getShareScheduleUrl(user.getId());
        if (!TextUtils.isNull(token)) {
            url += "&token=" + token;
        }
        webView.getSettings().setJavaScriptEnabled(true);//支持javascript
        webView.setWebChromeClient(new WebChromeClient());//支持特殊的javascript
        webView.setWebViewClient(new WebViewClient());//点击链接在本webview中打开
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl(url);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.BUTTON_BACK:
                try {
                    webView.goBack();
                }catch (Exception e){
                    finishOk();
                }
                break;
        }
        return true;
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
