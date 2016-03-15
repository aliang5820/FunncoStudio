package com.funnco.funnco.bean;

/**
 * 我的团队
 * Created by user on 2015/10/15.
 */
public class TeamMy {
    private String address;
    private String role;//技师在团队中的角色（0是创建者，1管理员，2普通团员）
    private String phone;
    private String cover_pic;
    private String allow_search;
    private String intro;
    private String team_id;
    private String team_name;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCover_pic(String cover_pic) {
        this.cover_pic = cover_pic;
    }

    public void setAllow_search(String allow_search) {
        this.allow_search = allow_search;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getCover_pic() {
        return cover_pic;
    }

    public String getAllow_search() {
        return allow_search;
    }

    public String getIntro() {
        return intro;
    }

    public String getTeam_id() {
        return team_id;
    }

    public String getTeam_name() {
        return team_name;
    }
}