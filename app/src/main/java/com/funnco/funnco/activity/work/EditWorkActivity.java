package com.funnco.funnco.activity.work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.adapter.EditWorkAdapter;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.file.FileTypeUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.support.PublicWay;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.listview.HandyListview;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.util.List;

/**
 * Created by user on 2015/5/28.
 */
public class EditWorkActivity extends BaseActivity {

    private TextView tvBack;
    private TextView tvTitle;
    private TextView tvSubmit;
    private HandyListview listView;
    private HttpUtils utils;
    private EditWorkAdapter adapter;

    private AlertDialog.Builder builder;
    private ImageItem deleteItem;
    private boolean isUploadding;
    private static final int RESULT_CODE_ADDWORK = 0xff31;

    private Intent intent;
    private boolean isTeamWork = false;
    private String team_id;

    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_editwork, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null) {
            isTeamWork = intent.getBooleanExtra("isTeamWork", false);
            if (isTeamWork) {
                team_id = intent.getStringExtra("team_id");
            }
        }
        utils = new HttpUtils();
        tvBack = (TextView) findViewById(R.id.tv_headcommon_headl);
        tvTitle = (TextView) findViewById(R.id.tv_headcommon_headm);
        tvSubmit = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvSubmit.setText(getResources().getString(R.string.save));
        tvTitle.setText(getResources().getString(R.string.editwork));
        listView = (HandyListview) findViewById(R.id.lv_editwork_list);

        builder = new AlertDialog.Builder(mContext, R.style.dialog_themen);
        builder.setMessage("确定删除？");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bimp.tempSelectBitmap.remove(deleteItem);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        EditWorkAdapter.isEdit = true;
        adapter = new EditWorkAdapter(mContext, Bimp.tempSelectBitmap);
        adapter.setOnImageButtonClick(new EditWorkAdapter.ImageButtonClickListener() {
            @Override
            public void onImageButtonClickListener(ImageButton imageButton, String imagePath) {
                String ip = (String) imageButton.getTag();
                for (ImageItem item : Bimp.tempSelectBitmap) {
                    if (item.getImagePath().equals(ip)) {
                        deleteItem = item;
                    }
                }
                builder.show();
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    protected void initEvents() {
        tvBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headcommon_headl://取消
                //取消的时候将临时保存的图片集合清除
                Bimp.tempSelectBitmap.clear();
                finishOk();
                break;
            case R.id.tv_headcommon_headr://保存
                if(checkParams()) {
                    showToast("请填写完整照片的标题和描述");
                    return;
                }
                if (!NetUtils.isConnection(mContext)) {
                    showNetInfo();
                    return;
                }
                if (isUploadding) {
                    showSimpleMessageDialog(R.string.uploading);
                    return;
                }
                //进行提交
                uploadMethod();
                break;
        }
    }

    /**
     * 检查是否填写完整描述和标题
     */
    private boolean checkParams() {
        boolean error = false;
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            ImageItem item = Bimp.tempSelectBitmap.get(i);
            String title = PublicWay.hashMapTitle.get(item.getImagePath());
            String desc = PublicWay.hashMapDesc.get(item.getImagePath());
            String path = FileTypeUtils.getType(item.getImagePath());
            if(TextUtils.isNull(title) || TextUtils.isNull(desc) || TextUtils.isNull(path)) {
                error = true;
                break;
            }
        }
        return error;
    }

    private void uploadMethod() {
        isUploadding = true;
        RequestParams params = new RequestParams("UTF-8");
        CookieStore cookieStore = BaseApplication.getInstance().getCookieStore();
        if (cookieStore == null) {
            showSimpleMessageDialog(R.string.failue);
            return;
        }
//        showLoadingDialog();
        showLoading(parentView);
        StringBuilder sb = new StringBuilder();
        List<Cookie> list = cookieStore.getCookies();
        for (int i = 0; i < list.size(); i++) {
            Cookie c = list.get(i);
            sb.append(c.getName() + "=" + c.getValue() + ";");
        }
        sb.deleteCharAt(sb.length() - 1);
        params.addHeader("Cookie", sb.toString());
        if (isTeamWork) {
            params.addBodyParameter("team_id", team_id);
        }
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            ImageItem item = Bimp.tempSelectBitmap.get(i);
            params.addBodyParameter("title" + (i + 1), PublicWay.hashMapTitle.get(item.getImagePath()) + "");
            params.addBodyParameter("description" + (i + 1), PublicWay.hashMapDesc.get(item.getImagePath()) + "");
            params.addBodyParameter("file" + (i + 1), new File(item.getImagePath()), FileTypeUtils.getType(item.getImagePath()) + "");
        }

        params.addBodyParameter(Constants.ClIENT, Constants.STR_CLIENT);
        params.addBodyParameter(Constants.LAN, STR_LAN);
        utils.send(HttpRequest.HttpMethod.POST, FunncoUrls.getAddWorkUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.e("-----", "文件上传成功。。。" + responseInfo.result);
                dismissLoading();
                isUploadding = false;
                showToast(R.string.success_upload);
                clearTem();
                //上传成功回到主界面 否则回到添加界面
//                setResult(1033);
                //发送一个全局广播  workfragment收到后进行保存
//                sendBroadcast(new Intent(ACTION_WORKUP_SUCCESS));
                setResult(RESULT_CODE_ADDWORK);
                finishOk();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.e("-----", "文件上传失败" + s);
                dismissLoading();
                isUploadding = false;
                showSimpleMessageDialog(s + "");//修改为弹出错误消息
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
            }

            @Override
            public void onStart() {
                super.onStart();
            }
        });
    }

    @Override
    protected void onDestroy() {
//        clearTem();
        super.onDestroy();
    }

    private void clearTem() {
        if (Bimp.tempSelectBitmap != null)
            Bimp.tempSelectBitmap.clear();
    }
}
