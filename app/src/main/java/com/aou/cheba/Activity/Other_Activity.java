package com.aou.cheba.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
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
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.bean.MyCode_otherInfo;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
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
 * Created by Administrator on 2016/12/8.
 */
public class Other_Activity extends SwipeBackActivity implements View.OnClickListener {

    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private ImageView iv_datu;
    private ImageView iv_head;
    private TextView tv_nickname;
    private TextView tv_feel;
    private TextView tv_guanzhu;
    private TextView tv_fensi;
    private LinearLayout ll_fensi;
    private LinearLayout ll_guaunzhu;
    private ImageView iv_gender;
    private TextView tv_level;
    private ImageView iv_finish;
    private MyCode_otherInfo.ObjBean other_head;
    private int uid;
    private List<MyCode_data.ObjBean> list_other = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        uid = getIntent().getIntExtra("uid", 0);
        findviewbyid();

        inithttp_head(uid, SPUtils.getString(Other_Activity.this, "token"));
        inithttp_lv(uid, SPUtils.getString(Other_Activity.this, "token"), Data_Util.MAXLONG);
    }

    private void findviewbyid() {

        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);
        iv_finish = (ImageView) findViewById(R.id.iv_finish);
        iv_finish.setOnClickListener(this);
    }

    private void gethead() {
        init_lv();
        View inflate = View.inflate(Other_Activity.this, R.layout.other_head, null);
        mLoadMoreListView.addHeaderView(inflate);

        initData();

        iv_datu = (ImageView) inflate.findViewById(R.id.iv_datu);
        iv_head = (ImageView) inflate.findViewById(R.id.iv_head);

        iv_gender = (ImageView) inflate.findViewById(R.id.iv_gender);
        tv_nickname = (TextView) inflate.findViewById(R.id.tv_nickname);
        tv_level = (TextView) inflate.findViewById(R.id.tv_level);
        tv_feel = (TextView) inflate.findViewById(R.id.tv_feel);
        tv_guanzhu = (TextView) inflate.findViewById(R.id.tv_guanzhu);
        tv_fensi = (TextView) inflate.findViewById(R.id.tv_fensi);
        ll_fensi = (LinearLayout) inflate.findViewById(R.id.ll_fensi);
        ll_guaunzhu = (LinearLayout) inflate.findViewById(R.id.ll_guaunzhu);


        ll_guaunzhu.setOnClickListener(this);
        ll_fensi.setOnClickListener(this);

        Glide.with(Other_Activity.this).load(Data_Util.IMG+other_head.getHeadImg()).into(iv_head);
        Glide.with(Other_Activity.this).load(Data_Util.IMG+other_head.getBgImg()).into(iv_datu);
        tv_nickname.setText(other_head.getNickname());
        if (TextUtils.isEmpty(other_head.getSignature())) {
            tv_feel.setText("此人很懒什么都没留下");
        } else {
            tv_feel.setText(other_head.getSignature());
        }
        tv_guanzhu.setText(other_head.getFollowCount() + "");
        tv_fensi.setText(other_head.getFansCount() + "");
        if (other_head.getGender() == 1) {
            iv_gender.setImageResource(R.mipmap.nan);
        } else {
            iv_gender.setImageResource(R.mipmap.nv);
        }
        tv_level.setText("LV:" + other_head.getLevel());

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
                inithttp_lv(uid, SPUtils.getString(Other_Activity.this, "token"), Data_Util.MAXLONG);
            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                inithttp_lv(uid, SPUtils.getString(Other_Activity.this, "token"), list_other.get(list_other.size()-1).getAddtime());
            }
        });
    }

    private boolean isenter = false;

    private void init_lv() {
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
                        MyCode_data.ObjBean objBean = list_other.get(position - 1);
                        Intent intent = new Intent(Other_Activity.this, HuaTi_Activity.class);
                        intent.putExtra("position", -1);
                        intent.putExtra("ser", objBean);
                        startActivityForResult(intent, 12);
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.ll_fensi:
                Intent intent = new Intent(Other_Activity.this, Other_GuanZhu_Activity.class);
                intent.putExtra("uid", other_head.getUid());
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            case R.id.ll_guaunzhu:
                Intent intent2 = new Intent(Other_Activity.this, Other_GuanZhu_Activity.class);
                intent2.putExtra("uid", other_head.getUid());
                intent2.putExtra("type",0);
                startActivity(intent2);
                break;
        }
    }


    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_other.size();
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
                convertView = View.inflate(Other_Activity.this, R.layout.other_new_item, null);
                viewHolder = new ViewHolder();
               /* viewHolder.fl = (RelativeLayout) convertView.findViewById(R.id.fl_datu);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.content);
                viewHolder.image_shu = (TextView) convertView.findViewById(R.id.image_shu);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.iv_datu = (ImageView) convertView.findViewById(R.id.iv_datu);*/

                viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tv_biaoti = (TextView) convertView.findViewById(R.id.tv_biaoti);
                viewHolder.tv_yue = (TextView) convertView.findViewById(R.id.tv_yue);
                viewHolder.tv_ri = (TextView) convertView.findViewById(R.id.tv_ri);
                viewHolder.tv_xian = (TextView) convertView.findViewById(R.id.tv_xian);
                viewHolder.rl_hige = (RelativeLayout) convertView.findViewById(R.id.rl_hige);


                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            if (list_other.get(position).getPictrue() != null && !TextUtils.isEmpty(list_other.get(position).getPictrue())) {
                viewHolder.iv_image.setVisibility(View.VISIBLE);
                Glide.with(Other_Activity.this).load(Data_Util.IMG+list_other.get(position).getPictrue().split(",")[0]).into(viewHolder.iv_image);
                viewHolder.tv_content.setText("");
            } else {
                viewHolder.tv_content.setText(list_other.get(position).getContent());
                viewHolder.iv_image.setVisibility(View.GONE);
            }
            viewHolder.tv_biaoti.setText(list_other.get(position).getTitle());
         /*   Date dat1 = new Date(list_other.get(position).getAddtime());
            GregorianCalendar gc1 = new GregorianCalendar();
            gc1.setTime(dat1);
            java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("MM-dd hh:mm:ss");
            String sb1 = format1.format(gc1.getTime());

            String substring = sb1.substring(0, 2);
            String s = Num_change.foematInteger(Integer.parseInt(substring));
            if (s.length() == 3) {
            }*/
            String yue = TimeUtil.getDateTimeFromMillisecond3(list_other.get(position).getAddtime());
            if (yue.substring(0, 1).equals("0")) {
                viewHolder.tv_yue.setText(yue.substring(1, 2) + " 月");
            } else {
                viewHolder.tv_yue.setText(yue + " 月");
            }

            viewHolder.tv_ri.setText(TimeUtil.getDateTimeFromMillisecond4(list_other.get(position).getAddtime()));


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

            if (position == list_other.size() - 1) {
                viewHolder.tv_xian.setVisibility(View.GONE);
            } else {
                viewHolder.tv_xian.setVisibility(View.VISIBLE);
            }


            return convertView;
        }
    }


    private void inithttp_head(int i, String s) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":" + i + ",\"token\":\"" + s + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!GetUserInfo.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(Other_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_otherInfo myCodeInfo = gson.fromJson(res, MyCode_otherInfo.class);

                if (myCodeInfo.getCode() == 0) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            other_head = myCodeInfo.getObj();
                            Log.i("test", other_head.getNickname());
                            if (other_head != null) {
                                gethead();
                            }
                        }
                    });
                }
            }
        });
    }

    private void inithttp_lv(int uid, String token, final Long addtime) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\": { \"addtime\": " + addtime + ",\"uid\":"+uid+"},\"token\": \"" + token + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!LoadMyData.action")
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
                        MyToast.showToast(Other_Activity.this, "连接服务器失败");
                    }
                }, 500);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCode_data myCodeInfo = gson.fromJson(res, MyCode_data.class);

                if (myCodeInfo.getCode() == 0) {
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (addtime == Data_Util.MAXLONG) {
                                list_other.clear();
                                list_other = myCodeInfo.getObj();
                                if (myAdapter == null) {
                                    init_lv();
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }

                                if (list_other.size() < 10) {
                                    mLoadMoreListView.setHaveMoreData(false);
                                } else {
                                    mLoadMoreListView.setHaveMoreData(true);
                                }
                                //当加载完成之后设置此时不在刷新状态
                                mRefreshAndLoadMoreView.setRefreshing(false);
                                mLoadMoreListView.onLoadComplete();
                            } else {
                                List<MyCode_data.ObjBean> obj = myCodeInfo.getObj();
                                if (obj != null && obj.size() != 0) {
                                    list_other.addAll(obj);
                                    myAdapter.notifyDataSetChanged();
                                    if (obj.size() < 10) {
                                        mLoadMoreListView.setHaveMoreData(false);
                                    }
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                } else {
                                    mRefreshAndLoadMoreView.setRefreshing(false);
                                    mLoadMoreListView.onLoadComplete();
                                    mLoadMoreListView.setHaveMoreData(false);
                                    MyToast.showToast(Other_Activity.this, "暂无更多数据");
                                }
                            }
                        }
                    }, 500);
                }
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
        ImageView iv_image;
    }
}
