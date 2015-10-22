package com.lichkin.fragments;

import android.content.Context;

import com.lichkin.activity.R;
import com.lichkin.entity.DeviceInfo;

public class TypeFactory {

	private static BaseResultFragment mBPResultFragment;
	private static BaseResultFragment mBSResultFragment;
	private static BaseResultFragment mFHResultFragment;

	private static BaseHistoryFragment mBPHistoryFragment;
	private static BaseHistoryFragment mBSHistoryFragment;
	private static BaseHistoryFragment mFHHistoryFragment;

	public static BaseResultFragment getResultFragment(String type) {
		if (type.equals(DeviceInfo.TYPE_BS)) {
			if (mBSResultFragment == null)
				mBSResultFragment = new BSResultFragment();
			return mBSResultFragment;
		} else if (type.equals(DeviceInfo.TYPE_BP)) {
			if (mBPResultFragment == null)
				mBPResultFragment = new BPResultFragment();
			return mBPResultFragment;
		} else if (DeviceInfo.TYPE_FH.equals(type)) {
			if (mFHResultFragment == null)
				mFHResultFragment = new FHResultFragment();
			return mFHResultFragment;
		}
		return null;
	}

	public static BaseHistoryFragment getHistoryFragment(String type) {
		if (type.equals(DeviceInfo.TYPE_BS)) {
			if (mBSHistoryFragment == null)
				mBSHistoryFragment = new BSHistoryFragment();
			return mBSHistoryFragment;
		} else if (type.equals(DeviceInfo.TYPE_BP)) {
			if (mBPHistoryFragment == null)
				mBPHistoryFragment = new BPHistoryFragment();
			return mBPHistoryFragment;
		} else if (DeviceInfo.TYPE_FH.equals(type)) {
			if (mFHHistoryFragment == null)
				mFHHistoryFragment = new FHHistoryFragment();
			return mFHHistoryFragment;
		}
		return null;
	}

	public static String getTitleByType(Context context, String type) {
		String title = "";
		switch (type) {
		case DeviceInfo.TYPE_BP:
			title = context.getResources().getString(R.string.bp_text);
			break;
		case DeviceInfo.TYPE_BS:
			title = context.getResources().getString(R.string.bs_text);
			break;
		case DeviceInfo.TYPE_FH:
			title = context.getResources().getString(R.string.fh_text);
			break;
		}
		return title;
	}

}
