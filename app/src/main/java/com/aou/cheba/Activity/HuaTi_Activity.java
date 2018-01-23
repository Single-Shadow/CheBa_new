package com.aou.cheba.Activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.aou.cheba.bean.MyCode_pinglun;
import com.aou.cheba.bean.MyCode_uplist;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.utils.Utils;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class HuaTi_Activity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView finish;
    private Button fabiao;
    private EditText et;
    private String biaoti;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private MyListView lv;
    private int position_cheka;
    private ImageView iv_beijing;
    private TextView tv_nickname;
    private TextView tv_time;
    private CircleImageView iv_head;
    private TextView tv_titlfe;
    private TextView tv_conftent;
    private LinearLayout ll_love;
    private ImageView iv_1;
    private ImageView iv_2;
    private ImageView iv_3;
    private ImageView iv_4;
    private ImageView iv_5;
    private ImageView iv_6;
    private ImageView iv_7;
    private List<MyCode_uplist.ObjBean> obj = new ArrayList<>();
    private List<MyCode_pinglun.ObjBean> comment_list = new ArrayList<>();
    private int page = 1;
    private Long isson = null;
    private RelativeLayout rl_wai;
    private LinearLayout rl_xia;
    private EditText tv_huitie2;
    private String s;
    private ImageView iv_xin;
    private ImageView iv_shoucang;
    private ImageView iv_fenxiang;
    private MyCode_data.ObjBean ser = new MyCode_data.ObjBean();
    private ImageView iv_gender;
    private boolean isclickson;
    private LinearLayout ll_hide;
    private WebView web;
    private TextView btn_guanzhu;
    private TextView tv_dizhi;
    private LinearLayout ll_suiyi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huati_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ser = (MyCode_data.ObjBean) getIntent().getSerializableExtra("ser");
        position_cheka = getIntent().getIntExtra("position", 0);

        findViewById();
        getHead();
        inithttp_getpinglun_list(1);
    }

    private void initData() {
        adapter = new MyAdapter();
        mLoadMoreListView.setAdapter(adapter);

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
                    InputMethodManager imm = (InputMethodManager) HuaTi_Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
            case R.id.tv_zan:
                int tag = (int) v.getTag();
                if (TextUtils.isEmpty(SPUtils.getString(HuaTi_Activity.this, "token"))) {
                    finish();
                    startActivity(new Intent(HuaTi_Activity.this, Login_Activity.class));
                    MyToast.showToast(HuaTi_Activity.this, "请先登录");
                } else {
                    if (comment_list.get(tag).getUped() == null || !(comment_list.get(tag).getUped() == Integer.parseInt(SPUtils.getString(HuaTi_Activity.this, "uid")))) {
                        inithttp_pl_up(tag);
                    }
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
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_Activity.this, "请先登录");
                    } else {
                        if (ser.isFollowed()) {
                            MyToast.showToast(HuaTi_Activity.this, "已关注");
                        } else {
                            inithttp_guanzhu(ser.getUid(), SPUtils.getString(HuaTi_Activity.this, "token"), position_cheka);
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
                    Intent intent = new Intent(HuaTi_Activity.this, Other_Activity.class);
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
                    Intent intent1 = new Intent(HuaTi_Activity.this, Love_Activity.class);
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
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_Activity.this, "请先登录");
                    } else {

                        if (ser.isUped()) {
                            MyToast.showToast(HuaTi_Activity.this, "您已经赞过了");
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
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_Activity.this, "请先登录");
                    } else {
                        if (ser.isCollected()) {
                            inithttp_delshoucang(ser.getId(), SPUtils.getString(HuaTi_Activity.this, "token"), position_cheka);
                        } else {
                            inithttp_shoucang(ser.getId(), SPUtils.getString(HuaTi_Activity.this, "token"), position_cheka);
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
                        oks.setImageUrl(Data_Util.IMG+ser.getPictrue().split(",")[0]);
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
                    oks.show(HuaTi_Activity.this);
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
                    if (TextUtils.isEmpty(SPUtils.getString(HuaTi_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(HuaTi_Activity.this, Login_Activity.class));
                        MyToast.showToast(HuaTi_Activity.this, "请先登录");
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
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
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
                            if (me_mycodes != null&&me_mycodes.getObj()!=null) {
                                me_mycodes.getObj().setFollowCount(me_mycodes.getObj().getFollowCount() + 1);
                            }
                            SPUtils.put(HuaTi_Activity.this, "islogin", true);
                            MyToast.showToast(HuaTi_Activity.this, "关注成功");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_Activity.this, "请先登录");
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
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(HuaTi_Activity.this, "token") + "\",\"obj\":{\"id\":\"" + ser.getId() + "\"}}");

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
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
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
                            iv_xin.setImageResource(R.mipmap.x_hong);
                            setResult(55);

                            /*MyCode_uplist.ObjBean objBean = new MyCode_uplist.ObjBean();
                            objBean.setHeadImg(SPUtils.getString(HuaTi_Activity.this, "headImage"));
                            obj.add(0, objBean);
                            data_up(obj);*/

                            MyToast.showToast(HuaTi_Activity.this, "赞");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_Activity.this, "请先登录");
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
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
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
                            if (position >= 0) {
                                if (ser.getType() == 5) {
                                    new ZiXun_Fragment().list_data.get(position).setCollected(true);
                                } else if (ser.getType() == 6) {
                                    new CheYouQuan_Fragment().list_data.get(position).setCollected(true);
                                } else if (ser.getType() == 7) {
                                    new ShaiChe_Fragment().list_data.get(position).setCollected(true);
                                } else if (ser.getType() == 9) {
                                    new WenDa_Fragment().list_data.get(position).setCollected(true);
                                }
                            }

                            iv_shoucang.setImageResource(R.mipmap.sc_hong);
                            setResult(55);

                            MyToast.showToast(HuaTi_Activity.this, "收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_Activity.this, "请先登录");
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
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
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
                            iv_shoucang.setImageResource(R.mipmap.sc);
                            setResult(55);
                            MyToast.showToast(HuaTi_Activity.this, "取消收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }


    ArrayList<ImageView> list_iv = new ArrayList<>();
    boolean isup = false;

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

        if (ser.isUped()) {
            iv_xin.setImageResource(R.mipmap.x_hong);
        } else {
            iv_xin.setImageResource(R.mipmap.x);
        }

        if (ser.isCollected()) {
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

    private int size = 0;

    private void controlKeyboardLayout(final View root, final View needToScrollView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                HuaTi_Activity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = HuaTi_Activity.this.getWindow().getDecorView().getRootView().getHeight();
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

    private void getHead() {
        View inflate = View.inflate(HuaTi_Activity.this, R.layout.huati_head, null);
        mLoadMoreListView.addHeaderView(inflate);
        lv = (MyListView) inflate.findViewById(R.id.lv);
        iv_beijing = (ImageView) inflate.findViewById(R.id.iv_beijing);
        iv_gender = (ImageView) inflate.findViewById(R.id.iv_gender);
        iv_head = (CircleImageView) inflate.findViewById(R.id.iv_head);
        tv_nickname = (TextView) inflate.findViewById(R.id.tv_nickname);
        tv_time = (TextView) inflate.findViewById(R.id.tv_time);
        btn_guanzhu = (TextView) inflate.findViewById(R.id.btn_guanzhu);
        tv_dizhi = (TextView) inflate.findViewById(R.id.tv_dizhi);
        tv_conftent = (TextView) inflate.findViewById(R.id.tv_content);
        tv_titlfe = (TextView) inflate.findViewById(R.id.tv_title);
        ll_love = (LinearLayout) inflate.findViewById(R.id.ll_love);
        ll_hide = (LinearLayout) inflate.findViewById(R.id.ll_hide);
        web = (WebView) inflate.findViewById(R.id.web);

        iv_1 = (ImageView) inflate.findViewById(R.id.iv_1);
        iv_2 = (ImageView) inflate.findViewById(R.id.iv_2);
        iv_3 = (ImageView) inflate.findViewById(R.id.iv_3);
        iv_4 = (ImageView) inflate.findViewById(R.id.iv_4);
        iv_5 = (ImageView) inflate.findViewById(R.id.iv_5);
        iv_6 = (ImageView) inflate.findViewById(R.id.iv_6);
        iv_7 = (ImageView) inflate.findViewById(R.id.iv_7);
        list_iv.add(iv_1);
        list_iv.add(iv_2);
        list_iv.add(iv_3);
        list_iv.add(iv_4);
        list_iv.add(iv_5);
        list_iv.add(iv_6);
        list_iv.add(iv_7);

        ll_love.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        btn_guanzhu.setOnClickListener(this);
        initData();

//        inithttp_getup_list(1, ser.getId());

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
        if (SPUtils.getString(HuaTi_Activity.this, "uid").equals(ser.getUid() + "")) {
            btn_guanzhu.setVisibility(View.GONE);
        } else {
            if (ser.isFollowed() && !TextUtils.isEmpty(SPUtils.getString(HuaTi_Activity.this, "token"))) {
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

        Glide.with(HuaTi_Activity.this).load(Data_Util.IMG + ser.getHeadImg()).into(iv_head);
        tv_nickname.setText(ser.getNickname());

        tv_time.setText(TimeUtil.getDateTimeFromMillisecond2(ser.getAddtime()));

        ll_hide.setVisibility(View.GONE);
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

       /* if (ser.getContent() != null && !Utils.isContainChinese(ser.getContent().substring(0,33))) {
//            web.loadUrl(Data_Util.HTML+web_url);
//            web.setWebViewClient(new HelloWebViewClient());
        } else {
            ll_hide.setVisibility(View.VISIBLE);
            web.setVisibility(View.GONE);
            tv_conftent.setText(ser.getContent());
            tv_titlfe.setText(ser.getTitle());
            lv.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return list_image.size() - 1;
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
                    if (convertView == null) {
                        convertView = View.inflate(HuaTi_Activity.this, R.layout.item_layout, null);
                    }
                    ImageView image = (ImageView) convertView.findViewById(R.id.iv_image);

                    ViewGroup.LayoutParams vParams = image.getLayoutParams();
                    vParams.height = (int) (DisplayUtil.getMobileHeight(HuaTi_Activity.this) * 0.37);
                    image.setLayoutParams(vParams);

                    Glide.with(HuaTi_Activity.this).load(Data_Util.IMG+list_image.get(position + 1)).into(image);

                    return convertView;
                }
            });
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);

                        String[] split1 = ser.getPictrue().split(",");
                        String sp = "";
                        for (int i = 0; i < split1.length; i++) {
                            if (i != 0) {
                                if (i == 1) {
                                    sp = sp + split1[i];
                                } else {
                                    sp = sp + "," + split1[i];
                                }
                            }
                        }

                        Intent i = new Intent(HuaTi_Activity.this, LiuLan_Activity.class);
                        i.putExtra("split", sp);
                        i.putExtra("item", position);
                        startActivity(i);
                    }
                }
            });
        }*/
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
                    if (src4!=null){
                        for (String style : src4) {
                            s = s.replace("<div"+style+">", "");  //统一去掉div 删掉样式
                        }
                    }

                    s=s.replace("</div>","");

                    String body = Utils.substringBetween(s, "<body>", "</body>");
                    String body1=body.replace("width","");
                    body1=body1.replace("height","");
                    body1=body1.replace("margin","");
                    body1=body1.replace("padding","");
                    s=s.replace(body,body1);

//                    MyDebug.showLargeLog(s);
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

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // view.loadUrl(url);
            return true;
        }
    }

    private void inithttp_pl_up(final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(HuaTi_Activity.this, "token") + "\",\"obj\":{\"id\":" + comment_list.get(position).getId() + "}}");

        String urls = "";
        switch (ser.getType()) {
            case 5:
                urls = "/Carbar/Server!CommentLike.action";
                break;
            case 6:
                urls = "/Carbar/Server!CommentLike.action";
                break;
            case 7:
                urls = "/Carbar/ShaiChe!CommentLike.action";
                break;
            case 9:
                urls = "/Carbar/WenDa!CommentLike.action";
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
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
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
                            comment_list.get(position).setUped(Integer.parseInt(SPUtils.getString(HuaTi_Activity.this, "uid")));
                            comment_list.get(position).setUpCount(comment_list.get(position).getUpCount() + 1);
                            adapter.notifyDataSetChanged();
                            MyToast.showToast(HuaTi_Activity.this, "赞");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(HuaTi_Activity.this, "token", "");
                            MyToast.showToast(HuaTi_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }


    private void inithttp_getpinglun_list(final int page) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"page\":{\"page\":" + page + "},\"obj\":{\"did\":" + ser.getId() + "},\"token\":\"" + SPUtils.getString(HuaTi_Activity.this, "token") + "\"}");
        Utils.i("ID评论：" + ser.getId() + "   " + page);

        String urls = "";
        switch (ser.getType()) {
            case 5:
                urls = "/Carbar/Server!LoadComment.action";
                break;
            case 6:
                urls = "/Carbar/Traffic!LoadComment.action";
                break;
            case 7:
                urls = "/Carbar/ShaiChe!LoadComment.action";
                break;
            case 9:
                urls = "/Carbar/WenDa!LoadComment.action";
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
                        mRefreshAndLoadMoreView.setRefreshing(false);
                        mLoadMoreListView.onLoadComplete();
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Utils.i("评论列表：" + res);
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

    private void inithttp_getup_list(final int page, long l) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"page\":{\"page\":" + page + "},\"obj\":{\"id\":" + l + "}}");

        String urls = "";
        switch (ser.getType()) {
            case 5:
                urls = "/Carbar/Server!LoadUp.action";
                break;
            case 6:
                urls = "/Carbar/Traffic!LoadUp.action";
                break;
            case 7:
                urls = "/Carbar/ShaiChe!LoadUp.action";
                break;
            case 9:
                urls = "/Carbar/WenDa!LoadUp.action";
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
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Utils.i(res);
                Gson gson = new Gson();
                final MyCode_uplist mycode = gson.fromJson(res, MyCode_uplist.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {

                            obj = mycode.getObj();
                            //初始化点赞列表
                            data_up(obj);
                        }
                    }
                });
            }
        });
    }

    private void data_up(List<MyCode_uplist.ObjBean> obj) {
        if (obj == null || obj.size() == 0) {
            ll_love.setVisibility(View.GONE);
        } else {
            ll_love.setVisibility(View.VISIBLE);

            if (obj.size() >= 6) {
                for (int i = 0; i < 7; i++) {
                    list_iv.get(i).setVisibility(View.VISIBLE);
                    if (i == 6) {
                        list_iv.get(i).setImageResource(R.mipmap.shenlue);
                    } else {
                        try {
                            Glide.with(HuaTi_Activity.this).load(Data_Util.IMG + obj.get(i).getHeadImg()).into(list_iv.get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                for (int i = 0; i < 7; i++) {
                    if (i < obj.size()) {
                        list_iv.get(i).setVisibility(View.VISIBLE);

                        try {
                            Glide.with(HuaTi_Activity.this).load(Data_Util.IMG + obj.get(i).getHeadImg()).into(list_iv.get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (i == obj.size()) {
                        list_iv.get(i).setVisibility(View.VISIBLE);
                        list_iv.get(i).setImageResource(R.mipmap.shenlue);
                    } else {
                        list_iv.get(i).setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
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
                convertView = View.inflate(HuaTi_Activity.this, R.layout.pinglun_item, null);
                viewHolder = new ViewHolder();
                viewHolder.headima = (CircleImageView) convertView.findViewById(R.id.iv_headima);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
                viewHolder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.lv = (MyListView) convertView.findViewById(R.id.lv_pinglun);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            Glide.with(HuaTi_Activity.this).load(Data_Util.IMG + comment_list.get(position).getHeadImg()).into(viewHolder.headima);
            viewHolder.nickname.setText(comment_list.get(position).getNickname());

            Log.i("test", comment_list.get(position).getUped() + "");
            if (comment_list.get(position).getUped() != null && (comment_list.get(position).getUped() == Integer.parseInt(SPUtils.getString(HuaTi_Activity.this, "uid")))) {
                viewHolder.tv_zan.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(HuaTi_Activity.this, R.mipmap.zan_hong), null, null, null);
            } else {
                viewHolder.tv_zan.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(HuaTi_Activity.this, R.mipmap.zan2), null, null, null);
            }

            if (comment_list.get(position).getUpCount() == 0) {
                viewHolder.tv_zan.setText("");
            } else {
                viewHolder.tv_zan.setText(comment_list.get(position).getUpCount() + "");
            }

            viewHolder.content.setText(comment_list.get(position).getContent());
            viewHolder.headima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HuaTi_Activity.this, Other_Activity.class);
                    intent.putExtra("uid", comment_list.get(position).getUid());
                    startActivity(intent);
                }
            });

            viewHolder.tv_zan.setTag(position);
            viewHolder.tv_zan.setOnClickListener(HuaTi_Activity.this);

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
                            convertView = View.inflate(HuaTi_Activity.this, R.layout.pinglun_item_son, null);
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

    private void inithttp_tjpl(String s, final Long pid) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        Map<String, Object> reqData = new HashMap<>();
        Map<String, Object> reqDataObj = new HashMap<>();
        reqDataObj.put("content", s);
        reqDataObj.put("did", ser.getId());
        reqDataObj.put("pid", pid);
        reqData.put("obj", reqDataObj);
        reqData.put("token", SPUtils.getString(HuaTi_Activity.this, "token"));
        String s1 = "";
        try {
            s1 = SerializeUtils.object2Json(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, s1);

        String urls = "";
        switch (ser.getType()) {
            case 5:
                urls = "/Carbar/Server!Comment.action";
                break;
            case 6:
                urls = "/Carbar/Traffic!Comment.action";
                break;
            case 7:
                urls = "/Carbar/ShaiChe!Comment.action";
                break;
            case 9:
                urls = "/Carbar/WenDa!Comment.action";
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
                        MyToast.showToast(HuaTi_Activity.this, "连接服务器失败");
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
                                    if (ser.getType() == 5) {
                                        new ZiXun_Fragment().list_data.get(position_cheka).setCommentNum(new ZiXun_Fragment().list_data.get(position_cheka).getCommentNum() + 1);
                                    } else if (ser.getType() == 6) {
                                        new CheYouQuan_Fragment().list_data.get(position_cheka).setCommentNum(new CheYouQuan_Fragment().list_data.get(position_cheka).getCommentNum() + 1);
                                    } else if (ser.getType() == 7) {
                                        new ShaiChe_Fragment().list_data.get(position_cheka).setCommentNum(new ShaiChe_Fragment().list_data.get(position_cheka).getCommentNum() + 1);
                                    } else if (ser.getType() == 9) {
                                        new WenDa_Fragment().list_data.get(position_cheka).setCommentNum(new WenDa_Fragment().list_data.get(position_cheka).getCommentNum() + 1);
                                    }
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
                            MyToast.showToast(HuaTi_Activity.this, "请先登录");
                        }
                    });
                }

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev) && isShouldHideInput(fabiao, ev)) {
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
        if (v != null) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web.destroy();
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
