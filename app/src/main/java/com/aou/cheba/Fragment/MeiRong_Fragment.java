package com.aou.cheba.Fragment;

import android.content.Intent;
import android.graphics.Rect;
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
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.Login_Activity;
import com.aou.cheba.Activity.MainActivity;
import com.aou.cheba.Activity.MeiRong_xiangqiang_Activity;
import com.aou.cheba.R;
import com.aou.cheba.Service.LocationService;
import com.aou.cheba.bean.MyCode_meirong;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.LoadMoreListView;
import com.aou.cheba.view.MyToast;
import com.aou.cheba.view.ProgressBarCircularIndeterminate;
import com.aou.cheba.view.RatingBar;
import com.aou.cheba.view.RefreshAndLoadMoreView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
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
 * Created by Administrator on 2016/8/25.
 */
public class MeiRong_Fragment extends Fragment {
    public static List<MyCode_meirong.ObjBean> list_meirong = new ArrayList<>();
    private View rootView;
    private int page = 1;
    private RelativeLayout ll_home;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private ImageView iv_search;
    private MyAdapter myAdapter;
    private EditText et_sousuo;
    private TextView tv_sousuo;
    //    private ImageView iv_sousuo;
    private LinearLayout rl_wai;
    private boolean issearch = false;
    private String trim;
    private LocationService locationService;
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.meirong_fragment, container, false);

            findviewbyid();
            initData();

            // inithttp_data(Long.MAX_VALUE, 1, "香梅");
            Location();
        }
        return rootView;
    }

    private void findviewbyid() {
        rl_wai = (LinearLayout) rootView.findViewById(R.id.rl_wai);
        ll_home = (RelativeLayout) rootView.findViewById(R.id.ll_xiyou);
        et_sousuo = (EditText) rootView.findViewById(R.id.et_sousuo);
        tv_sousuo = (TextView) rootView.findViewById(R.id.tv_sousuo);
//        iv_sousuo = (ImageView) rootView.findViewById(R.id.iv_sousuo);
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.progressBarCircularIndetermininate);

        tv_sousuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_sousuo.getText().toString().trim())) {

                } else {
                    issearch = true;
                    trim = et_sousuo.getText().toString().trim();
                    inithttp_search(Long.MAX_VALUE, 1, trim);
                }
            }
        });

        rl_wai.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rl_wai.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rl_wai.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);
                        if (heightDifference > 200) {
//                            tv_sousuo.setVisibility(View.GONE);
//                            iv_sousuo.setVisibility(View.VISIBLE);
                        } else {
                            et_sousuo.setText("");
//                            tv_sousuo.setVisibility(View.VISIBLE);
//                            iv_sousuo.setVisibility(View.GONE);
                        }
                    }
                });

        mLoadMoreListView = (LoadMoreListView) rootView.findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) rootView.findViewById(R.id.refresh_and_load_more);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.08);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.05);
            ll_home.setLayoutParams(vParams1);
        }
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
                Location();
                /*issearch = false;
                page = 1;
                inithttp_data(Long.MAX_VALUE, page, "香梅");*/
            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //  loadData(pageIndex + 1);
                if (issearch) {
                    page += 1;
                    inithttp_search(list_meirong.get(list_meirong.size() - 1).getId(), page, trim);
                } else {
                    page += 1;
                    inithttp_data(list_meirong.get(list_meirong.size() - 1).getId(), page, cityName);
                }
            }
        });
        //   mLoadMoreListView.setOnItemClickListener(new ItemClickListener());
    }

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

    private StringBuffer sb;
    private String cityName = "";
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
                Log.i("test", "城市 ：" + location.getCity());
                if (cityName.endsWith("市")) {
                    cityName = cityName.substring(0, cityName.length() - 1);
                }

                locationService.unregisterListener(mListener); //注销掉监听
                locationService.stop(); //停止定位服务

                issearch = false;
                page = 1;
                inithttp_data(Long.MAX_VALUE, page, cityName);

            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    private boolean isenter = false;

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
                    MyCode_meirong.ObjBean objBean = list_meirong.get(position);
                    Intent intent = new Intent(getActivity(), MeiRong_xiangqiang_Activity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("objBean", objBean);
                    startActivity(intent);

                /*    MyCode_meirong.ObjBean objBean = list_meirong.get(position);
                    Intent intent = new Intent(getActivity(), HuaTi_Activity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("ser", objBean);
                    startActivityForResult(intent, 12);*/
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list_meirong.size();
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
                convertView = View.inflate(getActivity(), R.layout.meirong_item, null);
                holderRightImg = new ViewHolderRightImg();
                holderRightImg.image_dian = (ImageView) convertView.findViewById(R.id.image_dian);
                holderRightImg.tv_dianname = (TextView) convertView.findViewById(R.id.tv_dianname);
                holderRightImg.tv_ping_num = (TextView) convertView.findViewById(R.id.tv_ping_num);
                holderRightImg.tv_dizhi = (TextView) convertView.findViewById(R.id.tv_dizhi);
                holderRightImg.xin = (RatingBar) convertView.findViewById(R.id.xin);
                holderRightImg.tv_type = (TextView) convertView.findViewById(R.id.tv_type);

                convertView.setTag(holderRightImg);
            }
            holderRightImg = (ViewHolderRightImg) convertView.getTag();
