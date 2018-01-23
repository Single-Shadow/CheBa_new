package com.aou.cheba.Fragment_new;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.HuaTi_Activity;
import com.aou.cheba.Activity.Login_Activity;
import com.aou.cheba.Activity.Other_Activity;
import com.aou.cheba.Activity.Publish_Activity;
import com.aou.cheba.Activity.Search_Activity;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.bean.MyCode_head;
import com.aou.cheba.bean.MyCode_huati;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MoreWindow;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.aou.cheba.view.RollHeaderView;
import com.bumptech.glide.Glide;
import com.github.library.bubbleview.BubbleTextVew;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.onekeyshare.OnekeyShare;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/5/31.
 */
public class ZiXun_Fragment extends Fragment {
    int[] arr_rand={1,2,1,3,2,1,0,1,3,2,3,2,1};
    private View rootView;
    private RollHeaderView rh;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> list2 = new ArrayList<>();
    private RelativeLayout ll_home;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private MyAdapter myAdapter;
    public static List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private int page = 1;
    private Map<Integer, Integer> map_position = new HashMap<>();
    private ImageView iv_search;
    private TextView tv_search;
    private ImageView iv_jia;
    private PopupWindow window;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.zixun_fragment, container, false);
            findviewbyid();
            initData();
            inithttp_head("");
            inithttp_data(Long.MAX_VALUE, 1);
        }
        return rootView;
    }

    private void findviewbyid() {
        iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
        tv_search = ((TextView) rootView.findViewById(R.id.tv_search));
        iv_jia = ((ImageView) rootView.findViewById(R.id.iv_jia));
        iv_search.setOnClickListener(new MyOnclickListenr());
        iv_jia.setOnClickListener(new MyOnclickListenr());
        tv_search.setOnClickListener(new MyOnclickListenr());

        ll_home = (RelativeLayout) rootView.findViewById(R.id.ll_home);
        mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) rootView.findViewById(R.id.refresh_and_load_more);

        /*iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
//        cheba = (ImageView) rootView.findViewById(R.id.cheba);
        iv_search.setOnClickListener(new MyOnclickListenr());*/

        View inflate = View.inflate(getActivity(), R.layout.zixun_head, null);
        mLoadMoreListView.addHeaderView(inflate);
        rh = (RollHeaderView) inflate.findViewById(R.id.rh);

        rh.setDownOrUpListenner(new RollHeaderView.DownOrUpListenner() {
            @Override
            public void isDown() {
//                mRefreshAndLoadMoreView.setEnabled(false);
                mLoadMoreListView.smoothScrollBy(3,100);
//                Log.i("test","down");
            }

            @Override
            public void isUp() {
//                Log.i("test","up");
//                mRefreshAndLoadMoreView.setEnabled(true);
                mLoadMoreListView.smoothScrollBy(-3,100);
            }
        });

        Log.i("test", Build.VERSION.SDK_INT + "");
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }

        //    0    平移动画效果
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0,
                0, 0, 0, 0);
        translateAnimation.setDuration(3500);
        translateAnimation.setInterpolator(new LinearInterpolator());
