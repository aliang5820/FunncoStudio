package com.funnco.funnco.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 用户登录的信息对象
 * Created by user on 2015/5/18.
 * @author Shawn
 */
@Table(name = "UserLoginInfo")
public class UserLoginInfo implements Parcelable {
    @Column(column = "id")
    @Id
    private String id;//用户id
    @Column(column = "nickname")
    private String nickname;//昵称
    @Column(column = "mobile")
    private String mobile;//手机号
    @Column(column = "career_id")
    private String career_id;//行业类型
    @Column(column = "career_name")
    private String career_name;//行业名字*
    @Column(column = "headpic")
    private String headpic;//头像地址
    @Column(column = "sex")
    private String sex;//性别
    @Column(column = "intro")
    private String intro;//简介
    @Column(column = "country_id")
    private String country_id;//国家id
    @Column(column = "province_id")
    private String province_id;//省份id
    @Column(column = "province_name")
    private String province_name;//省
    @Column(column = "address")
    private String address;//地址
    @Column(column = "pic_qrcode")
    private String pic_qrcode;//二维码*
    @Column(column = "bookings")
    private String bookings;//预约数*
    @Column(column = "types")
    private String types;//用户类型*
    @Column(column = "work_phone")
    private String work_phone;//工作电话
    @Column(column = "service_count")
    private String service_count;//服务数据 代表服务个数
    @Column(column = "work_count")
    private String work_count;//作品数据 代表作品个数
    @Column(column = "token")
    private String token;//授权令牌

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCareer_id() {
        return career_id;
    }

    public void setCareer_id(String career_id) {
        this.career_id = career_id;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCareer_name() {
        return career_name;
    }

    public void setCareer_name(String career_name) {
        this.career_name = career_name;
    }

    public String getPic_qrcode() {
        return pic_qrcode;
    }

    public void setPic_qrcode(String pic_qrcode) {
        this.pic_qrcode = pic_qrcode;
    }

    public String getBookings() {
        return bookings;
    }

    public void setBookings(String bookings) {
        this.bookings = bookings;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getWork_phone() {
        return work_phone;
    }

    public void setWork_phone(String work_phone) {
        this.work_phone = work_phone;
    }

    public String getService_count() {
        return service_count;
    }

    public void setService_count(String service_count) {
        this.service_count = service_count;
    }

    public String getWork_count() {
        return work_count;
    }

    public void setWork_count(String work_count) {
        this.work_count = work_count;
    }

    public UserLoginInfo() {
    }

    public UserLoginInfo(String id, String nickname, String mobile,
                         String career_id, String career_name, String headpic,
                         String sex, String intro, String country_id,
                         String province_id, String province_name, String address,
                         String pic_qrcode, String bookings, String types,
                         String token,String work_phone,String service_count,String work_count) {
        this.id = id;
        this.nickname = nickname;
        this.mobile = mobile;
        this.career_id = career_id;
        this.career_name = career_name;
        this.headpic = headpic;
        this.sex = sex;
        this.intro = intro;
        this.country_id = country_id;
        this.province_id = province_id;
        this.province_name = province_name;
        this.address = address;
        this.pic_qrcode = pic_qrcode;
        this.bookings = bookings;
        this.types = types;
        this.token = token;
        this.work_phone = work_phone;
        this.service_count = service_count;
        this.work_count = work_count;
    }

    @Override
    public String toString() {
        return "UserLoginInfo{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", career_id='" + career_id + '\'' +
                ", career_name='" + career_name + '\'' +
                ", headpic='" + headpic + '\'' +
                ", sex='" + sex + '\'' +
                ", intro='" + intro + '\'' +
                ", country_id='" + country_id + '\'' +
                ", province_id='" + province_id + '\'' +
                ", province_name='" + province_name + '\'' +
                ", address='" + address + '\'' +
                ", pic_qrcode='" + pic_qrcode + '\'' +
                ", bookings='" + bookings + '\'' +
                ", types='" + types + '\'' +
                ", token='" + token + '\'' +
                ", work_phone='" + work_phone + '\'' +
                ", service_count='" + service_count + '\'' +
                ", work_count='" + work_count + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nickname);
        dest.writeString(mobile);
        dest.writeString(career_id);
        dest.writeString(career_name);
        dest.writeString(headpic);
        dest.writeString(sex);
        dest.writeString(intro);
        dest.writeString(country_id);
        dest.writeString(province_id);
        dest.writeString(province_name);
        dest.writeString(address);
        dest.writeString(pic_qrcode);
        dest.writeString(bookings);
        dest.writeString(types);
        dest.writeString(token);
        dest.writeString(work_phone);
        dest.writeString(service_count);
        dest.writeString(work_count);
    }

    public static Creator<UserLoginInfo> CREATOR = new Creator<UserLoginInfo>() {

        @Override
        public UserLoginInfo createFromParcel(Parcel source) {
            String id = source.readString();
            String nickname = source.readString();
            String mobile = source.readString();
            String carceer_id = source.readString();
            String carceer_name = source.readString();
            String headpic = source.readString();
            String sex = source.readString();
            String intro = source.readString();
            String country_id = source.readString();
            String province_id = source.readString();
            String province_name = source.readString();
            String address = source.readString();
            String pic_qrcode = source.readString();
            String bookings = source.readString();
            String types = source.readString();
            String token = source.readString();
            String work_phone = source.readString();
            String service_count = source.readString();
            String work_count = source.readString();

            return new UserLoginInfo(id, nickname, mobile, carceer_id,
                    carceer_name, headpic, sex, intro, country_id,
                    province_id, province_name, address, pic_qrcode,
                    bookings, types,token,work_phone,service_count,work_count);
        }

        @Override
        public UserLoginInfo[] newArray(int size) {
            return new UserLoginInfo[0];
        }
    };
}
