package com.funnco.funnco.utils.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.funnco.funnco.R;
import com.funnco.funnco.application.BaseApplication;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.utils.log.LogUtils;

import java.util.Map;

/**
 * 使用volley请求数据,下载图片
 * 
 * @author Shawn
 * 
 */
public class VolleyUtils {
	// 这个可以添加所有的Request请求
	public static RequestQueue requestQueue;
	// 运行内存中的缓存
	public LruCache<String, Bitmap> lruCache;

	private Context activity;

	public VolleyUtils(Context activity) {
		this.activity = activity;
		// 此方法是为了让queue的生命周期和应用一样长
		if(requestQueue == null){
			requestQueue = Volley.newRequestQueue(BaseApplication.getInstance());
			initCache();
		}
		if (lruCache == null){
			initCache();
		}
	}

	/**
	 * 请求方法为get
	 * 
	 * @param url
	 * @param callBack
	 */
	// 请求方法为GET
	public void stringRequest(String url, final DataBack callBack) {
		// 参数:1.请求方式 2.请求地址 3.成功后的接口回调 4.错误的结果返回(接口)

		StringRequest stringRequest = new StringRequest(Method.GET, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (!TextUtils.isEmpty(response)) {
							callBack.getString(response);
						}
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});
		// 3.将StringRequest加入到请求队列中,就是加入queue
		stringRequest.setTag(activity);
		requestQueue.add(stringRequest);
	}

	/**
	 * 请求方法为post
	 * 
	 * @param url
	 * @param map
	 * @param callBack
	 */
	// 请求方法为post(需要重写:getParams() 方法,因为post请求是键值对组合到一起的)
	public void stringRequestPost(String url, final Map<String, String> map,
			final DataBack callBack) {
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (!TextUtils.isEmpty(response)) {
							callBack.getString(response);
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
					}
				}) {
			// post请求需要去重写的方法,需要根据接口文档的信息区上传键值对
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return map;
			}
		};
		stringRequest.setTag(activity);
		requestQueue.add(stringRequest);

	}

	/**
	 * 获取图片
	 * 
	 * @param url
	 * @param maxWidth 最大宽度,0为不变
	 * @param maxHeight 最大高度,0为不变
	 * @param callBack
	 */
	// 获取图片
	public void imageRequest(final String url, int maxWidth, int maxHeight,
			final DataBack callBack) {
		// 参数: 1.url代表图片的地址 2.正确图片结果返回 3.maxWidth可以设置图片最大的宽度
		// 4.可以设置图片最大高度 5.图片显示参数的设置 6.请求错误的返回
		// 注意:如果最大宽度或者最大高度是0,说明是图片原本的高度宽度

		ImageRequest imageRequest = new ImageRequest(url,
				new Listener<Bitmap>() {

					@Override
					public void onResponse(Bitmap response) {
						if (response != null) {
							//将图片地址也一同回传
							callBack.getBitmap(url,response);
						}
					}
				}, maxWidth, maxHeight, Config.RGB_565, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						LogUtils.e("imageRequest ", "--onErrorResponse--");
					}
				});
		// 打tag的原因是因为如果要销毁这个请求,就要用标记去销毁
