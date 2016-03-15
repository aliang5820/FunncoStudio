package com.funnco.funnco.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
 * Created by user on 2015/5/18.
 * @author Shawn
 */
public class AboutActivity extends BaseActivity {

    private WebView webView = null;
    private TextView tvTitle = null;
    private TextView tvBack = null;
    private Intent intent = null;
    private String title = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.layout_activity_about);
    }

    @Override
    protected void initView() {
        if (intent != null){
            title = intent.getStringExtra("info");
        }
        webView = (WebView) findViewById(R.id.wv_about_info);
        tvTitle = (TextView) findViewById(R.id.tv_headcommon_headm);
        tvBack = (TextView) findViewById(R.id.tv_headcommon_headl);
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
        webView.getSettings().setJavaScriptEnabled(true);//支持javascript
        webView.setWebChromeClient(new WebChromeClient());//支持特殊的javascript
        webView.setWebViewClient(new WebViewClient());//点击链接在本webview中打开

        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.loadUrl(FunncoUrls.getAboutUrl());
    }

    @Override
    protected void initEvents() {
        tvBack.setOnClickListener(this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.BUTTON_BACK:
                try {
                    webView.goBack();
                }catch (Exception e){
                    AboutActivity.this.finish();
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://标题栏的返回按钮
                finishOk();
                break;
        }
    }
}
