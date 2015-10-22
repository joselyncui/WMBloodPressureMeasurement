package com.lichkin.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceInfo implements Parcelable {

	public static final String INTENT_TYPE = "type";
	public static final String TYPE_BP = "BLOOD_PRESSURE";// ÑªÑ¹
	public static final String TYPE_BS = "BLOOD_SUGAR";// ÑªÌÇ
	public static final String TYPE_FH = "FETAL_HEART";// Ì¥ÐÄÒÇ

	public int id;
	public String type;
	public String name;
	public String address;

	public DeviceInfo() {
	}

	public DeviceInfo(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}

	public DeviceInfo(String type, String name, String address) {
		super();
		this.type = type;
		this.name = name;
		this.address = address;
	}

	public DeviceInfo(int id, String type, String name, String address) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.address = address;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(type);
		dest.writeString(address);
	}

	public static final Parcelable.Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {

		@Override
		public DeviceInfo createFromParcel(Parcel source) {
			DeviceInfo deviceInfo = new DeviceInfo();
			deviceInfo.id = source.readInt();
			deviceInfo.name = source.readString();
			deviceInfo.type = source.readString();
			deviceInfo.address = source.readString();
			return deviceInfo;
		}

		@Override
		public DeviceInfo[] newArray(int size) {
			return null;
		}
	};

}
