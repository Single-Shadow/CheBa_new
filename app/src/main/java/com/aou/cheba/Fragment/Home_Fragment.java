package com.aou.cheba.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.HuaTi_Activity;
import com.aou.cheba.Activity.LiuLan_Activity;
import com.aou.cheba.Activity.Login_Activity;
import com.aou.cheba.Activity.Other_Activity;
import com.aou.cheba.Activity.Publish_Activity;
import com.aou.cheba.Activity.Search_Activity;
import com.aou.cheba.Activity.Web_Activity;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.bean.MyCode_head;
import com.aou.cheba.bean.MyCode_huati;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.aou.cheba.view.RollHeaderView;
import com.bumptech.glide.Glide;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;
import me.maxwin.view.XListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/25.
 */
public class Home_Fragment extends Fragment {
    private int pageIndex = 0;
    private View rootView;
    private RollHeaderView rh;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> list2 = new ArrayList<>();
    private RelativeLayout ll_home;
    private XListView mListView;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private MyAdapter myAdapter;
    public static List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private ImageView iv_search;
    private ImageView cheba;
    private int page = 1;
    private int cardWidth;
    private int cardHeight;
    private ArrayList<InnerAdapter> inners = new ArrayList<>();
    private int position_swiper = 0;
    private Map<Integer, Integer> map_position = new HashMap<>();
    Map<Integer, InnerAdapter> map_inner = new HashMap<>();
    private Animation animation;

