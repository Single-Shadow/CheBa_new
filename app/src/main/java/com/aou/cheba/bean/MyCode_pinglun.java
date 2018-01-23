package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/1.
 */
public class MyCode_pinglun {

    /**
     * code : 0
     * obj : [{"content":"CeShi","date":1480569176360,"did":1480564125430432175,"headImg":"https://www.baidu.com/img/bd_logo1.png","id":1480569176360423658,"nickname":"CeShi","subList":[{"content":"ceshiss12345678916","date":1480659982929,"did":1480651500436670523,"headImg":"http://192.168.0.253:4888","id":1480659982929809126,"nickname":"车友570461","pid":1480659765236398404,"uid":21085836,"upCount":0}],"uid":56338693,"upCount":0},{"content":"ceshi","date":1480570442660,"did":1480564125430432175,"headImg":"https://www.baidu.com/img/bd_logo1.png","id":1480570442660603026,"nickname":"CeShi","pid":1480564125430432172,"subList":[],"uid":56338693,"upCount":0}]
     */

    private int code;
    /**
     * content : CeShi
     * date : 1480569176360
     * did : 1480564125430432175
     * headImg : https://www.baidu.com/img/bd_logo1.png
     * id : 1480569176360423658
     * nickname : CeShi
     * subList : [{"content":"ceshiss12345678916","date":1480659982929,"did":1480651500436670523,"headImg":"http://192.168.0.253:4888","id":1480659982929809126,"nickname":"车友570461","pid":1480659765236398404,"uid":21085836,"upCount":0}]
     * uid : 56338693
     * upCount : 0
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
        private int uid;
        private Integer uped;
        private int upCount;
        /**
         * content : ceshiss12345678916
         * date : 1480659982929
         * did : 1480651500436670523
         * headImg : http://192.168.0.253:4888
         * id : 1480659982929809126
         * nickname : 车友570461
         * pid : 1480659765236398404
         * uid : 21085836
         * upCount : 0
         */

        private List<SubListBean> subList;

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

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public Integer getUped() {
            return uped;
        }

        public void setUped(Integer uped) {
            this.uped = uped;
        }

        public int getUpCount() {
            return upCount;
        }

        public void setUpCount(int upCount) {
            this.upCount = upCount;
        }

        public List<SubListBean> getSubList() {
            return subList;
        }

        public void setSubList(List<SubListBean> subList) {
            this.subList = subList;
        }

        public static class SubListBean {
            private String content;
            private long date;
            private long did;
            private String headImg;
            private long id;
            private String nickname;
            private long pid;
            private int uid;
            private Integer upCount;

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

            public long getPid() {
                return pid;
            }

            public void setPid(long pid) {
                this.pid = pid;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public Integer getUpCount() {
                return upCount;
            }

            public void setUpCount(Integer upCount) {
                this.upCount = upCount;
            }
        }
    }
}
