package com.funnco.funnco.httpc;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.util.Log;

import com.funnco.funnco.interfaces.IJsonParsable;
import com.funnco.funnco.interfaces.LoadNetworkBack;
import com.loopj.android.http.RequestParams;


public class HttpHelper extends BaseHttpClients {
	public void getNetData(LoadNetworkBack<? extends IJsonParsable> cb,
			String url, IJsonParsable pa) {
		mBack = cb;
		mJsonP = pa;
		Log.i("test", "url:"+url);
		doRequst(url, null, 0);

	}
	public void getNetDataPost(LoadNetworkBack<? extends IJsonParsable> cb,
			String url, IJsonParsable pa,RequestParams param) {
		mBack = cb;
		mJsonP = pa;
		Log.i("test", "url:"+url);
		doRequst(url, param, 2);

	}
	public void getNetDataPost(LoadNetworkBack<? extends IJsonParsable> cb,
			String url, IJsonParsable pa,Context context,HttpEntity entity,String contentType) {
		mBack = cb;
		mJsonP = pa;
		mContext=context;
		Log.i("test", "url:"+url);
		post(url, entity, contentType, this);
	}
	public void getNetData(LoadNetworkBack<? extends IJsonParsable> cb,
			String url, IJsonParsable pa,RequestParams param) {
		mBack = cb;
		mJsonP = pa;
		Log.i("test", "url:"+url);
		doRequst(url, param, 1);

	}
	
	public void getNetDataPut(LoadNetworkBack<? extends IJsonParsable> cb,
			String url, IJsonParsable pa) {
		mBack = cb;
		mJsonP = pa;
		Log.i("test", "url:"+url);
		doRequst(url, null, 3);

	}
	
	public void getNetDataPut(LoadNetworkBack<? extends IJsonParsable> cb,
			String url, IJsonParsable pa,Context context,HttpEntity entity,String contentType) {
		mBack = cb;
		mJsonP = pa;
		mContext=context;
		Log.i("test", "url:"+url);
		put(url, entity, contentType, this);

	}
	
	public void getNetDataPut(LoadNetworkBack<? extends IJsonParsable> cb,
			String url, IJsonParsable pa,RequestParams param) {
		mBack = cb;
		mJsonP = pa;
		Log.i("test", "url:"+url);
	//	doRequst(url, param, 2);
		
		put(url, param,this);

	}
}
