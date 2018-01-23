package com.aou.cheba.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.R;
import com.aou.cheba.Service.LocationService;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeIma;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.ImageUtils;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyGrideView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.SweetAlertDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
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
 * Created by Administrator on 2016/11/28.
 */
public class Publish_Activity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView finish;
    private ImageView iv_image;
    Handler h = new Handler();
    private MyGrideView datu;
    private ViewGroup.LayoutParams vParams;
    private ArrayList<String> image_list = new ArrayList<>();
    private ImageView fabiao;
    private TextView tv_biaoti;
    private LinearLayout ll_fengmian;
    private LinearLayout ll_fenlei;
    private LinearLayout ll_location;
    private LinearLayout ll_gongkai;
    private TextView et_content;
    private MyAdapter_Gride myAdapter_gride;
    private String fengmian = "";
    private TextView tv_location;
    private ImageView iv_gongkai;
    private TextView tv_gongkai;
    private String cityName;
    private int size;
    private String md5s = "";
    private String biaoti;
    private String content;
    private int select_fenlei = 1;
    private TextView tv_fenlei;
    private int fenlei=1;
    private boolean isenter = false;
    private LocationService locationService;
    private StringBuffer sb;
    public static boolean ispublic_cheba=false;
    private LocationService locationServices;
    private RelativeLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_fragment);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        image_list = (ArrayList<String>) getIntent().getSerializableExtra("intent_selected_picture");

        findViewById();

        if (image_list.size() != 0) {
            mohu(image_list.get(0));
            fengmian = image_list.get(0);
        }


        //让banner的高度是屏幕的1/4*******************************************************************
        vParams = datu.getLayoutParams();
        vParams.height = (int) (DisplayUtil.getMobileHeight(Publish_Activity.this) * 0.14);


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
                        Intent intent = new Intent(Publish_Activity.this, SelectPictureActivity.class);
                        intent.putExtra("isfrist", false);
                        intent.putExtra("intent_max_num", 9 - image_list.size());
                        startActivityForResult(intent, 33);

                    } else {
                        new SweetAlertDialog(Publish_Activity.this, SweetAlertDialog.WARNING_TYPE)
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
                                        if (image_list.size() == 0) {
                                            fengmian = "";
                                        }
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

    private void findViewById() {
        finish = (ImageView) findViewById(R.id.iv_finish);
        fabiao = (ImageView) findViewById(R.id.tv_publish);
        iv_gongkai = (ImageView) findViewById(R.id.iv_gongkai);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        datu = (MyGrideView) findViewById(R.id.iv_datu);
        tv_biaoti = (TextView) findViewById(R.id.image_biaoti);
        tv_fenlei = (TextView) findViewById(R.id.image_fenlei);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_gongkai = (TextView) findViewById(R.id.tv_gongkai);
        et_content = (TextView) findViewById(R.id.et_content);
        ll_fengmian = (LinearLayout) findViewById(R.id.ll_fengmian);
        ll_fenlei = (LinearLayout) findViewById(R.id.ll_fenlei);
        ll_location = (LinearLayout) findViewById(R.id.ll_location);
        ll_gongkai = (LinearLayout) findViewById(R.id.ll_gongkai);
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Publish_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Publish_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }


        finish.setOnClickListener(this);
        fabiao.setOnClickListener(this);
        et_content.setOnClickListener(this);
        tv_biaoti.setOnClickListener(this);
        ll_fengmian.setOnClickListener(this);
        ll_gongkai.setOnClickListener(this);
        ll_location.setOnClickListener(this);
        ll_fenlei.setOnClickListener(this);

     /*   new Thread(new Runnable() {
            @Override
            public void run() {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LocationUtils.getCNBylocation(Publish_Activity.this);
                            cityName = LocationUtils.cityName;
                            if (TextUtils.isEmpty(cityName)) {
                                tv_location.setText("未定位");
                            } else {
                                tv_location.setText(cityName);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        }).start();*/
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                //   logMsg(sb.toString());
                cityName = location.getCity()+location.getDistrict()+location.getStreet();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_location.setText(cityName);
                    }
                });

            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = new MainActivity().locationServices;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    private boolean isgongkai = true;
    private final OkHttpClient client = new OkHttpClient();
    private ArrayList<File> mImgUrls = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    finish();
                }
                break;
            case R.id.tv_publish:
                if (isbiaoti) {
                    fabiao.setEnabled(false);
                    if (image_list.size() == 0 && TextUtils.isEmpty(et_content.getText().toString().trim())) {
                        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("车友,不能发布空话题哦~")
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

                        if (!TextUtils.isEmpty(fengmian)) {
                            File f = new File(fengmian);
                            mImgUrls.add(0, f);
                        }

                        //      fabiao.setEnabled(false);
                        if (mImgUrls.size() != 0) {
                            MyToast.showToast(Publish_Activity.this, "上传中 请稍后。。。");
                            size = mImgUrls.size();
                            uploadImg(mImgUrls, 0);
                        } else {
                            md5s = "";
                            inithttp_publish(md5s);
                        }
                    }
                } else {
                    new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("车友,还没有添加标题哦~")
                                    //  .setContentText("本次签到获得5积分")
                            .setCustomImage(R.mipmap.dialog)
                            .setConfirmText("添加标题")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                    String trim;
                                    if (isbiaoti) {
                                        trim = tv_biaoti.getText().toString().trim();
                                    } else {
                                        trim = "";
                                    }
                                    Intent intent1 = new Intent(Publish_Activity.this, BianjiActivity.class);
                                    intent1.putExtra("tv_biaoti", trim);
                                    startActivityForResult(intent1, 2);
                                }
                            })
                            .show();
                }


                break;
            case R.id.image_biaoti:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    String trim;
                    if (isbiaoti) {
                        trim = tv_biaoti.getText().toString().trim();
                    } else {
                        trim = "";
                    }
                    Intent intent1 = new Intent(Publish_Activity.this, BianjiActivity.class);
                    intent1.putExtra("tv_biaoti", trim);
                    startActivityForResult(intent1, 2);
                }
                break;
            case R.id.ll_fengmian:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent2 = new Intent(Publish_Activity.this, FengmianActivity.class);
                    intent2.putExtra("select", fengmian);
                    intent2.putExtra("list", image_list);
                    startActivityForResult(intent2, 5);
                }
                break;
            case R.id.ll_fenlei:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent7 = new Intent(Publish_Activity.this, Fenlei_Activity.class);
                    intent7.putExtra("intent7", select_fenlei);
                    startActivityForResult(intent7, 7);
                }
                break;
            case R.id.ll_gongkai:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (isgongkai) {
                        isgongkai = false;
                        tv_gongkai.setText("隐藏位置");
                        iv_gongkai.setImageResource(R.mipmap.yincang);
                    } else {
                        isgongkai = true;
                        tv_gongkai.setText("公开位置");
                        iv_gongkai.setImageResource(R.mipmap.gongkai);
                    }
                }
                break;
            case R.id.ll_location:
               // startActivity(new Intent(Publish_Activity.this, Activity.class));
                break;
            case R.id.et_content:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent = new Intent(Publish_Activity.this, Bianji_contentActivity.class);
                    intent.putExtra("s", et_content.getText().toString().trim());
                    startActivityForResult(intent, 4);
                }
                break;
        }
    }


    private void uploadImg(final ArrayList<File> mImgUrls, final int a) {
        if (a == mImgUrls.size()) {
            inithttp_publish(md5s);
            MyToast.showToast(Publish_Activity.this, "图片上传成功 准备发表。。。");
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
                                    MyToast.showToast(Publish_Activity.this, "上传图片失败");
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
                                        MyToast.showToast(Publish_Activity.this, "" + a);

                                        uploadImg(mImgUrls, a + 1);

                                    } else {
                                        MyToast.showToast(Publish_Activity.this, "图片上传失败");
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
                                    MyToast.showToast(Publish_Activity.this, "上传图片失败");
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
                                        MyToast.showToast(Publish_Activity.this, "图片上传失败");
                                    }

                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private void inithttp_publish(String s) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        //  String json = "{\"obj\":{\"content\":\"" + content + "\",\"location\":\"" + location + "\",\"pictrue\":\"" + s + "\"},\"token\":\"" + token + "\"}";
        Map<String, Object> reqData = new HashMap<>();
        Map<String, Object> reqDataObj = new HashMap<>();
        reqDataObj.put("content", content);
        reqDataObj.put("title", biaoti);
        reqDataObj.put("location", isgongkai ? cityName : "未定位");
        reqDataObj.put("pictrue", s);
        reqDataObj.put("type", fenlei);
        reqDataObj.put("template", select_fenlei);
        reqData.put("obj", reqDataObj);
        reqData.put("token", SPUtils.getString(Publish_Activity.this, "token"));
        String s1 = "";
        try {
            s1 = SerializeUtils.object2Json(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, s1);
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Server!Create.action")
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
                        MyToast.showToast(Publish_Activity.this, "连接服务器失败");
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
                            ispublic_cheba=true;
                            finish();
                            MyToast.showToast(Publish_Activity.this, "发表完成");
                        } else if (myCode1.getCode() == 4) {
                            fabiao.setEnabled(true);
                            SPUtils.put(Publish_Activity.this, "token", "");
                            MyToast.showToast(Publish_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
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
                imageView = new ImageView(Publish_Activity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, vParams.height));//设置ImageView对象布局
                imageView.setAdjustViewBounds(true);//设置边界对齐
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型
                // imageView.setPadding(8, 8, 8, 8);//设置间距
            } else {
                imageView = (ImageView) convertView;
            }

            if (position == image_list.size()) {
                Glide.with(Publish_Activity.this).load(R.mipmap.select_image).into(imageView);
            } else {
                Glide.with(Publish_Activity.this).load(image_list.get(position)).into(imageView);
            }

            return imageView;
        }
    }

    private boolean isbiaoti = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 22) {
            biaoti = data.getStringExtra("biaoti");
            if (TextUtils.isEmpty(biaoti)) {
                tv_biaoti.setText("添加标题");
                isbiaoti = false;
            } else {
                isbiaoti = true;
                tv_biaoti.setText(biaoti);
            }
        }

        if (resultCode == 44) {
            content = data.getStringExtra("content");
            et_content.setText(content);
        }

        if (resultCode == 55) {
            fengmian = data.getStringExtra("select");
            mohu(fengmian);
        }

        if (resultCode == 77) {
            select_fenlei = data.getIntExtra("select_fenlai", 1);
            switch (select_fenlei) {
                case 1:
                    fenlei=1;
                    tv_fenlei.setText("吐槽");
                    break;
                case 2:
                    fenlei=2;
                    tv_fenlei.setText("嘻游");
                    break;
                case 3:
                    fenlei=3;
                    tv_fenlei.setText("经验");
                    break;
                case 4:
                    fenlei=4;
                    tv_fenlei.setText("话题");
                    break;
            }
        }

        if (resultCode == -1) {
            ArrayList<String> serializableExtra = (ArrayList<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            if (image_list.size() == 0 && serializableExtra.size() != 0) {
                image_list.addAll(serializableExtra);

                mohu(image_list.get(0));
                fengmian = image_list.get(0);
                myAdapter_gride.notifyDataSetChanged();
            } else {
                image_list.addAll(serializableExtra);
                myAdapter_gride.notifyDataSetChanged();
            }
        }
    }

    private void mohu(final String s) {
        Glide.with(Publish_Activity.this).load(s).into(iv_image);
    }
}
