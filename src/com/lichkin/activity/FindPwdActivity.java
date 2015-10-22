package com.lichkin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FindPwdActivity extends Activity {
	@InjectView(R.id.text_phone)
	EditText mTxtPhon;
	@InjectView(R.id.text_verify_code)
	EditText mTxtCode;
	@InjectView(R.id.text_new_pwd)
	EditText mTxtPwd;
	@InjectView(R.id.text_conform_new_pwd)
	EditText mTxtConfirmPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_pwd);
		ButterKnife.inject(this);
		getWindow().setBackgroundDrawable(null);//remove window bg
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_pwd, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@OnClick(R.id.btn_send_code)
	public void sendCode(){
		
	}
	
	@OnClick(R.id.find_pwd_back)
	public void back() {
		finish();
	}
	
	@OnClick(R.id.btn_find)
	public void find(){
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(R.anim.scale_fade_in,
					R.anim.slide_out_to_right);
		}
	}
}
