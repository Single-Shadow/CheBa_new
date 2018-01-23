package com.aou.cheba.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aou.cheba.R;

/**
 * Created by Administrator on 2016/11/29.
 */
public class Web_Activity extends Activity implements View.OnClickListener {


    private String web_url;
    private WebView web;
    //  private com.tencent.smtt.sdk.WebView tbsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        web_url = getIntent().getStringExtra("web");

        web = (WebView) findViewById(R.id.web);

        //设置WebView属性，能够执行Javascript脚本
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBlockNetworkImage(false);
        web.loadUrl(web_url);
        web.setWebViewClient(new HelloWebViewClient());

    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_publish:

                break;
        }
    }
}
