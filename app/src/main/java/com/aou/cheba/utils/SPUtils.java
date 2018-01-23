package com.aou.cheba.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SPUtils {

	private static SharedPreferences sp;

	public static void put(Context context, String key, Object value) {
		try {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = sp.edit();
			if (value instanceof String) {
				edit.putString(key, (String) value);
			} else if (value instanceof Integer) {
				edit.putInt(key, (int) value);
			} else if (value instanceof Boolean) {
				edit.putBoolean(key, (boolean) value);
			}
			edit.commit();
		} catch (Exception e) {
			Log.i("test","首选项存储失败");
		}


	}
	
	/**
	 * 获取字符串
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		try {
			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			return sp.getString(key, "");
		} catch (Exception e) {
			Log.i("test","首选项存储失败");
			return "";
		}
	}
	
	/**
	 * 获取整数
	 * @param context
	 * @param key
	 * @return
	 */
	public static int getInt(Context context, String key) {
		try {
			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			return sp.getInt(key, 0);
		} catch (Exception e) {
			Log.i("test","首选项存储失败");
			return 0;
		}

	}
	
	/**
	 * 获取布尔
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	/**
	 * 判断手机的网络状态
	 *
	 * @param context
	 * @return 数据网络或WiFi都会返回true
	 */
	public static boolean checkNetwork(Context context) {
		// ConnectivityManager
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// android.Manifest.permission.ACCESS_NETWORK_STATE
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}

		int type = networkInfo.getType();
		if (type == ConnectivityManager.TYPE_MOBILE
				|| type == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

}
