package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Wang on 2017/2/21.
 */
public class MyCode_che {

    /**
     * code : 0
     * obj : [{"id":1000,"name":"A 奥迪"},{"id":1001,"name":"A 阿斯顿·马丁"}]
     */

    private int code;
    /**
     * id : 1000
     * name : A 奥迪
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
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
