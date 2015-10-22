package com.lichkin.entity;

import android.content.Intent;

import com.lichkin.activity.R;

public class SettingData {
	public String settingName;
	public String subContent;
	public boolean hasSubContent;
	public boolean hasMoreContent;
	public int src = R.drawable.setting_go;
	public Intent targetIntent;

	public SettingData(String settingName, String subContent) {
		this.settingName = settingName;
		this.subContent = subContent;
		this.hasMoreContent = false;
		this.hasSubContent = true;
	}

	public SettingData(String settingName) {
		this.settingName = settingName;
		this.hasMoreContent = false;
		this.hasSubContent = false;
	}

	public SettingData(String settingName, Intent targetIntent) {
		this.settingName = settingName;
		this.targetIntent = targetIntent;
		this.hasMoreContent = true;
		this.hasSubContent = false;
	}

	public SettingData(String settingName, String subContent,
			Intent targetIntent) {
		this.settingName = settingName;
		this.subContent = subContent;
		this.hasMoreContent = true;
		this.hasSubContent = true;
	}

}
