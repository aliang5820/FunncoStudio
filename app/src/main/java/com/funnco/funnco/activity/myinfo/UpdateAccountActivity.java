package com.funnco.funnco.activity.myinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.AccountBusinessInfo;
import com.funnco.funnco.bean.MyCustomerConventation;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.SQliteAsynchTask;
import com.funnco.funnco.utils.http.AsyncTaskUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.circleview.BaseCircleView;
import com.funnco.funnco.view.circleview.CircleProgressRightView;

import org.json.JSONObject;

/**
 * 账号升级
 * Created by user on 2015/11/10.
 */
public class UpdateAccountActivity extends BaseActivity {

    private View parentView;
    private BaseCircleView vipTimeView;
    private BaseCircleView vipLeftView;
    private CircleProgressRightView vipRightView;
    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.activity_update_account, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_money);
        vipTimeView = (BaseCircleView) findViewById(R.id.vip_last_time_progress);
        vipLeftView = (BaseCircleView) findViewById(R.id.vip_left_progress);
        vipRightView = (CircleProgressRightView) findViewById(R.id.vip_right_progress);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        putAsyncTask(AsyncTaskUtils.requestGet(new DataBack() {
            @Override
            public void getString(String result) {
                if (JsonUtils.getResponseCode(result) == 0) {
                    //进行解析
                    JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                    AccountBusinessInfo info = JsonUtils.getObject(paramsJSONObject.toString(), AccountBusinessInfo.class);
                    vipTimeView.setMaxCount(info.getTotalDays());
                    vipTimeView.setCurrentCount(info.getLeftDays());
                    //左边圆
                    vipLeftView.setMaxCount(info.getTotalPushNums());
                    vipLeftView.setCurrentCount(info.getPushNums());
                    //右边圆
                    vipRightView.setMaxCount(info.getTotalAlterNums());
                    vipRightView.setCurrentCount(info.getAlterNums());
                } else {
                    showToast(JsonUtils.getResponseMsg(result) + "");
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {

            }
        }, FunncoUrls.getBusinessStatusUrl(), true));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
        }
    }

    public void onQJCharge(View view) {
        //旗舰版
        Intent intent = new Intent(this, UpdateAccountActivity_2.class);
        intent.putExtra(Constants.ORDER_TYPE, Constants.ORDER_TYPE_QJ);
        startActivity(intent);
    }

    public void onSWCharge(View view) {
        //商务版
        Intent intent = new Intent(this, UpdateAccountActivity_2.class);
        intent.putExtra(Constants.ORDER_TYPE, Constants.ORDER_TYPE_SW);
        startActivity(intent);
    }

    public void onGLCharge(View view) {
        //人数管理版
        Intent intent = new Intent(this, UpdateAccountActivity_2.class);
        intent.putExtra(Constants.ORDER_TYPE, Constants.ORDER_TYPE_GL);
        startActivity(intent);
    }
}
