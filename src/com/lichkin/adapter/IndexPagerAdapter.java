package com.lichkin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lichkin.activity.R;
import com.lichkin.customview.PagerSlidingTitleIconTabStrip.TitleIconTabProvider;
import com.lichkin.fragments.DeviceFragment;
import com.lichkin.fragments.IndexFragment;
import com.lichkin.fragments.SettingFragment;

public class IndexPagerAdapter extends FragmentStatePagerAdapter implements
		TitleIconTabProvider {

	private IndexFragment mIndexFragment = new IndexFragment();
	private DeviceFragment mDeviceFragment = new DeviceFragment();
	private SettingFragment mSettingFragment = new SettingFragment();

	public IndexPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	private Fragment[] mFragments = new Fragment[] { mIndexFragment,
			mDeviceFragment, mSettingFragment };
	private String[] mTitles = new String[] { "首   页", "设   备", "设   置" };
	private int[] mIcons = new int[] { R.drawable.home_tab_selector,
			R.drawable.device_tab_selector, R.drawable.setting_tab_selector };

	@Override
	public Fragment getItem(int index) {
		return mFragments[index];
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles[position];
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public int getPageIconResId(int position) {
		return mIcons[position];
	}

	public IndexFragment getIndexFragment() {
		return mIndexFragment;
	}

	public DeviceFragment getDeviceFragment() {
		return mDeviceFragment;
	}

	public SettingFragment getSettingFragment() {
		return mSettingFragment;
	}

}
