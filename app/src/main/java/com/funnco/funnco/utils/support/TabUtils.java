package com.funnco.funnco.utils.support;

//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.funnco.funnco.utils.log.LogUtils;

import java.util.List;


/**
 * @ClassName: TabUtils
 * @说明:
 * @author Shawn
 * @date 2015-05-13
 */
public class TabUtils implements RadioGroup.OnCheckedChangeListener {
	
	private List<Fragment> fragments; // 一个tab页面对应一个Fragment
//	private List<View> list;//用户存放头部控件的集合
	private RadioGroup rgs; // 用于切换tab
	private FragmentManager fragmentManager; // Fragment所属的Activity
	private int fragmentContentId; // Activity中所要被替换的区域的id
	private int currentTab; // 当前Tab页面索引
	private int checkedColor,unCheckedColor;
	private Context context;
	private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener; // 用于让调用者在切换tab时候增加新的功能

	public TabUtils(FragmentManager fragmentManager,
					List<Fragment> fragments, int fragmentContentId,
					RadioGroup rgs ,int checkedColor,int unCheckedColor,Context context) {
		this.fragments = fragments;
		this.rgs = rgs;
		this.fragmentManager = fragmentManager;
		this.fragmentContentId = fragmentContentId;
		this.checkedColor = checkedColor;
		this.unCheckedColor = unCheckedColor;
		this.context = context;
//		this.list = list;
		// 默认显示第一页
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(fragmentContentId, fragments.get(0),"fragment0");
//		ft.replace(fragmentContentId, fragments.get(0), "fragment0");
		((RadioButton)rgs.getChildAt(0)).setChecked(true);
		ft.commit();
		rgs.setOnCheckedChangeListener(this);
	}

	//当点击切换的时候
	
	@Override
   public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		LogUtils.e("*****", "onCheckedChanged----wsf");
		//遍历radioGroup
		for (int i = 0; i < rgs.getChildCount(); i++) {
			if (rgs.getChildAt(i).getId() == checkedId) {//选中
				LogUtils.e("*****","onCheckedChanged----wsf选中");
				((RadioButton)(rgs.getChildAt(i))).setTextColor(context.getResources().getColor(checkedColor));//"#e46331"
				Fragment fragment = fragments.get(i);
				LogUtils.e("Fragment的状态是：","isAdded:"+fragment.isAdded()+
						" ,isDetached:"+fragment.isDetached()+" ,isResumed:"+fragment.isResumed()+
						" ,isHidden:"+fragment.isHidden()+" ,isVisible"+fragment.isVisible()+
						" ,isInLayout"+fragment.isInLayout()+" ,isRemoving"+fragment.isRemoving());
				//给fragment切换的时候添加一个动画
				FragmentTransaction ft = obtainFragmentTransaction(i);
//				 getCurrentFragment().onPause(); // 暂停当前tab
				getCurrentFragment().onStop(); // 暂停当前tab***
				//判断当前的fragment是否加入到
				if (fragment.isAdded()) {
					fragment.onStart(); // 启动目标tab的fragment onStart()
//					 fragment.onResume(); // 启动目标tab的onResume()
				} else {
//					ft.replace(fragmentContentId, fragment,"fragment"+i);
					ft.add(fragmentContentId, fragment,"fragment"+i);
					ft.commit();
//					//根据i替换的列表中view内容
				}
//				changeContent(i);
				showTab(i); // 显示目标tab

				// 如果设置了切换tab额外功能功能接口
				if (null != onRgsExtraCheckedChangedListener) {
					onRgsExtraCheckedChangedListener.OnRgsExtraCheckedChanged(ft,radioGroup, checkedId, i);
				}

			}else{
				LogUtils.e("*****", "onCheckedChanged----wsf未选中");
				((RadioButton)(rgs.getChildAt(i))).setTextColor(context.getResources().getColor(unCheckedColor));//Color.parseColor("#9fa7b0")
			}
		}
	}

	/**
     * 
    * Title: showTab
    * Description:
    * @param idx
     */
	private void showTab(int idx) {
		LogUtils.e("***","***showTab-----wsf");
		for (int i = 0; i < fragments.size(); i++) {
			LogUtils.e("***","***showTab-----wsf---遍历fragments");
			Fragment fragment = fragments.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(idx);
			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commit();
		}
		currentTab = idx; // 更新目标tab为当前tab
	}

	/**
	 * 获取一个带动画的FragmentTransaction
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		// 设置切换动画，当前选中大于
//		if (index > currentTab) {
//			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
//		} else {
//			ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
//		}
		return ft;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public Fragment getCurrentFragment() {
		return fragments.get(currentTab);
	}

	public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
		return onRgsExtraCheckedChangedListener;
	}
	
	public void setOnRgsExtraCheckedChangedListener(OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
		this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
	}

	/**
	 * 切换tab额外功能功能接口
	 */
	public interface OnRgsExtraCheckedChangedListener {
		void OnRgsExtraCheckedChanged(FragmentTransaction fragmentTransaction,RadioGroup radioGroup, int checkedId, int index);
	}

}
