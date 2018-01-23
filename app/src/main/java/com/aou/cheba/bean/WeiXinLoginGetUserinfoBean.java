package com.aou.cheba.bean;


import java.io.Serializable;

public class WeiXinLoginGetUserinfoBean implements Serializable{
    private String openid;
    private String nickname;
    private String headimgurl;

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    private Integer sex;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Override
    public String toString() {
        return "WeiXinLoginGetUserinfoBean{" +
                "openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                '}';
    }
}
