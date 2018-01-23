package com.aou.cheba.wxapi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RadioButton;

import com.aou.cheba.Activity.Login_Activity;
import com.aou.cheba.Activity.MainActivity;
import com.aou.cheba.bean.MyCode1;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.bean.WeiXinLoginGetTokenBean;
import com.aou.cheba.bean.WeiXinLoginGetUserinfoBean;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.utils.SerializeUtils;
import com.aou.cheba.view.MyToast;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.message.UTrack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/26.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private Context mContext;
    private String wxid;
    private String nickname;
    private String headimgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        MainActivity.api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                finish();
                String code = ((SendAuth.Resp) baseResp).code;
                if (code != null) {
                    new AsynctaskToken().execute("https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=" + "wx79ee8a2e6fdf7ae0" + "&secret=" + "7a2e921d4129d6d4c3098f738da4ede5" + "&grant_type=authorization_code" + "&code=" + code);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            default:
                finish();
                break;
        }
    }


    //** 微信登录第二步：获取token *//*
    class AsynctaskToken extends AsyncTask<Object, Object, Object> {
       /* @Override
        protected Object doInBackground(Object... params) {
            HttpGet httpRequest = new HttpGet(params[0].toString());
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Log.i("test", "请求个人信息成功");
                    String strResult = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                    return strResult;
                } else {
                    Log.i("test", "请求个人信息失败");
                    return "请求出错";
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }*/

        @Override
        protected String doInBackground(Object... params) {
            HttpURLConnection conn = null;
            BufferedReader in = null;
            StringBuilder result = new StringBuilder();

            try {
                conn = (HttpURLConnection) new URL(params[0].toString()).openConnection();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String buf;
                while ((buf = in.readLine()) != null)
                    result.append(buf);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result.toString();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Object obj = null;

            Gson gson = new Gson();
            final WeiXinLoginGetTokenBean mycode = gson.fromJson(o.toString(), WeiXinLoginGetTokenBean.class);

            try {
                obj = SerializeUtils.json2Object(o.toString(), WeiXinLoginGetTokenBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            WeiXinLoginGetTokenBean bean = (WeiXinLoginGetTokenBean) obj;
            wxid = bean.getOpenid();
            Log.i("test", "获取token成功：\n" + "token:" + bean.getAccess_token() + "\nopenid" + bean.getOpenid());
            String url = "https://api.weixin.qq.com/sns/userinfo?" + "access_token=" + bean.getAccess_token() + "&openid=" + bean.getOpenid();
            new AsynctaskInfo().execute(url);
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }
    }

    //** 微信登录第三步：获取用户信息 *//*
    class AsynctaskInfo extends AsyncTask<Object, Object, Object> {
        @Override
        protected String doInBackground(Object... params) {
            HttpURLConnection conn = null;
            BufferedReader in = null;
            StringBuilder result = new StringBuilder();

            try {
                conn = (HttpURLConnection) new URL(params[0].toString()).openConnection();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String buf;
                while ((buf = in.readLine()) != null)
                    result.append(buf);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result.toString();
        }



      /*  @Override
        protected Object doInBackground(Object... params) {
            HttpGet httpRequest = new HttpGet(params[0].toString());
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Log.i("test", "请求个人信息成功");
                    String strResult = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                    return strResult;
                } else {
                    Log.i("test", "请求个人信息失败");
                    return "请求出错";
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }*/

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Object obj = null;
            try {
                obj = SerializeUtils.json2Object(o.toString(), WeiXinLoginGetUserinfoBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            WeiXinLoginGetUserinfoBean bean = (WeiXinLoginGetUserinfoBean) obj;

            //
            nickname = bean.getNickname();
            headimgurl = bean.getHeadimgurl();

            Log.i("test", "获取用户信息成功：\n" + "昵称:" + bean.getNickname() + "\n头像路径" + bean.getHeadimgurl());
            qq_login();
            //     finish();

        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }
    }


    private Handler h = new Handler();

    private void qq_login() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"loginType\":3,\"wid\":\"" + wxid + "\"}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Login.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(WXEntryActivity.this, "连接服务器失败");
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
                            SPUtils.put(WXEntryActivity.this, "logintype", "3");
                            SPUtils.put(WXEntryActivity.this, "token", (String) mycode.getObj());
                            inithttp_data();

                        } else if (3 == mycode.getCode()) {
                            MyToast.showToast(WXEntryActivity.this, "账号已注册");
                        } else if (1 == mycode.getCode()) {
                            qq_regeister();
                        } else if (2 == mycode.getCode()) {
                            MyToast.showToast(WXEntryActivity.this, "密码错误");
                        } else if (-1 == mycode.getCode()) {
                            MyToast.showToast(WXEntryActivity.this, "登录失败");
                        }
                    }
                });
            }
        });
    }

    private void inithttp_data() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"token\":\"" + SPUtils.getString(WXEntryActivity.this, "token") + "\"}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Info.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(WXEntryActivity.this, "连接服务器失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();

                Gson gson = new Gson();
                final MyCodeInfo mycodes = gson.fromJson(res, MyCodeInfo.class);

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if (0 == mycodes.getCode()) {
                            MyToast.showToast(WXEntryActivity.this, "登录成功");

                            SPUtils.put(WXEntryActivity.this, "uid", "" + mycodes.getObj().getUid());
                            SPUtils.put(WXEntryActivity.this, "headImage", "" + mycodes.getObj().getHeadImg());
                            SPUtils.put(WXEntryActivity.this, "nickeName", "" + mycodes.getObj().getNickname());

                            //绑定友盟 Alias
                            //绑定友盟 Alias
                            new MainActivity().mPushAgent.addExclusiveAlias(mycodes.getObj().getUid() + "", "carbar", new UTrack.ICallBack() {
                                @Override
                                public void onMessage(boolean b, String s) {
                                    Log.i("test_绑定友盟onMessage",s+b);
                                }
                            });

//                            new MainActivity().mPushAgent.setExclusiveAlias(mycodes.getObj().getUid() + "", "carbar");

                            new Login_Activity().mContext.finish();

                            ((RadioButton) new MainActivity().mainRg.getChildAt(4)).setChecked(true);
                            SPUtils.put(WXEntryActivity.this, "islogin", true);
                            finish();
                            //      inithttp_loginup(SPUtils.getString(Login_Activity.this, "token"));
                        }
                    }
                });
            }
        });
    }

    private void qq_regeister() {
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(5000, TimeUnit.SECONDS)//设置连接超时时间
                        .build();

        MediaType JSON = MediaType.parse("application/json; Charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, "{\"obj\":{\"loginType\":3,\"wid\":\"" + wxid + "\",\"headImg\":\"" + headimgurl + "\",\"nickname\":\"" + nickname + "\",\"gender\":" + 1 + "}}");
//创建一个请求对象
        Request request = new Request.Builder()
                .url(Data_Util.HttPHEAD + "/Carbar/User!Register.action")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showToast(WXEntryActivity.this, "连接服务器失败");
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
                            qq_login();
                        } else if (3 == mycode.getCode()) {
                            Log.i("test", "账号已注册");
                            MyToast.showToast(WXEntryActivity.this, "账号已注册");
                        } else if (-1 == mycode.getCode()) {
                            Log.i("test", "注册失败");
                            MyToast.showToast(WXEntryActivity.this, "注册失败");

                        } else if (7 == mycode.getCode()) {
                            Log.i("test", "验证码无效");
                            MyToast.showToast(WXEntryActivity.this, "验证码无效");

                        }
                    }
                });
            }
        });

    }
}
