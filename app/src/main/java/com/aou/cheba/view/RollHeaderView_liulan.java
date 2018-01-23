package com.aou.cheba.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.aou.cheba.R;

import java.util.ArrayList;
import java.util.List;

/**
 * android banner图
 */
public class RollHeaderView_liulan extends FrameLayout implements OnPageChangeListener, View.OnClickListener {

    private Context mContext;
    public ViewPager mViewPager;

    private List<String> mUrlList;

    private List<ImageView> dotList = null;
    private MyAdapter mAdapter = null;
    private Handler mHandler = null;
    private AutoRollRunnable mAutoRollRunnable = null;

    private int prePosition = 0;

    private HeaderViewClickListener headerViewClickListener;

    private ArrayList<String> lists;
    private Button xiazai;
    private Button shoucang;
    private TextView yeshu;
    private ImageView finish;

    public RollHeaderView_liulan(Context context) {
        this(context, null);
    }

    public RollHeaderView_liulan(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollHeaderView_liulan(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
        initData();
        initListener();
    }

    //初始化view
    private void initView() {
        View.inflate(mContext, R.layout.view_header_liulan, this);
        mViewPager = (ViewPager) findViewById(R.id.vp);
        xiazai = (Button) findViewById(R.id.bt_xiazai);
        shoucang = (Button) findViewById(R.id.bt_shoucang);
        yeshu = (TextView) findViewById(R.id.te_shu);
        finish = (ImageView) findViewById(R.id.te_finish);
        xiazai.setOnClickListener(this);
        shoucang.setOnClickListener(this);
        finish.setOnClickListener(this);

        lists = new ArrayList<>();


     /*   //让banner的高度是屏幕的1/4
        ViewGroup.LayoutParams vParams = mViewPager.getLayoutParams();

        vParams.height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT);
        vParams.width = (int) (ViewGroup.LayoutParams.WRAP_CONTENT);
        mViewPager.setLayoutParams(vParams);*/
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
            //  dotList.clear();
            //mDotLl.removeAllViews();
            //  ImageView dotIv;
            //   LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        /*    for (int i = 0; i < mUrlList.size(); i++) {
                dotIv = new ImageView(mContext);
                if (i == 0) {
                    dotIv.setBackgroundResource(R.mipmap.lan);
                } else {
                    dotIv.setBackgroundResource(R.mipmap.bai);
                }
                //设置点的间距
                params.setMargins(0, 0, DisplayUtil.dip2px(mContext, 5), 0);
                dotIv.setLayoutParams(params);

                //添加点到view上
             //   mDotLl.addView(dotIv);
                //添加到集合中, 以便控制其切换
                dotList.add(dotIv);
            }*/
        }

        mAdapter = new MyAdapter();
        mViewPager.setAdapter(mAdapter);

        //设置viewpager初始位置, +10000就够了
        mViewPager.setCurrentItem(urlList.size() + 10000);

        //   tex.setText(lists.get(0));

        //  startRoll();
    }

    public void settext(ArrayList<String> list) {
        lists = list;
    }

    /**
     * 设置点击事件
     *
     * @param headerViewClickListener
     */
    public void setOnHeaderViewClickListener(HeaderViewClickListener headerViewClickListener) {
        this.headerViewClickListener = headerViewClickListener;
    }


    //开始轮播
    public void startRoll() {
        mAutoRollRunnable.start();
    }

    // 停止轮播
    public void stopRoll() {
        mAutoRollRunnable.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_xiazai:
                headerViewClickListener.XiaZai(prePosition);
                break;
            case R.id.bt_shoucang:
                headerViewClickListener.shoucang(prePosition);

                break;
            case R.id.te_finish:
                headerViewClickListener.isfinish();

                break;
        }
    }

    public void setxiazaienable() {
        xiazai.setEnabled(true);
    }

    public void setxiazaiunenable() {
        xiazai.setEnabled(false);
    }

    public void setshoucangenable() {
        shoucang.setEnabled(true);
    }

    public void setshoucangunenable() {
        shoucang.setEnabled(false);
    }

    private class AutoRollRunnable implements Runnable {

        //是否在轮播的标志
        boolean isRunning = false;

        public void start() {
            if (!isRunning) {
                isRunning = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, 1500);
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
                mHandler.postDelayed(this, 1500);
            }
        }
    }

    public interface HeaderViewClickListener {
        void HeaderViewClick(int position);

        void XiaZai(int position);
        void isfinish();

        void shoucang(int position);
    }

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

            ImageView iv;

            //获取ImageView对象
            if (imgCache.size() > 0) {
                iv = imgCache.remove(0);
            } else {
                iv = new ImageView(mContext);
            }
            //  iv.setScaleType(ScaleType.FIT_XY);

            iv.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    headerViewClickListener.HeaderViewClick(position % mUrlList.size());
                    return false;
                }
            });

            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    headerViewClickListener.isfinish();
                }
            });

         /*   iv.setOnTouchListener(new OnTouchListener() {
                private int downX = 0;
                private int downY = 0;
                private long downTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //   mAutoRollRunnable.stop();
                            //获取按下的x坐标
                            downX = (int) v.getX();
                            downY = (int) v.getY();

                            downTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            //    mAutoRollRunnable.start();
                            int moveX = (int) v.getX();
                            int moveY = (int) v.getY();
                            long moveTime = System.currentTimeMillis();
                            if (downX == moveX && downY == moveY && (moveTime - downTime < 700)) {//点击的条件
                                //轮播图回调点击事件
                                headerViewClickListener.isfinish();
                            }
                            if (downX == moveX && downY == moveY && (moveTime - downTime > 700)) {//点击的条件
                                //轮播图回调点击事件
                                headerViewClickListener.HeaderViewClick(position % mUrlList.size());
                            }

                            break;
                        case MotionEvent.ACTION_CANCEL:
                            //  mAutoRollRunnable.start();
                            break;
                    }
                    return true;
                }
            });*/


            //加载图片
            Glide.with(mContext).load(mUrlList.get(position % mUrlList.size()))
                    .error(R.mipmap.ic_launcher).into(iv);

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

        prePosition = position % mUrlList.size();

        yeshu.setText(prePosition + 1 + "/" + mUrlList.size());
        //    dotList.get(prePosition).setBackgroundResource(R.mipmap.bai);

        //  if (lists.size() != 0) {

        //   tex.setText(lists.get(position % lists.size()));
        //   }
        //   dotList.get(position % dotList.size()).setBackgroundResource(R.mipmap.lan);
        //   prePosition = position % dotList.size();
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
