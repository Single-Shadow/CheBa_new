package com.aou.cheba.Activity;
/**
 * Created by Administrator on 2016/9/5.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_che;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.MD5Utils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyToast;
import com.google.gson.Gson;
import com.tandong.bottomview.view.BottomView;

import java.io.IOException;
import java.util.List;
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
public class Regeister extends SwipeBackActivity implements View.OnClickListener {
    private static final String[] AVATARS = new String[400];
    Button btnView;
    EditText csed1 = null, csed2 = null;
    EditText vaildatepwd = null;
    private String zh;
    private String pwd;
    private Button bt_yanzheng;
    private RelativeLayout rl_denglu;
    private String code;
    private ImageView finish;
    private Handler h = new Handler();
    private TextView che;
    private String name;
    private BottomView bottomView;
    private ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuce_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SMSSDK.initSDK(this, Data_Util.APP_KEY, Data_Util.APP_SECRET); //车吧的短信验证
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
        btnView = (Button) findViewById(R.id.get_btn);
        che = (TextView) findViewById(R.id.che);
        vaildatepwd = (EditText) findViewById(R.id.vaildatepwd);
        bt_yanzheng = (Button) findViewById(R.id.rgs_btn);
        finish = (ImageView) findViewById(R.id.iv_finish);


        rl_denglu = (RelativeLayout) findViewById(R.id.rl_denglu);
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Regeister.this) * Data_Util.HEAD_NEW);
            rl_denglu.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Regeister.this) * Data_Util.HEAD_OLD);
            rl_denglu.setLayoutParams(vParams1);
        }

    }

    private void init() {
        btnView.setOnClickListener(this);
        bt_yanzheng.setOnClickListener(this);
        che.setOnClickListener(this);
        finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                        bt_yanzheng.setClickable(false);
                        inithttp();
                    } else {
                        MyToast.showToast(Regeister.this, "请输入验证码");
                    }
                }
                break;

            case R.id.iv_finish:
                finish();
                break;
            case R.id.che:
                inithttp_chelist();
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
                        MyToast.showToast(Regeister.this, "连接服务器失败");
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
                            bottomView = new BottomView(Regeister.this,R.style.BottomViewTheme_Defalut, R.layout.bottom_view);
                            bottomView.setAnimation(R.style.BottomToTopAnim);

                            bottomView.showBottomView(true);
                            lv = (ListView) bottomView.getView().findViewById(R.id.lv_list);
                            MyAdapter adapter = new MyAdapter();
                            lv.setAdapter(adapter);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    name=list_che.get(arg2).getName();
                                    che.setText(name);
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
                convertView = View.inflate(Regeister.this, R.layout.che_list_item, null);
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

    private void inithttp() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        String ima="default_headImg";
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\": {\"loginType\": 1,\"password\": \"" + MD5Utils.encode(pwd) + "\",\"carInfo\": \"" + name + "\",\"phone\": \"" + zh + "\",\"verifyCode\":\"" + code + "\",\"headImg\": \"" + ima + "\"}}");
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
                        bt_yanzheng.setClickable(true);
                        MyToast.showToast(Regeister.this, "连接服务器失败");
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
                        bt_yanzheng.setClickable(true);
                        if (0 == mycode.getCode()) {
                            MyToast.showToast(Regeister.this, "注册成功 请登录");
                            finish();
                        } else if (3 == mycode.getCode()) {

                            MyToast.showToast(Regeister.this, "账号已注册");
                        } else if (-1 == mycode.getCode()) {
                            MyToast.showToast(Regeister.this, "注册失败");

                        } else if (7 == mycode.getCode()) {
                            MyToast.showToast(Regeister.this, "验证码无效");
                        }
                    }
                });
            }
        });
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
        pwd = csed2.getText().toString().trim();
        //首先要判断是否为空
        if (!zh.equals("") || null != zh) {
            if (zh.length() == 11) {
                if (!pwd.equals("") || null != pwd) {
                    if (pwd.length() >= 6) {
                        if (pwd.length() <= 16) {
                            if (TextUtils.isEmpty(che.getText().toString().trim())) {
                                MyToast.showToast(Regeister.this, "请选择车型");
                            } else {
                                return true;
                            }
                        } else {
                            MyToast.showToast(Regeister.this, "密码过长 请重新输入");
                            csed2.requestFocus();
                        }
                    } else {
                        MyToast.showToast(Regeister.this, "密码过短 请重新输入");
                        csed2.requestFocus();
                    }
                } else {
                    MyToast.showToast(Regeister.this, "密码不能为空");
                    csed2.requestFocus();
                }
            } else {
                MyToast.showToast(Regeister.this, "手机号格式错误");
                csed1.requestFocus();
            }
        } else {
            MyToast.showToast(Regeister.this, "手机号不能为空");
            csed1.requestFocus();
        }
        return false;
    }


}