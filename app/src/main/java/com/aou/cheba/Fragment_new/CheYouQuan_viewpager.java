package com.aou.cheba.Fragment_new;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aou.cheba.Activity.Login_Activity;
import com.aou.cheba.Activity.Search_Activity;
import com.aou.cheba.R;
import com.aou.cheba.utils.Data_Util;
import com.aou.cheba.utils.SPUtils;
import com.aou.cheba.view.DisplayUtil;
import com.aou.cheba.view.MoreWindow;
import com.aou.cheba.view.MyToast;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/1.
 */
public class CheYouQuan_viewpager extends Fragment {

    private View rootView;
    private ViewPager mPager;
    private TextView tv_1;
    private TextView tv_2;
    private int currIndex = 0;
    private ImageView iv_search;
    private TextView tv_search;
    private ImageView iv_jia;
    private RelativeLayout ll_home;
    ArrayList<Fragment> mFragments = new ArrayList<>(2);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.new_cheyou_viewpager, container, false);
            mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
            tv_1 = ((TextView) rootView.findViewById(R.id.tv_1));
            tv_2 = ((TextView) rootView.findViewById(R.id.tv_2));
            iv_search = (ImageView) rootView.findViewById(R.id.iv_search);
            tv_search = ((TextView) rootView.findViewById(R.id.tv_search));
            iv_jia = ((ImageView) rootView.findViewById(R.id.iv_jia));

            ll_home = (RelativeLayout) rootView.findViewById(R.id.ll_home);
            if (Build.VERSION.SDK_INT >= 19) {
                ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
                vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * Data_Util.HEAD_NEW);
                ll_home.setLayoutParams(vParams1);
            } else {
                ViewGroup.LayoutParams vParams1 = ll_home.getLayoutParams();
                vParams1.height = (int) (DisplayUtil.getMobileHeight(getActivity()) * Data_Util.HEAD_OLD);
                ll_home.setLayoutParams(vParams1);
            }

            tv_1.setOnClickListener(new MyOnClickListener());
            tv_2.setOnClickListener(new MyOnClickListener());
            iv_search.setOnClickListener(new MyOnClickListener());
            iv_jia.setOnClickListener(new MyOnClickListener());
            tv_search.setOnClickListener(new MyOnClickListener());
        }
        InitViewPager();
        return rootView;
    }

    private void InitViewPager() {
        mPager.setAdapter(new PagerAdater(getChildFragmentManager()));
        mPager.addOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);
    }

    private class PagerAdater extends FragmentPagerAdapter {

        public PagerAdater(FragmentManager fm) {
            super(fm);
            Log.i("test", "(new New_cheyo");
            mFragments.add(new CheYouQuan_Fragment());
            mFragments.add(new WenDa_Fragment());
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //不销毁 子fragment
//            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        tv_1.setTextColor(Color.parseColor("#4F8EFF"));
                        tv_2.setTextColor(Color.parseColor("#969696"));
                        tv_1.setTextSize(16);
                        tv_2.setTextSize(14);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        tv_2.setTextColor(Color.parseColor("#4F8EFF"));
                        tv_1.setTextColor(Color.parseColor("#969696"));
                        tv_2.setTextSize(16);
                        tv_1.setTextSize(14);
                    }
                    break;
            }
            currIndex = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private boolean isenter = false;
    Handler h = new Handler();

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_1:
                    if (currIndex == 1)
                        mPager.setCurrentItem(0);
                    break;
                case R.id.tv_2:
                    if (currIndex == 0)
                        mPager.setCurrentItem(1);
                    break;
                case R.id.iv_search:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        startActivity(new Intent(getActivity(), Search_Activity.class));
                    }
                    break;
                case R.id.tv_search:
                    if (!isenter) {
                        isenter = true;
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isenter = false;
                            }
                        }, 500);
                        startActivity(new Intent(getActivity(), Search_Activity.class));
                    }
                    break;
                case R.id.iv_jia:
                    if (TextUtils.isEmpty(SPUtils.getString(getActivity(), "token"))) {
                        if (!isenter) {
                            isenter = true;
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isenter = false;
                                }
                            }, 500);
                            startActivity(new Intent(getActivity(), Login_Activity.class));
                            MyToast.showToast(getActivity(), "发表前请登录");
                        }

                    } else {
                        showMoreWindow(iv_jia);
                    }
                    break;
            }
        }
    }

    MoreWindow mMoreWindow;
    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(getActivity());
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view, 100);
    }

    public void MIXscoll(){
        if (currIndex==0){
            CheYouQuan_Fragment cyq=((CheYouQuan_Fragment) mFragments.get(0));
            cyq.CYQscoll();
        }else {
            WenDa_Fragment wd=((WenDa_Fragment) mFragments.get(1));
            wd.WDscoll();
        }
    }
}
