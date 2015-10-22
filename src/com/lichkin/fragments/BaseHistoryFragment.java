package com.lichkin.fragments;

import java.util.UUID;

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

public abstract class BaseHistoryFragment extends Fragment implements
		IHandleConnect {

	protected BluetoothLeService mBluetoothLeService;
	protected Context mContext;

	public BaseHistoryFragment() {
	}

	public BaseHistoryFragment(BluetoothLeService bluetoothLeService) {
		this.mBluetoothLeService = bluetoothLeService;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setBluetoothLeService(BluetoothLeService bluetoothLeService) {
		this.mBluetoothLeService = bluetoothLeService;
	}

	protected BluetoothGattCharacteristic getInfoCharacteristic(
			String serviceUUID, String characUUID) {
		BluetoothGattService service = mBluetoothLeService
				.getServiceByUuid(serviceUUID);
		return service.getCharacteristic(UUID.fromString(characUUID));
	}

	@Override
	public boolean handleServiceDiscover() {
		return false;
	}

}
