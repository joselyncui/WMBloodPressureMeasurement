package com.lichkin.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lichkin.activity.R;

public class CustomTabView extends HorizontalScrollView {

	private int mDefaultTextSize = 15;

	private ViewPager mPager;
	private LinearLayout mTabsContainer;
	private int mHeight;
	private boolean mHasAddedTab = false;
	private OnPageChangeListener mListener;

	public CustomTabView(Context context) {
		super(context);
	}

	public CustomTabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFillViewport(true);
		setWillNotDraw(false);

		mTabsContainer = new LinearLayout(context);
		mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		mTabsContainer.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mTabsContainer);
	}

	private void addTabs() {
		int tabCount = mPager.getAdapter().getCount();
		int paddingTop = (int) (mHeight * 0.5);
		for (int i = 0; i < tabCount; i++) {
			final int position = i;
			TextView tab = new TextView(getContext());
			tab.setText(mPager.getAdapter().getPageTitle(position));
			tab.setTextColor(getResources().getColor(R.color.white));
			tab.setTextSize(mDefaultTextSize);
			tab.setPadding(0, paddingTop, 0, 0);
			tab.setGravity(Gravity.CENTER_HORIZONTAL);
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPager.setCurrentItem(position);
				}
			});
			tab.setSingleLine();
			mTabsContainer.addView(tab, position,
					new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT,
							1.0f));
		}
	}

	public void setViewPager(ViewPager pager) {
		this.mPager = pager;
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (mListener != null)
					mListener.onPageSelected(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				if (mListener != null)
					mListener.onPageScrolled(position, positionOffset,
							positionOffsetPixels);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (mListener != null)
					mListener.onPageScrollStateChanged(state);
			}
		});
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.mListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mHeight = getHeight();
		if (!mHasAddedTab && mPager != null) {
			addTabs();
			mHasAddedTab = true;
		}
	}

}
