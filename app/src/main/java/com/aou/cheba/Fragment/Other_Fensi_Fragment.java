package com.aou.cheba.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.Other_Activity;
import com.aou.cheba.Activity.Other_GuanZhu_Activity;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_fensi;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/6.
 */
public class Other_Fensi_Fragment extends Fragment {

    private View rootView;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private LinearLayout ll_kong;
    private MyAdapter myAdapter;
    private List<MyCode_fensi.ObjBean> fensi_list = new ArrayList<>();
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fensi_fragment, container, false);

        mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) rootView.findViewById(R.id.refresh_and_load_more);
        ll_kong = (LinearLayout) rootView.findViewById(R.id.ll_kong);

        initData();
        page = 1;
        inithttp_data(page,new Other_GuanZhu_Activity().uid);
        return rootView;
    }


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
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshAndLoadMoreView.setRefreshing(false);
                    }
                }, 500);

            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //  loadData(pageIndex + 1);
                page += 1;
                inithttp_data(page,new Other_GuanZhu_Activity().uid);
            }
        });
        //   mLoadMoreListView.setOnItemClickListener(new ItemClickListener());
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fensi_list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.fensi_item, null);
            }
            ImageView iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
            TextView tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            TextView tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
            Picasso.with(getActivity()).load(Data_Util.IMG+fensi_list.get(position).getHeadImg()).into(iv_head);
            tv_nickname.setText(fensi_list.get(position).getNickname());

            if ((fensi_list.get(position).getFromId() + "").equals(SPUtils.getString(getActivity(), "uid"))) {
                tv_guanzhu.setVisibility(View.INVISIBLE);
            } else {
                if (fensi_list.get(position).isCollected()) {
                    tv_guanzhu.setVisibility(View.VISIBLE);
                    tv_guanzhu.setText("已关注");
                    tv_guanzhu.setTextColor(Color.parseColor("#d9d9d9"));
                    tv_guanzhu.setBackgroundResource(R.drawable.custom_hui);
                    tv_guanzhu.setEnabled(false);
                } else {
                    tv_guanzhu.setVisibility(View.VISIBLE);
                    tv_guanzhu.setEnabled(true);
                    tv_guanzhu.setText("+ 关注");
                    tv_guanzhu.setTextColor(Color.parseColor("#ffffff"));
                    tv_guanzhu.setBackgroundResource(R.drawable.custom_lv);
                }

            }


            iv_head.setTag(position);
            iv_head.setOnClickListener(new MyonclickListenr());
            tv_nickname.setTag(position);
            tv_nickname.setOnClickListener(new MyonclickListenr());
            tv_guanzhu.setTag(position);
            tv_guanzhu.setOnClickListener(new MyonclickListenr());

            return convertView;
        }

    }

    class MyonclickListenr implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_head:
                    int i = (int) v.getTag();
                    Intent intent = new Intent(getActivity(), Other_Activity.class);
                    intent.putExtra("uid", fensi_list.get(i).getFromId());
                    startActivity(intent);
                    break;
                case R.id.tv_nickname:
                    int i2 = (int) v.getTag();
                    Intent intent2 = new Intent(getActivity(), Other_Activity.class);
                    intent2.putExtra("uid", fensi_list.get(i2).getFromId());
                    startActivity(intent2);
                    break;
                case R.id.tv_guanzhu:
                    int position = (int) v.getTag();
                    inithttp_guanzhu(fensi_list.get(position).getFromId(), SPUtils.getString(getActivity(), "token"), position);
                    break;
            }
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
                            for (int i = 0; i < new Home_Fragment().list_data.size(); i++) {
                                if (new Home_Fragment().list_data.get(i).getUid() == uid) {
                                    new Home_Fragment().list_data.get(i).setFollowed(true);
                                }
                            }
                            if (new Me_Fragment().mycodes != null) {
                                new Me_Fragment().mycodes.getObj().setFollowCount(new Me_Fragment().mycodes.getObj().getFollowCount() + 1);
                            }

                            fensi_list.get(position).setCollected(true);
                            myAdapter.notifyDataSetChanged();

                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
                        }
                    }
                });
            }
        });
    }

    Handler h = new Handler();

    private void inithttp_data(final int page, int uid) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"page\":{\"page\":" + page + "},\"obj\":{\"uid\":" + uid + "},\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!LoadFansUser.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //当加载完成之后设置此时不在刷新状态
                        mRefreshAndLoadMoreView.setRefreshing(false);
                        mLoadMoreListView.onLoadComplete();
                        MyToast.showToast(getActivity(), "连接服务器失败");
                    }
                }, 500);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();


                Gson gson = new Gson();
                final MyCode_fensi mycode = gson.fromJson(res, MyCode_fensi.class);

                if (mycode.getCode() == 0) {
                    if (page == 1) {
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fensi_list = mycode.getObj();

                                if (fensi_list == null || fensi_list.size() == 0) {
                                    ll_kong.setVisibility(View.VISIBLE);
                                    mLoadMoreListView.setVisibility(View.INVISIBLE);
                                } else {
                                    ll_kong.setVisibility(View.INVISIBLE);
                                    mLoadMoreListView.setVisibility(View.VISIBLE);
                                    myAdapter = new MyAdapter();
                                    mLoadMoreListView.setAdapter(myAdapter);
                                    if (fensi_list.size() < 10) {
                                        mRefreshAndLoadMoreView.setRefreshing(false);
                                        mLoadMoreListView.onLoadComplete();
                                        mLoadMoreListView.setHaveMoreData(false);
                                    } else {
                                        mRefreshAndLoadMoreView.setRefreshing(false);
                                        mLoadMoreListView.onLoadComplete();
                                        mLoadMoreListView.setHaveMoreData(true);
                                    }
                                }

                            }
                        }, 500);
                    } else {
                        final List<MyCode_fensi.ObjBean> obj = mycode.getObj();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (obj != null && obj.size() != 0) {
                                    fensi_list.addAll(obj);

                                    myAdapter.notifyDataSetChanged();
                                    //当加载完成之后设置此时不在刷新状态
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.setHaveMoreData(true);
                                    mLoadMoreListView.onLoadComplete();

                                } else {
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                    mLoadMoreListView.setHaveMoreData(false);
                                    MyToast.showToast(getActivity(), "暂无更多数据");
                                }

                            }
                        }, 500);
                    }
                }
            }
        });
    }

}
