package com.funnco.funnco.activity.myinfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.PingppLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 账号升级
 * Created by user on 2015/11/10.
 */
public class UpdateAccountActivity_3 extends BaseActivity {
    private View parentView, tip_more_layout;
    private CheckBox alipay, wxpay;
    private TextView order_name, order_admin_num, order_member_num, order_amount, tip_btn;
    private int amount = 1;//测试1分钱
    private int adminNum = 0, memberNum = 0;
    private boolean isOpenTip = false;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CHANNEL_WECHAT = "wx";//微信支付渠道
    private static final String CHANNEL_ALIPAY = "alipay";//支付宝支付渠道

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*//设置需要使用的支付方式,true:显示该支付通道，默认为false
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
        PingppOnePayment.CONTENT_TYPE = "application/json";*/
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
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_money_pay);
        wxpay = (CheckBox) findViewById(R.id.wechatButton);
        alipay = (CheckBox) findViewById(R.id.alipayButton);
        order_name = (TextView) findViewById(R.id.order_name);
        order_admin_num = (TextView) findViewById(R.id.order_admin_num);
        order_member_num = (TextView) findViewById(R.id.order_member_num);
        order_amount = (TextView) findViewById(R.id.order_amount);
        tip_more_layout = findViewById(R.id.tip_more_layout);
        tip_btn = (TextView) findViewById(R.id.tip_btn);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        wxpay.setOnClickListener(this);
        alipay.setOnClickListener(this);
        tip_btn.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.alipayButton:
                alipay.setChecked(true);
                wxpay.setChecked(false);
                break;
            case R.id.wechatButton:
                alipay.setChecked(false);
                wxpay.setChecked(true);
                break;
            case R.id.tip_btn:
                if(isOpenTip) {
                    tip_more_layout.setVisibility(View.GONE);
                    tip_btn.setText(R.string.pay_tip_more_btn_big);
                    isOpenTip = false;
                } else {
                    tip_more_layout.setVisibility(View.VISIBLE);
                    tip_btn.setText(R.string.pay_tip_more_btn_small);
                    isOpenTip = true;
                }
                break;
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

    /**
     * 获取订单数据
     *
     * @param paymentRequest
     */
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
                    Intent intent = new Intent(mContext, PaymentActivity.class);
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

    //进行支付
    public void onPay(View view) {
        if (alipay.isChecked()) {
            postJson(new PaymentRequest("支付宝支付测试", "支付测试描述", CHANNEL_ALIPAY, amount));
        } else if (wxpay.isChecked()) {
            postJson(new PaymentRequest("微信支付测试", "支付测试描述", CHANNEL_WECHAT, amount));
        } else {
            showToast("请选择支付方式");
        }
    }

    public void onJiaAdmin(View view) {
        //管理人员数+1
        adminNum += 1;
        order_admin_num.setText("" + adminNum);
        changePayAmount();
    }

    public void onJianAdmin(View view) {
        //管理人员数-1
        if(adminNum > 0) {
            adminNum -= 1;
            order_admin_num.setText("" + adminNum);
            changePayAmount();
        }
    }

    public void onJiaMember(View view) {
        //成员数+1
        memberNum += 1;
        order_member_num.setText("" + memberNum);
        changePayAmount();
    }

    public void onJianMember(View view) {
        //成员数-1
        if(memberNum > 0) {
            memberNum -= 1;
            order_member_num.setText("" + memberNum);
            changePayAmount();
        }
    }

    //自动计算总额
    private void changePayAmount() {
        amount = adminNum * 10 + memberNum * 8;
        order_amount.setText(getString(R.string.pay_amount, amount));
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
}