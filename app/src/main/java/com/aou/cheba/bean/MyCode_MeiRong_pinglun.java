package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Wang on 2017/2/14.
 */
public class MyCode_MeiRong_pinglun {

    /**
     * code : 0
     * obj : [{"content":"第2条评论","date":1487059358000,"did":1223372036854775837,"headImg":"http://192.168.0.253:4888/CarbarFileServer/download/d9af5a082e4fc840d1b56d374abe078d","id":122,"nickname":"爱我的人不少","pictrue":"http://192.168.0.253:4888/CarbarFileServer/html/images/a10da72bffe673b2dd0a98ae1f66d7b6,http://192.168.0.253:4888/CarbarFileServer/html/images/a10da72bffe673b2dd0a98ae1f66d7b6,http://192.168.0.253:4888/CarbarFileServer/html/images/84e265e6b4fdc6c95041567cef342e6d,http://192.168.0.253:4888/CarbarFileServer/html/images/5ad8741a0d8b7761f2516189bed2a6b8,http://192.168.0.253:4888/CarbarFileServer/html/images/7d20d359ab2269efc9bf4ce09c7f7528,http://192.168.0.253:4888/CarbarFileServer/html/images/7c1f39330e3f58e22d9945b03c095ec6,http://192.168.0.253:4888/CarbarFileServer/html/images/427a559abf241d71ca07a1cafa3b4f13,http://192.168.0.253:4888/CarbarFileServer/html/images/238767b56a04eab1c0544b0a8d127d4f,http://192.168.0.253:4888/CarbarFileServer/html/images/4d8caa1bd31e5757da282903e03ee2e3,http://192.168.0.253:4888/CarbarFileServer/html/images/d37f48d48e91455437d0cba025936805,http://192.168.0.253:4888/CarbarFileServer/html/images/d47551888414249be4a87e6ec0749265","score":2,"uid":10071734}]
     */

    private int code;
    /**
     * content : 第2条评论
     * date : 1487059358000
     * did : 1223372036854775837
     * headImg : http://192.168.0.253:4888/CarbarFileServer/download/d9af5a082e4fc840d1b56d374abe078d
     * id : 122
     * nickname : 爱我的人不少
     * pictrue : http://192.168.0.253:4888/CarbarFileServer/html/images/a10da72bffe673b2dd0a98ae1f66d7b6,http://192.168.0.253:4888/CarbarFileServer/html/images/a10da72bffe673b2dd0a98ae1f66d7b6,http://192.168.0.253:4888/CarbarFileServer/html/images/84e265e6b4fdc6c95041567cef342e6d,http://192.168.0.253:4888/CarbarFileServer/html/images/5ad8741a0d8b7761f2516189bed2a6b8,http://192.168.0.253:4888/CarbarFileServer/html/images/7d20d359ab2269efc9bf4ce09c7f7528,http://192.168.0.253:4888/CarbarFileServer/html/images/7c1f39330e3f58e22d9945b03c095ec6,http://192.168.0.253:4888/CarbarFileServer/html/images/427a559abf241d71ca07a1cafa3b4f13,http://192.168.0.253:4888/CarbarFileServer/html/images/238767b56a04eab1c0544b0a8d127d4f,http://192.168.0.253:4888/CarbarFileServer/html/images/4d8caa1bd31e5757da282903e03ee2e3,http://192.168.0.253:4888/CarbarFileServer/html/images/d37f48d48e91455437d0cba025936805,http://192.168.0.253:4888/CarbarFileServer/html/images/d47551888414249be4a87e6ec0749265
     * score : 2
     * uid : 10071734
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
        private String content;
        private long date;
        private long did;
        private String headImg;
        private long id;
        private String nickname;
        private String pictrue;
        private int score;
        private int uid;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public long getDid() {
            return did;
        }

        public void setDid(long did) {
            this.did = did;
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

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }
    }
}