//屏幕适配
            ViewGroup.LayoutParams vParams = holderRightImg.image_dian.getLayoutParams();
            vParams.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * 0.11);
            holderRightImg.image_dian.setLayoutParams(vParams);
            Glide.with(getActivity()).load(list_meirong.get(position).getPictrue().split(",")[0]).into(holderRightImg.image_dian);
            holderRightImg.tv_dizhi.setText(list_meirong.get(position).getArea());
            holderRightImg.tv_type.setText(list_meirong.get(position).getStore_type());

            holderRightImg.xin.setStar(list_meirong.get(position).getScore());//设置显示的星星个数
            holderRightImg.xin.setStepSize(RatingBar.StepSize.Full);//设置每次点击增加一颗星还是半颗星

            holderRightImg.tv_dianname.setText(list_meirong.get(position).getStore_name());
            holderRightImg.tv_ping_num.setText(list_meirong.get(position).getCommentNum() + "");

            return convertView;
        }
    }

    Handler h = new Handler();

    private void inithttp_data(long id, final int page, String s) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"obj\":{\"addr\":\"" + s + "\"},\"page\":{\"id\":" + id + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/CarStore!Load.action")
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
                }, 400);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();


                Gson gson = new Gson();
                final MyCode_meirong mycode = gson.fromJson(res, MyCode_meirong.class);


                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            progressBarCircularIndetermininate.setVisibility(View.GONE);

                            List<MyCode_meirong.ObjBean> rows = mycode.getObj();

                            if (page == 1) {
                                if (rows.size() != 0) {
                                    list_meirong.clear();
                                    list_meirong.addAll(rows);
                                }
                            } else {
                                list_meirong.addAll(rows);
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

    private void inithttp_search(long id, final int page, String s) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(getActivity(), "token") + "\",\"obj\":{\"store_name\":\"" + s + "\"},\"page\":{\"id\":" + id + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/CarStore!Search.action")
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
                final MyCode_meirong mycode = gson.fromJson(res, MyCode_meirong.class);


                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            List<MyCode_meirong.ObjBean> rows = mycode.getObj();

                            if (page == 1) {
                                if (rows.size() != 0) {
                                    list_meirong.clear();
                                    list_meirong.addAll(rows);
                                }
                            } else {
                                list_meirong.addAll(rows);
                            }

                            if (rows.size() != 0) {
                                if (myAdapter == null) {
                                    intView();
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }

                                mLoadMoreListView.setHaveMoreData(true);


                                myAdapter.notifyDataSetChanged();
                                //当加载完成之后设置此时不在刷新状态
                                mRefreshAndLoadMoreView.setRefreshing(false);
                                mLoadMoreListView.onLoadComplete();
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
                }, 500);


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }

        if (Login_Activity.isoutlogin_meirong) {
            Login_Activity.isoutlogin_meirong = false;
            page = 1;
            inithttp_data(Long.MAX_VALUE, page, cityName);
        }
    }


    class ViewHolderRightImg {
        private ImageView image_dian;
        private TextView tv_dianname;
        private TextView tv_ping_num;
        private RatingBar xin;
        private TextView tv_dizhi;
        private TextView tv_type;
    }
}
