package com.aou.cheba.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode_News;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.bean.MyCode_huati;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MyToast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/23.
 */
public class News_Activity extends SwipeBackActivity {

    private ImageView finish;
    private ImageView tv_shafa;
    private MyAdapter myAdapter;
    private MyCode_News news_bean = new MyCode_News();
    private ListView mLoadMoreListView;
    private RelativeLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);


        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        finish = (ImageView) findViewById(R.id.iv_finish);
        mLoadMoreListView = (ListView) findViewById(R.id.load_more_list);
        tv_shafa = (ImageView) findViewById(R.id.tv_tishi);
        ll_home = (RelativeLayout) findViewById(R.id.rl_denglu);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(News_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(News_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }

        SPUtils.put(News_Activity.this, "newsnum" + SPUtils.getString(News_Activity.this, "uid"),0);
        TextView tv_news_num = new Me_Fragment().news_num;
        if (tv_news_num != null) {
            tv_news_num.setText("");
        }

        finish.setOnClickListener(new MyOnClicklistener());


        String news = SPUtils.getString(News_Activity.this, "news" + SPUtils.getString(News_Activity.this, "uid"));
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(news)) {
            try {
                news_bean = SerializeUtils.json2Object(news, MyCode_News.class);
            } catch (Exception e) {
                Log.d("test", "news 解析出错了" + e.getMessage());
                e.printStackTrace();
            }

        }
        // initData();
        if (news == null || news_bean.getData().size() == 0) {
            tv_shafa.setVisibility(View.VISIBLE);
        } else {
            tv_shafa.setVisibility(View.GONE);
            initAdapter();
        }
    }

    private Handler h = new Handler();

    private void initAdapter() {
        myAdapter = new MyAdapter();
        mLoadMoreListView.setAdapter(myAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return news_bean.getData().size();
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
            List<MyCode_News.DataBean> data = news_bean.getData();
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(News_Activity.this, R.layout.news_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                viewHolder.iv_news = (ImageView) convertView.findViewById(R.id.iv_news);
                viewHolder.iv_head = (CircleImageView) convertView.findViewById(R.id.iv_head);
                viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tv_biaoti = (TextView) convertView.findViewById(R.id.tv_biaoti);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_xian = (TextView) convertView.findViewById(R.id.tv_xian);
                viewHolder.rl_hige = (RelativeLayout) convertView.findViewById(R.id.rl_hige);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            if (data.get(position).getPictrue() != null && !TextUtils.isEmpty(data.get(position).getPictrue())) {
                Glide.with(News_Activity.this).load(data.get(position).getPictrue()).into(viewHolder.iv_image);
            } else {
                viewHolder.iv_image.setVisibility(View.GONE);
            }

            Date dat1 = new Date(data.get(position).getRecvTime());
            GregorianCalendar gc1 = new GregorianCalendar();
            gc1.setTime(dat1);
            java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm");
            String sb1 = format1.format(gc1.getTime());
            viewHolder.tv_time.setText(sb1);


            viewHolder.tv_biaoti.setText(data.get(position).getNickname());
            Glide.with(News_Activity.this).load(data.get(position).getHeadImg()).into(viewHolder.iv_head);
            viewHolder.tv_content.setText(data.get(position).getContent());
            switch (data.get(position).getType()) {
                case "1":
                    viewHolder.tv_type.setText("发表了新动态");
                    break;
                case "2":
                    viewHolder.tv_type.setText("回复了您");
                    break;
                case "3":
                    viewHolder.tv_type.setText("评论了您的话题");
                    break;
                case "4":
                    viewHolder.tv_type.setText("评论了你的收藏话题");
                    break;
            }

            if (position == data.size() - 1) {
                viewHolder.tv_xian.setVisibility(View.GONE);
            } else {
                viewHolder.tv_xian.setVisibility(View.VISIBLE);
            }

            viewHolder.rl_hige.setTag(position);
            viewHolder.rl_hige.setOnClickListener(new MyOnClicklistener());
            return convertView;
        }
    }

    class MyOnClicklistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.rl_hige:
                    int tag = (int) v.getTag();
                    inithttp_huati(SPUtils.getString(News_Activity.this, "token"), Long.parseLong(news_bean.getData().get(tag).getDid()));
                    break;
            }
        }
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
                        MyToast.showToast(News_Activity.this, "连接服务器失败");
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
                            if (!isenter) {
                                isenter = true;
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isenter = false;
                                    }
                                }, 500);
                                MyCode_huati.ObjBean obj = mycode.getObj();
                                MyCode_data.ObjBean objBean= new MyCode_data.ObjBean();
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

                                Intent intent = new Intent(News_Activity.this, HuaTi_Activity.class);
                                intent.putExtra("position", -1);
                                intent.putExtra("ser", objBean);
                                startActivity(intent);
                            }
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
        TextView tv_type;
        TextView tv_time;
        ImageView iv_image;
        ImageView iv_news;
        CircleImageView iv_head;

    }
}
