package com.aou.cheba;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.aou.cheba.Activity.MainActivity;
import com.aou.cheba.Service.LocationService;
import com.baidu.mapapi.SDKInitializer;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 * <p/>
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 * <p/>
 * 直接拷贝com.baidu.location.service包到自己的工程下，简单配置即可获取定位结果，也可以根据demo内容自行封装
 */
public class LocationApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;
    protected static LocationApplication instance;
    public static PushAgent mPushAgent;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
//        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

//友盟推送服务
        mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.i("test_token","success"+deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i("test_token_failure",s+" s1 "+s1);
            }
        });

    }

//    The number of method references in a .dex file cannot exceed 64K.   解决
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            restartApp();//发生崩溃异常时,重启应用
        }
    };

    //用于重启app
    public void restartApp() {
        Intent intent = new Intent(instance, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        instance.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

}
