package com.aou.cheba.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.Fankui;
import com.aou.cheba.Activity.GeRen_Activity;
import com.aou.cheba.Activity.GuanZhu_Activity;
import com.aou.cheba.Activity.Login_Activity;
import com.aou.cheba.Activity.MainActivity;
import com.aou.cheba.Activity.MeiRongActivity;
import com.aou.cheba.Activity.My_VipActivity;
import com.aou.cheba.Activity.News_Activity;
import com.aou.cheba.Activity.SelectPictureActivity;
import com.aou.cheba.Activity.Self_fabiao;
import com.aou.cheba.Activity.Shoucang;
import com.aou.cheba.Activity.Vip_Activity;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCodeIma;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.bean.MyCode_isqiandao;
import com.aou.cheba.bean.MyCode_qiandao;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.ImageUtils;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.utils.Utils;
import com.aou.cheba.view.HeadZoomScrollView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.SweetAlertDialog;
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
 * Created by Administrator on 2016/11/25.
 */
public class Me_Fragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageView iv_datu;
    private ImageView iv_head;
    private TextView tv_nickname;
    private TextView tv_feel;
    private ImageView iv_sign;
    private TextView tv_guanzhu;
    private TextView tv_fensi;
    private LinearLayout ll_fensi;
    private LinearLayout ll_guaunzhu;
    private RelativeLayout rl_mydata;
    private RelativeLayout rl_opinion;
    private RelativeLayout rl_graden;
    private RelativeLayout rl_news;
    private RelativeLayout rl_attention;
    private RelativeLayout rl_publish;
    private RelativeLayout rl_meirong;
    public static MyCodeInfo mycodes;
    private ImageView iv_gender;
    private TextView tv_level;
    public static TextView news_num;
    private RelativeLayout ll_home;
    private RelativeLayout rl_vip;
    private boolean isCreate = false;//onCreateView是否执行过 （判断控件是否加载）
    private HeadZoomScrollView scrollview;
    private RelativeLayout rl_title;

    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null && SPUtils.getBoolean(getActivity(), "islogin")) {
            int uid = SPUtils.getInt(getActivity(), "newsnum" + SPUtils.getString(getActivity(), "uid"));
            if (uid != 0) {
                news_num.setText("你有" + uid + "条新消息");
            }
            SPUtils.put(getActivity(), "islogin", false);
        }
        if (isCreate) inithttp_data();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.new_me_fragment, container, false);
            findviewbyid();

            inithttp_data();
            isCreate = true;
        } else {
            try {
                tv_guanzhu.setText(mycodes.getObj().getFollowCount() + "");
            } catch (Exception e) {
                Log.i("test", "tv_guanzhu.setText");
            }
        }

        try {
            inithttp_isqiandao();
        } catch (Exception e) {
            Log.i("test", "inithttp_isqiandao挂掉了");
        }

        return rootView;
    }

    private void init_View() {
        if (mycodes != null && mycodes.getObj() != null) {
            Glide.with(getActivity()).load(Data_Util.IMG + mycodes.getObj().getHeadImg()).into(iv_head);
            if (!TextUtils.isEmpty(mycodes.getObj().getBgImg())) {
//                Glide.with(getActivity()).load(mycodes.getObj().getBgImg()).into(iv_datu);
            }

            tv_nickname.setText(mycodes.getObj().getNickname());
            if (TextUtils.isEmpty(mycodes.getObj().getSignature())) {
                tv_feel.setText("此人很懒什么都没留下");
            } else {
                tv_feel.setText(mycodes.getObj().getSignature());
            }
            tv_guanzhu.setText(mycodes.getObj().getFollowCount() + "");
            tv_fensi.setText(mycodes.getObj().getFansCount() + "");
            if (mycodes.getObj().getGender() == 1) {
                iv_gender.setImageResource(R.mipmap.new_nan);
            } else {
                iv_gender.setImageResource(R.mipmap.new_nv);
            }
            tv_level.setText("LV:" + mycodes.getObj().getLevel());

            SPUtils.put(getActivity(), "headImage", mycodes.getObj().getHeadImg());
            //   mohu("", iv_datu);
        }
    }

    Handler h = new Handler();

    private void findviewbyid() {
        iv_datu = (ImageView) rootView.findViewById(R.id.iv_datu);
        scrollview = (HeadZoomScrollView) rootView.findViewById(R.id.scrollview);
        rl_title = (RelativeLayout) rootView.findViewById(R.id.rl_title);
        iv_head = (ImageView) rootView.findViewById(R.id.iv_head);
        iv_sign = (ImageView) rootView.findViewById(R.id.iv_sign);
        iv_gender = (ImageView) rootView.findViewById(R.id.iv_gender);
        tv_nickname = (TextView) rootView.findViewById(R.id.tv_nickname);
        news_num = (TextView) rootView.findViewById(R.id.news_num);
        tv_level = (TextView) rootView.findViewById(R.id.tv_level);
        tv_feel = (TextView) rootView.findViewById(R.id.tv_feel);
        tv_guanzhu = (TextView) rootView.findViewById(R.id.tv_guanzhu);
        tv_fensi = (TextView) rootView.findViewById(R.id.tv_fensi);
        ll_fensi = (LinearLayout) rootView.findViewById(R.id.ll_fensi);
        ll_guaunzhu = (LinearLayout) rootView.findViewById(R.id.ll_guaunzhu);
        rl_mydata = (RelativeLayout) rootView.findViewById(R.id.rl_mydata);
        rl_opinion = (RelativeLayout) rootView.findViewById(R.id.rl_opinion);
        rl_graden = (RelativeLayout) rootView.findViewById(R.id.rl_graden);
        rl_news = (RelativeLayout) rootView.findViewById(R.id.rl_news);
        //  ll_home = (RelativeLayout) rootView.findViewById(R.id.ll_home);
        rl_attention = (RelativeLayout) rootView.findViewById(R.id.rl_attention);
        rl_publish = (RelativeLayout) rootView.findViewById(R.id.rl_publish);
        rl_meirong = (RelativeLayout) rootView.findViewById(R.id.rl_meirong);
        rl_vip = (RelativeLayout) rootView.findViewById(R.id.rl_vip);

        scrollview.setZoomView(rl_title);//设置下拉放大的控件
//        iv_datu.setOnClickListener(this);
        iv_sign.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        ll_fensi.setOnClickListener(this);
        ll_guaunzhu.setOnClickListener(this);
        rl_opinion.setOnClickListener(this);
        rl_graden.setOnClickListener(this);
        rl_news.setOnClickListener(this);
        rl_attention.setOnClickListener(this);
        rl_mydata.setOnClickListener(this);
        rl_publish.setOnClickListener(this);
        rl_meirong.setOnClickListener(this);
        rl_vip.setOnClickListener(this);

        /*if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.08);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.05);
            ll_home.setLayoutParams(vParams1);
        }*/
    }

    private boolean ishead = false;
    private boolean isenter = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_datu:
                new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("更换背景墙")
                        //  .setContentText("本次签到获得5积分")
                        .setCustomImage(R.mipmap.dialog)
                        .showCancelButton(true)
                        .setCancelText("取消更换")
                        .setConfirmText("选择照片")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = null;
                                try {
                                    intent = new Intent(getActivity(), SelectPictureActivity.class);
                                    intent.putExtra("isfrist", false);
                                    intent.putExtra("intent_max_num", 1);
                                    sweetAlertDialog.dismiss();
                                    startActivityForResult(intent, 9);
                                } catch (Exception e) {
                                    sweetAlertDialog.dismiss();
                                    e.printStackTrace();
                                }

                            }
                        })
                        .show();
                break;
            case R.id.iv_head:

                new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("更换头像")
                        //  .setContentText("本次签到获得5积分")
                        .setCustomImage(R.mipmap.dialog)
                        .showCancelButton(true)
                        .setCancelText("取消更换")
                        .setConfirmText("选择照片")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = null;
                                try {
                                    intent = new Intent(getActivity(), SelectPictureActivity.class);
                                    intent.putExtra("isfrist", false);
                                    intent.putExtra("intent_max_num", 1);
                                    ishead = true;
                                    sweetAlertDialog.dismiss();
                                    startActivityForResult(intent, 9);
                                } catch (Exception e) {
                                    sweetAlertDialog.dismiss();
                                    e.printStackTrace();
                                }

                            }
                        })
                        .show();
                break;
            case R.id.iv_sign:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    inithttp_qiandao();
                }
                break;
            case R.id.ll_fensi:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    Intent intent = new Intent(getActivity(), GuanZhu_Activity.class);
                    intent.putExtra("type", 1);
                    startActivityForResult(intent, 66);
                }
                break;
            case R.id.ll_guaunzhu:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    Intent intent = new Intent(getActivity(), GuanZhu_Activity.class);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, 66);
                }
                break;
            case R.id.rl_opinion:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    startActivity(new Intent(getActivity(), Fankui.class));
                }
                break;
            case R.id.rl_graden:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    startActivity(new Intent(getActivity(), Vip_Activity.class));
                }
                break;
            case R.id.rl_news:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    SPUtils.put(getActivity(), "newsnum" + SPUtils.getString(getActivity(), "uid"), 0);
                    news_num.setText("");
                    startActivity(new Intent(getActivity(), News_Activity.class));
                }
                break;
            case R.id.rl_attention:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    startActivity(new Intent(getActivity(), Shoucang.class));
                }
                break;
            case R.id.rl_mydata:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    startActivityForResult(new Intent(getActivity(), GeRen_Activity.class), 55);
                }
                break;
            case R.id.rl_publish:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    startActivity(new Intent(getActivity(), Self_fabiao.class));
                }
                break;
            case R.id.rl_meirong:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    startActivity(new Intent(getActivity(), MeiRongActivity.class));
                }
                break;
            case R.id.rl_vip:
                if (!isenter) {
                    isenter = true;
                    change_state();
                    startActivity(new Intent(getActivity(), My_VipActivity.class));
                }
                break;
        }
    }

    private void change_state() {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                isenter = false;
            }
        }, 500);
    }

    private void inithttp_qiandao() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Sign.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_qiandao myCode_qiandao = gson.fromJson(res, MyCode_qiandao.class);

                if (myCode_qiandao.getCode() == 0) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            iv_sign.setEnabled(false);
                            iv_sign.setImageResource(R.mipmap.new_yiqiandao);

                            new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                    .setTitleText("本次签到获得" + myCode_qiandao.getObj().getScore() + "积分" + " 已连续签到" + myCode_qiandao.getObj().getDays() + "天")
                                    //  .setContentText("本次签到获得5积分")
                                    .setCustomImage(R.mipmap.qian)
                                    .setConfirmText("确定")
                                    .show();
                        }
                    });
                } else if (myCode_qiandao.getCode() == 4) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            SPUtils.put(getActivity(), "token", "");
                            ((RadioButton) (((MainActivity) getActivity()).mainRg).getChildAt(0)).setChecked(true);
                            startActivity(new Intent(getActivity(), Login_Activity.class));
                            MyToast.showToast(getActivity(), "登录已失效 请重新登录");
                        }
                    });
                }
            }
        });
    }

    private void inithttp_isqiandao() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!GetUserSign.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_isqiandao myCode_isqiandao = gson.fromJson(res, MyCode_isqiandao.class);

                if (myCode_isqiandao.getCode() == 0) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            if (myCode_isqiandao.isObj()) {
                                iv_sign.setEnabled(false);
                                iv_sign.setImageResource(R.mipmap.new_yiqiandao);
                            } else {
                                iv_sign.setEnabled(true);
                                iv_sign.setImageResource(R.mipmap.new_qiandao);
                            }
                        }
                    });
                } else {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            SPUtils.put(getActivity(), "token", "");
                            ((RadioButton) (((MainActivity) getActivity()).mainRg).getChildAt(0)).setChecked(true);
                            startActivity(new Intent(getActivity(), Login_Activity.class));
                            MyToast.showToast(getActivity(), "登录已失效 请重新登录");
                        }
                    });
                }
            }
        });
    }

    private void inithttp_data() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Info.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();
                Log.i("test info", res);
                Gson gson = new Gson();
                mycodes = gson.fromJson(res, MyCodeInfo.class);

                if (mycodes.getCode() == 0) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                init_View();
                            } catch (Exception e) {
                                Log.i("test", "init_View挂掉了");
                            }
                        }
                    });
                } else {
                    Utils.i("登录失效");
                }
            }
        });
    }

    private void inithttp_updata(String s1, String s2, String s3, Integer i, String s4) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        Map<String, Object> reqData = new HashMap<>();
        Map<String, Object> reqDataObj = new HashMap<>();
        reqDataObj.put("nickname", s1);
        reqDataObj.put("headImg", s2);
        reqDataObj.put("carInfo", s3);
        reqDataObj.put("gender", i);
        reqDataObj.put("bgImg", s4);
        reqData.put("obj", reqDataObj);
        reqData.put("token", SPUtils.getString(getActivity(), "token"));
        String body = "";
        try {
            body = SerializeUtils.object2Json(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, body);
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!ModifyInfo.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                inithttp_data();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            ArrayList<String> list_beijing = (ArrayList<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            if (list_beijing != null && list_beijing.size() != 0) {
                if (ishead) {
                    uploadImg(new File(list_beijing.get(0)), true);
                    ishead = false;
                } else {
                    uploadImg(new File(list_beijing.get(0)), false);
                    mycodes.getObj().setBgImg(list_beijing.get(0));
                    //   mohu(list_beijing.get(0), iv_datu);
//                    Glide.with(getActivity()).load(list_beijing.get(0)).into(iv_datu);
                }
            }
        }

        if (resultCode == 11) {
            MainActivity activity = (MainActivity) getActivity();
            ((RadioButton) activity.mainRg.getChildAt(0)).setChecked(true);
        }

        if (requestCode == 55) {
            init_View();
        }

        if (resultCode == 99) {
            Log.i("test", "返回");
            tv_guanzhu.setText(mycodes.getObj().getFollowCount() + "");
            tv_fensi.setText(mycodes.getObj().getFansCount() + "");
        }

    }

    private final OkHttpClient client = new OkHttpClient();

    private void uploadImg(final File file_img, final boolean isheadimage) {
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
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.showToast(getActivity(), "上传图片失败");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        Log.i("test", "返回" + res);
                        Gson gson = new Gson();
                        final MyCodeIma myCode = gson.fromJson(res, MyCodeIma.class);

                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if (myCode.isRet()) {

                                    String s = myCode.getInfo().getMd5();
                                    if (isheadimage) {
                                        inithttp_updata(null, s, null, null, null);
                                    } else {
                                        inithttp_updata(null, null, null, null, s);
                                    }

                                } else {
                                    MyToast.showToast(getActivity(), "图片上传失败");
                                }

                            }
                        });
                    }
                });
            } else {
                String s = Environment.getExternalStorageDirectory().getPath() + "/CompressPic" + 1;
                String targetPath = s + "compressPic.jpg";
                final String compressImage = ImageUtils.compressImage(file_img.getPath(), targetPath, 60);
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
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.showToast(getActivity(), "上传图片失败");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        Log.i("test", "返回" + res);
                        compressedPic.delete();//删除压缩的后上传的图片

                        Gson gson = new Gson();
                        final MyCodeIma myCode = gson.fromJson(res, MyCodeIma.class);

                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if (myCode.isRet()) {
                                    String s = myCode.getInfo().getMd5();
                                    if (isheadimage) {
                                        inithttp_updata(null, s, null, null, null);
                                    } else {
                                        inithttp_updata(null, null, null, null, s);
                                    }
                                } else {
                                    MyToast.showToast(getActivity(), "图片上传失败");
                                }

                            }
                        });
                    }
                });
            }
        }
    }
}
