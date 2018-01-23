package com.aou.cheba.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.Fragment_new.CheYouQuan_Fragment;
import com.aou.cheba.Fragment_new.ShaiChe_Fragment;
import com.aou.cheba.Fragment_new.WenDa_Fragment;
import com.aou.cheba.Fragment_new.ZiXun_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.bean.MyCode_uplist;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.MyDebug;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.utils.Utils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import net.qiujuer.genius.blur.StackBlur;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Created by Administrator on 2016/11/29.
 */
public class HuaTi_WenDa_Activity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView finish;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private MyListView lv;
    private int position_cheka;
    private ImageView iv_beijing;
    private TextView tv_nickname;
    private TextView tv_time;
    private CircleImageView iv_head;
    private List<MyCode_uplist.ObjBean> obj = new ArrayList<>();
    private List<MyCode_data.ObjBean> comment_list = new ArrayList<>();
    private MyCode_data.ObjBean ser = new MyCode_data.ObjBean();
    private ImageView iv_gender;
    private WebView web;
    private TextView btn_guanzhu;
    private TextView tv_dizhi;
    private TextView tv_huida;
    private TextView tv_guanzhu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huati_wenda_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ser = (MyCode_data.ObjBean) getIntent().getSerializableExtra("ser");
        position_cheka = getIntent().getIntExtra("position", 0);

        findViewById();
        getHead();
        inithttp_DataToById(ser.getId(), Long.MAX_VALUE);
    }

    private void initData() {
        adapter = new MyAdapter();
        mLoadMoreListView.setAdapter(adapter);

        mRefreshAndLoadMoreView.setLoadMoreListView(mLoadMoreListView);
        mLoadMoreListView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreView);

        mRefreshAndLoadMoreView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                inithttp_DataToById(ser.getId(), Long.MAX_VALUE);
            }
        });
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                inithttp_DataToById(ser.getId(), comment_list.get(comment_list.size() - 1).getId());
            }
        });

        mLoadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    MyCode_data.ObjBean objBean = comment_list.get(position - 1);
                    Intent intent = new Intent(HuaTi_WenDa_Activity.this, HuaTi_Activity.class);
                    intent.putExtra("position", position - 1);
                    intent.putExtra("ser", objBean);
                    startActivityForResult(intent, 12);
                }
            }
        });
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
            case R.id.iv_head:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent = new Intent(HuaTi_WenDa_Activity.this, Other_Activity.class);
                    intent.putExtra("uid", ser.getUid());
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
                    Intent intent1 = new Intent(HuaTi_WenDa_Activity.this, Love_Activity.class);
                    intent1.putExtra("id", ser.getId());
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
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_WenDa_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_WenDa_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
                    } else {

                        if (ser.isUped()) {
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "您已经赞过了");
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
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_WenDa_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_WenDa_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
                    } else {
                        if (ser.isCollected()) {
                            inithttp_delshoucang(ser.getId(), SPUtils.getString(HuaTi_WenDa_Activity.this, "token"), position_cheka);
                        } else {
//                            inithttp_shoucang(ser.getId(), SPUtils.getString(HuaTi_WenDa_Activity.this, "token"), position_cheka);
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
                    oks.setTitle(ser.getTitle());
                    // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                    oks.setTitleUrl("http://www.anou.net.cn/web/share/cbshare.jsp");
                    // text是分享文本，所有平台都需要这个字段
                    if (ser.getContent() != null && ser.getContent().length() > 33) {
                        oks.setText(ser.getContent().substring(33, ser.getContent().length()));
                    } else {
                        oks.setText("");
                    }
                    // 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                    if (ser.getPictrue() != null && !TextUtils.isEmpty(ser.getPictrue())) {
                        oks.setImageUrl(Data_Util.IMG + ser.getPictrue().split(",")[0]);
                    } else {
                        oks.setImageUrl("http://www.szcheba.com/CarbarFileServer/download/41fd795ddb0a38421f0269371c516b08");
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
                    oks.show(HuaTi_WenDa_Activity.this);
                }
                break;

            case R.id.tv_huida:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_WenDa_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_WenDa_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
                    } else {

                        Intent intent = new Intent(HuaTi_WenDa_Activity.this, SelectPictureActivity.class);
                        intent.putExtra("isfrist", true);
                        intent.putExtra("intent_max_num", 9);
                        intent.putExtra("type", 9);
                        intent.putExtra("byId", ser.getId());
                        startActivity(intent);
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "回答问题");
                    }
                }
                break;
            case R.id.tv_guanzhu:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_WenDa_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_WenDa_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
                    } else {
                        if (ser.isCollected()) {
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "已关注问题");
                        } else {
                            inithttp_shoucang(ser.getId(), SPUtils.getString(HuaTi_WenDa_Activity.this, "token"));
                        }
                    }
                }
                break;

        }
    }

    private void inithttp_guanzhu(final long uid, String token, final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\": {\"uid\": " + uid + "},\"token\": \"" + token + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!FollowUser.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "连接服务器失败");
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
                            btn_guanzhu.setVisibility(View.VISIBLE);
                            btn_guanzhu.setText("已关注");
                            btn_guanzhu.setTextColor(Color.parseColor("#d9d9d9"));
                            btn_guanzhu.setBackgroundResource(R.drawable.custom_hui);
                            btn_guanzhu.setEnabled(false);
                            if (position >= 0) {
                                if (ser.getType() == 5) {
                                    new ZiXun_Fragment().list_data.get(position).setFollowed(true);
                                } else if (ser.getType() == 6) {
                                    new CheYouQuan_Fragment().list_data.get(position).setFollowed(true);
                                } else if (ser.getType() == 7) {
                                    new ShaiChe_Fragment().list_data.get(position).setFollowed(true);
                                } else if (ser.getType() == 9) {
                                    new WenDa_Fragment().list_data.get(position).setFollowed(true);
                                }
                            }

                            MyCodeInfo me_mycodes = new Me_Fragment().mycodes;
                            if (me_mycodes != null) {
                                me_mycodes.getObj().setFollowCount(me_mycodes.getObj().getFollowCount() + 1);
                            }
                            SPUtils.put(HuaTi_WenDa_Activity.this, "islogin", true);
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "关注成功");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_WenDa_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_up(final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(HuaTi_WenDa_Activity.this, "token") + "\",\"obj\":{\"id\":\"" + ser.getId() + "\"}}");

        String urls = "";
        switch (ser.getType()) {
            case 5:
                urls = "/Carbar/Server!Like.action";
                break;
            case 6:
                urls = "/Carbar/Traffic!Like.action";
                break;
            case 7:
                urls = "/Carbar/ShaiChe!Like.action";
                break;
            case 9:
                urls = "/Carbar/WenDa!Like.action";
                break;
        }

