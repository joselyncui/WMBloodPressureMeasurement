package com.lichkin.blecore;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;

public class BluetoothPrepare {

	private Context mContext;
	private BluetoothAdapter mBluetoothAdapter;
	private static BluetoothPrepare mBlePrepare;

	private BluetoothPrepare(Context context, BluetoothAdapter bluetoothAdapter) {
		this.mContext = context;
		this.mBluetoothAdapter = bluetoothAdapter;
	}

	public static BluetoothPrepare getInstance(Context context,
			BluetoothAdapter bluetoothAdapter) {
		if (mBlePrepare == null) {
			mBlePrepare = new BluetoothPrepare(context, bluetoothAdapter);
		}
		return mBlePrepare;
	}

	/**
	 * 检查设备是否支持BLE
	 * 
	 * @return 如果支持返回true, 否则放回false
	 */
	public boolean checkSupport() {
		if (!mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断蓝牙是否开启
	 * 
	 * @return 开启返回true，否则返回false
	 */
	public boolean isBluetoothOpen() {
		return !(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled());
	}

}
