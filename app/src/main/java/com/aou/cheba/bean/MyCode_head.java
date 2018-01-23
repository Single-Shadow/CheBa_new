package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/24.
 */
public class MyCode_head {

    /**
     * code : 0
     * obj : [{"id":1481269938157283652,"pictrue":"http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d","title":"测试标题","type":1,"url":""},{"id":1481768104266378642,"pictrue":"http://192.168.0.253:4888/download/07b9c856e1bd70608f5a018d65791f22","title":"测试标题","type":1,"url":""},{"id":1481768104266378643,"pictrue":"https://www.baidu.com/img/bd_logo1.png","title":"测试标题URL","type":2,"url":"https://www.baidu.com/"},{"id":1481768104266378644,"pictrue":"https://www.baidu.com/img/bd_logo2.png","title":"测试标题URL","type":2,"url":"https://www.baidu.com"},{"id":1481768104266378641,"pictrue":"https://www.baidu.com/img/bd_logo3.png","title":"测试标题URL","type":2,"url":"https://www.baidu.com"}]
     */

    private int code;
    /**
     * id : 1481269938157283652
     * pictrue : http://192.168.0.253:4888/download/19232e30166c76bd1a45940fb3e0301d
     * title : 测试标题
     * type : 1
     * url :
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
        private long id;
        private String pictrue;
        private String title;
        private int type;
        private String url;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getPictrue() {
            return pictrue;
        }

        public void setPictrue(String pictrue) {
            this.pictrue = pictrue;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