    private void method() {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getActivity())
                .setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                    @Override
                    public MemoryCacheParams get() {

                        return new MemoryCacheParams(20 << 20,
                                100,
                                Integer.MAX_VALUE,
                                Integer.MAX_VALUE,
                                Integer.MAX_VALUE
                        );
                    }
                }).setMainDiskCacheConfig(DiskCacheConfig.newBuilder(getActivity())
                        .setMaxCacheSize(50 << 20)
                        .setBaseDirectoryPath(getActivity().getCacheDir())
                        .setBaseDirectoryName("fresco")
                        .build())
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(getActivity(), config);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        method();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.home_fragment, container, false);

            findviewbyid();
            initData();
            inithttp_head("");
            inithttp_data(Long.MAX_VALUE, 1);
        }
        return rootView;
    }

    private void findviewbyid() {
        ll_home = (RelativeLayout) rootView.findViewById(R.id.ll_home);
        mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) rootView.findViewById(R.id.refresh_and_load_more);

        iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
        cheba = (ImageView) rootView.findViewById(R.id.cheba);
        iv_search.setOnClickListener(new MyOnclickListenr());

        View inflate = View.inflate(getActivity(), R.layout.head, null);
        mLoadMoreListView.addHeaderView(inflate);
        rh = (RollHeaderView) inflate.findViewById(R.id.rh);

        Log.i("test", Build.VERSION.SDK_INT + "");
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.08);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.05);
            ll_home.setLayoutParams(vParams1);
        }

        //    0    平移动画效果
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0,
                0, 0, 0, 0);
        translateAnimation.setDuration(3500);
        translateAnimation.setInterpolator(new LinearInterpolator());
        cheba.startAnimation(translateAnimation);

    }

    private void intView() {
        int a = 0;
        for (int i = 0; i < list_data.size(); i++) {
            if (list_data.get(i).getTemplate() == 1) {
                map_position.put(i, a);
                a = a + 1;
            }
        }

        myAdapter = new MyAdapter();
        mLoadMoreListView.setAdapter(myAdapter);

        mLoadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                    if (position != 0) {
                        MyCode_data.ObjBean objBean = list_data.get(position - 1);
                        Intent intent = new Intent(getActivity(), HuaTi_Activity.class);
                        intent.putExtra("position", position - 1);
                        intent.putExtra("type","huati");
                        intent.putExtra("ser", objBean);
                        startActivityForResult(intent, 12);
                    }
                }
            }
        });
    }

    private void inithttp_data(long id, final int page) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"page\":{\"id\":" + id + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Server!Load.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //      onLoad();

                        if (myAdapter != null) {
                            myAdapter.notifyDataSetChanged();
                        }

                        //当加载完成之后设置此时不在刷新状态
                        mRefreshAndLoadMoreView.setRefreshing(false);
                        mLoadMoreListView.onLoadComplete();
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                }, 800);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_data mycode = gson.fromJson(res, MyCode_data.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            List<MyCode_data.ObjBean> rows = mycode.getObj();

                            if (page == 1) {
                                list_data.clear();
                                list_data.addAll(rows);
                            } else {
                                list_data.addAll(rows);
                            }

                            if (rows.size() != 0) {
                                if (myAdapter == null) {
                                    intView();
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }

                                int a = 0;
                                map_position.clear();
                                for (int i = 0; i < list_data.size(); i++) {
                                    if (list_data.get(i).getTemplate() == 1) {
                                        map_position.put(i, a);
                                        a = a + 1;
                                    }
                                }

                                if (rows.size() < 15) {
                                    mLoadMoreListView.setHaveMoreData(false);
                                    myAdapter.notifyDataSetChanged();
                                    //当加载完成之后设置此时不在刷新状态
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                } else {
                                    mLoadMoreListView.setHaveMoreData(true);
                                    myAdapter.notifyDataSetChanged();
                                    //当加载完成之后设置此时不在刷新状态
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                }

                            } else {
                                mRefreshAndLoadMoreView.setRefreshing(false);
                                mLoadMoreListView.onLoadComplete();
                                mLoadMoreListView.setHaveMoreData(false);
                                MyToast.showToast(getActivity(), "暂无更多数据");
                            }
                        } else {
                            mRefreshAndLoadMoreView.setRefreshing(false);
                            mLoadMoreListView.onLoadComplete();
                        }
                    }
                });


            }
        });
    }

    Handler h = new Handler();

    private void initData() {
        //程序开始就加载第一页数据
        //    loadData(1);
        mRefreshAndLoadMoreView.setLoadMoreListView(mLoadMoreListView);
        mLoadMoreListView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreView);
        //设置下拉刷新监听
        mRefreshAndLoadMoreView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //     loadData(1);
                page = 1;
                inithttp_data(Long.MAX_VALUE, page);
                inithttp_head("");

            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //  loadData(pageIndex + 1);
                page += 1;
                inithttp_data(list_data.get(list_data.size() - 1).getId(), page);

            }
        });
        //   mLoadMoreListView.setOnItemClickListener(new ItemClickListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        rh.startRoll();
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
        if (new Publish_Activity().ispublic_cheba|| Login_Activity.isoutlogin_home) {
            new Publish_Activity().ispublic_cheba = false;
            Login_Activity.isoutlogin_home=false;
            page = 1;
            inithttp_data(Long.MAX_VALUE, page);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        rh.stopRoll();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list_data.size();
        }

        @Override
        public Object getItem(int position) {
            return list_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolderTime holderTime = null;
            ViewHolderTime4 holderTime4 = null;
            ViewHolderTime5 holderTime5 = null;
            ViewHolderTime6 holderTime6 = null;
            ViewHolderRightText holderRightText = null;
            ViewHolderRightImg holderRightImg = null;

            if (convertView == null) {
                switch (getItemViewType(position)) {
                    case 2://一张图
                        holderTime = new ViewHolderTime();
                        convertView = View.inflate(getActivity(), R.layout.item1, null);
                        holderTime.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                        //   holderTime.iv_xin = (ImageView) convertView.findViewById(R.id.iv_xin);
                        //   holderTime.iv_shoucang = (ImageView) convertView.findViewById(R.id.iv_shoucang);
                        //  holderTime.iv_fengxiang = (ImageView) convertView.findViewById(R.id.iv_fengxiang);
                        //   holderTime.headimg = (CircleImageView) convertView.findViewById(R.id.login);
                        holderTime.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holderTime.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        //    holderTime.btn_guanzhu = (TextView) convertView.findViewById(R.id.btn_guanzhu);
                        holderTime.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        //  holderTime.tv_xin = (TextView) convertView.findViewById(R.id.tv_xin);
                        holderTime.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        //    holderTime.dizhi = (TextView) convertView.findViewById(R.id.tv_dizhi);

                        convertView.setTag(holderTime);
                        break;

                    case 1://三张图
                        holderRightText = new ViewHolderRightText();
                        convertView = View.inflate(getActivity(), R.layout.item2, null);
                        holderRightText.iv1 = (ImageView) convertView.findViewById(R.id.iv_image1);
                        holderRightText.iv2 = (ImageView) convertView.findViewById(R.id.iv_image2);
                        holderRightText.iv3 = (ImageView) convertView.findViewById(R.id.iv_image3);
                        //  holderRightText.iv_xin = (ImageView) convertView.findViewById(R.id.iv_xin);
                        //   holderRightText.iv_shoucang = (ImageView) convertView.findViewById(R.id.iv_shoucang);
                        //  holderRightText.iv_fengxiang = (ImageView) convertView.findViewById(R.id.iv_fengxiang);
                        //  holderRightText.headimg = (CircleImageView) convertView.findViewById(R.id.login);
                        holderRightText.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderRightText.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holderRightText.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        // holderRightText.btn_guanzhu = (TextView) convertView.findViewById(R.id.btn_guanzhu);
                        holderRightText.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        //  holderRightText.tv_xin = (TextView) convertView.findViewById(R.id.tv_xin);
                        holderRightText.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        //  holderRightText.dizhi = (TextView) convertView.findViewById(R.id.tv_dizhi);
                        holderRightText.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);

                        convertView.setTag(holderRightText);
                        break;
                    case 3://一张小图 左边
                        holderTime4 = new ViewHolderTime4();
                        convertView = View.inflate(getActivity(), R.layout.item4, null);
                        holderTime4.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                        holderTime4.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime4.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime4.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime4.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holderTime4.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        holderTime4.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);
                        convertView.setTag(holderTime4);
                        break;

                    case 4://一张小图 右边
                        holderTime5 = new ViewHolderTime5();
                        convertView = View.inflate(getActivity(), R.layout.item5, null);
                        holderTime5.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                        holderTime5.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime5.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime5.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime5.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        holderTime5.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holderTime5.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);
                        convertView.setTag(holderTime5);
                        break;

                    case 5://没有图片
                        holderTime6 = new ViewHolderTime6();
                        convertView = View.inflate(getActivity(), R.layout.item6, null);
                        holderTime6.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime6.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime6.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime6.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holderTime6.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        convertView.setTag(holderTime6);
                        break;

                    case 0://大图
                        holderRightImg = new ViewHolderRightImg();
                        convertView = View.inflate(getActivity(), R.layout.item3, null);
                        holderRightImg.iv = (SwipeFlingAdapterView) convertView.findViewById(R.id.iv_image);
                        holderRightImg.iv_shoucang = (ImageView) convertView.findViewById(R.id.iv_shoucang);
                        holderRightImg.iv_fengxiang = (ImageView) convertView.findViewById(R.id.iv_fengxiang);
                        holderRightImg.iv_xin = (ImageView) convertView.findViewById(R.id.iv_xin);
                        holderRightImg.headimg = (CircleImageView) convertView.findViewById(R.id.login);
                        holderRightImg.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderRightImg.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holderRightImg.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderRightImg.btn_guanzhu = (TextView) convertView.findViewById(R.id.btn_guanzhu);
                        holderRightImg.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderRightImg.tv_xin = (TextView) convertView.findViewById(R.id.tv_xin);
                        holderRightImg.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        holderRightImg.dizhi = (TextView) convertView.findViewById(R.id.tv_dizhi);

                        convertView.setTag(holderRightImg);
                        break;

                }
            }

            switch (getItemViewType(position)) {
                case 2:
                    holderTime = (ViewHolderTime) convertView.getTag();

                    final String[] split = list_data.get(position).getPictrue().split(",");
                    //  Glide.with(getActivity()).load(list_data.get(position).getHeadImg()).into(holderTime.headimg);

                    ViewGroup.LayoutParams vParams4 = holderTime.iv.getLayoutParams();
                    vParams4.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.26);
                    holderTime.iv.setLayoutParams(vParams4);

                    if (split != null || split.length != 0) {
                        Glide.with(getActivity()).load(split[0]).centerCrop().into(holderTime.iv);
                    }

                    holderTime.nickname.setText(list_data.get(position).getNickname());
                  /*  if (list_data.get(position).getLocation() == null || list_data.get(position).getLocation().equals("未定位")) {
                        holderTime.dizhi.setVisibility(View.INVISIBLE);
                    } else {
                        holderTime.dizhi.setVisibility(View.VISIBLE);
                        holderTime.dizhi.setText(list_data.get(position).getLocation());
                    }*/

                    switch (list_data.get(position).getType()) {
                        case 1:
                            holderTime.tv_type.setText("吐槽");
                            break;
                        case 2:
                            holderTime.tv_type.setText("嘻游");
                            break;
                        case 3:
                            holderTime.tv_type.setText("经验");
                            break;
                        case 4:
                            holderTime.tv_type.setText("话题");
                            break;
                        case 5:
                            holderTime.tv_type.setText("新闻");
                            break;
                    }

                    /*Date dat1 = new Date(list_data.get(position).getAddtime());
                    GregorianCalendar gc1 = new GregorianCalendar();
                    gc1.setTime(dat1);
                    java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("MM-dd hh:mm");
                    String sb1 = format1.format(gc1.getTime());*/


                    holderTime.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));


                    holderTime.shuoshuo.setText(list_data.get(position).getTitle());
                    //  holderTime.tv_xin.setText(list_data.get(position).getUpCount() + "");
                    holderTime.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");
               /*     if (list_data.get(position).isUped() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        holderTime.iv_xin.setImageResource(R.mipmap.hongxin);
                    } else {
                        holderTime.iv_xin.setImageResource(R.mipmap.xin);
                    }

                    if (list_data.get(position).isCollected() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        holderTime.iv_shoucang.setImageResource(R.mipmap.isshoucang);
                    } else {
                        holderTime.iv_shoucang.setImageResource(R.mipmap.shoucang);
                    }*/

                 /*   if (SPUtils.getString(getActivity(), "uid").equals(list_data.get(position).getUid() + "")) {
                        holderTime.btn_guanzhu.setVisibility(View.GONE);
                    } else {
                        if (list_data.get(position).isFollowed() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            holderTime.btn_guanzhu.setVisibility(View.VISIBLE);
                            holderTime.btn_guanzhu.setText("已关注");
                            holderTime.btn_guanzhu.setTextColor(Color.parseColor("#d9d9d9"));
                            holderTime.btn_guanzhu.setBackgroundResource(R.drawable.custom_hui);
                            holderTime.btn_guanzhu.setEnabled(false);
                        } else {
                            holderTime.btn_guanzhu.setEnabled(true);
                            holderTime.btn_guanzhu.setVisibility(View.VISIBLE);
                            holderTime.btn_guanzhu.setText("+ 关注");
                            holderTime.btn_guanzhu.setTextColor(Color.parseColor("#ffffff"));
                            holderTime.btn_guanzhu.setBackgroundResource(R.drawable.custom_lv);
                        }
                    }*/

              /*      holderTime.headimg.setTag(R.id.image_tag, position);
                    holderTime.headimg.setOnClickListener(new MyOnclickListenr());
                    holderTime.btn_guanzhu.setTag(position);
                    holderTime.btn_guanzhu.setOnClickListener(new MyOnclickListenr());
                    holderTime.iv_shoucang.setTag(position);
                    holderTime.iv_shoucang.setOnClickListener(new MyOnclickListenr());
                    holderTime.iv_fengxiang.setTag(position);
                    holderTime.iv_fengxiang.setOnClickListener(new MyOnclickListenr());
                    holderTime.iv_xin.setTag(position);
                    holderTime.iv_xin.setOnClickListener(new MyOnclickListenr());
*/
                    break;

                case 3:
                    holderTime4 = (ViewHolderTime4) convertView.getTag();

                    final String[] split4 = list_data.get(position).getPictrue().split(",");
                    //  Glide.with(getActivity()).load(list_data.get(position).getHeadImg()).into(holderTime.headimg);

                    ViewGroup.LayoutParams vParam4s = holderTime4.ll_iv.getLayoutParams();
                    vParam4s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.12);
                    holderTime4.ll_iv.setLayoutParams(vParam4s);

                    if (split4 != null || split4.length != 0) {
                        Glide.with(getActivity()).load(split4[0]).centerCrop().into(holderTime4.iv);
                    }
                    switch (list_data.get(position).getType()) {
                        case 1:
                            holderTime4.tv_type.setText("吐槽");
                            break;
                        case 2:
                            holderTime4.tv_type.setText("嘻游");
                            break;
                        case 3:
                            holderTime4.tv_type.setText("经验");
                            break;
                        case 4:
                            holderTime4.tv_type.setText("话题");
                            break;
                        case 5:
                            holderTime4.tv_type.setText("新闻");
                            break;
                    }
                    holderTime4.nickname.setText(list_data.get(position).getNickname());

                    /*Date dat4 = new Date(list_data.get(position).getAddtime());
                    GregorianCalendar gc4 = new GregorianCalendar();
                    gc4.setTime(dat4);
                    java.text.SimpleDateFormat format4 = new java.text.SimpleDateFormat("MM-dd hh:mm");
                    String sb4 = format4.format(gc4.getTime());
                    holderTime4.tv_time.setText(sb4);*/
                    holderTime4.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));

                    holderTime4.shuoshuo.setText(list_data.get(position).getTitle());
                    //  holderTime.tv_xin.setText(list_data.get(position).getUpCount() + "");
                    holderTime4.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");

                    break;

                case 4:
                    holderTime5 = (ViewHolderTime5) convertView.getTag();

                    final String[] split5 = list_data.get(position).getPictrue().split(",");
                    //  Glide.with(getActivity()).load(list_data.get(position).getHeadImg()).into(holderTime.headimg);

                    ViewGroup.LayoutParams vParam5s = holderTime5.ll_iv.getLayoutParams();
                    vParam5s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.12);
                    holderTime5.ll_iv.setLayoutParams(vParam5s);

                    if (split5 != null || split5.length != 0) {
                        Glide.with(getActivity()).load(split5[0]).centerCrop().into(holderTime5.iv);
                    }
                    switch (list_data.get(position).getType()) {
                        case 1:
                            holderTime5.tv_type.setText("吐槽");
                            break;
                        case 2:
                            holderTime5.tv_type.setText("嘻游");
                            break;
                        case 3:
                            holderTime5.tv_type.setText("经验");
                            break;
                        case 4:
                            holderTime5.tv_type.setText("话题");
                            break;
                        case 5:
                            holderTime5.tv_type.setText("新闻");
                            break;
                    }
                    holderTime5.nickname.setText(list_data.get(position).getNickname());

                    /*Date dat5 = new Date(list_data.get(position).getAddtime());
                    GregorianCalendar gc5 = new GregorianCalendar();
                    gc5.setTime(dat5);
                    java.text.SimpleDateFormat format5 = new java.text.SimpleDateFormat("MM-dd hh:mm");
                    String sb5 = format5.format(gc5.getTime());
                    holderTime5.tv_time.setText(sb5);*/
                    holderTime5.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));

                    holderTime5.shuoshuo.setText(list_data.get(position).getTitle());
                    //  holderTime.tv_xin.setText(list_data.get(position).getUpCount() + "");
                    holderTime5.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");

                    break;

                case 5:
                    holderTime6 = (ViewHolderTime6) convertView.getTag();

                    final String[] split6 = list_data.get(position).getPictrue().split(",");
                    //  Glide.with(getActivity()).load(list_data.get(position).getHeadImg()).into(holderTime.headimg);

                    holderTime6.nickname.setText(list_data.get(position).getNickname());
                    switch (list_data.get(position).getType()) {
                        case 1:
                            holderTime6.tv_type.setText("吐槽");
                            break;
                        case 2:
                            holderTime6.tv_type.setText("嘻游");
                            break;
                        case 3:
                            holderTime6.tv_type.setText("经验");
                            break;
                        case 4:
                            holderTime6.tv_type.setText("话题");
                            break;
                        case 5:
                            holderTime6.tv_type.setText("新闻");
                            break;
                    }
                  /*  Date dat6 = new Date(list_data.get(position).getAddtime());
                    GregorianCalendar gc6 = new GregorianCalendar();
                    gc6.setTime(dat6);
                    java.text.SimpleDateFormat format6 = new java.text.SimpleDateFormat("MM-dd hh:mm");
                    String sb6 = format6.format(gc6.getTime());
                    holderTime6.tv_time.setText(sb6);*/
                    holderTime6.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));

                    holderTime6.shuoshuo.setText(list_data.get(position).getTitle());
                    //  holderTime.tv_xin.setText(list_data.get(position).getUpCount() + "");
                    holderTime6.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");

                    break;

                case 1:
                    holderRightText = (ViewHolderRightText) convertView.getTag();
                    String[] split2 = list_data.get(position).getPictrue().split(",");
                    //  Glide.with(getActivity()).load(list_data.get(position).getHeadImg()).into(holderRightText.headimg);

                    if (split2 == null || split2.length == 0 || split2.length == 1) {
                        holderRightText.ll_iv.setVisibility(View.GONE);
                    } else {
                        holderRightText.ll_iv.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams vParams1 = holderRightText.ll_iv.getLayoutParams();
                        vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.12);
                        holderRightText.ll_iv.setLayoutParams(vParams1);
                    }

                    if (split2.length == 2) {
                        Glide.with(getActivity()).load(split2[1]).centerCrop().into(holderRightText.iv1);
                        Glide.with(getActivity()).load("").centerCrop().into(holderRightText.iv2);
                        Glide.with(getActivity()).load("").centerCrop().into(holderRightText.iv3);
                    } else if (split2.length == 3) {
                        Glide.with(getActivity()).load(split2[1]).centerCrop().into(holderRightText.iv1);
                        Glide.with(getActivity()).load(split2[2]).centerCrop().into(holderRightText.iv2);
                        Glide.with(getActivity()).load("").centerCrop().into(holderRightText.iv3);
                    } else if (split2.length > 3) {
                        Glide.with(getActivity()).load(split2[1]).centerCrop().into(holderRightText.iv1);
                        Glide.with(getActivity()).load(split2[2]).centerCrop().into(holderRightText.iv2);
                        Glide.with(getActivity()).load(split2[3]).centerCrop().into(holderRightText.iv3);
                    }

                    holderRightText.nickname.setText(list_data.get(position).getNickname());
                    switch (list_data.get(position).getType()) {
                        case 1:
                            holderRightText.tv_type.setText("吐槽");
                            break;
                        case 2:
                            holderRightText.tv_type.setText("嘻游");
                            break;
                        case 3:
                            holderRightText.tv_type.setText("经验");
                            break;
                        case 4:
                            holderRightText.tv_type.setText("话题");
                            break;
                        case 5:
                            holderRightText.tv_type.setText("新闻");
                            break;
                    }

                    /*Date dat2 = new Date(list_data.get(position).getAddtime());
                    GregorianCalendar gc2 = new GregorianCalendar();
                    gc2.setTime(dat2);
                    java.text.SimpleDateFormat format2 = new java.text.SimpleDateFormat("MM-dd hh:mm");
                    String sb2 = format2.format(gc2.getTime());
                    holderRightText.tv_time.setText(sb2);*/
                    holderRightText.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));

                    holderRightText.shuoshuo.setText(list_data.get(position).getTitle());
                    //  holderRightText.tv_xin.setText(list_data.get(position).getUpCount() + "");
                    holderRightText.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");

                 /*   if (list_data.get(position).getLocation() == null || list_data.get(position).getLocation().equals("未定位")) {
                        holderRightText.dizhi.setVisibility(View.INVISIBLE);
                    } else {
                        holderRightText.dizhi.setVisibility(View.VISIBLE);
                        holderRightText.dizhi.setText(list_data.get(position).getLocation());
                    }*/

                  /*  if (list_data.get(position).isUped() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        holderRightText.iv_xin.setImageResource(R.mipmap.hongxin);
                    } else {
                        holderRightText.iv_xin.setImageResource(R.mipmap.xin);
                    }
                    if (list_data.get(position).isCollected() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        holderRightText.iv_shoucang.setImageResource(R.mipmap.isshoucang);
                    } else {
                        holderRightText.iv_shoucang.setImageResource(R.mipmap.shoucang);
                    }


                    if (SPUtils.getString(getActivity(), "uid").equals(list_data.get(position).getUid() + "")) {
                        holderRightText.btn_guanzhu.setVisibility(View.GONE);
                    } else {
                        if (list_data.get(position).isFollowed() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            holderRightText.btn_guanzhu.setVisibility(View.VISIBLE);
                            holderRightText.btn_guanzhu.setText("已关注");
                            holderRightText.btn_guanzhu.setTextColor(Color.parseColor("#d9d9d9"));
                            holderRightText.btn_guanzhu.setBackgroundResource(R.drawable.custom_hui);
                            holderRightText.btn_guanzhu.setEnabled(false);
                        } else {
                            holderRightText.btn_guanzhu.setEnabled(true);
                            holderRightText.btn_guanzhu.setVisibility(View.VISIBLE);
                            holderRightText.btn_guanzhu.setText("+ 关注");
                            holderRightText.btn_guanzhu.setTextColor(Color.parseColor("#ffffff"));
                            holderRightText.btn_guanzhu.setBackgroundResource(R.drawable.custom_lv);
                        }
                    }*/


            /*        holderRightText.headimg.setTag(R.id.image_tag, position);
                    holderRightText.headimg.setOnClickListener(new MyOnclickListenr());
                    holderRightText.btn_guanzhu.setTag(position);
                    holderRightText.btn_guanzhu.setOnClickListener(new MyOnclickListenr());
                    holderRightText.iv_shoucang.setTag(position);
                    holderRightText.iv_shoucang.setOnClickListener(new MyOnclickListenr());
                    holderRightText.iv_fengxiang.setTag(position);
                    holderRightText.iv_fengxiang.setOnClickListener(new MyOnclickListenr());
                    holderRightText.iv_xin.setTag(position);
                    holderRightText.iv_xin.setOnClickListener(new MyOnclickListenr());
*/
                    break;
                case 0:
                    holderRightImg = (ViewHolderRightImg) convertView.getTag();
                    Glide.with(getActivity()).load(list_data.get(position).getHeadImg()).into(holderRightImg.headimg);
                    holderRightImg.nickname.setText(list_data.get(position).getNickname());
                    switch (list_data.get(position).getTemplate()) {
                        case 1:
                            holderRightImg.tv_type.setText("吐槽");
                            break;
                        case 2:
                            holderRightImg.tv_type.setText("嘻游");
                            break;
                        case 3:
                            holderRightImg.tv_type.setText("经验");
                            break;
                        case 4:
                            holderRightImg.tv_type.setText("话题");
                            break;
                        case 5:
                            holderRightImg.tv_type.setText("新闻");
                            break;
                    }
