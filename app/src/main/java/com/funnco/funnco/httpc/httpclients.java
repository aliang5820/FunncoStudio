package com.funnco.funnco.httpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class httpclients {
	private static DefaultHttpClient client = null;

	/** ��ʼhttpClinet */
	public static DefaultHttpClient initHttpClient() {
		if (client == null) {
			if (client == null) {
				BasicHttpParams localBasicHttpParams = new BasicHttpParams();
				HttpProtocolParams.setHttpElementCharset(localBasicHttpParams,
						"UTF-8");
				HttpProtocolParams.setVersion(localBasicHttpParams,
						HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(localBasicHttpParams,
						"UTF-8");
				HttpProtocolParams.setUseExpectContinue(localBasicHttpParams,
						true);
				HttpProtocolParams
						.setUserAgent(
								localBasicHttpParams,
								"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
				ConnManagerParams.setTimeout(localBasicHttpParams, 2000L);
				HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
						5000);
				HttpConnectionParams.setSoTimeout(localBasicHttpParams, 30000);
				SchemeRegistry localSchemeRegistry = new SchemeRegistry();
				localSchemeRegistry.register(new Scheme("http",
						PlainSocketFactory.getSocketFactory(), 80));
				localSchemeRegistry.register(new Scheme("https",
						SSLSocketFactory.getSocketFactory(), 443));
				client = new DefaultHttpClient(new ThreadSafeClientConnManager(
						localBasicHttpParams, localSchemeRegistry),
						localBasicHttpParams);
			}
			return client;
		}
		return client;
	}

	public static String testUpdate(String str, String ulrs) {
		String url ;
		url = ulrs;
		HttpClient client = initHttpClient();
		HttpPut put = new HttpPut(url);
		put.setHeader("Content-type", "application/json");
		StringEntity params;
		try {
			params = new StringEntity(str);

			put.setEntity(params);

			HttpResponse response = client.execute(put);
			System.out.println("Response Code:"
					+ response.getStatusLine().getStatusCode());
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			System.out.println("result:" + result);
			return result.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	/** 
     * HttpClient PUT请求 
     * @author huang 
     * @date 2013-4-10 
     * @return 
     */  
    @SuppressWarnings( "deprecation" )  
    public  static String doPut(String uri,String jsonObj){
		return jsonObj;}  
	/** ��������XML,JSON ,�Լ�����˽��Э������ */
	public static String getNetworkDataPost(String url) {
		DefaultHttpClient httpclient = initHttpClient();
		HttpPost httpRequest = new HttpPost(url);

		try {
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpRequest);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK) {
				HttpEntity httpEntity = response.getEntity();

				return EntityUtils.toString(httpEntity, "utf-8");
			} else {
				return null;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/** ��������XML,JSON ,�Լ�����˽��Э������ */
	public static String getNetworkDataGet(String url) {
		DefaultHttpClient httpclient = initHttpClient();

		try {
			HttpGet httpRequest = new HttpGet(url);
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpRequest);

			HttpEntity httpEntity = response.getEntity();
			String result = EntityUtils.toString(httpEntity, "utf-8");
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return e.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();

		}

	}

	/** ����ͼƬ */
	public static Bitmap loadImageFromInternet(String url) {
		Log.i("test", "Get net bitmap or other net file!" + url);
		Bitmap bitmap = null;
		DefaultHttpClient client = initHttpClient();
		HttpResponse response = null;
		InputStream inputStream = null;
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			response = client.execute(httpGet);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK) {
				// Log.d(TAG, "func [loadImage] stateCode=" + stateCode);
				return bitmap;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try {
					inputStream = entity.getContent();
					return bitmap = BitmapFactory.decodeStream(inputStream);
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
			Log.i("test", "load one bitmap sucseess");
		} catch (ClientProtocolException e) {
			httpGet.abort();
			e.printStackTrace();
		} catch (IOException e) {
			httpGet.abort();
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.i("test", "e:" + e);
		} finally {

		}
		return bitmap;
	}

	public static void closeClinet() {
		if (client != null) {

		}
	}
}
