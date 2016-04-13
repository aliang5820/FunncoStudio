package com.funnco.funnco.bean;

/**
 * Created by Edison on 2016/4/13.
 */
public class ScheduleTimeInfo {
    private String formatTime;
    private int time;
    private boolean isEnable;

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
