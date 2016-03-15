package com.funnco.funnco.view.pop;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.funnco.funnco.R;
import com.funnco.funnco.adapter.ListPickerAdapter;
import com.funnco.funnco.model.ListPickerIMd;

import java.util.ArrayList;

/**
 * Created by Cenkai on 2016/3/6.
 */
public class ListPicker {

    public static  interface onPicker {

    }
    public  static  void getListPicker(View parent,Context context,int  width, ArrayList<ListPickerIMd> data)
    {
        Log.i("test","onPicker");
        ListPickerAdapter  adapter=new ListPickerAdapter();

        adapter.setData(data);
        PopupWindow popupWindow=null;
        ListView lv_group = null;
        View view ;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.pop_view_listpicker, null);
        lv_group = (ListView) view.findViewById(R.id.list_info);
        //      lv_group.setAdapter(groupAdapter);
        popupWindow = new PopupWindow(view, width,450);
        lv_group.setAdapter(adapter);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        popupWindow.showAsDropDown(parent);
//            lv_group.setOnItemClickListener(new OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                }
//            });

        Log.i("test","onPicker");
    }


}

