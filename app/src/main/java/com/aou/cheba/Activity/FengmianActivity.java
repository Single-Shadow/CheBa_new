package com.aou.cheba.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aou.cheba.R;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.view.DisplayUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/29.
 */
public class FengmianActivity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView finish;
    private ImageView fabiao;
    private GridView iv_datu;
    private MyAdapter_Gride myAdapter_gride;
    private ArrayList<String> serializableExtra;
    private String select;
    private RelativeLayout ll_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fengmian);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        serializableExtra = (ArrayList<String>) getIntent().getSerializableExtra("list");
        select = getIntent().getStringExtra("select");


        finish = (ImageView) findViewById(R.id.iv_finish);
        fabiao = (ImageView) findViewById(R.id.tv_publish);
        iv_datu = (GridView) findViewById(R.id.iv_datu);
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);

        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(FengmianActivity.this) * Data_Util.HEAD_NEW);
            ll_home.setLayoutParams(vParams1);
        } else {
            ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
            vParams1.height = (int) (DisplayUtil.getMobileHeight(FengmianActivity.this) * Data_Util.HEAD_OLD);
            ll_home.setLayoutParams(vParams1);
        }


        iv_datu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (!serializableExtra.get(position).equals(select)) {
                    select=serializableExtra.get(position);
                    myAdapter_gride.notifyDataSetChanged();
                }

            }
        });
        myAdapter_gride = new MyAdapter_Gride();
        iv_datu.setAdapter(myAdapter_gride);


        finish.setOnClickListener(this);
        fabiao.setOnClickListener(this);
    }

    class MyAdapter_Gride extends BaseAdapter {
        @Override
        public int getCount() {
            return serializableExtra.size();
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
            Viewholder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(FengmianActivity.this, R.layout.grid_item, null);
                viewHolder = new Viewholder();

                viewHolder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
                viewHolder.iv2 = (ImageView) convertView.findViewById(R.id.iv2);

                ViewGroup.LayoutParams vParams = viewHolder.iv1.getLayoutParams();
                vParams.height = (int) (DisplayUtil.getMobileHeight(FengmianActivity.this) * 0.18);
                viewHolder.iv1.setLayoutParams(vParams);


                convertView.setTag(viewHolder);
            }
            viewHolder = (Viewholder) convertView.getTag();


            if (serializableExtra.get(position).equals(select)) {
                viewHolder.iv2.setVisibility(View.VISIBLE);
            } else {
                viewHolder.iv2.setVisibility(View.INVISIBLE);
            }


            Glide.with(FengmianActivity.this).load(serializableExtra.get(position)).into(viewHolder.iv1);

            return convertView;
        }
    }

    static class Viewholder {
        ImageView iv1;
        ImageView iv2;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_publish:
                Intent data = new Intent();
                data.putExtra("select", select);
                setResult(55, data);
                finish();
                break;
        }
    }
}
