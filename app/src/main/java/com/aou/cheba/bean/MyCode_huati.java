package com.aou.cheba.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/24.
 */
public class MyCode_huati {

    /**
     * code : 0
     * obj : {"addtime":1481267340238,"collected":false,"commentNum":3,"content":"南京路统计局","followed":true,"gender":1,"headImg":"http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d","id":1481267340237546812,"location":"深圳","nickname":"爱你我不能说","pictrue":"http://192.168.0.253:4888/download/9784061a01f7fe54f72476819e4d7511,http://192.168.0.253:4888/download/9784061a01f7fe54f72476819e4d7511,http://192.168.0.253:4888/download/575063bd66069219a009ffc79c1ea063,http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d","template":1,"title":"慢慢来嘛","uid":68530893,"upCount":2,"uped":false}
     */

    private int code;
    /**
     * addtime : 1481267340238
     * collected : false
     * commentNum : 3
     * content : 南京路统计局
     * followed : true
     * gender : 1
     * headImg : http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d
     * id : 1481267340237546812
     * location : 深圳
     * nickname : 爱你我不能说
     * pictrue : http://192.168.0.253:4888/download/9784061a01f7fe54f72476819e4d7511,http://192.168.0.253:4888/download/9784061a01f7fe54f72476819e4d7511,http://192.168.0.253:4888/download/575063bd66069219a009ffc79c1ea063,http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d
     * template : 1
     * title : 慢慢来嘛
     * uid : 68530893
     * upCount : 2
     * uped : false
     */

    private ObjBean obj;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public static class ObjBean implements Serializable {
        private long addtime;
        private boolean collected;
        private int commentNum;
        private String content;
        private boolean followed;
        private int gender;
        private String headImg;
        private long id;
        private String location;
        private String nickname;
        private String pictrue;
        private int template;
        private String title;
        private int uid;
        private int type;
        private int upCount;
        private boolean uped;

        public long getAddtime() {
            return addtime;
        }

        public void setAddtime(long addtime) {
            this.addtime = addtime;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(int commentNum) {
            this.commentNum = commentNum;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isFollowed() {
            return followed;
        }

        public void setFollowed(boolean followed) {
            this.followed = followed;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPictrue() {
            return pictrue;
        }

        public void setPictrue(String pictrue) {
            this.pictrue = pictrue;
        }

        public int getTemplate() {
            return template;
        }

        public void setTemplate(int template) {
            this.template = template;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getUpCount() {
            return upCount;
        }

        public void setUpCount(int upCount) {
            this.upCount = upCount;
        }

        public boolean isUped() {
            return uped;
        }

        public void setUped(boolean uped) {
            this.uped = uped;
        }
    }
}
