package com.aou.cheba.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.aou.cheba.Activity.SelectPictureActivity;
import com.aou.cheba.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MoreWindow extends PopupWindow implements OnClickListener {

    private String TAG = MoreWindow.class.getSimpleName();
    Activity mContext;
    private int mWidth;
    private int mHeight;
    private int statusBarHeight;
    private Bitmap mBitmap = null;
    private Bitmap overlay = null;

    private Handler mHandler = new Handler();

    public MoreWindow(Activity context) {
        mContext = context;
    }

    public void init() {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        setWidth(mWidth);
        setHeight(mHeight);
    }

    private Bitmap blur() {
        if (null != overlay) {
            return overlay;
        }
        long startMs = System.currentTimeMillis();

        View view = mContext.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        mBitmap = view.getDrawingCache();

        float scaleFactor = 8;
        float radius = 10;
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        Log.i(TAG, "blur time is:" + (System.currentTimeMillis() - startMs));
        return overlay;
    }

    private Animation showAnimation1(final View view, int fromY, int toY) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation go = new TranslateAnimation(0, 0, fromY, toY);
        go.setDuration(300);
        TranslateAnimation go1 = new TranslateAnimation(0, 0, -10, 2);
        go1.setDuration(100);
        go1.setStartOffset(250);
        set.addAnimation(go1);
        set.addAnimation(go);

        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        return set;
    }


    public void showMoreWindow(View anchor, int bottomMargin) {
        final RelativeLayout layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.center_music_more_window, null);
        setContentView(layout);

        ImageView close = (ImageView) layout.findViewById(R.id.center_music_window_close);

        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    closeAnimation(layout);
                }
            }

        });

        showAnimation(layout);
        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.NO_GRAVITY, 0, statusBarHeight);
    }

    private void showAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            if (child.getId() == R.id.center_music_window_close) {
                continue;
            }
            LinearLayout child1 = (LinearLayout) child;
            for (int i1 = 0; i1 < child1.getChildCount(); i1++) {
                child1.getChildAt(i1).setOnClickListener(this);
            }
            //	child.setOnClickListener(this);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 900, 0);
                    fadeAnim.setDuration(600);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(300);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, i * 50);
        }

    }

    private void closeAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            if (child.getId() == R.id.center_music_window_close) {
                continue;
            }
            child.setOnClickListener(this);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
                    fadeAnim.setDuration(200);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(100);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                    fadeAnim.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            child.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }, (layout.getChildCount() - i - 1) * 30);

            if (child.getId() == R.id.ll) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        dismiss();
                    }
                }, (layout.getChildCount() - i) * 30 + 80);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//http://blog.csdn.net/zahngjialiang/article/details/52723236
//            case R.id.more_window_zixun:      发表暂时去掉资讯

/*			if (PermissionUtils.isCameraPermission(mContext, 0x007)){
                Log.i("test","权限还没有授予xiangji");
				byCamera();
		} else {
				Log.i("test","meiyouxiangji权限还没有授予");
				Toast.makeText(mContext, "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
				ActivityCompat.requestPermissions(mContext, PERMISSIONS_CAMERA_AND_STORAGE,
						0x007);
			}

			if (ContextCompat.checkSelfPermission(mContext,
					Manifest.permission.READ_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED)
			{
				Log.i("test","权限还没有授予");
				//，需要在这里写申请权限的代码
				ActivityCompat.requestPermissions(mContext, PERMISSIONS_CAMERA_AND_STORAGE,
						0x007);

			}else {
				Log.i("test","权限已经被授予");
				//权限已经被授予，在这里直接写要执行的相应方法即可
			}*/

                /*Intent intent = new Intent(mContext, SelectPictureActivity.class);
                intent.putExtra("isfrist", true);
                intent.putExtra("intent_max_num", 9);
                intent.putExtra("type", 5);
                mContext.startActivity(intent);
                dismiss();
                break;*/
            case R.id.more_window_cheyouquan:
                Intent intent2 = new Intent(mContext, SelectPictureActivity.class);
                intent2.putExtra("isfrist", true);
                intent2.putExtra("intent_max_num", 9);
                intent2.putExtra("type", 6);
                mContext.startActivity(intent2);
                dismiss();
                break;
            case R.id.more_window_shaiche:
                Intent intent3 = new Intent(mContext, SelectPictureActivity.class);
                intent3.putExtra("isfrist", true);
                intent3.putExtra("intent_max_num", 9);
                intent3.putExtra("type", 7);
                mContext.startActivity(intent3);
                dismiss();
                break;
            case R.id.more_window_wenda:
                Intent intent4 = new Intent(mContext, SelectPictureActivity.class);
                intent4.putExtra("isfrist", true);
                intent4.putExtra("intent_max_num", 9);
                intent4.putExtra("type", 9);
                mContext.startActivity(intent4);
                dismiss();
                break;
            default:
                break;
        }
    }

    private String imagePath;

    public void byCamera() {
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (savePath == null || "".equals(savePath)) {
            Log.e("test", "无法保存照片，请检查SD卡是否挂载");
//			System.out.println("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //照片命名
        String fileName = timeStamp + ".jpg";
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);
        //该照片的绝对路径
        imagePath = savePath + fileName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mContext.startActivityForResult(intent, 0x008);
    }

    public void destroy() {
        if (null != overlay) {
            overlay.recycle();
            overlay = null;
            System.gc();
        }
        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }

}