//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + urls)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "连接服务器失败");
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
                            if (position >= 0) {
                                if (ser.getType() == 5) {
                                    new ZiXun_Fragment().list_data.get(position).setUpCount(new ZiXun_Fragment().list_data.get(position).getUpCount() + 1);
                                    new ZiXun_Fragment().list_data.get(position).setUped(true);
                                } else if (ser.getType() == 6) {
                                    new CheYouQuan_Fragment().list_data.get(position).setUpCount(new CheYouQuan_Fragment().list_data.get(position).getUpCount() + 1);
                                    new CheYouQuan_Fragment().list_data.get(position).setUped(true);
                                } else if (ser.getType() == 7) {
                                    new ShaiChe_Fragment().list_data.get(position).setUpCount(new ShaiChe_Fragment().list_data.get(position).getUpCount() + 1);
                                    new ShaiChe_Fragment().list_data.get(position).setUped(true);
                                } else if (ser.getType() == 9) {
                                    new WenDa_Fragment().list_data.get(position).setUpCount(new WenDa_Fragment().list_data.get(position).getUpCount() + 1);
                                    new WenDa_Fragment().list_data.get(position).setUped(true);
                                }
                            }

                            ser.setUped(true);
//                            iv_xin.setImageResource(R.mipmap.x_hong);
                            setResult(55);

                            MyCode_uplist.ObjBean objBean = new MyCode_uplist.ObjBean();
                            objBean.setHeadImg(SPUtils.getString(HuaTi_WenDa_Activity.this, "headImage"));
                            obj.add(0, objBean);

                            MyToast.showToast(HuaTi_WenDa_Activity.this, "赞");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_WenDa_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_shoucang(long did, String token) {
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
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "连接服务器失败");
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
                            ser.setCollected(true);

                            tv_guanzhu.setText("√ 问答已关注");
                            tv_guanzhu.setEnabled(false);
                            tv_guanzhu.setTextColor(Color.parseColor("#969696"));
                            setResult(55);

                            MyToast.showToast(HuaTi_WenDa_Activity.this, "问答已关注");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_WenDa_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
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
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "连接服务器失败");
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
                            ser.setCollected(false);
                            if (position >= 0) {
                                if (ser.getType() == 5) {
                                    new ZiXun_Fragment().list_data.get(position).setCollected(false);
                                } else if (ser.getType() == 6) {
                                    new CheYouQuan_Fragment().list_data.get(position).setCollected(false);
                                } else if (ser.getType() == 7) {
                                    new ShaiChe_Fragment().list_data.get(position).setCollected(false);
                                } else if (ser.getType() == 9) {
                                    new WenDa_Fragment().list_data.get(position).setCollected(false);
                                }
                            }
