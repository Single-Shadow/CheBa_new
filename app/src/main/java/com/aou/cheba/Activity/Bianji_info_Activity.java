package com.aou.cheba.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aou.cheba.R;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.view.DisplayUtil;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/11/29.
 */
public class Bianji_info_Activity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView finish;
    private ImageView fabiao;
    private EditText et;
    private int max;
    private RelativeLayout ll_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bianji_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        max = getIntent().getIntExtra("max", 20);

        finish = (ImageView) findViewById(R.id.iv_finish);
        fabiao = (ImageView) findViewById(R.id.tv_publish);
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Bianji_info_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Bianji_info_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }


        et = (EditText) findViewById(R.id.et);

        et.setHint("不超过" + max + "个字符");
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        finish.setOnClickListener(this);
        fabiao.setOnClickListener(this);
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
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_publish:
                String trim = et.getText().toString().trim();

                String s = readStream(getResources().openRawResource(R.raw.mingan));
                String[] split = s.split(",");



                for (int i = 0; i < split.length; i++) {
                    String x = split[i];  //x为敏感词汇
                    if (trim.contains(x)) {
                        trim = trim.replaceAll(x, getXing(x));
                    }
                }

                Intent data = new Intent();
                data.putExtra("content", trim);
                setResult(max, data);
                finish();
                break;
        }
    }

    private String getXing(String f) {
        String a = "";
        for (int i = 0; i < f.length(); i++) {
            a = a + "*";
        }
        return a;
    }

    private String readStream(InputStream is)    {
        // 资源流(GBK汉字码）变为串
        String res;
        try      {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            res = new String(buf,"GBK");   	//  必须将GBK码制转成Unicode
            is.close();
        } catch (Exception e)
        {
            res="";
        }
        return res;            //  把资源文本文件送到String串中
    }
}
