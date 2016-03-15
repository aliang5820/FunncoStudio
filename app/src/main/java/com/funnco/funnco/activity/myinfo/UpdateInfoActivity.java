package com.funnco.funnco.activity.myinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.activity.ChooseIconActivity;
import com.funnco.funnco.adapter.BucketChooseAdatper;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Career;
import com.funnco.funnco.bean.ImageBucket;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.bimp.BitmapUtils;
import com.funnco.funnco.utils.file.AlbumHelper;
import com.funnco.funnco.utils.file.FileTypeUtils;
import com.funnco.funnco.utils.http.LoginUtils;
import com.funnco.funnco.utils.http.NetUtils;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.Constants;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wq.photo.CropImageActivity;
import com.wq.photo.MediaChoseActivity;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 修改个人资料
 * Created by user on 2015/5/18.
 *
 * @author Shawn
 */
public class UpdateInfoActivity extends BaseActivity {

    //网络
    private HttpUtils xUtils = null;
    private RequestParams params;
    private Intent intent = null;
    private List<Career> list = null;

    private boolean isSubmiting = false;
    private View parentView;
    //各个属性
    private CircleImageView civIcon;//头像
    private EditText etUsername = null;
    private ImageView ivSex;
    private TextView tvCareer;
    private TextView tvConventationcount;
    private EditText etInfo = null;
    private EditText etWorkphone, etAddress;


    private String strNickname = null;
    private String strSex = "男";
    private String strCarceer = "1";
    private String strDec = null;
    private String strCareerName = "";
    private String strWorkphone = "";
    private String strAddress = "";


    private String msg;

    private UserLoginInfo user = null;
    //选择头像的popupwindow
    private PopupWindow pwPhotoChoose;
    //选择相册
    private PopupWindow pwBucketChoose;
    private ListView bkListView;
    //所有相册的集合
    private AlbumHelper helper;
    private List<ImageBucket> contentList;
    private BucketChooseAdatper bucketChooseAdatper;

    private LinearLayout ll_popup;
    //选择职业
    private PopupWindow pwCareerChoose;
    //选择性别
    private PopupWindow pwSexChoose;
    private Button btMan, btWomen, btCancle2;

    private NumberPicker np;
    private String[] listData;
    private Button btCancle3;
    private Button btOk;

    private String newIconPath;
    public static final int REQUEST_IMAGE = 1000;
    //    public static final int REQUEST_CODE_CAMERA = 2001;
    public static final int REQUEST_CODE_CROP = 2002;
    public static final int CHOSE_MODE_SINGLE = 0;//单选
    public static final int CHOSE_MODE_MULTIPLE = 1;//多选  模式
    //    public static final int RESULT_CODE_UPDATE = 0x4f01;//修改成功返回的 响应码
    private final static int RESULT_CODE_EDIT = 0xf082;
    private boolean isCamer = false;
//    private VolleyUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_updateinfo_2, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
//        utils = new VolleyUtils(mContext);
        xUtils = new HttpUtils(10000);
        user = BaseApplication.getInstance().getUser();
        list = new ArrayList<>();
        etUsername = (EditText) findViewById(R.id.et_updateinfo_username);
        ivSex = (ImageView) findViewById(R.id.iv_updateinfo_sex);
        ((TextView) findViewById(R.id.tv_headcommon_headm)).setText(R.string.str_personalinfo_edit);

        tvConventationcount = (TextView) findViewById(R.id.tv_updateinfo_conventationcount);
        tvCareer = (TextView) findViewById(R.id.tv_updateinfo_career);
        etInfo = (EditText) findViewById(R.id.et_updateinfo_instroducation);
        etWorkphone = (EditText) findViewById(R.id.et_updateinfo_workphone);
        etAddress = (EditText) findViewById(R.id.et_updateinfo_address);

        civIcon = (CircleImageView) findViewById(R.id.civ_updateinfo_icon);
        //初始化各个值
        if (user != null) {
            setViewText();
        }
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

