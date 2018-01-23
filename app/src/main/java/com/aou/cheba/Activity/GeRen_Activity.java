package com.aou.cheba.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeIma;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.bean.MyCode_che;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.ImageUtils;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.SweetAlertDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tandong.bottomview.view.BottomView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/26.
 */
public class GeRen_Activity extends SwipeBackActivity implements View.OnClickListener {

    private Button bt_xiugai_mima;
    private Button bt_tuichu_denglu;
    private RelativeLayout rl_denglu;
    private RelativeLayout rl_head;
    private TextView uid;
    private CircleImageView head;
    private TextView nickname;
    private TextView iv_man;
    private ImageView finish;
    private TextView chename;
    private MyCodeInfo myCodeInfo;
    private TextView phonenum;
    private int isman = 1;
    private RelativeLayout xiugai_phonenum;
    private String ima_md5 = "";
    private boolean ishead;
    private TextView tv_qianming;
    private RelativeLayout rl_qianming;
    private LinearLayout ll_nickname;
    private RelativeLayout rl_chexing;
    private LinearLayout ll_man;
    private BottomView bottomView;
    private ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geren_activity);
        Me_Fragment me_fragment = new Me_Fragment();
        myCodeInfo = me_fragment.mycodes;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        bt_xiugai_mima = (Button) findViewById(R.id.bt_xiugai_mima);
        bt_tuichu_denglu = (Button) findViewById(R.id.bt_tuichu_denglu);
        rl_head = (RelativeLayout) findViewById(R.id.rl_xiugai_mima);
        xiugai_phonenum = (RelativeLayout) findViewById(R.id.rl_xiugai_phonenum);
        rl_qianming = (RelativeLayout) findViewById(R.id.rl_qianming);
        rl_chexing = (RelativeLayout) findViewById(R.id.rl_chexing);
        ll_nickname = (LinearLayout) findViewById(R.id.ll_nickname);
        ll_man = (LinearLayout) findViewById(R.id.ll_man);
        uid = (TextView) findViewById(R.id.et_uid);
        nickname = (TextView) findViewById(R.id.et_nickname);
        chename = (TextView) findViewById(R.id.tv_che);
        phonenum = (TextView) findViewById(R.id.tv_phonenum);
        tv_qianming = (TextView) findViewById(R.id.tv_qianming);
        iv_man = (TextView) findViewById(R.id.iv_man);

        finish = (ImageView) findViewById(R.id.iv_finish);
        head = (CircleImageView) findViewById(R.id.iv_head);

        if (myCodeInfo != null) {
            uid.setText(myCodeInfo.getObj().getUid() + "");
            nickname.setText(myCodeInfo.getObj().getNickname());
            if (myCodeInfo.getObj().getGender() == 1) {
                iv_man.setText("男");
                Drawable drawable = getResources().getDrawable(R.mipmap.nan);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                iv_man.setCompoundDrawables(drawable, null, null, null);
            } else {
                iv_man.setText("女");
                Drawable drawable = getResources().getDrawable(R.mipmap.nv);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                iv_man.setCompoundDrawables(drawable, null, null, null);
            }

            if (myCodeInfo.getObj().getPhone() != null && !TextUtils.isEmpty(myCodeInfo.getObj().getPhone())) {
                phonenum.setText(myCodeInfo.getObj().getPhone());
            } else {
                phonenum.setText("未绑定");
            }

            if (myCodeInfo.getObj().getCarInfo() != null && !TextUtils.isEmpty(myCodeInfo.getObj().getCarInfo())) {
                chename.setText(myCodeInfo.getObj().getCarInfo());
            } else {
                chename.setText("未填写");
            }
            if (TextUtils.isEmpty(myCodeInfo.getObj().getSignature())) {
                tv_qianming.setText("此人很懒 什么都没留下");
            } else {
                tv_qianming.setText(myCodeInfo.getObj().getSignature());
            }
            Glide.with(GeRen_Activity.this).load(Data_Util.IMG+myCodeInfo.getObj().getHeadImg()).into(head);
        }

        rl_chexing.setOnClickListener(this);
        ll_nickname.setOnClickListener(this);
        finish.setOnClickListener(this);
        rl_head.setOnClickListener(this);
        ll_man.setOnClickListener(this);
        rl_qianming.setOnClickListener(this);
        bt_tuichu_denglu.setOnClickListener(this);
        bt_xiugai_mima.setOnClickListener(this);
        xiugai_phonenum.setOnClickListener(this);

        rl_denglu = (RelativeLayout) findViewById(R.id.rl_denglu);
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            Log.e("test", DisplayUtil.getMobileHeight(GeRen_Activity.this) + "");
            vParams1.height = (int) (DisplayUtil.getMobileHeight(GeRen_Activity.this) * 0.09);
            rl_denglu.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(GeRen_Activity.this) * 0.05);
            rl_denglu.setLayoutParams(vParams1);
        }
    }

    private void inithttp_loginout() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(GeRen_Activity.this, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Logout.action")
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(GeRen_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {
                            SPUtils.put(GeRen_Activity.this, "token", "");
                            SPUtils.put(GeRen_Activity.this, "uid", "");
                            SPUtils.put(GeRen_Activity.this, "logintype", "");
                            SPUtils.put(GeRen_Activity.this, "cheba_up", "");
                            SPUtils.put(GeRen_Activity.this, "headImage", "");
                            SPUtils.put(GeRen_Activity.this, "nickeName", "");
                            SPUtils.put(GeRen_Activity.this, "password", "");
                            MainActivity.qq_login.removeAccount();
                            MainActivity.weibo_login.removeAccount();
                            setResult(11);
                            finish();
                        } else if (-1 == mycode.getCode()) {
                            MyToast.showToast(GeRen_Activity.this, "退出失败");
                        } else if (4 == mycode.getCode()) {
                            SPUtils.put(GeRen_Activity.this, "token", "");
                            SPUtils.put(GeRen_Activity.this, "uid", "");
                            SPUtils.put(GeRen_Activity.this, "logintype", "");
                            SPUtils.put(GeRen_Activity.this, "cheba_up", "");
                            SPUtils.put(GeRen_Activity.this, "headImage", "");
                            SPUtils.put(GeRen_Activity.this, "nickeName", "");
                            SPUtils.put(GeRen_Activity.this, "password", "");
                            setResult(11);
                            finish();
                        }
                    }
                });
            }
        });
    }

    private boolean isenter = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.ll_man:
                new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("选择性别")
                        .setCustomImage(R.mipmap.dialog)
                        .setCancelText("女")
                        .setConfirmText("男")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                iv_man.setText("男");
                                Drawable drawable = getResources().getDrawable(R.mipmap.nan);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                iv_man.setCompoundDrawables(drawable, null, null, null);

                                new Me_Fragment().mycodes.getObj().setGender(1);
                                inithttp_updata(null, null, null, 1, null, null);
                                sDialog.setTitleText("性别:男")
                                        //  .setContentText("图片已删除！")
                                        .setConfirmText("确定")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                iv_man.setText("女");
                                Drawable drawable = getResources().getDrawable(R.mipmap.nv);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                iv_man.setCompoundDrawables(drawable, null, null, null);

                                new Me_Fragment().mycodes.getObj().setGender(2);
                                inithttp_updata(null, null, null, 2, null, null);
                                sweetAlertDialog.setTitleText("性别:女")
                                        //  .setContentText("图片已删除！")
                                        .setConfirmText("确定")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).show();
                break;

            case R.id.bt_tuichu_denglu:
                inithttp_loginout();
                break;
            case R.id.bt_xiugai_mima:
                if (SPUtils.getString(GeRen_Activity.this, "logintype").equals("1")) {
                    startActivity(new Intent(GeRen_Activity.this, XiuGai_Activity.class));
                } else {
                    MyToast.showToast(GeRen_Activity.this, "第三方用户无法修改密码");
                }
                break;
            case R.id.rl_xiugai_mima:
                Intent intent4 = new Intent(GeRen_Activity.this, SelectPictureActivity.class);
                intent4.putExtra("isfrist", false);
                intent4.putExtra("intent_max_num", 1);
                startActivityForResult(intent4, 9);
                break;
            case R.id.rl_xiugai_phonenum:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    startActivityForResult(new Intent(GeRen_Activity.this, xiugai_phonenum_activity.class), 22);
                }
                break;
            case R.id.rl_qianming:
                Intent intent = new Intent(GeRen_Activity.this, Bianji_info_Activity.class);
                intent.putExtra("max", 30);
                startActivityForResult(intent, 33);
                break;
            case R.id.rl_chexing:
                inithttp_chelist();
              /*  if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent1 = new Intent(GeRen_Activity.this, Bianji_info_Activity.class);
                    intent1.putExtra("max", 12);
                    startActivityForResult(intent1, 44);
                }*/

                break;
            case R.id.ll_nickname:
                Intent intent3 = new Intent(GeRen_Activity.this, Bianji_info_Activity.class);
                intent3.putExtra("max", 20);
                startActivityForResult(intent3, 88);
                break;
        }
    }

    private void inithttp_chelist() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Global!LoadPublicCarInfo.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(GeRen_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_che mycode = gson.fromJson(res, MyCode_che.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {
                            list_che = mycode.getObj();
                            bottomView = new BottomView(GeRen_Activity.this, R.style.BottomViewTheme_Defalut, R.layout.bottom_view);
                            bottomView.setAnimation(R.style.BottomToTopAnim);

                            bottomView.showBottomView(true);
                            lv = (ListView) bottomView.getView().findViewById(R.id.lv_list);
                            MyAdapter adapter = new MyAdapter();
                            lv.setAdapter(adapter);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    name = list_che.get(arg2).getName();
                                    chename.setText(name);
                                    new Me_Fragment().mycodes.getObj().setCarInfo(name);
                                    inithttp_updata(null, null, name, null, null, null);

                                    bottomView.dismissBottomView();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private List<MyCode_che.ObjBean> list_che;
    private String name;

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_che.size();
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
            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = View.inflate(GeRen_Activity.this, R.layout.che_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.item_name);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.nickname.setText(list_che.get(position).getName());

            return convertView;
        }
    }

    static class ViewHolder {
        TextView nickname;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 2) {
            String phone = data.getStringExtra("phone");
            phonenum.setText(phone);
        }
        if (resultCode == 20) {
            String content = data.getStringExtra("content");
            if (!TextUtils.isEmpty(content)) {
                nickname.setText(content);
                new Me_Fragment().mycodes.getObj().setNickname(content);
                inithttp_updata(content, null, null, null, null, null);
                SPUtils.put(GeRen_Activity.this, "nickeName", content);
            }
        }
        if (resultCode == 30) {
            String content = data.getStringExtra("content");
            tv_qianming.setText(content);
            new Me_Fragment().mycodes.getObj().setSignature(content);
            inithttp_updata(null, null, null, null, null, content);
        }


        if (resultCode == -1) {
            ArrayList<String> list_beijing = (ArrayList<String>) data.getSerializableExtra(SelectPictureActivity.INTENT_SELECTED_PICTURE);
            if (list_beijing != null && list_beijing.size() != 0) {
                uploadImg(new File(list_beijing.get(0)), true);
                Glide.with(GeRen_Activity.this).load(list_beijing.get(0)).into(head);
                SPUtils.put(GeRen_Activity.this, "headImage", list_beijing.get(0));
            }
        }


    }

    Handler h = new Handler();
    private final OkHttpClient client = new OkHttpClient();

    private void inithttp_updata(String s1, String s2, String s3, Integer i, String s4, String s5) {
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
        reqDataObj.put("signature", s5);
        reqData.put("obj", reqDataObj);
        reqData.put("token", SPUtils.getString(GeRen_Activity.this, "token"));
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
                        MyToast.showToast(GeRen_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }


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
                                MyToast.showToast(GeRen_Activity.this, "上传图片失败");
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
                                    if (isheadimage) {
                                        inithttp_updata(null, s, null, null, null, null);
                                    } else {
                                        inithttp_updata(null, null, null, null, s, null);
                                    }

                                } else {
                                    MyToast.showToast(GeRen_Activity.this, "图片上传失败");
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
                                MyToast.showToast(GeRen_Activity.this, "上传图片失败");
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
                                    if (isheadimage) {
                                        inithttp_updata(null, s, null, null, null, null);
                                    } else {
                                        inithttp_updata(null, null, null, null, s, null);
                                    }
                                } else {
                                    MyToast.showToast(GeRen_Activity.this, "图片上传失败");
                                }

                            }
                        });
                    }
                });
            }
        }
    }

}
