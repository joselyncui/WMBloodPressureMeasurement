package com.lichkin.blecore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BleBroadcastReceiver extends BroadcastReceiver {

	private IHandleConnect mHandleConnect;

	private static BleBroadcastReceiver mReceiver;
	private static IntentFilter mIntentFilter;

	private BleBroadcastReceiver() {
	}

	public static BleBroadcastReceiver getInstance(
			BluetoothLeService bluetoothLeService, IHandleConnect handleConnect) {
		if (mReceiver == null)
			mReceiver = new BleBroadcastReceiver();
		mReceiver.mHandleConnect = handleConnect;
		return mReceiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
			mHandleConnect.handleConnect();
		} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
			String reason = intent.getStringExtra(BluetoothLeService.DISCONNECT_REASON);
			System.out.println("disconnect, 原因：" + reason);
			mHandleConnect.handleDisconnect();
		} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
			String extraData = intent
					.getStringExtra(BluetoothLeService.EXTRA_DATA);
			mHandleConnect.handleGetData(extraData);
		} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
				.equals(action)) {
			mHandleConnect.handleServiceDiscover();
		}
	}

	/**
	 * 为广播接收者创建意图过滤器
	 * 
	 * @return
	 */
	public static IntentFilter getIntentFilter() {
		if (mIntentFilter == null) {
			mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
			mIntentFilter
					.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
			mIntentFilter
					.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
			mIntentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		}
		return mIntentFilter;
	}

}
