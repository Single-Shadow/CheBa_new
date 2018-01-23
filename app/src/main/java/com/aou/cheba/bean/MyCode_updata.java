package com.aou.cheba.bean;

/**
 * Created by Administrator on 2016/12/26.
 */
public class MyCode_updata {

    /**
     * code : 0
     * obj : {"apk_size":9,"apk_url":"00000000","version_code":1132,"version_date":1482719814,"version_desc":"版本测试","version_name":"V1.1.32","version_type":0}
     */

    private int code;
    /**
     * apk_size : 9
     * apk_url : 00000000
     * version_code : 1132
     * version_date : 1482719814
     * version_desc : 版本测试
     * version_name : V1.1.32
     * version_type : 0
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

    public static class ObjBean {
        private int apk_size;
        private String apk_url;
        private int version_code;
        private int version_date;
        private String version_desc;
        private String version_name;
        private int version_type;

        public int getApk_size() {
            return apk_size;
        }

        public void setApk_size(int apk_size) {
            this.apk_size = apk_size;
        }

        public String getApk_url() {
            return apk_url;
        }

        public void setApk_url(String apk_url) {
            this.apk_url = apk_url;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public int getVersion_date() {
            return version_date;
        }

        public void setVersion_date(int version_date) {
            this.version_date = version_date;
        }

        public String getVersion_desc() {
            return version_desc;
        }

        public void setVersion_desc(String version_desc) {
            this.version_desc = version_desc;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public int getVersion_type() {
            return version_type;
        }

        public void setVersion_type(int version_type) {
            this.version_type = version_type;
        }
    }
}
