package com.funnco.funnco.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.com.funnco.funnco.callback.DataBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.bimp.BitmapUtils;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.utils.http.VolleyUtils;

public class ViewHolder {
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;
	private VolleyUtils volleyUtils;
	private ViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
		volleyUtils = new VolleyUtils(context);
//		bitmapUtils = new BitmapUtils(context, Environment.getExternalStorageDirectory().getAbsolutePath(), Runtime.getRuntime().maxMemory()/8);
	}

	/**
	 * 拿到一个ViewHolder对象
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
			ViewGroup parent, int layoutId, int position) {
		if (convertView == null) {
			return new ViewHolder(context, parent, layoutId, position);
		}
		return (ViewHolder) convertView.getTag();
	}

	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 *
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 *
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		if (TextUtils.isNull(text)) {
			view.setText("");
		}else{
			view.setText(text);
		}
		return this;
	}

	public ViewHolder setText(int viewId,int resId){
		TextView textView = getView(viewId);
		textView.setText(resId);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 *
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 为ImageView设置图片
	 *
	 * @param viewId
	 * @param
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * @param viewId
	 * @param drawable
	 * @return
	 */
	public ViewHolder setImageDrawable(int viewId, Drawable drawable){
		ImageView view = getView(viewId);
		view.setImageDrawable(drawable);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 *
	 * @param viewId
	 * @param
	 * @return
	 */
	public ViewHolder setImageByUrl(final int viewId, String url) {


		volleyUtils.imageRequest(url, 0, 0, new DataBack() {

			@Override
			public void getString(String result) {

			}

			@Override
			public void getBitmap(String rul,Bitmap bitmap) {
				ImageView view = getView(viewId);
				if (bitmap != null) {
					Bitmap bt = BitmapUtils.getCircleBitmap2(bitmap, R.mipmap.icon_edit_profile_many_pic_2x);
					view.setImageBitmap(bt);
				}
			}
		});
//		bitmapUtils.display(getView(viewId), url);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 *
	 * @param viewId
	 * @param
	 * @return
	 */
	public ViewHolder setImageByUrl2(final int viewId, String url) {
		ImageView view = getView(viewId);
		volleyUtils.imageLoader(url,view);
		return this;
	}

	public int getPosition() {
		return mPosition;
	}

	/**
	 * 设置监听方法，根据之前打上的tag标记进行确定位置R.id.ib_item_work_delete
	 * @param resId
	 * @param post
	 */
	public void setListener(int resId,final Post post){
		View ib = getView(resId);
		ib.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				post.post(Integer.parseInt((String) getConvertView().findViewById(R.id.ib_item_work_delete).getTag()));
			}
		});
	}

	/**
	 * CheckBox 设置监听事件
	 * @param resId
	 * @param post
	 */
	public void setListenerCb(final int resId,final Post post){
		CheckBox cb = getView(resId);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				post.post(Integer.parseInt((String) getConvertView().findViewById(resId).getTag()));
			}
		});
	}

	public void setCommonListener(int resId,final Post post){
		View tagView = getView(resId);
		tagView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String tag = (String) v.getTag();
				LogUtils.e("设置监听；；；；","setCommonListener-----tag="+tag);
				post.post(Integer.parseInt(tag));
			}
		});
	}

}