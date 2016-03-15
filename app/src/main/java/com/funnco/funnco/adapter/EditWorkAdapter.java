package com.funnco.funnco.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.utils.log.LogUtils;
import com.funnco.funnco.utils.support.PublicWay;

import java.util.ArrayList;

/**
 * Created by user on 2015/5/31.
 */
public class EditWorkAdapter extends BaseAdapter {

    public static boolean isEdit;
    private Context context;
    private ArrayList<ImageItem> list;
    private int position2;
    private int index = -1;

    public EditWorkAdapter(Context context,ArrayList<ImageItem> list){
        this.context = context;
        this.list = list;
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

//    Holder holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LogUtils.e("1111111", "调用来了getView()" + position);
        position2 = position;
        convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_addwork_edit, parent, false);
        ImageView iv = (ImageView) convertView.findViewById(R.id.iv_editwork_image);
        EditText etTitle = (EditText) convertView.findViewById(R.id.et_editwork_title);
        EditText etDesc = (EditText) convertView.findViewById(R.id.et_editwork_desc);
        final ImageButton ib = (ImageButton) convertView.findViewById(R.id.ib_editwork_delete);
        ib.setTag(list.get(position).getImagePath());

        ib.setOnClickListener(new ImageButtonListener(ib));
        //删除按钮的监听
        iv.setImageBitmap(list.get(position).getBitmap());

        if (PublicWay.hashMapTitle.containsKey(ib.getTag()+"") && PublicWay.hashMapTitle.get(ib.getTag()+"") != null){
            etTitle.setText(PublicWay.hashMapTitle.get(ib.getTag()+""));
        }
        if (PublicWay.hashMapDesc.containsKey(ib.getTag()+"") && PublicWay.hashMapDesc.get(ib.getTag()+"") != null){
            etDesc.setText(PublicWay.hashMapDesc.get(ib.getTag()+""));
        }
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                LogUtils.e("afterTextChanged-------------","s : "+s );
                PublicWay.hashMapTitle.put(ib.getTag()+"", s+"");
            }
        });
        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                PublicWay.hashMapDesc.put(ib.getTag()+"", s+"");
            }
        });

        etTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 在TOUCH的UP事件中，要保存当前的行下标，因为弹出软键盘后，整个画面会被重画
                // 在getView方法的最后，要根据index和当前的行下标手动为EditText设置焦点
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                }
                return false;
            }
        });
        etDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                }
                return false;
            }
        });
        etTitle.clearFocus();
        etDesc.clearFocus();
        if (index != -1 && index == position){
            etTitle.requestFocus();
//            etDesc.requestFocus();
        }
        if (isEdit && !PublicWay.editWorkItemList.containsKey(list.get(position).getImagePath())){
            PublicWay.editWorkItemList.put(list.get(position).getImagePath(), convertView);
            LogUtils.e("CommonAdapter", "适配器添加Item  View" + position);
        }
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        LogUtils.e("----","适配器更新了数据前 ： size"+PublicWay.editWorkItemList.size());
        PublicWay.editWorkItemList.clear();
        PublicWay.hashMapTitle.clear();
        PublicWay.hashMapDesc.clear();
        LogUtils.e("----", "适配器更新了数据" + PublicWay.editWorkItemList.size());
        super.notifyDataSetChanged();
        LogUtils.e("----", "适配器更新了数据后 ： size"+PublicWay.editWorkItemList.size());
    }

    public class ImageButtonListener implements ImageButton.OnClickListener{
        ImageButton ib;
        public ImageButtonListener(ImageButton ib){
            this.ib = ib;
        }
        @Override
        public void onClick(View v) {
            if (imageButtonClickListener != null) {
                imageButtonClickListener.onImageButtonClickListener(ib, list.get(position2).getImagePath());
            }
        }
    }

    ImageButtonClickListener imageButtonClickListener;
    public void setOnImageButtonClick(ImageButtonClickListener l){
        imageButtonClickListener = l;
    }
    public interface ImageButtonClickListener{
        void onImageButtonClickListener(ImageButton imageButton,String imagePath);

    }

}