/*
                    Date dat0 = new Date(list_data.get(position).getAddtime());
                    GregorianCalendar gc0 = new GregorianCalendar();
                    gc0.setTime(dat0);
                    java.text.SimpleDateFormat format0 = new java.text.SimpleDateFormat("MM-dd hh:mm");
                    String sb0 = format0.format(gc0.getTime());
                    holderRightImg.tv_time.setText(sb0);*/
                    holderRightImg.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));



                    holderRightImg.shuoshuo.setText(list_data.get(position).getTitle());
                    holderRightImg.tv_xin.setText(list_data.get(position).getUpCount() + "");
                    holderRightImg.tv_ping_num.setText(list_data.get(position).getCommentNum() + "");

                    if (list_data.get(position).getLocation() == null || list_data.get(position).getLocation().equals("未定位")) {
                        holderRightImg.dizhi.setVisibility(View.INVISIBLE);
                    } else {
                        holderRightImg.dizhi.setVisibility(View.VISIBLE);
                        holderRightImg.dizhi.setText(list_data.get(position).getLocation());
                    }

                    if (list_data.get(position).isUped() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        holderRightImg.iv_xin.setImageResource(R.mipmap.hongxin);
                    } else {
                        holderRightImg.iv_xin.setImageResource(R.mipmap.xin);
                    }
                    if (list_data.get(position).isCollected() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        holderRightImg.iv_shoucang.setImageResource(R.mipmap.isshoucang);
                    } else {
                        holderRightImg.iv_shoucang.setImageResource(R.mipmap.shoucang);
                    }

                    if (SPUtils.getString(getActivity(), "uid").equals(list_data.get(position).getUid() + "")) {
                        holderRightImg.btn_guanzhu.setVisibility(View.GONE);
                    } else {
                        if (list_data.get(position).isFollowed() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            holderRightImg.btn_guanzhu.setVisibility(View.VISIBLE);
                            holderRightImg.btn_guanzhu.setText("已关注");
                            holderRightImg.btn_guanzhu.setTextColor(Color.parseColor("#d9d9d9"));
                            holderRightImg.btn_guanzhu.setBackgroundResource(R.drawable.custom_hui);
                            holderRightImg.btn_guanzhu.setEnabled(false);
                        } else {
                            holderRightImg.btn_guanzhu.setEnabled(true);
                            holderRightImg.btn_guanzhu.setVisibility(View.VISIBLE);
                            holderRightImg.btn_guanzhu.setText("+ 关注");
                            holderRightImg.btn_guanzhu.setTextColor(Color.parseColor("#ffffff"));
                            holderRightImg.btn_guanzhu.setBackgroundResource(R.drawable.custom_lv);
                        }

                    }

                    holderRightImg.headimg.setTag(R.id.image_tag, position);
                    holderRightImg.headimg.setOnClickListener(new MyOnclickListenr());
                    holderRightImg.btn_guanzhu.setTag(position);
                    holderRightImg.btn_guanzhu.setOnClickListener(new MyOnclickListenr());
                    holderRightImg.iv_shoucang.setTag(position);
                    holderRightImg.iv_shoucang.setOnClickListener(new MyOnclickListenr());
                    holderRightImg.iv_fengxiang.setTag(position);
                    holderRightImg.iv_fengxiang.setOnClickListener(new MyOnclickListenr());
                    holderRightImg.iv_xin.setTag(position);
                    holderRightImg.iv_xin.setOnClickListener(new MyOnclickListenr());


                    //**************************************************************************************************
                    //    SwipeFlingAdapterView sf = (SwipeFlingAdapterView) convertView.findViewByIds(R.id.iv_datu);

                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    float density = dm.density;
