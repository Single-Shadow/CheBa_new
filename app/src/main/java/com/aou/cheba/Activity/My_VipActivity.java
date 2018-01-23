package com.aou.cheba.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aou.cheba.Fragment.Me_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.bean.MyCodeInfo;
import com.aou.cheba.pay.AliPay;
import com.aou.cheba.utils.SPUtils;
import com.bumptech.glide.Glide;

import java.sql.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/8/24.
 */
public class My_VipActivity extends SwipeBackActivity {

    private CircleImageView iv_head;
    private CircleImageView iv_head2;
    private TextView tv_name;
    private ImageView iv_finish;
    public static Button bt_vip;
    public static Context context;
    private boolean isVip = false;
    private static ImageView iv_vip;
    private static TextView tv_yxtime;
    private static ImageView iv_kt;
    private static ImageView iv_xf;
    private static TextView tv_1;
    private static TextView tv_2;
    private static TextView tv_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_vipactivity);

        context = My_VipActivity.this;
        isVip = new Me_Fragment().mycodes.getObj().isIsVip();

        iv_head = ((CircleImageView) findViewById(R.id.iv_head));
        iv_head2 = ((CircleImageView) findViewById(R.id.iv_head2));
        iv_finish = ((ImageView) findViewById(R.id.iv_finish));
        iv_vip = ((ImageView) findViewById(R.id.iv_vip));
        iv_kt = ((ImageView) findViewById(R.id.iv_kt));
        iv_xf = ((ImageView) findViewById(R.id.iv_xf));
        tv_name = ((TextView) findViewById(R.id.tv_name));
        tv_yxtime = ((TextView) findViewById(R.id.tv_yxtime));
        tv_1 = ((TextView) findViewById(R.id.tv_1));
        tv_2 = ((TextView) findViewById(R.id.tv_2));
        tv_3 = ((TextView) findViewById(R.id.tv_3));
        bt_vip = ((Button) findViewById(R.id.bt_vip));

        iv_finish.setOnClickListener(new MyOnclikListener());
        bt_vip.setOnClickListener(new MyOnclikListener());
        iv_kt.setOnClickListener(new MyOnclikListener());
        iv_xf.setOnClickListener(new MyOnclikListener());

        String img = SPUtils.getString(My_VipActivity.this, "headImage");
        Glide.with(this).load(img).into(iv_head);
        Glide.with(this).load(img).into(iv_head2);
        tv_name.setText(SPUtils.getString(My_VipActivity.this, "nickeName"));

        if (isVip) {
            iv_vip.setVisibility(View.VISIBLE);
            iv_kt.setVisibility(View.GONE);
            iv_xf.setVisibility(View.VISIBLE);
            tv_1.setVisibility(View.VISIBLE);
            tv_2.setVisibility(View.VISIBLE);
            tv_3.setVisibility(View.VISIBLE);

            bt_vip.setText("续费会员");
            MyCodeInfo.ObjBean.VipInfoBean vipInfo = new Me_Fragment().mycodes.getObj().getVipInfo();
            if (vipInfo != null) {
                tv_yxtime.setText("会员有效期: "+vipInfo.getEndDate());

                long time = Date.valueOf(vipInfo.getEndDate()).getTime();
                long l = System.currentTimeMillis();
                int a = (int) ((time - l) / 1000 / 60 / 60 / 24);

                tv_2.setText("" + a);
            } else {
                tv_yxtime.setText("已开通会员");
                tv_2.setText("30");
            }
        } else {
            iv_vip.setVisibility(View.GONE);
            iv_kt.setVisibility(View.VISIBLE);
            iv_xf.setVisibility(View.GONE);
            tv_1.setVisibility(View.GONE);
            tv_2.setVisibility(View.GONE);
            tv_3.setVisibility(View.GONE);
            bt_vip.setText("开通会员");
            tv_yxtime.setText("未开通会员");
        }

    }

    class MyOnclikListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.bt_vip:
                    String proid = "车吧-Vip";
                    AliPay.pay(30.00, proid, My_VipActivity.this, null);
                    break;
                case R.id.iv_xf:
                    String proid_xf = "车吧-Vip";
                    AliPay.pay(30.00, proid_xf, My_VipActivity.this, null);
                    break;
                case R.id.iv_kt:
                    String proid_kt = "车吧-Vip";
                    AliPay.pay(30.00, proid_kt, My_VipActivity.this, null);
                    break;
            }
        }
    }

    public static void method() {
        if (bt_vip != null) {
            bt_vip.setText("续费会员");
            iv_vip.setVisibility(View.VISIBLE);
            iv_kt.setVisibility(View.GONE);
            iv_xf.setVisibility(View.VISIBLE);
            tv_1.setVisibility(View.VISIBLE);
            tv_2.setVisibility(View.VISIBLE);
            tv_3.setVisibility(View.VISIBLE);
            tv_yxtime.setText("会员有效期：31天");
            tv_2.setText("30");
        }
    }
}
