package com.aou.cheba.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment.MeiRong_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_MeiRong_pinglun;
import com.aou.cheba.bean.MyCode_meirong;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RatingBar;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class MeiRong_xiangqiang_Activity extends SwipeBackActivity implements View.OnClickListener {
    // private List<MyCode_meirong.ObjBean> list_m = new ArrayList<>();
    private int posi;
    private int size;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private ImageView finish;
    private ImageView iv_fengxiang;
    private ImageView iv_shoucang;
    private TextView ping;
    private TextView zan;
    private TextView xiangxidizhi;
    private TextView huanjing;
    private TextView fuwu;
    private TextView jishu;
    private TextView type;
    private TextView dizhi;
    private TextView pingNum;
    private RatingBar xin;
    private TextView name;
    private MyAdapter adapter;
    private List<MyCode_MeiRong_pinglun.ObjBean> comment_list = new ArrayList<>();
    private List<String> strings = new ArrayList<>();
    private List<String> string_pic = new ArrayList<>();
    private HorizontalScrollView ho;
    public static MyCode_meirong.ObjBean list_m = new MyCode_meirong.ObjBean();
    private ImageView iv_phone;
    private TextView image_shu;
    private ImageView iv_1;
    private LinearLayout ll_iv;
    private ImageView iv_2;
    private ImageView iv_3;
    private ImageView iv_4;
    private RelativeLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meirong_xiangqiang);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        list_m = (MyCode_meirong.ObjBean) (getIntent().getSerializableExtra("objBean"));
        posi = getIntent().getIntExtra("position", 0);

        findViewById();
        getHead();
        inithttp_getpinglun_list(1);
    }

    private void getHead() {
        View inflate = View.inflate(MeiRong_xiangqiang_Activity.this, R.layout.meirong_head, null);
        mLoadMoreListView.addHeaderView(inflate);
        name = (TextView) inflate.findViewById(R.id.name);
        xin = (RatingBar) inflate.findViewById(R.id.xin);
        pingNum = (TextView) inflate.findViewById(R.id.pingNum);
        dizhi = (TextView) inflate.findViewById(R.id.dizhi);
        type = (TextView) inflate.findViewById(R.id.type);
        jishu = (TextView) inflate.findViewById(R.id.jishu);
        fuwu = (TextView) inflate.findViewById(R.id.fuwu);
        huanjing = (TextView) inflate.findViewById(R.id.huanjing);
        xiangxidizhi = (TextView) inflate.findViewById(R.id.xiangxidizhi);
        iv_phone = (ImageView) inflate.findViewById(R.id.iv_phone);

        image_shu = (TextView) inflate.findViewById(R.id.image_shu);
        ll_iv = (LinearLayout) inflate.findViewById(R.id.ll_iv);
        iv_1 = (ImageView) inflate.findViewById(R.id.iv_1);
        iv_2 = (ImageView) inflate.findViewById(R.id.iv_2);
        iv_3 = (ImageView) inflate.findViewById(R.id.iv_3);
        iv_4 = (ImageView) inflate.findViewById(R.id.iv_4);

        String[] split = list_m.getPictrue().split(",");
        string_pic = Arrays.asList(split);
        // ho = (HorizontalScrollView) inflate.findViewById(R.id.ho);
        if (string_pic.size() != 0) {
            ll_iv.setVisibility(View.VISIBLE);
            if (string_pic.size() < 5) {
                image_shu.setVisibility(View.GONE);
                switch (string_pic.size()) {
                    case 1:
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(0)).into(iv_1);
                        iv_1.setOnClickListener(new MyOnClicklistenr2());
                        Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(iv_2);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(iv_3);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(iv_4);
                        break;
                    case 2:
                        iv_1.setOnClickListener(new MyOnClicklistenr2());
                        iv_2.setOnClickListener(new MyOnClicklistenr2());
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(0)).into(iv_1);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(1)).into(iv_2);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(iv_3);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(iv_4);
                        break;
                    case 3:
                        iv_1.setOnClickListener(new MyOnClicklistenr2());
                        iv_2.setOnClickListener(new MyOnClicklistenr2());
                        iv_3.setOnClickListener(new MyOnClicklistenr2());
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(0)).into(iv_1);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(1)).into(iv_2);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(2)).into(iv_3);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(iv_4);
                        break;
                    case 4:
                        iv_1.setOnClickListener(new MyOnClicklistenr2());
                        iv_2.setOnClickListener(new MyOnClicklistenr2());
                        iv_3.setOnClickListener(new MyOnClicklistenr2());
                        iv_4.setOnClickListener(new MyOnClicklistenr2());

                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(0)).into(iv_1);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(1)).into(iv_2);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(2)).into(iv_3);
                        Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(3)).into(iv_4);
                        break;
                }

            } else {
                image_shu.setVisibility(View.VISIBLE);
                Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(0)).into(iv_1);
                Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(1)).into(iv_2);
                Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(2)).into(iv_3);
                Glide.with(MeiRong_xiangqiang_Activity.this).load(string_pic.get(3)).into(iv_4);
                iv_1.setOnClickListener(new MyOnClicklistenr2());
                iv_2.setOnClickListener(new MyOnClicklistenr2());
                iv_3.setOnClickListener(new MyOnClicklistenr2());
                iv_4.setOnClickListener(new MyOnClicklistenr2());
                image_shu.setText(string_pic.size() + " 图");
            }
        } else {
            ll_iv.setVisibility(View.GONE);
        }

        //    setGridView();

        xin.setStar(list_m.getScore());//设置显示的星星个数
        xin.setStepSize(RatingBar.StepSize.Full);//设置每次点击增加一颗星还是半颗星


        name.setText(list_m.getStore_name());
        dizhi.setText(list_m.getCity());
        type.setText(list_m.getStore_type());
        xiangxidizhi.setText(list_m.getStore_addr());
        //  xin.setText(list_m.get(posi).getCommentNum()+"条");
        jishu.setText("技术:" + list_m.getJs());
        fuwu.setText(" 服务:" + list_m.getFw());
        huanjing.setText(" 环境:" + list_m.getHj());
        pingNum.setText(list_m.getCommentNum() + "条");

        iv_phone.setOnClickListener(this);

        initData();
    }

    private int page = 1;

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

    }

    private boolean isenter = false;

    private void findViewById() {
        finish = ((ImageView) findViewById(R.id.iv_finish));
        iv_fengxiang = ((ImageView) findViewById(R.id.iv_fengxiang));
        iv_shoucang = ((ImageView) findViewById(R.id.iv_shoucang));
        zan = ((TextView) findViewById(R.id.zan));
        ping = ((TextView) findViewById(R.id.ping));
        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(MeiRong_xiangqiang_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(MeiRong_xiangqiang_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }

        finish.setOnClickListener(this);
        iv_fengxiang.setOnClickListener(this);
        iv_shoucang.setOnClickListener(this);
        zan.setOnClickListener(this);
        ping.setOnClickListener(this);

        if (list_m.isUped()) {
            Drawable nav_up = getResources().getDrawable(R.mipmap.hongxin);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            zan.setCompoundDrawables(null, nav_up, null, null);

            // zan.setImageResource(R.mipmap.hongxin);
        } else {
            Drawable nav_up = getResources().getDrawable(R.mipmap.xin);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            zan.setCompoundDrawables(null, nav_up, null, null);
            //   iv_xin.setImageResource(R.mipmap.xin);
        }

        if (list_m.isCollected()) {
            iv_shoucang.setImageResource(R.mipmap.isshoucang);
        } else {
            iv_shoucang.setImageResource(R.mipmap.shoucang_bai);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.iv_fengxiang:
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
                    oks.setTitle(list_m.getStore_name());
                    // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                    oks.setTitleUrl("http://www.anou.net.cn/web/share/cbshare.jsp");
                    // text是分享文本，所有平台都需要这个字段
                    int v1 = ((int) (Math.random() * 100));
                    Log.i("test", "int:   " + v1);
                    if (v1 % 3 == 2) {
                        oks.setText("更多车美容尽在车吧");
                    } else if (v1 % 3 == 1) {
                        oks.setText("进入车吧，了解更多车美容资讯");
                    } else {
                        oks.setText("车吧，让您的爱车更美丽");
                    }

                    // 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                    if (list_m.getPictrue() != null && !TextUtils.isEmpty(list_m.getPictrue())) {
                        oks.setImageUrl(list_m.getPictrue().split(",")[0]);
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
                    oks.show(MeiRong_xiangqiang_Activity.this);
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
                    if (TextUtils.isEmpty(SPUtils.getString(MeiRong_xiangqiang_Activity.this, "token"))) {
                        MyToast.showToast(MeiRong_xiangqiang_Activity.this, "请先登录");
                    } else {
                        if (list_m.isCollected()) {
                            inithttp_delshoucang(list_m.getId(), SPUtils.getString(MeiRong_xiangqiang_Activity.this, "token"));
                        } else {
                            inithttp_shoucang(list_m.getId(), SPUtils.getString(MeiRong_xiangqiang_Activity.this, "token"));
                        }
                    }
                }
                break;
            case R.id.zan:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(MeiRong_xiangqiang_Activity.this, "token"))) {
                        MyToast.showToast(MeiRong_xiangqiang_Activity.this, "请先登录");
                    } else {
                        if (list_m.isUped()) {
                            MyToast.showToast(MeiRong_xiangqiang_Activity.this, "您已经赞过了");
                        } else {
                            inithttp_up();
                        }
                    }
                }
                break;
            case R.id.ping:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    if (TextUtils.isEmpty(SPUtils.getString(MeiRong_xiangqiang_Activity.this, "token"))) {
                        finish();
                        startActivity(new Intent(MeiRong_xiangqiang_Activity.this, Login_Activity.class));
                        MyToast.showToast(MeiRong_xiangqiang_Activity.this, "发表前请登录");
                    } else {
                        Intent intent1 = new Intent(MeiRong_xiangqiang_Activity.this, MeiRong_ping_Activity.class);
                        intent1.putExtra("pos", posi);
                        startActivityForResult(intent1, 110);
                    }
                }
                break;
            case R.id.iv_phone:
                if (!isenter) {
                    isenter = true;
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isenter = false;
                        }
                    }, 500);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + list_m.getStore_tel());
                    intent.setData(data);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            page = 1;
            pingNum.setText(list_m.getCommentNum()+1 + "条");
            inithttp_getpinglun_list(page);
        }
    }

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
                convertView = View.inflate(MeiRong_xiangqiang_Activity.this, R.layout.pinglun_item_meirong, null);
                viewHolder = new ViewHolder();
                viewHolder.headima = (CircleImageView) convertView.findViewById(R.id.iv_headima);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
                viewHolder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.image_shu = (TextView) convertView.findViewById(R.id.image_shu);
                viewHolder.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);
                viewHolder.iv_1 = (ImageView) convertView.findViewById(R.id.iv_1);
                viewHolder.iv_2 = (ImageView) convertView.findViewById(R.id.iv_2);
                viewHolder.iv_3 = (ImageView) convertView.findViewById(R.id.iv_3);
                viewHolder.iv_4 = (ImageView) convertView.findViewById(R.id.iv_4);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            Glide.with(MeiRong_xiangqiang_Activity.this).load(comment_list.get(position).getHeadImg()).into(viewHolder.headima);
            viewHolder.nickname.setText(comment_list.get(position).getNickname());

            viewHolder.tv_zan.setText(TimeUtil.getDateTimeFromMillisecond2(comment_list.get(position).getDate()));

            viewHolder.content.setText(comment_list.get(position).getContent());
            viewHolder.headima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeiRong_xiangqiang_Activity.this, Other_Activity.class);
                    intent.putExtra("uid", comment_list.get(position).getUid());
                    startActivity(intent);
                }
            });
            if (TextUtils.isEmpty(comment_list.get(position).getPictrue())) {
                strings = new ArrayList<>();
            } else {
                String[] split = comment_list.get(position).getPictrue().split(",");
                strings = Arrays.asList(split);
            }

            if (strings.size() != 0) {
                viewHolder.ll_iv.setVisibility(View.VISIBLE);
                if (strings.size() < 5) {
                    viewHolder.image_shu.setVisibility(View.GONE);
                    switch (strings.size()) {
                        case 1:
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(0)).into(viewHolder.iv_1);
                            viewHolder.iv_1.setTag(R.id.ping_tag, position);
                            viewHolder.iv_1.setOnClickListener(new MyOnClicklistenr());
                            Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(viewHolder.iv_2);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(viewHolder.iv_3);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(viewHolder.iv_4);
                            break;
                        case 2:
                            viewHolder.iv_1.setTag(R.id.ping_tag, position);
                            viewHolder.iv_1.setOnClickListener(new MyOnClicklistenr());
                            viewHolder.iv_2.setTag(R.id.ping_tag, position);
                            viewHolder.iv_2.setOnClickListener(new MyOnClicklistenr());
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(0)).into(viewHolder.iv_1);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(1)).into(viewHolder.iv_2);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(viewHolder.iv_3);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(viewHolder.iv_4);
                            break;
                        case 3:
                            viewHolder.iv_1.setTag(R.id.ping_tag, position);
                            viewHolder.iv_1.setOnClickListener(new MyOnClicklistenr());
                            viewHolder.iv_2.setTag(R.id.ping_tag, position);
                            viewHolder.iv_2.setOnClickListener(new MyOnClicklistenr());
                            viewHolder.iv_3.setTag(R.id.ping_tag, position);
                            viewHolder.iv_3.setOnClickListener(new MyOnClicklistenr());
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(0)).into(viewHolder.iv_1);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(1)).into(viewHolder.iv_2);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(2)).into(viewHolder.iv_3);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load("").into(viewHolder.iv_4);
                            break;
                        case 4:
                            viewHolder.iv_1.setTag(R.id.ping_tag, position);
                            viewHolder.iv_1.setOnClickListener(new MyOnClicklistenr());
                            viewHolder.iv_2.setTag(R.id.ping_tag, position);
                            viewHolder.iv_2.setOnClickListener(new MyOnClicklistenr());
                            viewHolder.iv_3.setTag(R.id.ping_tag, position);
                            viewHolder.iv_3.setOnClickListener(new MyOnClicklistenr());
                            viewHolder.iv_4.setTag(R.id.ping_tag, position);
                            viewHolder.iv_4.setOnClickListener(new MyOnClicklistenr());

                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(0)).into(viewHolder.iv_1);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(1)).into(viewHolder.iv_2);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(2)).into(viewHolder.iv_3);
                            Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(3)).into(viewHolder.iv_4);
                            break;
                    }

                } else {
                    viewHolder.image_shu.setVisibility(View.VISIBLE);
                    Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(0)).into(viewHolder.iv_1);
                    Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(1)).into(viewHolder.iv_2);
                    Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(2)).into(viewHolder.iv_3);
                    Glide.with(MeiRong_xiangqiang_Activity.this).load(strings.get(3)).into(viewHolder.iv_4);
                    viewHolder.iv_1.setTag(R.id.ping_tag, position);
                    viewHolder.iv_1.setOnClickListener(new MyOnClicklistenr());
                    viewHolder.iv_2.setTag(R.id.ping_tag, position);
                    viewHolder.iv_2.setOnClickListener(new MyOnClicklistenr());
                    viewHolder.iv_3.setTag(R.id.ping_tag, position);
                    viewHolder.iv_3.setOnClickListener(new MyOnClicklistenr());
                    viewHolder.iv_4.setTag(R.id.ping_tag, position);
                    viewHolder.iv_4.setOnClickListener(new MyOnClicklistenr());
                    viewHolder.image_shu.setText(strings.size() + " 图");
                }
            } else {
                viewHolder.ll_iv.setVisibility(View.GONE);
            }


            return convertView;
        }
    }

    Handler h = new Handler();

    private void inithttp_getpinglun_list(final int page) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"page\":{\"page\":" + page + "},\"obj\":{\"did\":" + list_m.getId() + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/CarStore!LoadComment.action")
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
                        MyToast.showToast(MeiRong_xiangqiang_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_MeiRong_pinglun mycode = gson.fromJson(res, MyCode_MeiRong_pinglun.class);

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
                                        List<MyCode_MeiRong_pinglun.ObjBean> obj = mycode.getObj();
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

    private void inithttp_up() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(MeiRong_xiangqiang_Activity.this, "token") + "\",\"obj\":{\"id\":\"" + list_m.getId() + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/CarStore!Like.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(MeiRong_xiangqiang_Activity.this, "连接服务器失败");
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
                            list_m.setUped(true);
                            if (posi >= 0) {
                                new MeiRong_Fragment().list_meirong.get(posi).setUped(true);
                            }
                            Drawable nav_up = getResources().getDrawable(R.mipmap.hongxin);
                            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                            zan.setCompoundDrawables(null, nav_up, null, null);

                            MyToast.showToast(MeiRong_xiangqiang_Activity.this, "赞");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(MeiRong_xiangqiang_Activity.this, "token", "");
                            MyToast.showToast(MeiRong_xiangqiang_Activity.this, "请先登录");
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
                        MyToast.showToast(MeiRong_xiangqiang_Activity.this, "连接服务器失败");
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
                            list_m.setCollected(true);
                            if (posi >= 0) {
                                new MeiRong_Fragment().list_meirong.get(posi).setCollected(true);
                            }
                            iv_shoucang.setImageResource(R.mipmap.isshoucang);

                            MyToast.showToast(MeiRong_xiangqiang_Activity.this, "收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(MeiRong_xiangqiang_Activity.this, "token", "");
                            MyToast.showToast(MeiRong_xiangqiang_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_delshoucang(long did, String token) {
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
                        MyToast.showToast(MeiRong_xiangqiang_Activity.this, "连接服务器失败");
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
                            list_m.setCollected(false);
                            if (posi >= 0) {
                                new MeiRong_Fragment().list_meirong.get(posi).setCollected(false);
                            }
                            iv_shoucang.setImageResource(R.mipmap.shoucang);
                            setResult(55);
                            MyToast.showToast(MeiRong_xiangqiang_Activity.this, "取消收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(MeiRong_xiangqiang_Activity.this, "token", "");
                            MyToast.showToast(MeiRong_xiangqiang_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }


    class MyOnClicklistenr implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag(R.id.ping_tag);
            switch (v.getId()) {
                case R.id.iv_1:
                    Intent i1 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i1.putExtra("split", comment_list.get(tag).getPictrue());
                    i1.putExtra("item", 0);
                    startActivity(i1);
                    break;
                case R.id.iv_2:
                    Intent i2 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i2.putExtra("split", comment_list.get(tag).getPictrue());
                    i2.putExtra("item", 1);
                    startActivity(i2);
                    break;
                case R.id.iv_3:
                    Intent i3 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i3.putExtra("split", comment_list.get(tag).getPictrue());
                    i3.putExtra("item", 2);
                    startActivity(i3);
                    break;
                case R.id.iv_4:
                    Intent i4 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i4.putExtra("split", comment_list.get(tag).getPictrue());
                    i4.putExtra("item", 3);
                    startActivity(i4);
                    break;

            }
        }
    }

    class MyOnClicklistenr2 implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_1:
                    Intent i1 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i1.putExtra("split", list_m.getPictrue());
                    i1.putExtra("item", 0);
                    startActivity(i1);
                    break;
                case R.id.iv_2:
                    Intent i2 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i2.putExtra("split", list_m.getPictrue());
                    i2.putExtra("item", 1);
                    startActivity(i2);
                    break;
                case R.id.iv_3:
                    Intent i3 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i3.putExtra("split", list_m.getPictrue());
                    i3.putExtra("item", 2);
                    startActivity(i3);
                    break;
                case R.id.iv_4:
                    Intent i4 = new Intent(MeiRong_xiangqiang_Activity.this, LiuLan_Activity.class);
                    i4.putExtra("split", list_m.getPictrue());
                    i4.putExtra("item", 3);
                    startActivity(i4);
                    break;

            }
        }
    }

    static class ViewHolder {
        CircleImageView headima;
        LinearLayout ll_iv;
        TextView nickname;
        TextView tv_zan;
        TextView content;
        TextView image_shu;
        ImageView iv_1;
        ImageView iv_2;
        ImageView iv_3;
        ImageView iv_4;
    }

}
