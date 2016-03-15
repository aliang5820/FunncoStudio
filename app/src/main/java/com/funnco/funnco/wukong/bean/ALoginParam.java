package com.funnco.funnco.wukong.bean;

/**
 * 悟空登录所需要的数据
 * Created by user on 2015/11/11.
 */
public class ALoginParam {

    private String signature;
    private long openId;
    private String domain;
    private String appKey;
    private String nonce;
    private long timestamp;

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setOpenId(long openId) {
        this.openId = openId;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public long getOpenId() {
        return openId;
    }

    public String getDomain() {
        return domain;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
