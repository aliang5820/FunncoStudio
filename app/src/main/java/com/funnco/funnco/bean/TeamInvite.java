package com.funnco.funnco.bean;

/**
 * 消息通知中的团队邀请
 * Created by user on 2015/10/23.
 */
public class TeamInvite {

    /**
     * types : 1
     * inviter_nickname : 大飞哥哥
     * id : 1
     * team_id : 6
     * team_name : 啊啦啦啦
     * status : 0
     */
    private String types;
    private String inviter_nickname;
    private String id;
    private String team_id;
    private String team_name;
    private String headpic;
    private String status;

    public void setTypes(String types) {
        this.types = types;
    }

    public void setInviter_nickname(String inviter_nickname) {
        this.inviter_nickname = inviter_nickname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypes() {
        return types;
    }

    public String getInviter_nickname() {
        return inviter_nickname;
    }

    public String getId() {
        return id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getStatus() {
        return status;
    }
}
