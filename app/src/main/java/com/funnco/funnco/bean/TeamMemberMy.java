package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 团队成员 我的团队
 * Created by user on 2015/10/16.
 */
@Table(name = "TeamMemberMy")
public class TeamMemberMy {
    @Id
    @Column(column = "uid")
    private String uid;
    @Column(column = "role")
    private String role;
    @Column(column = "nickname")
    private String nickname;
    @Column(column = "headpic")
    private String headpic;
    //未来区别属于那个团队
    @Column(column = "team_id")
    private String team_id;//另外加入

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
    }

    public String getNickname() {
        return nickname;
    }

    public String getHeadpic() {
        return headpic;
    }
}