//            cardWidth = (int) (dm.widthPixels - (2 * 18 * density));
                    cardWidth = dm.widthPixels;
                    cardHeight = (int) (dm.heightPixels - (338 * density));

                    holderRightImg.iv.setIsNeedSwipe(true);  //是否开启swipe滑动效果，当不调用此方法设置时，默认开启
                    //让banner的高度是屏幕的1/4***********************************************************************
                    if (TextUtils.isEmpty(list_data.get(position).getPictrue())) {
                        ViewGroup.LayoutParams vParams = holderRightImg.iv.getLayoutParams();
                        vParams.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0);
                        holderRightImg.iv.setLayoutParams(vParams);
                    } else {
                        ViewGroup.LayoutParams vParams = holderRightImg.iv.getLayoutParams();
                        vParams.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.34);
                        holderRightImg.iv.setLayoutParams(vParams);
                    }
                    //**********************************************************************************************

                    if (clickposition < 0) {
                        InnerAdapter innerAdapter = new InnerAdapter();
                        map_inner.put(position, innerAdapter);
                    }


                    holderRightImg.iv.setAdapter(map_inner.get(position));

                    loadData(position);

                    holderRightImg.iv.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                        @Override
                        public void removeFirstObjectInAdapter() {
                            map_inner.get(position).remove(0);
                        }

                        @Override
                        public void onLeftCardExit(Object dataObject) {
                        }

                        @Override
                        public void onRightCardExit(Object dataObject) {
                        }

                        @Override
                        public void onAdapterAboutToEmpty(int itemsInAdapter) {
                            if (itemsInAdapter <= 3) {
                                loadData(position);
                            }
                        }

                        @Override
                        public void onScroll(float progress, float scrollXProgress) {

                        }

                    });

                    holderRightImg.iv.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClicked(MotionEvent event, View v, Object dataObject) {

                            Intent i = new Intent(getActivity(), LiuLan_Activity.class);
                            String[] split1 = list_data.get(position).getPictrue().split(",");
                            List<String> listUrl = new ArrayList();
                            for (int i1 = 0; i1 < split1.length; i1++) {
                                listUrl.add(split1[i1]);
                            }
                            listUrl.remove(0);
                            String ss = "";
                            for (String s : listUrl) {
                                if (ss.equals("")) {
                                    ss = s;
                                } else {
                                    ss = ss + "," + s;
                                }
                            }
                            i.putExtra("split", ss);
                            startActivity(i);
                        }
                    });
                    break;

            }

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return list_data.get(position).getTemplate() - 1;
        }

        @Override
        public int getViewTypeCount() {
            return 6;
        }
    }

    private void inithttp_up(final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"obj\":{\"id\":\"" + list_data.get(position).getId() + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Server!Like.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(getActivity(), "连接服务器失败");
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

                            list_data.get(position).setUpCount(list_data.get(position).getUpCount() + 1);
                            list_data.get(position).setUped(true);
                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });
                            MyToast.showToast(getActivity(), "赞");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
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
                        MyToast.showToast(getActivity(), "连接服务器失败");
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
                            list_data.get(position).setCollected(true);
                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });
                            MyToast.showToast(getActivity(), "收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
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
                        MyToast.showToast(getActivity(), "连接服务器失败");
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
                            list_data.get(position).setCollected(false);
                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });
                            MyToast.showToast(getActivity(), "取消收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
                        }
                    }
                });
            }
        });
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
                        MyToast.showToast(getActivity(), "连接服务器失败");
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
                            //      list_data.get(position).setFollowed(true);
                            for (int i = 0; i < list_data.size(); i++) {
                                if (list_data.get(i).getUid() == uid) {
                                    list_data.get(i).setFollowed(true);
                                }
                            }

                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });

                            MyCodeInfo me_mycodes = new Me_Fragment().mycodes;
                            if (me_mycodes != null) {
                                me_mycodes.getObj().setFollowCount(me_mycodes.getObj().getFollowCount() + 1);
                            }
                            SPUtils.put(getActivity(), "islogin", true);
                            MyToast.showToast(getActivity(), "关注成功");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
                        }
                    }
                });
            }
        });
    }

    class MyOnclickListenr implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position;
            switch (v.getId()) {
                case R.id.iv_search:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        startActivity(new Intent(getActivity(), Search_Activity.class));
                    }
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
                        if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            MyToast.showToast(getActivity(), "请先登录");
                        } else {
                            position = (int) v.getTag();
                            if (list_data.get(position).isUped()) {
                                MyToast.showToast(getActivity(), "您已经赞过了");
                            } else {
                                inithttp_up(position);
                            }
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
                        if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            MyToast.showToast(getActivity(), "请先登录");
                        } else {
                            position = (int) v.getTag();
                            if (list_data.get(position).isFollowed()) {
                                MyToast.showToast(getActivity(), "已关注");
                            } else {
                                inithttp_guanzhu(list_data.get(position).getUid(), SPUtils.getString(getActivity(), "token"), position);
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
                        if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            MyToast.showToast(getActivity(), "请先登录");
                        } else {
                            position = (int) v.getTag();
                            if (list_data.get(position).isCollected()) {
                                inithttp_delshoucang(list_data.get(position).getId(), SPUtils.getString(getActivity(), "token"), position);
                            } else {
                                inithttp_shoucang(list_data.get(position).getId(), SPUtils.getString(getActivity(), "token"), position);
                            }
                        }
                    }
                    break;

                case R.id.login:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        position = (int) v.getTag(R.id.image_tag);
                        Intent intent = new Intent(getActivity(), Other_Activity.class);
                        intent.putExtra("uid", list_data.get(position).getUid());
                        startActivity(intent);
                    }

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
                        position = (int) v.getTag();

                        OnekeyShare oks = new OnekeyShare();
                        // 关闭sso授权
                        oks.disableSSOWhenAuthorize();

                        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
                        // oks.setNotification(R.drawable.ic_launcher,
                        // getString(R.string.app_name));
                        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                        oks.setTitle(list_data.get(position).getTitle());
                        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                        oks.setTitleUrl("http://www.anou.net.cn/web/share/cbshare.jsp");
                        // text是分享文本，所有平台都需要这个字段
                        if (list_data.get(position).getContent() != null && !TextUtils.isEmpty(list_data.get(position).getContent())) {
                            oks.setText(list_data.get(position).getContent());
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
                        if (list_data.get(position).getPictrue() != null && !TextUtils.isEmpty(list_data.get(position).getPictrue())) {
                            oks.setImageUrl(list_data.get(position).getPictrue().split(",")[0]);
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
                        oks.show(getActivity());
                    }
                    break;
            }
        }
    }

    private int clickposition = -1;
    private int clickposition_jubao = -1;

    public static class Talent {
        public String headerIcon;
    }

    private static class ViewHolder1 {
        SimpleDraweeView portraitView;
    }

    private class InnerAdapter extends BaseAdapter {

        ArrayList<Talent> objs;

        public InnerAdapter() {
            objs = new ArrayList<>();
        }

        public void addAll(Collection<Talent> collection) {
            if (isEmpty()) {
                objs.addAll(collection);
                notifyDataSetChanged();
            } else {
                objs.addAll(collection);
            }
        }

        public void clear() {
            objs.clear();
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return objs.isEmpty();
        }

        public void remove(int index) {
            if (index > -1 && index < objs.size()) {
                objs.remove(index);
                notifyDataSetChanged();
            }
        }


        @Override
        public int getCount() {
            return objs.size();
        }

        @Override
        public Talent getItem(int position) {
            if (objs == null || objs.size() == 0) return null;
            return objs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // TODO: getView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Talent talent = getItem(position);

            ViewHolder1 holder = null;
            if (convertView == null) {
                holder = new ViewHolder1();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ceshi2, parent, false);
                holder.portraitView = (SimpleDraweeView) convertView.findViewById(R.id.portrait);
                convertView.setTag(holder);

            }
            holder = (ViewHolder1) convertView.getTag();
            convertView.getLayoutParams().width = cardWidth;
            //holder.portraitView.getLayoutParams().width = cardWidth;
            holder.portraitView.getLayoutParams().height = cardHeight;

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(talent.headerIcon))
                    .setResizeOptions(new ResizeOptions(600, 600))
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder.portraitView.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>())
                    .build();
            holder.portraitView.setController(controller);

            return convertView;
        }
    }

    private void loadData(final int position) {
        final String[] split = list_data.get(position).getPictrue().split(",");

        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, List<Talent>>() {

            @Override
            protected List<Talent> doInBackground(Void... params) {
                ArrayList<Talent> list_telent = new ArrayList<>(split.length);
                Talent talent;
                for (int i = 0; i < split.length; i++) {
                    talent = new Talent();
                    talent.headerIcon = split[i];
                    list_telent.add(talent);
                }
                return list_telent;
            }

            @Override
            protected void onPostExecute(List<Talent> list) {
                super.onPostExecute(list);
                //   list.remove(0);
                if (list.size() != 0) {
                    list.remove(0);
                }

                map_inner.get(position).addAll(list);
            }
        });
    }


    class ViewHolderTime {
        private ImageView iv;
        //   private ImageView iv_xin;
        //  private ImageView iv_shoucang;
        //   private ImageView iv_fengxiang;
        //  private CircleImageView headimg;
        private TextView nickname;
        private TextView tv_type;
        private TextView tv_time;
        //   private TextView btn_guanzhu;
        private TextView shuoshuo;
        // private TextView tv_xin;
        private TextView tv_ping_num;
        //    private TextView dizhi;
    }

    class ViewHolderTime4 {
        private ImageView iv;
        private TextView nickname;
        private TextView tv_time;
        private TextView tv_type;
        private TextView shuoshuo;
        private TextView tv_ping_num;
        private LinearLayout ll_iv;
    }

    class ViewHolderTime5 {
        private ImageView iv;
        private TextView nickname;
        private TextView tv_time;
        private TextView tv_type;
        private TextView shuoshuo;
        private TextView tv_ping_num;
        private LinearLayout ll_iv;
    }

    class ViewHolderTime6 {
        private TextView nickname;
        private TextView tv_time;
        private TextView shuoshuo;
        private TextView tv_type;
        private TextView tv_ping_num;
    }

    class ViewHolderRightText {
        private ImageView iv1;
        private ImageView iv2;
        private ImageView iv3;
        //    private ImageView iv_xin;
        //   private ImageView iv_shoucang;
        //   private ImageView iv_fengxiang;
        //   private CircleImageView headimg;
        private TextView nickname;
        private TextView tv_type;
        private TextView tv_time;
        //  private TextView btn_guanzhu;
        private TextView shuoshuo;
        //  private TextView tv_xin;
        private TextView tv_ping_num;
        //  private TextView dizhi;
        private LinearLayout ll_iv;
    }

    class ViewHolderRightImg {
        private SwipeFlingAdapterView iv;
        private ImageView iv_xin;
        private ImageView iv_shoucang;
        private ImageView iv_fengxiang;
        private CircleImageView headimg;
        private TextView nickname;
        private TextView tv_type;
        private TextView tv_time;
        private TextView btn_guanzhu;
        private TextView shuoshuo;
        private TextView tv_xin;
        private TextView tv_ping_num;
        private TextView dizhi;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 55) {
            myAdapter.notifyDataSetChanged();
        }
    }

    private void inithttp_head(String token) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + token + "\"}");
