package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/8.
 */
public class MyCode_fensi {

    /**
     * obj : [{"fromId":68530893,"goId":57800456,"addDate":1481112922946,"collected":true,"nickname":"爱你我不能说","headImg":"http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d"}]
     * code : 0
     */

    private int code;
    /**
     * fromId : 68530893
     * goId : 57800456
     * addDate : 1481112922946
     * collected : true
     * nickname : 爱你我不能说
     * headImg : http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d
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
        private int fromId;
        private int goId;
        private long addDate;
        private boolean collected;
        private String nickname;
        private String headImg;

        public int getFromId() {
            return fromId;
        }

        public void setFromId(int fromId) {
            this.fromId = fromId;
        }

        public int getGoId() {
            return goId;
        }

        public void setGoId(int goId) {
            this.goId = goId;
        }

        public long getAddDate() {
            return addDate;
        }

        public void setAddDate(long addDate) {
            this.addDate = addDate;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
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
    }
}