//        cheba.startAnimation(translateAnimation);

    }

    private void intView() {
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

                            //删除 不感兴趣的
                            String deletUid = SPUtils.getString(getActivity(), "deletUid");
                            String[] split = deletUid.split(",");
                            if (split!=null&&split.length!=0){
                                List<String> list = Arrays.asList(split);
                                for (int i = list_data.size()-1; i >=0 ; i--) {
                                    if (list.contains(list_data.get(i).getId()+"")){
                                        list_data.remove(i);
                                    }
                                }
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
            ViewHolder1 holderTime = null;
            ViewHolder2 holderTime2 = null;
            ViewHolder3 holderTime3 = null;
            ViewHolder4 holderTime4 = null;

            int rand=getItemViewType(position);

            if (convertView == null) {
                switch (rand) {
                    case 0://大图
                        holderTime = new ViewHolder1();//大图
                        convertView = View.inflate(getActivity(), R.layout.item1_zixun, null);
                        holderTime.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        holderTime.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                        holderTime.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                        convertView.setTag(holderTime);
                        break;

                    case 1://三张图
                        holderTime2 = new ViewHolder2();//三张图
                        convertView = View.inflate(getActivity(), R.layout.item2_zixun, null);
                        holderTime2.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime2.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime2.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        holderTime2.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime2.iv1 = (ImageView) convertView.findViewById(R.id.iv_image1);
                        holderTime2.iv2 = (ImageView) convertView.findViewById(R.id.iv_image2);
                        holderTime2.iv3 = (ImageView) convertView.findViewById(R.id.iv_image3);
                        holderTime2.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);
                        holderTime2.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                        convertView.setTag(holderTime2);
                        break;


                    case 2://一张小图 左边
                        holderTime3 = new ViewHolder3();//左边一张图
                        convertView = View.inflate(getActivity(), R.layout.item4_zixun, null);
                        holderTime3.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime3.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime3.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        holderTime3.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime3.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                        holderTime3.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);
                        holderTime3.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                        convertView.setTag(holderTime3);
                        break;

                    case 3://一张小图 右边
                        holderTime4 = new ViewHolder4();//右边一张图
                        convertView = View.inflate(getActivity(), R.layout.item5_zixun, null);
                        holderTime4.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime4.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime4.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        holderTime4.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime4.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                        holderTime4.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);
                        holderTime4.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                        convertView.setTag(holderTime4);
                        break;

/*                    case 5://没有图片
                        holderTime6 = new ViewHolderTime6();
                        convertView = View.inflate(getActivity(), R.layout.item6_zixun, null);
                        holderTime6.nickname = (TextView) convertView.findViewById(R.id.nickname);
                        holderTime6.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                        holderTime6.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                        holderTime6.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                        holderTime6.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                        convertView.setTag(holderTime6);
                        break;*/

                }
            }

            switch (rand) {
                case 0:
                    holderTime = (ViewHolder1) convertView.getTag();
                    holderTime.shuoshuo.setText(list_data.get(position).getTitle());
                    holderTime.nickname.setText(list_data.get(position).getNickname());
                    holderTime.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");
                    holderTime.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));
                    String[] urls = list_data.get(position).getPictrue().split(",");
                    if (urls.length > 0)
                        Glide.with(getActivity()).load(Data_Util.IMG+urls[0]).into(holderTime.iv);

                    ViewGroup.LayoutParams vParams = holderTime.iv.getLayoutParams();
                    vParams.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.29);
                    holderTime.iv.setLayoutParams(vParams);

                    holderTime.iv_delete.setTag(position);
                    holderTime.iv_delete.setOnClickListener(new MyOnclickListenr());
                    break;
                case 1:
                    holderTime2 = (ViewHolder2) convertView.getTag();
                    holderTime2.shuoshuo.setText(list_data.get(position).getTitle());
                    holderTime2.nickname.setText(list_data.get(position).getNickname());
                    holderTime2.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");
                    holderTime2.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));
                    String[] urls2 = list_data.get(position).getPictrue().split(",");
                    if (urls2.length > 2) {
                        Glide.with(getActivity()).load(Data_Util.IMG+urls2[0]).into(holderTime2.iv1);
                        Glide.with(getActivity()).load(Data_Util.IMG+urls2[1]).into(holderTime2.iv2);
                        Glide.with(getActivity()).load(Data_Util.IMG+urls2[2]).into(holderTime2.iv3);
                    }
                    ViewGroup.LayoutParams vParam2s = holderTime2.ll_iv.getLayoutParams();
                    vParam2s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.13);
                    holderTime2.ll_iv.setLayoutParams(vParam2s);

                    holderTime2.iv_delete.setTag(position);
                    holderTime2.iv_delete.setOnClickListener(new MyOnclickListenr());
                    break;
                case 2:
                    holderTime3 = (ViewHolder3) convertView.getTag();
                    holderTime3.shuoshuo.setText(list_data.get(position).getTitle());
                    holderTime3.nickname.setText(list_data.get(position).getNickname());
                    holderTime3.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");
                    holderTime3.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));
                    String[] urls3 = list_data.get(position).getPictrue().split(",");
                    if (urls3.length > 0)
                        Glide.with(getActivity()).load(Data_Util.IMG+urls3[0]).into(holderTime3.iv);

                    ViewGroup.LayoutParams vParam3s = holderTime3.ll_iv.getLayoutParams();
                    vParam3s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.13);
                    holderTime3.ll_iv.setLayoutParams(vParam3s);

                    holderTime3.iv_delete.setTag(position);
                    holderTime3.iv_delete.setOnClickListener(new MyOnclickListenr());
                    break;
                case 3:
                    holderTime4 = (ViewHolder4) convertView.getTag();
                    holderTime4.shuoshuo.setText(list_data.get(position).getTitle());
                    holderTime4.nickname.setText(list_data.get(position).getNickname());
                    holderTime4.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");
                    holderTime4.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));
                    String[] urls4 = list_data.get(position).getPictrue().split(",");
                    if (urls4.length > 0)
                        Glide.with(getActivity()).load(Data_Util.IMG+urls4[0]).into(holderTime4.iv);

                    ViewGroup.LayoutParams vParam4s = holderTime4.ll_iv.getLayoutParams();
                    vParam4s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.13);
                    holderTime4.ll_iv.setLayoutParams(vParam4s);

                    holderTime4.iv_delete.setTag(position);
                    holderTime4.iv_delete.setOnClickListener(new MyOnclickListenr());
                    break;

            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            String[] split = list_data.get(position).getPictrue().split(",");
            if (split.length<3){
                return  2;
            }else {
                return arr_rand[position%13];
            }
        }

        @Override
        public int getViewTypeCount() {
            return 4;
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
                case R.id.tv_search:
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
                case R.id.iv_jia:
                    if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        if (!isenter) {
                            isenter = true;
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isenter = false;
                                }
                            }, 500);
                            startActivity(new Intent(getActivity(), Login_Activity.class));
                            MyToast.showToast(getActivity(), "发表前请登录");
                        }

                    } else {
                        showMoreWindow(iv_jia);
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

                case R.id.iv_delete:
                    View popupView = getActivity().getLayoutInflater().inflate(R.layout.pop_window, null);
                    popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int measuredWidth = popupView.getMeasuredWidth();
                    int measuredHeight = popupView.getMeasuredHeight();
                    window = new PopupWindow(popupView, measuredWidth,measuredHeight);

                    //获取点击View的坐标
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);

                    // TODO: 2016/5/17 设置动画
                    window.setAnimationStyle(R.style.popup_window_anim);
                    // TODO: 2016/5/17 设置背景颜色
                    window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
                    // TODO: 2016/5/17 设置可以获取焦点
                    window.setFocusable(true);
                    // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
                    window.setOutsideTouchable(true);
                    window.showAtLocation(v, Gravity.NO_GRAVITY,location[0]-measuredWidth,location[1]-(measuredHeight/2-v.getHeight()));


                    int tag = (int) v.getTag();
                    BubbleTextVew pb= (BubbleTextVew) popupView.findViewById(R.id.pb);
                    pb.setTag(tag);
                    pb.setOnClickListener(new MyOnclickListenr());
                    break;

                case R.id.pb:
                    window.dismiss();
                    int tag_pb = (int) v.getTag();
                    SPUtils.put(getActivity(),"deletUid",SPUtils.getString(getActivity(),"deletUid")+","+list_data.get(tag_pb).getId());
                    list_data.remove(tag_pb);
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    MoreWindow mMoreWindow;

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(getActivity());
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view, 100);
    }

    private int clickposition = -1;
    private int clickposition_jubao = -1;

    class ViewHolder1 {
        private ImageView iv;
        private ImageView iv_delete;
        private TextView nickname;
        private TextView tv_time;
        private TextView shuoshuo;
        private TextView tv_ping_num;
    }

    class ViewHolder2 {
        private ImageView iv1;
        private ImageView iv2;
        private ImageView iv3;
        private ImageView iv_delete;
        private TextView nickname;
        private TextView tv_time;
        private TextView shuoshuo;
        private TextView tv_ping_num;
        private LinearLayout ll_iv;
    }

    class ViewHolder3 {
        private ImageView iv;
        private ImageView iv_delete;
        private TextView nickname;
        private TextView tv_time;
        private TextView shuoshuo;
        private TextView tv_ping_num;
        private LinearLayout ll_iv;
    }

    class ViewHolder4 {
        private TextView nickname;
        private TextView tv_time;
        private TextView shuoshuo;
        private TextView tv_ping_num;
        private ImageView iv;
        private ImageView iv_delete;
        private LinearLayout ll_iv;
    }

    class ViewHolderTime6 {
        private TextView nickname;
        private TextView tv_time;
        private TextView shuoshuo;
        private ImageView iv_delete;
        private TextView tv_ping_num;
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
                .url(Data_Util.HttPHEAD + "/Carbar/Server!LoadHotDataByComment.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        list.add("");
                        list2.add("");
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
                                for (int i = 0; i < mycode.getObj().size(); i++) {
                                    if (i==4) break;
                                    list.add(Data_Util.IMG+mycode.getObj().get(i).getPictrue().split(",")[0]);
                                    list2.add(mycode.getObj().get(i).getTitle());
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

                                            inithttp_huati(SPUtils.getString(getActivity(), "token"), mycode.getObj().get(position).getId());
                                           /* if (mycode.getObj().get(position).getType() == 1) {

                                            } else {
                                                Intent intent = new Intent(getActivity(), Web_Activity.class);
                                                intent.putExtra("web", mycode.getObj().get(position).getUrl());
                                                startActivity(intent);
                                            }*/
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
                            objBean.setType(obj.getType());
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

    public void ZXscoll(){
        mLoadMoreListView.smoothScrollToPositionFromTop(0,0);
    }
}
