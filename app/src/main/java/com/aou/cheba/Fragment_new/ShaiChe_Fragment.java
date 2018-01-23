package com.aou.cheba.Fragment_new;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.HuaTi_Activity;
import com.aou.cheba.Activity.Login_Activity;
import com.aou.cheba.Activity.MainActivity;
import com.aou.cheba.Activity.Other_Activity;
import com.aou.cheba.Activity.Publish_lukuang_Activity;
import com.aou.cheba.Activity.Search_Activity;
import com.aou.cheba.R;
import com.aou.cheba.Service.LocationService;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MoreWindow;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.ProgressBarCircularIndeterminate;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.github.library.bubbleview.BubbleTextVew;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ShaiChe_Fragment extends Fragment {
    private View rootView;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private MyAdapter myAdapter;
    public static List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private RelativeLayout rl_denglu;
    private ImageView iv_search;
    private TextView tv_search;
    private ImageView iv_jia;
    private LocationService locationService;
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    private PopupWindow window;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.new_shaiche, container, false);
            mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.load_more_list);
            mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) rootView.findViewById(R.id.refresh_and_load_more);
            progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.progressBarCircularIndetermininate);

            rl_denglu = (RelativeLayout) rootView.findViewById(R.id.ll_home);
            iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
            tv_search = ((TextView) rootView.findViewById(R.id.tv_search));
            iv_jia = ((ImageView) rootView.findViewById(R.id.iv_jia));
            iv_search.setOnClickListener(new MyOnClickListener());
            iv_jia.setOnClickListener(new MyOnClickListener());
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
            inithttp_data(Long.MAX_VALUE, cityName);
