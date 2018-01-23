package com.aou.cheba.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */
public class MyCode_data {

    /**
     * obj : [{"id":1481078679109746021,"uid":57800456,"addtime":1481078679109,"title":"灵敏","pictrue":"http://192.168.0.253:4888/download/0a503167f818ec46d0674409be07954f,http://192.168.0.253:4888/download/0a503167f818ec46d0674409be07954f,http://192.168.0.253:4888/download/575063bd66069219a009ffc79c1ea063,http://192.168.0.253:4888/download/c3f4a09c6cdb8ff6113c91c6defd2ee6","content":"获得各个","template":1,"upCount":0,"commentNum":0,"location":"车友558905","nickname":"车友558905","headImg":"http://192.168.0.253:4888/download/575063bd66069219a009ffc79c1ea063","uped":false,"collected":false,"gender":1,"followed":false}]
     * code : 0
     */

    private int code;
    /**
     * id : 1481078679109746021
     * uid : 57800456
     * addtime : 1481078679109
     * title : 灵敏
     * pictrue : http://192.168.0.253:4888/download/0a503167f818ec46d0674409be07954f,http://192.168.0.253:4888/download/0a503167f818ec46d0674409be07954f,http://192.168.0.253:4888/download/575063bd66069219a009ffc79c1ea063,http://192.168.0.253:4888/download/c3f4a09c6cdb8ff6113c91c6defd2ee6
     * content : 获得各个
     * template : 1
     * upCount : 0
     * commentNum : 0
     * location : 车友558905
     * nickname : 车友558905
     * headImg : http://192.168.0.253:4888/download/575063bd66069219a009ffc79c1ea063
     * uped : false
     * collected : false
     * gender : 1
     * followed : false
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

    public static class ObjBean implements Serializable{
        private long id;
        private int uid;
        private long addtime;
        private String title;
        private String pictrue;
        private String content;
        private int template;
        private int upCount;
        private int commentNum;
        private String location;
        private String nickname;
        private String headImg;
        private boolean uped;
        private boolean collected;
        private int gender;
        private int type;
        private boolean followed;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public long getAddtime() {
            return addtime;
        }

        public void setAddtime(long addtime) {
            this.addtime = addtime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPictrue() {
            return pictrue;
        }

        public void setPictrue(String pictrue) {
            this.pictrue = pictrue;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getTemplate() {
            return template;
        }

        public void setTemplate(int template) {
            this.template = template;
        }

        public int getUpCount() {
            return upCount;
        }

        public void setUpCount(int upCount) {
            this.upCount = upCount;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(int commentNum) {
            this.commentNum = commentNum;
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

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public boolean isUped() {
            return uped;
        }

        public void setUped(boolean uped) {
            this.uped = uped;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public boolean isFollowed() {
            return followed;
        }

        public void setFollowed(boolean followed) {
            this.followed = followed;
        }
    }
}
