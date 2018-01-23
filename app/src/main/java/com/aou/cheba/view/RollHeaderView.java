package com.aou.cheba.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aou.cheba.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * android banner图
 */
public class RollHeaderView extends FrameLayout implements OnPageChangeListener {

    private Context mContext;
    private ViewPager mViewPager;
    private LinearLayout mDotLl;
    private List<String> mUrlList;
    private List<ImageView> dotList = null;
    private MyAdapter mAdapter = null;
    private Handler mHandler = null;
    private AutoRollRunnable mAutoRollRunnable = null;
    private int prePosition = 0;
    private HeaderViewClickListener headerViewClickListener;
    private DownOrUpListenner downOrUpListenner;
    private TextView tex;
    private ArrayList<String> lists;
    private int TIME=3000;

    public RollHeaderView(Context context) {
        this(context, null);
    }
    public RollHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public RollHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
        initData();
        initListener();
    }

    //初始化view
    private void initView() {
        View.inflate(mContext, R.layout.view_header, this);
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mDotLl = (LinearLayout) findViewById(R.id.ll_dot);
        tex = (TextView) findViewById(R.id.tv_image_miaoshu);
        lists = new ArrayList<>();

        //让banner的高度是屏幕的1/4
        ViewGroup.LayoutParams vParams = mViewPager.getLayoutParams();
        vParams.height = (int) (DisplayUtil.getMobileHeight(mContext) * 0.29);
        mViewPager.setLayoutParams(vParams);


    }

    //初始化数据
    private void initData() {
        dotList = new ArrayList<ImageView>();
        mAutoRollRunnable = new AutoRollRunnable();
        mHandler = new Handler();
        mAdapter = new MyAdapter();
    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(this);
    }
    /**
     * 设置数据
     *
     * @param urlList
     */
    public void setImgUrlData(List<String> urlList) {
        this.mUrlList = urlList;
        if (mUrlList != null && !mUrlList.isEmpty()) {
            //清空数据
            dotList.clear();
            mDotLl.removeAllViews();
            ImageView dotIv;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < mUrlList.size(); i++) {
                dotIv = new ImageView(mContext);
                if (i == 0) {
                    dotIv.setBackgroundResource(R.mipmap.baise);
                } else {
                    dotIv.setBackgroundResource(R.mipmap.lanse);
                }
                //设置点的间距
                params.setMargins(0, 0, DisplayUtil.dip2px(mContext, 5), 0);
                dotIv.setLayoutParams(params);

                //添加点到view上
                mDotLl.addView(dotIv);
                //添加到集合中, 以便控制其切换
                dotList.add(dotIv);
            }
        }

        mAdapter = new MyAdapter();
        mViewPager.setAdapter(mAdapter);

        //设置viewpager初始位置, +10000就够了
        mViewPager.setCurrentItem(urlList.size() + 10000);

        tex.setText(lists.get(0));

        startRoll();
    }

    public void settext(ArrayList<String> list) {
        lists = list;
    }

    public void setTime(int i){
        TIME=i;
    }

    /**
     * 设置点击事件
     *
     * @param headerViewClickListener
     */
    public void setOnHeaderViewClickListener(HeaderViewClickListener headerViewClickListener) {
        this.headerViewClickListener = headerViewClickListener;
    }

    public void setDownOrUpListenner(DownOrUpListenner downOrUpListenner){
        this.downOrUpListenner=downOrUpListenner;
    }

    //开始轮播
    public void startRoll() {
        mAutoRollRunnable.start();
    }

    // 停止轮播
    public void stopRoll() {
        mAutoRollRunnable.stop();
    }

    private class AutoRollRunnable implements Runnable {

        //是否在轮播的标志
        boolean isRunning = false;

        public void start() {
            if (!isRunning) {
                isRunning = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, TIME);
            }
        }

        public void stop() {
            if (isRunning) {
                mHandler.removeCallbacks(this);
                isRunning = false;
            }
        }

        @Override
        public void run() {
            if (isRunning) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                mHandler.postDelayed(this, TIME);
            }
        }
    }

    public interface HeaderViewClickListener {
        void HeaderViewClick(int position);
    }

    public interface DownOrUpListenner{
        void isDown();
        void isUp();
    }


/*    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }*/

    private class MyAdapter extends PagerAdapter {

        //为了复用
        private List<ImageView> imgCache = new ArrayList<ImageView>();

        @Override
        public int getCount() {
            //无限滑动
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            MyImage iv;
            //获取ImageView对象
            if (imgCache.size() > 0) {
                iv = (MyImage) (imgCache.remove(0));
            } else {
                iv = new MyImage(mContext);
            }
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
/*
            //新加的
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                return super.dispatchTouchEvent(ev);
            }*/
            iv.setOnTouchListener(new OnTouchListener() {
                private int downX = 0;
                private int downY = 0;
                private long downTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downOrUpListenner.isDown();
                            mAutoRollRunnable.stop();
                            //获取按下的x坐标
                            downX = (int) v.getX();
                            downTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            downOrUpListenner.isUp();
                            mAutoRollRunnable.start();
                            int moveX = (int) v.getX();
                            int moveY = (int) v.getY();
                            long moveTime = System.currentTimeMillis();
                            if (downX == moveX &&downY== moveY&& (moveTime - downTime < 200)) {//点击的条件
                                //轮播图回调点击事件
                                headerViewClickListener.HeaderViewClick(position % mUrlList.size());
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            mAutoRollRunnable.start();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                    return true;
                }
            });

            //加载图片
            try {
                Glide.with(mContext).load(mUrlList.get(position % mUrlList.size())).into(iv);
//                Glide.with(mContext).load(mUrlList.get(position % mUrlList.size())).error(R.mipmap.ic_launcher).into(iv);
            } catch (Exception e) {

            }

            ((ViewPager) container).addView(iv);

            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object != null && object instanceof ImageView) {
                ImageView iv = (ImageView) object;
                ((ViewPager) container).removeView(iv);
                imgCache.add(iv);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        dotList.get(prePosition).setBackgroundResource(R.mipmap.lanse);
        if (lists.size() != 0) {
            tex.setText(lists.get(position % lists.size()));
        }
        dotList.get(position % dotList.size()).setBackgroundResource(R.mipmap.baise);
        prePosition = position % dotList.size();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    //停止轮播
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRoll();
    }

}
