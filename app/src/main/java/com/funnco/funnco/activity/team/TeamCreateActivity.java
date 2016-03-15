package com.funnco.funnco.activity.team;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.wq.photo.CropImageActivity;
import com.wq.photo.MediaChoseActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建团队
 * Created by user on 2015/8/21.
 */
public class TeamCreateActivity extends BaseActivity {

    private View parentView;
    private Button btNext;//下一步
    private Button btTeampic;//修改封面
    private ImageView ivTeampic;//封面
    private EditText etTeamName;

    //选择头像的popupwindow
    private PopupWindow pwPhotoChoose;
    private LinearLayout ll_popup;

    private String teamName;
    private String newIconPath;
    private String msg = "";
    //是否是拍照
    private boolean isCamer = false;
    public static final int REQUEST_IMAGE = 1000;
    public static final int REQUEST_CODE_CROP = 2002;
    public static final int CHOSE_MODE_SINGLE = 0;//单选
    public static final int CHOSE_MODE_MULTIPLE = 1;//多选  模式
    private static final int REQUEST_CODE_COMMON = 0xf01;
    private static final int RESULT_CODE_COMMON = 0xf11;
    private static final int RESULT_CODE_CREATENOW = 0xf22;//团队创建成功

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_teamcreate, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_team_create);
        btNext = (Button) findViewById(R.id.bt_save);
        btNext.setText(R.string.next);
        btTeampic = (Button) findViewById(R.id.bt_teamcreate_teampic);
        ivTeampic = (ImageView) findViewById(R.id.iv_teamcreate_icon);
        etTeamName = (EditText) findViewById(R.id.et_teamcreate_teamname);

        //相册的popupwindow
        pwPhotoChoose = new PopupWindow(mContext);
        View view = getLayoutInflater().inflate(R.layout.layout_popupwindwo_choosephoto, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.llayout_popupwindow);
        pwPhotoChoose.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pwPhotoChoose.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwPhotoChoose.setBackgroundDrawable(new BitmapDrawable());
        pwPhotoChoose.setFocusable(true);
        pwPhotoChoose.setOutsideTouchable(true);
        pwPhotoChoose.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwPhotoChoose.dismiss();
                ll_popup.clearAnimation();
            }
        });
        //进行拍照
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isCamer = true;
                pwPhotoChoose.dismiss();
                ll_popup.clearAnimation();
                sendStarCamera();
            }
        });
        //照片选择
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isCamer = false;
                pwPhotoChoose.dismiss();
                ll_popup.clearAnimation();
                Intent intent = new Intent(mContext, MediaChoseActivity.class);
                intent.putExtra("crop", true);
                intent.putExtra("crop_image_w", FunncoUtils.getScreenWidth(mContext));//宽度
                intent.putExtra("crop_image_h", FunncoUtils.getScreenWidth(mContext) - 50);//高度可以自定义
                intent.putExtra("chose_mode", CHOSE_MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pwPhotoChoose.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }
    //进行拍照
    private static final int REQUEST_CODE_CAMERA = 14;
    private File currentfile;
    private String imagepath;
    public void sendStarCamera() {
        currentfile = getTempFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentfile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }
    //得到临时文件存储路径
    public File getTempFile() {
        String str = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Funnco", "FC_" + str + ".jpg");
    }
    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        btNext.setOnClickListener(this);
        btTeampic.setOnClickListener(this);
        ivTeampic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.bt_save://下一步
                teamName = etTeamName.getText()+"";
                if (!checkData()){
                    showSimpleMessageDialog(msg + "");
                    return;
                }
                Intent intent = new Intent(mContext, TeamCardSettingActivity.class);
                intent.putExtra("teamName",teamName);
                intent.putExtra("filePath",imagepath);
                startActivityForResult(intent, REQUEST_CODE_COMMON);
                break;
            case R.id.iv_teamcreate_icon:
            case R.id.bt_teamcreate_teampic://设置封面按钮(设置返回后需要将修改封面按钮进行隐藏)
                pwPhotoChoose.showAtLocation(parentView, Gravity.BOTTOM,0,0);
                break;
        }
    }

    private boolean checkData() {
        if (TextUtils.isNull(teamName)){
            msg = getString(R.string.p_fillout_teamname);
            return false;
        }
        if (TextUtils.isNull(imagepath) || imagepath.length() <=4){
            msg = getString(R.string.p_fillout_teampic);
            return  false;
        }
        return true;
    }

    public void btnClick(View view){
    }

    /**
     * 启动裁剪的Activity  传递的参数有：裁剪的高宽，裁剪后的图片存放的绝对路径
     *
     * @param path
     */
    public void sendStarCrop(String path) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.setData(Uri.fromFile(new File(path)));
        intent.putExtra("crop_image_w", FunncoUtils.getScreenWidth(mContext));//宽度
        intent.putExtra("crop_image_h", FunncoUtils.dp2px(mActivity,200));//高度可以自定义
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCropFile().getAbsolutePath());
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }
    public File getCropFile() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Funnco", "crop.jpg");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //获得相册照片
            case REQUEST_IMAGE:
                if (data != null) {
                    imagepath = data.getStringExtra("currentimage");
                    File file = new File(imagepath);
                    if (file.exists()) {
                        currentfile = new File(imagepath);
                        //进行裁剪
                        sendStarCrop(currentfile.getAbsolutePath());
                    } else {
                    }
                }
                break;
            //获得拍照的照片
            case REQUEST_CODE_CAMERA:
                if (currentfile != null) {
                    sendStarCrop(currentfile.getAbsolutePath());
                } else {
                }
                break;
            //将拍照照片进行裁剪
            case REQUEST_CODE_CROP:
                //得到裁剪图片存储路径
                if (data == null) {
                    ivTeampic.setVisibility(View.GONE);
                    btTeampic.setVisibility(View.VISIBLE);
                    return;
                }
                String crop_path = data.getStringExtra("crop_path");
                if (crop_path != null) {
                    currentfile = new File(crop_path);
                    if (currentfile.exists()) {
                        newIconPath = crop_path;
                        try {
                            Bitmap bm = Bimp.revitionImageSize(crop_path);
//                            bm = BitmapUtils.getCircleBitmap(bm, 150, 150);
                            ivTeampic.setVisibility(View.VISIBLE);
                            btTeampic.setVisibility(View.GONE);
                            ivTeampic.setImageBitmap(bm);
                        } catch (IOException e) {
                            ivTeampic.setVisibility(View.GONE);
                            btTeampic.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
                break;
            case REQUEST_CODE_COMMON:
                if (resultCode == RESULT_CODE_COMMON){
                    setResult(RESULT_CODE_CREATENOW);
                    finishOk();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
