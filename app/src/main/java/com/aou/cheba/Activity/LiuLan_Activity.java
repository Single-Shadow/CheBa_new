package com.aou.cheba.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import com.aou.cheba.R;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RollHeaderView_liulan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/9/18.
 */
public class LiuLan_Activity extends Activity {

    private RollHeaderView_liulan rh;
    private String[] split;
    List<String> listUrl = new ArrayList();
    Bitmap bitmap;
    int p;
    String s = "";

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                String path = Environment.getExternalStorageDirectory().getPath();

                if ("xiazai".equals(s)) {

//保存图片到相册
                    String fi = MediaStore.Images.Media.insertImage(LiuLan_Activity.this.getApplicationContext().getContentResolver(), bitmap, System.currentTimeMillis() + ".jpg", "有何不可");
                    Log.i("test :", fi);
                    rh.setxiazaienable();//设置按钮可点击
                    MyToast.showToast(LiuLan_Activity.this, "下载成功 ");
                } else {
                    SavaImage(bitmap, path + "/Test");//下载图片到test文件夹
                    rh.setshoucangenable();
                    MyToast.showToast(LiuLan_Activity.this, "收藏成功 ");
                }
                s = "";

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liulan_activity);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏  一些手机底部存在虚拟键 所以不需要
        //    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        Intent intent = getIntent();
        String str = intent.getStringExtra("split");
        int item=intent.getIntExtra("item",0);

        split = str.split(",");
        listUrl = Arrays.asList(this.split);


        rh = (RollHeaderView_liulan) findViewById(R.id.rh);
        rh.setImgUrlData(listUrl);


        int i = listUrl.size()*10000+item;

        rh.mViewPager.setCurrentItem(i);
        rh.setOnHeaderViewClickListener(new RollHeaderView_liulan.HeaderViewClickListener() {
            @Override
            public void HeaderViewClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LiuLan_Activity.this);
                //   builder.setMessage("请选择");
                builder.setTitle("请选择");
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //  shoucang(position);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XiaZai(position);
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }

            @Override
            public void XiaZai(int position) {
                rh.setxiazaiunenable();
                p = position;
                s = "xiazai";
                new Task().execute(split[position]);

            }

            @Override
            public void isfinish() {
                finish();
            }

            @Override
            public void shoucang(int position) {
                rh.setshoucangunenable();
                p = position;
                new Task().execute(split[position]);
            }


        });
    }

    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = GetImageInputStream(params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = 0x123;
            handler.sendMessage(message);
        }

    }

    public void SavaImage(Bitmap bitmap, String path) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(path + "/" + split[p].split("/")[split[p].split("/").length - 1]);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