//                            iv_shoucang.setImageResource(R.mipmap.sc);
                            setResult(55);
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "取消收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_WenDa_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_WenDa_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void findViewById() {
        finish = (ImageView) findViewById(R.id.iv_finish);
        tv_huida = ((TextView) findViewById(R.id.tv_huida));
        tv_guanzhu = ((TextView) findViewById(R.id.tv_guanzhu));

        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);

        if (ser.isCollected()) {
            tv_guanzhu.setText("√ 问答已关注");
            tv_guanzhu.setEnabled(false);
            tv_guanzhu.setTextColor(Color.parseColor("#969696"));
        } else {
            tv_guanzhu.setText("+ 关注问答");
            tv_guanzhu.setEnabled(true);
            tv_guanzhu.setTextColor(Color.parseColor("#333333"));
        }
        finish.setOnClickListener(this);
        tv_huida.setOnClickListener(this);
        tv_guanzhu.setOnClickListener(this);

    }

    private void getHead() {
        View inflate = View.inflate(HuaTi_WenDa_Activity.this, R.layout.huati_wenda_head, null);
        mLoadMoreListView.addHeaderView(inflate);
        lv = (MyListView) inflate.findViewById(R.id.lv);
        iv_beijing = (ImageView) inflate.findViewById(R.id.iv_beijing);
        iv_gender = (ImageView) inflate.findViewById(R.id.iv_gender);
        iv_head = (CircleImageView) inflate.findViewById(R.id.iv_head);
        tv_nickname = (TextView) inflate.findViewById(R.id.tv_nickname);
        tv_time = (TextView) inflate.findViewById(R.id.tv_time);
        btn_guanzhu = (TextView) inflate.findViewById(R.id.btn_guanzhu);
        tv_dizhi = (TextView) inflate.findViewById(R.id.tv_dizhi);

        web = (WebView) inflate.findViewById(R.id.web);

        iv_head.setOnClickListener(this);
        btn_guanzhu.setOnClickListener(this);
        initData();

        if (ser.getPictrue() != null && ser.getPictrue().length() != 0) {
            String[] split = ser.getPictrue().split(",");
            final List<String> list_image = Arrays.asList(split);


//        Glide.with(HuaTi_Activity.this).load(Data_Util.IMG+list_image.get(0)).into(iv_beijing);
            if (list_image == null || list_image.size() == 0) {
            } else {
                mohu(Data_Util.IMG + list_image.get(0), iv_beijing);
            }
        }

        if (ser.getGender() == 1) {
            iv_gender.setImageResource(R.mipmap.nan);
        } else {
            iv_gender.setImageResource(R.mipmap.nv);
        }

        if (TextUtils.isEmpty(ser.getLocation())) {
            tv_dizhi.setVisibility(View.INVISIBLE);
        } else {
            tv_dizhi.setVisibility(View.VISIBLE);
            tv_dizhi.setText(ser.getLocation());
        }
        if (SPUtils.getString(HuaTi_WenDa_Activity.this, "uid").equals(ser.getUid() + "")) {
            btn_guanzhu.setVisibility(View.GONE);
        } else {
            if (ser.isFollowed() && !TextUtils.isEmpty(SPUtils.getString(HuaTi_WenDa_Activity.this, "token"))) {
                btn_guanzhu.setVisibility(View.VISIBLE);
                btn_guanzhu.setText("已关注");
                btn_guanzhu.setTextColor(Color.parseColor("#d9d9d9"));
                btn_guanzhu.setBackgroundResource(R.drawable.custom_hui);
                btn_guanzhu.setEnabled(false);
            } else {
                btn_guanzhu.setEnabled(true);
                btn_guanzhu.setVisibility(View.VISIBLE);
                btn_guanzhu.setText("+ 关注");
                btn_guanzhu.setTextColor(Color.parseColor("#ffffff"));
                btn_guanzhu.setBackgroundResource(R.drawable.custom_lv);
            }
        }

        Glide.with(HuaTi_WenDa_Activity.this).load(Data_Util.IMG + ser.getHeadImg()).into(iv_head);
        tv_nickname.setText(ser.getNickname());

        tv_time.setText(TimeUtil.getDateTimeFromMillisecond2(ser.getAddtime()));

        web.setVisibility(View.VISIBLE);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBlockNetworkImage(false);
        web.setInitialScale(100);
        web.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
        String web_url;
        if (ser.getContent().contains(",")) {
            web_url = ser.getContent().split(",")[0];
        } else {
            web_url = ser.getContent();
        }
        loadWebView(Data_Util.HTML + web_url, web);
    }

    public void loadWebView(final String url2, final WebView web) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                connection = Jsoup.connect(url2);
                System.out.println("网页：");
                try {
                    Document document = connection.get();
                    String s = document.html();
                    System.out.println("网页：" + s);

                    String[] src = Utils.substringsBetween(s, "src=\"", "\"");
                    Set<String> src_set = new HashSet<>();
                    //去重

                    if (src != null && src.length != 0) {
                        Collections.addAll(src_set, src);
                        for (String src_str : src_set) {
                            s = s.replace(src_str, Data_Util.IMG + src_str);
                        }
                    }

                    String[] src2 = Utils.substringsBetween(s, "src=\'", "\'");
                    if (src2 != null && src2.length != 0) {
                        src_set = new HashSet<>();
                        Collections.addAll(src_set, src2);
                        for (String src_str : src_set) {
                            s = s.replace(src_str, Data_Util.IMG + src_str);
                        }
                    }

                    String s1 = Utils.substringBetween(s, "img{", "}");
                    if (s1 == null || s1.length() == 0) {
                        s = s.replace("img{}", "img{width: 100%;max-width: 100%;height: auto;margin: 10px auto;display: block;}");
                    } else {
                        s = s.replace(s1, "width: 100%;max-width: 100%;height: auto; margin: 10px auto ;display: block;");
                    }


                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int widthPixels = metrics.widthPixels;


                    String[] src3 = Utils.substringsBetween(s, "font-size:", "}");
                    String pix = "22px";
                    if (widthPixels <= 540) {
                        pix = "13px";
                    } else if (widthPixels > 540 && widthPixels < 640) {
                        pix = "14px";
                    } else if (widthPixels >= 640 && widthPixels < 720) {
                        pix = "15px";
                    } else if (widthPixels >= 720 && widthPixels < 750) {
                        pix = "16px";
                    } else if (widthPixels >= 750 && widthPixels < 800) {
                        pix = "17px";
                    } else if (widthPixels >= 800 && widthPixels < 960) {
                        pix = "18px";
                    } else if (widthPixels >= 960 && widthPixels < 1080) {
                        pix = "19px";
                    } else {
                        pix = "22px";
                    }

                    Utils.i("手机像素宽度：" + widthPixels);

                    for (String sp : src3) {
                        s = s.replace(sp, pix);  //字体大小统一调整
                    }

                    String[] src4 = Utils.substringsBetween(s, "<div", ">");
                    if (src4 != null) {
                        for (String style : src4) {
                            s = s.replace("<div" + style + ">", "");  //统一去掉div 删掉样式
                        }
                    }

                    s = s.replace("</div>", "");

                    String body = Utils.substringBetween(s, "<body>", "</body>");
                    String body1 = body.replace("width", "");
                    body1 = body1.replace("height", "");
                    body1 = body1.replace("margin", "");
                    body1 = body1.replace("padding", "");
                    s = s.replace(body, body1);

                    MyDebug.showLargeLog(s);
                    final String finalS = s;
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            web.loadDataWithBaseURL(null, finalS, "text/html", "utf-8", null);
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void inithttp_DataToById(final Long byId, final Long id) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"byId\":" + byId + ",\"id\":" + id + "},\"token\":\"" + SPUtils.getString(HuaTi_WenDa_Activity.this, "token") + "\"}");

