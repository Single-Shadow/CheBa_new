package com.aou.cheba.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aou.cheba.Activity.HuaTi_Activity;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreGrideView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMore_GrideView;
import com.aou.cheba.view.RollHeaderView;
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
public class New_zhuanqu extends Fragment {
    private View rootView;
    private LoadMoreGrideView mLoadMoreGrideView;
    private RefreshAndLoadMore_GrideView mRefreshAndLoadMoreGrideView;
    private MyGrideView myAdapter;
    List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private int page = 1;
    private RollHeaderView rh;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> list2 = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.new_zhaunqu, container, false);
            mLoadMoreGrideView = (LoadMoreGrideView) rootView.findViewById(R.id.load_more_list);
            mRefreshAndLoadMoreGrideView = (RefreshAndLoadMore_GrideView) rootView.findViewById(R.id.refresh_and_load_more);

            View inflate = View.inflate(getActivity(), R.layout.zixun_head, null);
            mLoadMoreGrideView.addHeaderView(inflate);
            rh = (RollHeaderView) inflate.findViewById(R.id.rh);

            list.add("http://images.ali213.net/picfile/pic/2013/02/04/927_110420232941-12.jpg");
            list.add("http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1207/18/c1/12378628_1342603613474.jpg");

            list2.add("最美不是下雨天");
            list2.add("一起躲雨的屋檐");
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

                    }

                    // startActivity(new Intent(getActivity(), WebView_activity.class));
                    //   Toast.makeText(getActivity(), "点击 : " + position, Toast.LENGTH_SHORT).show();
                }
            });

            initData();
            // inithttp_data1(page);
            inithttp_data(Long.MAX_VALUE, 1);
        }
        return rootView;
    }

    public static Handler h = new Handler();

    private void initData() {
        //程序开始就加载第一页数据
        //    loadData(1);
        mRefreshAndLoadMoreGrideView.setLoadMoreListView(mLoadMoreGrideView);
        mLoadMoreGrideView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreGrideView);
        //设置下拉刷新监听
        mRefreshAndLoadMoreGrideView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //     loadData(1);
                page = 1;
                inithttp_data(Long.MAX_VALUE, 1);
            }
        });
        //设置加载监听
        mLoadMoreGrideView.setOnLoadMoreListener(new LoadMoreGrideView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page += 1;
                inithttp_data(list_data.get(list_data.size() - 1).getId(), page);


                //  loadData(pageIndex + 1);
          /*      page += 1;
                inithttp_data(list_data.get(list_data.size() - 1).getId(), page);*/

            }
        });

        mLoadMoreGrideView.setSelection(0);
        mLoadMoreGrideView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
                    startActivityForResult(intent, 13);

                }
            }
        });
    }

    private boolean isenter = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 22) {
            myAdapter.notifyDataSetChanged();
            Log.i("test", "sdasd");
        }
    }

    class MyGrideView extends BaseAdapter {
        @Override
        public int getCount() {
            return list_data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolderTime holderTime = null;
            if (view == null) {
                holderTime = new ViewHolderTime();
                view = View.inflate(getActivity(), R.layout.new_zhaunqu_item, null);
                holderTime.iv_datu = (ImageView) view.findViewById(R.id.iv_datu);
                holderTime.tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(holderTime);
            }

            holderTime = (ViewHolderTime) view.getTag();
            String[] split = list_data.get(i).getPictrue().split(",");
            Glide.with(getActivity()).load(split[0]).into(holderTime.iv_datu);
            holderTime.tv_name.setText(list_data.get(i).getTitle());

            ViewGroup.LayoutParams vParams1 = holderTime.iv_datu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.18);
            holderTime.iv_datu.setLayoutParams(vParams1);

 /*           holderTime.pro.setProgress(list.get(i).getBuyNum() * 100 / list.get(i).getTotalNum());
            holderTime.tv_jindu.setText(list.get(i).getBuyNum() * 100 / list.get(i).getTotalNum() + "%");

            ViewGroup.LayoutParams vParams1 = holderTime.iv_datu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.19);
            holderTime.iv_datu.setLayoutParams(vParams1);
            Glide.with(getActivity()).load(list.get(i).getImg()).into(holderTime.iv_datu);

            holderTime.tv_name.setText(list.get(i).getGoodsName());*/

            return view;
        }
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
                        mRefreshAndLoadMoreGrideView.setRefreshing(false);
                        mLoadMoreGrideView.onLoadComplete();
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
                                    myAdapter = new MyGrideView();
                                    mLoadMoreGrideView.setAdapter(myAdapter);
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }

                                if (rows.size() < 15) {
                                    mLoadMoreGrideView.setHaveMoreData(false);
                                    myAdapter.notifyDataSetChanged();
                                    //当加载完成之后设置此时不在刷新状态
                                    mRefreshAndLoadMoreGrideView.setRefreshing(false);
                                    mLoadMoreGrideView.onLoadComplete();
                                } else {
                                    mLoadMoreGrideView.setHaveMoreData(true);
                                    myAdapter.notifyDataSetChanged();
                                    //当加载完成之后设置此时不在刷新状态
                                    mRefreshAndLoadMoreGrideView.setRefreshing(false);
                                    mLoadMoreGrideView.onLoadComplete();
                                }

                            } else {
                                mRefreshAndLoadMoreGrideView.setRefreshing(false);
                                mLoadMoreGrideView.onLoadComplete();
                                mLoadMoreGrideView.setHaveMoreData(false);
                                MyToast.showToast(getActivity(), "暂无更多数据");
                            }
                        } else {
                            mRefreshAndLoadMoreGrideView.setRefreshing(false);
                            mLoadMoreGrideView.onLoadComplete();
                        }
                    }
                });


            }
        });
    }

    class MyOnclikListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_finish:
                    getActivity().finish();
                    break;
            }
        }
    }

    class ViewHolderTime {
        private ImageView iv_datu;
        private TextView tv_name;
        private TextView tv_jindu;
        private TextView tv_duobao;
        private ProgressBar pro;
    }
}
