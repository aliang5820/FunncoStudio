package com.funnco.funnco.activity.service;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.base.BaseActivity;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.bean.Serve;
import com.funnco.funnco.utils.string.Actions;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;

/**
 * 添加课程
 * Created by user on 2015/9/21.
 */
public class AddCoursesActivity extends BaseActivity {

    private View parentView;
    private EditText etCoursesname;
    private EditText etCoursesnumber;
    private EditText etCoursesnumber_2;
    private EditText etCoursesdesc;
    private TextView tvAheadtime;
    private Button btNext;

    private String strCourseName = "";
    private String strCourseNumber = "";
    private String strCourseNumber_2 = "";
    private String strRemindtime = "15";
    private String strCoursedesc = "";

    private Intent intent = null;
    private boolean isEdit = false;
    private boolean numberErr = false;
    private boolean isTeamService = false;
    private String team_id;
    private Serve courses;
    private String msg = "";

    private static final int REQUEST_CODE_AHEADTIME = 0xf601;
    private static final int RESULT_CODE_AHEADTIME = 0xf611;
    private static final int REQUEST_CODE_REPEADMODE_COURSES = 0xf602;
    private static final int RESULT_CODE_REPEADMODE_COURSES = 0xf612;
    private static final int RESULT_CODE_REPEADTYPEPREVIOUS = 0xf04;

    private static final int RESULT_CODE_SERVICE_COURSES_ADD = 0xf202;
    private static final int RESULT_CODE_SERVICE_COURSES_EDIT = 0xf212;

    private static final int RESULT_CODE_COURSES = 0xf17;//课程的添加和修改

    @Override
    protected void loadLayout() {
        super.loadLayout();
        parentView = getLayoutInflater().inflate(R.layout.layout_activity_addcourses, null);
        setContentView(parentView);
    }

    @Override
    protected void initView() {
        intent = getIntent();
        etCoursesname = (EditText) findViewById(R.id.et_addcourses_coursesname);
        etCoursesnumber = (EditText) findViewById(R.id.et_addcourses_coursesnumber);
        etCoursesnumber_2 = (EditText) findViewById(R.id.et_addcourses_coursesnumber_2);
        etCoursesdesc = (EditText) findViewById(R.id.et_addcourses_servicedescribe);
        tvAheadtime = (TextView) findViewById(R.id.tv_addcourses_aheadtime);
        btNext = (Button) findViewById(R.id.llayout_foot);
        btNext.setText(R.string.next);
        if (intent != null){
            String key = intent.getStringExtra(KEY);
            isTeamService = intent.getBooleanExtra("isTeamService", false);
            if (isTeamService){
                team_id = intent.getStringExtra("team_id");
            }
            if (!TextUtils.isNull(key)){
                courses = (Serve) BaseApplication.getInstance().getT(key);
                BaseApplication.getInstance().removeT(key);
                if (courses != null){
                    isEdit = true;
                    LogUtils.e("------","接收到的课程信息是："+courses);
                    initUI();
                }else{
                    courses = new Serve();
                    isEdit = false;
                }
            }else{
                intent = new Intent();
                courses = new Serve();
                isEdit = false;
            }
        }else{
            intent = new Intent();
            courses = new Serve();
            isEdit = false;
        }
        ((TextView)findViewById(R.id.tv_headcommon_headm)).setText(isEdit ? (isTeamService ? R.string.str_service_editcourses_team : R.string.str_service_courses_edit) : (isTeamService ? R.string.str_service_addcourses_team : R.string.str_service_addcourses));
        //初始化默认值
        if (!isEdit) {
            courses.setRemind_time(strRemindtime);
        }
    }
    private void initUI() {
        if (courses != null){
            String coursesName = courses.getService_name();
            if (!TextUtils.isNull(coursesName)){
                etCoursesname.setText(coursesName);
            }else{
                etCoursesname.setText("");
            }
            String coursesNumber = courses.getNumbers();
            if (!TextUtils.isNull(coursesNumber) && !TextUtils.equals("null",coursesNumber)){
                etCoursesnumber.setText(coursesNumber);
            }else{
                etCoursesnumber.setText("");
            }
            String coursesNumber_2 = courses.getMin_numbers();
            if (!TextUtils.isNull(coursesNumber_2) && !TextUtils.equals("null",coursesNumber_2)){
                etCoursesnumber_2.setText(coursesNumber_2);
            }else{
                etCoursesnumber_2.setText("");
            }
            //提前时间

            String coursesDesc = courses.getDescription();
            if (!TextUtils.isNull(coursesDesc) && !TextUtils.equals("null",coursesDesc)){
                etCoursesdesc.setText(coursesDesc);
            }else{
                etCoursesdesc.setText("");
            }
        }
    }