//创建一个请求对象

        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Server!LoadHotData.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {

                        list.add("http://images.ali213.net/picfile/pic/2013/02/04/927_110420232941-12.jpg");
                        list.add("http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1207/18/c1/12378628_1342603613474.jpg");
                        list.add("http://c.hiphotos.baidu.com/zhidao/pic/item/83025aafa40f4bfb89bdd71e074f78f0f736180e.jpg");
                        list.add("http://www.my510.com/forumimg/20060815/1177619.jpg");

                        list2.add("一起躲雨的屋檐");
                        list2.add("枫叶是秋天的使者");
                        list2.add("时间不在于长短");
                        list2.add("最美不是下雨天");
                        rh.settext(list2);
                        rh.setImgUrlData(list);
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();
                Gson gson = new Gson();
                final MyCode_head mycode = gson.fromJson(res, MyCode_head.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {

                            Log.i("test","轮播图重新加载了");
                            list.clear();
                            list2.clear();
                            if (mycode.getObj().size() != 0) {
                                for (MyCode_head.ObjBean objBean : mycode.getObj()) {
                                    list.add(objBean.getPictrue().split(",")[0]);
                                    list2.add(objBean.getTitle());
                                }
                                rh.settext(list2);
                                rh.setImgUrlData(list);

                                rh.setOnHeaderViewClickListener(new RollHeaderView.HeaderViewClickListener() {
                                    @Override
                                    public void HeaderViewClick(int position) {
                                        if (!isenter) {
                                            isenter = true;
                                            h.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    isenter = false;
                                                }
                                            }, 500);
                                            if (mycode.getObj().get(position).getType() == 1) {
                                                inithttp_huati(SPUtils.getString(getActivity(), "token"), mycode.getObj().get(position).getId());
                                            } else {
                                                Intent intent = new Intent(getActivity(), Web_Activity.class);
                                                intent.putExtra("web", mycode.getObj().get(position).getUrl());
                                                startActivity(intent);
                                            }
                                        }

                                        // startActivity(new Intent(getActivity(), WebView_activity.class));
                                        //   Toast.makeText(getActivity(), "点击 : " + position, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    }
                });
            }
        });
    }

    private boolean isenter = false;

    private void inithttp_huati(String token, final long did) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"id\":" + did + "},\"token\":\"" + token + "\"}");
