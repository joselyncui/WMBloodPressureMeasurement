package com.lichkin.blecore;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

/**
 * 负责扫描的工具类
 * 
 * @author Like
 * 
 */
public class DeviceScanner {

	public final static int STATE_BEGIN_SCAN = 0;
	public final static int STATE_END_SCAN = 1;

	// 扫描时间，超过这个时间没有扫描到设备，认定扫描失败
	private final static int SCAN_PERIOD = 5000;
	private static DeviceScanner mDeviceScanner = null;
	// 存放扫描到的设备
	private List<BluetoothDevice> mDevices = null;
	private Handler mHandler = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private boolean mScanning = false;
	private ScanCallback mCallback = null;

	private DeviceScanner(BluetoothAdapter bluetoothAdapter) {
		this.mHandler = new Handler();
		this.mBluetoothAdapter = bluetoothAdapter;
		this.mDevices = new ArrayList<BluetoothDevice>();
	}

	public static DeviceScanner getInstance(BluetoothAdapter bluetoothAdapter,
			ScanCallback callback) {
		if (mDeviceScanner == null)
			mDeviceScanner = new DeviceScanner(bluetoothAdapter);
		mDeviceScanner.mCallback = callback;
		return mDeviceScanner;
	}

	public interface ScanCallback {

		void onScanStateChange(int scanState, List<BluetoothDevice> mDevices);

	}

	/**
	 * 控制扫描的开始与结束
	 * 
	 * @param enable
	 *            如果为true开始扫描，false结束扫描
	 */
	@SuppressWarnings("deprecation")
	public void scanLeDevice(final boolean enable) {
		if (enable) {
			if (mScanning)
				return;
			mDevices.clear();
			// 在指定时间之后停止扫描
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mScanning)
						scanLeDevice(false);
				}
			}, SCAN_PERIOD);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			mCallback.onScanStateChange(STATE_BEGIN_SCAN, null);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mCallback.onScanStateChange(STATE_END_SCAN, mDevices);
		}
	}

	private LeScanCallback mLeScanCallback = new LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			if (!mDevices.contains(device))
				mDevices.add(device);
		}
	};

	/**
	 * 用于判断是否处于正在扫描状态
	 * 
	 * @return 如果正在扫描返回true，否在返回false
	 */
	public boolean isScanning() {
		return mScanning;
	}

}
