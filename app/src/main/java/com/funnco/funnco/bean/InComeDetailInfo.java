package com.funnco.funnco.bean;

/**
 * Created by EdisonZhao on 16/3/31.
 */
public class InComeDetailInfo {
    private int id;
    private int type;
    private String title;
    private String desc;
    private float num;
    private int uid;
    private long addtime;
    private int desc_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public int getDesc_type() {
        return desc_type;
    }

    public void setDesc_type(int desc_type) {
        this.desc_type = desc_type;
    }
}
