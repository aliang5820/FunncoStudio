package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.FunncoEvent;
import com.funnco.funnco.bean.FunncoEventCustomer;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.string.TextUtils;
import com.funnco.funnco.view.imageview.CircleImageView;
import com.funnco.funnco.impl.SimpleSwipeListener;
import com.funnco.funnco.view.switcher.SwipeLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 日程列表专用适配器
 */
public class ListViewAdapter extends BaseSwipeAdapter {
	
	// 上下文对象
	private Context mContext;
	private ArrayList<FunncoEvent> list;
	private Post post;
	private int deleteposition;
	//用户传递回去的SwipLayout
	private SwipeLayout swip;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	//是否显示提示
//	private boolean isNofity = false;

		// 构造函数
		public ListViewAdapter(Context mContext, ArrayList<FunncoEvent> list,Post post,
							   ImageLoader imageLoader,DisplayImageOptions options) {
			this.mContext = mContext;
			this.list = list;
			this.post = post;
			this.imageLoader = imageLoader;
			this.options = options;
		}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// SwipeLayout的布局id
	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swip;
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.layout_schedult_item, parent, false);

		final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
//		if (isNofity){
//			swipeLayout.setSwipeEnabled(false);
//		}else{
			swipeLayout.setSwipeEnabled(true);
//		}
		final TextView delete = (TextView) swipeLayout.findViewById(R.id.delete);
		// 当隐藏的删除menu被打开的时候的回调函数
		swipeLayout.addSwipeListener(new SimpleSwipeListener(){
			@Override
			public void onOpen(SwipeLayout layout) {
				super.onOpen(layout);
				TextView text = (TextView) swipeLayout.findViewById(R.id.delete);
				deleteposition = Integer.parseInt((String)text.getTag(R.id.id_delete));
				swip = swipeLayout;
			}
		});
		LinearLayout dele = (LinearLayout) view.findViewById(R.id.ll_menu);
		dele.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 点击完成之后，关闭删除menu
				swipeLayout.close();
				post.post(deleteposition);
			}
		});
		return view;
	}


	@Override
	public void fillValues(int position, View convertView) {
		SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
		CircleImageView ivCustomer = (CircleImageView) convertView.findViewById(R.id.iv_item_schedule_icon);
		CircleImageView civIcon = (CircleImageView) convertView.findViewById(R.id.civ_item_schedule_conventionicon);
		LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.llayout_item_schedule_des);
		TextView tvEventDes = (TextView) convertView.findViewById(R.id.tv_item_schedule_eventdes);
		TextView tvConventionUsername = (TextView) convertView.findViewById(R.id.tv_item_schedule_convention_username);

			ivCustomer.setVisibility(View.GONE);
			convertView.setTag(R.id.id_delete, position + "");
			FunncoEvent funncoEvent = list.get(position);
			//给删除按钮打标记
			convertView.findViewById(R.id.delete).setTag(R.id.id_delete, position + "");
			swipeLayout.setSwipeEnabled(true);
			TextView textDelete = (TextView) swipeLayout.findViewById(R.id.delete);

			List<FunncoEventCustomer> list = funncoEvent.getList();
			String number = funncoEvent.getNumbers()+"";
			String time = funncoEvent.getTimes()+"";

			if (!TextUtils.isNull(number) && !TextUtils.equals("null",number)) {
				textDelete.setText(number.equals("0") ? R.string.delete : R.string.cancle);
			}else{
				number = "0";
				textDelete.setText(R.string.delete);
			}

			TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_item_schedule_username);
			tvTitle.setText(funncoEvent.getTitle() + "");
			TextView tvTime = (TextView) convertView.findViewById(R.id.tv_item_schedule_time);
			tvTime.setText(time);
			TextView tvGuideTime = (TextView) convertView.findViewById(R.id.tv_item_schedule_guide_time);
			//时间线
			TextView tvTimeline = (TextView) convertView.findViewById(R.id.tv_item_schedule_timeline);
			//点标记
			ImageView ivDot = (ImageView) convertView.findViewById(R.id.iv_item_schedule_timedot);
			//预约个数
			TextView tvConventionNumber = (TextView) convertView.findViewById(R.id.tv_item_schedule_conventionnumber);
			//有备注的人数
			TextView tvRemarkNumber = (TextView) convertView.findViewById(R.id.tv_item_schedule_conventionremark);
			int count = Integer.parseInt(number);
			if (time.length() >=5) {
				tvGuideTime.setText(time.substring(0,5));
			}
			convertView.findViewById(R.id.iv_item_schedult_arrow).setVisibility(count > 0 ? View.INVISIBLE : View.VISIBLE);
			//为“0” 表示是事件 而不是预约 所以直接显示头像
			if (number.equals("0")) {
				linearLayout.setVisibility(View.GONE);
				tvEventDes.setVisibility(View.VISIBLE);
				String eventDes = funncoEvent.getRemark();
				tvEventDes.setText(!TextUtils.isNull(eventDes) ? eventDes : "");
				tvTimeline.setBackgroundResource(R.drawable.shape_schedule_timeline_red);
				ivDot.setImageResource(R.mipmap.common_schedule_timedot_event);
			} else {
				tvTimeline.setBackgroundResource(R.drawable.shape_schedule_timeline_green);
				linearLayout.setVisibility(View.VISIBLE);
				tvEventDes.setVisibility(View.GONE);
				ivDot.setImageResource(R.mipmap.common_schedule_timedot_conventation);
				if (funncoEvent != null && list != null) {
					tvConventionNumber.setText(""+list.size() + "/" +count);
					int remarkNumber = 0;
					for (int i = 0;i < list.size(); i++){
						FunncoEventCustomer fec = list.get(i);
						if (fec != null){
							String rk = fec.getRemark();
							if (!TextUtils.isNull(rk)){
								remarkNumber ++;
							}
						}
					}
					tvRemarkNumber.setText(remarkNumber + "");
					if (list != null && list.size() > 1) {
						civIcon.setImageResource(R.mipmap.common_schedule_conventiontype_icon);
						tvConventionUsername.setText("共有");
					} else if (list != null && list.size() == 1){
						String conventionUserName = list.get(0).getTruename();
						tvConventionUsername.setText(!TextUtils.isNull(conventionUserName) ? conventionUserName : "");
						String url = list.get(0).getHeadpic();
						if (!TextUtils.isNull(url)) {
							imageLoader.displayImage(url,civIcon,options);
						}
					}
				}
			}
//		}
	}

	public SwipeLayout getSwip() {
		return swip;
	}
}
