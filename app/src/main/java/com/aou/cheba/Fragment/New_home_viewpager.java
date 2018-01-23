package com.aou.cheba.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aou.cheba.Activity.Search_Activity;
import com.aou.cheba.Fragment_new.ZiXun_Fragment;
import com.aou.cheba.R;
import com.aou.cheba.view.MyToast;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/1.
 */
public class New_home_viewpager extends Fragment {

    private View rootView_home;
    private ViewPager mPager_home;
    private TextView tv_1;
    private TextView tv_2;
    private TextView tv_3;
    private TextView tv_4;
    private TextView tv_5;
    private int currIndex = 0;
    private ImageView iv_search;
    private TextView tv_search;
    private ImageView iv_jia;
    //    private ImageView iv_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView_home == null) {
            rootView_home = inflater.inflate(R.layout.new_home_viewpager, container, false);
            mPager_home = (ViewPager) rootView_home.findViewById(R.id.viewpager);
            tv_1 = ((TextView) rootView_home.findViewById(R.id.tv_1));
            tv_2 = ((TextView) rootView_home.findViewById(R.id.tv_2));
            tv_3 = ((TextView) rootView_home.findViewById(R.id.tv_3));
            tv_4 = ((TextView) rootView_home.findViewById(R.id.tv_4));
            tv_5 = ((TextView) rootView_home.findViewById(R.id.tv_5));
            iv_search = (ImageView) rootView_home.findViewById(R.id.iv_search);
            tv_search = ((TextView) rootView_home.findViewById(R.id.tv_search));
            iv_jia = ((ImageView) rootView_home.findViewById(R.id.iv_jia));
//          cheba = (ImageView) rootView_home.findViewById(R.id.cheba);
            iv_search.setOnClickListener(new MyOnClickListener());
            tv_1.setOnClickListener(new MyOnClickListener());
            tv_2.setOnClickListener(new MyOnClickListener());
            tv_3.setOnClickListener(new MyOnClickListener());
            tv_4.setOnClickListener(new MyOnClickListener());
            tv_5.setOnClickListener(new MyOnClickListener());
            iv_search.setOnClickListener(new MyOnClickListener());
            iv_jia.setOnClickListener(new MyOnClickListener());
            tv_search.setOnClickListener(new MyOnClickListener());

            InitViewPager();
        }
        return rootView_home;
    }

    private void InitViewPager() {
        mPager_home.setAdapter(new PagerAdater_home(getChildFragmentManager()));
        mPager_home.addOnPageChangeListener(new MyOnPageChangeListener_home());
        mPager_home.setCurrentItem(0);
    }

    private class PagerAdater_home extends FragmentPagerAdapter {
        ArrayList<Fragment> hFragments = new ArrayList<>(5);

        public PagerAdater_home(FragmentManager fm) {
            super(fm);
            hFragments.add(new ZiXun_Fragment());
            hFragments.add(new New_daogou());
            hFragments.add(new New_pince());
            hFragments.add(new New_yongche());
            hFragments.add(new New_wanche());
        }

        @Override
        public Fragment getItem(int i) {
            Log.e("test", "getItem");
            return hFragments.get(i);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //不销毁 子fragment
//            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return hFragments.size();
        }
    }

    private class MyOnPageChangeListener_home implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    if (currIndex != 0) {
                        tv_1.setTextColor(Color.parseColor("#f1f1f1"));
                        tv_2.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_3.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_4.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_5.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_1.setTextSize(16);
                        tv_2.setTextSize(14);
                        tv_3.setTextSize(14);
                        tv_4.setTextSize(14);
                        tv_5.setTextSize(14);
                    }
                    break;
                case 1:
                    if (currIndex != 1) {
                        tv_1.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_2.setTextColor(Color.parseColor("#f1f1f1"));
                        tv_3.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_4.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_5.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_1.setTextSize(14);
                        tv_2.setTextSize(16);
                        tv_3.setTextSize(14);
                        tv_4.setTextSize(14);
                        tv_5.setTextSize(14);
                    }
                    break;
                case 2:
                    if (currIndex != 2) {
                        tv_1.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_2.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_3.setTextColor(Color.parseColor("#f1f1f1"));
                        tv_4.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_5.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_1.setTextSize(14);
                        tv_2.setTextSize(14);
                        tv_3.setTextSize(16);
                        tv_4.setTextSize(14);
                        tv_5.setTextSize(14);
                    }
                    break;
                case 3:
                    if (currIndex != 3) {
                        tv_1.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_2.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_3.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_4.setTextColor(Color.parseColor("#f1f1f1"));
                        tv_5.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_1.setTextSize(14);
                        tv_2.setTextSize(14);
                        tv_3.setTextSize(14);
                        tv_4.setTextSize(16);
                        tv_5.setTextSize(14);
                    }
                    break;
                case 4:
                    if (currIndex != 4) {
                        tv_1.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_2.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_3.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_4.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_5.setTextColor(Color.parseColor("#f1f1f1"));
                        tv_1.setTextSize(14);
                        tv_2.setTextSize(14);
                        tv_3.setTextSize(14);
                        tv_4.setTextSize(14);
                        tv_5.setTextSize(16);
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
                    if (currIndex != 0)
                        mPager_home.setCurrentItem(0);
                    break;
                case R.id.tv_2:
                    if (currIndex != 1)
                        mPager_home.setCurrentItem(1);
                    break;
                case R.id.tv_3:
                    if (currIndex != 2)
                        mPager_home.setCurrentItem(2);
                    break;
                case R.id.tv_4:
                    if (currIndex != 3)
                        mPager_home.setCurrentItem(3);
                    break;
                case R.id.tv_5:
                    if (currIndex != 4)
                        mPager_home.setCurrentItem(4);
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
                    MyToast.showToast(getActivity(),"发表");
                    break;
            }
        }
    }

    ;
}
