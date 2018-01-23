package com.aou.cheba.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode_updata;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.DownLoadManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VersionActivity extends Activity {
    private final String TAG = "test";
    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    private Button getVersion;
    private int localVersion;
    private MyCode_updata mycode;
    private Handler h = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_first);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    localVersion = getVersionCode();
                    Log.i("test","版本测试shuju  "+localVersion + " 当前版本号");
                    inithttp_version("");
                    //  CheckVersionTask cv = new CheckVersionTask();
                    //  new Thread(cv).start();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 1000);

    }

    private int getVersionCode() throws Exception {
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
                0);
        return packInfo.versionCode;
    }


    private void inithttp_version(String token) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + token + "\"}");
//创建一个请求对象

        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Client!LatestVersion.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();
                Gson gson = new Gson();
                mycode = gson.fromJson(res, MyCode_updata.class);
                if (mycode.getCode() == 0) {
                    if (mycode.getObj() == null) {
                        Log.i("test","空");
                        Message msg = new Message();
                        msg.what = UPDATA_NONEED;
                        handler.sendMessage(msg);
                        return;
                    }
                    Log.i(TAG, localVersion + "当前版本号");
                    Log.i(TAG, mycode.getObj().getVersion_code() + "获取版本号");
                    Log.i(TAG, mycode.getObj().getApk_url() + "下载链接");
                    if (localVersion == mycode.getObj().getVersion_code()) {
                        Log.i(TAG, "版本号相同");
                        Message msg = new Message();
                        msg.what = UPDATA_NONEED;
                        handler.sendMessage(msg);
                        // LoginMain();
                    } else {
                        Log.i(TAG, "版本号不相同asdas ");
                        Message msg = new Message();
                        msg.what = UPDATA_CLIENT;
                        handler.sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    msg.what = GET_UNDATAINFO_ERROR;
                    handler.sendMessage(msg);
                }

            }
        });
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_NONEED:
                    finish();
                    startActivity(new Intent(VersionActivity.this, MainActivity.class));
                    break;
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(VersionActivity.this, MainActivity.class));
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(VersionActivity.this, MainActivity.class));
                    break;
            }
        }
    };

    /*
     *
     * 弹出对话框通知用户更新程序
     *
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdataDialog() {
        AlertDialog.Builder builer = new Builder(this);
        builer.setTitle("版本升级");
        builer.setMessage(mycode.getObj().getVersion_desc());
        //当点确定按钮时从服务器上下载 新的apk 然后安装   װ
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载apk,更新");
                downLoadApk();
            }
        });
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //do sth
                finish();
                startActivity(new Intent(VersionActivity.this, MainActivity.class));
            }
        });
        AlertDialog dialog = builer.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, mycode.getObj().getApk_url());
                    File file = DownLoadManager.getFileFromServer(mycode.getObj().getApk_url(), pd, mycode.getObj().getApk_size());
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
        finish();// 没写finish的话不会弹出完成和打开
    }
}
