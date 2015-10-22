package com.lichkin.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lichkin.db.DeviceDataContract.DeviceEntry;
import com.lichkin.entity.DeviceInfo;

public class DeviceDBManager {

	private Context mContext;
	private DBHelper mDBHelper;
	private static DeviceDBManager mDeviceDBManager;

	private DeviceDBManager(Context context) {
		this.mContext = context;
		mDBHelper = new DBHelper(mContext);
	}

	public static DeviceDBManager getInstance(Context context) {
		if (mDeviceDBManager == null) {
			mDeviceDBManager = new DeviceDBManager(context);
		}
		return mDeviceDBManager;
	}

	public long addDevice(DeviceInfo deviceInfo) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DeviceEntry.COLUMN_NAME_NAME, deviceInfo.name);
		values.put(DeviceEntry.COLUMN_NAME_ADDRESS, deviceInfo.address);
		values.put(DeviceEntry.COLUMN_NAME_TYPE, deviceInfo.type);
		long newRowId;
		newRowId = db.insert(DeviceEntry.TABLE_NAME,
				DeviceEntry.COLUMN_NAME_NULLABLE, values);
		return newRowId;
	}

	public void updateDevice(DeviceInfo deviceInfo) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DeviceEntry.COLUMN_NAME_NAME, deviceInfo.name);
		values.put(DeviceEntry.COLUMN_NAME_ADDRESS, deviceInfo.address);
		values.put(DeviceEntry.COLUMN_NAME_TYPE, deviceInfo.type);
		String where = DeviceEntry.COLUMN_NAME_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(deviceInfo.id) };
		db.update(DeviceEntry.TABLE_NAME, values, where, whereArgs);
	}

	public DeviceInfo getDeviceByAddress(String address) {
		DeviceInfo deviceInfo = null;
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String[] projection = { DeviceEntry.COLUMN_NAME_ID,
				DeviceEntry.COLUMN_NAME_NAME, DeviceEntry.COLUMN_NAME_ADDRESS,
				DeviceEntry.COLUMN_NAME_TYPE };
		String selection = DeviceEntry.COLUMN_NAME_ADDRESS + "=?";
		String selectionArgs[] = new String[] { address };
		Cursor c = db.query(DeviceEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);
		if (c.moveToFirst()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_ID));
			String name = c.getString(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_NAME));
			String type = c.getString(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_TYPE));
			deviceInfo = new DeviceInfo(id, type, name, address);
		}
		return deviceInfo;
	}

	public List<DeviceInfo> getAllDevices() {
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String[] projection = { DeviceEntry.COLUMN_NAME_ID,
				DeviceEntry.COLUMN_NAME_NAME, DeviceEntry.COLUMN_NAME_ADDRESS,
				DeviceEntry.COLUMN_NAME_TYPE };
		Cursor c = db.query(DeviceEntry.TABLE_NAME, projection, null, null,
				null, null, null);
		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_ID));
			String name = c.getString(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_NAME));
			String type = c.getString(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_TYPE));
			String address = c.getString(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_ADDRESS));
			devices.add(new DeviceInfo(id, type, name, address));
		}
		return devices;
	}

	public List<DeviceInfo> getDeviceByType(String type) {
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String[] projection = { DeviceEntry.COLUMN_NAME_ID,
				DeviceEntry.COLUMN_NAME_NAME, DeviceEntry.COLUMN_NAME_ADDRESS,
				DeviceEntry.COLUMN_NAME_TYPE };
		String where = DeviceEntry.COLUMN_NAME_TYPE + " = ?";
		String[] whereArgs = new String[] { type };
		Cursor c = db.query(DeviceEntry.TABLE_NAME, projection, where,
				whereArgs, null, null, null);
		while (c.moveToNext()) {
			int id = c.getInt(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_ID));
			String name = c.getString(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_NAME));
			String address = c.getString(c
					.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_ADDRESS));
			devices.add(new DeviceInfo(id, type, name, address));
		}
		return devices;
	}

	public void removeDeviceById(int id) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String selection = DeviceEntry.COLUMN_NAME_ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };
		db.delete(DeviceEntry.TABLE_NAME, selection, selectionArgs);
	}

	public void removeDeviceByAddress(String address) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		String selection = DeviceEntry.COLUMN_NAME_ADDRESS + " = ?";
		String[] selectionArgs = { address };
		db.delete(DeviceEntry.TABLE_NAME, selection, selectionArgs);
		db.close();
	}

}
