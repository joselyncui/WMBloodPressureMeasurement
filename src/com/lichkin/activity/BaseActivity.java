package com.lichkin.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lichkin.network.CheckNeedUploadTask;
import com.lichkin.network.NetChangeReceiver;
import com.lichkin.network.NetChangeReceiver.NetChangeCallBack;

public class BaseActivity extends BLEBaseActivity implements NetChangeCallBack {

	protected AlertDialog mAlertDialog;
	private NetChangeReceiver mReceiver;
	protected Button btnUpdYes, btnUpdNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setBackgroundDrawable(null);//remove window bg
		
		if (mAlertDialog == null) {
			createUploadDialog();
		}

		if (mReceiver == null)
			mReceiver = NetChangeReceiver.getInstance(this);
	}

	private void createUploadDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		LayoutInflater inflater = this.getLayoutInflater();
		View mainView = inflater.inflate(R.layout.dialog_ask_upload,
				new LinearLayout(this), false);
		btnUpdYes = (Button) mainView.findViewById(R.id.btn_upload_yes);
		btnUpdNo = (Button) mainView.findViewById(R.id.btn_upload_no);

		mAlertDialog = builder.create();
		mAlertDialog.setView(mainView, 0, 0, 0, 0);
		mAlertDialog.setCanceledOnTouchOutside(true);
		mAlertDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, NetChangeReceiver.getIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
		if (isFinishing()) {
			overridePendingTransition(R.anim.scale_fade_in,
					R.anim.slide_out_to_right);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onChange(int netType) {
		new CheckNeedUploadTask(mContext, mAlertDialog, btnUpdNo, btnUpdYes,
				netType).execute();
	}
}
