package com.aou.cheba.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.aou.cheba.view.Tag;
import com.aou.cheba.view.TagListView;
import com.aou.cheba.view.TagView;
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
 * Created by Administrator on 2016/11/15.
 */
public class Search_Activity extends SwipeBackActivity {

    private Button search;
    private EditText content;
    private int page = 1;
    private MyAdapter myAdapter;
    public static List<MyCode_data.ObjBean> list_search = new ArrayList<>();
    private ImageView finish;
    private TextView tv_shafa;
    private TagListView mTagListView;
    private List<Tag> mTags = new ArrayList<Tag>();
    private List<String> titles = new ArrayList<>();

    // private String[] titles = new String[9];
    private LinearLayout ll_tag;
    private RelativeLayout rl_biaoti;
    private Handler h = new Handler();
    private boolean isenter = false;
    private boolean islukuang;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        islukuang = getIntent().getBooleanExtra("islukuang", false);

        search = (Button) findViewById(R.id.bt_search);
        content = (EditText) findViewById(R.id.tv_content);
        finish = (ImageView) findViewById(R.id.iv_finish);
        tv_shafa = (TextView) findViewById(R.id.tv_tishi);
        ll_tag = (LinearLayout) findViewById(R.id.ll_tag);
        rl_biaoti = (RelativeLayout) findViewById(R.id.rl_biaoti);
        mTagListView = (TagListView) findViewById(R.id.tagview);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = rl_biaoti.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Search_Activity.this) * 0.08);
            rl_biaoti.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = rl_biaoti.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Search_Activity.this) * 0.05);
            rl_biaoti.setLayoutParams(vParams1);
        }

        String fankui_st = SPUtils.getString(Search_Activity.this, "search");

        if (!TextUtils.isEmpty(fankui_st)) {
            String[] split_up = fankui_st.split(",,;");
            for (String s : split_up) {
                titles.add(0, s);
            }
        }

        if (titles.size() == 0) {

        } else {
            titles.add("删除");
        }

        setUpData();
        mTagListView.setTags(mTags);
        mTagListView.setOnTagClickListener(new TagListView.OnTagClickListener() {

            @Override
            public void onTagClick(TagView tagView, Tag tag) {
                // TODO Auto-generated method stub
                if (tag.getId() == titles.size() - 1) {
                    SPUtils.put(Search_Activity.this, "search", "");
                    titles.clear();
                    ll_tag.setVisibility(View.GONE);
                } else {
                    ll_tag.setVisibility(View.GONE);
                    list_search.clear();
                    content.setText(tag.getTitle());
                    page = 1;
                    inithttp_data(tag.getTitle(), Long.MAX_VALUE);
                }
            }
        });
        mTagListView.setOnDelClickListener(new TagListView.OnDelClickListener() {

            @Override
            public void onDelClick(TextView textView, Tag tag) {
                // TODO Auto-generated method stub
                if (tag.getId() != titles.size() - 1) {
                    mTags.clear();
                    titles.remove(tag.getId());
                    String s_search = "";
                    for (int i = titles.size() - 2; i >= 0; i--) {
                        if (TextUtils.isEmpty(s_search)) {
                            s_search = titles.get(i);
                        } else {
                            s_search = s_search + ",,;" + titles.get(i);
                        }
                    }
                    SPUtils.put(Search_Activity.this, "search", s_search);
                    if (titles.size() == 1) {
                        ll_tag.setVisibility(View.GONE);
                    } else {
                        setUpData();
                        mTagListView.setTags(mTags);
                    }
                }
            }
        });

        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);


        finish.setOnClickListener(new MyOnClicklistener());
        search.setOnClickListener(new MyOnClicklistener());

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

                    if (position != list_search.size()) {
                        if (list_search.get(position).getType()==9){
                            MyCode_data.ObjBean objBean = list_search.get(position);
                            Intent intent = new Intent(Search_Activity.this, HuaTi_WenDa_Activity.class);
                            intent.putExtra("position", -1);
                            intent.putExtra("ser", objBean);
                            startActivity(intent);
                        }else if (list_search.get(position).getType()==8){
                            //悦图
                            MyCode_data.ObjBean objBean = list_search.get(position);
                            Intent intent = new Intent(Search_Activity.this, YueTu_Activity.class);
                            intent.putExtra("position", -1);
                            intent.putExtra("ser", objBean);
                            startActivity(intent);
                        }else{
                            MyCode_data.ObjBean objBean = list_search.get(position);
                            Intent intent = new Intent(Search_Activity.this, HuaTi_Activity.class);
                            intent.putExtra("position", -1);
                            intent.putExtra("ser", objBean);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private void setUpData() {
        for (int i = 0; i < titles.size(); i++) {
            Tag tag = new Tag();
            tag.setId(i);
            if (i == titles.size() - 1) {
                tag.setShowDel(false);
            }
            tag.setTitle(titles.get(i));
            mTags.add(tag);
        }
    }

    private void initAdapter() {
        //程序开始就加载第一页数据
        //    loadData(1);
        mRefreshAndLoadMoreView.setLoadMoreListView(mLoadMoreListView);
        mLoadMoreListView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreView);
        mRefreshAndLoadMoreView.setEnabled(false);//设置禁止下拉刷新

        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page = 2;
                key = content.getText().toString().trim();
                inithttp_data(key, list_search.get(list_search.size() - 1).getAddtime());
            }
        });

        myAdapter = new MyAdapter();
        mLoadMoreListView.setAdapter(myAdapter);
        if (list_search.size() == 0) {
            tv_shafa.setVisibility(View.VISIBLE);
            mLoadMoreListView.setVisibility(View.INVISIBLE);
            mRefreshAndLoadMoreView.setVisibility(View.INVISIBLE);
            mRefreshAndLoadMoreView.setRefreshing(false);
            mLoadMoreListView.onLoadComplete();
            mLoadMoreListView.setHaveMoreData(false);
        } else {
            tv_shafa.setVisibility(View.INVISIBLE);
            mLoadMoreListView.setVisibility(View.VISIBLE);
            mRefreshAndLoadMoreView.setVisibility(View.VISIBLE);
            mRefreshAndLoadMoreView.setRefreshing(false);
            mLoadMoreListView.onLoadComplete();
            mLoadMoreListView.setHaveMoreData(true);
        }

    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_search.size();
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
                convertView = View.inflate(Search_Activity.this, R.layout.item5, null);
                viewHolder = new ViewHolder();
                viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                viewHolder.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                viewHolder.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();

            String[] split5 = list_search.get(position).getPictrue().split(",");
            ViewGroup.LayoutParams vParam5s = viewHolder.ll_iv.getLayoutParams();
            vParam5s.height = (int) (DisplayUtil.getMobileHeight(Search_Activity.this) * 0.12);
            viewHolder.ll_iv.setLayoutParams(vParam5s);

            if (split5.length != 0) {
                Glide.with(Search_Activity.this).load(Data_Util.IMG+split5[0]).into(viewHolder.iv);
            }
            switch (list_search.get(position).getType()) {
                case 5:
                    viewHolder.tv_type.setText("资讯");
                    break;
                case 6:
                    viewHolder.tv_type.setText("车友圈");
                    break;
                case 7:
                    viewHolder.tv_type.setText("晒车");
                    break;
                case 8:
                    viewHolder.tv_type.setText("悦图");
                    break;
                case 9:
                    viewHolder.tv_type.setText("问答");
                    break;
            }
            viewHolder.nickname.setText(list_search.get(position).getNickname());
            viewHolder.tv_time.setText(TimeUtil.getSpaceTime(list_search.get(position).getAddtime()));
            viewHolder.shuoshuo.setText(list_search.get(position).getTitle());
            viewHolder.tv_ping_num.setText(list_search.get(position).getCommentNum() + "评论");

            return convertView;
        }
    }

    boolean iscon = false;
    private String key;

    class MyOnClicklistener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.ll_footer:
                    page = 2;
                    key = content.getText().toString().trim();
                    inithttp_data(key, list_search.get(list_search.size() - 1).getAddtime());

                    break;
                case R.id.bt_search:
                    key = content.getText().toString().trim();
                    if (TextUtils.isEmpty(key)) {
                        MyToast.showToast(Search_Activity.this, "搜索内容为空");
                    } else {
                        mLoadMoreListView.setEnabled(false);
                        search.setEnabled(false);
                        ll_tag.setVisibility(View.INVISIBLE);

                        for (String title : titles) {
                            if (key.equals(title)) {
                                iscon = true;
                            }
                        }
                        if (!iscon) {
                            if (titles.size() <= 8) {
                                String ss = SPUtils.getString(Search_Activity.this, "search");
                                if (TextUtils.isEmpty(ss)) {
                                    SPUtils.put(Search_Activity.this, "search", key);
                                    titles.add(0, key);
                                } else {
                                    titles.add(0, key);
                                    SPUtils.put(Search_Activity.this, "search", ss + ",,;" + key);
                                }
                            } else {
                                String ss = SPUtils.getString(Search_Activity.this, "search");
                                String s = "";
                                if (!TextUtils.isEmpty(ss)) {
                                    String[] split_up = ss.split(",,;");

                                    for (int i = 0; i < split_up.length; i++) {
                                        if (i != 0) {
                                            if (TextUtils.isEmpty(s)) {
                                                s = split_up[i];
                                            } else {
                                                s = s + ",,;" + split_up[i];
                                            }
                                        }
                                    }

                                    titles.add(0, key);
                                    SPUtils.put(Search_Activity.this, "search", s + ",,;" + key);
                                }
                            }
                        }
                        page = 1;
                        inithttp_data(key, Long.MAX_VALUE);

                    }
                    break;
            }
        }
    }

    static class ViewHolder {
        private ImageView iv;
        private TextView nickname;
        private TextView tv_time;
        private TextView tv_type;
        private TextView shuoshuo;
        private TextView tv_ping_num;
        private LinearLayout ll_iv;
    }

    Handler handler = new Handler();

    private void inithttp_data(String s, final long time) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(Search_Activity.this, "token") + "\",\"page\":{\"id\":" + time + "},\"obj\":{\"title\":\"" + s + "\"}}");
