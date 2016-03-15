package com.funnco.funnco.view.layout;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.funnco.funnco.R;
import com.funnco.funnco.utils.support.Constant;
import com.funnco.funnco.view.imageview.ImageText;

import java.util.ArrayList;
import java.util.List;

public class BottomControlPanel extends RelativeLayout implements View.OnClickListener {
	  private Context mContext;
	  private ImageText mMsgBtn = null;
	  private ImageText mContactsBtn = null;
	  private ImageText mNewsBtn = null;
	  private ImageText mSettingBtn = null;
	  private int DEFALUT_BACKGROUND_COLOR = Color.rgb(243, 243, 243); //Color.rgb(192, 192, 192)
	  private BottomPanelCallback mBottomCallback = null;
	  private List<ImageText> viewList = new ArrayList<ImageText>();

	  /**
	   * 用于回调给主Activity 传递选中的项
	   * @author user
	   *
	   */
	  public interface BottomPanelCallback{
	    public void onBottomPanelClick(int itemId);
	  }
	  public BottomControlPanel(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    // TODO Auto-generated constructor stub
	  }
	  @Override
	  protected void onFinishInflate() {
	    mMsgBtn = (ImageText)findViewById(R.id.btn_schedule);
	    mContactsBtn = (ImageText)findViewById(R.id.btn_service);
	    mNewsBtn = (ImageText)findViewById(R.id.btn_upload);
	    mSettingBtn = (ImageText)findViewById(R.id.btn_my);
	    setBackgroundColor(DEFALUT_BACKGROUND_COLOR);
	    viewList.add(mMsgBtn);
	    viewList.add(mContactsBtn);
	    viewList.add(mNewsBtn);
	    viewList.add(mSettingBtn);

	  }
	  public void initBottomPanel(){
	    if(mMsgBtn != null){
	      mMsgBtn.setImage(R.mipmap.common_schedule_unselect);
	      mMsgBtn.setText(getResources().getString(R.string.schedule));
	    }
	    if(mContactsBtn != null){
	      mContactsBtn.setImage(R.mipmap.common_message_unselect);
	      mContactsBtn.setText(getResources().getString(R.string.message));
	    }
	    if(mNewsBtn != null){
	      mNewsBtn.setImage(R.mipmap.common_service_unselect);
	      mNewsBtn.setText(getResources().getString(R.string.service));
	    }
	    if(mSettingBtn != null){
	      mSettingBtn.setImage(R.mipmap.common_my_unselect);
	      mSettingBtn.setText(getResources().getString(R.string.my));
	    }
	    setBtnListener();

	  }
	  private void setBtnListener(){
	    int num = this.getChildCount();
	    for(int i = 0; i < num; i++){
	      View v = getChildAt(i);
	      if(v != null){
	        v.setOnClickListener(this);
	      }
	    }
	  }
	  public void setBottomCallback(BottomPanelCallback bottomCallback){
	    mBottomCallback = bottomCallback;
	  }
	  @Override
	  public void onClick(View v) {
	    // TODO Auto-generated method stub
	    initBottomPanel();
	    int index = -1;
	    switch(v.getId()){
	    case R.id.btn_schedule:
	      index = Constant.BTN_FLAG_SCHEDULE;
	      mMsgBtn.setChecked(Constant.BTN_FLAG_SCHEDULE);
	      break;
	    case R.id.btn_service:
	      index = Constant.BTN_FLAG_SERVICE;
	      mContactsBtn.setChecked(Constant.BTN_FLAG_SERVICE);
	      break;
	    case R.id.btn_upload:
	      index = Constant.BTN_FLAG_UPLOAD;
	      mNewsBtn.setChecked(Constant.BTN_FLAG_UPLOAD);
	      break;
	    case R.id.btn_my:
	      index = Constant.BTN_FLAG_MY;
	      mSettingBtn.setChecked(Constant.BTN_FLAG_MY);
	      break;
	    default:break;
	    }
	    if(mBottomCallback != null){
	      mBottomCallback.onBottomPanelClick(index);
	    }
	  }
	  public void defaultBtnChecked(){
	    if(mMsgBtn != null){
	      mMsgBtn.setChecked(Constant.BTN_FLAG_SCHEDULE);
	    }
	  }
	  @Override
	  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	    // TODO Auto-generated method stub
	    super.onLayout(changed, left, top, right, bottom);
	    layoutItems(left, top, right, bottom);
	  }
	  /**
	   * @param left
	   * @param top
	   * @param right
	   * @param bottom
	   */
	  private void layoutItems(int left, int top, int right, int bottom){
	    int n = getChildCount();
	    if(n == 0){
	      return;
	    }
	    int paddingLeft = getPaddingLeft();
	    int paddingRight = getPaddingRight();
	    Log.i("yanguoqi", "paddingLeft = " + paddingLeft + " paddingRight = " + paddingRight);
	    int width = right - left;
	    int height = bottom - top;
	    Log.i("yanguoqi", "width = " + width + " height = " + height);
	    int allViewWidth = 0;
	    for(int i = 0; i< n; i++){
	      View v = getChildAt(i);
	      Log.i("yanguoqi", "v.getWidth() = " + v.getWidth());
	      allViewWidth += v.getWidth();
	    }
	    int blankWidth = (width - allViewWidth - paddingLeft - paddingRight) / (n - 1);
	    Log.i("yanguoqi", "blankV = " + blankWidth );

	    LayoutParams params1 = (LayoutParams) viewList.get(1).getLayoutParams();
	    params1.leftMargin = blankWidth;
	    viewList.get(1).setLayoutParams(params1);

	    LayoutParams params2 = (LayoutParams) viewList.get(2).getLayoutParams();
	    params2.leftMargin = blankWidth;
	    viewList.get(2).setLayoutParams(params2);
	  }



	}
