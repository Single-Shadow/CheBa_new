package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */
public class MyCode_uplist {

    /**
     * obj : [{"id":1480495419395738017,"uid":56338693,"headImg":"https://www.baidu.com/img/bd_logo1.png","nickname":"CeShi","followed":true,"addDate":1480495419395}]
     * code : 0
     */

    private int code;
    /**
     * id : 1480495419395738017
     * uid : 56338693
     * headImg : https://www.baidu.com/img/bd_logo1.png
     * nickname : CeShi
     * followed : true
     * addDate : 1480495419395
     */

    private List<ObjBean> obj;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ObjBean> getObj() {
        return obj;
    }

    public void setObj(List<ObjBean> obj) {
        this.obj = obj;
    }

    public static class ObjBean {
        private long id;
        private int uid;
        private String headImg;
        private String nickname;
        private boolean followed;
        private long addDate;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public boolean isFollowed() {
            return followed;
        }

        public void setFollowed(boolean followed) {
            this.followed = followed;
        }

        public long getAddDate() {
            return addDate;
        }

        public void setAddDate(long addDate) {
            this.addDate = addDate;
        }
    }
}