//创建一个请求对象

        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Server!Search.action")
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
                        MyToast.showToast(Search_Activity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();
                Gson gson = new Gson();
                final MyCode_data mycode = gson.fromJson(res, MyCode_data.class);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            if (page == 1) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        list_search = mycode.getObj();

                                        if (myAdapter != null) {
                                            if (list_search.size() == 0) {
                                                tv_shafa.setVisibility(View.VISIBLE);
                                                mLoadMoreListView.setVisibility(View.INVISIBLE);
                                                mRefreshAndLoadMoreView.setVisibility(View.INVISIBLE);
                                                mRefreshAndLoadMoreView.setRefreshing(false);
                                                mLoadMoreListView.onLoadComplete();
                                                mLoadMoreListView.setHaveMoreData(false);
                                            } else {
                                                mLoadMoreListView.setVisibility(View.VISIBLE);
                                                mRefreshAndLoadMoreView.setVisibility(View.VISIBLE);
                                                mLoadMoreListView.setHaveMoreData(true);
                                                myAdapter.notifyDataSetChanged();
                                                //当加载完成之后设置此时不在刷新状态
                                                mRefreshAndLoadMoreView.setRefreshing(false);
                                                mLoadMoreListView.onLoadComplete();
                                                tv_shafa.setVisibility(View.INVISIBLE);
                                            }
                                        } else {
                                            initAdapter();
                                        }

                                        mLoadMoreListView.setEnabled(true);
                                        search.setEnabled(true);
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<MyCode_data.ObjBean> obj = mycode.getObj();
                                        if (obj.size() == 0) {
                                            mRefreshAndLoadMoreView.setRefreshing(false);
                                            mLoadMoreListView.onLoadComplete();
                                            mLoadMoreListView.setHaveMoreData(false);
                                            MyToast.showToast(Search_Activity.this, "暂无更多");
                                        } else {
                                            list_search.addAll(obj);

                                            mLoadMoreListView.setHaveMoreData(true);
                                            myAdapter.notifyDataSetChanged();
                                            //当加载完成之后设置此时不在刷新状态
                                            mRefreshAndLoadMoreView.setRefreshing(false);
                                            mLoadMoreListView.onLoadComplete();
                                        }
                                    }
                                });
                            }
                        } else {
                            mRefreshAndLoadMoreView.setRefreshing(false);
                            mLoadMoreListView.onLoadComplete();
                            mLoadMoreListView.setHaveMoreData(false);
                            MyToast.showToast(Search_Activity.this, "搜索失败");
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
