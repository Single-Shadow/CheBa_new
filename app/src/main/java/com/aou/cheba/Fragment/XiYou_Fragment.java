package com.aou.cheba.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.bean.MyCode_data;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.TimeUtil;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.ProgressBarCircularIndeterminate;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/25.
 */
public class XiYou_Fragment extends Fragment {

    private View rootView;
    private MyAdapter myAdapter;
    private int page = 1;
    //  public static List<MyCode_xiyou.ObjBean.RowsBean> list_xiyou = new ArrayList<>();
    public static List<MyCode_data.ObjBean> list_data = new ArrayList<>();
    private ImageView search;
    private RelativeLayout ll_home;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private LocationService locationService;
    private RelativeLayout rl_search;
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.xiyou_fragment, container, false);

            findviewbyid();
            initData();
            //  inithttp_data(Long.MAX_VALUE, 1, "深圳");
            Location();
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
                Location();
            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //  loadData(pageIndex + 1);
                page += 1;
                inithttp_data(list_data.get(list_data.size() - 1).getId(), page, cityName);
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
                    page = 1;
                    inithttp_data(Long.MAX_VALUE, page, cityName);
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
                page = 1;
                inithttp_data(Long.MAX_VALUE, page, cityName);
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    private void intView() {
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

                    MyCode_data.ObjBean objBean = list_data.get(position);
                    Intent intent = new Intent(getActivity(), HuaTi_Activity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("type","xiyou");
                    intent.putExtra("ser", objBean);
                    startActivityForResult(intent, 12);

                }
            }
        });
    }

    private void findviewbyid() {
        ll_home = (RelativeLayout) rootView.findViewById(R.id.ll_xiyou);
        mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) rootView.findViewById(R.id.refresh_and_load_more);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.progressBarCircularIndetermininate);


        rl_search = (RelativeLayout) rootView.findViewById(R.id.rl_search);
        rl_search.setOnClickListener(new MyOnclickListenr());

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.083);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.05);
            ll_home.setLayoutParams(vParams1);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
        if (Publish_lukuang_Activity.ispublic_lukuang|| Login_Activity.isoutlogin_xiyou) {
            Publish_lukuang_Activity.ispublic_lukuang = false;
            Login_Activity.isoutlogin_xiyou=false;
            page = 1;
            inithttp_data(Long.MAX_VALUE, page, cityName);
        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_data.size();
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
            ViewHolderRightImg holderRightImg = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.xiyou_item, null);
                holderRightImg = new ViewHolderRightImg();
                holderRightImg.iv = (SwipeFlingAdapterView) convertView.findViewById(R.id.iv_image);
                holderRightImg.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holderRightImg.fl_datu = (RelativeLayout) convertView.findViewById(R.id.fl_datu);
                holderRightImg.iv_shoucang = (ImageView) convertView.findViewById(R.id.iv_shoucang);
                holderRightImg.iv_datu = (ImageView) convertView.findViewById(R.id.iv_datu);
                holderRightImg.iv_fengxiang = (ImageView) convertView.findViewById(R.id.iv_fengxiang);
                holderRightImg.iv_xin = (ImageView) convertView.findViewById(R.id.iv_xin);
                holderRightImg.headimg = (CircleImageView) convertView.findViewById(R.id.login);
                holderRightImg.nickname = (TextView) convertView.findViewById(R.id.nickname);
//                holderRightImg.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                holderRightImg.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holderRightImg.btn_guanzhu = (TextView) convertView.findViewById(R.id.btn_guanzhu);
                holderRightImg.shuoshuo = (TextView) convertView.findViewById(R.id.shuoshuo);
                holderRightImg.tv_xin = (TextView) convertView.findViewById(R.id.tv_xin);
                holderRightImg.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                holderRightImg.dizhi = (TextView) convertView.findViewById(R.id.tv_dizhi);
                holderRightImg.image_shu = (TextView) convertView.findViewById(R.id.image_shu);

                convertView.setTag(holderRightImg);
            }
            holderRightImg = (ViewHolderRightImg) convertView.getTag();
