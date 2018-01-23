package com.aou.cheba.Activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment_new.YueTu_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.bean.MyCode_pinglun;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.aou.cheba.view.RollHeaderView_yuetu;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/12/12.
 */
public class YueTu_Activity extends SwipeBackActivity implements View.OnClickListener {

    private MyCode_data.ObjBean info;
    private String pictrue;
    private String[] split;
    Handler h = new Handler();
    Bitmap bitmap;
    int p;
    String s = "";
    private RelativeLayout rl_wai;
    private LinearLayout rl_xia;
    private LinearLayout ll_suiyi;
    private ImageView finish;
    private ImageView iv_xin;
    private ImageView iv_shoucang;
    private ImageView iv_fenxiang;
    private EditText tv_huitie2;
    private Button fabiao;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    boolean isup = false;
    private Long isson = null;
    private boolean isclickson;
    private RollHeaderView_yuetu rh;
    private TextView title;
    private List<MyCode_pinglun.ObjBean> comment_list = new ArrayList<>();
    private int position_cheka;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yuetu_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        info = (MyCode_data.ObjBean) getIntent().getSerializableExtra("ser");
        position_cheka = getIntent().getIntExtra("position", 0);
        findViewById();
        getHead();
        initData();
        inithttp_getpinglun_list(1);

    }

    private void findViewById() {
        rl_wai = (RelativeLayout) findViewById(R.id.rl_wai);
        rl_xia = (LinearLayout) findViewById(R.id.rl_xia);
        ll_suiyi = (LinearLayout) findViewById(R.id.ll_suiyi);
        finish = (ImageView) findViewById(R.id.iv_finish);
        iv_xin = (ImageView) findViewById(R.id.iv_xin);
        iv_shoucang = (ImageView) findViewById(R.id.iv_shoucang);
        iv_fenxiang = (ImageView) findViewById(R.id.iv_fenxiang);
        tv_huitie2 = (EditText) findViewById(R.id.tv_huitie2);
        fabiao = (Button) findViewById(R.id.tv_publish);
        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);

        finish.setOnClickListener(this);
        fabiao.setOnClickListener(this);
        // tv_huitie2.setOnClickListener(this);
        iv_xin.setOnClickListener(this);
        iv_shoucang.setOnClickListener(this);
        iv_fenxiang.setOnClickListener(this);

        if (info.isUped()) {
            iv_xin.setImageResource(R.mipmap.x_hong);
        } else {
            iv_xin.setImageResource(R.mipmap.x);
        }

        if (info.isCollected()) {
            iv_shoucang.setImageResource(R.mipmap.sc_hong);
        } else {
            iv_shoucang.setImageResource(R.mipmap.sc);
        }

        //软键盘的弹出监听
        controlKeyboardLayout(rl_wai, rl_xia);

        rl_wai.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rl_wai.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rl_wai.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);
                        if (heightDifference > 200) {

                            fabiao.setVisibility(View.VISIBLE);
                            ll_suiyi.setVisibility(View.GONE);
                            isup = true;
                            Log.i("test", "弹出键盘");
                            tv_huitie2.setHint(s);
                        } else {
                            if (isup) {
                                fabiao.setVisibility(View.GONE);
                                ll_suiyi.setVisibility(View.VISIBLE);
                                Log.i("test", "键盘收回");
                                isup = false;
                                isson = null;
                                s = "";

                                tv_huitie2.setText("");
                                tv_huitie2.setHint("说几句...");
                            }


                        }
                    }
                });
    }

    private void getHead() {
        View inflate = View.inflate(YueTu_Activity.this, R.layout.yuetu_head, null);
        mLoadMoreListView.addHeaderView(inflate);

        rh = ((RollHeaderView_yuetu) inflate.findViewById(R.id.rh));
        title = ((TextView) inflate.findViewById(R.id.title));
        title.setText(info.getTitle());
        pictrue = info.getPictrue();
        split = pictrue.split(",");
        List<String> list = new ArrayList<>();
        for (String s1 : split) {
            list.add(Data_Util.IMG + s1);
        }

        ViewGroup.LayoutParams vParam4s = rh.getLayoutParams();
        vParam4s.height = (int) (DisplayUtil.getMobileHeight(YueTu_Activity.this) * 0.6);
        rh.setLayoutParams(vParam4s);

        rh.setImgUrlData(list);
        rh.mViewPager.setCurrentItem(0);
        rh.setOnHeaderViewClickListener(new RollHeaderView_yuetu.HeaderViewClickListener() {
            @Override
            public void HeaderViewClick(int position) {

            }

            @Override
            public void XiaZai(int position) {

            }

            @Override
            public void isfinish() {

            }

            @Override
            public void shoucang(int position) {

            }

            @Override
            public void toDetail(int position) {
                Intent i=new Intent(YueTu_Activity.this,YueTuDetailActivity.class);
                i.putExtra("pic",info.getPictrue());
                i.putExtra("pos",position);
                startActivity(i);
            }
        });
    }

    private void initData() {
        adapter = new MyAdapter();
        mLoadMoreListView.setAdapter(adapter);

        mRefreshAndLoadMoreView.setEnabled(false);
        mRefreshAndLoadMoreView.setLoadMoreListView(mLoadMoreListView);
        mLoadMoreListView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreView);

        mRefreshAndLoadMoreView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                inithttp_getpinglun_list(page);
            }
        });
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page += 1;
                inithttp_getpinglun_list(page);
            }
        });

        mLoadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("test", "键盘");
                if (position != 0) {
                    isclickson = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isclickson = false;
                        }
                    }, 500);


