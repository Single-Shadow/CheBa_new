package com.aou.cheba.bean;

/**
 * Created by Administrator on 2016/12/8.
 */
public class MyCode_qiandao {

    /**
     * uid : 57800456
     * signTime : 1481195391110
     * days : 4
     * score : 15
     * signed : false
     */

    private ObjBean obj;
    /**
     * obj : {"uid":57800456,"signTime":1481195391110,"days":4,"score":15,"signed":false}
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
        private int uid;
        private long signTime;
        private int days;
        private int score;
        private boolean signed;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public long getSignTime() {
            return signTime;
        }

        public void setSignTime(long signTime) {
            this.signTime = signTime;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public boolean isSigned() {
            return signed;
        }

        public void setSigned(boolean signed) {
            this.signed = signed;
        }
    }
}
