package com.funnco.funnco.fragment.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.funnco.funnco.R;

public class GuideFragment_1 extends BaseGuideFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_fragment_guide_1, container, false);
	}
	
	@Override
	public int getRootViewId() {
		return R.id.layout_guide_1;
	}

	@Override
	public int[] getChildViewId() {
		return new int[]{R.id.iv_guide_1_1};
	}

}
