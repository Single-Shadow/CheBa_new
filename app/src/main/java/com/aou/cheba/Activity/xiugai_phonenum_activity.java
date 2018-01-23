package com.aou.cheba.Activity;
/**
 * Created by Administrator on 2016/9/5.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.MD5Utils;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyToast;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Boom on 2016/8/2.
 */
public class xiugai_phonenum_activity extends SwipeBackActivity implements View.OnClickListener {
    private static final String[] AVATARS = new String[400];
    TextView btnView;
    EditText csed1 = null;
    EditText vaildatepwd = null;
    private String zh;
    private Button bt_yanzheng;
    private RelativeLayout rl_denglu;
    private String code;
    private ImageView finish;
    private MyCodeInfo myCodeInfo;
    private EditText csed2;
    private LinearLayout ll_mima;
    private View xian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiugaiphonenum_activity);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        SMSSDK.initSDK(this, Data_Util.APP_KEY, Data_Util.APP_SECRET); //车吧的短信验证
        //注册短信回调

        myCodeInfo = new Me_Fragment().mycodes;
        findViewId();
        init();


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
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
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


    private void findViewId() {
        csed1 = (EditText) findViewById(R.id.csed1);
        csed2 = (EditText) findViewById(R.id.csed2);
        btnView = (TextView) findViewById(R.id.get_btn);
        vaildatepwd = (EditText) findViewById(R.id.vaildatepwd);
        bt_yanzheng = (Button) findViewById(R.id.rgs_btn);
        finish = (ImageView) findViewById(R.id.iv_finish);
        ll_mima = (LinearLayout) findViewById(R.id.ll_mima);
        xian = (View) findViewById(R.id.xian);

        rl_denglu = (RelativeLayout) findViewById(R.id.rl_denglu);
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(xiugai_phonenum_activity.this) * Data_Util.HEAD_NEW);
            rl_denglu.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(xiugai_phonenum_activity.this) * Data_Util.HEAD_OLD);
            rl_denglu.setLayoutParams(vParams1);
        }

    }

    private boolean isfirst = false;

    private void init() {
        btnView.setOnClickListener(this);
        bt_yanzheng.setOnClickListener(this);
        finish.setOnClickListener(this);

        if (SPUtils.getString(xiugai_phonenum_activity.this, "password") == null || TextUtils.isEmpty(SPUtils.getString(xiugai_phonenum_activity.this, "password"))) {
            isfirst = true;
        } else {
            isfirst = false;
            xian.setVisibility(View.GONE);
            ll_mima.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击获取验证码控件
            case R.id.get_btn:

                if (vaildateinfo()) {
                    btnView.setEnabled(false);
                    btnView.setTextColor(Color.GRAY);
                    //启动获取验证码 86是中国
                    String zh = csed1.getText().toString().trim();
                    SMSSDK.getVerificationCode("86", zh);
                    timer.start();

                }
                break;
            //点击提交信息按钮
            case R.id.rgs_btn:
                //   VaildateputInfo();

                code = vaildatepwd.getText().toString().trim();
                if (method()) {
                    if (!TextUtils.isEmpty(code)) {
                        if (isfirst) {
                            inithttp(csed2.getText().toString().trim());
                        } else {
                            inithttp("");
                        }
                    } else {
                        MyToast.showToast(xiugai_phonenum_activity.this, "请输入验证码");
                    }
                }
                break;

            case R.id.iv_finish:
                finish();
                break;
        }
    }

    private void inithttp(String s) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"oldPhone\":\"" + myCodeInfo.getObj().getPhone() + "\",\"newPhone\":\"" + zh + "\",\"password\":\"" + MD5Utils.encode(s) + "\",\"verifyCode\":\"" + code + "\"},\"token\":\"" + SPUtils.getString(xiugai_phonenum_activity.this, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!BindPhone.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(xiugai_phonenum_activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);
                Log.i("test", "地址：" + "请求成功" + mycode.getCode());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycode.getCode()) {
                            Intent intent = new Intent();
                            intent.putExtra("phone", zh);
                            setResult(2, intent);
                            SPUtils.put(xiugai_phonenum_activity.this, "password", "" + zh);
                            MyToast.showToast(xiugai_phonenum_activity.this, "修改成功");
                            finish();
                        } else if (9 == mycode.getCode()) {
                            MyToast.showToast(xiugai_phonenum_activity.this, "此手机号已被绑定");
                        } else if (7 == mycode.getCode()) {
                            MyToast.showToast(xiugai_phonenum_activity.this, "验证码无效");
                        } else {
                            MyToast.showToast(xiugai_phonenum_activity.this, "修改失败");

                        }
                    }
                });
            }
        });
    }

    /**
     * 1.验证验证码
     * 2.提交用户信息
     */
    private void VaildateputInfo() {
        vaildatePassword();
    }

    //验证 验证码
    private void vaildatePassword() {

        SMSSDK.submitVerificationCode("86", zh, code);
        putUserInfo("86", zh);
    }

    //提交用户信息
    private void putUserInfo(String country, String phone) {
        Random rnd = new Random();
        int id = Math.abs(rnd.nextInt());
        String uid = String.valueOf(id);
        String nickName = "SmsSDK_User_" + uid;
        String avatar = AVATARS[id % 12];
        SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
    }

    //计时器
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            btnView.setText((millisUntilFinished / 1000) + "秒后可重发");
        }

        @Override
        public void onFinish() {
            btnView.setEnabled(true);
            btnView.setText("获取验证码");
            btnView.setTextColor(Color.parseColor("#444444"));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    //验证注册信息
    private boolean vaildateinfo() {
        return method();
    }

    private boolean method() {
        zh = csed1.getText().toString().trim();
        //首先要判断是否为空
        if (!zh.equals("") || null != zh) {
            if (zh.length() == 11) {
                if (isfirst) {
                    if (TextUtils.isEmpty(csed2.getText().toString().trim())) {
                        MyToast.showToast(xiugai_phonenum_activity.this, "请设置密码");
                    } else {
                        if (csed2.getText().toString().trim().length() < 6 || csed2.getText().toString().trim().length() > 16) {
                            MyToast.showToast(xiugai_phonenum_activity.this, "密码格式不正确");
                        } else {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            } else {
                MyToast.showToast(xiugai_phonenum_activity.this, "手机号格式错误");
                csed1.requestFocus();
            }
        } else {
            MyToast.showToast(xiugai_phonenum_activity.this, "手机号不能为空");
            csed1.requestFocus();
        }
        return false;
    }


}