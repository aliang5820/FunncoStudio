package com.funnco.funnco.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.model.ListPickerIMd;

import java.util.ArrayList;

/**
 * Created by Cenkai on 2016/3/7.
 */
public class ListPickerAdapter extends BaseAdapter {

    ArrayList<ListPickerIMd>  data;

    public void  setData(   ArrayList<ListPickerIMd>  data)
    {

        this.data=data;
    }
    @Override
    public int getCount() {

        if(null!=data)
        {
            return data.size();
        }
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=null;

        if(convertView==null)
        {
            holder=new ViewHolder();
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_list_items,null);
            holder.title= (TextView) convertView.findViewById(R.id.tx_txt);

        }

        if(data!=null)
        {
            ListPickerIMd  item=data.get(position);

            holder.title.setText(item.name);
        }
        return convertView;
    }

    class  ViewHolder{

     public   TextView  title;
    }
}
