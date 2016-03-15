package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

import java.util.List;

/**
 * 团队
 * Created by user on 2015/8/21.
 */
@Table(name = "Team")
public class Team {
    @Id
    @Column(column = "team_id")
    private String team_id;
    @Transient
    private List<TeamMember> list;
    @Column(column = "team_name")
    private String team_name;
    @Column(column = "u_id")
    private String u_id;//用户Id

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public void setList(List<TeamMember> list) {
        this.list = list;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_id() {
        return team_id;
    }

    public List<TeamMember> getList() {
        return list;
    }

    public String getTeam_name() {
        return team_name;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public Team() {
    }

    public Team(String team_id, List<TeamMember> list, String team_name,String u_id) {
        this.team_id = team_id;
        this.list = list;
        this.team_name = team_name;
        this.u_id = u_id;
    }
}
