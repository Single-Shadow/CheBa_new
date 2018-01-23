package com.aou.cheba.view;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Wang on 2017/2/22.
 */
public class MyImage extends ImageView {
    public MyImage(Context context) {
        super(context);
    }

//    请求父类不拦截   会屏蔽掉上层的touch事件
/*    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }*/
}
