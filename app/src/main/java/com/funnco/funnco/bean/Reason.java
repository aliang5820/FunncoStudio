package com.funnco.funnco.bean;

/**
 * 取消删除原因
 * Created by user on 2015/6/14.
 */

public class Reason {

    private String id;
    private String contents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Reason(String id, String contents) {
        this.id = id;
        this.contents = contents;
    }

    public Reason() {
    }

    @Override
    public String toString() {
        return "Reason{" +
                "id='" + id + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
