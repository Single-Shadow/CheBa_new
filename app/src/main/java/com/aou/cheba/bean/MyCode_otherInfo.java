package com.aou.cheba.bean;

/**
 * Created by Administrator on 2016/12/8.
 */
public class MyCode_otherInfo {

    /**
     * followCount : 2
     * gender : 1
     * headImg : http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d
     * level : 6
     * signature : 你懂的歌是什么线谱
     * regDate : 1481015209730
     * fansCount : 2
     * bgImg : http://192.168.0.253:4888/download/b55147fa44fb54f7f937419ab5686d7d
     * collected : false
     * updDate : 1481163302534
     * uid : 68530893
     * nickname : 爱你我不能说
     * carInfo : 爱你我不能说
     */

    private ObjBean obj;
    /**
     * obj : {"followCount":2,"gender":1,"headImg":"http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d","level":6,"signature":"你懂的歌是什么线谱","regDate":1481015209730,"fansCount":2,"bgImg":"http://192.168.0.253:4888/download/b55147fa44fb54f7f937419ab5686d7d","collected":false,"updDate":1481163302534,"uid":68530893,"nickname":"爱你我不能说","carInfo":"爱你我不能说"}
     * code : 0
     */

    private int code;

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ObjBean {
        private int followCount;
        private int gender;
        private String headImg;
        private int level;
        private String signature;
        private long regDate;
        private int fansCount;
        private String bgImg;
        private boolean collected;
        private long updDate;
        private int uid;
        private String nickname;
        private String carInfo;

        public int getFollowCount() {
            return followCount;
        }

        public void setFollowCount(int followCount) {
            this.followCount = followCount;
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

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public long getRegDate() {
            return regDate;
        }

        public void setRegDate(long regDate) {
            this.regDate = regDate;
        }

        public int getFansCount() {
            return fansCount;
        }

        public void setFansCount(int fansCount) {
            this.fansCount = fansCount;
        }

        public String getBgImg() {
            return bgImg;
        }

        public void setBgImg(String bgImg) {
            this.bgImg = bgImg;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public long getUpdDate() {
            return updDate;
        }

        public void setUpdDate(long updDate) {
            this.updDate = updDate;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getCarInfo() {
            return carInfo;
        }

        public void setCarInfo(String carInfo) {
            this.carInfo = carInfo;
        }
    }
}
