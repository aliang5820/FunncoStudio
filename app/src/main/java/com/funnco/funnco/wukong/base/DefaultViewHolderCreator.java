package com.funnco.funnco.wukong.base;

import android.util.Log;

import com.funnco.funnco.wukong.viewholder.ViewHolder;
import com.funnco.funnco.wukong.viewholder.ViewHolderCreator;

/**
 * 默认的ViewHolder创建类，需要传入 无参的类型
 * Created by zijunlzj on 14/12/23.
 */
public class DefaultViewHolderCreator implements ViewHolderCreator<ViewHolder,Object> {

    @Override
    public ViewHolder onCreate(Class<ViewHolder>[] target, Object source) {
        ViewHolder viewHolder = null;
        try {
            viewHolder = target[0].newInstance();
        }  catch (InstantiationException e) {
            e.printStackTrace();
            Log.e(DefaultViewHolderCreator.class.getSimpleName(),"creater viewholder error",e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e(DefaultViewHolderCreator.class.getSimpleName(),"creater viewholder error",e);
        }
        return viewHolder;
    }
}
