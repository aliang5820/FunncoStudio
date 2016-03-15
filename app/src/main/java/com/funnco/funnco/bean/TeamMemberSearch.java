package com.funnco.funnco.bean;

/**
 * 2.1新增
 * 成员添加搜索
 * Created by user on 2015/10/14.
 */
public class TeamMemberSearch {
    private String nickname;
    private String id;
    private String headpic;
    private String career_name;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public void setCareer_name(String career_name) {
        this.career_name = career_name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getId() {
        return id;
    }

    public String getHeadpic() {
        return headpic;
    }

    public String getCareer_name() {
        return career_name;
    }
}
