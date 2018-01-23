package com.aou.cheba.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment.Home_Fragment;
import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_uplist;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
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
 * Created by Administrator on 2016/12/8.
 */
public class Love_Activity extends SwipeBackActivity {

    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private int page = 1;
    private long id;
    private MyAdapter myAdapter;
    private List<MyCode_uplist.ObjBean> list_love = new ArrayList<>();
    private ImageView finish;
    private RelativeLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Love_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Love_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }


        finish = (ImageView) findViewById(R.id.iv_finish);
        finish.setOnClickListener(new MyonclickListenr());

        id = getIntent().getLongExtra("id", 0);

        initData();
        page = 1;
        inithttp_data(page, id);
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
                inithttp_data(page, id);
            }
        });
        //   mLoadMoreListView.setOnItemClickListener(new ItemClickListener());
    }

    Handler h = new Handler();

    private void inithttp_data(final int page, long l) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"page\":{\"page\":" + page + "},\"obj\":{\"id\":" + l + "},\"token\":\"" + SPUtils.getString(Love_Activity.this, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Server!LoadUp.action")
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
                        MyToast.showToast(Love_Activity.this, "连接服务器失败");
                    }
                }, 500);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();


                Gson gson = new Gson();
                final MyCode_uplist mycode = gson.fromJson(res, MyCode_uplist.class);

                if (mycode.getCode() == 0) {
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (page == 1) {
                                list_love = mycode.getObj();

                                myAdapter = new MyAdapter();
                                mLoadMoreListView.setAdapter(myAdapter);
                                if (list_love.size() < 15) {
                                    mLoadMoreListView.onLoadComplete();
                                    mLoadMoreListView.setHaveMoreData(false);
                                } else {
                                    mLoadMoreListView.onLoadComplete();
                                }
                            } else {
                                final List<MyCode_uplist.ObjBean> obj = mycode.getObj();

                                if (obj != null && obj.size() != 0) {
                                    list_love.addAll(obj);

                                    myAdapter.notifyDataSetChanged();
                                    //当加载完成之后设置此时不在刷新状态
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.setHaveMoreData(true);
                                    mLoadMoreListView.onLoadComplete();
                                } else {
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                    mLoadMoreListView.setHaveMoreData(false);
                                    MyToast.showToast(Love_Activity.this, "暂无更多数据");
                                }
                            }
                        }
                    }, 500);

                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list_love.size();
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
                convertView = View.inflate(Love_Activity.this, R.layout.guanzhu_item, null);
            }
            ImageView iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
            TextView tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            TextView tv_guanzhu = (TextView) convertView.findViewById(R.id.tv_guanzhu);
            Picasso.with(Love_Activity.this).load(list_love.get(position).getHeadImg()).into(iv_head);
            tv_nickname.setText(list_love.get(position).getNickname());

            if ((list_love.get(position).getUid() + "").equals(SPUtils.getString(Love_Activity.this, "uid"))) {
                tv_guanzhu.setVisibility(View.INVISIBLE);
            } else {
                if (list_love.get(position).isFollowed()) {
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
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.iv_head:
                    int i = (int) v.getTag();
                    Intent intent = new Intent(Love_Activity.this, Other_Activity.class);
                    intent.putExtra("uid", list_love.get(i).getUid());
                    startActivity(intent);
                    break;
                case R.id.tv_nickname:
                    int i2 = (int) v.getTag();
                    Intent intent2 = new Intent(Love_Activity.this, Other_Activity.class);
                    intent2.putExtra("uid", list_love.get(i2).getUid());
                    startActivity(intent2);
                    break;
                case R.id.tv_guanzhu:
                    int position = (int) v.getTag();
                    inithttp_guanzhu(list_love.get(position).getUid(), SPUtils.getString(Love_Activity.this, "token"), position);
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
                        MyToast.showToast(Love_Activity.this, "连接服务器失败");
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

                            list_love.get(position).setFollowed(true);
                            myAdapter.notifyDataSetChanged();

                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(Love_Activity.this, "token", "");
                            MyToast.showToast(Love_Activity.this, "请先登录");
                        }
                    }
                });
            }
        });
    }

}
