package com.aou.cheba.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aou.cheba.R;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.view.DisplayUtil;

/**
 * Created by Administrator on 2016/11/29.
 */
public class Fenlei_Activity extends SwipeBackActivity implements View.OnClickListener{

    private ImageView finish;
    private ImageView fabiao;
    private EditText et;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;
    private ImageView iv4;
    private int select=1;
    private RelativeLayout r1;
    private RelativeLayout r2;
    private RelativeLayout r3;
    private RelativeLayout r4;
    private RelativeLayout ll_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fenlei);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        select = getIntent().getIntExtra("intent7", 1);

        finish = (ImageView) findViewById(R.id.iv_finish);
        fabiao = (ImageView) findViewById(R.id.tv_publish);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv4 = (ImageView) findViewById(R.id.iv4);
        r1 = (RelativeLayout) findViewById(R.id.rl_1);
        r2 = (RelativeLayout) findViewById(R.id.rl_2);
        r3 = (RelativeLayout) findViewById(R.id.rl_3);
        r4 = (RelativeLayout) findViewById(R.id.rl_4);
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Fenlei_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(Fenlei_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }

        et = (EditText) findViewById(R.id.et);

        finish.setOnClickListener(this);
        fabiao.setOnClickListener(this);
        r1.setOnClickListener(this);
        r2.setOnClickListener(this);
        r3.setOnClickListener(this);
        r4.setOnClickListener(this);

        switch (select) {
            case 1:
                r1.callOnClick();
                break;
            case 2:
                r2.callOnClick();
                break;
            case 3:
                r3.callOnClick();
                break;
            case 4:
                r4.callOnClick();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_publish:
                Intent data = new Intent();
                data.putExtra("select_fenlai", select);
                setResult(77, data);
                finish();
                break;

            case R.id.rl_1:
                select=1;
                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.INVISIBLE);
                iv3.setVisibility(View.INVISIBLE);
                iv4.setVisibility(View.INVISIBLE);
                break;
            case R.id.rl_2:
                select=2;
                iv1.setVisibility(View.INVISIBLE);
                iv2.setVisibility(View.VISIBLE);
                iv3.setVisibility(View.INVISIBLE);
                iv4.setVisibility(View.INVISIBLE);
                break;
            case R.id.rl_3:
                select=3;
                iv1.setVisibility(View.INVISIBLE);
                iv2.setVisibility(View.INVISIBLE);
                iv3.setVisibility(View.VISIBLE);
                iv4.setVisibility(View.INVISIBLE);
                break;
            case R.id.rl_4:
                select=4;
                iv1.setVisibility(View.INVISIBLE);
                iv2.setVisibility(View.INVISIBLE);
                iv3.setVisibility(View.INVISIBLE);
                iv4.setVisibility(View.VISIBLE);
                break;
        }
    }
}
