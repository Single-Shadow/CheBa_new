package com.aou.cheba.utils;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 */
public class LocationUtils {

    //public static String cityName = "深圳";  //城市名
    public static String cityName ;  //城市名

    private static Geocoder geocoder;   //此对象能通过经纬度来获取相应的城市等信息

    /**
     * 通过地理坐标获取城市名  其中CN分别是city和name的首字母缩写
     * @param context
     */
    public static void getCNBylocation(Context context){

        geocoder = new Geocoder(context);
        //用于获取Location对象，以及其他
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        //实例化一个LocationManager对象
        locationManager = (LocationManager)context.getSystemService(serviceName);
        //provider的类型
      //  String provider = LocationManager.NETWORK_PROVIDER;

        String provider;
        List<String> providers = locationManager.getProviders(true);
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            provider = LocationManager.GPS_PROVIDER;
            Log.i("test", "GPS");
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            provider = LocationManager.NETWORK_PROVIDER;
            Log.i("test","Network");
        }else{
            Log.i("test", "没有可用的位置提供器");
          //  Toast.makeText(context, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return ;
        }

        String bestProvider = locationManager.getBestProvider(getCriteria(),true);
        Location location = locationManager.getLastKnownLocation(bestProvider);


      /*  LocationClientOption option = new LocationClientOption();
        //就是这个方法设置为true，才能获取当前的位置信息
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span = 1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);*/

        //通过最后一次的地理位置来获得Location对象
        Log.i("test","provider:"+provider);

        if (location == null) {

             location = locationManager.getLastKnownLocation(provider);
        }

        if (location!=null){
            Log.i("test", "维度：" + location.getLatitude() +"\n" + "经度：" + location.getLongitude());
        }else {
            Log.i("test","location为空");
        }


        String queryed_name = updateWithNewLocation(location);
        if((queryed_name != null) && (0 != queryed_name.length())){

            cityName = queryed_name;
        }

        /*
         * 第二个参数表示更新的周期，单位为毫秒；第三个参数的含义表示最小距离间隔，单位是米
         * 设定每30秒进行一次自动定位
         */
     //   locationManager.requestLocationUpdates(provider, 30000, 50,locationListener);
        //移除监听器，在只有一个widget的时候，这个还是适用的
    //    locationManager.removeUpdates(locationListener);
    }

    /**
     * 方位改变时触发，进行调用
     */
    private final static LocationListener locationListener = new LocationListener() {
        String tempCityName;
        public void onLocationChanged(Location location) {

            tempCityName = updateWithNewLocation(location);
            if((tempCityName != null) && (tempCityName.length() != 0)){

                cityName = tempCityName;
            }
        }

        public void onProviderDisabled(String provider) {
            tempCityName = updateWithNewLocation(null);
            if ((tempCityName != null) && (tempCityName.length() != 0)) {

                cityName = tempCityName;
            }
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 更新location
     * @param location
     * @return cityName
     */
    private static String updateWithNewLocation(Location location) {
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {

            System.out.println("无法获取地理信息");
        }

        try {

            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mcityName += add.getLocality();
            }
        }
        if(mcityName.length()!=0){

            return mcityName.substring(0, (mcityName.length()-1));
        } else {
            return mcityName;
        }
    }

    /**
     * 通过经纬度获取地址信息的另一种方法
     * @param latitude
     * @param longitude
     * @return 城市名
     */
    public static String GetAddr(String latitude, String longitude) {
        String addr = "";

        /*
         * 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址
         * 密钥可以随便写一个key=abc
         * output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析
         */
        String url = String.format(
                "http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",
                latitude, longitude);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {

            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {

            httpsConn = (URLConnection) myURL.openConnection();

            if (httpsConn != null) {
                InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(insr);
                String data = null;
                if ((data = br.readLine()) != null) {
                    String[] retList = data.split(",");
                    if (retList.length > 2 && ("200".equals(retList[0]))) {
                        addr = retList[2];
                    } else {
                        addr = "";
                    }
                }
                insr.close();
            }
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
        return addr;
    }

    public static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}