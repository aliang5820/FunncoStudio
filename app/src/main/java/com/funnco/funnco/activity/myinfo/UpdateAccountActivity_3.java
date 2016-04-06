package com.funnco.funnco.activity.myinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
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
    private TextView order_name, order_time_num, order_admin_num, order_member_num, order_amount, tip_btn, pay_time_select;
    private int amount = 1;
    private boolean isOpenTip = false;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private PaymentRequest paymentRequest;
    private static final String CHANNEL_WECHAT = "wx";//微信支付渠道
    private static final String CHANNEL_ALIPAY = "alipay";//支付宝支付渠道

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        order_time_num = (TextView) findViewById(R.id.order_time_num);
        order_admin_num = (TextView) findViewById(R.id.order_admin_num);
        order_member_num = (TextView) findViewById(R.id.order_member_num);
        order_amount = (TextView) findViewById(R.id.order_amount);
        pay_time_select = (TextView) findViewById(R.id.pay_time_select);
        tip_more_layout = findViewById(R.id.tip_more_layout);
        tip_btn = (TextView) findViewById(R.id.tip_btn);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        wxpay.setOnClickListener(this);
        alipay.setOnClickListener(this);
        tip_btn.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {
        paymentRequest = new PaymentRequest();
        int type = getIntent().getIntExtra(Constants.ORDER_TYPE, -1);
        switch (type) {
            case R.id.swOrderMonth:
                //商务包月
                order_name.setText("商务版包月");
                pay_time_select.setText(R.string.pay_time_month);
                paymentRequest.setBaseAmount(88);
                paymentRequest.setType(Constants.ORDER_TYPE_SW_MONTH);
                break;
            case R.id.swOrderYear:
                //商务包年
                order_name.setText("商务版包年");
                pay_time_select.setText(R.string.pay_time_year);
                paymentRequest.setBaseAmount(888);
                paymentRequest.setType(Constants.ORDER_TYPE_SW_YEAR);
                break;
            case R.id.qjOrderMonth:
                //旗舰包月
                order_name.setText("旗舰版包月");
                pay_time_select.setText(R.string.pay_time_month);
                paymentRequest.setBaseAmount(88);
                paymentRequest.setType(Constants.ORDER_TYPE_QJ_MONTH);
                break;
            case R.id.qjOrderYear:
                //旗舰包年
                order_name.setText("旗舰版包年");
                pay_time_select.setText(R.string.pay_time_year);
                paymentRequest.setBaseAmount(1388);
                paymentRequest.setType(Constants.ORDER_TYPE_QJ_YEAR);
                break;
            case R.id.glOrderAdmin:
                //增加管理员
                order_name.setText("人员管理");
                findViewById(R.id.layout_months).setVisibility(View.GONE);
                findViewById(R.id.lineView).setVisibility(View.GONE);
                break;
            case R.id.glOrderMember:
                //增加成员
                order_name.setText("人员管理");
                findViewById(R.id.layout_months).setVisibility(View.GONE);
                findViewById(R.id.lineView).setVisibility(View.GONE);
                break;
        }
        changePayAmount();
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
                if (isOpenTip) {
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

                if (TextUtils.equals("success", result)) {
                    LayoutInflater inflaterDl = LayoutInflater.from(this);
                    RelativeLayout layout = (RelativeLayout) inflaterDl.inflate(R.layout.dialog_pay_result_layout, null);
                    //对话框
                    final Dialog dialog = new AlertDialog.Builder(mContext).create();
                    dialog.show();
                    dialog.getWindow().setContentView(layout);

                    layout.findViewById(R.id.close_dialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            finishOk();
                        }
                    });
                } else if (TextUtils.equals("fail", result)) {
                    showToast("支付失败:\n" + errorMsg + "\n" + extraMsg);
                } else if (TextUtils.equals("cancel", result)) {
                    showToast("取消支付");
                } else {
                    showToast("未找到可支付的插件");
                }
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
        map.put(Constants.ORDER_MONTHS, paymentRequest.channel);
        map.put(Constants.ORDER_A_NUM, paymentRequest.channel);
        map.put(Constants.ORDER_B_NUM, paymentRequest.channel);
        map.put(Constants.ORDER_TYPE, paymentRequest.channel);
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
        if (amount <= 0) {
            showToast("请选择相应的增值服务");
        } else if (alipay.isChecked()) {
            //单位为分 测试需要改为1分钱
            paymentRequest.setTitle("android-支付宝支付");
            paymentRequest.setContent("android-支付宝支付描述");
            paymentRequest.setAmount(amount * 100);
            paymentRequest.setChannel(CHANNEL_ALIPAY);
            postJson(paymentRequest);
        } else if (wxpay.isChecked()) {
            //单位为分
            paymentRequest.setTitle("android-微信支付");
            paymentRequest.setContent("android-微信支付描述");
            paymentRequest.setAmount(amount * 100);
            paymentRequest.setChannel(CHANNEL_WECHAT);
            postJson(paymentRequest);
        } else {
            showToast("请选择支付方式");
        }
    }

    public void onJiaTimes(View view) {
        //增加购买时限
        paymentRequest.setMonths(paymentRequest.getMonths() + 1);
        order_time_num.setText("" + paymentRequest.getMonths());
        changePayAmount();
    }

    public void onJianTimes(View view) {
        //减少购买时限
        if (paymentRequest.getMonths() > 1) {
            paymentRequest.setMonths(paymentRequest.getMonths() - 1);
            order_time_num.setText("" + paymentRequest.getMonths());
            changePayAmount();
        }
    }

    public void onJiaAdmin(View view) {
        //管理人员数+1
        paymentRequest.setA_num(paymentRequest.getA_num() + 1);
        order_admin_num.setText("" + paymentRequest.getA_num());
        changePayAmount();
    }

    public void onJianAdmin(View view) {
        //管理人员数-1
        if (paymentRequest.getA_num() > 0) {
            paymentRequest.setA_num(paymentRequest.getA_num() - 1);
            order_admin_num.setText("" + paymentRequest.getA_num());
            changePayAmount();
        }
    }

    public void onJiaMember(View view) {
        //成员数+1
        paymentRequest.setB_num(paymentRequest.getB_num() + 1);
        order_member_num.setText("" + paymentRequest.getB_num());
        changePayAmount();
    }

    public void onJianMember(View view) {
        //成员数-1
        if (paymentRequest.getB_num() > 0) {
            paymentRequest.setB_num(paymentRequest.getB_num() - 1);
            order_member_num.setText("" + paymentRequest.getB_num());
            changePayAmount();
        }
    }

    //自动计算总额
    private void changePayAmount() {
        amount = paymentRequest.getA_num() * 10 + paymentRequest.getB_num() * 8 + paymentRequest.getBaseAmount() * paymentRequest.getMonths();
        order_amount.setText(getString(R.string.pay_amount, amount));
    }

    class PaymentRequest {
        String title;//订单标题
        String content;//订单描述
        String channel;//渠道
        int amount;//金额,单位分
        int months = 1;//购买多少个月
        int a_num;//管理员数量
        int b_num;//成员数量
        int type;//1表示商务版包月，2表示商务版包年，3表示旗舰版包月，4表示旗舰版包年
        int baseAmount;//基础价格

        public int getBaseAmount() {
            return baseAmount;
        }

        public void setBaseAmount(int baseAmount) {
            this.baseAmount = baseAmount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getMonths() {
            return months;
        }

        public void setMonths(int months) {
            this.months = months;
        }

        public int getA_num() {
            return a_num;
        }

        public void setA_num(int a_num) {
            this.a_num = a_num;
        }

        public int getB_num() {
            return b_num;
        }

        public void setB_num(int b_num) {
            this.b_num = b_num;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}