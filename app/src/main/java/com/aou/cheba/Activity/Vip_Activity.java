package com.aou.cheba.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCode_shopping;
import com.aou.cheba.pay.AliPay;
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
import java.math.BigDecimal;
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
 * Created by Administrator on 2017/8/22.
 */
public class Vip_Activity extends SwipeBackActivity {
    private LoadMoreGrideView mLoadMoreGrideView;
    private RefreshAndLoadMore_GrideView mRefreshAndLoadMoreGrideView;
    private MyGrideView myAdapter;
    private static List<MyCode_shopping.ObjBean> list_data = new ArrayList<>();
    private int page = 1;
    private RollHeaderView rh;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> list2 = new ArrayList<>();
    private boolean isinithead = false;
    private static Dialog dialog;
    private View inflate;
    private ImageView buy_iv;
    private TextView tv_price;
    private TextView tv_name;
    private TextView tv_vip_price;
    private TextView bt_jian;
    private TextView bt_jia;
    private static TextView tv_payNum;
    private ImageView iv_xuanze1;
    private ImageView iv_xuanze2;
    private ImageView iv_delete;
    private TextView tv_dz_phone;
    private TextView tv_dz;
    private EditText et_shname;
    private EditText et_phonenum;
    private EditText ed_xxdz;
    private static TextView pay_vip;
    private static TextView tv_pay;
    private ImageView iv_finish;
    private RelativeLayout rl_dz;
    private boolean isOlddz = false;
    private TextView tv_dz_name;
    private static boolean isVip = false;
    private static int posi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      setContentView(new DrawingWithBezier(this));
        setContentView(R.layout.vip_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        isVip=new Me_Fragment().mycodes.getObj().isIsVip();

        mLoadMoreGrideView = (LoadMoreGrideView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreGrideView = (RefreshAndLoadMore_GrideView) findViewById(R.id.refresh_and_load_more);
        iv_finish = ((ImageView) findViewById(R.id.iv_finish));
        iv_finish.setOnClickListener(new MyOnclikListener());

        View inflate = View.inflate(Vip_Activity.this, R.layout.sc_head, null);
        mLoadMoreGrideView.addHeaderView(inflate);
        rh = (RollHeaderView) inflate.findViewById(R.id.rh);

        initData();
        // inithttp_data1(page);
        inithttp_data(1);

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
                inithttp_data(1);
            }
        });
        //设置加载监听
        mLoadMoreGrideView.setOnLoadMoreListener(new LoadMoreGrideView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page += 1;
                inithttp_data(page);
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

                    posi = position;

                    dialog = new Dialog(Vip_Activity.this, R.style.ActionSheetDialogStyle);
                    //填充对话框的布局
                    inflate = LayoutInflater.from(Vip_Activity.this).inflate(R.layout.buy_order, null);

                    buy_iv = ((ImageView) inflate.findViewById(R.id.iv));
                    tv_price = ((TextView) inflate.findViewById(R.id.tv_price));
                    tv_vip_price = ((TextView) inflate.findViewById(R.id.tv_vip_price));
                    tv_name = ((TextView) inflate.findViewById(R.id.tv_name));

                    bt_jian = ((TextView) inflate.findViewById(R.id.bt_jian));
                    bt_jia = ((TextView) inflate.findViewById(R.id.bt_jia));
                    tv_payNum = ((TextView) inflate.findViewById(R.id.tv_payNum));
                    iv_xuanze1 = ((ImageView) inflate.findViewById(R.id.iv_xuanze1));
                    iv_xuanze2 = ((ImageView) inflate.findViewById(R.id.iv_xuanze2));
                    iv_delete = ((ImageView) inflate.findViewById(R.id.iv_delete));
                    tv_dz_name = ((TextView) inflate.findViewById(R.id.tv_dz_name));
                    tv_dz_phone = ((TextView) inflate.findViewById(R.id.tv_dz_phone));
                    tv_dz = ((TextView) inflate.findViewById(R.id.tv_dz));
                    et_shname = ((EditText) inflate.findViewById(R.id.et_shname));
                    et_phonenum = ((EditText) inflate.findViewById(R.id.et_phonenum));
                    ed_xxdz = ((EditText) inflate.findViewById(R.id.ed_xxdz));
                    pay_vip = ((TextView) inflate.findViewById(R.id.pay_vip));
                    tv_pay = ((TextView) inflate.findViewById(R.id.tv_pay));
                    rl_dz = ((RelativeLayout) inflate.findViewById(R.id.rl_dz));

                    iv_xuanze1.setOnClickListener(new MyOnclikListener());
                    iv_xuanze2.setOnClickListener(new MyOnclikListener());
                    bt_jia.setTag(position);
                    bt_jia.setOnClickListener(new MyOnclikListener());
                    bt_jian.setTag(position);
                    bt_jian.setOnClickListener(new MyOnclikListener());
                    iv_delete.setOnClickListener(new MyOnclikListener());
                    tv_pay.setTag(position);
                    tv_pay.setOnClickListener(new MyOnclikListener());
                    pay_vip.setOnClickListener(new MyOnclikListener());

                    String dz = SPUtils.getString(Vip_Activity.this, SPUtils.getString(Vip_Activity.this, "uid") + "dz");
                    if (dz != null && dz != "") {
                        rl_dz.setVisibility(View.VISIBLE);
                        isOlddz = true;

                        String[] split = dz.split("&##");
                        tv_dz_name.setText(split[0]);
                        tv_dz_phone.setText(split[1]);
                        tv_dz.setText(split[2]);

                        iv_xuanze1.setImageResource(R.mipmap.xuanze);
                        iv_xuanze2.setImageResource(R.mipmap.quan);
                    } else {
                        isOlddz = false;
                        rl_dz.setVisibility(View.GONE);

//                        iv_xuanze1.setImageResource(R.mipmap.xuanze);
                        iv_xuanze2.setImageResource(R.mipmap.xuanze);
                    }

                    tv_name.setText("商品名称:" + list_data.get(position).getGoods_name());
                    tv_vip_price.setText("  会员价:" + list_data.get(position).getGoods_vip_price());
                    tv_price.setText("普通价:" + list_data.get(position).getGoods_price());
                    Glide.with(Vip_Activity.this).load(list_data.get(position).getGoods_img()).into(buy_iv);

                    if (isVip) {
                        pay_vip.setText("续费会员");
                        tv_pay.setText("购买" + list_data.get(position).getGoods_vip_price());
                    } else {
                        pay_vip.setText("开通会员");
                        tv_pay.setText("购买" + list_data.get(position).getGoods_price());
                    }


                    //将布局设置给Dialog
                    dialog.setContentView(inflate);
                    //获取当前Activity所在的窗体
                    Window dialogWindow = dialog.getWindow();
                    //设置Dialog从窗体底部弹出
                    dialogWindow.setGravity(Gravity.BOTTOM);
                    //获得窗体的属性
                    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                    //    lp.y = 20;//设置Dialog距离底部的距离
                    //       将属性设置给窗体
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    //设置窗口高度为包裹内容
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialogWindow.setAttributes(lp);
                    dialog.show();//显示对话框

                }
            }
        });
    }

    private boolean isenter = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 22) {
//            myAdapter.notifyDataSetChanged();
//            Log.i("test", "sdasd");
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
                view = View.inflate(Vip_Activity.this, R.layout.new_sc_item, null);
                holderTime.iv_datu = (ImageView) view.findViewById(R.id.iv_datu);
                holderTime.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holderTime.tv_vip_prive = (TextView) view.findViewById(R.id.tv_vip_price);
                view.setTag(holderTime);
            }

            holderTime = (ViewHolderTime) view.getTag();
            Glide.with(Vip_Activity.this).load(list_data.get(i).getGoods_img()).into(holderTime.iv_datu);
            holderTime.tv_name.setText(list_data.get(i).getGoods_name() + " ￥" + list_data.get(i).getGoods_price());
            holderTime.tv_vip_prive.setText(list_data.get(i).getGoods_vip_price() + "");


            ViewGroup.LayoutParams vParams1 = holderTime.iv_datu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Vip_Activity.this) * 0.18);
            holderTime.iv_datu.setLayoutParams(vParams1);

 /*           holderTime.pro.setProgress(list.get(i).getBuyNum() * 100 / list.get(i).getTotalNum());
            holderTime.tv_jindu.setText(list.get(i).getBuyNum() * 100 / list.get(i).getTotalNum() + "%");

            ViewGroup.LayoutParams vParams1 = holderTime.iv_datu.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Vip_Activity.this) * 0.19);
            holderTime.iv_datu.setLayoutParams(vParams1);
            Glide.with(Vip_Activity.this).load(list.get(i).getImg()).into(holderTime.iv_datu);

            holderTime.tv_name.setText(list.get(i).getGoodsName());*/

            return view;
        }
    }

    private void inithttp_data(final int page) {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(Vip_Activity.this, "token") + "\",\"page\":{\"page\":" + page + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/CarShop!Load.action")
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
                        MyToast.showToast(Vip_Activity.this, "连接服务器失败");
                    }
                }, 800);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                final MyCode_shopping mycode = gson.fromJson(res, MyCode_shopping.class);

                if (mycode == null) {
                    Log.i("test", "返回值有误");
                    return;
                }

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            List<MyCode_shopping.ObjBean> rows = mycode.getObj();

                            if (page == 1) {
                                list_data.clear();
                                list_data.addAll(rows);
                            } else {
                                list_data.addAll(rows);
                            }

                            if (!isinithead) {
                                init_head();//初始化头
                                isinithead = true;
                            }

                            if (rows.size() != 0) {
                                if (myAdapter == null) {
                                    myAdapter = new MyGrideView();
                                    mLoadMoreGrideView.setAdapter(myAdapter);
                                } else {
                                    myAdapter.notifyDataSetChanged();
                                }

                                if (rows.size() < 10) {
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
                                MyToast.showToast(Vip_Activity.this, "暂无更多数据");
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

    private void init_head() {
        list.clear();
        list2.clear();

        list.add(list_data.get(4).getGoods_img());
        list.add(list_data.get(5).getGoods_img());
        list.add(list_data.get(2).getGoods_img());
        list.add(list_data.get(3).getGoods_img());

        list2.add(list_data.get(4).getGoods_name());
        list2.add(list_data.get(5).getGoods_name());
        list2.add(list_data.get(2).getGoods_name());
        list2.add(list_data.get(3).getGoods_name());

        rh.settext(list2);
        rh.setImgUrlData(list);
        rh.setTime(5000);
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
                // startActivity(new Intent(Vip_Activity.this, WebView_activity.class));
                //   Toast.makeText(Vip_Activity.this, "点击 : " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MyOnclikListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.iv_xuanze1:
                    if (isOlddz) {

                    } else {
                        isOlddz = true;
                        iv_xuanze1.setImageResource(R.mipmap.xuanze);
                        iv_xuanze2.setImageResource(R.mipmap.quan);
                    }
                    break;
                case R.id.iv_xuanze2:
                    if (isOlddz) {
                        isOlddz = false;
                        iv_xuanze1.setImageResource(R.mipmap.quan);
                        iv_xuanze2.setImageResource(R.mipmap.xuanze);
                    } else {

                    }
                    break;
                case R.id.iv_delete:
                    SPUtils.put(Vip_Activity.this, SPUtils.getString(Vip_Activity.this, "uid") + "dz", "");
                    isOlddz = false;
                    rl_dz.setVisibility(View.GONE);
                    iv_xuanze2.setImageResource(R.mipmap.xuanze);

                    break;
                case R.id.tv_pay:
                    int position = (int) view.getTag();
                    double price;
                    if (tv_payNum.getText().toString().equals("0")) {
                        MyToast.showToast(Vip_Activity.this, "请选择购买数量");
                    } else {
                        if (isOlddz) {
                            if (isVip) {
                                price = list_data.get(position).getGoods_vip_price() * (Integer.parseInt(tv_payNum.getText().toString()));
                            } else {
                                price = list_data.get(position).getGoods_price() * (Integer.parseInt(tv_payNum.getText().toString()));
                            }

                            String proid = "车吧-" + list_data.get(position).getGoods_name();
                            AliPay.pay(price, proid, Vip_Activity.this, dialog);
//                            dialog.dismiss();
                        } else {
                            String shname = et_shname.getText().toString().trim();
                            String phone = et_phonenum.getText().toString().trim();
                            String dz = ed_xxdz.getText().toString().trim();
                            if (TextUtils.isEmpty(shname) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(dz)) {
                                MyToast.showToast(Vip_Activity.this, "请完善您的收货地址信息");
                            } else {
                                String newdz = shname + "&##" + phone + "&##" + dz;
                                SPUtils.put(Vip_Activity.this, SPUtils.getString(Vip_Activity.this, "uid") + "dz", newdz);

                                if (isVip) {
                                    price = list_data.get(position).getGoods_vip_price() * (Integer.parseInt(tv_payNum.getText().toString()));
                                } else {
                                    price = list_data.get(position).getGoods_price() * (Integer.parseInt(tv_payNum.getText().toString()));
                                }

                                String proid = "车吧-" + list_data.get(position).getGoods_name();
                                AliPay.pay(price, proid, Vip_Activity.this, dialog);
//                                dialog.dismiss();
                            }
                        }
                    }

                    break;
                case R.id.pay_vip:
                    String proid = "车吧-Vip";
                    AliPay.pay(30.00, proid, Vip_Activity.this, null);
                    break;
                case R.id.bt_jia:
                    int pos_jia = (int) view.getTag();

                    if (tv_payNum.getText().toString().equals("9")) {
                        bt_jia.setTextColor(Color.parseColor("#dedede"));
                        bt_jia.setEnabled(false);
                    }

                    if (tv_payNum.getText().toString().equals("0")) {
                        bt_jian.setTextColor(Color.parseColor("#929292"));
                        bt_jian.setEnabled(true);
                    }

                    if (!tv_payNum.getText().toString().equals("10")) {
                        tv_payNum.setText(Integer.parseInt(tv_payNum.getText().toString()) + 1 + "");
                    }

                    if (isVip) {
                        BigDecimal b = new BigDecimal(list_data.get(pos_jia).getGoods_vip_price() * Integer.parseInt(tv_payNum.getText().toString()));
                        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        tv_pay.setText("购买" + f1);
                    } else {
                        BigDecimal b = new BigDecimal(list_data.get(pos_jia).getGoods_price() * Integer.parseInt(tv_payNum.getText().toString()));
                        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        tv_pay.setText("购买" + f1);
                    }

                    break;

                case R.id.bt_jian:
                    int pos_jian = (int) view.getTag();

                    if (tv_payNum.getText().toString().equals("10")) {
                        bt_jia.setTextColor(Color.parseColor("#929292"));
                        bt_jia.setEnabled(true);
                    }

                    if (tv_payNum.getText().toString().equals("1")) {
                        bt_jian.setTextColor(Color.parseColor("#dedede"));
                        bt_jian.setEnabled(false);
                        tv_pay.setText("购买");
                    }

                    if (!tv_payNum.getText().toString().equals("0")) {
                        tv_payNum.setText(Integer.parseInt(tv_payNum.getText().toString()) - 1 + "");

                        if (isVip) {
                            BigDecimal b = new BigDecimal(list_data.get(pos_jian).getGoods_vip_price() * Integer.parseInt(tv_payNum.getText().toString()));
                            double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            tv_pay.setText("购买" + f1);

                        } else {
                            BigDecimal b = new BigDecimal(list_data.get(pos_jian).getGoods_price() * Integer.parseInt(tv_payNum.getText().toString()));
                            double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            tv_pay.setText("购买" + f1);

                        }
                    }

                    if (tv_payNum.getText().toString().equals("0")) {
                        tv_pay.setText("购买");
                    }

                    break;
            }
        }
    }

    public static void method() {
        isVip = true;
        if (pay_vip != null && tv_payNum != null && tv_pay != null) {
            pay_vip.setText("续费会员");
            if (tv_payNum.getText().toString().equals("0")) {
                tv_pay.setText("购买");
            } else {
                BigDecimal b = new BigDecimal(list_data.get(posi).getGoods_vip_price() * Integer.parseInt(tv_payNum.getText().toString()));
                double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                tv_pay.setText("购买" + f1);
            }
        }
    }

    class ViewHolderTime {
        private ImageView iv_datu;
        private TextView tv_name;
        private TextView tv_vip_prive;
    }
}
