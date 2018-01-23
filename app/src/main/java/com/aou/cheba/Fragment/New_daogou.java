package com.aou.cheba.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.HuaTi_Activity;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

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
 * Created by Administrator on 2017/6/1.
 */
public class New_daogou extends Fragment {
    private View rootView;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private MyAdapter myAdapter;
    List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.new_zixun, container, false);
            mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.load_more_list);
            mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) rootView.findViewById(R.id.refresh_and_load_more);
            initData();
            // inithttp_data1(page);
            inithttp_data(Long.MAX_VALUE, 1);
        }
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
                page = 1;
                inithttp_data(Long.MAX_VALUE, page);

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

    private boolean isenter = false;

    private void intView() {

        myAdapter = new MyAdapter();
        mLoadMoreListView.setAdapter(myAdapter);

        mLoadMoreListView.setSelection(0);
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

                    MyCode_data.ObjBean objBean = list_data.get(position);
                    Intent intent = new Intent(getActivity(), HuaTi_Activity.class);
                    intent.putExtra("position", position);
//                    intent.putExtra("type", "huati");
                    intent.putExtra("ser", objBean);
                    startActivityForResult(intent, 12);

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
            ViewHolderTime4 holderTime4 = null;

            if (convertView == null) {
                holderTime4 = new ViewHolderTime4();
                convertView = View.inflate(getActivity(), R.layout.new_daogou_item4, null);
                holderTime4.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                holderTime4.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holderTime4.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                holderTime4.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                holderTime4.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);

                convertView.setTag(holderTime4);
            }
            holderTime4 = (ViewHolderTime4) convertView.getTag();

            ViewGroup.LayoutParams vParam4s = holderTime4.ll_iv.getLayoutParams();
            vParam4s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.12);
            holderTime4.ll_iv.setLayoutParams(vParam4s);

            holderTime4.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));
            holderTime4.tv_ping_num.setText(list_data.get(position).getCommentNum() + "评论");
            holderTime4.shuoshuo.setText(list_data.get(position).getTitle());
            final String[] split4 = list_data.get(position).getPictrue().split(",");
            if (split4 != null || split4.length != 0) {
                Glide.with(getActivity()).load(split4[0]).centerCrop().into(holderTime4.iv);
            }
            return convertView;
        }
    }

    class ViewHolderTime4 {
        private ImageView iv;
        private TextView tv_time;
        private TextView shuoshuo;
        private TextView tv_ping_num;
        private LinearLayout ll_iv;
    }
}
