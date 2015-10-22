package com.lichkin.fragments;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lichkin.blecore.BluetoothLeService;
import com.lichkin.blecore.IHandleConnect;

public abstract class BaseResultFragment extends Fragment implements
		IHandleConnect {

	public final static int BTN_STATE_AVAILABLE = 0;
	public final static int BTN_STATE_UNAVAILABLE = 1;
	public final static int BTN_STATE_UNAVAILABLE_WAITING = 2;

	protected BluetoothLeService mBluetoothLeService;
	protected Context mContext;
	protected ActivityCallback mCallback;

	public BaseResultFragment() {
	}

	public BaseResultFragment(BluetoothLeService bluetoothLeService) {
		this.mBluetoothLeService = bluetoothLeService;
	}

	public interface ActivityCallback {

		void showResult(String data);

		void setButtonState(int state);

		void closeActivity();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mCallback = (ActivityCallback) getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void onBindService(BluetoothLeService bluetoothLeService) {
		this.mBluetoothLeService = bluetoothLeService;
	}

	protected BluetoothGattCharacteristic getInfoCharacteristic(
			String serviceUUID, String characUUID) {
		BluetoothGattService service = mBluetoothLeService
				.getServiceByUuid(serviceUUID);
		return service.getCharacteristic(UUID.fromString(characUUID));
	}

	public abstract void record();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

}
