package com.lichkin.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lichkin.utils.SystemUtils;

public class NetChangeReceiver extends BroadcastReceiver {

	private NetChangeCallBack mCallback;
	private int mLastType = -10;
	private static IntentFilter mIntentFilter;
	private static NetChangeReceiver mReceiver;

	private NetChangeReceiver() {
	};

	private NetChangeReceiver(NetChangeCallBack callback) {
		this.mCallback = callback;
	}

	public static NetChangeReceiver getInstance(NetChangeCallBack callback) {
		if (mReceiver == null) {
			mReceiver = new NetChangeReceiver();
		}
		mReceiver.mCallback = callback;
		return mReceiver;
	}

	public interface NetChangeCallBack {
		void onChange(int netType);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int netType = SystemUtils.getConnectState(context);
		if (mLastType != -10 && mLastType != netType) {
			mCallback.onChange(netType);
		}
		mLastType = netType;
	}

	public static IntentFilter getIntentFilter() {
		if (mIntentFilter == null) {
			mIntentFilter = new IntentFilter();
			mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		}
		return mIntentFilter;
	}

}
