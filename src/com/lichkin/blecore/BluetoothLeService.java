package com.lichkin.blecore;

import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * 检测设备连接状态，发送广播
 * 
 * @author MGC09
 * 
 */
public class BluetoothLeService extends Service {

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;
	private Handler mHandler;
	private Runnable mDisconnectRunnable;
	private boolean mDelayed = false;

	// 连接状态
	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;
	
	public static final String DISCONNECT_REASON = "reason";
	public static final String DISONNECT_FROM_STATE = "接收到onConnectionStateChange变为disconnect";
	public static final String DISCONNECT_FROM_DELAY = "连接超时或连接成功后获取数据超时";

	public final static String ACTION_GATT_CONNECTED = "com.wm.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.wm.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.wm.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.wm.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.wm.bluetooth.le.EXTRA_DATA";

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
		mDisconnectRunnable = new Runnable() {
			@Override
			public void run() {
				if(mConnectionState != STATE_DISCONNECTED) {
					mDelayed = false;
					disconnect();
				}
			}
		};
	}
	
	// 连接的回调方法
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		// 连接状态发生改变
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED && mConnectionState != STATE_CONNECTED) {
				removeDelayOperation();
				// 如果连接成功，通过广播方式告知MainAcivity
				addDelayOperation(5000);
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction, "");
				mBluetoothGatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED && mConnectionState != STATE_DISCONNECTED) {
				removeDelayOperation();
				// 如果连接断开，通过广播方式告知MainActivity
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				broadcastUpdate(intentAction, DISONNECT_FROM_STATE);
			}
		};

		// 发现services
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {

			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, "");
			}
		};

		// 读取characteristic
		public void onCharacteristicRead(BluetoothGatt gatt,
				android.bluetooth.BluetoothGattCharacteristic characteristic,
				int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		};

		// 写入characteristic
		public void onCharacteristicWrite(BluetoothGatt gatt,
				android.bluetooth.BluetoothGattCharacteristic characteristic,
				int status) {
			System.out.println("on write success   "
					+ characteristic.getValue());
		};

		// characteristic改变，接受notify的数据
		public void onCharacteristicChanged(BluetoothGatt gatt,
				android.bluetooth.BluetoothGattCharacteristic characteristic) {
			removeDelayOperation();
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		};

	};

	private IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		return super.onUnbind(intent);
	}

	// 在做完操作后，需要关闭连接和服务
	public void close() {
		removeDelayOperation();
		disconnect();
		if (mBluetoothGatt == null)
			return;
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	// 发送广播
	private void broadcastUpdate(final String action, String reason) {
		final Intent intent = new Intent(action);
		intent.putExtra(DISCONNECT_REASON, reason);
		sendBroadcast(intent);
	}

	// 将数据转换成16进制后发送广播
	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		final byte[] data = characteristic.getValue();
		// 将数据转换成16进制
		if (data != null && data.length > 0) {
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data) {
				stringBuilder.append(String.format("%02x ", byteChar));
			}
			Log.i("oriData", String.valueOf(data.length) + "     " + characteristic.getUuid().toString());
			// 将数据放入intent中
			intent.putExtra(EXTRA_DATA, stringBuilder.toString().trim());
		}
		sendBroadcast(intent);
	}

	// 开启或关闭characteristic告知
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString("00002902-0000-1000-8000-00805f9b34fb"));
		if (descriptor != null) {
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	}

	// 初始化参数
	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			return false;
		}

		return true;
	}

	// 根据address连接设备
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			mConnectionState = STATE_DISCONNECTED;
			return false;
		}
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			mConnectionState = STATE_DISCONNECTED;
			return false;
		}
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				mConnectionState = STATE_DISCONNECTED;
				return false;
			}
		}
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	public boolean connect(final String address, int overtime) {
		removeDelayOperation();
		boolean result = connect(address);
		addDelayOperation(overtime);
		return result;
	}

	// 断开连接
	public void disconnect() {
		if (mBluetoothGatt == null) {
			return;
		}
		this.mBluetoothGatt.disconnect();
		if(mConnectionState != STATE_DISCONNECTED) {
			this.mConnectionState = STATE_DISCONNECTED;
			broadcastUpdate(ACTION_GATT_DISCONNECTED, "调用断开方法");
		}
	}

	public int getConnectState() {
		return mConnectionState;
	}

	// 根据UUID获取service
	public BluetoothGattService getServiceByUuid(String uuid) {
		if (mBluetoothGatt == null) {
			return null;
		}
		return mBluetoothGatt.getService(UUID.fromString(uuid));
	}

	private void removeDelayOperation() {

		if (mDelayed) {
			mHandler.removeCallbacks(mDisconnectRunnable);
			mDelayed = false;
		}
	}

	private void addDelayOperation(int delayMillis) {
		if (!mDelayed) {
			mDelayed = mHandler.postDelayed(mDisconnectRunnable, delayMillis);
		}
	}

	// 向characteristic写入value
	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.writeCharacteristic(characteristic);
	}

	// 读取characteristic中的value
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

}
