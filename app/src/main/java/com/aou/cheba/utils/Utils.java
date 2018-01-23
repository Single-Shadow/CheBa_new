package com.aou.cheba.utils;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	
	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources){
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
		return (int) px;
	}

	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}
	public static String[] substringsBetween(String str, String open, String close) {
		if(str != null && !isEmpty(open) && !isEmpty(close)) {
			int strLen = str.length();
			if(strLen == 0) {
				return new String[0];
			} else {
				int closeLen = close.length();
				int openLen = open.length();
				ArrayList list = new ArrayList();

				int end;
				for(int pos = 0; pos < strLen - closeLen; pos = end + closeLen) {
					int start = str.indexOf(open, pos);
					if(start < 0) {
						break;
					}

					start += openLen;
					end = str.indexOf(close, start);
					if(end < 0) {
						break;
					}

					list.add(str.substring(start, end));
				}

				return list.isEmpty()?null:(String[])list.toArray(new String[list.size()]);
			}
		} else {
			return null;
		}
	}

	public static String substringBetween(String str, String open, String close) {
		if(str != null && open != null && close != null) {
			int start = str.indexOf(open);
			if(start != -1) {
				int end = str.indexOf(close, start + open.length());
				if(end != -1) {
					return str.substring(start + open.length(), end);
				}
			}

			return null;
		} else {
			return null;
		}
	}

	public  static  void i(String s){
		Log.i("test",s);
	}

}
