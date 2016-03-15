package com.funnco.funnco.view.dialog;

import android.content.Context;

public class FlippingLoadingDialog extends BaseDialog {

	public FlippingLoadingDialog(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
//		setContentView(R.layout.layout_dialog_loading);
//		setDialogContentView(R.layout.layout_dialog_loading, true);
	}


	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}
}
