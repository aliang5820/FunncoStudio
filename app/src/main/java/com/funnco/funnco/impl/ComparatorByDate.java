package com.funnco.funnco.impl;

import com.funnco.funnco.bean.MyCustomerD;

import java.util.Comparator;

/**
 * Created by user on 2015/6/14.
 */
public class ComparatorByDate implements Comparator<MyCustomerD> {

    /**
     * 排序接口算法实现
     * @param lhs
     * @param rhs
     * @return 比较结果的大小
     */
    @Override
    public int compare(MyCustomerD lhs, MyCustomerD rhs) {
//        return lhs.getTruename().hashCode() - rhs.getTruename().hashCode();
        return lhs.getDate2().compareTo(rhs.getDate2());
    }
}
