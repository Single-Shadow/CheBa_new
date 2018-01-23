package com.aou.cheba.Activity;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aou.cheba.Fragment.Fensi_Fragment;
import com.aou.cheba.Fragment.Guanzhu_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.view.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/5.
 */
public class GuanZhu_Activity extends SwipeBackActivity implements View.OnClickListener {

    private  List<Fragment> fragments = new ArrayList<Fragment>();
    private LinearLayout ll_guanzhu;
    private LinearLayout ll_fensi;
    private boolean isguanzhu = true;
    private View v_guanzhu;
    private View v_fensi;
    private ImageView finish;
    private RelativeLayout ll_home;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guanzhu_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        type = getIntent().getIntExtra("type", 0);

        finish = (ImageView) findViewById(R.id.iv_finish);
        ll_guanzhu = (LinearLayout) findViewById(R.id.ll_guanzhu);
        ll_fensi = (LinearLayout) findViewById(R.id.ll_fensi);
        v_guanzhu = (View) findViewById(R.id.v_guanzhu);
        v_fensi = (View) findViewById(R.id.v_fensi);
        ll_fensi.setOnClickListener(this);
        ll_guanzhu.setOnClickListener(this);
        fragments.add(new Guanzhu_Fragment());
        fragments.add(new Fensi_Fragment());
        finish.setOnClickListener(this);
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(GuanZhu_Activity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(GuanZhu_Activity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }

        if (type==1){
            isguanzhu=false;
            v_fensi.setVisibility(View.VISIBLE);
            v_guanzhu.setVisibility(View.INVISIBLE);
        }

        setResult(99);
        replaceFragmentWithBackStack(fragments.get(type));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_fensi:
                if (isguanzhu) {
                    isguanzhu=false;
                    v_fensi.setVisibility(View.VISIBLE);
                    v_guanzhu.setVisibility(View.INVISIBLE);
                    replaceFragmentWithBackStack(fragments.get(1));
                }
                break;
            case R.id.ll_guanzhu:
                if (!isguanzhu) {
                    isguanzhu=true;
                    v_fensi.setVisibility(View.INVISIBLE);
                    v_guanzhu.setVisibility(View.VISIBLE);
                    replaceFragmentWithBackStack(fragments.get(0));
                }
                break;
            case R.id.iv_finish:
                finish();
                break;
        }
    }
    public void replaceFragmentWithBackStack(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commitAllowingStateLoss();
    }

}
