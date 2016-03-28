package com.funnco.funnco.activity.myinfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PayResultCallBack;
import com.pingplusplus.libone.PingppOnePayment;

import java.util.HashMap;
import java.util.Map;

/**
 * 账号升级
 * Created by user on 2015/11/10.
 */
public class UpdateAccountActivity_3 extends BaseActivity implements PayResultCallBack {
    private View parentView;
    private static final int REQUEST_CODE_PAYMENT = 1;
    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置需要使用的支付方式,true:显示该支付通道，默认为false
        PingppOnePayment.SHOW_CHANNEL_WECHAT = true;
        PingppOnePayment.SHOW_CHANNEL_UPACP = true;
        PingppOnePayment.SHOW_CHANNEL_BFB = true;
        PingppOnePayment.SHOW_CHANNEL_ALIPAY = true;

        //设置支付通道的排序,最小的排在最前
        PingppOnePayment.CHANNEL_UPACP_INDEX = 1;
        PingppOnePayment.CHANNEL_ALIPAY_INDEX = 2;
        PingppOnePayment.CHANNEL_WECHAT_INDEX = 3;
        PingppOnePayment.CHANNEL_BFB_INDEX = 4;

        //提交数据的格式，默认格式为json
        PingppOnePayment.CONTENT_TYPE = "application/json";
        PingppLog.DEBUG = true;
    }

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_pay_layout, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_money);
        findViewById(R.id.wechatButton).setOnClickListener(this);
        findViewById(R.id.alipayButton).setOnClickListener(this);
        findViewById(R.id.upmpButton).setOnClickListener(this);
        findViewById(R.id.bfbButton).setOnClickListener(this);
        findViewById(R.id.jdpayButton).setOnClickListener(this);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
    }

    @Override
    protected void initEvents() {

    }

    public void onClick(View view) {
        int amount = 1;
        if (view.getId() == R.id.tv_headcommon_headl) {
            finishOk();
        } else if (view.getId() == R.id.upmpButton) {
            postJson(new PaymentRequest("银联支付测试", "支付测试描述", CHANNEL_UPACP, amount));
        } else if (view.getId() == R.id.alipayButton) {
            postJson(new PaymentRequest("支付宝支付测试", "支付测试描述", CHANNEL_ALIPAY, amount));
        } else if (view.getId() == R.id.wechatButton) {
            postJson(new PaymentRequest("微信支付测试", "支付测试描述", CHANNEL_WECHAT, amount));
        } else if (view.getId() == R.id.bfbButton) {
            postJson(new PaymentRequest("百度钱包支付测试", "支付测试描述", CHANNEL_BFB, amount));
        } else if (view.getId() == R.id.jdpayButton) {
            postJson(new PaymentRequest("京东支付测试", "支付测试描述", CHANNEL_JDPAY_WAP, amount));
        } else {
            Toast.makeText(mContext, "你是不是点错了?", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null != msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null != msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private void postJson(PaymentRequest paymentRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.ORDER_TITLE, paymentRequest.title);
        map.put(Constants.ORDER_CONTENT, paymentRequest.content);
        map.put(Constants.ORDER_AMOUNT, paymentRequest.amount);
        map.put(Constants.ORDER_CHANNEL, paymentRequest.channel);
        showLoading(parentView);
        AsyTask task = new AsyTask(map, new DataBack() {
            @Override
            public void getString(String result) {
                dismissLoading();
                if (JsonUtils.getResponseCode(result) == 0) {
                    String charge = JsonUtils.getStringByKey4JOb(result, "params");
                    LogUtils.e(TAG, "获取到charge:" + charge);
                    Intent intent = new Intent(UpdateAccountActivity_3.this, PaymentActivity.class);
                    intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                } else {
                    String msg = JsonUtils.getResponseMsg(result);
                    showSimpleMessageDialog(msg);
                }
            }

            @Override
            public void getBitmap(String rul, Bitmap bitmap) {
                dismissLoading();
            }
        }, true);
        putAsyncTask(task);
        task.execute(FunncoUrls.getOrderUrl());
    }

    class PaymentRequest {
        String title;
        String content;
        String channel;
        int amount;

        public PaymentRequest(String title, String content, String channel, int amount) {
            this.title = title;
            this.content = content;
            this.channel = channel;
            this.amount = amount;
        }
    }

    @Override
    public void getPayResult(Intent data) {
        if (data != null) {
            /**
             * result：支付结果信息
             * code：支付结果码  -2:用户自定义错误、 -1：失败、 0：取消、1：成功
             */
            Toast.makeText(this, data.getExtras().getString("result") + "  " + data.getExtras().getInt("code"), Toast.LENGTH_LONG).show();
        }
    }

}

