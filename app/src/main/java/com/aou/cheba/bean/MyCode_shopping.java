package com.aou.cheba.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/22.
 */
public class MyCode_shopping {

    /**
     * obj : [{"id":100020,"goods_name":"银色壁虎车贴","goods_img":"http://120.25.59.184:4888/CarbarFileServer/download/5639c2a7Ne74a0427","goods_img_large":"http://120.25.59.184:4888/CarbarFileServer/download/5639c2a7Ne74a0427","goods_price":12,"goods_vip_price":11.4,"addDate":1503386003000}]
     * code : 0
     */

    private int code;
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
        /**
         * id : 100020
         * goods_name : 银色壁虎车贴
         * goods_img : http://120.25.59.184:4888/CarbarFileServer/download/5639c2a7Ne74a0427
         * goods_img_large : http://120.25.59.184:4888/CarbarFileServer/download/5639c2a7Ne74a0427
         * goods_price : 12
         * goods_vip_price : 11.4
         * addDate : 1503386003000
         */

        private int id;
        private String goods_name;
        private String goods_img;
        private String goods_img_large;
        private double goods_price;
        private double goods_vip_price;
        private long addDate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getGoods_img() {
            return goods_img;
        }

        public void setGoods_img(String goods_img) {
            this.goods_img = goods_img;
        }

        public String getGoods_img_large() {
            return goods_img_large;
        }

        public void setGoods_img_large(String goods_img_large) {
            this.goods_img_large = goods_img_large;
        }

        public double getGoods_price() {
            return goods_price;
        }

        public void setGoods_price(double goods_price) {
            this.goods_price = goods_price;
        }

        public double getGoods_vip_price() {
            return goods_vip_price;
        }

        public void setGoods_vip_price(double goods_vip_price) {
            this.goods_vip_price = goods_vip_price;
        }

        public long getAddDate() {
            return addDate;
        }

        public void setAddDate(long addDate) {
            this.addDate = addDate;
        }
    }
}
