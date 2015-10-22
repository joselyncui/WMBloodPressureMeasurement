package com.lichkin.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

public class SystemUtils {

	public static final int TYPE_NONE = -1;
	public static final int TYPE_GPRS = 0;
	public static final int TYPE_WIFI = 1;

	public static float getDensity(Context mContext) {
		DisplayMetrics metric = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.density;
	}

	/**
	 * check existing Internet connection
	 * 
	 * @param _context
	 * @return
	 */
	public static final int getConnectState(final Context _context) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
			return TYPE_WIFI;
		}
		if (mobNetInfo != null && mobNetInfo.isConnected()) {
			return TYPE_GPRS;
		}
		return SystemUtils.TYPE_NONE;
	}

}
