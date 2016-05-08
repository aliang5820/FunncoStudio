package com.funnco.funnco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.Team;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edison on 2016/5/8.
 */
public class ShareAdapter extends BaseAdapter {
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private List<Team> list;
    private Context context;
    private ImageLoader imageLoader;
    private int checkedPosition = 0;

    public ShareAdapter(Context context, List<Team> list, ImageLoader imageLoader) {
        this.list = list;
        this.context = context;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_share_item, viewGroup, false);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_name);
        Team t = list.get(position);
        ((TextView) view.findViewById(R.id.item_name)).setText(t.getTeam_name());
        ImageView imageView = ((ImageView) view.findViewById(R.id.item_img));
        imageLoader.displayImage(t.getCover_pic(), imageView);

        if (position == 0) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        if (!checkBoxList.contains(checkBox)) {
            checkBoxList.add(checkBox);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                checkBox.setChecked(true);
                checkedPosition = position;
                for (CheckBox ch : checkBoxList) {
                    if (checkBox != ch) {
                        ch.setChecked(false);
                    }
                }
            }
        });
        return view;
    }

    public int getCheckedPosition() {
        return checkedPosition;
    }
}
