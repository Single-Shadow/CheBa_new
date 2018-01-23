package com.aou.cheba.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment.MeiRong_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeIma;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.ImageUtils;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyGrideView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RatingBar;
import com.aou.cheba.view.SweetAlertDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/29.
 */
public class MeiRong_ping_Activity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView finish;
    private Button fabiao;
    private EditText et;
    private String biaoti;
    private MyGrideView datu;
    private ViewGroup.LayoutParams vParams;
    private boolean isenter = false;
    private Handler h = new Handler();
    private ArrayList<String> image_list = new ArrayList<>();
    private MyAdapter_Gride myAdapter_gride;
    private RatingBar xin_zongti;
    private RatingBar xin_jishu;
    private RatingBar xin_fuwu;
    private RatingBar xin_huanjing;
    private TextView tv_xin;
    private String md5s = "";
//    private List<MyCode_meirong.ObjBean> list_m;
    private int posi;
    private int zongti_int = 0;
    private int jishu_int = 0;
    private int fuwu_int = 0;
    private int huanjing_int = 0;
    private TextView tv_jishu;
    private TextView tv_fuwu;
    private TextView tv_huanjing;
    private RelativeLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meirong_pinglun);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

       // list_m = new MeiRong_Fragment().list_meirong;

        posi = getIntent().getIntExtra("pos", 0);

        datu = (MyGrideView) findViewById(R.id.iv_datu);
        finish = (ImageView) findViewById(R.id.iv_finish);
        fabiao = (Button) findViewById(R.id.tv_publish);
        et = (EditText) findViewById(R.id.et);
        xin_zongti = ((RatingBar) findViewById(R.id.xin_zongti));
        xin_jishu = ((RatingBar) findViewById(R.id.xin_jishu));
        xin_fuwu = ((RatingBar) findViewById(R.id.xin_fuwu));
        xin_huanjing = ((RatingBar) findViewById(R.id.xin_huanjing));
        tv_xin = ((TextView) findViewById(R.id.tv_xin));
        tv_jishu = ((TextView) findViewById(R.id.tv_jishu));
        tv_fuwu = ((TextView) findViewById(R.id.tv_fuwu));
        tv_huanjing = ((TextView) findViewById(R.id.tv_huanjing));
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(MeiRong_ping_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(MeiRong_ping_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }

        xin_zongti.setStar(0);//设置显示的星星个数
        xin_zongti.setStepSize(RatingBar.StepSize.Full);//设置每次点击增加一颗星还是半颗星
        xin_zongti.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {//点击星星变化后选中的个数
                zongti_int = ((int) ratingCount);
                switch (zongti_int) {
                    case 1:
                        tv_xin.setText("一星");
                        break;
                    case 2:
                        tv_xin.setText("二星");
                        break;
                    case 3:
                        tv_xin.setText("三星");
                        break;
                    case 4:
                        tv_xin.setText("四星");
                        break;
                    case 5:
                        tv_xin.setText("五星");
                        break;
                }
            }
        });

        xin_jishu.setStar(0);//设置显示的星星个数
        xin_jishu.setStepSize(RatingBar.StepSize.Full);//设置每次点击增加一颗星还是半颗星
        xin_jishu.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {//点击星星变化后选中的个数
                jishu_int = ((int) ratingCount);
                switch (jishu_int) {
                    case 1:
                        tv_jishu.setText("差");
                        break;
                    case 2:
                        tv_jishu.setText("一般");
                        break;
                    case 3:
                        tv_jishu.setText("好");
                        break;
                    case 4:
                        tv_jishu.setText("很好");
                        break;
                    case 5:
                        tv_jishu.setText("非常好");
                        break;
                }
            }
        });

        xin_fuwu.setStar(0);//设置显示的星星个数
        xin_fuwu.setStepSize(RatingBar.StepSize.Full);//设置每次点击增加一颗星还是半颗星
        xin_fuwu.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {//点击星星变化后选中的个数
                fuwu_int = ((int) ratingCount);
                switch (fuwu_int) {
                    case 1:
                        tv_fuwu.setText("差");
                        break;
                    case 2:
                        tv_fuwu.setText("一般");
                        break;
                    case 3:
                        tv_fuwu.setText("好");
                        break;
                    case 4:
                        tv_fuwu.setText("很好");
                        break;
                    case 5:
                        tv_fuwu.setText("非常好");
                        break;
                }
            }
        });

        xin_huanjing.setStar(0);//设置显示的星星个数
        xin_huanjing.setStepSize(RatingBar.StepSize.Full);//设置每次点击增加一颗星还是半颗星
        xin_huanjing.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {//点击星星变化后选中的个数
                huanjing_int = ((int) ratingCount);
                switch (huanjing_int) {
                    case 1:
                        tv_huanjing.setText("差");
                        break;
                    case 2:
                        tv_huanjing.setText("一般");
                        break;
                    case 3:
                        tv_huanjing.setText("好");
                        break;
                    case 4:
                        tv_huanjing.setText("很好");
                        break;
                    case 5:
                        tv_huanjing.setText("非常好");
                        break;
                }
            }
        });

