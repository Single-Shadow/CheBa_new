package com.aou.cheba.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.bean.MyCode_meirong;
import com.aou.cheba.bean.MyCode_shoucang_max;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.aou.cheba.view.SweetAlertDialog;
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
 * Created by Administrator on 2016/11/14.
 */
public class Shoucang extends SwipeBackActivity {

    List<MyCode_shoucang_max.ObjBean> list_shoucang = new ArrayList<>();
    private MyListView lv;
    private int rows;
    private int page = 1;
    private ImageView finish;
    private LinearLayout footer;
    private LinearLayout footer2;
    private MyAdapter myAdapter;
    private ImageView tv_shafa;
    private Handler h = new Handler();
    private boolean isenter = false;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private RelativeLayout ll_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoucang);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        finish = (ImageView) findViewById(R.id.iv_finish);
        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);
        tv_shafa = (ImageView) findViewById(R.id.tv_tishi);
        ll_home = (RelativeLayout) findViewById(R.id.rl_xiugai_mima);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Shoucang.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Shoucang.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }

        finish.setOnClickListener(new MyOnClicklistener());


        inithttp_data(SPUtils.getString(Shoucang.this, "token"), 1);
    }

    Handler handler = new Handler();

    private void initAdapter() {
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

                    if (position != list_shoucang.size()) {
                        if (list_shoucang.get(position).getScore() > 0) {
                            MyCode_meirong.ObjBean objBean = new MyCode_meirong.ObjBean();
                            objBean.setCollected(list_shoucang.get(position).isCollected());
                            objBean.setUped(list_shoucang.get(position).isUped());
                            objBean.setPictrue(list_shoucang.get(position).getPictrue());
                            objBean.setAdd_time(list_shoucang.get(position).getAddtime());
                            objBean.setArea(list_shoucang.get(position).getArea());
                            objBean.setCity(list_shoucang.get(position).getCity());
                            objBean.setFw(list_shoucang.get(position).getFw());
                            objBean.setHj(list_shoucang.get(position).getHj());
                            objBean.setId(list_shoucang.get(position).getId());
                            objBean.setJs(list_shoucang.get(position).getJs());
                            objBean.setRegion(list_shoucang.get(position).getRegion());
                            objBean.setScore(list_shoucang.get(position).getScore());
                            objBean.setCommentNum(list_shoucang.get(position).getCommentNum());
                            objBean.setStore_addr(list_shoucang.get(position).getStore_addr());
                            objBean.setStore_name(list_shoucang.get(position).getStore_name());
                            objBean.setStore_tel(list_shoucang.get(position).getStore_tel());
                            objBean.setWorking_time(list_shoucang.get(position).getWorking_time());
                            objBean.setStore_type(list_shoucang.get(position).getStore_type());
                                 //   list_meirong.get(position);
                            Intent intent = new Intent(Shoucang.this, MeiRong_xiangqiang_Activity.class);
                            intent.putExtra("position", -1);
                            intent.putExtra("objBean", objBean);
                            startActivity(intent);
                        } else {
                            MyCode_data.ObjBean objBean = new MyCode_data.ObjBean();
                            objBean.setCollected(list_shoucang.get(position).isCollected());
                            objBean.setUped(list_shoucang.get(position).isUped());
                            objBean.setUpCount(list_shoucang.get(position).getUpCount());
                            objBean.setAddtime(list_shoucang.get(position).getAddtime());
                            objBean.setCommentNum(list_shoucang.get(position).getCommentNum());
                            objBean.setContent(list_shoucang.get(position).getContent());
                            objBean.setFollowed(list_shoucang.get(position).isFollowed());
                            objBean.setGender(list_shoucang.get(position).getGender());
                            objBean.setHeadImg(list_shoucang.get(position).getHeadImg());
                            objBean.setId(list_shoucang.get(position).getId());
                            objBean.setLocation(list_shoucang.get(position).getLocation());
                            objBean.setNickname(list_shoucang.get(position).getNickname());
                            objBean.setPictrue(list_shoucang.get(position).getPictrue());
                            objBean.setTemplate(list_shoucang.get(position).getTemplate());
                            objBean.setType(list_shoucang.get(position).getType());
                            objBean.setTitle(list_shoucang.get(position).getTitle());
                            objBean.setUid(list_shoucang.get(position).getUid());

                            if (objBean.getType()==9&&(objBean.getTitle()!=null&&objBean.getTitle().length()!=0)){
                                Intent intent = new Intent(Shoucang.this, HuaTi_WenDa_Activity.class);
                                intent.putExtra("position", position);
                                intent.putExtra("ser", objBean);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(Shoucang.this, HuaTi_Activity.class);
                                intent.putExtra("position", -1);
                                intent.putExtra("ser", objBean);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        });
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
                inithttp_data(SPUtils.getString(Shoucang.this, "token"), page);
            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page += 1;
                inithttp_data(SPUtils.getString(Shoucang.this, "token"), page);
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_shoucang.size();
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
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(Shoucang.this, R.layout.shoucang_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                viewHolder.iv_love = (ImageView) convertView.findViewById(R.id.iv_love);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tv_biaoti = (TextView) convertView.findViewById(R.id.tv_biaoti);
                viewHolder.tv_love = (TextView) convertView.findViewById(R.id.tv_love);
                viewHolder.tv_yue = (TextView) convertView.findViewById(R.id.tv_yue);
                viewHolder.tv_ri = (TextView) convertView.findViewById(R.id.tv_ri);
                viewHolder.tv_xian = (TextView) convertView.findViewById(R.id.tv_xian);
                viewHolder.rl_hige = (RelativeLayout) convertView.findViewById(R.id.rl_hige);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            if (list_shoucang.get(position).getPictrue() != null && !TextUtils.isEmpty(list_shoucang.get(position).getPictrue())) {
                viewHolder.iv_image.setVisibility(View.VISIBLE);
                Glide.with(Shoucang.this).load(Data_Util.IMG+list_shoucang.get(position).getPictrue().split(",")[0]).into(viewHolder.iv_image);
                viewHolder.tv_content.setText("");
            } else {
                if (list_shoucang.get(position).getContent() != null && list_shoucang.get(position).getContent().length() > 33) {
                    viewHolder.tv_content.setText(list_shoucang.get(position).getContent().substring(33, list_shoucang.get(position).getContent().length()));
                } else {
                    viewHolder.tv_content.setText("");
                }
                viewHolder.iv_image.setVisibility(View.GONE);
            }

            String yue = TimeUtil.getDateTimeFromMillisecond3(list_shoucang.get(position).getAddtime());
            if (yue.substring(0, 1).equals("0")) {
                viewHolder.tv_yue.setText(yue.substring(1, 2) + " 月");
            } else {
                viewHolder.tv_yue.setText(yue + " 月");
            }

            //收藏的  类型区分
            if (list_shoucang.get(position).getScore() > 0) {
                viewHolder.tv_love.setText(list_shoucang.get(position).getCommentNum() + "");
            } else {
                viewHolder.tv_love.setText(list_shoucang.get(position).getUpCount() + "");
            }
            if (list_shoucang.get(position).getScore() > 0) {
                viewHolder.tv_biaoti.setText(list_shoucang.get(position).getStore_name());
            } else {
                viewHolder.tv_biaoti.setText(list_shoucang.get(position).getTitle());
            }


            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            viewHolder.rl_hige.measure(w, h);
            int height = viewHolder.rl_hige.getMeasuredHeight();
            //获取当前控件的布局对象
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.tv_xian.getLayoutParams();
            params.height = height;//设置当前控件布局的高度
            viewHolder.tv_xian.setLayoutParams(params);

            if (position == list_shoucang.size() - 1) {
                viewHolder.tv_xian.setVisibility(View.GONE);
            } else {
                viewHolder.tv_xian.setVisibility(View.VISIBLE);
            }

            viewHolder.iv_delete.setTag(position);
            viewHolder.iv_delete.setOnClickListener(new MyOnClicklistener());
            return convertView;
        }
    }

    class MyOnClicklistener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.iv_delete:
                    new SweetAlertDialog(Shoucang.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("是否删除该话题")
                            .setCustomImage(R.mipmap.dialog)
                            .setCancelText("取消")
                            .setConfirmText("删除")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    int tag = (int) v.getTag();
                                    inithttp_delshoucang(list_shoucang.get(tag).getId(), SPUtils.getString(Shoucang.this, "token"), tag);

                                    sDialog.setTitleText("已删除")
                                            //.setContentText("图片已删除！")
                                            .setConfirmText("确定")
                                            .showCancelButton(false)
                                            .setCancelClickListener(null)
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            }).show();
                    break;
            }
        }
    }

    private void inithttp_data(String token, final int page) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        rows = 15;
        RequestBody requestBody = RequestBody.create(JSON, "{\"page\": { \"page\": " + page + ",\"rows\": " + rows + "},\"token\": \"" + token + "\"}");
