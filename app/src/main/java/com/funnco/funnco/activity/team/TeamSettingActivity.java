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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.TeamMy;
import com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.http.XTaskUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.support.FunncoUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.switcher.SwitchView;
import com.lidroid.xutils.HttpUtils;
import com.wq.photo.CropImageActivity;
import com.wq.photo.MediaChoseActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  编辑团队资料 只有创建团队才有修改封面权限
 * Created by user on 2015/8/17.
 */
public class TeamSettingActivity extends BaseActivity {

    private View parentView;
    private FrameLayout container;
    private View viewManager;

    private TeamMy team;
    private String role;
    private SwitchView switchView;
    private ImageView ivTeampic;//封面
    private EditText etTeamname;//团队名字
    private EditText etMobile;
    private EditText etAddress;
    private EditText etDesc;

    private boolean searchable = true;
    private String teamName;
    private String mobile;
    private String address;
    private String desc;


    private TextView tvTeamBreakUp;

    //选择头像的popupwindow
    private PopupWindow pwPhotoChoose;
    private LinearLayout ll_popup;
    private String newIconPath;
    //是否是拍照
    private boolean isCamer = false;
    public static final int REQUEST_IMAGE = 1000;
    public static final int REQUEST_CODE_CROP = 2002;
    public static final int CHOSE_MODE_SINGLE = 0;//单选
    public static final int CHOSE_MODE_MULTIPLE = 1;//多选  模式
    private static final int REQUEST_CODE_COMMON = 0xf01;
    private static final int RESULT_CODE_COMMON = 0xf503;

    private static final int RESULT_CODE_TEAM_BREAKUP = 0xf502;

