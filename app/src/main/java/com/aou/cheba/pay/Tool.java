package com.aou.cheba.pay;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Tool {
    public static Context context;
    public static String PROJECT_ITEM = "40046";
    public static Activity mActivity;
    /***********************
     * 001:斯凯
     * <p>
     * <p>
     * 008 TCL
     */
    public static String BILLING_ID = "013";
    public static Handler handler = new Handler();
    //是否显示LOG
    public static boolean ShowLog = false;
    public static int CHANNEL_TYPE = 1;

    public static final String AddSdks = "001,016,017";
    public static String SupportSdk[] = {"null", "null", "null", "null"};

    //public static String USERURL = "http://120.76.223.181:83/InterFace/ServerInterFace.aspx?code=";
    public static final String URL = "http://120.76.223.181:83/NewInterface/";
    private static int sendUserTime = 0;

    public static void Init(Activity act) {
        context = act;
        mActivity = act;
        getSupportSdk();
        SendUserInfo();
        CheckInfo();
    }

    public static void CheckInfo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String signNumber = "";
                try {
                    PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(
                            mActivity.getPackageName(), PackageManager.GET_SIGNATURES);
                    Signature[] signs = packageInfo.signatures;
                    signNumber = signs[0].hashCode() + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String net = "http://120.76.223.181:83/Interface/olt.aspx?" + "item=" + Tool.PROJECT_ITEM + "&" + "package=" + Tool.getPackageID() + "&" + "zmid=" + Tool.getZMID() + "&" + "zyfid=" + Tool.getZYFID() + "&" + "version=" + getAppVersionName(context);
                //String net = "http://192.168.0.111:83/Interface/olt.aspx?"+"item="+Tool.PROJECT_ITEM+"&"+"package="+Tool.getPackageID()+"&"+"zmid="+Tool.getZMID()+"&"+"zyfid="+Tool.getZYFID();
                String getStr = Tool.getStrFromUrlByPar(net);
                String mInfo = null;
                String url = null;
                if (Tool.ShowLog) {
                    Log.e("test", "getStr:" + getStr);
                }
                if (getStr != null) {
                    try {
                        JSONObject jsonobject = new JSONObject(getStr);
                        mInfo = jsonobject.getString("minfo");
                        url = jsonobject.getString("url");
                        Tool.mActivity.getSharedPreferences("olt", 0).edit().putBoolean("olt1", jsonobject.getBoolean("status")).commit();
                        Tool.mActivity.getSharedPreferences("olt", 0).edit().putBoolean("olt2", jsonobject.getBoolean("status1")).commit();
                        Tool.mActivity.getSharedPreferences("olt", 0).edit().putBoolean("olt3", jsonobject.getBoolean("status2")).commit();
                        Tool.mActivity.getSharedPreferences("isupdate", 0).edit().putBoolean("isupdate", jsonobject.getBoolean("isupdate")).commit();
                        Tool.mActivity.getSharedPreferences("isupdate", 0).edit().putString("mInfo", mInfo).commit();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (Tool.mActivity.getSharedPreferences("isupdate", 0).getBoolean("isupdate", false)) {
                    if (mInfo == null) {
                        mInfo = Tool.mActivity.getSharedPreferences("isupdate", 0).getString("mInfo", "");
                    }
                }
            }
        }).start();
    }

    public static String getZMID() {
        return getAssetsFolderData("ZM_ChannelID");
    }

    public static String getZYFID() {
        String channelId_Assets = "ZYFID";
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("infos.cfg");
            if (inputStream == null) {
                return "ERROR";
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            channelId_Assets = (String) properties.get("C");
        } catch (Exception e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        Log.e("test", "channelId_Assets:" + channelId_Assets);
        return channelId_Assets;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static void getSupportSdk() {
        String sdkstr = mActivity.getSharedPreferences("sdktype",
                Activity.MODE_PRIVATE).getString("sdktype", AddSdks);
        if (!sdkstr.equalsIgnoreCase("000")) {
            String sdks[] = sdkstr.split(",");
            for (int i = 0; i < sdks.length; i++) {
                SupportSdk[i] = sdks[i];
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Random randobj = new Random();
                int rand = randobj.nextInt();
                String Md5sing = Tool.MD5("001" + Tool.PROJECT_ITEM + Tool.getPackageID() + Tool.getImsi() + Tool.getICCID() + AddSdks + rand + Tool.getImei()).substring(0, 2);
                String par = null;
                par = "001#" + Tool.PROJECT_ITEM + "&" + Tool.getPackageID() + "&" + Tool.getImsi() + "&" + Tool.getICCID() + "&" + AddSdks + "&" + rand + "&" + Tool.getImei() + "&" + Md5sing;
                String net = "";
                try {
//                    net = URL + "calc9.aspx?code=" + URLEncoder.encode(Interface.getStr(par), "utf-8");
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String getStr = Tool.getStrFromUrlByPar(net);

                if (Tool.ShowLog) {
                    Log.e("test", "getStr:" + getStr);
                }
                try {
//                    getStr = Interface.getStrEx(getStr);
                    String pars[] = getStr.split("#");
                    String state = pars[1].split("&")[0];
                    if (state.equals("1")) {
                        String sdkstr = pars[1].split("&")[1];
                        String sdks[] = sdkstr.split(",");
                        int i = 0;
                        for (i = 0; i < sdks.length; i++) {
                            SupportSdk[i] = sdks[i];
                        }
                        for (; i < SupportSdk.length; i++) {
                            SupportSdk[i] = "null";
                        }
                        Tool.mActivity.getSharedPreferences("sdktype",
                                Activity.MODE_PRIVATE).edit().putString("sdktype", sdkstr).commit();

                    }
                    if (pars.length > 2) {
                        Tool.mActivity.getSharedPreferences("ysec",
                                Activity.MODE_PRIVATE).edit().putInt("ysec", Integer.parseInt(pars[2])).commit();
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public static void SendUserInfo() {
        sendUserTime = 0;
        final Timer timer = new Timer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        String netStr = null;
                        try {
                            String Md5sing = MD5("001" + PROJECT_ITEM + getBusinessID() + getPackageID() + getImsi() + getImei()).substring(0, 2);
                            netStr = "001#" + PROJECT_ITEM + "&" + getBusinessID() + "&" + getPackageID()
                                    + "&" + getImsi() + "&" + getImei() + "&" + getPhoneType() + "&" + getPhoneResolution() + "&" + BILLING_ID + "&" + getSimIsValid() + "&" + Md5sing + "&" + getICCID();
//                            netStr = URL + "calc8.aspx?code=" + URLEncoder.encode(Interface.getStr(netStr), "utf-8");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        String result = getStrFromUrlByPar(netStr);

                        if (ShowLog)
                            Log.e("test", "SendUserInfo:" + sendUserTime + ";result:" + result);
                        if (result != null) {
//                            result = Interface.getStrEx(result);
                            if (result.equalsIgnoreCase("001#true")) {
                                timer.cancel();
                                return;
                            }
                        }
                        sendUserTime++;
                        if (sendUserTime > 30) {
                            timer.cancel();
                            return;
                        }
                    }
                }, 1, 10000);
            }
        }).start();
    }

    public static void SendBillingResult(final String orderid, final int proid, final int price, final int state, final String billingid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String datestr = formatter.format(date);
                String Md5sing = MD5("002" + PROJECT_ITEM + getBusinessID() + getPackageID() + getImsi() + getImei()).substring(0, 2);
                String str = "002#" + PROJECT_ITEM + "&" + getBusinessID() + "&" + getPackageID() + "&" + getImsi() + "&" + getImei() + "&" + orderid + "&" + proid + "&" + price + "&" + state + "&" + datestr + "&" + billingid + "&" + getICCID() + "&" + Md5sing;

                try {
//                    str = URL + "calc8.aspx?code=" + URLEncoder.encode(Interface.getStr(str), "utf-8");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String result = Tool.getStrFromUrlByPar(str);
            }
        }).start();
    }

    public static String getICCID() {
        String iccid = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
        if (iccid == null || iccid.equals("")) {
            iccid = "NULL";
        }
        return iccid;
    }

    /**
     * 申请设备电源锁
     */
    public static void acquireWakeScreenLock() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    public static Context getCurContext() {
        return context;
    }

    /**
     * 释放设备电源锁
     */
    public static void releaseWakeScreenLock() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    public static String getBusinessID() {
        return getAssetsFolderData("commerceid");
    }

    public static String getPackageID() {
        return getAssetsFolderData("channelid");
    }

    public static String getImei() {
        String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (imei == null || imei.equals("")) {
            imei = "NULL";
        }
        return imei;
    }

    public static String getImsi() {
        String imsi = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
        if (imsi == null || imsi.equals("")) {
            imsi = "NULL";
        }
        return imsi;
    }

    public static String getPhoneType() {
        return android.os.Build.MODEL;
    }

    public static String getPhoneResolution() {

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return width + "x" + height;
    }

    public static String getAssetsFolderData(String fileName) {

        String string = null;

        InputStream is = null;

        try {
            is = context.getAssets().open(fileName);
            context.getAssets().getLocales();
            byte[] buffer = new byte[is.available()];
            // is.read(buffer,0,is.available());
            is.read(buffer);
            string = new String(buffer, "utf-8").trim();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        string = !(string == null || "".equals(string)) ? string : "ATY";
        //Log.e("test", "string:" + string);
        return string;
    }

    /**
     * 获取签名 返回string类型的值
     */
    public static String getPackageSignature() {
        Log.e("test", "getPackageSignature");
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> it = apps.iterator();
        while (it.hasNext()) {
            PackageInfo info = it.next();
            if (info.packageName.equals(context.getPackageName())) {
                Log.e("test", "getPackageSignature1");
                return info.signatures[0].toCharsString();
            }
        }
        return null;
    }

    public static String getSimIsValid() {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = tManager.getSimState();
        if (state == TelephonyManager.SIM_STATE_READY) {
            return "1";
        } else {
            return "0";
        }
    }

    public static String getDBVersion() {
        return getAssetsFolderData("DBVersion");

    }

    public static boolean isRunning(String packageName) {
        /*ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo rapi : infos) {
			if (rapi.processName.equals(packageName))
				return true;
		}*/
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        if (info.topActivity.getPackageName().equalsIgnoreCase(packageName)) {
            return true;
        }
        return false;
    }

    public static String getVersionName() {
        String versionCode = null;
        try {
            versionCode = "" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取assets文件夹下的文件内容
     *
     * @param context
     * @param fileName 文件名
     * @return String 返回文件里面的内容
     */
    public static String getAssetsFolderData(Context context, String fileName) {

        String str = "ATY";

        InputStream is = null;

        try {
            is = context.getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            // is.read(buffer,0,is.available());
            is.read(buffer);
            str = new String(buffer, "utf-8").trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return !"".equals(str) ? str : "ATY";
    }

    public static String getProjectItem() {
        return PROJECT_ITEM;
    }

    public static String getBillingItem() {
        return BILLING_ID;
    }

    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            //byte[] btInput = s.getBytes("utf-8");
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStrFromUrlByPar(String netStr) {
        String str = null;
        try {
            // 设置请求的地址 通过URLEncoder.encode(String s, String enc)
            java.net.URL url = new URL(netStr);
            // url.openConnection()打开网络链接
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");// 设置请求的方式
            urlConnection.setReadTimeout(5000);// 设置超时的时间
            urlConnection.setConnectTimeout(5000);// 设置链接超时的时间
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            // 获取响应的状态码 404 200 505 302
            int state = urlConnection.getResponseCode();
            if (state == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();

                // 创建字节输出流对象
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    os.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                os.close();
                // 返回字符串
                str = new String(os.toByteArray());
            } else {
                System.out.println("------------------链接失败-----------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void showExitView() {
        Tool.handler.post(new Runnable() {
            @Override
            public void run() {
                Builder builder = new Builder(Tool.mActivity);
                builder.setTitle("提示");
                builder.setMessage("确定退出?");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setPositiveButton("确定", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                builder.setNegativeButton("取消", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    public static boolean getIsTip() {
        if (CHANNEL_TYPE == 14 || CHANNEL_TYPE == 16)
            return Tool.mActivity.getSharedPreferences("IsSecondEx", Activity.MODE_PRIVATE).getBoolean("tips", false);
        return true;
    }

    public static boolean isAppInstalled(String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static String getNickname() {
        return "我的名字";
    }

    public static String getUserID() {
        return "888";
    }

    public static String getHeadImage() {
        return "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png";
    }

    public static int getDianquan() {
        return 100;
    }

    public static String getGameitem() {
        return "10000";
    }
}
