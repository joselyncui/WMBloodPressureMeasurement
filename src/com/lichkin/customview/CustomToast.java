package com.lichkin.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.lichkin.activity.R;

public class CustomToast {
	private WindowManager wdm;
	private double time;
	private View mView;
	private WindowManager.LayoutParams params;
	private Handler mHandler;
	private Runnable mRun;
	private boolean isAttach = false;
	private int animStyleId = R.style.CustomToast;

	@SuppressLint("ShowToast")
	private CustomToast(Handler mHandler,Context context, String text, double time) {
		wdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.mHandler = mHandler;
		
		mRun = new MRunnable();
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		mView = toast.getView();

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		params.windowAnimations = animStyleId;//显示和�?出动�?
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		params.y = 200;
		this.time = time;
	}

	public static CustomToast makeText(Handler mHandler,Context context, String text, double time) {
		CustomToast toastCustom = new CustomToast(mHandler,context, text, time);
		return toastCustom;
	}

	public void show() {
		if(isAttach) 
			return;
		isAttach = true;
		wdm.addView(mView, params);
		mHandler.postDelayed(mRun, (long)time*1000);
	}

	public void cancel() {
		if(isAttach) {
			wdm.removeView(mView);
			isAttach = false;
		}
		mHandler.removeCallbacks(mRun);
	}
	
	class MRunnable implements Runnable{

		@Override
		public void run() {
			isAttach = false;
			wdm.removeView(mView);
		}
		
	}
}
