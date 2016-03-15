package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 团队成员 日程成员列表
 * Created by user on 2015/8/21.
 */
@Table(name = "TeamMember")
public class TeamMember {
    @Column (column = "nickname")
    private String nickname;
    @Column (column = "headpic")
    private String headpic;
    @Id
    @Column (column = "member_uid")
    private String member_uid;
    @Column (column = "team_id")
    private String team_id;//所在团队的Id
//    @Column (column = "team_name")
//    private String team_name;//所在团队的名字
    @Column (column = "u_id")
    private String u_id;//所属管理者Id


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public void setMember_uid(String member_uid) {
        this.member_uid = member_uid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getHeadpic() {
        return headpic;
    }

    public String getMember_uid() {
        return member_uid;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getU_id() {
        return u_id;
    }


    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public TeamMember() {
    }

    public TeamMember(String nickname, String headpic, String member_uid, String team_id, String u_id) {
        this.nickname = nickname;
        this.headpic = headpic;
        this.member_uid = member_uid;
        this.team_id = team_id;
        this.u_id = u_id;
    }

    @Override
    public String toString() {
        return "TeamMember{" +
                "nickname='" + nickname + '\'' +
                ", headpic='" + headpic + '\'' +
                ", member_uid='" + member_uid + '\'' +
                ", team_id='" + team_id + '\'' +
                ", u_id='" + u_id + '\'' +
                '}';
    }
}