//打开软键盘
                    InputMethodManager imm = (InputMethodManager) YueTu_Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    // 获取编辑框焦点
                    tv_huitie2.setFocusable(true);
                    tv_huitie2.setFocusableInTouchMode(true);
                    tv_huitie2.requestFocus();
         /*           InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(tv_huitie2, InputMethodManager.SHOW_FORCED);*/

//                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(tv_huitie2,InputMethodManager.SHOW_FORCED);
                    isson = comment_list.get(position - 1).getId();
                    s = "回复" + comment_list.get(position - 1).getNickname();
                }
            }
        });

    }

    private MyAdapter adapter;

    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return comment_list.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = View.inflate(YueTu_Activity.this, R.layout.pinglun_item, null);
                viewHolder = new ViewHolder();
                viewHolder.headima = (CircleImageView) convertView.findViewById(R.id.iv_headima);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
                viewHolder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.lv = (MyListView) convertView.findViewById(R.id.lv_pinglun);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            Glide.with(YueTu_Activity.this).load(Data_Util.IMG + comment_list.get(position).getHeadImg()).into(viewHolder.headima);
            viewHolder.nickname.setText(comment_list.get(position).getNickname());

            viewHolder.tv_zan.setText(TimeUtil.getDateTimeFromMillisecond2(comment_list.get(position).getDate()));

            viewHolder.content.setText(comment_list.get(position).getContent());
            viewHolder.headima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(YueTu_Activity.this, Other_Activity.class);
                    intent.putExtra("uid", comment_list.get(position).getUid());
                    startActivity(intent);
                }
            });


            if (comment_list.get(position).getSubList() == null || comment_list.get(position).getSubList().size() == 0) {
                viewHolder.lv.setVisibility(View.GONE);
            } else {
                viewHolder.lv.setVisibility(View.VISIBLE);
                viewHolder.lv.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return comment_list.get(position).getSubList().size();
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
                    public View getView(int i, View convertView, ViewGroup parent) {
                        ViewHolder2 viewHolder2 = null;

                        if (convertView == null) {
                            convertView = View.inflate(YueTu_Activity.this, R.layout.pinglun_item_son, null);
                            viewHolder2 = new ViewHolder2();
                            viewHolder2.tv_content_son = (TextView) convertView.findViewById(R.id.tv_content_son);
                            viewHolder2.tv_nickname_son = (TextView) convertView.findViewById(R.id.tv_nickname_son);

                            convertView.setTag(viewHolder2);
                        }
                        viewHolder2 = (ViewHolder2) convertView.getTag();
                        viewHolder2.tv_content_son.setText(":" + comment_list.get(position).getSubList().get(i).getContent());
                        viewHolder2.tv_nickname_son.setText(comment_list.get(position).getSubList().get(i).getNickname());

                        return convertView;
                    }
                });
            }
            return convertView;
        }
    }

    private void inithttp_up(final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(YueTu_Activity.this, "token") + "\",\"obj\":{\"id\":\"" + info.getId() + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Yuetu!Like.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(YueTu_Activity.this, "连接服务器失败");
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
                        if (mycode.getCode() == 0) {

                            info.setUped(true);
                            iv_xin.setImageResource(R.mipmap.x_hong);
                            YueTu_Fragment.list_data.get(position).setUped(true);

                            MyToast.showToast(YueTu_Activity.this, "赞");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(YueTu_Activity.this, "token", "");
                            MyToast.showToast(YueTu_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_shoucang(long did, String token, final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\": {\"did\": " + did + "},\"token\": \"" + token + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Collection.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(YueTu_Activity.this, "连接服务器失败");
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
                            info.setCollected(true);
                            YueTu_Fragment.list_data.get(position).setCollected(true);
                            iv_shoucang.setImageResource(R.mipmap.sc_hong);
                            setResult(55);

                            MyToast.showToast(YueTu_Activity.this, "收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(YueTu_Activity.this, "token", "");
                            MyToast.showToast(YueTu_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_delshoucang(long did, String token, final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\": {\"did\": " + did + "},\"token\": \"" + token + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!DelCollection.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(YueTu_Activity.this, "连接服务器失败");
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
                            info.setCollected(false);
                            YueTu_Fragment.list_data.get(position).setUped(false);
                            iv_shoucang.setImageResource(R.mipmap.sc);
                            setResult(55);
                            MyToast.showToast(YueTu_Activity.this, "取消收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(YueTu_Activity.this, "token", "");
                            MyToast.showToast(YueTu_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)&&isShouldHideInput(fabiao,ev)) {

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
        if (v != null) {
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

    private boolean isenter = false;

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
            case R.id.btn_guanzhu:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(YueTu_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(YueTu_Activity.this, Login_Activity.class));
                        MyToast.showToast(YueTu_Activity.this, "请先登录");
                    } else {
                        if (info.isFollowed()) {
                            MyToast.showToast(YueTu_Activity.this, "已关注");
                        } else {
//                            inithttp_guanzhu(info.getUid(), SPUtils.getString(YueTu_Activity.this, "token"));
                        }
                    }
                }
                break;
            case R.id.iv_head:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent = new Intent(YueTu_Activity.this, Other_Activity.class);
                    intent.putExtra("uid", info.getUid());
                    startActivity(intent);
                }
                break;
            case R.id.ll_love:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent1 = new Intent(YueTu_Activity.this, Love_Activity.class);
                    intent1.putExtra("id", info.getId());
                    startActivity(intent1);
                }
                break;
            case R.id.tv_huitie2:

                break;
            case R.id.iv_xin:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(YueTu_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(YueTu_Activity.this, Login_Activity.class));
                        MyToast.showToast(YueTu_Activity.this, "请先登录");
                    } else {

                        if (info.isUped()) {
                            MyToast.showToast(YueTu_Activity.this, "您已经赞过了");
                        } else {
                            inithttp_up(position_cheka);
                        }
                    }
                }
                break;
            case R.id.iv_shoucang:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(YueTu_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(YueTu_Activity.this, Login_Activity.class));
                        MyToast.showToast(YueTu_Activity.this, "请先登录");
                    } else {
                        if (info.isCollected()) {
                            inithttp_delshoucang(info.getId(), SPUtils.getString(YueTu_Activity.this, "token"), position_cheka);
                        } else {
                            inithttp_shoucang(info.getId(), SPUtils.getString(YueTu_Activity.this, "token"), position_cheka);
                        }
                    }
                }
                break;
            case R.id.iv_fenxiang:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);

                    OnekeyShare oks = new OnekeyShare();
                    // 关闭sso授权
                    oks.disableSSOWhenAuthorize();

                    // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
                    // oks.setNotification(R.drawable.ic_launcher,
                    // getString(R.string.app_name));
                    // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                    oks.setTitle(info.getTitle());
                    // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                    oks.setTitleUrl("http://www.anou.net.cn/web/share/cbshare.jsp");
                    // text是分享文本，所有平台都需要这个字段
                    if (info.getContent() != null && !TextUtils.isEmpty(info.getContent()) && !info.getContent().startsWith("http")) {
                        oks.setText(info.getContent());
                    } else {
                        int v1 = ((int) (Math.random() * 100));
                        Log.i("test", "int:   " + v1);
                        if (v1 % 3 == 2) {
                            oks.setText("更多实时路况尽在车吧");
                        } else if (v1 % 3 == 1) {
                            oks.setText("车吧，让出行更简单");
                        } else {
                            oks.setText("进入车吧，了解更多实时路况");
                        }
                    }
                    // 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                    if (info.getPictrue() != null && !TextUtils.isEmpty(info.getPictrue())) {
                        oks.setImageUrl(info.getPictrue().split(",")[0]);
                    } else {
                        oks.setImageUrl("http://inavy.cn:4888/icon.png");
                    }

                    // Log.i("test",list_tucao.get(position).getPictrue().split(",")[0]);
                    //    oks.setImageUrl("http://b248.photo.store.qq.com/psb?/2c280a19-7f4f-4e7d-a168-20d7fd2f70cc/pUL9tktoc3SHsXcU8hba08Tt5pRL2r.6SaZN4imCsls!/b/dCJ805PTDQAA&bo=ngL2AQAAAAABCUU!&rf=viewer_4" + "");
                    // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                    // oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
                    // url仅在微信（包括好友和朋友圈）中使用
                    oks.setUrl("http://www.anou.net.cn/web/share/cbshare.jsp");
                    // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                    oks.setComment("文章的评论");
                    // site是分享此内容的网站名称，仅在QQ空间使用
                    oks.setSite("分享内容的地址");
                    // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                    oks.setSiteUrl("http://www.anou.net.cn/web/share/cbshare.jsp");

                    // 启动分享GUI
                    oks.show(YueTu_Activity.this);
                }
                break;

            case R.id.tv_publish:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(YueTu_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(YueTu_Activity.this, Login_Activity.class));
                        MyToast.showToast(YueTu_Activity.this, "请先登录");
                    } else {
                        String trim = tv_huitie2.getText().toString().trim();

                        String s = readStream(getResources().openRawResource(R.raw.mingan));
                        String[] split = s.split(",");


                        for (int i = 0; i < split.length; i++) {
                            String x = split[i];  //x为敏感词汇
                            if (trim.contains(x)) {
                                trim = trim.replaceAll(x, getXing(x));
                            }
                        }


                        if (TextUtils.isEmpty(trim)) {

                        } else {
                            if (isson == null) {
                                inithttp_tjpl(trim, null);
                            } else {
                                inithttp_tjpl(trim, isson);
                                isson = null;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void inithttp_tjpl(String s, final Long pid) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        Map<String, Object> reqData = new HashMap<>();
        Map<String, Object> reqDataObj = new HashMap<>();
        reqDataObj.put("content", s);
        reqDataObj.put("did", info.getId());
        reqDataObj.put("pid", pid);
        reqData.put("obj", reqDataObj);
        reqData.put("token", SPUtils.getString(YueTu_Activity.this, "token"));
        String s1 = "";
        try {
            s1 = SerializeUtils.object2Json(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, s1);

//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Yuetu!Comment.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(YueTu_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode1 mycode = gson.fromJson(res, MyCode1.class);
                if (mycode.getCode() == 0) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            //  myAdapter.notifyDataSetChanged();

                            if (pid == null) {
                                if (position_cheka >= 0) {
                                    YueTu_Fragment.list_data.get(position_cheka).setCommentNum(Integer.parseInt(YueTu_Fragment.list_data.get(position_cheka).getCommentNum() + 1 + ""));
                                }
                            }

                            tv_huitie2.setText(null);
                            tv_huitie2.setHint("说几句...");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            //    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            imm.hideSoftInputFromWindow(tv_huitie2.getWindowToken(), 0);

                            page = 1;
                            inithttp_getpinglun_list(page);
                            setResult(55);
                        }
                    });
                }
                if (mycode.getCode() == 4) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showToast(YueTu_Activity.this, "请先登录");
                        }
                    });
                }

            }
        });
    }


    private void inithttp_getpinglun_list(final int page) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"page\":{\"page\":" + page + "},\"obj\":{\"did\":" + info.getId() + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Yuetu!LoadComment.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshAndLoadMoreView.setRefreshing(false);
                        mLoadMoreListView.onLoadComplete();
                        MyToast.showToast(YueTu_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_pinglun mycode = gson.fromJson(res, MyCode_pinglun.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {

                            if (page == 1) {
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        comment_list = mycode.getObj();
                                        if (adapter != null) {
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            initData();
                                        }
                                        if (comment_list.size() < 10) {
                                            mLoadMoreListView.setHaveMoreData(false);
                                            mRefreshAndLoadMoreView.setRefreshing(false);
                                            mLoadMoreListView.onLoadComplete();
                                        } else {
                                            mLoadMoreListView.setHaveMoreData(true);
                                            mRefreshAndLoadMoreView.setRefreshing(false);
                                            mLoadMoreListView.onLoadComplete();
                                        }

                                    }
                                }, 300);
                            } else {
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<MyCode_pinglun.ObjBean> obj = mycode.getObj();
                                        if (obj.size() == 0) {
                                            mLoadMoreListView.setHaveMoreData(false);
                                        } else {
                                            mLoadMoreListView.setHaveMoreData(true);
                                            comment_list.addAll(obj);
                                        }

                                        adapter.notifyDataSetChanged();
                                        //当加载完成之后设置此时不在刷新状态
                                        mRefreshAndLoadMoreView.setRefreshing(false);
                                        mLoadMoreListView.onLoadComplete();
                                    }
                                }, 300);
                            }

                        }
                    }
                });

            }
        });
    }


    private int size = 0;

    private void controlKeyboardLayout(final View root, final View needToScrollView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                YueTu_Activity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = YueTu_Activity.this.getWindow().getDecorView().getRootView().getHeight();
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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                String path = Environment.getExternalStorageDirectory().getPath();

                if ("xiazai".equals(s)) {
//保存图片到相册
                    String fi = MediaStore.Images.Media.insertImage(YueTu_Activity.this.getApplicationContext().getContentResolver(), bitmap, System.currentTimeMillis() + ".jpg", "有何不可");
                    rh.setxiazaienable();//设置按钮可点击
                    MyToast.showToast(YueTu_Activity.this, "下载成功 ");
                } else {
                    SavaImage(bitmap, path + "/Test");//下载图片到test文件夹
                    rh.setshoucangenable();
                    MyToast.showToast(YueTu_Activity.this, "收藏成功 ");
                }
                s = "";
            }
        }
    };

    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = GetImageInputStream(params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = 0x123;
            handler.sendMessage(message);
        }

    }

    public void SavaImage(Bitmap bitmap, String path) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(path + "/" + split[p].split("/")[split[p].split("/").length - 1]);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getXing(String f) {
        String a = "";
        for (int i = 0; i < f.length(); i++) {
            a = a + "*";
        }
        return a;
    }

    private String readStream(InputStream is) {
        // 资源流(GBK汉字码）变为串
        String res;
        try {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            res = new String(buf, "GBK");    //  必须将GBK码制转成Unicode
            is.close();
        } catch (Exception e) {
            res = "";
        }
        return res;            //  把资源文本文件送到String串中
    }

    static class ViewHolder {
        MyListView lv;
        CircleImageView headima;
        TextView nickname;
        TextView tv_zan;
        TextView content;
    }

    static class ViewHolder2 {
        TextView tv_content_son;
        TextView tv_nickname_son;
    }

}