    @Override
    protected void initEvents() {
        findViewById(R.id.tv_headcommon_headl).setOnClickListener(this);
        tvAheadtime.setOnClickListener(this);
        btNext.setOnClickListener(this);
        //开课人数输入框做监听
        etCoursesnumber_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                strCourseNumber = etCoursesnumber.getText() + "";
                strCourseNumber_2 = s.toString();
                if (!TextUtils.isNull(strCourseNumber) && !TextUtils.isNull(strCourseNumber_2)) {
                    int num1 = Integer.parseInt(strCourseNumber);
                    int num2 = Integer.parseInt(strCourseNumber_2);
                    if (num1 < num2) {
                        numberErr = true;
                        showSimpleMessageDialog(R.string.str_service_coursesnumber_err);
//                        etCoursesnumber_2.setTextColor(Color.RED);
                    } else {
                        numberErr = false;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AHEADTIME && resultCode == RESULT_CODE_AHEADTIME){
            LogUtils.e("-----   ","接受到了返回数据");
            if (data != null){
                strRemindtime = data.getStringExtra("remind_time");
                if (!TextUtils.isNull(strRemindtime)){
                    courses.setRemind_time(strRemindtime);
                }
                int index = data.getIntExtra("index",0);
                switch (index){
                    case 0:
                        tvAheadtime.setText(R.string.str_notify_ahead_15);
                        break;
                    case 1:
                        tvAheadtime.setText(R.string.str_notify_ahead_30);
                        break;
                    case 2:
                        tvAheadtime.setText(R.string.str_notify_ahead_60);
                        break;
                    case 3:
                        tvAheadtime.setText(R.string.str_notify_ahead_120);
                        break;
                }
            }
        }else if(requestCode == REQUEST_CODE_REPEADMODE_COURSES && resultCode == RESULT_CODE_REPEADMODE_COURSES){//选择循环模式
            if (data != null){
                String key = data.getStringExtra(KEY);
                if (!TextUtils.isNull(key)){
                    courses = (Serve) BaseApplication.getInstance().getT(key);
                    BaseApplication.getInstance().removeT(key);
                    initUI();
                }
            }
        }else if (resultCode == RESULT_CODE_SERVICE_COURSES_ADD || resultCode == RESULT_CODE_SERVICE_COURSES_EDIT){//修改或添加成功
            //setResult.....
            setResult(RESULT_CODE_COURSES);
            finishOk();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headcommon_headl:
                finishOk();
                break;
            case R.id.tv_addcourses_aheadtime://
                startActivityForResult(new Intent(Actions.ACTION_CHOOSE_AHEADTIME), REQUEST_CODE_AHEADTIME);
                break;
            case R.id.llayout_foot://下一步 循环模式
                strCourseName = etCoursesname.getText()+"";
                strCourseNumber = etCoursesnumber.getText()+"";
                strCourseNumber_2 = etCoursesnumber_2.getText()+"";
                strCoursedesc = etCoursesdesc.getText()+"";
                courses.setService_name(strCourseName);
                courses.setNumbers(strCourseNumber);
                courses.setMin_numbers(strCourseNumber_2);
                courses.setRemind_time(strRemindtime);
                courses.setDescription(strCoursedesc);


                if (checkData()){
                    Intent intent2 = new Intent(Actions.ACTION_CHOOSE_REPEADMODE_COURSES);
                    intent2.putExtra(KEY, "courses");
                    intent2.putExtra("isEdit",isEdit);
                    intent2.putExtra("isTeamService", isTeamService);
                    if (isTeamService){
                        intent2.putExtra("team_id", team_id+"");
                    }
                    BaseApplication.getInstance().setT("courses", courses);
                    startActivityForResult(intent2, REQUEST_CODE_REPEADMODE_COURSES);
                }else{
                    showSimpleMessageDialog(msg);
                    return;
                }
                break;
        }
    }

    private boolean checkData(){
        if (TextUtils.isNull(strCourseName)){
            msg = getString(R.string.p_fillout_) + getString(R.string.str_service_coursesname);
            return false;
        }
        if (TextUtils.isNull(strCourseNumber)){
            msg = getString(R.string.p_fillout_) + getString(R.string.str_service_coursesnumber);
            return false;
        }
        if (TextUtils.isNull(strCourseNumber_2)){
            msg = getString(R.string.p_fillout_) + getString(R.string.str_service_coursesnumber_2);
            return false;
        }
        if (numberErr){
            msg = getString(R.string.str_service_coursesnumber_err);
            return false;
        }
        return true;
    }
}
