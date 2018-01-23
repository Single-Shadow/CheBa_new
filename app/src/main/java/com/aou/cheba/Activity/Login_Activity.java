package com.aou.cheba.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.MD5Utils;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyToast;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.UTrack;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/2.
 */
public class Login_Activity extends SwipeBackActivity {

    private RelativeLayout rl_denglu;
    private Button bt_denglu;
    private TextView tv_zhuce;
    private TextView tv_forget;
    private TextView qq;
    private TextView weibo;
    private EditText yonghuming;
    private EditText mima;
    private Handler h = new Handler();
    private ImageView iv_finish;
    private TextView weixin;
    private IWXAPI api;
    public static Login_Activity mContext;
    private Platform qq_login;
    private Platform weibo_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        api = MainActivity.api;
        qq_login = MainActivity.qq_login;
        weibo_login = MainActivity.weibo_login;
        // regToWx();   放在Main中注册

        findviewbyid();
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

    private void findviewbyid() {
        rl_denglu = (RelativeLayout) findViewById(R.id.rl_denglu);
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Login_Activity.this) * Data_Util.HEAD_NEW);
            rl_denglu.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Login_Activity.this) * Data_Util.HEAD_OLD);
            rl_denglu.setLayoutParams(vParams1);
        }


        bt_denglu = (Button) findViewById(R.id.bt_denglu);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        tv_zhuce = (TextView) findViewById(R.id.tv_zhuce);
        tv_forget = (TextView) findViewById(R.id.tv_forget);
        qq = (TextView) findViewById(R.id.tv_qq);
        weibo = (TextView) findViewById(R.id.tv_weibo);
        weixin = (TextView) findViewById(R.id.tv_weixin);
        yonghuming = (EditText) findViewById(R.id.et_yonghuming);

        mima = (EditText) findViewById(R.id.et_mima);

        bt_denglu.setOnClickListener(MyOnClick);
        iv_finish.setOnClickListener(MyOnClick);
        tv_zhuce.setOnClickListener(MyOnClick);
        tv_forget.setOnClickListener(MyOnClick);
        qq.setOnClickListener(MyOnClick);
        weibo.setOnClickListener(MyOnClick);
        weixin.setOnClickListener(MyOnClick);

    }

    private String name;
    private String password;
    private String qq_userGender;
    private String qq_userId;
    private String qq_userIcon;
    private String qq_userName;
    private String weibo_userGender = "男";
    private String weibo_userId;
    private String weibo_userIcon;
    private String weibo_userName;
    private boolean isclick = false;
    private View.OnClickListener MyOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.bt_denglu:
                    name = yonghuming.getText().toString().trim();
                    password = mima.getText().toString().trim();
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                        MyToast.showToast(Login_Activity.this, "用户名或密码为空");
                    } else {
                        inithttp();
                    }
                    break;
                case R.id.tv_zhuce:
                    startActivity(new Intent(Login_Activity.this, Regeister.class));
                    break;
                case R.id.tv_forget:
                    startActivity(new Intent(Login_Activity.this, Froget_pwd_Activity.class));
                    break;
                case R.id.tv_weixin:
                    if (!isclick) {
                        isclick = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isclick = false;
                            }
                        }, 1000);
                        getCode();
                    }

                    break;
                case R.id.tv_qq:
                    if (!isclick) {
                        isclick = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isclick = false;
                            }
                        }, 1000);

                        qq_login.setPlatformActionListener(new PlatformActionListener() {
                            @Override
                            public void onError(Platform arg0, int arg1, Throwable arg2) {
                                qq_login.removeAccount(true);
                                Log.i("test", "QQ授权失败");
                            }

                            @Override
                            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                                // TODO Auto-generated method stub
                                qq_userGender = arg0.getDb().getUserGender();
                                qq_userId = arg0.getDb().getUserId();
                                qq_userIcon = arg0.getDb().getUserIcon();
                                qq_userName = arg0.getDb().getUserName();
                                qq_login();
                            }

                            @Override
                            public void onCancel(Platform arg0, int arg1) {
                                // TODO Auto-generated method stub
                                Log.i("test", "QQ授权取消");
                            }
                        });
                        //authorize与showUser单独调用一个即可
                        //     qq.authorize();//单独授权,OnComplete返回的hashmap是空的
                        qq_login.showUser(null);//授权并获取用户信息
                        //移除授权
                        //weibo.removeAccount(true);
                    }
                    break;
                case R.id.tv_weibo:
                    if (!isclick) {
                        isclick = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isclick = false;
                            }
                        }, 1000);


                        weibo_login.setPlatformActionListener(new PlatformActionListener() {
                            @Override
                            public void onError(Platform arg0, int arg1, Throwable arg2) {
                                // TODO Auto-generated method stub
                                Log.i("test", "微博授权失败");
                                weibo_login.removeAccount(true);
                            }

                            @Override
                            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                                // TODO Auto-generated method stub
                                weibo_userGender = arg0.getDb().getUserGender();
                                weibo_userId = arg0.getDb().getUserId();
                                weibo_userIcon = arg0.getDb().getUserIcon();
                                weibo_userName = arg0.getDb().getUserName();
                                weibo_login();
                            }

                            @Override
                            public void onCancel(Platform arg0, int arg1) {
                                // TODO Auto-generated method stub
                                Log.i("test", "微博授权取消");
                            }
                        });
                        weibo_login.SSOSetting(false);
                        //authorize与showUser单独调用一个即可
                        //     weibo.authorize();//单独授权,OnComplete返回的hashmap是空的
                        weibo_login.showUser(null);//授权并获取用户信息
                        //移除授权
                        //weibo.removeAccount(true);
                    }
                    break;
            }
        }
    };

    private void regToWx() {
        // 通过WXAPIFactory工厂,获得IWXAPI的实例
        api = WXAPIFactory.createWXAPI(Login_Activity.this, "wx79ee8a2e6fdf7ae0", true);
        // 将应用的appid注册到微信
        api.registerApp("wx79ee8a2e6fdf7ae0");
    }

    //获取微信访问getCode
    private void getCode() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "carjob_wx_login";
        api.sendReq(req);
        Log.i("test", "gecode");
    }


    private void inithttp() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"loginType\":1,\"phone\":\"" + name + "\",\"password\":\"" + MD5Utils.encode(password) + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Login.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Login_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {

                            SPUtils.put(Login_Activity.this, "token", (String) mycode.getObj());
                            SPUtils.put(Login_Activity.this, "logintype", "1");

                            inithttp_data();

                        } else if (3 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "账号已注册");
                        } else if (1 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "用户不存在");
                        } else if (2 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "密码错误");
                        } else if (-1 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "登录失败");
                        }
                    }
                });
            }
        });
    }

    private void qq_login() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"loginType\":2,\"qid\":\"" + qq_userId + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Login.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Login_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {
                            SPUtils.put(Login_Activity.this, "logintype", "2");
                            SPUtils.put(Login_Activity.this, "token", (String) mycode.getObj());
                            inithttp_data();

                        } else if (3 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "账号已注册");
                        } else if (1 == mycode.getCode()) {
                            qq_regeister();
                        } else if (2 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "密码错误");
                        } else if (-1 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "登录失败");
                        }
                    }
                });
            }
        });

    }

    private void weibo_login() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"loginType\":3,\"sid\":\"" + weibo_userId + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Login.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Login_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {
                            SPUtils.put(Login_Activity.this, "logintype", "3");
                            SPUtils.put(Login_Activity.this, "token", (String) mycode.getObj());
                            inithttp_data();

                        } else if (3 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "账号已注册");
                        } else if (1 == mycode.getCode()) {
                            weibo_regeister();
                            MyToast.showToast(Login_Activity.this, "用户不存在");
                        } else if (2 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "密码错误");
                        } else if (-1 == mycode.getCode()) {
                            MyToast.showToast(Login_Activity.this, "登录失败");
                        }
                    }
                });
            }
        });
    }

    private void qq_regeister() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"loginType\":2,\"qid\":\"" + qq_userId + "\",\"headImg\":\"" + qq_userIcon + "\",\"nickname\":\"" + qq_userName + "\",\"gender\":" + (qq_userGender.equals("m") ? 1 : 2) + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Register.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Login_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {
                            qq_login();
                        } else if (3 == mycode.getCode()) {
                            Log.i("test", "账号已注册");
                            MyToast.showToast(Login_Activity.this, "账号已注册");
                        } else if (-1 == mycode.getCode()) {
                            Log.i("test", "注册失败");
                            MyToast.showToast(Login_Activity.this, "注册失败");

                        } else if (7 == mycode.getCode()) {
                            Log.i("test", "验证码无效");
                            MyToast.showToast(Login_Activity.this, "验证码无效");

                        }
                    }
                });
            }
        });
    }

    private void weibo_regeister() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"loginType\":3,\"sid\":\"" + weibo_userId + "\",\"headImg\":\"" + weibo_userIcon + "\",\"nickname\":\"" + weibo_userName + "\",\"gender\":" + (weibo_userGender.equals("m") ? 1 : 2) + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Register.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Login_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {

                            weibo_login();

                        } else if (3 == mycode.getCode()) {
                            Log.i("test", "账号已注册");
                            MyToast.showToast(Login_Activity.this, "账号已注册");
                        } else if (-1 == mycode.getCode()) {
                            Log.i("test", "注册失败");
                            MyToast.showToast(Login_Activity.this, "注册失败");

                        } else if (7 == mycode.getCode()) {
                            Log.i("test", "验证码无效");
                            MyToast.showToast(Login_Activity.this, "验证码无效");

                        }
                    }
                });
            }
        });

    }

    public static boolean isoutlogin_xiyou = false;
    public static boolean isoutlogin_home = false;
    public static boolean isoutlogin_meirong = false;

    private void inithttp_data() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(Login_Activity.this, "token") + "\"}");
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
                        MyToast.showToast(Login_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCodeInfo mycodes = gson.fromJson(res, MyCodeInfo.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycodes.getCode()) {
                            //   MyToast.showToast(Login_Activity.this, "登录成功");
                            isoutlogin_xiyou = true;
                            isoutlogin_home = true;
                            isoutlogin_meirong = true;

                            SPUtils.put(Login_Activity.this, "uid", "" + mycodes.getObj().getUid());
                            SPUtils.put(Login_Activity.this, "headImage", "" + mycodes.getObj().getHeadImg());
                            SPUtils.put(Login_Activity.this, "nickeName", "" + mycodes.getObj().getNickname());

                            if (mycodes.getObj().getPassword() != null) {
                                SPUtils.put(Login_Activity.this, "password", "" + mycodes.getObj().getPassword());
                            }
                            Log.i("test","友盟推送"+mycodes.getObj().getUid());
                            //绑定友盟 Alias
                            new MainActivity().mPushAgent.addExclusiveAlias(mycodes.getObj().getUid() + "", "carbar", new UTrack.ICallBack() {
                                @Override
                                public void onMessage(boolean b, String s) {
                                    Log.i("test_绑定友盟onMessage",s+b);
                                }
                            });

//                            new MainActivity().mPushAgent.setExclusiveAlias(mycodes.getObj().getUid() + "", "carbar");

//                            ((RadioButton) new MainActivity().mainRg.getChildAt(4)).setChecked(true);
                            SPUtils.put(Login_Activity.this, "islogin", true);

                            setResult(160);
                            finish();
                            finish();
                        }
                    }
                });
            }
        });
    }
}
