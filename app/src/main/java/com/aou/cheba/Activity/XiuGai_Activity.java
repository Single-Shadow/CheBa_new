package com.aou.cheba.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.MD5Utils;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyToast;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
public class XiuGai_Activity extends SwipeBackActivity {

    private EditText xg_mima;
    private EditText yuan_mima;
    private Button xg_queding;
    private EditText xg_again_mima;
    private RelativeLayout rl_xiugai;
    private String xin;
    private String yuan;
    private String again_mima;
    private ImageView finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiugai_mima_activity);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏 一些手机底部存在虚拟键 所以不需要
        //   getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        xg_mima = (EditText) findViewById(R.id.et_xg_mima);
        yuan_mima = (EditText) findViewById(R.id.et_yuan_mima);
        xg_again_mima = (EditText) findViewById(R.id.xg_again_mima);
        xg_queding = (Button) findViewById(R.id.bt_xg_queding);
        finish = (ImageView) findViewById(R.id.iv_finish);

        rl_xiugai = (RelativeLayout) findViewById(R.id.rl_xiugai_mima);
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = rl_xiugai.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(XiuGai_Activity.this) * Data_Util.HEAD_NEW);
            rl_xiugai.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = rl_xiugai.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(XiuGai_Activity.this) * Data_Util.HEAD_OLD);
            rl_xiugai.setLayoutParams(vParams1);
        }

        xg_queding.setOnClickListener(MyOnClick);
        finish.setOnClickListener(MyOnClick);
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

    private View.OnClickListener MyOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_xg_queding:
                    xin = xg_mima.getText().toString().trim();
                    yuan = yuan_mima.getText().toString().trim();
                    again_mima = xg_again_mima.getText().toString().trim();

                    if (TextUtils.isEmpty(xin) || TextUtils.isEmpty(yuan) || TextUtils.isEmpty(again_mima)) {
                        MyToast.showToast(XiuGai_Activity.this, "请输入密码");
                    } else {
                        if (xin.length() < 6 || xin.length() > 16 || yuan.length() < 6 || yuan.length() > 16 || again_mima.length() < 6 || again_mima.length() > 16) {
                            MyToast.showToast(XiuGai_Activity.this, "密码长度格式有误");
                        } else {
                            if (xin.equals(again_mima)) {
                                if (xin.equals(yuan)) {
                                    MyToast.showToast(XiuGai_Activity.this, "新密码不能与原密码相同");
                                } else {
                                    inithttp_xiugai();
                                }
                            } else {
                                Toast.makeText(XiuGai_Activity.this, "两次新密码输入不一致", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;

                case R.id.iv_finish:
                    finish();
                    break;
            }
        }
    };

    private void inithttp_xiugai() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        String encode_yuan = MD5Utils.encode(yuan);
        String encode_xin = MD5Utils.encode(xin);
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"oldPassword\":\"" + encode_yuan + "\",\"newPassword\":\"" + encode_xin + "\"},\"token\":\"" + SPUtils.getString(XiuGai_Activity.this, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!ModifyPwd.action")
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(XiuGai_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 myCode1 = gson.fromJson(res, MyCode1.class);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myCode1.getCode() == 0) {
                            finish();
                            MyToast.showToast(XiuGai_Activity.this, "修改成功");
                        } else {
                            MyToast.showToast(XiuGai_Activity.this, "修改失败");
                        }
                    }
                });
            }
        });
    }
}
