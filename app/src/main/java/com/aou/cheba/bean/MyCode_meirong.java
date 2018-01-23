package com.aou.cheba.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wang on 2017/2/13.
 */
public class MyCode_meirong {

    /**
     * code : 0
     * obj : [{"add_time":1486971755000,"area":"白云区","city":"广州","collected":false,"fw":8.2,"hj":9,"id":1223372036854775837,"js":7.1,"pictrue":"http://192.168.0.253:4888/CarbarFileServer/html/images/753bdc870b06ad3419c6c842b3bfe76e,http://192.168.0.253:4888/CarbarFileServer/html/images/753bdc870b06ad3419c6c842b3bfe76e,http://192.168.0.253:4888/CarbarFileServer/html/images/ab3376a1271b5910ac61b37d837d7260,http://192.168.0.253:4888/CarbarFileServer/html/images/fa8496a9a8ce8b3fd43fdf9e35aacacf,http://192.168.0.253:4888/CarbarFileServer/html/images/f2ec96ea97eecd5f626ea925c4f633f8,http://192.168.0.253:4888/CarbarFileServer/html/images/a023be00daf5eb8779bdd68afc1e4450,http://192.168.0.253:4888/CarbarFileServer/html/images/e3563d29641a02db663252a63d4a2a27","region":"车公庙","score":3,"store_addr":"香梅路香蜜二村2栋A1","store_name":"牛牛汽车美容中心","store_tel":"63227546","store_type":"汽车维修","uped":false,"working_time":"8:00-21:00"}]
     */

    private int code;
    /**
     * add_time : 1486971755000
     * area : 白云区
     * city : 广州
     * collected : false
     * fw : 8.2
     * hj : 9
     * id : 1223372036854775837
     * js : 7.1
     * pictrue : http://192.168.0.253:4888/CarbarFileServer/html/images/753bdc870b06ad3419c6c842b3bfe76e,http://192.168.0.253:4888/CarbarFileServer/html/images/753bdc870b06ad3419c6c842b3bfe76e,http://192.168.0.253:4888/CarbarFileServer/html/images/ab3376a1271b5910ac61b37d837d7260,http://192.168.0.253:4888/CarbarFileServer/html/images/fa8496a9a8ce8b3fd43fdf9e35aacacf,http://192.168.0.253:4888/CarbarFileServer/html/images/f2ec96ea97eecd5f626ea925c4f633f8,http://192.168.0.253:4888/CarbarFileServer/html/images/a023be00daf5eb8779bdd68afc1e4450,http://192.168.0.253:4888/CarbarFileServer/html/images/e3563d29641a02db663252a63d4a2a27
     * region : 车公庙
     * score : 3
     * store_addr : 香梅路香蜜二村2栋A1
     * store_name : 牛牛汽车美容中心
     * store_tel : 63227546
     * store_type : 汽车维修
     * uped : false
     * working_time : 8:00-21:00
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

    public static class ObjBean implements Serializable{
        private long add_time;
        private String area;
        private String city;
        private boolean collected;
        private double fw;
        private double hj;
        private long id;
        private double js;
        private String pictrue;
        private String region;
        private int commentNum;
        private int score;
        private String store_addr;
        private String store_name;
        private String store_tel;
        private String store_type;
        private boolean uped;
        private String working_time;

        public int getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(int commentNum) {
            this.commentNum = commentNum;
        }

        public long getAdd_time() {
            return add_time;
        }

        public void setAdd_time(long add_time) {
            this.add_time = add_time;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public double getFw() {
            return fw;
        }

        public void setFw(double fw) {
            this.fw = fw;
        }

        public double getHj() {
            return hj;
        }

        public void setHj(double hj) {
            this.hj = hj;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public double getJs() {
            return js;
        }

        public void setJs(double js) {
            this.js = js;
        }

        public String getPictrue() {
            return pictrue;
        }

        public void setPictrue(String pictrue) {
            this.pictrue = pictrue;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getStore_addr() {
            return store_addr;
        }

        public void setStore_addr(String store_addr) {
            this.store_addr = store_addr;
        }

        public String getStore_name() {
            return store_name;
        }

        public void setStore_name(String store_name) {
            this.store_name = store_name;
        }

        public String getStore_tel() {
            return store_tel;
        }

        public void setStore_tel(String store_tel) {
            this.store_tel = store_tel;
        }

        public String getStore_type() {
            return store_type;
        }

        public void setStore_type(String store_type) {
            this.store_type = store_type;
        }

        public boolean isUped() {
            return uped;
        }

        public void setUped(boolean uped) {
            this.uped = uped;
        }

        public String getWorking_time() {
            return working_time;
        }

        public void setWorking_time(String working_time) {
            this.working_time = working_time;
        }
    }
}