//创建一个请求对象

        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!LoadCollection.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //当加载完成之后设置此时不在刷新状态
                        mRefreshAndLoadMoreView.setRefreshing(false);
                        mLoadMoreListView.onLoadComplete();
                        MyToast.showToast(Shoucang.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                final MyCode_shoucang_max mycode = gson.fromJson(res, MyCode_shoucang_max.class);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            if (page == 1) {
                                list_shoucang.clear();
                                list_shoucang = mycode.getObj();

                                if (list_shoucang == null || list_shoucang.size() == 0) {
                                    mLoadMoreListView.setVisibility(View.INVISIBLE);
                                    tv_shafa.setVisibility(View.VISIBLE);
                                } else {
                                    mLoadMoreListView.setVisibility(View.VISIBLE);
                                    tv_shafa.setVisibility(View.INVISIBLE);
                                    if (list_shoucang.size() < 14) {
                                        mLoadMoreListView.setHaveMoreData(false);
                                    } else {
                                        mLoadMoreListView.setHaveMoreData(true);
                                    }
                                }

                                if (myAdapter == null) {
                                    initData();
                                    initAdapter();
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }

                                //当加载完成之后设置此时不在刷新状态
                                mRefreshAndLoadMoreView.setRefreshing(false);
                                mLoadMoreListView.onLoadComplete();
                            } else {
                                List<MyCode_shoucang_max.ObjBean> obj = mycode.getObj();
                                if (obj != null && obj.size() != 0) {
                                    list_shoucang.addAll(obj);
                                    myAdapter.notifyDataSetChanged();
                                    if (obj.size() < 10) {
                                        mLoadMoreListView.setHaveMoreData(false);
                                    } else {
                                        mLoadMoreListView.setHaveMoreData(true);
                                    }
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                } else {
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                    mLoadMoreListView.setHaveMoreData(false);
                                }
                            }
                        } else {
                            MyToast.showToast(Shoucang.this, "刷新失败");
                        }
                    }
                }, 500);
            }
        });
    }

    private void inithttp_delshoucang(final long did, String token, final int position) {
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
                        MyToast.showToast(Shoucang.this, "连接服务器失败");
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
                            list_shoucang.remove(position);
                            myAdapter.notifyDataSetChanged();

                         //   删除收藏主界面没有更新
                         /*   for (MyCode_data.ObjBean objBean : new Home_Fragment().list_data) {
                                if (objBean.equals(did)) {

                                }
                                objBean.getId().
                            }*/
                        }
                    }
                });
            }
        });
    }

    static class ViewHolder {
        RelativeLayout rl_hige;
        TextView tv_content;
        TextView tv_xian;
        TextView tv_biaoti;
        TextView tv_yue;
        TextView tv_ri;
        TextView tv_love;
        ImageView iv_image;
        ImageView iv_delete;
        ImageView iv_love;

    }
}
