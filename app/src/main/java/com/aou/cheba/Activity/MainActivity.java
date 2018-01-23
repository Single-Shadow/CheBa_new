package com.aou.cheba.Activity;

import android.app.FragmentManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.Fragment_new.CheYouQuan_viewpager;
import com.aou.cheba.Fragment_new.ShaiChe_Fragment;
import com.aou.cheba.Fragment_new.YueTu_Fragment;
import com.aou.cheba.Fragment_new.ZiXun_Fragment;
import com.aou.cheba.LocationApplication;
import com.aou.cheba.R;
import com.aou.cheba.Service.LocationService;
import com.aou.cheba.bean.MyCode_News;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.view.MoreWindow;
import com.aou.cheba.view.MyToast;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    public int id = -1;
    private long mExitTime;
    public static RadioGroup mainRg;
    public static List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentManager fragmentManager;
    private RadioButton bt_cheba;
    private RadioButton bt_wode;
    private RadioButton rb_jia;
    public static PushAgent mPushAgent;
    MoreWindow mMoreWindow;
    private RadioButton rg_lukuan;
    private RadioButton rg_meirong;
    public static LocationService locationServices;
    public static IWXAPI api;
    public static Platform qq_login;
    public static Platform weibo_login;
    private RadioButton rb_wode;
    private boolean checkLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShareSDK.initSDK(this);
        qq_login = ShareSDK.getPlatform(QQ.NAME);
        weibo_login = ShareSDK.getPlatform(SinaWeibo.NAME);
        // 通过WXAPIFactory工厂,获得IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, "wx79ee8a2e6fdf7ae0", true);
        // 将应用的appid注册到微信
        api.registerApp("wx79ee8a2e6fdf7ae0");


        LocationApplication application = (LocationApplication) getApplication();
        locationServices = application.locationService;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(100 * 1024 * 1024)
                .diskCacheFileCount(300).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);

        Log.d("test", "进入main");
        // 开启推送
 /*       mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();
        mPushAgent.setDebugMode(false);
        mPushAgent.enable(new IUmengCallback() {
            @Override
            public void onSuccess() {
                Log.i("test_token","success");
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.i("test_token_failure",s+" s1 "+s1);
            }
        });*/

        mPushAgent = LocationApplication.mPushAgent;
        mPushAgent.onAppStart();
        mPushAgent.setDebugMode(false);
        mPushAgent.setDisplayNotificationNumber(6);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                try {
                    String news = SPUtils.getString(MainActivity.this, "news" + SPUtils.getString(MainActivity.this, "uid"));
                    Gson gson = new Gson();
                    MyCode_News news_bean = new MyCode_News();
                    if (!TextUtils.isEmpty(news)) {
                        news_bean = SerializeUtils.json2Object(news, MyCode_News.class);
                    } else {
                        news_bean.setData(new ArrayList<MyCode_News.DataBean>());
                    }

                    MyCode_News.DataBean dataBean = new MyCode_News.DataBean();
                    Log.d("test", "getNotification5=");
                    Map<String, String> extra = msg.extra;
                    dataBean.setContent(extra.get("content"));
                    dataBean.setDid(extra.get("did"));
                    dataBean.setHeadImg(extra.get("headImg"));
                    dataBean.setNickname(extra.get("nickname"));
                    dataBean.setUid(extra.get("uid"));
                    dataBean.setType(extra.get("type"));
                    dataBean.setRecvTime(System.currentTimeMillis());
                    if (extra.get("pictrue") == null) {
                        dataBean.setPictrue("");
                    } else {
                        dataBean.setPictrue(extra.get("pictrue"));
                    }
                    news_bean.getData().add(0, dataBean);
                    String s1 = "";

                    s1 = SerializeUtils.object2Json(news_bean);
                    SPUtils.put(MainActivity.this, "news" + SPUtils.getString(MainActivity.this, "uid"), s1);

                    int put_num = SPUtils.getInt(MainActivity.this, "newsnum" + SPUtils.getString(MainActivity.this, "uid")) + 1;
                    SPUtils.put(MainActivity.this, "newsnum" + SPUtils.getString(MainActivity.this, "uid"), put_num);

                   /* TextView tv_news_num = new Me_Fragment().news_num;
                    if (tv_news_num != null) {
                        tv_news_num.setText("你有" + put_num + "条新消息");
                    }*/
                } catch (Exception e) {
                    Log.d("test", "main出错了=");
                    e.printStackTrace();
                }

                return super.getNotification(context, msg);
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        bt_cheba = (RadioButton) findViewById(R.id.rg_cheba);
        rg_lukuan = (RadioButton) findViewById(R.id.rg_lukuan);
        rg_meirong = (RadioButton) findViewById(R.id.rg_meirong);
        rb_jia = (RadioButton) findViewById(R.id.rb_jia);
        rb_wode = (RadioButton) findViewById(R.id.rb_wode);
        mainRg = (RadioGroup) findViewById(R.id.main_rg);
        bt_cheba.setOnClickListener(this);
        rg_lukuan.setOnClickListener(this);
        rg_meirong.setOnClickListener(this);
        rb_jia.setOnClickListener(this);
        rb_wode.setOnClickListener(this);


//        fragments.add(new New_home_viewpager());
        fragments.add(new ZiXun_Fragment());
        fragments.add(new CheYouQuan_viewpager());
        fragments.add(new ShaiChe_Fragment());
