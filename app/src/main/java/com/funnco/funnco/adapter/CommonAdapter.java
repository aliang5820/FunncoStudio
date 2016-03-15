package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class CommonAdapter<T> extends CommonBaseAdapter {
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;
	//是否打标记
	private boolean isTag;
	//将打标记的viewId
	private int[] tagId;

	public CommonAdapter(Context context,  List<T> mDatas , int itemLayoutId) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
	}
	public CommonAdapter(Context context,  List<T> mDatas , int itemLayoutId, CommonImp imp) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
		super.imp = imp;
	}
	public void setmDatas(List<T> mDatas){
		this.mDatas = mDatas;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (super.getCount() != 0){
			return super.getCount();
		}
		if(mDatas != null){
			return mDatas.size();
		}
		return 0;
	}

	@Override
	public T getItem(int position) {
		if (position >= mDatas.size()){
			return null;
		}
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView,
				parent);
		convert(viewHolder, getItem(position),position);
		View view = viewHolder.getConvertView();
		if (isTag) {
			for (int i = 0 ; i < tagId.length ; i ++) {
				int id = tagId[i];
				View v = view.findViewById(id);
				if (v != null){
					v.setTag(position + "");
				}
			}
		}
		return view;

	}
	public abstract void convert(ViewHolder helper, T item, int position);

	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
				position);
	}
	public void isTag(boolean isTag, int[] tagId){
		this.isTag = isTag;
		this.tagId = tagId;
	}

}