//屏幕适配

            ViewGroup.LayoutParams vParams = holderRightImg.fl_datu.getLayoutParams();
            vParams.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.3);
            holderRightImg.fl_datu.setLayoutParams(vParams);

            Glide.with(getActivity()).load(list_data.get(position).getHeadImg()).into(holderRightImg.headimg);
            Glide.with(getActivity()).load(list_data.get(position).getPictrue().split(",")[0]).into(holderRightImg.iv_datu);
            holderRightImg.nickname.setText(list_data.get(position).getNickname());
            holderRightImg.image_shu.setText(list_data.get(position).getPictrue().split(",").length - 1 + "图");

            if (position == 0 || position == 4) {
                Log.i("test","   "+list_data.get(position).getAddtime()+" ");
            }
            holderRightImg.tv_time.setText(TimeUtil.getSpaceTime(list_data.get(position).getAddtime()));

            holderRightImg.shuoshuo.setText(list_data.get(position).getTitle());
            holderRightImg.tv_xin.setText(list_data.get(position).getUpCount() + "");
            holderRightImg.tv_ping_num.setText(list_data.get(position).getCommentNum() + "");
            if (list_data.get(position).getLocation() == null || list_data.get(position).getLocation().equals("未定位")) {
                holderRightImg.dizhi.setVisibility(View.INVISIBLE);
            } else {
                holderRightImg.dizhi.setVisibility(View.VISIBLE);
                holderRightImg.dizhi.setText(list_data.get(position).getLocation());
            }

            if (list_data.get(position).isUped() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                holderRightImg.iv_xin.setImageResource(R.mipmap.hongxin);
            } else {
                holderRightImg.iv_xin.setImageResource(R.mipmap.xin);
            }
            if (list_data.get(position).isCollected() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                holderRightImg.iv_shoucang.setImageResource(R.mipmap.isshoucang);
            } else {
                holderRightImg.iv_shoucang.setImageResource(R.mipmap.shoucang);
            }

            if (SPUtils.getString(getActivity(), "uid").equals(list_data.get(position).getUid() + "")) {
                holderRightImg.btn_guanzhu.setVisibility(View.GONE);
            } else {
                if (list_data.get(position).isFollowed() && !TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                    holderRightImg.btn_guanzhu.setVisibility(View.VISIBLE);
                    holderRightImg.btn_guanzhu.setText("已关注");
                    holderRightImg.btn_guanzhu.setTextColor(Color.parseColor("#d9d9d9"));
                    holderRightImg.btn_guanzhu.setBackgroundResource(R.drawable.custom_hui);
                    holderRightImg.btn_guanzhu.setEnabled(false);
                } else {
                    holderRightImg.btn_guanzhu.setEnabled(true);
                    holderRightImg.btn_guanzhu.setVisibility(View.VISIBLE);
                    holderRightImg.btn_guanzhu.setText("+ 关注");
                    holderRightImg.btn_guanzhu.setTextColor(Color.parseColor("#ffffff"));
                    holderRightImg.btn_guanzhu.setBackgroundResource(R.drawable.custom_lv);
                }
            }

            holderRightImg.headimg.setTag(R.id.image_tag, position);
            holderRightImg.headimg.setOnClickListener(new MyOnclickListenr());
            holderRightImg.btn_guanzhu.setTag(position);
            holderRightImg.btn_guanzhu.setOnClickListener(new MyOnclickListenr());
            holderRightImg.iv_shoucang.setTag(position);
            holderRightImg.iv_shoucang.setOnClickListener(new MyOnclickListenr());
            holderRightImg.iv_fengxiang.setTag(position);
            holderRightImg.iv_fengxiang.setOnClickListener(new MyOnclickListenr());
            holderRightImg.iv_xin.setTag(position);
            holderRightImg.iv_xin.setOnClickListener(new MyOnclickListenr());

            return convertView;
        }
    }

    private int clickposition = -1;
    private int clickposition_jubao = -1;

    private void inithttp_up(final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"obj\":{\"id\":\"" + list_data.get(position).getId() + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Traffic!Like.action")
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
                        if (mycode.getCode() == 0) {

                            list_data.get(position).setUpCount(list_data.get(position).getUpCount() + 1);
                            list_data.get(position).setUped(true);
                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });
                            MyToast.showToast(getActivity(), "赞");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_shoucang(long did, String token, final int position) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\": {\"did\": " + did + "},\"token\": \"" + token + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Collection.action")
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
                            list_data.get(position).setCollected(true);
                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });
                            MyToast.showToast(getActivity(), "收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_delshoucang(long did, String token, final int position) {
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
                            list_data.get(position).setCollected(false);
                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });
                            MyToast.showToast(getActivity(), "取消收藏");
                        } else if (mycode.getCode() == 4) {
                            SPUtils.put(getActivity(), "token", "");
                            MyToast.showToast(getActivity(), "请先登录");
                        }
                    }
                });
            }
        });
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
                            //      list_data.get(position).setFollowed(true);
                            for (int i = 0; i < list_data.size(); i++) {
                                if (list_data.get(i).getUid() == uid) {
                                    list_data.get(i).setFollowed(true);
                                }
                            }


                            clickposition = position;
                            myAdapter.notifyDataSetChanged();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    clickposition = -1;
                                }
                            });

                            MyCodeInfo me_mycodes = new Me_Fragment().mycodes;
                            if (me_mycodes != null) {
                                me_mycodes.getObj().setFollowCount(me_mycodes.getObj().getFollowCount() + 1);
                            }
                            SPUtils.put(getActivity(), "islogin", true);
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

    private void inithttp_data(long id, final int page, String city) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"obj\":{\"city\":\"" + city + "\"},\"page\":{\"id\":" + id + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/Traffic!Load.action")
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

                Log.i("test",res);

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


    private boolean isenter = false;
    Handler h = new Handler();

    class MyOnclickListenr implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position;
            switch (v.getId()) {
                case R.id.rl_search:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        Intent intent = new Intent(getActivity(), Search_Activity.class);
                        intent.putExtra("islukuang", true);
                        startActivity(intent);
                    }
                    break;

                case R.id.iv_xin:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            startActivity(new Intent(getActivity(), Login_Activity.class));
                            MyToast.showToast(getActivity(), "请先登录");
                        } else {
                            position = (int) v.getTag();
                            if (list_data.get(position).isUped()) {
                                MyToast.showToast(getActivity(), "您已经赞过了");
                            } else {
                                inithttp_up(position);
                            }
                        }
                    }
                    break;

                case R.id.btn_guanzhu:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            startActivity(new Intent(getActivity(), Login_Activity.class));
                            MyToast.showToast(getActivity(), "请先登录");
                        } else {
                            position = (int) v.getTag();
                            if (list_data.get(position).isFollowed()) {
                                MyToast.showToast(getActivity(), "已关注");
                            } else {
                                inithttp_guanzhu(list_data.get(position).getUid(), SPUtils.getString(getActivity(), "token"), position);
                            }
                        }
                    }
                    break;

                case R.id.iv_shoucang:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                            startActivity(new Intent(getActivity(), Login_Activity.class));
                            MyToast.showToast(getActivity(), "请先登录");
                        } else {
                            position = (int) v.getTag();
                            if (list_data.get(position).isCollected()) {
                                inithttp_delshoucang(list_data.get(position).getId(), SPUtils.getString(getActivity(), "token"), position);
                            } else {
                                inithttp_shoucang(list_data.get(position).getId(), SPUtils.getString(getActivity(), "token"), position);
                            }
                        }
                    }
                    break;

                case R.id.login:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        position = (int) v.getTag(R.id.image_tag);
                        Intent intent = new Intent(getActivity(), Other_Activity.class);
                        intent.putExtra("uid", list_data.get(position).getUid());
                        startActivity(intent);
                    }

                    break;

                case R.id.iv_fengxiang:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        position = (int) v.getTag();
