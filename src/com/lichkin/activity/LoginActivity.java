package com.lichkin.activity;

import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTouch;

import com.lichkin.customview.ClearEditText;
import com.lichkin.db.HistoryDBManager;
import com.lichkin.db.UserInfoDBManager;
import com.lichkin.entity.LoginEntity;
import com.lichkin.entity.RequestEntity;
import com.lichkin.entity.Response;
import com.lichkin.network.NetworkFactory;
import com.lichkin.utils.DialogUtils;
import com.lichkin.utils.MD5Utils;
import com.lichkin.utils.PropertiesSharePrefs;
import com.lichkin.utils.SystemUtils;

/**
 * 
 * @author Like
 * 
 */
public class LoginActivity extends ActionBarActivity {

	@InjectView(R.id.login_scroll)
	ScrollView mScrollView;
	@InjectView(R.id.scroll_inner)
	View mInner;
	@InjectView(R.id.txt_username)
	ClearEditText mUserName;
	@InjectView(R.id.txt_pwd)
	ClearEditText mPwd;

	private Context mContext;
	private PropertiesSharePrefs mProperties;
	private Handler mHandler;
	private UserInfoDBManager mUserInfoDBManager;
	private HistoryDBManager mHistoryDBManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.inject(this);
		getWindow().setBackgroundDrawable(null);//remove window bg

		mHandler = new Handler();
		mContext = LoginActivity.this;
		mProperties = PropertiesSharePrefs.getInstance(mContext);
		mUserInfoDBManager = UserInfoDBManager.getInstance(mContext);
		mHistoryDBManager = HistoryDBManager.getInstance(mContext);

	}

	@Override
	protected void onResume() {
		super.onResume();
		String phone = PropertiesSharePrefs.getInstance(this).getProperty(
				PropertiesSharePrefs.TYPE_USER_PHONE, "");
		mUserName.setText(phone);
	}

	@OnClick({ R.id.txt_username, R.id.txt_pwd })
	public void clickUsername(View v) {
		if (v.isFocused()) {
			scrollToBottom();
		}
	}

	@OnFocusChange({ R.id.txt_pwd, R.id.txt_username })
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			scrollToBottom();
		}

		if (!hasFocus) {
			switch (v.getId()) {
			case R.id.txt_username:
				String loginName = mUserName.getText().toString();
				verifyName(loginName);
				break;
			case R.id.txt_pwd:
				String loginPwd = mPwd.getText().toString();
				verifyPwd(loginPwd);
			default:
				break;
			}
		}
	}

	private void scrollToBottom() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				if (mScrollView == null || mInner == null) {
					return;
				}
				int offset = mInner.getMeasuredHeight()
						- mScrollView.getHeight();
				if (offset < 0) {
					offset = 0;
				}
				mScrollView.scrollTo(0, offset);
			}
		}, 100);
	}

	@OnClick(R.id.register_txt)
	public void clickRegister(View v) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.btn_login)
	public void login(View v) {
		if (verify()) {
			new LoginTask().execute();
		}
	}

	@OnClick(R.id.add_back)
	public void back(View v) {
		finish();
	}

	private boolean verify() {
		String name = mUserName.getText().toString().trim();
		String pwd = mPwd.getText().toString().trim();
		if ("".equals(name) || "".equals(pwd)) {
			DialogUtils.showToast(this, getString(R.string.login_check),
					DialogUtils.ERROR);
			return false;
		}
		return true;
	}

	/**
	 * 验证用户名格式
	 * 
	 * @param name
	 * @return
	 */
	private boolean verifyName(String name) {
		final String[] msg = new String[1];
		boolean result = true;
		if ("".equals(name)) {
			msg[0] = "登录名不能为空";
			result = false;
		} else if (!Pattern.matches("^1[3-8]{1}\\d{9}$",
				name)) {
			msg[0] = "登录名格式不正确";
			result = false;
		}

		if (!result) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					DialogUtils.showToast(LoginActivity.this, msg[0],
							DialogUtils.ERROR);
				}
			}, 400);
		}

		return result;
	}

	/**
	 * 验证输入密码格式
	 * 
	 * @param pwd
	 * @return
	 */
	private boolean verifyPwd(String pwd) {
		boolean result = true;
		final String[] msg = new String[1];
		if ("".equals(pwd)) {
			msg[0] = "密码不能为空";
			result = false;
		} 

		if (!result) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					DialogUtils.showToast(LoginActivity.this, msg[0],
							DialogUtils.ERROR);
				}
			}, 400);
		}

		return result;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(R.anim.scale_fade_in,
					R.anim.slide_out_to_right);
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, Response> {

		private ProgressDialog mProgress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = DialogUtils.createProgressDialog(mContext, "",
					"正在登录...");
			mProgress.show();
		}

		@Override
		protected Response doInBackground(Void... params) {
			String userName = mUserName.getText().toString().trim();
			String pwd = MD5Utils.string2MD5(mPwd.getText().toString());
			LoginEntity loginEntity = new LoginEntity(userName, pwd);
			RequestEntity<LoginEntity> request = new RequestEntity<LoginEntity>(
					"test", "test", loginEntity);
			if (SystemUtils.getConnectState(mContext) == SystemUtils.TYPE_NONE)
				return null;
			Response response = null;
			try {
				response = NetworkFactory.getAuthService().login(request);
			} catch (Exception e) {

			}
			return response;
		}

		@Override
		protected void onPostExecute(final Response result) {
			mProgress.dismiss();
			if (result == null) {
				DialogUtils.showToast(LoginActivity.this,
						getString(R.string.network_error), DialogUtils.ERROR);
				return;
			}
			if (result.code == 0) {
				mProperties.saveProperty(PropertiesSharePrefs.TYPE_LOGIN, true);
				mProperties.saveProperty(PropertiesSharePrefs.TYPE_CARD,
						result.datas.userInfo.userCard);
				mProperties.saveProperty(PropertiesSharePrefs.TYPE_USER_PHONE, result.datas.userInfo.cellphone);
				DialogUtils.showToast(LoginActivity.this,
						getString(R.string.login_success), DialogUtils.SUCCESS);
				
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						String userCard = result.datas.userInfo.userCard;
						mUserInfoDBManager.saveUser(result.datas.userInfo);
						
						mHistoryDBManager.updateBpUserCard(userCard);
						mHistoryDBManager.updateBsUserCard(userCard);
						mHistoryDBManager.updateFhUserCard(userCard);
					}
				}).start();
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				DialogUtils.showToast(LoginActivity.this, result.info,
						DialogUtils.ERROR);
			}
		}
	}

	@OnTouch(R.id.scroll_inner)
	public boolean mInnerTouch(View v) {
		v.setFocusable(true);
		v.setFocusableInTouchMode(true);
		v.requestFocus();
		// 隐藏键盘
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mUserName.getWindowToken(), 0);
		return false;
	}
}
