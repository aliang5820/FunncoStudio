package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by user on 2015/5/24.
 */
@Table(name = "Province")
public class Province {
    @Id(column = "id")
    @Column(column = "id")
    private String id;
    @Column(column = "province_name")
    private String province_name;

    public String getProvince_name() {
        return province_name;
    }

    public String getId() {
        return id;
}

    public void setId(String id) {
        this.id = id;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public Province(String id, String province_name) {
        this.id = id;
        this.province_name = province_name;
    }

    public Province() {
    }
}
