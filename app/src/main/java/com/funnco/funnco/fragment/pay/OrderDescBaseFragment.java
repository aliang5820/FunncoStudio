package com.funnco.funnco.fragment.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.funnco.funnco.R;
import com.funnco.funnco.activity.myinfo.UpdateAccountActivity_3;
import com.funnco.funnco.fragment.BaseFragment;
import com.funnco.funnco.utils.support.Constants;
import com.unionpay.tsmservice.data.Constant;

import java.util.List;

/**
 * Created by Edison on 2016/3/28.
 */
public class OrderDescBaseFragment extends BaseFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.layout_order_desc, container, false);
        init();
        initViews();
        initEvents();
        return parentView;
    }

    @Override
    protected void initViews() {
        parentView.findViewById(R.id.swOrderMonth).setOnClickListener(this);
        parentView.findViewById(R.id.swOrderYear).setOnClickListener(this);
        parentView.findViewById(R.id.qjOrderMonth).setOnClickListener(this);
        parentView.findViewById(R.id.qjOrderYear).setOnClickListener(this);
        parentView.findViewById(R.id.glOrderAdmin).setOnClickListener(this);
        parentView.findViewById(R.id.glOrderMember).setOnClickListener(this);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, UpdateAccountActivity_3.class);
        switch (v.getId()) {
            case R.id.swOrderMonth:
                //商务包月
                intent.putExtra(Constants.ORDER_TYPE, R.id.swOrderMonth);
                break;
            case R.id.swOrderYear:
                //商务包年
                intent.putExtra(Constants.ORDER_TYPE, R.id.swOrderYear);
                break;
            case R.id.qjOrderMonth:
                //旗舰包月
                intent.putExtra(Constants.ORDER_TYPE, R.id.qjOrderMonth);
                break;
            case R.id.qjOrderYear:
                //旗舰包年
                intent.putExtra(Constants.ORDER_TYPE, R.id.qjOrderYear);
                break;
            case R.id.glOrderAdmin:
                //增加管理员
                intent.putExtra(Constants.ORDER_TYPE, R.id.glOrderAdmin);
            case R.id.glOrderMember:
                //增加成员
                intent.putExtra(Constants.ORDER_TYPE, R.id.glOrderMember);
        }
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onMainAction(String data) {

    }

    @Override
    public void onMainData(List<?>... list) {

    }

}