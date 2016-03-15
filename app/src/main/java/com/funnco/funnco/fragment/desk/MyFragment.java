package com.funnco.funnco.fragment.desk;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.funnco.funnco.R;
import com.funnco.funnco.activity.MyConventionActivity;
import com.funnco.funnco.activity.MyCustomersActivity;
import com.funnco.funnco.activity.myinfo.UpdateAccountActivity;
import com.funnco.funnco.activity.work.MyWorkActivity;
import com.funnco.funnco.activity.PersonalInfoActivity;
import com.funnco.funnco.activity.SettingActivity;
import com.funnco.funnco.activity.base.HelpActivity;
import com.funnco.funnco.activity.base.MainActivity;
import com.funnco.funnco.activity.base.QR_CodeActivity;
import com.funnco.funnco.activity.myinfo.IncomeWeekActivity;
import com.funnco.funnco.activity.team.TeamMyActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Career;
import com.funnco.funnco.bean.UserLoginInfo;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.utils.json.JsonUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.url.FunncoUrls;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.view.listview.MyListview;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Observable;

public class MyFragment extends BaseFragment implements View.OnClickListener{

    private MyListview myListview = null;
    private RelativeLayout rlayout = null;

    private TextView tvSetting;
    private TextView tvNickname;//昵称
    private TextView tvCareer;//职业
    private TextView tvBooking;//预约次数

    private TextView tvIncomeWeek;
    private TextView tvIncomeMonth;

    private CircleImageView ivIcon = null;
    private ImageView ivSex;
//    private View parentView;

    Observable bo;

    private MyBaseAdapter adapter = null;

    private static final int RESULT_CODE_UPDATE = 0x4f01;//修改成功返回的 响应码
    private static final int REQUEST_CODE_UPDATE = 0x4f00;//修改资料

    private static final int REQUEST_CODE_PERSONALINFO = 0xf041;
    private static final int RESULT_CODE_PERSONALINFO = 0xf042;

    //用户信息对象
    private UserLoginInfo userLoginInfo = null;
    //列表数据集合
    private String[] arr = null;
    //列表图标集合
    private int[] icons = {
            R.mipmap.mine_guanlituandui,
            R.mipmap.common_my_relations,
            R.mipmap.common_my_team,
            R.mipmap.common_my_work,
            R.mipmap.common_my_help,
            R.mipmap.common_my_vip
    };

