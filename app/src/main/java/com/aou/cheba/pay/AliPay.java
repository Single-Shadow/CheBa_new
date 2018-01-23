package com.aou.cheba.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.aou.cheba.Activity.My_VipActivity;
import com.aou.cheba.Activity.Vip_Activity;
import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.bean.MyCode_vip;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.MyToast;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AliPay {
    private static final int SDK_PAY_FLAG = 1;
    public static Context tcontext;
    private static String mPropId = "";
    private static double mPrice = 0;
    public static String mOrderId;
    public static Dialog dialogs;

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (dialogs == null) {
                            My_VipActivity.method();
                            Vip_Activity.method();
                            buy_vip();
                        } else {
                            dialogs.dismiss();
                            Toast.makeText(tcontext, "支付成功", Toast.LENGTH_SHORT).show();
                        }

//					AouCandy.BillingResult(mPropId,mPrice,102,mOrderId);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(tcontext, "支付结果确认中", Toast.LENGTH_SHORT).show();
//						AouCandy.BillingResult(mPropId,mPrice,0,mOrderId);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(tcontext, "支付失败", Toast.LENGTH_SHORT).show();
//						AouCandy.BillingResult(mPropId,mPrice,0,mOrderId);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    /**
     * call alipay sdk pay. 调用SDK支付
     */

    public static void pay(double price, String proid, final Context context, Dialog dialog) {
    /*    if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    //
                                    finish();
                                }
                            }).show();
            return;
        }
        // 订单
        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();
*/
        Tool.context = context;
        tcontext = context;
        dialogs = dialog;


        final String title = proid;
        mPropId = proid;
        mPrice = price;
        mOrderId = UUID.randomUUID() + "";
        final double payprice = price;
        Tool.PROJECT_ITEM = "012";
        final String body = Tool.PROJECT_ITEM + "zz" + mPropId + "z" + Tool.getImei() + "z" + Tool.getAssetsFolderData(Tool.mActivity, "channelid") + "z" + 0 + "zz" + Tool.getICCID();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) context);

                String net = (new StringBuilder("http://www.anou.net.cn:81/BillingInterface/Alipay/aliSign.aspx?subject=")).append(URLEncoder.encode(title)).append("&body=").append(URLEncoder.encode(body)).append("&price=").append(URLEncoder.encode(payprice + "")).toString();
                final String payInfo2 = Tool.getStrFromUrlByPar(net);

                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo2,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private static Handler h = new Handler();

    private static void buy_vip() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");

        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(tcontext, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!OpenVip.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(tcontext, "连接服务器失败");
                    }
                }, 800);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();

                Log.e("test  vip", res);
                Gson gson = new Gson();
                final MyCode_vip mycode = gson.fromJson(res, MyCode_vip.class);

                if (mycode == null) {
                    Log.i("test", "返回值有误");
                    return;
                }

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mycode.getCode() == 0) {
                            new Me_Fragment().mycodes.getObj().setIsVip(true);
                            MyToast.showToast(tcontext, "开通会员成功");
                        } else {
                            MyToast.showToast(tcontext, "服务器出错!");
                        }

                    }
                });
            }
        });
    }


    public static void pay2(int proid, int price) {
//		final String title=SelectDlg.TITLE[proid-1];
        final String title = proid - 1 + "";
//        mPropId = proid;
        mPrice = price;
        mOrderId = UUID.randomUUID() + "";
        final float payprice = price;
        Tool.PROJECT_ITEM = "012";
        final String body = Tool.PROJECT_ITEM + "zz" + mPropId + "z" + Tool.getImei() + "z" + Tool.getAssetsFolderData(Tool.mActivity, "channelid") + "z" + 0 + "zz" + Tool.getICCID();
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String net = (new StringBuilder("http://www.anou.net.cn:81/BillingInterface/Alipay/aliSign.aspx?subject=")).append(URLEncoder.encode(title)).append("&body=").append(URLEncoder.encode(body)).append("&price=").append(URLEncoder.encode(payprice + "")).toString();
                    String payInfo = Tool.getStrFromUrlByPar(net);
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(Tool.mActivity);
                    // 调用支付接口，获取支付结果

                    String result = alipay.pay(payInfo,true);

					/*Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);*/
                    PayResult payResult = new PayResult((String) result);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {

                        Tool.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(Tool.mActivity, "支付成功", Toast.LENGTH_SHORT).show();
//								Billing.BillingResult(mPropId,mPrice,102,mOrderId,Tool.PROJECT_ITEM);
                            }
                        });
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Tool.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(Tool.mActivity, "支付结果确认中", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //Toast.makeText(Tool.mActivity, "支付结果确认中", Toast.LENGTH_SHORT).show();
//							Billing.BillingResult(mPropId,mPrice,0,mOrderId,Tool.PROJECT_ITEM);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Tool.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(Tool.mActivity, "支付失败", Toast.LENGTH_SHORT).show();
//									Billing.BillingResult(mPropId,mPrice,0,mOrderId,Tool.PROJECT_ITEM);
                                }
                            });
                            //Toast.makeText(Tool.mActivity, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (Exception e) {
                    Tool.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(Tool.mActivity, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(Tool.mActivity, "支付失败", Toast.LENGTH_SHORT).show();
//					Billing.BillingResult(mPropId,mPrice,0,mOrderId,Tool.PROJECT_ITEM);
                }
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


}
