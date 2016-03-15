package com.funnco.funnco.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.funnco.funnco.R;


public class LoadingDialog extends Dialog {
	private TextView tv;

	public LoadingDialog(Context context) {
		super(context, R.style.loadingDialogStyle);
	}

	private LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		tv = (TextView) this.findViewById(R.id.tv);
		
		LinearLayout linearLayout = (LinearLayout) this
				.findViewById(R.id.LinearLayout);
		linearLayout.getBackground().setAlpha(210);
		
		this.setCanceledOnTouchOutside(false);
	}
	
	public void setStateName(String str) {
		Log.i("test", "info:"+tv);
		if(tv!=null)
		tv.setText(str);
		
	}
}
