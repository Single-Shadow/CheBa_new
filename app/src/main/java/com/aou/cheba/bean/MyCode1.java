package com.aou.cheba.bean;

/**
 * Created by Administrator on 2016/10/19.
 */
public class MyCode1 {

    /**
     * obj : null
     * code : 0
     */

    private Object obj;
    private int code;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "MyCode1{" +
                "obj=" + obj +
                ", code=" + code +
                '}';
    }
}