//pickphotos_to_camera_normal
        finish.setOnClickListener(this);
        fabiao.setOnClickListener(this);

        //让banner的高度是屏幕的1/4*******************************************************************
        vParams = datu.getLayoutParams();
        vParams.height = (int) (DisplayUtil.getMobileHeight(MeiRong_ping_Activity.this) * 0.13);

        datu.setOnItemClickListener(new AdapterView.OnItemClickListener() {//图片点击监听跳转到浏览界面
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (position == image_list.size()) {
                        Intent intent = new Intent(MeiRong_ping_Activity.this, SelectPictureActivity.class);
                        intent.putExtra("isfrist", false);
                        intent.putExtra("intent_max_num", 9 - image_list.size());
                        startActivityForResult(intent, 33);

                    } else {
                        new SweetAlertDialog(MeiRong_ping_Activity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("是否删除图片")
                                        //  .setContentText("选择后不可恢复！")
                                .setCancelText("取消")
                                .setConfirmText("删除")
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

                                        image_list.remove(position);

                                        myAdapter_gride.notifyDataSetChanged();

                                        sDialog.setTitleText("图片已删除")
                                                //  .setContentText("图片已删除！")
                                                .setConfirmText("确定")
                                                .showCancelButton(false)
                                                .setCancelClickListener(null)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();
                    }
                }

            }
        });
        myAdapter_gride = new MyAdapter_Gride();
        datu.setAdapter(myAdapter_gride);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            ArrayList<String> serializableExtra = (ArrayList<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            if (image_list.size() == 0 && serializableExtra.size() != 0) {
                image_list.addAll(serializableExtra);
                myAdapter_gride.notifyDataSetChanged();
            } else {
                image_list.addAll(serializableExtra);
                myAdapter_gride.notifyDataSetChanged();
            }
        }
    }

    class MyAdapter_Gride extends BaseAdapter {
        @Override
        public int getCount() {
            if (image_list.size() == 9) {
                return 9;
            } else {
                return image_list.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(MeiRong_ping_Activity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vParams.height));//设置ImageView对象布局
                imageView.setAdjustViewBounds(true);//设置边界对齐
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
                // imageView.setPadding(8, 8, 8, 8);//设置间距
            } else {
                imageView = (ImageView) convertView;
            }

            if (position == image_list.size()) { //select_imaeg
                Glide.with(MeiRong_ping_Activity.this).load(R.mipmap.select_image).into(imageView);
            } else {
                Glide.with(MeiRong_ping_Activity.this).load(image_list.get(position)).into(imageView);
            }

            return imageView;
        }
    }

    private int size;
    private ArrayList<File> mImgUrls = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_publish:
                fabiao.setEnabled(false);
                if (zongti_int == 0 || jishu_int == 0 || fuwu_int == 0 || huanjing_int == 0) {
                    new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("车友,请评星级~")
                                    //  .setContentText("本次签到获得5积分")
                            .setCustomImage(R.mipmap.dialog)
                            .setConfirmText("确定")
                            .show();
                    fabiao.setEnabled(true);
                } else {
                    if (image_list.size() == 0 && TextUtils.isEmpty(et.getText().toString().trim())) {
                        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("车友,请提出您的宝贵意见~")
                                        //  .setContentText("本次签到获得5积分")
                                .setCustomImage(R.mipmap.dialog)
                                .setConfirmText("确定")
                                .show();
                        fabiao.setEnabled(true);
                    } else {
                        if (image_list.size() != 0) {
                            for (int i = 0; i < image_list.size(); i++) {
                                File f = new File(image_list.get(i));
                                mImgUrls.add(f);
                            }
                        }

                        //      fabiao.setEnabled(false);
                        if (mImgUrls.size() != 0) {
                            MyToast.showToast(MeiRong_ping_Activity.this, "上传中 请稍后。。。");
                            size = mImgUrls.size();
                            uploadImg(mImgUrls, 0);
                        } else {
                            md5s = "";
                            inithttp_publish(md5s);
                        }
                    }
                }
                //   finish();
                break;
        }
    }

    private void uploadImg(final ArrayList<File> mImgUrls, final int a) {
        if (a == mImgUrls.size()) {
            inithttp_publish(md5s);
            MyToast.showToast(MeiRong_ping_Activity.this, "图片上传成功 准备发表。。。");
            return;
        } else {
            File file_img = mImgUrls.get(a);

            if (file_img != null) {
                RequestBody requestBody1;

                if (file_img.length() < 204800) {

                    requestBody1 = RequestBody.create(MediaType.parse("png"), file_img);

                    Request request = new Request.Builder()
                            .url(Data_Util.HttPHEAD_image + "upload")//地址
                            .post(requestBody1)//添加请求体
                            .header("Content-Type", "png")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fabiao.setEnabled(true);
                                    MyToast.showToast(MeiRong_ping_Activity.this, "上传图片失败");
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();

                            Gson gson = new Gson();
                            final MyCodeIma myCode = gson.fromJson(res, MyCodeIma.class);

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (myCode.isRet()) {

                                        String s = myCode.getInfo().getMd5();

                                        if (md5s != "") {
                                            md5s = md5s + "," + s;
                                        } else {
                                            md5s = s;
                                        }
                                        MyToast.showToast(MeiRong_ping_Activity.this, "" + a);

                                        uploadImg(mImgUrls, a + 1);

                                    } else {
                                        MyToast.showToast(MeiRong_ping_Activity.this, "图片上传失败");
                                    }

                                }
                            });
                        }
                    });
                } else {
                    String s = Environment.getExternalStorageDirectory().getPath() + "/CompressPic" + a;
                    String targetPath = s + "compressPic.jpg";
                    final String compressImage = ImageUtils.compressImage(file_img.getPath(), targetPath, 90);
                    final File compressedPic = new File(compressImage);

                    requestBody1 = RequestBody.create(MediaType.parse("png"), compressedPic);

                    Request request = new Request.Builder()
                            .url(Data_Util.HttPHEAD_image + "upload")//地址
                            .post(requestBody1)//添加请求体
                            .header("Content-Type", "png")
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //   Log.i("test",e.toString());
                                    compressedPic.delete();//删除压缩的后上传的图片
                                    fabiao.setEnabled(true);
                                    MyToast.showToast(MeiRong_ping_Activity.this, "上传图片失败");
                                }
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();

                            compressedPic.delete();//删除压缩的后上传的图片

                            Gson gson = new Gson();
                            final MyCodeIma myCode = gson.fromJson(res, MyCodeIma.class);

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (myCode.isRet()) {
                                        String s = myCode.getInfo().getMd5();

                                        if (md5s != "") {
                                            md5s = md5s + "," + s;
                                        } else {
                                            md5s = s;
                                        }

                                        uploadImg(mImgUrls, a + 1);
                                    } else {
                                        MyToast.showToast(MeiRong_ping_Activity.this, "图片上传失败");
                                    }

                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private String getXing(String f) {
        String a = "";
        for (int i = 0; i < f.length(); i++) {
            a = a + "*";
        }
        return a;
    }

    private String readStream(InputStream is)    {
        // 资源流(GBK汉字码）变为串
        String res;
        try      {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            res = new String(buf,"GBK");   	//  必须将GBK码制转成Unicode
            is.close();
        } catch (Exception e)
        {
            res="";
        }
        return res;            //  把资源文本文件送到String串中
    }

    private void inithttp_publish(String pic) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        String trim = et.getText().toString().trim();
        String s = readStream(getResources().openRawResource(R.raw.mingan));
        String[] split = s.split(",");


        for (int i = 0; i < split.length; i++) {
            String x = split[i];  //x为敏感词汇
            if (trim.contains(x)) {
                trim = trim.replaceAll(x, getXing(x));
            }
        }


        //  String json = "{\"obj\":{\"content\":\"" + content + "\",\"location\":\"" + location + "\",\"pictrue\":\"" + s + "\"},\"token\":\"" + token + "\"}";
        Map<String, Object> reqData = new HashMap<>();
        Map<String, Object> reqDataObj = new HashMap<>();
        reqDataObj.put("content", trim);
        reqDataObj.put("did", MeiRong_xiangqiang_Activity.list_m.getId());
        reqDataObj.put("fw", fuwu_int);
        reqDataObj.put("hj", huanjing_int);
        reqDataObj.put("js", jishu_int);
        reqDataObj.put("score", zongti_int);
        reqDataObj.put("pictrue", pic);
        reqData.put("obj", reqDataObj);
        reqData.put("token", SPUtils.getString(MeiRong_ping_Activity.this, "token"));
        String s1 = "";

        try {
            s1 = SerializeUtils.object2Json(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, s1);
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/CarStore!Comment.action")
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        //   bt_go.setEnabled(true);
                        md5s = "";
                        fabiao.setEnabled(true);
                        MyToast.showToast(MeiRong_ping_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 myCode1 = gson.fromJson(res, MyCode1.class);


                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (myCode1.getCode() == 0) {
                            if (posi >= 0) {
                                MeiRong_Fragment.list_meirong.get(posi).setCommentNum(MeiRong_Fragment.list_meirong.get(posi).getCommentNum()+1);
                            }

                            setResult(101);
                            finish();
                            MyToast.showToast(MeiRong_ping_Activity.this, "发表完成");
                        } else if (myCode1.getCode() == 4) {
                            fabiao.setEnabled(true);
                            SPUtils.put(MeiRong_ping_Activity.this, "token", "");
                            MyToast.showToast(MeiRong_ping_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }
}
