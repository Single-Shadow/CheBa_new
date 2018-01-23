package com.aou.cheba.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/9/26.
 */
public class MyGrideView extends GridView {
    public MyGrideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGrideView(Context context) {
        super(context);
    }

    public MyGrideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//测量   scrollview 嵌套 gridview 显示不全
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
