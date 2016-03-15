package com.funnco.funnco.impl;

import com.funnco.funnco.bean.MyCustomerD;

import java.util.Comparator;

/**
 * 从小到大
 * Created by user on 2015/6/14.
 */
public class ComparatorByDate2 implements Comparator<MyCustomerD> {

    /**
     * 排序接口算法实现
     * @param lhs
     * @param rhs
     * @return 比较结果的大小
     */
    @Override
    public int compare(MyCustomerD lhs, MyCustomerD rhs) {
//        return lhs.getTruename().hashCode() - rhs.getTruename().hashCode();
        return rhs.getDate2().compareTo(lhs.getDate2());
    }
}
