package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 用户职业
 * Created by user on 2015/5/22.
 * @author Shawn
 */

@Table(name="Career")
public class Career {
    @Id(column = "id")
    @Column(column = "id")
    private String id;
    @Column(column = "career_name")
    private String career_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCareer_name() {
        return career_name;
    }

    public void setCareer_name(String career_name) {
        this.career_name = career_name;
    }

    public Career(String id, String career_name) {
        this.id = id;
        this.career_name = career_name;
    }

    public Career() {
    }

    @Override
    public String toString() {
        return "Career{" +
                "id='" + id + '\'' +
                ", career_name='" + career_name + '\'' +
                '}';
    }
}
