package com.lichkin.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.lichkin.adapter.IndexPagerAdapter;
import com.lichkin.customview.BarViewPager;
import com.lichkin.customview.CustomTabView;
import com.lichkin.fragments.DeviceFragment;
import com.lichkin.fragments.DeviceFragment.OnStateChangeListener;
import com.lichkin.network.CheckNeedUploadTask;
import com.lichkin.utils.SystemUtils;
import com.lichkin.utils.TabPagerSharePrefs;

public class MainActivity extends BaseActivity implements
		OnStateChangeListener, OnPageChangeListener {

	// request code to open bluetooth
	public static int REQUEST_ENABLE_BT = 1;

	@InjectView(R.id.main_pager)
	BarViewPager mPager;
	@InjectView(R.id.main_tab)
	CustomTabView mTabView;

	private int mBackClickTimes = 0;
	private boolean mDeviceEdit = false;
	private IndexPagerAdapter mIndexPagerAdapter;
	private TabPagerSharePrefs mTabPager;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		// 初始化Tab
		initTab();
		// 初始化全局参数
		mBackClickTimes = 0;
		mTabPager = TabPagerSharePrefs.getInstance(mContext);
		mTabView.setViewPager(mPager);
		mTabView.setOnPageChangeListener(this);

		int connectState = SystemUtils.getConnectState(mContext);
		new CheckNeedUploadTask(mContext, mAlertDialog, btnUpdYes, btnUpdNo,
				connectState).execute();

	}

	/**
	 * 初始化tab
	 */
	private void initTab() {
		mIndexPagerAdapter = new IndexPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mIndexPagerAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		int currentPage = mTabPager.getCurrentPage();
		if (currentPage != -1) {
			mTabPager.clear();
			mPager.setCurrentItem(currentPage);
		}
	}

	@Override
	public void onBackPressed() {
		if (mDeviceEdit) { // 如果设备列表处于编辑状态，点击返回时使设备列表返回普通状态
			DeviceFragment deviceFragment = mIndexPagerAdapter
					.getDeviceFragment();
			deviceFragment.resetList();
			return;
		}
		// 连续点击两次返回键，退出程序
		if (mBackClickTimes == 0) {
			String str = getResources().getString(R.string.ask_when_exit);
			Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
			mBackClickTimes = 1;
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						mBackClickTimes = 0;
					}
				}
			}.start();
			return;
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public void onStateChange(int state) {
		if (state == DeviceFragment.STATE_NORMAL) {
			this.mDeviceEdit = false;
		} else {
			this.mDeviceEdit = true;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		if (mDeviceEdit && position != 1) {
			mIndexPagerAdapter.getDeviceFragment().resetList();
		}
	}

}
