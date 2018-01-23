package com.aou.cheba.Activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_fankui;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyToast;
import com.bumptech.glide.Glide;
import com.github.library.bubbleview.BubbleTextVew;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * Created by Administrator on 2016/11/17.
 */
public class Fankui extends SwipeBackActivity implements View.OnClickListener {

    private RelativeLayout biaoti;
    private ImageView finish;
    private ListView lv;
    private EditText et_con;
    private Button enter;
    private List<String> list_content=new ArrayList<>();
    private List<Map<String,String>> list_content_map=new ArrayList<>();
    private MyAdap myAdap;
    private RelativeLayout rl_wai;
    private LinearLayout ll_huadong;
    private String trim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fankui_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        findId();
        inithttp_init_fankui();
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

    class MyAdap extends BaseAdapter{

        @Override
        public int getCount() {
            return list_content_map.size();
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
                convertView = View.inflate(Fankui.this, R.layout.fankui_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_1 = (ImageView) convertView.findViewById(R.id.iv_1);
                viewHolder.iv_2 = (ImageView) convertView.findViewById(R.id.iv_2);
                viewHolder.tv_1 = (BubbleTextVew) convertView.findViewById(R.id.textView);
                viewHolder.tv_2 = (BubbleTextVew) convertView.findViewById(R.id.textView2);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            if (list_content_map.get(position).get("0") == null) {
                viewHolder.iv_2.setVisibility(View.GONE);
                viewHolder.iv_1.setVisibility(View.VISIBLE);
                viewHolder.tv_2.setVisibility(View.GONE);
                viewHolder.tv_1.setVisibility(View.VISIBLE);
                viewHolder.tv_1.setText(list_content_map.get(position).get("1"));
            } else {
                viewHolder.iv_1.setVisibility(View.GONE);
                viewHolder.iv_2.setVisibility(View.VISIBLE);
                viewHolder.tv_1.setVisibility(View.GONE);
                viewHolder.tv_2.setVisibility(View.VISIBLE);
                viewHolder.tv_2.setText(list_content_map.get(position).get("0"));
                Glide.with(Fankui.this).load(SPUtils.getString(Fankui.this, "headImage")).into(viewHolder.iv_2);
            }

            return convertView;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.bt_fabiao:
                trim = et_con.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    MyToast.showToast(Fankui.this, "请输入反馈内容");
                } else {
                    inithttp_fankui(trim);
                }
                break;
        }
    }

    Handler handler=new Handler();
    private void inithttp_fankui(final String s) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"content\":\"" + s + "\"},\"token\":\"" + SPUtils.getString(Fankui.this, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!FeedBack.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Fankui.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            HashMap<String, String> st = new HashMap<>();
                            st.put("0",s);
                            list_content_map.add(st);
                            myAdap.notifyDataSetChanged();

                            et_con.setText(null);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                            lv.setSelection(myAdap.getCount());
                        } else {
                            MyToast.showToast(Fankui.this, "反馈失败");
                        }
                    }
                });

            }
        });
    }

    private void inithttp_init_fankui() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(Fankui.this, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!LoadFeedback.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Fankui.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_fankui mycode = gson.fromJson(res, MyCode_fankui.class);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            HashMap<String, String> map;
                            HashMap<String, String> map2;
                            for (MyCode_fankui.ObjBean objBean : mycode.getObj()) {
                                map = new HashMap<>();
                                if (objBean.getHandle() == null) {
                                    map.put("0", objBean.getContent());
                                    list_content_map.add(map);
                                } else {
                                    map.put("0", objBean.getContent());
                                    list_content_map.add(map);


                                    map2 = new HashMap<>();
                                    map2.put("1", objBean.getHandle());
                                    list_content_map.add(map2);
                                }
                            }

                            HashMap<String, String> stringStringHashMap = new HashMap<>();
                            stringStringHashMap.put("1","如果你有什么意见和建议，请反馈给我们的技术人员，您的支持是我们的动力源泉。");
                            list_content_map.add(0,stringStringHashMap);

                            myAdap = new MyAdap();
                            lv.setAdapter(myAdap);

                            lv.setSelection(myAdap.getCount());
                        }
                    }
                });

            }
        });
    }

    static class ViewHolder {

        ImageView iv_1;
        ImageView iv_2;
        BubbleTextVew tv_1;
        BubbleTextVew tv_2;
    }

    private void findId() {
        biaoti = (RelativeLayout) findViewById(R.id.rl_biaoti);
        rl_wai = (RelativeLayout) findViewById(R.id.rl_wai);
        ll_huadong = (LinearLayout) findViewById(R.id.rl_dicontent);
        finish = (ImageView) findViewById(R.id.iv_finish);
        lv = (ListView) findViewById(R.id.lv);
        et_con = (EditText) findViewById(R.id.tv_huitie);
        enter = (Button) findViewById(R.id.bt_fabiao);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = biaoti.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Fankui.this) * Data_Util.HEAD_NEW);
            biaoti.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = biaoti.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Fankui.this) * Data_Util.HEAD_OLD);
            biaoti.setLayoutParams(vParams1);
        }

        //软键盘的弹出监听
        controlKeyboardLayout(rl_wai, ll_huadong);

        lv.setClickable(false);
        finish.setOnClickListener(this);
        enter.setOnClickListener(this);
    }

    private int size = 0;
    private void controlKeyboardLayout(final View root, final View needToScrollView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private Rect r = new Rect();
            @Override
            public void onGlobalLayout() {
                Fankui.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = Fankui.this.getWindow().getDecorView().getRootView().getHeight();
                int heightDifference = screenHeight - r.bottom;
                if (Math.abs(heightDifference - size) != 0) {
                    final ObjectAnimator animator = ObjectAnimator.ofFloat(needToScrollView, "translationY", 0, -heightDifference);
                    size = heightDifference;
                    animator.setDuration(0);
                    animator.start();
                }
            }
        });
    }
}
