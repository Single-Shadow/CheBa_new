package com.aou.cheba.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.aou.cheba.R;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.view.RollHeaderView_liulan;
import com.aou.cheba.view.SweetAlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */
public class YueTuDetailActivity extends Activity {

    private RollHeaderView_liulan rh;
    private String pic;
    private String[] split;
    int p;
    int pos;
    String s = "";
    Handler h = new Handler();
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yuetudetail);

        pic = getIntent().getStringExtra("pic");
        pos = getIntent().getIntExtra("pos", 0);

        split = pic.split(",");
        List<String> list = new ArrayList<>();
        for (String s1 : split) {
            list.add(Data_Util.IMG + s1);
        }
        rh = ((RollHeaderView_liulan) findViewById(R.id.rh));
        rh.setImgUrlData(list);
        rh.mViewPager.setCurrentItem(pos);
        rh.setOnHeaderViewClickListener(new RollHeaderView_liulan.HeaderViewClickListener() {
            @Override
            public void HeaderViewClick(final int position) {
                new SweetAlertDialog(YueTuDetailActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("是否保存图片到相册")
                        .setCustomImage(R.mipmap.dialog)
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                XiaZai(position);
                                sDialog.setTitleText("图片已保存到相册")
                                        //  .setContentText("图片已删除！")
                                        .setConfirmText("确定")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).show();

            }

            @Override
            public void XiaZai(int position) {
                rh.setxiazaiunenable();
                p = position;
                s = "xiazai";
                new Task().execute(Data_Util.IMG + split[position]);

            }

            @Override
            public void isfinish() {
                finish();
            }

            @Override
            public void shoucang(int position) {
                rh.setshoucangunenable();
                p = position;
                new Task().execute(Data_Util.IMG + split[position]);
            }
        });
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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                String path = Environment.getExternalStorageDirectory().getPath();

                if ("xiazai".equals(s)) {

//保存图片到相册
                    String fi = MediaStore.Images.Media.insertImage(YueTuDetailActivity.this.getApplicationContext().getContentResolver(), bitmap, System.currentTimeMillis() + ".jpg", "有何不可");
                    rh.setxiazaienable();//设置按钮可点击
//                    MyToast.showToast(YueTuDetailActivity.this, "保存相册成功 ");
                } else {
                    SavaImage(bitmap, path + "/Test");//下载图片到test文件夹
                    rh.setshoucangenable();
//                    MyToast.showToast(YueTuDetailActivity.this, "收藏成功 ");
                }
                s = "";
            }
        }
    };
}
