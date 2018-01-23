package com.aou.cheba.Fragment_new;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aou.cheba.Activity.HuaTi_WenDa_Activity;
import com.aou.cheba.Activity.Other_Activity;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.bumptech.glide.Glide;
import com.github.library.bubbleview.BubbleTextVew;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class WenDa_Fragment extends Fragment {
    private View rootView;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private MyAdapter myAdapter;
    public static List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private int page = 1;
    private PopupWindow window;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.wenda, container, false);
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
                    Intent intent = new Intent(getActivity(), HuaTi_WenDa_Activity.class);
                    intent.putExtra("position", position);
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
        String city="";
        RequestBody requestBody = RequestBody.create(JSON,"{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"obj\":{\"city\":\"" + city + "\"},\"page\":{\"id\":" + id + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/WenDa!Load.action")
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
            ViewHolderRightText holderRightText = null;
            if (convertView == null) {

                holderRightText = new ViewHolderRightText();
                convertView = View.inflate(getActivity(), R.layout.wenda_item, null);
                holderRightText.head = (ImageView) convertView.findViewById(R.id.headimage);
                holderRightText.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                holderRightText.iv2 = (ImageView) convertView.findViewById(R.id.iv2);
                holderRightText.iv3 = (ImageView) convertView.findViewById(R.id.iv3);
                holderRightText.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                holderRightText.nickname = (TextView) convertView.findViewById(R.id.nickname);
                holderRightText.title = (TextView) convertView.findViewById(R.id.title);
                holderRightText.content = (TextView) convertView.findViewById(R.id.content);
                holderRightText.zan_num = (TextView) convertView.findViewById(R.id.zan_num);
                holderRightText.huida_num = (TextView) convertView.findViewById(R.id.huida_num);
                holderRightText.ll_iv = (LinearLayout) convertView.findViewById(R.id.ll_iv);

                convertView.setTag(holderRightText);

            }
            holderRightText = (ViewHolderRightText) convertView.getTag();
            if (list_data.get(position).getPictrue()==null||list_data.get(position).getPictrue().length()==0){
                holderRightText.ll_iv.setVisibility(View.GONE);
            }else {
                holderRightText.ll_iv.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams vParam4s = holderRightText.ll_iv.getLayoutParams();
                vParam4s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.12);
                holderRightText.ll_iv.setLayoutParams(vParam4s);
                String[] split = list_data.get(position).getPictrue().split(",");

                if (split.length == 1) {
                    holderRightText.iv1.setVisibility(View.VISIBLE);
                    holderRightText.iv2.setVisibility(View.INVISIBLE);
                    holderRightText.iv3.setVisibility(View.INVISIBLE);
                    Glide.with(getActivity()).load(Data_Util.IMG+split[0]).centerCrop().into(holderRightText.iv1);
                } else if (split.length == 2) {
                    holderRightText.iv1.setVisibility(View.VISIBLE);
                    holderRightText.iv2.setVisibility(View.VISIBLE);
                    holderRightText.iv3.setVisibility(View.INVISIBLE);
                    Glide.with(getActivity()).load(Data_Util.IMG+split[0]).centerCrop().into(holderRightText.iv1);
                    Glide.with(getActivity()).load(Data_Util.IMG+split[1]).centerCrop().into(holderRightText.iv2);
                } else if (split.length > 2) {
                    holderRightText.iv1.setVisibility(View.VISIBLE);
                    holderRightText.iv2.setVisibility(View.VISIBLE);
                    holderRightText.iv3.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).load(Data_Util.IMG+split[0]).centerCrop().into(holderRightText.iv1);
                    Glide.with(getActivity()).load(Data_Util.IMG+split[1]).centerCrop().into(holderRightText.iv2);
                    Glide.with(getActivity()).load(Data_Util.IMG+split[2]).centerCrop().into(holderRightText.iv3);
                }
            }

            holderRightText.title.setText(list_data.get(position).getTitle());
            holderRightText.nickname.setText(list_data.get(position).getNickname());
            Glide.with(getActivity()).load(Data_Util.IMG+list_data.get(position).getHeadImg()).centerCrop().into(holderRightText.head);

            String content = list_data.get(position).getContent();
            String con="";
            if (content.length()>32&&regexMethod(content)){
                con=content.substring(33,content.length());
            }else {
                con=list_data.get(position).getContent();
            }
            if (con==null||con.length()==0){
                holderRightText.content.setVisibility(View.GONE);
            }else {
                holderRightText.content.setVisibility(View.VISIBLE);
                holderRightText.content.setText(con);
            }

            holderRightText.zan_num.setText(list_data.get(position).getUpCount()+"赞");
            holderRightText.huida_num.setText(list_data.get(position).getCommentNum()+"回答");

            holderRightText.iv_delete.setTag(position);
            holderRightText.iv_delete.setOnClickListener(new MyOnclickListenr());
            holderRightText.head.setTag(R.id.headimage,position);
            holderRightText.head.setOnClickListener(new MyOnclickListenr());

            return convertView;
        }
    }

    private class MyOnclickListenr implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.headimage:
                    int position_head = (int) v.getTag(R.id.headimage);
                    Intent intent = new Intent(getActivity(), Other_Activity.class);
                    intent.putExtra("uid", list_data.get(position_head).getUid());
                    startActivity(intent);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 55) {
            myAdapter.notifyDataSetChanged();
        }
    }

    private boolean regexMethod(String con){
        String substring = con.substring(0, 32);
        String regexPattern = "^[0-9a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(substring);
        return matcher.matches();
    }

    class ViewHolderRightText {
        private TextView nickname;
        private ImageView head;
        private ImageView iv1;
        private ImageView iv2;
        private ImageView iv3;
        private ImageView iv_delete;
        private TextView title;
        private TextView content;
        private TextView zan_num;
        private TextView huida_num;
        private LinearLayout ll_iv;
    }
    public void WDscoll(){
        mLoadMoreListView.smoothScrollToPositionFromTop(0,0);
    }
}
