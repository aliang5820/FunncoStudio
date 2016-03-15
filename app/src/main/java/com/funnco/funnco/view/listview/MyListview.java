package com.funnco.funnco.view.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListview extends ListView {

	public MyListview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyListview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	// 其中onMeasure函数决定了组件显示的高度与宽度；
	// makeMeasureSpec函数中第一个函数决定布局空间的大小，第二个参数是布局模式
	// MeasureSpec.AT_MOST的意思就是子控件需要多大的控件就扩展到多大的空间
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);//对应修改第二个值
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
