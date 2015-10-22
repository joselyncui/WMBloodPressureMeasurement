package com.lichkin.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.lichkin.blecore.BluetoothPrepare;

/**
 * 
 * @author Like
 * 
 */
public class BLEBaseActivity extends ActionBarActivity {

	// request code to open bluetooth
	public static int REQUEST_ENABLE_BT = 1;

	protected BluetoothAdapter mBluetoothAdapter;
	protected BluetoothPrepare mBluetoothPrepare;
	protected Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = BLEBaseActivity.this;
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mBluetoothPrepare = BluetoothPrepare.getInstance(mContext,
				mBluetoothAdapter);
		// 如果手机不支持BLE，关闭应用
		if (!mBluetoothPrepare.checkSupport()) {
			String remindStr = getResources().getString(
					R.string.remind_device_not_support);
			Toast.makeText(mContext, remindStr, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 每次开启应用时检查蓝牙是否开启
		requestBluetooth();
		// 注册接受者，监听蓝牙是否保持开启
		registerReceiver(mBleStateReceiver, makeBleStateIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBleStateReceiver);
	}

	/**
	 * 请求打开蓝牙
	 */
	private void requestBluetooth() {
		if (!mBluetoothPrepare.isBluetoothOpen()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			// 如果用户不同意打开蓝牙，关闭应用
			if (resultCode == RESULT_CANCELED) {
				String remindStr = getResources().getString(
						R.string.remind_ble_must_open);
				Toast.makeText(mContext, remindStr, Toast.LENGTH_SHORT).show();
				finish();
				System.exit(0);
			}
		}
	}

	// 监听手机蓝牙状态，如果中途关闭蓝牙，再次请求
	private final BroadcastReceiver mBleStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			requestBluetooth();
		}
	};

	private static IntentFilter makeBleStateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
		return intentFilter;
	}

}
