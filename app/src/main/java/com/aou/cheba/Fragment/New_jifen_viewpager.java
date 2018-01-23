package com.aou.cheba.Fragment;

import android.graphics.Color;
import android.os.Bundle;
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

import com.aou.cheba.R;
import com.aou.cheba.view.MyToast;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/1.
 */
public class New_jifen_viewpager extends Fragment {

    private View rootView;
    private ViewPager mPager;
    private TextView tv_1;
    private TextView tv_2;
    private int currIndex = 0;
    private ImageView iv_search;
    private TextView tv_search;
    private ImageView iv_jia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.new_jifen_viewpager, container, false);
            mPager = (ViewPager) rootView.findViewById(R.id.viewpager);
            tv_1 = ((TextView) rootView.findViewById(R.id.tv_1));
            tv_2 = ((TextView) rootView.findViewById(R.id.tv_2));
            iv_search = ((ImageView) rootView.findViewById(R.id.iv_search));
            tv_search = ((TextView) rootView.findViewById(R.id.tv_search));
            iv_jia = ((ImageView) rootView.findViewById(R.id.iv_jia));
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
        ArrayList<Fragment> mFragments = new ArrayList<>(2);

        public PagerAdater(FragmentManager fm) {
            super(fm);
            Log.i("test", "(new New_cheyo");
            mFragments.add(new New_zhuanqu());
            mFragments.add(new New_shangcheng());
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
                        tv_1.setTextColor(Color.parseColor("#f1f1f1"));
                        tv_2.setTextColor(Color.parseColor("#9ed7fd"));
                        tv_1.setTextSize(16);
                        tv_2.setTextSize(14);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        tv_2.setTextColor(Color.parseColor("#f1f1f1"));
                        tv_1.setTextColor(Color.parseColor("#9ed7fd"));
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
                    MyToast.showToast(getActivity(),"搜索");
                    break;
                case R.id.tv_search:
                    MyToast.showToast(getActivity(),"搜索");
                    break;
                case R.id.iv_jia:
                    MyToast.showToast(getActivity(),"发表");
                    break;
            }
        }
    }

    ;
}
