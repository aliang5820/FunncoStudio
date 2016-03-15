package com.funnco.funnco.utils.http;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;

import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.utils.bimp.BitmapUtils;
import com.funnco.funnco.utils.file.SharedPreferencesUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyHttpUtils {
	/**
	 * Post 请求
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url, Map<String, Object> params)
			throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		//此处上传cookies
		if (BaseApplication.getInstance().getCookieStore() != null){
			client.setCookieStore(BaseApplication.getInstance().getCookieStore());
		}
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			NameValuePair nameValuePair = new BasicNameValuePair(
					entry.getKey(), (String) entry.getValue());
			nameValuePairs.add(nameValuePair);
		}

		UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(
				nameValuePairs, HTTP.UTF_8);
		//设置超时时间
		HttpParams params1 = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params1, 5000);
		HttpConnectionParams.setSoTimeout(params1, 5000);
		HttpPost post = new HttpPost(url);
//		Header header = (Header) new RequestDefaultHeaders();
//		post.addHeader(header);
		post.setEntity(encodedFormEntity);
		HttpResponse response = client.execute(post);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			List<Cookie> list = ((AbstractHttpClient)client).getCookieStore().getCookies();
//			List<HttpCookie> list = cookieStore.getCookies();
			for (int i = 0; i < list.size(); i++) {
				//保存cookie
				Cookie cookie  = list.get(i);
				LogUtils.e("获取的额Cookie", cookie.getName() + " = " + cookie.getValue());
			}
			//当全局变量中的cookie为空时进行获取和保存
			CookieStore store = client.getCookieStore();
			BaseApplication.getInstance().setCookieStore(store);
			SharedPreferencesUtils.saveCookies(client);
			return EntityUtils.toString(entity);
		}
		return null;
	}
	/**
	 * get请求
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String url) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		if (BaseApplication.getInstance().getCookieStore() != null){
			client.setCookieStore(BaseApplication.getInstance().getCookieStore());
		}
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		HttpGet get = new HttpGet(url);
		HttpResponse response=null;
		try {
			 response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (BaseApplication.getInstance().getCookieStore() == null){
					CookieStore store = client.getCookieStore();
					BaseApplication.getInstance().setCookieStore(store);
				}
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}
		} catch (SocketTimeoutException e) {
			// TODO: handle exception
		    LogUtils.e("tag","网络超时。。。");
		}
		return null;
	}
	/**
	 * 网络获取图片
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFormUrl(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		// 设置超时时间
		HttpConnectionParams.setConnectionTimeout(new BasicHttpParams(),
				6 * 1000);
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				// Bitmap bitmap =
				// BitmapFactory.decodeStream(entity.getContent());
				//将获得的图片进行二次采样，避免大图片占用内存
				Bitmap bitmap = BitmapUtils.decodeBitmap(
						EntityUtils.toByteArray(entity), 200, 200);
				return bitmap;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static HttpHandler handler2 = null;

	/**
	 * 下载文件
	 * @param url 文件链接
	 * @param target 目标地址
	 * @param handler 用于更新的 handler
	 * @param context 当前上下文环境
	 */
	public static void downLoad(String url, String target, final Handler handler,final Context context) {
		HttpUtils http = new HttpUtils();
		final File file = new File(target);
		handler2 = http.download(url, target, true, true,
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						LogUtils.e("-----onSuccess-----","");
						handler.sendEmptyMessage(100);
						openFile(context, file);
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						LogUtils.e("-----onFailure-----","arg1:"+arg1);
						handler.sendEmptyMessage(101);
					}

					@Override
					public void onLoading(long total, long current,
										  boolean isUploading) {
						LogUtils.e("-----onLoading-----total=","  current="+current);
						super.onLoading(total, current, isUploading);
						handler.sendEmptyMessage((int)(((float)current/(float)total)*100));

					}
				});
	}

	public static void stop(){
		if(handler2 != null){
			handler2.cancel();
		}
	}

	// 打开APK程序代码

	private static void openFile(Context context, File file) {
		LogUtils.e("OpenFile", file.getName());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
