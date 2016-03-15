package com.funnco.funnco.bean;

/**
 * 添加新预约返回的时间段对象
 * Created by user on 2015/11/9.
 */
public class EnableTime {

    private int types;
    private int counts;
    private String service_id;
    private String numbers;
    private int endtime;
    private int starttime;

    public void setTypes(int types) {
        this.types = types;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public void setEndtime(int endtime) {
        this.endtime = endtime;
    }

    public void setStarttime(int starttime) {
        this.starttime = starttime;
    }

    public int getTypes() {
        return types;
    }

    public int getCounts() {
        return counts;
    }

    public String getService_id() {
        return service_id;
    }

    public String getNumbers() {
        return numbers;
    }

    public int getEndtime() {
        return endtime;
    }

    public int getStarttime() {
        return starttime;
    }
}