//创建一个请求对象

        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Server!LoadById.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();
                Gson gson = new Gson();
                final MyCode_huati mycode = gson.fromJson(res, MyCode_huati.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {

                            MyCode_huati.ObjBean obj = mycode.getObj();
                            MyCode_data.ObjBean objBean = new MyCode_data.ObjBean();
                            objBean.setCollected(obj.isCollected());
                            objBean.setFollowed(obj.isFollowed());
                            objBean.setUped(obj.isUped());
                            objBean.setAddtime(obj.getAddtime());
                            objBean.setCommentNum(obj.getCommentNum());
                            objBean.setContent(obj.getContent());
                            objBean.setGender(obj.getGender());
                            objBean.setHeadImg(obj.getHeadImg());
                            objBean.setId(obj.getId());
                            objBean.setLocation(obj.getLocation());
                            objBean.setNickname(obj.getNickname());
                            objBean.setPictrue(obj.getPictrue());
                            objBean.setTemplate(obj.getTemplate());
                            objBean.setTitle(obj.getTitle());
                            objBean.setUid(obj.getUid());
                            objBean.setUpCount(obj.getUpCount());

                            Intent intent = new Intent(getActivity(), HuaTi_Activity.class);
                            intent.putExtra("position", -1);
                            intent.putExtra("ser", objBean);
                            startActivity(intent);

                        }
                    }
                });
            }
        });
    }
}
