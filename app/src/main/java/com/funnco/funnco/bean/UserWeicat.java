package com.funnco.funnco.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 微信登陆 获取的微信账号的信息
 * Created by user on 2015/7/6.
 */
@Table(name = "UserWeicat")
public class UserWeicat {
    @Column(column = "sex")
    private String sex;//性别
    @Column(column = "nickname")
    private String nickname;//昵称
    @Column(column = "unionid")
    private String unionid;
    @Column(column = "province")
    private String province;
    @Id
    @Column(column = "openid")
    private String openid;
    @Column(column = "language")
    private String language;
    @Column(column = "headimgurl")
    private String headimgurl;//头像地址
    @Column(column = "country")
    private String country;//所属国家
    @Column(column = "city")
    private String city;//所在城市

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public UserWeicat() {
    }

    public UserWeicat(String sex, String nickname, String unionid, String province, String openid, String language, String headimgurl, String country, String city) {
        this.sex = sex;
        this.nickname = nickname;
        this.unionid = unionid;
        this.province = province;
        this.openid = openid;
        this.language = language;
        this.headimgurl = headimgurl;
        this.country = country;
        this.city = city;
    }

    @Override
    public String toString() {
        return "UserWeicat{" +
                "sex='" + sex + '\'' +
                ", nickname='" + nickname + '\'' +
                ", unionid='" + unionid + '\'' +
                ", province='" + province + '\'' +
                ", openid='" + openid + '\'' +
                ", language='" + language + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
