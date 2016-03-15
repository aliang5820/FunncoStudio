package com.funnco.funnco.utils.view;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.PopupWindow;

import java.util.List;

/**
 * Created by user on 2015/6/20.
 */
public class PopupWindowUtils {

    public synchronized static PopupWindow getCommonPw(View view,PopupWindow popupWindow){
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(true);
        return popupWindow;
    }

    public synchronized static PopupWindow getCommonPw(PopupWindow popupWindow,View view,NumberPicker numberPicker,List<String> list){
        popupWindow = getCommonPw(view,popupWindow);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(list.size());
        String[] arr = new String[list.size()];
        int i = 0;
        for (String s : list){
            arr[i] = s;
            i++;
        }
        numberPicker.setDisplayedValues(arr);
//        view.setTag(numberPicker);
        return popupWindow;
    }

}