//            Location();       暂时不需要定位
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
                inithttp_data(Long.MAX_VALUE, cityName);
                //Location();       暂时不需要定位
            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                inithttp_data(list_data.get(list_data.size() - 1).getId(), cityName);
            }
        });
        //   mLoadMoreListView.setOnItemClickListener(new ItemClickListener());
    }

    private StringBuffer sb;
    private String cityName = "";
    private void Location() {
        locationService = new MainActivity().locationServices;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getActivity().getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(cityName)) {
                    locationService.unregisterListener(mListener); //注销掉监听
                    locationService.stop(); //停止定位服务

                    //当加载完成之后设置此时不在刷新状态
                    mRefreshAndLoadMoreView.setRefreshing(false);
                    mLoadMoreListView.onLoadComplete();
                    inithttp_data(Long.MAX_VALUE, cityName);
                } else {
                    //当加载完成之后设置此时不在刷新状态
                    mRefreshAndLoadMoreView.setRefreshing(false);
                    mLoadMoreListView.onLoadComplete();
                }
            }
        }, 1000);
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                //   logMsg(sb.toString());
//                +location.getDistrict()+location.getStreet();
                cityName = location.getCity();
                if (cityName.endsWith("市")) {
                    cityName = cityName.substring(0, cityName.length() - 1);
                }

                locationService.unregisterListener(mListener); //注销掉监听
                locationService.stop(); //停止定位服务
                inithttp_data(Long.MAX_VALUE,cityName);
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

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
                    intent.putExtra("ser", objBean);
                    startActivityForResult(intent, 12);
                }
            }
        });
    }

    private void inithttp_data(final long id, String city) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        city = "";        //暂时先不按位置获取信息

        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"obj\":{\"city\":\"" + city + "\"},\"page\":{\"id\":" + id + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/ShaiChe!Load.action")
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
                            if (id == Long.MAX_VALUE) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
        if (Publish_lukuang_Activity.ispublic_lukuang || Login_Activity.isoutlogin_xiyou) {
            Publish_lukuang_Activity.ispublic_lukuang = false;
            Login_Activity.isoutlogin_xiyou = false;
            inithttp_data(Long.MAX_VALUE, cityName);
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
                convertView = View.inflate(getActivity(), R.layout.new_shaiche_item, null);
                holderRightText.iv1 = (ImageView) convertView.findViewById(R.id.image);
                holderRightText.shuoshuo = (TextView) convertView.findViewById(R.id.content);
                holderRightText.nickname = (TextView) convertView.findViewById(R.id.nickname);
                holderRightText.zan_num = (TextView) convertView.findViewById(R.id.zan_num);
                holderRightText.time = (TextView) convertView.findViewById(R.id.time);
                holderRightText.pinglun_num = (TextView) convertView.findViewById(R.id.pinglun_num);
                holderRightText.weizhi = (TextView) convertView.findViewById(R.id.weizhi);
                holderRightText.headima = (ImageView) convertView.findViewById(R.id.headima);
                holderRightText.ll_iv = (ImageView) convertView.findViewById(R.id.image);
                holderRightText.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                holderRightText.iv_gz = (ImageView) convertView.findViewById(R.id.iv_gz);

                convertView.setTag(holderRightText);
            }
            holderRightText = (ViewHolderRightText) convertView.getTag();

            ViewGroup.LayoutParams vParam4s = holderRightText.ll_iv.getLayoutParams();
            vParam4s.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.3);
            holderRightText.ll_iv.setLayoutParams(vParam4s);

            String[] split2 = list_data.get(position).getPictrue().split(",");
            holderRightText.shuoshuo.setText(list_data.get(position).getTitle());
            holderRightText.nickname.setText(list_data.get(position).getNickname());
            if (list_data.get(position).getLocation() == null || TextUtils.isEmpty(list_data.get(position).getLocation())
                    || list_data.get(position).getLocation().endsWith("未定位")) {
                holderRightText.weizhi.setVisibility(View.GONE);
            } else {
                holderRightText.weizhi.setVisibility(View.VISIBLE);
                holderRightText.weizhi.setText(list_data.get(position).getLocation());
            }

            Glide.with(getActivity()).load(Data_Util.IMG+list_data.get(position).getHeadImg()).into(holderRightText.headima);
            if (split2!=null&&split2.length!=0){
                Glide.with(getActivity()).load(Data_Util.IMG+split2[0]).centerCrop().into(holderRightText.iv1);
            }

            if ((list_data.get(position).getUid()+"").equals(SPUtils.getString(getActivity(),"uid"))){
                holderRightText.iv_gz.setVisibility(View.INVISIBLE);
            }else {
                holderRightText.iv_gz.setVisibility(View.VISIBLE);
                if (list_data.get(position).isFollowed() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                    holderRightText.iv_gz.setEnabled(false);
                    holderRightText.iv_gz.setImageResource(R.mipmap.bun_gz2);
                } else {
                    holderRightText.iv_gz.setEnabled(true);
                    holderRightText.iv_gz.setImageResource(R.mipmap.bun_gz1);
                }
            }

            holderRightText.time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));
            holderRightText.zan_num.setText(list_data.get(position).getUpCount()+"赞");
            holderRightText.pinglun_num.setText(list_data.get(position).getCommentNum()+"评论");
            holderRightText.iv_delete.setTag(position);
            holderRightText.iv_delete.setOnClickListener(new MyOnClickListener());
            holderRightText.iv_gz.setTag(position);
            holderRightText.iv_gz.setOnClickListener(new MyOnClickListener());
            holderRightText.headima.setTag(R.id.headima,position);
            holderRightText.headima.setOnClickListener(new MyOnClickListener());

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
                case R.id.iv_jia:
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
                    pb.setOnClickListener(new MyOnClickListener());

                    break;
                case R.id.iv_gz:
                    int tag_gz = (int) v.getTag();
                    inithttp_guanzhu(list_data.get(tag_gz).getUid(),SPUtils.getString(getActivity(),"token"),tag_gz);
                    break;
                case R.id.headima:
                    int position_head = (int) v.getTag(R.id.headima);
                    Intent intent = new Intent(getActivity(), Other_Activity.class);
                    intent.putExtra("uid", list_data.get(position_head).getUid());
                    startActivity(intent);
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
                            list_data.get(position).setFollowed(true);
                            for (int i = 0; i < list_data.size(); i++) {
                                if (list_data.get(i).getUid() == uid) {
                                    list_data.get(i).setFollowed(true);
                                }
                            }

                            myAdapter.notifyDataSetChanged();

                            MyToast.showToast(getActivity(), "关注成功");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
                        }
                    }
                });
            }
        });
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
        private ImageView iv_delete;
        private ImageView headima;
        private TextView nickname;
        private TextView weizhi;
        private TextView time;
        private TextView shuoshuo;
        private TextView pinglun_num;
        private TextView zan_num;
        private ImageView ll_iv;
        private ImageView iv_gz;
    }

    public void SCscoll(){
        mLoadMoreListView.smoothScrollToPositionFromTop(0,0);
    }
}
