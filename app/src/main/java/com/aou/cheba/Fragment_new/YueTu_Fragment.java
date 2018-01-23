package com.aou.cheba.Fragment_new;

import android.content.Intent;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.Search_Activity;
import com.aou.cheba.Activity.YueTu_Activity;
import com.aou.cheba.R;
import com.aou.cheba.Service.LocationService;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreGrideView;
import com.aou.cheba.view.MoreWindow;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.ProgressBarCircularIndeterminate;
import com.aou.cheba.view.RefreshAndLoadMore_GrideView;
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
public class YueTu_Fragment extends Fragment {
    private View rootView;
    private LoadMoreGrideView mLoadMoreListView;
    private RefreshAndLoadMore_GrideView mRefreshAndLoadMoreView;
    private MyAdapter myAdapter;
    public static List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private int page = 1;
    private RelativeLayout rl_denglu;
    private ImageView iv_search;
    private TextView tv_search;
//    private ImageView iv_jia;
    private LocationService locationService;
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.yuetu_fragment, container, false);
            mLoadMoreListView = (LoadMoreGrideView) rootView.findViewById(R.id.load_more_list);
            mRefreshAndLoadMoreView = (RefreshAndLoadMore_GrideView) rootView.findViewById(R.id.refresh_and_load_more);
            progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.progressBarCircularIndetermininate);

            rl_denglu = (RelativeLayout) rootView.findViewById(R.id.ll_home);
            iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
            tv_search = ((TextView) rootView.findViewById(R.id.tv_search));
//            iv_jia = ((ImageView) rootView.findViewById(R.id.iv_jia));
            iv_search.setOnClickListener(new MyOnClickListener());
//            iv_jia.setOnClickListener(new MyOnClickListener());
            tv_search.setOnClickListener(new MyOnClickListener());

            if (Build.VERSION.SDK_INT >= 19) {
                ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
                vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * Data_Util.HEAD_NEW);
                rl_denglu.setLayoutParams(vParams1);
            } else {
                ViewGroup.LayoutParams vParams1 = rl_denglu.getLayoutParams();
                vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * Data_Util.HEAD_OLD);
                rl_denglu.setLayoutParams(vParams1);
            }

            initData();
            inithttp_data(Long.MAX_VALUE, page);
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
                page = 1;
                inithttp_data(Long.MAX_VALUE, page);

            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreGrideView.OnLoadMoreListener() {
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
                    Intent intent = new Intent(getActivity(), YueTu_Activity.class);
                    intent.putExtra("ser", objBean);
                    intent.putExtra("position", position);
                    startActivity(intent);
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
                .url(Data_Util.HttPHEAD + "/Carbar/Yuetu!Load.action")
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
                        progressBarCircularIndetermininate.setVisibility(View.GONE);
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
                            progressBarCircularIndetermininate.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
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
            ViewHolderRightText holderRightText = null;

            if (convertView == null) {
                holderRightText = new ViewHolderRightText();
                convertView = View.inflate(getActivity(), R.layout.yuetu_item, null);
                holderRightText.iv1 = (ImageView) convertView.findViewById(R.id.iv_datu);
                holderRightText.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holderRightText.content = (TextView) convertView.findViewById(R.id.tv_name);
                holderRightText.tv_ping_num = (TextView) convertView.findViewById(R.id.pl_num);

                convertView.setTag(holderRightText);
            }
            holderRightText = (ViewHolderRightText) convertView.getTag();

            ViewGroup.LayoutParams vParam4s = holderRightText.iv1.getLayoutParams();
            vParam4s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.28);
            holderRightText.iv1.setLayoutParams(vParam4s);

            String[] split2 = list_data.get(position).getPictrue().split(",");
            Glide.with(getActivity()).load(Data_Util.IMG+split2[0]).centerCrop().into(holderRightText.iv1);

            holderRightText.content.setText(list_data.get(position).getTitle());
            holderRightText.tv_ping_num.setText(list_data.get(position).getCommentNum()+"");
            holderRightText.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));

            return convertView;
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
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
/*                case R.id.iv_jia:
//                    MyToast.showToast(getActivity(),"发表");

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
                    break;*/
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 55) {
            myAdapter.notifyDataSetChanged();
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

    class ViewHolderRightText {
        private ImageView iv1;
        private TextView tv_time;
        private TextView content;
        private TextView tv_ping_num;
    }
    public void YTscoll(){
        mLoadMoreListView.smoothScrollToPositionFromTop(0,0);
    }
}