        pwSexChoose = new PopupWindow(mContext);
        View viewSex = getLayoutInflater().inflate(R.layout.layout_popupwindwo_choosesex, null);
        pwSexChoose.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pwSexChoose.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwSexChoose.setBackgroundDrawable(new BitmapDrawable());
        pwSexChoose.setFocusable(true);
        pwSexChoose.setOutsideTouchable(true);
        pwSexChoose.setContentView(viewSex);
        btMan = (Button) viewSex.findViewById(R.id.item_popupwindows_man);
        btWomen = (Button) viewSex.findViewById(R.id.item_popupwindows_women);
        btCancle2 = (Button) viewSex.findViewById(R.id.item_popupwindows_cancel);

//        pwCareerChoose = new PopupWindow(mContext);
        View viewCareer = getLayoutInflater().inflate(R.layout.layout_dialog_valuespicker, null);
        pwCareerChoose = new PopupWindow(viewCareer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btCancle3 = (Button) viewCareer.findViewById(R.id.bt_activity_pw_cancle);
        btOk = (Button) viewCareer.findViewById(R.id.bt_activity_pw_ok);
        np = (NumberPicker) viewCareer.findViewById(R.id.np_activity_pw_career);
        pwCareerChoose.setBackgroundDrawable(new BitmapDrawable());
        pwCareerChoose.setFocusable(true);
        pwCareerChoose.setOutsideTouchable(true);
        pwCareerChoose.setClippingEnabled(true);

        pwBucketChoose = new PopupWindow(mContext);
        View viewBucket = getLayoutInflater().inflate(R.layout.layout_popupwindwo_bucketchoose, null);
        pwBucketChoose.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pwBucketChoose.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pwBucketChoose.setBackgroundDrawable(new BitmapDrawable());
        pwBucketChoose.setFocusable(true);
        pwBucketChoose.setOutsideTouchable(true);
        bkListView = (ListView) viewBucket.findViewById(R.id.lv_popupwindow_bucketlist);
        pwBucketChoose.setContentView(viewBucket);

        helper = AlbumHelper.getHelper();
        helper.init(BaseApplication.getInstance());
        contentList = helper.getImagesBucketList(false);
        bucketChooseAdatper = new BucketChooseAdatper(mContext, contentList);
        bucketChooseAdatper.setPost(new Post() {
            @Override
            public void post(int ...position) {
                BaseApplication.imageBucket = contentList.get(position[0]);
                Intent intent = new Intent(mContext, ChooseIconActivity.class);
                startActivityForResult(intent, 104);
            }
        });
        bkListView.setAdapter(bucketChooseAdatper);

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
                Intent intent = new Intent(UpdateInfoActivity.this, MediaChoseActivity.class);
                intent.putExtra("crop", true);
                intent.putExtra("crop_image_w", 300);//宽度
                intent.putExtra("crop_image_h", 300);//高度可以自定义
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

        try {
            if (dbUtils.tableIsExist(Career.class)) {
                List<Career> ls = dbUtils.findAll(Career.class);
                if (ls != null && ls.size() > 0) {
                    if (null != list && list.size() > 0) {
                        list.clear();
                    }
                    list.addAll(ls);
                    changeData();
                } else {
                    getCareerData();
                }
            } else {
                getCareerData();
            }
        } catch (Exception e) {

        }
    }

    private void initIcon() {
        if (user != null && !TextUtils.isEmpty(user.getHeadpic()) && !TextUtils.equals("null", user.getHeadpic())) {
            if (imageLoader != null && options != null) {
                imageLoader.displayImage(user.getHeadpic(), civIcon, options);
            }
        }
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

    //得到临时文件存储路径
    public File getTempFile() {
        String str = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Funnco", "FC_" + str + ".jpg");
    }

    public File getCropFile() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Funnco", "crop.jpg");
    }