//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/WenDa!LoadDatatobyId.action")
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
                        MyToast.showToast(HuaTi_WenDa_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();
                Utils.i(res);
                Gson gson = new Gson();
                final MyCode_data mycode = gson.fromJson(res, MyCode_data.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {

                            if (id == Long.MAX_VALUE) {
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
                                        List<MyCode_data.ObjBean> obj = mycode.getObj();
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

    Handler h = new Handler();

    private void mohu(final String s, final ImageView iv) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //     final Bitmap bitmap_s = BitmapFactory.decodeFile(s);
                Bitmap bitmap_s = getBitMBitmap(s);
                final Bitmap newBitmap;
                try {
                    if (bitmap_s != null) {
                        newBitmap = StackBlur.blurNatively(bitmap_s, 20, false);
                    } else {
                        return;
                    }
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(newBitmap);
                        }
                    });
                } catch (Exception e) {
                }
            }
        }).start();
    }

    //高斯模糊  保留
    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
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
                convertView = View.inflate(HuaTi_WenDa_Activity.this, R.layout.wenda_answer_item, null);
                viewHolder = new ViewHolder();
                viewHolder.headima = (CircleImageView) convertView.findViewById(R.id.headima);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname);
                viewHolder.tv_summary = (TextView) convertView.findViewById(R.id.tv_summary);
                viewHolder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
                viewHolder.iv_zan = (ImageView) convertView.findViewById(R.id.iv_zan);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            ViewGroup.LayoutParams vParams = viewHolder.image.getLayoutParams();
            vParams.height = (int) (DisplayUtil.getMobileHeight(HuaTi_WenDa_Activity.this) * 0.20);
            vParams.width = (int) (DisplayUtil.getMobileWidth(HuaTi_WenDa_Activity.this) * 0.55);
            viewHolder.image.setLayoutParams(vParams);

            Glide.with(HuaTi_WenDa_Activity.this).load(Data_Util.IMG + comment_list.get(position).getHeadImg()).into(viewHolder.headima);
            viewHolder.nickname.setText(comment_list.get(position).getNickname());

            viewHolder.tv_zan.setText(comment_list.get(position).getUpCount() + "");

            String[] split = comment_list.get(position).getContent().split(",");
            if (split != null && split.length > 1) {
                String cot = split[1] + "<font color='#2B90D6'>...查看全文</font>";
                viewHolder.tv_summary.setText(Html.fromHtml(cot));
            }

            if (comment_list.get(position).getPictrue() != null) {
                String[] split_pic = comment_list.get(position).getPictrue().split(",");
                if (split_pic != null) {
                    viewHolder.image.setVisibility(View.VISIBLE);
                    Glide.with(HuaTi_WenDa_Activity.this).load(Data_Util.IMG + split_pic[0]).into(viewHolder.image);
                } else {
                    viewHolder.image.setVisibility(View.GONE);
                }
            } else {
                viewHolder.image.setVisibility(View.GONE);
            }


            viewHolder.headima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HuaTi_WenDa_Activity.this, Other_Activity.class);
                    intent.putExtra("uid", comment_list.get(position).getUid());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web.destroy();
    }

    static class ViewHolder {
        CircleImageView headima;
        TextView nickname;
        TextView tv_zan;
        TextView tv_summary;
        ImageView image;
        ImageView iv_zan;
    }
}
