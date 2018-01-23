package com.aou.cheba.bean;

/**
 * Created by Administrator on 2016/10/25.
 */
public class MyCodeIma {

    /**
     * ret : true
     * info : {"md5":"5f189d8ec57f5a5a0d3dcba47fa797e2","size":29615}
     */

    private boolean ret;
    /**
     * md5 : 5f189d8ec57f5a5a0d3dcba47fa797e2
     * size : 29615
     */

    private InfoBean info;

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        private String md5;
        private int size;

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