    private void changeData() {
        listData = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            listData[i] = list.get(i).getCareer_name();
        }
        np.setMinValue(0);
        np.setMaxValue(listData.length - 1);
        np.setDisplayedValues(listData);
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        findViewById(R.id.llayout_foot).setOnClickListener(this);
        civIcon.setOnClickListener(this);
        tvCareer.setOnClickListener(this);
        btCancle2.setOnClickListener(this);
        btCancle3.setOnClickListener(this);
        btOk.setOnClickListener(this);
        btMan.setOnClickListener(this);
        btWomen.setOnClickListener(this);
        ivSex.setOnClickListener(this);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                LogUtils.e("NumberPicker的只发生改变了：：：", "" + newVal);
//                strCarceer = newVal + "";
                strCareerName = listData[newVal];
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llayout_foot://提交
                if (!NetUtils.isConnection(mContext)) {
                    showNetInfo();
                    return;
                }
//                showToast("提交");
                strNickname = etUsername.getText() + "";
                strDec = etInfo.getText() + "";
                strWorkphone = etWorkphone.getText() + "";
                strAddress = etAddress.getText() + "";
                if (!checkData()) {
                    showSimpleMessageDialog(msg + "");
                    return;
                }
                if (isSubmiting) {
                    showSimpleMessageDialog(R.string.submiting);
                    return;
                }
                xUtilPostData();
//                postData();
                break;
            case R.id.civ_updateinfo_icon:
                ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.activity_translate_in));
                pwPhotoChoose.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.iv_updateinfo_sex:
                pwSexChoose.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.tv_headcommon_headl://标题栏的返回按钮
                finish();
                break;
            case R.id.tv_updateinfo_career://选择职业
                pwCareerChoose.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.bt_activity_pw_cancle:
                pwCareerChoose.dismiss();
                break;
            case R.id.bt_activity_pw_ok:
                tvCareer.setText(strCareerName);
                strCarceer = getCareerId(list, strCareerName);//获得id
                pwCareerChoose.dismiss();
                break;
            case R.id.item_popupwindows_cancel:
                strCarceer = user.getCareer_id() + "";
                strCareerName = user.getCareer_name() + "";
                tvCareer.setText(strCareerName);
                pwSexChoose.dismiss();
                break;
            case R.id.item_popupwindows_man:
                strSex = "男";
                ivSex.setImageResource(R.mipmap.common_my_boy_icon);
                pwSexChoose.dismiss();
                break;
            case R.id.item_popupwindows_women:
                strSex = "女";
                ivSex.setImageResource(R.mipmap.common_my_girl_icon);
                pwSexChoose.dismiss();
                break;
        }
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(strNickname)) {
            msg = getString(R.string.p_fillout_nickname);
            return false;
        }
        if (TextUtils.isEmpty(strCarceer)) {
            msg = getString(R.string.p_fillout_career);
            return false;
        }
        if (TextUtils.isEmpty(strDec)) {
            msg = getString(R.string.p_fillout_desc);
            return false;
        }
        params = new RequestParams("utf-8");
        CookieStore cookieStore = BaseApplication.getInstance().getCookieStore();
        if (cookieStore == null) {
            LogUtils.e("全局Cookie为空！！！", "");
            LoginUtils.reLogin(mContext, new Post() {
                @Override
                public void post(int ...position) {
                }
            });
            return false;
        }
        ArrayList<NameValuePair> listParams = new ArrayList<>();
        listParams.add(new BasicNameValuePair(Constants.ID, user.getId() + ""));
        listParams.add(new BasicNameValuePair(Constants.TOKEN, user.getToken() + ""));
        listParams.add(new BasicNameValuePair(Constants.NICK_NAME, strNickname + ""));
        listParams.add(new BasicNameValuePair(Constants.CAREER_ID, strCarceer + ""));
        listParams.add(new BasicNameValuePair(Constants.INTRO, strDec + ""));
        listParams.add(new BasicNameValuePair(Constants.SEX, strSex + ""));
        listParams.add(new BasicNameValuePair(Constants.WORK_PHONE, strWorkphone + ""));
        listParams.add(new BasicNameValuePair(Constants.ADDRESS, strAddress + ""));
        listParams.add(new BasicNameValuePair(Constants.ClIENT, Constants.ClIENT));
        listParams.add(new BasicNameValuePair(Constants.LAN, Constants.LAN));

        for (NameValuePair nvp : listParams) {
            LogUtils.e("提交的数据是：key:" + nvp.getName(), ",value:" + nvp.getValue());
        }

        StringBuilder sb = new StringBuilder();
        List<Cookie> list = cookieStore.getCookies();
        for (int i = 0; i < list.size(); i++) {
            Cookie c = list.get(i);
            sb.append(c.getName() + "=" + c.getValue() + ";");
        }
        sb.deleteCharAt(sb.length() - 1);
        params.addHeader("Cookie", sb.toString());

        params.addBodyParameter(listParams);
        if (currentfile != null && currentfile.exists() && currentfile.length() > 0) {
            params.addBodyParameter("file1", currentfile, FileTypeUtils.getType(currentfile.getAbsolutePath()));
            if (user != null && user.getHeadpic() != null) {
                params.addBodyParameter("headpic", user.getHeadpic() + "");
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //获得相册照片
            case REQUEST_IMAGE:
                LogUtils.e("单选图片返回成功！！", "-----");
                if (data != null) {
                    String imagepath = data.getStringExtra("currentimage");
                    LogUtils.e("单选图片", "  tupian地址是：" + imagepath);
                    File file = new File(imagepath);
                    if (file.exists()) {
                        LogUtils.e("单选图片", "  文件存在");
                        currentfile = new File(imagepath);
                        //进行裁剪
                        sendStarCrop(currentfile.getAbsolutePath());
                    } else {
                        LogUtils.e("单选图片", "  文件获取失败：");
                    }
                }
                break;
            //获得拍照的照片
            case REQUEST_CODE_CAMERA:
                LogUtils.e("接收到了拍照后的照片", "");
                if (currentfile != null) {
                    LogUtils.e("接收到了拍照后的照片路径是：", "，，，，" + currentfile.getAbsolutePath());
                    sendStarCrop(currentfile.getAbsolutePath());
                } else {
                    LogUtils.e("获取图片失败", "");
                }
                break;
            //将拍照照片进行裁剪
            case REQUEST_CODE_CROP:
                //得到裁剪图片存储路径
                if (data == null) {
                    return;
                }
                String crop_path = data.getStringExtra("crop_path");
                LogUtils.e("剪切后得到的图片路径是：", "" + crop_path);
                if (crop_path != null) {
                    currentfile = new File(crop_path);
                    if (currentfile.exists()) {
                        newIconPath = crop_path;
                        LogUtils.e("裁剪接受到的图片存在+++++", "");
                        try {
                            Bitmap bm = Bimp.revitionImageSize(crop_path);
                            bm = BitmapUtils.getCircleBitmap(bm, 150, 150);
                            civIcon.setImageBitmap(bm);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LogUtils.e("裁剪接受到的图片不存在", "");
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setViewText() {
        if (user != null) {
            initIcon();
            strCarceer = user.getCareer_id() + "";
            strCareerName = user.getCareer_name() + "";
            strDec = user.getIntro() + "";
            strSex = user.getSex() + "";
            strNickname = user.getNickname() + "";

            strAddress = user.getAddress() + "";
            strWorkphone = user.getWork_phone() + "";

            tvCareer.setText(strCareerName);
            etUsername.setText(strNickname);
            if (strSex.endsWith("男")) {
                ivSex.setImageResource(R.mipmap.common_my_boy_icon);
            } else {
                ivSex.setImageResource(R.mipmap.common_my_girl_icon);
            }
            etInfo.setText(strDec);
            etAddress.setText(strAddress);
            if (!TextUtils.isEmpty(strWorkphone) && !TextUtils.equals(strWorkphone, "null")) {
                etWorkphone.setText(strWorkphone);
            }
            if (!TextUtils.isEmpty(user.getBookings()) && !TextUtils.equals("null", user.getBookings())) {
                tvConventationcount.setText(user.getBookings() + "次");
            }
        }
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

    //获取网络数据行业类别
    public void getCareerData() {
        postData2(null,FunncoUrls.getCareerTypeListUrl(),false);


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
            changeData();
            setViewText();
        }
    }

    private void xUtilPostData() {
        showLoading(parentView);
        isSubmiting = true;
        if (params != null && xUtils != null) {
            xUtils.send(HttpRequest.HttpMethod.POST, FunncoUrls.getUpdateInfoUrl(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    isSubmiting = false;
                    dismissLoading();
                    String result = responseInfo.result;
                    LogUtils.e("修改资料上传成功！！返回的数据是：", "result----" + result);
                    if (!result.startsWith("{") || !result.endsWith("}")) {
                        result = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
                    }
                    if (JsonUtils.getResponseCode(result) == 0) {
                        JSONObject paramsJSONObject = JsonUtils.getJObt(result, "params");
                        String headpic = JsonUtils.getStringByKey4JOb(paramsJSONObject.toString(), "headpic");
                        if (!TextUtils.isEmpty(headpic) && headpic.startsWith("http")) {
                            user.setHeadpic(headpic);
                        }
                    }
                    user.setNickname(strNickname);
                    user.setCareer_id(strCarceer);
                    user.setCareer_name(strCareerName);
                    user.setIntro(strDec);
                    user.setSex(strSex);
                    user.setWork_phone(strWorkphone);
                    user.setAddress(strAddress);
                    BaseApplication.getInstance().setUser(user);
                    try {
                        dbUtils.saveOrUpdate(user);//更新数据库
                    } catch (DbException e) {}
                    setResult(RESULT_CODE_EDIT);
                    finishOk();
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    dismissLoading();
                    isSubmiting = false;
                    showSimpleMessageDialog(s + "");
                    LogUtils.e("修改资料上传失败！！返回的数据是：", "result----" + s);
                }
            });
        } else {
            isSubmiting = false;
            dismissLoading();
        }
    }


    private String getCareerId(List<Career> list, String careerName) {
        String id = "0";
        if (list == null || list.size() == 0 || TextUtils.isEmpty(careerName)) {
            return id;
        }
        for (Career c : list) {
            if (c.getCareer_name().equals(careerName)) {
                return c.getId() + "";
            }
        }
        return id;
    }
}