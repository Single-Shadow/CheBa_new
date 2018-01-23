package com.aou.cheba.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */
public class MyCode_News {


    /**
     * content : 近年来你
     * did : 1482477111523596556
     * headImg : http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d
     * nickname : 爱你我不能说
     * pictrue :
     * recvTime : 111111111111
     * type : 3
     * uid : 68530893
     */

    private List<DataBean> data=new ArrayList<DataBean>();

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String content;
        private String did;
        private String headImg;
        private String nickname;
        private String pictrue;
        private long recvTime;
        private String type;
        private String uid;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
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

        public String getPictrue() {
            return pictrue;
        }

        public void setPictrue(String pictrue) {
            this.pictrue = pictrue;
        }

        public long getRecvTime() {
            return recvTime;
        }

        public void setRecvTime(long recvTime) {
            this.recvTime = recvTime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }
}
