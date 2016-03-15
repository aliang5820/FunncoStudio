package com.funnco.funnco.impl;

import com.funnco.funnco.bean.MyCustomer;

import java.util.Comparator;

/**
 * Created by user on 2015/6/14.
 */
public class ComparatorByName implements Comparator<MyCustomer> {

    /**
     * 排序接口算法实现
     * @param lhs
     * @param rhs
     * @return 比较结果的大小
     */
    @Override
    public int compare(MyCustomer lhs, MyCustomer rhs) {
        if (lhs.getLetter().equals("@") || rhs.getLetter().equals("#")){
            return -1;
        }else if (lhs.getLetter().equals("#") || rhs.getLetter().equals("@")){
            return 1;
        }else{
            //进行首字母比较
            return lhs.getLetter().compareTo(rhs.getLetter());
        }
    }
}
