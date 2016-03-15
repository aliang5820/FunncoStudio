package com.funnco.funnco.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.funnco.funnco.R;

import java.util.ArrayList;
import java.util.List;

public class ValuesPickerDialog extends Dialog {

	private Context mContext = null;
	private TextView mTvTitle = null;
	private NumberPicker mNp = null;
	private Button mBtnCancle = null;
	private Button mBtnDone = null;
	private List<String> list = new ArrayList<String>();
	private OnValuesPickerListener onValuesPickerListener = null;
	private View view = null;
	public ValuesPickerDialog(Context context) {
		this(context,0);
	}

	public ValuesPickerDialog(Context context,int style) {
		super(context, R.style.customDialog);
		this.mContext = context;
		view = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_valuespicker, null);
		initView(view);

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(view);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		Resources resources = mContext.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();	
		params.height = (int) (dm.heightPixels * 0.5);
		params.width = (int) (dm.widthPixels * 0.9);
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);		
	}
	
	/**
	 * 初始化控件
	 */
	private void initView(View mView) {
//		mTvTitle = (TextView) mView.findViewById(R.id.tv_vp_title);
		mNp = (NumberPicker) mView.findViewById(R.id.np_activity_pw_career);
		mBtnCancle = (Button) mView.findViewById(R.id.bt_activity_pw_cancle);
		mBtnDone = (Button) mView.findViewById(R.id.bt_activity_pw_ok);
		for (int i = 0; i < mNp.getChildCount(); i++) {
			View view = mNp.getChildAt(i);
			if (view instanceof EditText) {
				Log.e("valu  ", "i = "+i+"   *****");
	            ((EditText) view).setFocusable(false);
			}
			
		}
		mBtnCancle.setOnClickListener(onClickListener);
		mBtnDone.setOnClickListener(onClickListener);
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.bt_activity_pw_cancle) {
				ValuesPickerDialog.this.dismiss();
			} else if (v.getId() == R.id.bt_activity_pw_ok) {
				if (list != null && list.size() > 0  && onValuesPickerListener != null) {
					Log.e("valu", list.get(mNp.getValue()));	
					onValuesPickerListener.onPickerValues(list.get(mNp.getValue()));
				}
				ValuesPickerDialog.this.dismiss();
			}
		}
	};
	
	/**
	 * 设置dialog title
	 */
	public void setTitleText(String str) {
		mTvTitle.setText(str);
	}
	
	/**
	 * 设置显示数据
	 * @param _list
	 */
	public void setShowValues(List<String> _list) {
		if ((_list == null) || !(_list.size() > 0)) {
			return;
		}
		String[] strs = new String[_list.size()];
		for (int i = 0; i < _list.size(); i++) {
			list.add(_list.get(i));
			strs[i] = _list.get(i);
		}
		mNp.setMinValue(0);
		mNp.setMaxValue(list.size()-1);	
		mNp.setDisplayedValues(strs);
	}
	
	/**
	 * 设置picker返回值监听
	 * @param _onValuesPickerListener
	 */
	public void setOnValuesPickerListener(OnValuesPickerListener _onValuesPickerListener) {
		this.onValuesPickerListener = _onValuesPickerListener;
	}
	
	public interface OnValuesPickerListener {
		public void onPickerValues(String value);
	}
}
