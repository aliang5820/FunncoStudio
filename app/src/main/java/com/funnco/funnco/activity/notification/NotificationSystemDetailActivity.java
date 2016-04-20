package com.funnco.funnco.activity.notification;

import android.view.View;
import android.webkit.WebView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.utils.support.Constants;

/**
 * Created by Edison on 2016/4/20.
 */
public class NotificationSystemDetailActivity extends BaseActivity {
    private WebView webView;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        setContentView(R.layout.activity_layout_notify_system_detail);
    }

    @Override
    protected void initView() {
        webView = (WebView) findViewById(R.id.webView);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
    }

    @Override
    protected void initEvents() {
        String url = getIntent().getStringExtra(Constants.URL);
        webView.loadUrl(url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }
}