    private Intent intent = null;
    private Bundle bundle = new Bundle();
    private DbUtils dbUtils;
    private ImageLoader imageLoader;
    protected DisplayImageOptions options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_fragment_my, container, false);
        userLoginInfo = BaseApplication.getInstance().getUser();
        initViews();
        initEvents();
        return parentView;
    }

    @Override
    protected void initViews() {
        mContext = getActivity();
        mActivity = (MainActivity)mContext;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.icon_edit_profile_default_2x)
                .showImageForEmptyUri(R.mipmap.icon_edit_profile_default_2x)
                .showImageOnFail(R.mipmap.icon_edit_profile_default_2x)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        dbUtils = BaseApplication.getInstance().getDbUtils();
        parentView.findViewById(R.id.tv_headcommon_headl).setVisibility(View.GONE);
        ((TextView)parentView.findViewById(R.id.tv_headcommon_headm)).setText(R.string.my_zoom);
        tvSetting = (TextView) parentView.findViewById(R.id.tv_headcommon_headr);
        tvSetting.setText(R.string.setting);

        myListview = (MyListview) parentView.findViewById(R.id.id_listView);
        rlayout = (RelativeLayout) parentView.findViewById(R.id.rlayout_my_personalinfo);
        ivIcon = (CircleImageView) parentView.findViewById(R.id.iv_my_usericon);
        ivSex = (ImageView) parentView.findViewById(R.id.iv_my_sex);

        tvNickname = (TextView) parentView.findViewById(R.id.tv_my_username);
        tvCareer = (TextView) parentView.findViewById(R.id.tv_my_career);
        tvBooking = (TextView) parentView.findViewById(R.id.tv_my_count);
        tvIncomeWeek = (TextView) parentView.findViewById(R.id.id_title_1);
        tvIncomeMonth = (TextView) findViewById(R.id.id_title_3);
        //拿到全局的用户信息对象
        if (userLoginInfo != null) {
            refreshData();
            try {
                if (!dbUtils.tableIsExist(Career.class) || dbUtils.findAll(Career.class).size() == 0){
                    getCareerData();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        arr = getResources().getStringArray(R.array.array_my_memulist);
        adapter = new MyBaseAdapter();
        myListview.setAdapter(adapter);

        intent = new Intent();
        if (userLoginInfo != null && !TextUtils.isEmpty(userLoginInfo.getHeadpic())){
            imageLoader.displayImage(userLoginInfo.getHeadpic(), ivIcon,options);//修改了头像加载方式
        }
    }

    private void getCareerData() {
        postData2(null, FunncoUrls.getCareerTypeListUrl(), false);
    }

    @Override
    protected void dataPostBack(String result, String url) {
        super.dataPostBack(result,url);
        JSONObject object = JsonUtils.getJObt(result, "params");
        if(object != null) {
            JSONArray jsonArray = JsonUtils.getJAry(object.toString(), "list");
            List<Career>ls = JSON.parseArray(jsonArray.toString(), Career.class);
            try {
                if (ls != null){
                    dbUtils.saveAll(ls);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initEvents() {
        myListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bundle.putString("info", arr[position]);
                Class cls = null;
                switch (position){
                    case 0://我的预约
                        cls = MyConventionActivity.class;
                        break;
                    case 1://通讯录
                        cls = MyCustomersActivity.class;
                        break;
                    case 2:
                        cls = TeamMyActivity.class;
                        break;
                    case 3:
                        cls = MyWorkActivity.class;
                        break;
                    case 4://使用帮助
                        cls = HelpActivity.class;
                        break;
                    case 5:
                        cls = UpdateAccountActivity.class;
                        break;
                }
                startActivity(cls,bundle);
            }
        });
        rlayout.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        parentView.findViewById(R.id.iv_my_qr_code).setOnClickListener(this);

        tvIncomeMonth.setOnClickListener(this);
        tvIncomeWeek.setOnClickListener(this);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onStart() {
        super.onStart();
//        refreshData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlayout_my_personalinfo:
                //跳转到
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("userLoginInfo", userLoginInfo);
                intent.putExtras(bundle);
                intent.putExtra("info", "修改资料");
//                intent.setClass(mContext, UpdateInfoActivity.class);
                intent.setClass(mContext, PersonalInfoActivity.class);//进入个人资料预览
                startActivityForResult(intent, REQUEST_CODE_PERSONALINFO);
                break;
            case R.id.tv_headcommon_headr:
                startActivity(SettingActivity.class);
                break;
            case R.id.iv_my_qr_code:
                startActivity(QR_CodeActivity.class);
                break;
            case R.id.id_title_1://周收入
                startActivity(IncomeWeekActivity.class);
                break;
            case R.id.id_title_3://月收入
                startActivity(IncomeWeekActivity.class);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("-----","回调了Fragment 的requestCode:"+requestCode+",resultCode:"+resultCode);
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_CODE_UPDATE){//修改资料成功返回的数据
            refreshData();
        }
        if (requestCode == REQUEST_CODE_PERSONALINFO && resultCode == RESULT_CODE_PERSONALINFO){//预览返回之后
            refreshData();
        }
    }

    private void refreshData() {
        userLoginInfo = BaseApplication.getInstance().getUser();
        if (userLoginInfo != null) {
            LogUtils.e("进行刷新 user不为空！！","");
            setData();
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setData(){
        if (userLoginInfo == null){
            showToast(R.string.user_err);
            return;
        }
        LogUtils.e("***", "用户信息是：" + userLoginInfo);
        imageLoader.displayImage(userLoginInfo.getHeadpic(), ivIcon,options);//用于修改数据后的更新网络图片
        tvNickname.setText(userLoginInfo.getNickname()+"");
        Drawable drawable = null;
        Drawable drawable2 = new BitmapDrawable();
        if (userLoginInfo.getSex().equals("女")){
            ivSex.setImageResource(R.mipmap.common_my_girl_icon);
        }else{
            ivSex.setImageResource(R.mipmap.common_my_boy_icon);
        }
        tvNickname.setCompoundDrawables(drawable2, drawable2, drawable, drawable2);
        tvCareer.setText(userLoginInfo.getCareer_name()+"");
        tvBooking.setText("预约数："+userLoginInfo.getBookings()+"");

    }

    class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arr.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_my,parent,false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            }else {
                holder = (Holder) convertView.getTag();
            }
            if (position == icons.length -1){
                holder.space.setVisibility(View.VISIBLE);
            }else{
                holder.space.setVisibility(View.GONE);
            }
            holder.ivIcon.setImageResource(icons[position]);
            holder.tvTitle.setText(arr[position]);
            return convertView;
        }

        class Holder{
            View space;
            TextView tvTitle = null;
            ImageView ivIcon = null;
            public Holder(View convertView){
                space = convertView.findViewById(R.id.v_item_my_space);
                tvTitle = (TextView) convertView.findViewById(R.id.tv_item_my_title);
                ivIcon = (ImageView) convertView.findViewById(R.id.iv_item_my_icon);
            }
        }
    }

    @Override
    public void onMainAction(String data) {

    }

    @Override
    public void onMainData(List<?>... list) {

    }
}
