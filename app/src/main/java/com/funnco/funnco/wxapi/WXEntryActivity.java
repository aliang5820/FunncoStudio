
package com.funnco.funnco.wxapi;

import android.widget.Toast;

import com.funnco.funnco.utils.log.LogUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
    @Override
    public void onReq(BaseReq req) {
        LogUtils.e("", "----wsf---------onReq----------openId:"+req.openId+"  ,transaction:"+req.transaction);
        super.onReq(req);
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtils.e("", "-----wsf--------onResp----------errStr:"+resp.errStr+" , openId:"+resp.openId+" , transaction:"+resp.transaction);
        super.onResp(resp);
        String result = null;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK: {
//				CreditManager creditManager = ShuiDi.instance().getCreditManager();
//				creditManager.submitGainCreditEventToServer(gainCreditType, dataShareId);
                result = "发布成功";
            }
            break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发布取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发布被拒绝";
                break;
            default:
                result = "发布返回";
                break;
        }

        Toast.makeText(this, "" + result, Toast.LENGTH_LONG).show();
        this.finish();
    }
}
