package com.funnco.funnco.fragment.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.login.WelcomeActivity;

public class GuideFragment_3 extends BaseGuideFragment implements View.OnClickListener {
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_guide_3, container, false);
		rootView = view.findViewById(R.id.iv_guide_3_1);
		rootView.setOnClickListener(this);
		return view;
	}
	
	@Override
	public int getRootViewId() {
		return R.id.layout_guide_3;
	}

	@Override
	public int[] getChildViewId() {
		return new int[]{R.id.iv_guide_3_1};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_guide_3_1:
				((WelcomeActivity)getActivity()).enterApp();
				break;
		}
	}
}