//		imageRequest.setTag(activity);
//		imageRequest.setTag(url);
		imageRequest.setTag("imageRequest");
		requestQueue.add(imageRequest);
	}

	// 首先要得到lruCache的容量,因为接下来的两个获取图片的方法需要用到
	private void initCache() {
		// 分配运行内存的1/8给lruCache
		int size = (int) (Runtime.getRuntime().maxMemory() / 8);
		lruCache = new LruCache<String, Bitmap>(size) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 返回每个图片的大小,计算运行内存的容量
//				if (value != null) {
				return value.getByteCount();
//					return value.getRowBytes() * value.getHeight();
//				} else {
//					return 0;
//				}
//				return value.getByteCount();
			}
		};
	}

	/**
	 * 使用imageLoader下载图片
	 * 
	 * @param url
	 * @param imageView
	 */

	// 获取图片,使用imageLoader
	public void imageLoader(String url, ImageView imageView) {
		// 1.建立一个imageLoader的对象
		// 2.通过imageLoader这个对象,将imageListener对象构造出来
		// 3.通过imageLoader这个方法去得到图片
		// 参数: 1.定义好的RequestQueue 2.可以做运行内存缓存
		// 先调用此方法得到lruCache容量
		ImageLoader imageLoader = new ImageLoader(requestQueue,
				new ImageCache() {

					@Override
					public void putBitmap(String url, Bitmap bitmap) {
//						if (bitmap != null && lruCache.get(url) != null) {
//							Bitmap bm = BitmapUtils.getCircleBitmap(bitmap,150,150);
//							lruCache.put(url, bm);
//						}
						if (bitmap != null) {
//							Bitmap bm = BitmapUtils.getCircleBitmap(bitmap,150,150);
							lruCache.put(url, bitmap);
						}
					}

					@Override
					public Bitmap getBitmap(String url) {
						return lruCache.get(url);
					}
				});
		// 2.通过imageLoader这个对象,将imageListener对象构造出来
		// 第二个参数,显示默认图片,第三个参数,下载失败或错误显示的图片
		ImageListener imageListener = ImageLoader.getImageListener(imageView,
				R.mipmap.icon_edit_profile_default_2x,
				R.mipmap.icon_edit_profile_default_2x);
		// 3.通过imageLoader这个方法去得到图片
		imageLoader.get(url, imageListener);
	}

	// 获取图片,使用imageLoader
	public void imageLoader2(String url, ImageView imageView, int defaultImageId,int loadFailureImageId) {
		// 1.建立一个imageLoader的对象
		// 2.通过imageLoader这个对象,将imageListener对象构造出来
		// 3.通过imageLoader这个方法去得到图片
		// 参数: 1.定义好的RequestQueue 2.可以做运行内存缓存
		// 先调用此方法得到lruCache容量
		ImageLoader imageLoader = new ImageLoader(requestQueue,
				new ImageCache() {

					@Override
					public void putBitmap(String url, Bitmap bitmap) {
//						if (lruCache.get(url) != null)
						lruCache.put(url, bitmap);
//						lruCache.put(url, BitmapUtils.getCircleBitmap2(bitmap,R.mipmap.icon_edit_profile_default_2x));
					}

					@Override
					public Bitmap getBitmap(String url) {
						return lruCache.get(url);
					}
				});
		// 2.通过imageLoader这个对象,将imageListener对象构造出来
		// 第二个参数,显示默认图片,第三个参数,下载失败或错误显示的图片
		ImageListener imageListener = ImageLoader.getImageListener(imageView,defaultImageId,loadFailureImageId);
		// 3.通过imageLoader这个方法去得到图片
		imageLoader.get(url, imageListener);
	}

	/**
	 * NetworkImageView 的使用
	 * 
	 * @param networkImageView
	 */
	// NetworkImageView 的使用
	public void netWorkImageView(NetworkImageView networkImageView, String url) {
		initCache();
		// 设置 networkImageView 的默认背景图
		networkImageView.setDefaultImageResId(R.mipmap.launch_icon);
		networkImageView
				.setErrorImageResId(R.mipmap.launch_icon);
		// 如果下载错误,设置 networkImageView 的背景图
//		String url = "http://ww1.sinaimg.cn/bmiddle/6b7c4cf3gw1eq7k0htfszj20zk1hcn7s.jpg";
		ImageLoader imageLoader = new ImageLoader(requestQueue,
				new ImageCache() {

					@Override
					public void putBitmap(String url, Bitmap bitmap) {
						lruCache.put(url, bitmap);
					}

					@Override
					public Bitmap getBitmap(String url) {
						return lruCache.get(url);
					}
				});
		networkImageView.setImageUrl(url, imageLoader);
	}
	/**
	 * 清除缓存
	 */
	public void clearCache(){
		final Cache cache = requestQueue.getCache();
		ClearCacheRequest clearCacheRequest = new ClearCacheRequest(cache, new Runnable() {
			
			@Override
			public void run() {
				cache.clear();
			}
		});
		requestQueue.add(clearCacheRequest);
	}

	public void clearTask(){
		requestQueue.cancelAll(activity);
		requestQueue.stop();
	}
}
