package com.funnco.funnco.activity.base;

/**
 * Created by user on 2015/11/6.
 */

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.utils.file.FileUtils;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.support.Constants;
import com.lidroid.xutils.exception.DbException;

/**
 * 二维码名片
 * Created by user on 2015/6/13.
 * @author Shawn
 */
public class QR_CodeActivity extends BaseActivity {
    //头部
    private TextView tvSave;

    private ImageView ivQRCode;
    //当前登录账号对象
    private UserLoginInfo userLoginInfo;
    //进行保存的popupwindow
    private PopupWindow pwSave;
    private View pwView;
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_qrcode,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        //        utils = new VolleyUtils(mContext);
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.qr_code);
        tvSave = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvSave.setText(R.string.save);
        ivQRCode = (ImageView) findViewById(R.id.iv_qr_codde_2d);
        userLoginInfo = BaseApplication.getInstance().getUser();
        if (userLoginInfo == null && dbUtils!= null){
            try {
                userLoginInfo = dbUtils.findById(UserLoginInfo.class,
                        SharedPreferencesUtils.getValue(mContext, Constants.SHAREDPREFERENCE_CONFIG, Constants.UID));
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (userLoginInfo == null) {
                showSimpleMessageDialog(R.string.user_err);
                return;
            }
        }
        imageLoader.displayImage(userLoginInfo.getPic_qrcode(), ivQRCode);

        pwView = getLayoutInflater().inflate(R.layout.layout_popupwindow_logout, null);
        ((TextView)pwView.findViewById(R.id.tv_pw_delete_title)).setText(R.string.save_y_n);
        pwSave = new PopupWindow(pwView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
    }

    @Override
    protected void initEvents() {
        tvSave.setOnClickListener(this);
        ivQRCode.setOnClickListener(this);
        ivQRCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pwSave.showAtLocation(parentView, Gravity.BOTTOM,0,0);
                return false;
            }
        });
        pwView.findViewById(R.id.bt_popupwindow_delete).setOnClickListener(this);
        pwView.findViewById(R.id.bt_popupwindow_cancle).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.tv_headcommon_headr:
                if (!NetUtils.isConnection(mContext)){
                    showToast(R.string.net_err);
                    return;
                }
                pwSave.showAtLocation(parentView, Gravity.BOTTOM,0,0);
                break;
            case R.id.bt_popupwindow_cancle://取消
                pwSave.dismiss();
                break;
            case R.id.bt_popupwindow_delete://保存
                pwSave.dismiss();
                if (!NetUtils.isConnection(mContext)){
                    showToast(R.string.net_err);
                    return;
                }
                saveQR_code();
                break;
            case R.id.iv_qr_codde_2d:
                break;
            default:
                break;
        }
    }

    private void saveQR_code() {
        if (ivQRCode != null){
            BitmapDrawable d = (BitmapDrawable) ivQRCode.getDrawable();
            if (d == null){
                showSimpleMessageDialog(R.string.failue);
                return;
            }
            Bitmap bitmap = d.getBitmap();
            String imgName = "QR-funnco-"+ SharedPreferencesUtils.getValue(mContext,
                    Constants.SHAREDPREFERENCE_CONFIG,Constants.UID);
            String path = FileUtils.saveBitmap(QR_CodeActivity.this, bitmap, imgName);
            showToast(R.string.success);
        }
    }
}
