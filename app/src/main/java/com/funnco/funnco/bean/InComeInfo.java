package com.funnco.funnco.bean;

import java.util.List;

/**
 * Created by EdisonZhao on 16/3/31.
 */
public class InComeInfo {
    private float weekAccountIncome;
    private float monthAccountIncome;
    private float monthAccountPay;
    private float allIncome;
    private List<InComeDetailInfo> accountDetail;

    public float getWeekAccountIncome() {
        return weekAccountIncome;
    }

    public void setWeekAccountIncome(float weekAccountIncome) {
        this.weekAccountIncome = weekAccountIncome;
    }

    public float getMonthAccountIncome() {
        return monthAccountIncome;
    }

    public void setMonthAccountIncome(float monthAccountIncome) {
        this.monthAccountIncome = monthAccountIncome;
    }

    public float getMonthAccountPay() {
        return monthAccountPay;
    }

    public void setMonthAccountPay(float monthAccountPay) {
        this.monthAccountPay = monthAccountPay;
    }

    public float getAllIncome() {
        return allIncome;
    }

    public void setAllIncome(float allIncome) {
        this.allIncome = allIncome;
    }

    public List<InComeDetailInfo> getAccountDetail() {
        return accountDetail;
    }

    public void setAccountDetail(List<InComeDetailInfo> accountDetail) {
        this.accountDetail = accountDetail;
    }
}
