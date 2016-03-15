package com.funnco.funnco.httpc;

import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpClientUtils extends AsyncHttpResponseHandler {
	public static AsyncHttpClient mHttpClient = new AsyncHttpClient();
	public static Context mContext;
	static {
		// mHttpClient = new AsyncHttpClient();
		mHttpClient.setTimeout(20000);
		SSLSocketFactory sf = createSSLSocketFactory();
		if (sf != null) {
			mHttpClient.setSSLSocketFactory(sf);
		}

		Log.i("test", "SSLSocketFactory setCreate");
		HttpProtocolParams.setUseExpectContinue(mHttpClient.getHttpClient()
				.getParams(), false);
		// mHttpClient.set

	}

	public static SSLSocketFactory createSSLSocketFactory() {
		MySSLSocketFactory sf = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sf;
	}

	public static String HEADER_NAME = "user-token";

	public static void get(String url, BaseHttpClients handler) {

		mHttpClient.get(url, handler);

	}

	public static void get(String url, AsyncHttpResponseHandler handler,
			String token) {
		mHttpClient.addHeader(HEADER_NAME, token);
		mHttpClient.get(url, handler);

	}

	public static SchemeRegistry getSchemeRegistry() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new SSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			return registry;
		} catch (Exception e) {
			return null;
		}
	}

	public static void delete(String url, AsyncHttpResponseHandler handler,
			String token) {

		mHttpClient.addHeader(HEADER_NAME, token);
		mHttpClient.delete(url, handler);

	}

	public static void delete(String url, AsyncHttpResponseHandler handler,
			String token, String contentType) {

		mHttpClient.addHeader(HEADER_NAME, token);
		mHttpClient.addHeader("Content-Type", contentType);
		mHttpClient.delete(url, handler);

	}

	public static void get(String url, RequestParams params,
			BaseHttpClients handler) {
		mHttpClient.get(url, params, handler);

	}
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler handler) {
		mHttpClient.get(url, params, handler);

	}
	public static void post(String url, RequestParams params,
			BaseHttpClients handler) {
		mHttpClient.post(url, params, handler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler handler, String token) {
		if (token != null) {
			mHttpClient.addHeader(HEADER_NAME, token);
		}
		mHttpClient.post(url, params, handler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler handler, String token, String contentType) {
		mHttpClient.addHeader(HEADER_NAME, token);
		mHttpClient.addHeader("Content-Type", contentType);

		mHttpClient.post(url, params, handler);
	}

	public static void post(String url, AsyncHttpResponseHandler handler,
			String token) {
		mHttpClient.addHeader(HEADER_NAME, token);
		mHttpClient.post(url, handler);

	}

	public static void post(Context context, String url, HttpEntity entity,
			String textType, AsyncHttpResponseHandler handler, String token) {
		mHttpClient.addHeader(HEADER_NAME, token);
		mHttpClient.post(context, url, entity, textType, handler);

	}

	public static void post(String url, HttpEntity entity, String arg3,
			BaseHttpClients handler) {

		mHttpClient.post(mContext, url, entity, arg3, handler);

	}

	public static void post(String url, HttpEntity entity, String arg3,
			BaseHttpClients handler, String token) {
		mHttpClient.addHeader(HEADER_NAME, token);

		mHttpClient.post(mContext, url, entity, arg3, handler);

	}

	public static void put(String url, HttpEntity entity, String arg3,
			BaseHttpClients handler) {
		mHttpClient.put(mContext, url, entity, arg3, handler);
	}

	public static void put(String url, BaseHttpClients handler) {
		mHttpClient.put(url, handler);

	}

	public static void put(String url, RequestParams params,
			BaseHttpClients handler) {
		mHttpClient.put(url, params, handler);
	}
}
