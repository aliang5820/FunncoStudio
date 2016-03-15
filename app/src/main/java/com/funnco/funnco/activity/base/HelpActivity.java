package com.funnco.funnco.activity.base;

import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.utils.url.FunncoUrls;

/**
 * 使用帮助
 * Created by user on 2015/9/7.
 */
public class HelpActivity extends BaseActivity {

    private View parentView;
    private WebView webView;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_about, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_help_how2use);
        webView = (WebView) findViewById(R.id.wv_about_info);
        webView.getSettings().setJavaScriptEnabled(true);//支持javascript
        webView.setWebChromeClient(new WebChromeClient());//支持特殊的javascript
        webView.setWebViewClient(new WebViewClient());//点击链接在本webview中打开
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        //自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.loadUrl(FunncoUrls.getHelpUrl());
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.BUTTON_BACK:
                try {
                    webView.goBack();
                } catch (Exception e) {
                    finishOk();
                }
                break;
        }
        return true;
    }
}