//http://www.anou.net.cn:4888/CarbarFileServer/app/icarba.apk  http://www.dyyule.com/
                        OnekeyShare oks = new OnekeyShare();
                        // 关闭sso授权
                        oks.disableSSOWhenAuthorize();

                        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
                        // oks.setNotification(R.drawable.ic_launcher,
                        // getString(R.string.app_name));
                        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                        oks.setTitle(list_data.get(position).getTitle());
                        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                        oks.setTitleUrl("http://www.anou.net.cn/web/share/cbshare.jsp");
                        // text是分享文本，所有平台都需要这个字段
                        if (list_data.get(position).getContent() != null && !TextUtils.isEmpty(list_data.get(position).getContent())) {
                            oks.setText(list_data.get(position).getContent());
                        } else {
                            int v1 = ((int) (Math.random() * 100));
                            Log.i("test", "int:   " + v1);
                            if (v1 % 3 == 2) {
                                oks.setText("更多实时路况尽在车吧");
                            } else if (v1 % 3 == 1) {
                                oks.setText("车吧，让出行更简单");
                            } else {
                                oks.setText("进入车吧，了解更多实时路况");
                            }
                        }

                        // 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                        if (list_data.get(position).getPictrue() != null && !TextUtils.isEmpty(list_data.get(position).getPictrue())) {
                            oks.setImageUrl(list_data.get(position).getPictrue().split(",")[0]);
                        } else {
                            oks.setImageUrl("http://inavy.cn:4888/icon.png");
                        }

                        // Log.i("test",list_tucao.get(position).getPictrue().split(",")[0]);
                        //    oks.setImageUrl("http://b248.photo.store.qq.com/psb?/2c280a19-7f4f-4e7d-a168-20d7fd2f70cc/pUL9tktoc3SHsXcU8hba08Tt5pRL2r.6SaZN4imCsls!/b/dCJ805PTDQAA&bo=ngL2AQAAAAABCUU!&rf=viewer_4" + "");
                        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                        // oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
                        // url仅在微信（包括好友和朋友圈）中使用
                        oks.setUrl("http://www.anou.net.cn/web/share/cbshare.jsp");
                        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                        oks.setComment("文章的评论");
                        // site是分享此内容的网站名称，仅在QQ空间使用
                        oks.setSite("分享内容的地址");
                        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                        oks.setSiteUrl("http://www.anou.net.cn/web/share/cbshare.jsp");

                        // 启动分享GUI
                        oks.show(getActivity());

                    }
                    break;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

        }
    }

    class ViewHolderRightImg {
        private SwipeFlingAdapterView iv;
        private ImageView iv_xin;
        private ImageView iv_shoucang;
        private ImageView iv_datu;
        private ImageView iv_fengxiang;
        private CircleImageView headimg;
        private TextView nickname;
        private TextView tv_time;
        private TextView btn_guanzhu;
        private TextView shuoshuo;
        private TextView tv_xin;
        private TextView tv_ping_num;
        private TextView dizhi;
        private TextView image_shu;
        private RelativeLayout fl_datu;
    }
}