    private Intent intent;

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_container,null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        if (intent != null){
            String key = intent.getStringExtra(KEY);
            if (!TextUtils.isNull(key)){
                team = (TeamMy) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
            }
        }
        container = (FrameLayout) findViewById(R.id.layout_container);
        tvTeamBreakUp = (TextView) findViewById(R.id.tv_headcommon_headr);
        tvTeamBreakUp.setText(R.string.str_team_breakup);
        tvTeamBreakUp.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(getString(R.string.str_team_setting));
        if (team != null){
            initUI();
        }
    }

    private void initUI() {
        if (team != null){
            role = team.getRole();
            if (role.equals("0")){
                findViewById(R.id.llayout_foot).setVisibility(View.VISIBLE);
                tvTeamBreakUp.setVisibility(View.VISIBLE);
                viewManager = getLayoutInflater().inflate(R.layout.layout_activity_teamsetting_manager, null);
                container.removeAllViews();
                container.addView(viewManager);
                switchView = (SwitchView) viewManager.findViewById(R.id.id_switchview);
                ivTeampic = (ImageView) viewManager.findViewById(R.id.id_imageview);
                etTeamname = (EditText) viewManager.findViewById(R.id.id_title_0);
                etMobile = (EditText) viewManager.findViewById(R.id.id_title_1);
                etAddress = (EditText) viewManager.findViewById(R.id.id_title_2);
                etDesc = (EditText) viewManager.findViewById(R.id.id_title_3);

                if (team.getAllow_search().equals("1")){
                    switchView.toggleSwitch(true);
                }else{
                    switchView.toggleSwitch(false);
                }

                imageLoader.displayImage(team.getCover_pic()+"", ivTeampic, options);
                etTeamname.setText(team.getTeam_name());
                mobile = team.getPhone();
                address = team.getAddress();
                desc = team.getIntro();
                if (!TextUtils.isNull(mobile)){
                    etMobile.setText(mobile);
                }else{
                    etMobile.setText("");
                }
                if (!TextUtils.isNull(address)){
                    etAddress.setText(address);
                }else{
                    etAddress.setText("");
                }
                if (!TextUtils.isNull(desc)){
                    etDesc.setText(desc);
                }else{
                    etDesc.setText("");
                }
                initChoosePhoto();
            }else{
                findViewById(R.id.llayout_foot).setVisibility(View.GONE);
                View viewMember = getLayoutInflater().inflate(R.layout.layout_activity_teamsetting, null);
                container.removeAllViews();
                container.addView(viewMember);
                ((TextView)viewMember.findViewById(R.id.id_title_0)).setText(team.getTeam_name());
                ivTeampic = (ImageView) viewMember.findViewById(R.id.id_imageview);
                imageLoader.displayImage(team.getCover_pic()+"", ivTeampic, options);
            }
        }
    }

    private void initChoosePhoto() {
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
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvTeamBreakUp.setOnClickListener(this);
        if (role.equals("0")){
            findViewById(R.id.llayout_foot).setOnClickListener(this);
            switchView.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                @Override
                public void toggleToOn() {
                    searchable = true;
                    switchView.toggleSwitch(true);
                }
                @Override
                public void toggleToOff() {
                    searchable = false;
                    switchView.toggleSwitch(false);
                }
            });
            if (ivTeampic != null){
                ivTeampic.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl://返回
                finishOk();
                break;
            case R.id.tv_headcommon_headr://解散团队
                String title = getString(R.string.str_team_breakup);
                FunncoUtils.showAlertDialog(mContext, title, new FunncoUtils.DialogCallback() {
                    @Override
                    public void onPositive() {
                        map.clear();
                        map.put("team_id", team.getTeam_id() + "");
                        postData2(map, FunncoUrls.getTeamBreakUpUrl(), false);
                    }
                });
                break;
            case R.id.id_imageview:
                pwPhotoChoose.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.llayout_foot://进行提交数据
                if (!role.equals("0")){
                    return;
                }
                teamName = etTeamname.getText()+"";
                mobile = etMobile.getText()+"";
                address = etMobile.getText()+"";
                desc = etDesc.getText()+"";
                if (!check()){
                    showSimpleMessageDialog(msg+"");
                    return;
                }
                xPost(FunncoUrls.getTeamSettingUrl(),new String[]{"team_id","team_name","allow_search","phone","intro","address"},
                        new Object[]{team.getTeam_id(),teamName,searchable?"1":"0",mobile,desc,address},
                        new String[]{"cover_pic"},new String[]{imagepath});
                break;
        }
    }
    private String msg ;
    private boolean check(){
        if (team == null){
            msg = getString(R.string.data_err);
            return false;
        }
        if (TextUtils.isNull(teamName)){
            msg = getString(R.string.p_fillout_teamname);
            return  false;
        }
        return true;
    }
    private void xPost(final String url,String[]keys,Object[]values,String[] fileKeys,String[] filePaths){
        XTaskUtils.requestPost(mContext, new HttpUtils(10000), url, keys, values, fileKeys, filePaths, new DataBack() {
            @Override
            public void getString(String result) {
                showToast(JsonUtils.getResponseMsg(result));
                if (JsonUtils.getResponseCode(result) == 0) {
                    finishOk();
                }
            }

            @Override
            public void getBitmap(String url, Bitmap bitmap) {}
        });
    }
    private Map<String, Object> map = new HashMap<>();
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.id_textview://修改封面
                if (team.getRole().equals("0") || team.getRole().equals("1")){
                    pwPhotoChoose.showAtLocation(parentView, Gravity.BOTTOM,0,0);
                }
                break;
            case R.id.id_button://退出团队
                map.clear();
                map.put("team_id", team.getTeam_id() + "");
//                map.put("team_uid", BaseApplication.getInstance().getUser().getId()+"");
                postData2(map, FunncoUrls.getTeamMemberRemoveUrl(), true);
                break;
        }
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        clearAsyncTask();
        if (url.equals(FunncoUrls.getTeamBreakUpUrl())) {
            mActivity.setResult(RESULT_CODE_TEAM_BREAKUP);//作为创建者解散团队
        }else if (url.equals(FunncoUrls.getTeamMemberRemoveUrl())){//作为成员 退出团队
            mActivity.setResult(RESULT_CODE_COMMON);
        }
        finishOk();
    }

    @Override
    protected void dataPostBackF(String result, String url) {
        super.dataPostBackF(result, url);
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
                    }
                }
                break;
            //获得拍照的照片
            case REQUEST_CODE_CAMERA:
                if (currentfile != null) {
                    sendStarCrop(currentfile.getAbsolutePath());
                }
                break;
            //将拍照照片进行裁剪
            case REQUEST_CODE_CROP:
                //得到裁剪图片存储路径
                if (data == null){
                    return;
                }
                String crop_path = data.getStringExtra("crop_path");
                if (crop_path != null) {
                    currentfile = new File(crop_path);
                    if (currentfile.exists()) {
                        newIconPath = crop_path;
                        try {
                            Bitmap bm = Bimp.revitionImageSize(crop_path);
                            if (ivTeampic != null) {
                                ivTeampic.setImageBitmap(bm);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
