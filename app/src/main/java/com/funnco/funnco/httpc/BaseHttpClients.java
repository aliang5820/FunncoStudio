package com.funnco.funnco.httpc;

import org.json.JSONException;

import android.util.Log;

import com.funnco.funnco.interfaces.IJsonParsable;
import com.funnco.funnco.interfaces.LoadNetworkBack;
import com.loopj.android.http.RequestParams;


public class BaseHttpClients extends HttpClientUtils {
	@SuppressWarnings("rawtypes")
	public LoadNetworkBack mBack;
	public IJsonParsable mJsonP = null;

	@Override
	public void onFailure(Throwable arg0, String arg1) {
		// TODO Auto-generated method stub
		super.onFailure(arg0, arg1);
		setOnFailure(arg1);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		super.onFailure(arg0);
	}

	@Override
	public void onSuccess(int statusCode, String result) {
		// TODO Auto-generated method stub
		super.onSuccess(statusCode, result);
		switch (statusCode) {
		case 200:
			sendResult(result);
			break;
		case 401:
			setOnFailure("error cod 401 验证错误！");
			break;
		case 403:
			setOnFailure("error cod 403 没有找到链接");
			break;
		default:
			setOnFailure("网页返回码:" + statusCode + "  结果数据result:" + result);

			break;
		}
	}

	@Override
	public void onSuccess(String result) {
		// TODO Auto-generated method stub
		super.onSuccess(result);

	}

	/**
	 * type 为0时为get单url，1时为带参数geturl请求 type 为2时 为post请求
	 * 
	 * @param url
	 * @param param
	 * @param type
	 */
	public void doRequst(String url, RequestParams param, int type) {
		switch (type) {
		case 0:
			get(url, this);
			break;
		case 1:
			get(url, param, this);
			break;
		case 2:
			post(url, param, this);
			break;
		case 3:
			put(url, this);
			break;
		}

	}

	public void setOnFailure(Object obj) {
		if (mBack != null) {
			mBack.Failure(obj);
		}
	}

	@SuppressWarnings("unchecked")
	public void sendResult(String result) {
		Log.i("test", "result:" + result);
		if (mBack != null) {
			HttpResponse response;
			try {
				response = new HttpResponse(result);
				mJsonP.readFromJson(response);
				mBack.Suceess(mJsonP);
			} catch (JSONException e) {

				mBack.otherData(result);
				e.printStackTrace();
			}
		}
	}
}
