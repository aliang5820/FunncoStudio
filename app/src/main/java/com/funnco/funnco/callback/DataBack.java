package com.funnco.funnco.callback;

import android.graphics.Bitmap;

public interface DataBack {
	//接口能将数据传到调用该接口的地方
	//调用接口得到String数据
	public void getString(String result);
	//调用接口得到Bitmap数据
	public void getBitmap(String url,Bitmap bitmap);
}
