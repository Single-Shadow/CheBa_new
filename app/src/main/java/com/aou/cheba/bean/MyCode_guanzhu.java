package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public class MyCode_guanzhu {

    /**
     * obj : [{"fromId":68530893,"goId":46304300,"addDate":1481107285051,"nickname":"","headImg":"http://192.168.0.253:4888/download/c38c5a1eb3cf6c3e4d6aac280fb3dbf9"},{"fromId":68530893,"goId":99450868,"addDate":1481107246171,"nickname":"吾记之谈","headImg":"http://tva3.sinaimg.cn/crop.0.13.1242.1242.1024/006zx0zijw8f8ng81hjdvj30yi0z8dji.jpg"}]
     * code : 0
     */

    private int code;
    /**
     * fromId : 68530893
     * goId : 46304300
     * addDate : 1481107285051
     * nickname :
     * headImg : http://192.168.0.253:4888/download/c38c5a1eb3cf6c3e4d6aac280fb3dbf9
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
