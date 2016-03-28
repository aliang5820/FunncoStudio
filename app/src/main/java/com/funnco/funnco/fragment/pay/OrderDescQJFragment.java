package com.funnco.funnco.fragment.pay;

import android.view.View;

import com.funnco.funnco.R;

/**
 * Created by Edison on 2016/3/28.
 */
public class OrderDescQJFragment extends OrderDescBaseFragment {

    @Override
    protected void initViews() {
        super.initViews();
        findViewById(R.id.order_desc_qj).setVisibility(View.VISIBLE);
    }
}
