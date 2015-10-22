package com.lichkin.customview;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lichkin.activity.R;
import com.lichkin.entity.DeviceInfo;

public class DeviceIcon extends ImageView {

	public DeviceIcon(Context context) {
		super(context);
	}

	public DeviceIcon(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DeviceIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public void setType(String type) {
		
		int resId = R.drawable.bs_icon;
		if (type.equals(DeviceInfo.TYPE_BS)) {
			resId = R.drawable.bs_icon;
		} else if (type.equals(DeviceInfo.TYPE_BP)) {
			resId = R.drawable.bp_icon;
		} else {
			resId = R.drawable.fh_icon;
		}
		setImageResource(resId);
	}

}
