package com.aou.cheba.view;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/9/19.
 */
public class MyToast {

    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static Handler h=new Handler();
    public static void showToast(final Context context, final String s) {

        h.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    try {
                        toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e) {

                    }

                    oneTime = System.currentTimeMillis();
                } else {
                    twoTime = System.currentTimeMillis();
                    if (s.equals(oldMsg)) {
                        if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                            toast.show();
                        }
                    } else {
                        oldMsg = s;
                        toast.setText(s);
                        toast.show();
                    }
                }
                oneTime = twoTime;
            }
        });

    }


    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

}
