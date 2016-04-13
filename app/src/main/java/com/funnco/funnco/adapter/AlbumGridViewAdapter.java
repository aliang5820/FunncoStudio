package com.funnco.funnco.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.callback.ImageCallBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.bimp.BitmapCache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 *
 * @version 2015年5月27日17:35:35
 */
public class AlbumGridViewAdapter extends BaseAdapter{
	final String TAG = getClass().getSimpleName();
	private Context mContext;
	private ArrayList<ImageItem> dataList;//所有图片的集合
	private ArrayList<ImageItem> selectedDataList;//选中图片的集合，从外部出入---Bimp.tempSelectBitmap
	//用来存储已选择的图片
	private HashMap<String,String> tempDesign = new HashMap<>();
	private DisplayMetrics dm;
	private BitmapCache cache;
	private Post post;
	
	
	public AlbumGridViewAdapter(Context c, ArrayList<ImageItem> dataList,
			ArrayList<ImageItem> selectedDataList,Post post) {
		mContext = c;
		this.post = post;
		cache = new BitmapCache();
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
	}

	public int getCount() {
		return dataList.size();
	}

	public Object getItem(int position) {
		return dataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	ImageCallBack callback = new ImageCallBack() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView imageView;
//		public ToggleButton toggleButton;
		public CheckBox choosetoggle;
//		public TextView textView;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_addwork_imageselect, parent, false);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.image_view);
//			viewHolder.toggleButton = (ToggleButton) convertView
//					.findViewById(R.id.toggle_button);
			viewHolder.choosetoggle = (CheckBox) convertView
					.findViewById(R.id.choosedbt);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dipToPx(65)); 
//			lp.setMargins(50, 0, 50,0); 
//			viewHolder.imageView.setLayoutParams(lp);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path;
		if (dataList != null && dataList.size() > position) {
			path = dataList.get(position).imagePath;
		}else {
			path = "camera_default";
		}
		if (path.contains("camera_default")) {
			viewHolder.imageView.setImageResource(R.mipmap.plugin_camera_no_pictures);
		} else {
//			ImageManager2.from(mContext).displayImage(viewHolder.imageView,
//					path, Res.getDrawableID("plugin_camera_camera_default"), 100, 100);
			final ImageItem item = dataList.get(position);
			viewHolder.imageView.setTag(item.imagePath);//给每张图片的ImageView打上标记TAG
			cache.displayBmp(viewHolder.imageView, item.thumbnailPath, item.imagePath,
					callback);
		}
//		viewHolder.toggleButton.setTag(position);
		viewHolder.choosetoggle.setTag(position);
//		viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
//		viewHolder.choosetoggle.setOnCheckedChangeListener(new CheckBoxClickListener(viewHolder.toggleButton));
		viewHolder.choosetoggle.setOnCheckedChangeListener(new CheckBoxClickListener());
		//设置图片呢的点击事件/将position传递回去
		viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				post.post(position);
			}
		});
		if (selectedDataList.contains(dataList.get(position))) {
//			viewHolder.toggleButton.setChecked(true);
//			viewHolder.choosetoggle.setVisibility(View.VISIBLE);
		} else {
//			viewHolder.toggleButton.setChecked(false);
//			viewHolder.choosetoggle.setVisibility(View.GONE);
		}
		if (Bimp.tempSelectBitmap.contains(dataList.get(position))){
			viewHolder.choosetoggle.setChecked(true);
		}else{
			viewHolder.choosetoggle.setChecked(false);
		}
		return convertView;
	}
	
	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}
//	private class ToggleClickListener implements OnClickListener{
//		Button chooseBt;
//		public ToggleClickListener(Button choosebt){
//			this.chooseBt = choosebt;
//		}
//
//		@Override
//		public void onClick(View view) {
//			if (view instanceof ToggleButton) {
//				ToggleButton toggleButton = (ToggleButton) view;
//				int position = (Integer) toggleButton.getTag();
//				if (dataList != null && mOnItemClickListener != null
//						&& position < dataList.size()) {
//					mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(),chooseBt);
//				}
//			}
//		}
//	}

	private class CheckBoxClickListener implements CheckBox.OnCheckedChangeListener{
		ToggleButton toggleButton;
//		public CheckBoxClickListener(ToggleButton toggleButton){
//			this.toggleButton = toggleButton;
//		}
		public CheckBoxClickListener(){

		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			CheckBox choosebx = (CheckBox) buttonView;
			int position = (Integer) choosebx.getTag();
			if (dataList != null && mOnItemCheckeBoxClickListener != null && position < dataList.size()){
				mOnItemCheckeBoxClickListener.onCheckedClick(toggleButton, position, choosebx.isChecked(), choosebx);
			}
		}
	}
	
//
//	private OnItemClickListener mOnItemClickListener;
//
//	public void setOnItemClickListener(OnItemClickListener l) {
//		mOnItemClickListener = l;
//	}

//	public interface OnItemClickListener {
//		public void onItemClick(ToggleButton view, int position,
//								boolean isChecked, Button chooseBt);
//	}

	private OnItemCheckeBoxClickListener mOnItemCheckeBoxClickListener;
	public void setOnItemCheckeBoxClickListener(OnItemCheckeBoxClickListener l){
		mOnItemCheckeBoxClickListener = l;
	}
public interface OnItemCheckeBoxClickListener {
	public void onCheckedClick(ToggleButton view, int position,
							boolean isChecked, CheckBox chooseBx);
}

}
