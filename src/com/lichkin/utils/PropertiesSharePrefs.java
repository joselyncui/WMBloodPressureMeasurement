package com.lichkin.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PropertiesSharePrefs {

	public static final String SP_NAME = "prefs_properties";
	public static final String TYPE_AUTH = "auth";
	public static final String TYPE_LOGIN = "login";
	public static final String TYPE_CARD = "card";
	public static final String TYPE_USER_PHONE = "phone_number";
	public static final String TYLE_USER_PWD = "pwd";

	private SharedPreferences mSharePreferences;
	private Context mContext;
	private static PropertiesSharePrefs mState;

	private PropertiesSharePrefs(Context context) {
		this.mContext = context;
		this.mSharePreferences = mContext.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
	}

	public static PropertiesSharePrefs getInstance(Context context) {
		if (mState == null) {
			mState = new PropertiesSharePrefs(context);
		}
		return mState;
	}

	public void saveProperty(String type, boolean value) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putBoolean(type, value);
		editor.commit();
	}

	public void saveProperty(String type, String value) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putString(type, value);
		editor.commit();
	}

	public boolean getProperty(String type, boolean defaultValue) {
		return mSharePreferences.getBoolean(type, defaultValue);
	}

	public String getProperty(String type, String defaultValue) {
		return mSharePreferences.getString(type, defaultValue);
	}

}
