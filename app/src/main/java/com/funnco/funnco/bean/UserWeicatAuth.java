package com.funnco.funnco.bean;

/**
 * 微信授权返回的信息
 * Created by user on 2015/7/6.
 */
public class UserWeicatAuth {
    private String uid;
    private String refresh_token_expires;
    private String openid;//上传
    private String expires_in;
    private String refresh_token;
    private String scope;
    private String access_token;//上传

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRefresh_token_expires() {
        return refresh_token_expires;
    }

    public void setRefresh_token_expires(String refresh_token_expires) {
        this.refresh_token_expires = refresh_token_expires;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public UserWeicatAuth() {
    }

    public UserWeicatAuth(String uid, String refresh_token_expires, String openid, String expires_in, String refresh_token, String scope, String access_token) {
        this.uid = uid;
        this.refresh_token_expires = refresh_token_expires;
        this.openid = openid;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
        this.access_token = access_token;
    }

    @Override
    public String toString() {
        return "UserWeicatAuth{" +
                "uid='" + uid + '\'' +
                ", refresh_token_expires='" + refresh_token_expires + '\'' +
                ", openid='" + openid + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", scope='" + scope + '\'' +
                ", access_token='" + access_token + '\'' +
                '}';
    }
}
