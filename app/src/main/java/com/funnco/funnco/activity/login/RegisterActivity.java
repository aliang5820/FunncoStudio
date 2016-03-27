package com.funnco.funnco.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Career;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.task.AsyTask;
import com.funnco.funnco.utils.bimp.BitmapUtils;
import com.funnco.funnco.utils.file.FileTypeUtils;
import com.funnco.funnco.utils.file.FileUtils;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wq.photo.CropImageActivity;
import com.wq.photo.MediaChoseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends BaseActivity {

    private TextView tvInstruction;
    private EditText etUsername ;
    private EditText etPassword ;
    private Button tvCareerType;
    private EditText etPhoneNumber;
    private CircleImageView ivIcon;
    //验证码
    private EditText etAuthorCode ;
    //获取验证码
    private TextView tvGetAuthorcode ;
    //职业类别
    private PopupWindow pwCareer;
    private View viewCareers;
    private NumberPicker npCareer;
    private String currentCareer;
    private String strUsername ;
    private String strPassword ;
    private String career_id = "1";
    private String strPhonenumber ;
    private String strAuthorCode;
//    private String device_token;

    private PopupWindow pwPhotoChoose;
    private View view;
    private String newIconPath;
    //是否是拍照
    private boolean isCamer = false;
    public static final int REQUEST_IMAGE = 1000;
    public static final int REQUEST_CODE_CROP = 2002;
    public static final int CHOSE_MODE_SINGLE = 0;//单选

    //网络
    private HttpUtils utils ;
    private RequestParams params;
    private DbUtils dbUtils ;
    private Intent intent ;
    private List<Career> list = new ArrayList<>();;
    private View parentView;

    //信息发送成功
    private boolean isSending = false;
    //
    private boolean isSending2 = false;
    private int seconds = 60;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    tvGetAuthorcode.setText("再次获取"+(--seconds));
                    if(seconds <=0){
                        isSending2 = false;
                        tvGetAuthorcode.setEnabled(true);
                        tvGetAuthorcode.setText("获取验证码");
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_register,null);
        setContentView(parentView);
    }
    @Override
    protected void initView() {
        tvInstruction = (TextView) findViewById(R.id.tv_register_instruction);
        etUsername = (EditText) findViewById(R.id.et_register_username);
        etPassword = (EditText) findViewById(R.id.et_register_password);
        tvCareerType = (Button) findViewById(R.id.tv_register_career);
        etPhoneNumber = (EditText) findViewById(R.id.et_register_phonenumber);
        etAuthorCode = (EditText) findViewById(R.id.et_register_authcode);
        tvGetAuthorcode = (TextView) findViewById(R.id.tv_register_getauthorcode);
        ivIcon = (CircleImageView) findViewById(R.id.iv_register_usericon);
        SpannableString spannableString = new SpannableString("注册即表示同意FUNNCO的服务条款、隐私政策");
        //设置超链接
        spannableString.setSpan(new URLSpan(FunncoUrls.getPrivacyUrl()), 14, 18,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new URLSpan(FunncoUrls.getPrivacyUrl()), 19, 23,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvInstruction.setText(spannableString);
        //设置可以点击
        tvInstruction.setMovementMethod(LinkMovementMethod.getInstance());

        utils = new HttpUtils("utf-8");
        params = new RequestParams("utf-8");
        dbUtils = BaseApplication.getInstance().getDbUtils();
        viewCareers = getLayoutInflater().inflate(R.layout.layout_dialog_valuespicker, null);
        npCareer = (NumberPicker) viewCareers.findViewById(R.id.np_activity_pw_career);
        initCarceerListData();
        pwCareer = new PopupWindow(viewCareers, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pwCareer.setBackgroundDrawable(new BitmapDrawable());
        pwCareer.setFocusable(true);
        pwCareer.setOutsideTouchable(true);
        pwCareer.setClippingEnabled(true);
        viewCareers.findViewById(R.id.bt_activity_pw_cancle).setOnClickListener(this);
        viewCareers.findViewById(R.id.bt_activity_pw_ok).setOnClickListener(this);

        //相册的popupwindow
        pwPhotoChoose = new PopupWindow(mContext);
        view = getLayoutInflater().inflate(R.layout.layout_popupwindwo_choosephoto, null);
        pwPhotoChoose.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pwPhotoChoose.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwPhotoChoose.setBackgroundDrawable(new BitmapDrawable());
        pwPhotoChoose.setFocusable(true);
        pwPhotoChoose.setOutsideTouchable(true);
        pwPhotoChoose.setContentView(view);

    }

    @Override
    protected void initEvents() {
        ivIcon.setOnClickListener(this);
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
            }
        });
        //进行拍照
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isCamer = true;
                pwPhotoChoose.dismiss();
                sendStarCamera();
            }
        });
        //照片选择
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isCamer = false;
                pwPhotoChoose.dismiss();
                Intent intent = new Intent(RegisterActivity.this, MediaChoseActivity.class);
                intent.putExtra("crop", true);
                intent.putExtra("crop_image_w", 350);//宽度
                intent.putExtra("crop_image_h", 350);//高度可以自定义
                intent.putExtra("chose_mode", CHOSE_MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pwPhotoChoose.dismiss();
            }
        });
    }
    //进行拍照
    private static final int REQUEST_CODE_CAMERA = 14;
    File currentfile;
    public void sendStarCamera() {
        currentfile = getTempFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentfile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }
    /**
     * 启动裁剪的Activity  传递的参数有：裁剪的高宽，裁剪后的图片存放的绝对路径
     *
     * @param path
     */
    public void sendStarCrop(String path) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.setData(Uri.fromFile(new File(path)));
        intent.putExtra("crop_image_w", 300);
        intent.putExtra("crop_image_h", 300);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCropFile().getAbsolutePath());
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }
    public File getCropFile() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Funnco", "crop.jpg");
    }
    //得到临时文件存储路径
    public File getTempFile() {
        String str = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
        return new File(FileUtils.getRootPath() + "/Funnco", "FC_" + str + ".jpg");
    }
    private void initCarceerListData() {
        if(null != list && list.size() >0){
            changeData();
            return;
        }
        try {
            if (dbUtils.tableIsExist(Career.class)) {
                List<Career> ls = dbUtils.findAll(Career.class);
                if (null != ls && ls.size() >0) {
                    list.addAll(dbUtils.findAll(Career.class));
                    changeData();
                }else{
                    getCareerData();
                }
            }else{
                getCareerData();
            }
        }catch (Exception e ){}
    }
    private synchronized void changeData(){
        if (list == null || list.size() ==0){
            return;
        }
        int size = list.size();
        final String[] data = new String[size];
        for (int i = 0;i < list.size(); i ++) {
            data[i] = list.get(i).getCareer_name();
        }
        npCareer.setMinValue(0);
        npCareer.setMaxValue(size - 1);
        npCareer.setDisplayedValues(data);
        npCareer.postInvalidate();
        npCareer.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentCareer = data[newVal];
                career_id = list.get(newVal).getId();
            }
        });
    }

    //获取网络数据行业类别
    public void getCareerData() {
        postData2(null, FunncoUrls.getCareerTypeListUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result, url);
        JSONObject object = JsonUtils.getJObt(result, "params");
        if (object != null) {//确保不为空是在处理相关逻辑
            JSONArray jsonArray = JsonUtils.getJAry(object.toString(), "list");
            list.addAll(JSON.parseArray(jsonArray.toString(), Career.class));
            try {
                dbUtils.saveAll(list);//下载完 数据保存
            } catch (Exception e) {
            }
            initCarceerListData();
        }
    }

    public void btnClick(View v) {
        switch (v.getId()){
            case R.id.tv_register_getauthorcode://获得手机验证码
                strPhonenumber = etPhoneNumber.getText()+"";
                if (!com.funnco.funnco.utils.string.TextUtils.isPhoneNumber(strPhonenumber)){//该部进行手机号验证
                    showSimpleMessageDialog(R.string.mobile_err);
                    return;
                }
                Map<String ,Object> map = new HashMap<>();
                map.put(Constants.PHONE_NUMBER, strPhonenumber);
                AsyTask task2 = new AsyTask(map, new DataBack() {
                    @Override
                    public void getString(String result) {
                        isSending2 = true;
                        if(!TextUtils.isEmpty(result)){
                            int resp = JsonUtils.getResponseCode(result);
                            String msg = JsonUtils.getResponseMsg(result);
                            switch (resp){
                                case 0:
                                    showSimpleMessageDialog(R.string.success);
                                    timeStart();
                                    break;
                                default:
                                    showSimpleMessageDialog(msg+"");
                                    break;
                            }
                        }
                    }
                    @Override
                    public void getBitmap(String rul,Bitmap bitmap) {
                    }
                },false);
                task2.execute(FunncoUrls.getAuthorCode());
                putAsyncTask(task2);
                //get提交
                break;
            case R.id.tv_register_register://进行注册
                if (isSending){
                    showSimpleMessageDialog(R.string.uploading);
                    return;
                }
                //分别获取各个值 并且进行验证操作
                strUsername = etUsername.getText()+"";
                strPassword = etPassword.getText()+"";
                strPhonenumber = etPhoneNumber.getText()+"";
                strAuthorCode = etAuthorCode.getText()+"";
                if (!checkData()){
                    showSimpleMessageDialog(R.string.fillout_right);
                    return;
                }
                params.addBodyParameter(Constants.NICK_NAME,strUsername);
                params.addBodyParameter(Constants.USER_PWD,strPassword);
                params.addBodyParameter(Constants.PHONE_NUMBER,strPhonenumber);
                params.addBodyParameter(Constants.CAREER_ID,career_id);
                params.addBodyParameter(Constants.VCODE,strAuthorCode+"");
                params.addBodyParameter(Constants.DEVICE_TOKEN,device_token);
                if (currentfile != null && currentfile.exists()){
                    params.addBodyParameter(Constants.FILE1,currentfile, FileTypeUtils.getType(newIconPath));
                }
                postUtilData(params);
                break;
            case R.id.tv_register_cancle://取消
                finishOk();
                break;
            case R.id.tv_register_career:
                if (null != pwCareer) {
                    pwCareer.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                }else{
                    showSimpleMessageDialog(R.string.data_err);
                }
                break;
            default:
                break;
        }
    }
    private boolean checkData() {
        if (TextUtils.isEmpty(strPhonenumber) || TextUtils.isEmpty(strAuthorCode) || TextUtils.isEmpty(strPassword) || TextUtils.isEmpty(career_id)){
            return false;
        }
        return true;
    }
    private void timeStart() {
        tvGetAuthorcode.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSending2){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }
    private void postUtilData(RequestParams params){

        if (utils != null){
            isSending = true;
            utils.send(HttpRequest.HttpMethod.POST, FunncoUrls.getRegisterUrl(), params,new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    isSending = false;
                    String result = responseInfo.result;
                    LogUtils.e("------", result);
                    if (JsonUtils.getResponseCode(result) == 0) {
                        UserLoginInfo user = JsonUtils.getObject(JsonUtils.getJObt(result, "params").toString(), UserLoginInfo.class);
                        BaseApplication.getInstance().setUser(user);
                        SharedPreferencesUtils.setValue(RegisterActivity.this, Constants.SHAREDPREFERENCE_CONFIG, Constants.USER_PWD, strPassword);
                        SharedPreferencesUtils.setUserValue(RegisterActivity.this, Constants.SHAREDPREFERENCE_CONFIG, user);
                        showToast(R.string.ok_regist);
                        intent.putExtra(Constants.PHONE_NUMBER, strPhonenumber);
                        setResult(RESULT_OK, intent);
                        finishOk();
                    }else{
                        String msg = JsonUtils.getResponseMsg(result);
                        showSimpleMessageDialog(msg);
                    }
                }
                @Override
                public void onFailure(HttpException e, String s) {
                    isSending = false;
                }
            });
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_activity_pw_cancle:
                tvCareerType.setText("");
                currentCareer = "";
                pwCareer.dismiss();
                break;
            case R.id.bt_activity_pw_ok:
                tvCareerType.setText(currentCareer);
                pwCareer.dismiss();
                break;
            case R.id.iv_register_usericon:
                pwPhotoChoose.showAtLocation(parentView,Gravity.BOTTOM,0,0);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //获得相册照片
            case REQUEST_IMAGE:
                if (data != null) {
                    String imagepath = data.getStringExtra("currentimage");
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
            //进行裁剪
            case REQUEST_CODE_CROP:
                //得到裁剪图片存储路径
                if (data == null){
                    return;
                }
                newIconPath = data.getStringExtra("crop_path");
                if (newIconPath != null) {
                    currentfile = new File(newIconPath);
                    if (currentfile.exists()) {
                        Bitmap bm = BitmapUtils.getCompressBitmap(newIconPath, 150, 150);
                        ivIcon.setImageBitmap(bm);
                    } else {
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}