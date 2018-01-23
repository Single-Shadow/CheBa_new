package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/21.
 */
public class MyCode_fankui {

    /**
     * obj : [{"id":"123","uid":17615637,"content":"APP反馈测试","handle":"已通过","addDate":"2016-11-21 09:47:17"},{"id":"1234","uid":17615637,"content":"APP反馈测试2","handle":"已通过2","addDate":"2016-11-21 09:47:17"}]
     * code : 0
     */

    private int code;
    /**
     * id : 123
     * uid : 17615637
     * content : APP反馈测试
     * handle : 已通过
     * addDate : 2016-11-21 09:47:17
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
        private String id;
        private int uid;
        private String content;
        private String handle;
        private String addDate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getHandle() {
            return handle;
        }

        public void setHandle(String handle) {
            this.handle = handle;
        }

        public String getAddDate() {
            return addDate;
        }

        public void setAddDate(String addDate) {
            this.addDate = addDate;
        }
    }
}
