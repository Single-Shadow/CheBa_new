package com.aou.cheba.utils;

import android.util.Log;


public class MyDebug {
    static int showLength=3900;
    static String tag="test";

    // 使用Log来显示调试信息,因为log在实现上每个message有4k字符长度限制
    // 所以这里使用自己分节的方式来输出足够长度的message
    public static void show(String str) {
        str = str.trim();
        int index = 0;
        int maxLength = 4000;
        String sub;
        while (index < str.length()) {
            // java的字符不允许指定超过总的长度end
            if (str.length() <= index + maxLength) {
                sub = str.substring(index);
            } else {
                sub = str.substring(index, maxLength);
            }

            index += maxLength;
            Log.i("test", sub.trim());
        }
    }

    /**
     * 分段打印出较长log文本
     * @param logContent  打印文本
     * @param showLength  规定每段显示的长度（AndroidStudio控制台打印log的最大信息量大小为4k）
     * @param tag         打印log的标记
     */
    public static void showLargeLog(String logContent){
        if(logContent.length() > showLength){
            String show = logContent.substring(0, showLength);
            Log.i(tag, show);
            /*剩余的字符串如果大于规定显示的长度，截取剩余字符串进行递归，否则打印结果*/
            if((logContent.length() - showLength) > showLength){
                String partLog = logContent.substring(showLength,logContent.length());
                showLargeLog(partLog);
            }else{
                String printLog = logContent.substring(showLength, logContent.length());
                Log.i(tag, printLog);
            }

        }else{
            Log.i(tag, logContent);
        }
    }

}