//        fragments.add(new New_jifen_viewpager());
        fragments.add(new YueTu_Fragment());
        fragments.add(new Me_Fragment());

        fragmentManager = getFragmentManager();
        mainRg.setOnCheckedChangeListener(checkListener);

        ((RadioButton) mainRg.getChildAt(0)).setChecked(true);
        id = 0;
    }

    private boolean isenter = false;
    Handler h = new Handler();
    private RadioGroup.OnCheckedChangeListener checkListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            mainRg.setEnabled(false);

            View child = group.findViewById(checkedId);
            int index = group.indexOfChild(child);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            switch (index) {
                case 0:
                    if (id != 0) {
                        for (int i = 0; i < fragments.size(); i++) {
                            if (i != 0) {
                                Fragment fragment = fragments.get(i);
                                if (fragment.isAdded()) {
                                    if (!fragment.isHidden()) {
                                        transaction.hide(fragment).commitAllowingStateLoss();
                                    }
                                }
                            }
                        }
                        replaceFragmentWithBackStack(fragments.get(0));
//                        id = 0;
                    }
                    break;

                case 1:
                    if (id != 1) {
                        for (int i = 0; i < fragments.size(); i++) {
                            if (i != 1) {
                                Fragment fragment = fragments.get(i);
                                if (fragment.isAdded()) {
                                    if (!fragment.isHidden()) {
                                        transaction.hide(fragment).commitAllowingStateLoss();
                                    }
                                }
                            }
                        }
                        replaceFragmentWithBackStack(fragments.get(1));
//                        id = 1;
                    }
                    break;
                case 2:
                    if (id != 2) {
                        for (int i = 0; i < fragments.size(); i++) {
                            if (i != 2) {
                                Fragment fragment = fragments.get(i);
                                if (fragment.isAdded()) {
                                    if (!fragment.isHidden()) {
                                        transaction.hide(fragment).commitAllowingStateLoss();
                                    }
                                }
                            }
                        }
                        replaceFragmentWithBackStack(fragments.get(2));
//                        id = 2;
                    }
                    break;

                case 3:
                    if (id != 3) {
                        for (int i = 0; i < fragments.size(); i++) {
                            if (i != 3) {
                                Fragment fragment = fragments.get(i);
                                if (fragment.isAdded()) {
                                    if (!fragment.isHidden()) {
                                        transaction.hide(fragment).commitAllowingStateLoss();
                                    }
                                }
                            }
                        }
                        replaceFragmentWithBackStack(fragments.get(3));
//                        id = 3;
                    }
                    break;

                case 4:
                    if (TextUtils.isEmpty(SPUtils.getString(MainActivity.this, "token"))) {
                        ((RadioButton) mainRg.getChildAt(id)).setChecked(true);
                        checkLogin = false;//防止点击事件后执行 会改变id
                        if (!isenter) {
                            isenter = true;
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isenter = false;
                                }
                            }, 500);
                            startActivityForResult(new Intent(MainActivity.this, Login_Activity.class), 100);
                        }
                    } else {
                        if (id != 4) {
                            for (int i = 0; i < fragments.size(); i++) {
                                if (i != 4) {
                                    Fragment fragment = fragments.get(i);
                                    if (fragment.isAdded()) {
                                        if (!fragment.isHidden()) {
                                            transaction.hide(fragment).commitAllowingStateLoss();
                                        }
                                    }
                                }
                            }
                            replaceFragmentWithBackStack(fragments.get(4));
//                            id = 4;
                        }
                    }
                    break;
            }
            mainRg.setEnabled(true);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 160) {
            Log.i("test", "返回函数160");
            if (SPUtils.getString(MainActivity.this, "token") != null && !TextUtils.isEmpty(SPUtils.getString(MainActivity.this, "token"))) {
                ((RadioButton) mainRg.getChildAt(4)).setChecked(true);
                id = 4;
            }
        }
        if (resultCode == 11) {
            Log.i("test", "返回函数11");
            ((RadioButton) mainRg.getChildAt(0)).setChecked(true);
            id = 0;
        }
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view, 100);
    }

    public void replaceFragmentWithBackStack(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (fragment.isAdded()) {
            transaction.show(fragment).commitAllowingStateLoss();
            return;
        }
        transaction.add(R.id.main_fragment_container, fragment).commitAllowingStateLoss();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                MyToast.showToast(this, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                // moveTaskToBack(true);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rg_cheba:
                if (id == 0) {
                    ZiXun_Fragment zx = (ZiXun_Fragment) fragments.get(0);
                    zx.ZXscoll();
                }
                id = 0;
                break;
            case R.id.rg_lukuan:
                if (id == 1) {
                    CheYouQuan_viewpager cyq = (CheYouQuan_viewpager) fragments.get(1);
                    cyq.MIXscoll();
                }
                id = 1;
                break;
            case R.id.rb_jia:
                if (id == 2) {
                    ShaiChe_Fragment sc = (ShaiChe_Fragment) fragments.get(2);
                    sc.SCscoll();
                }
                id = 2;
                break;
            case R.id.rg_meirong:
                if (id == 3) {
                    YueTu_Fragment yt = (YueTu_Fragment) fragments.get(3);
                    yt.YTscoll();
                }
                id = 3;
                break;
            case R.id.rb_wode:
                if (checkLogin) {
                    id = 4;
                } else {
                    checkLogin = true;
                }
                break;
        }
    }
}
