package com.lichkin.activity;

import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTouch;

import com.google.gson.Gson;
import com.lichkin.entity.MessageEntity;
import com.lichkin.entity.RegisterEntity;
import com.lichkin.entity.RequestEntity;
import com.lichkin.entity.Response;
import com.lichkin.network.NetworkFactory;
import com.lichkin.utils.DialogUtils;
import com.lichkin.utils.HttpUtils;
import com.lichkin.utils.InputLowerToUpper;
import com.lichkin.utils.MD5Utils;
import com.lichkin.utils.PropertiesSharePrefs;
import com.lichkin.utils.SystemUtils;

public class RegisterActivity extends ActionBarActivity implements
		OnCheckedChangeListener {

	@InjectView(R.id.btn_send_code)
	Button mbtnSendCode;
	@InjectView(R.id.btn_reg)
	Button mBtnReg;
	@InjectView(R.id.reg_code_hint)
	TextView mAuthCode;
	@InjectView(R.id.reg_name)
	EditText mRegName;
	@InjectView(R.id.reg_phone)
	EditText mRegPhone;
	@InjectView(R.id.reg_code)
	EditText mRegCode;
	@InjectView(R.id.reg_identity)
	EditText mRegIdentity;
	@InjectView(R.id.reg_psw)
	EditText mRegPsw;
	@InjectView(R.id.reg_conform_psw)
	EditText mRegConformPsw;
	@InjectView(R.id.reg_scroll)
	ScrollView mScrollView;
	@InjectView(R.id.reg_content)
	View mInner;
	@InjectView(R.id.reg_checkbox)
	CheckBox mRegCheckBox;
	@InjectView(R.id.reg_service_item)
	TextView mRegServiceItem;

	private CountDownTimer mCountTimer;
	private Context mContext;
	private Handler mHandler;
	private String verifyCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ButterKnife.inject(this);
		getWindow().setBackgroundDrawable(null);//remove window bg

		mRegCheckBox.setOnCheckedChangeListener(this);
		mContext = this;
		mHandler = new Handler();
		serviceItemLink();
		
		mRegIdentity.setTransformationMethod(new InputLowerToUpper());//转换大小写
	}
	
	

	private void serviceItemLink() {
		// 创建一个 SpannableString对象
		SpannableString sp = new SpannableString("《服务条款》");
		// 设置超链接
		sp.setSpan(new URLSpan(
				"http://www.leadingtechmed.cn/agreements/service.html"), 0, 6,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置高亮样式二
		sp.setSpan(
				new ForegroundColorSpan(getResources().getColor(
						R.color.colorPrimary)), 0, 6,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

		// SpannableString对象设置给TextView
		mRegServiceItem.setText(sp);
		// 设置TextView可点击
		mRegServiceItem.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@OnClick(R.id.btn_send_code)
	public void sendCode(View view) {
		
		final String phone = mRegPhone.getText().toString();
		if (!verifyPhone(phone)) {
			return;
		}

		if (SystemUtils.getConnectState(mContext) == SystemUtils.TYPE_NONE) {
			DialogUtils.showToast(this, getString(R.string.network_error),
					DialogUtils.ERROR);
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {

				MessageEntity msgEntity = new MessageEntity("test", "test",
						phone);
				try {
					verifyCode = NetworkFactory.getAuthService().sendMessage(
							msgEntity).datas.securityCode;
				} catch (Exception e) {
				}
				if (verifyCode == null) {
					DialogUtils.showToast(RegisterActivity.this, "发送短信异常，请重试",
							DialogUtils.ERROR);
					return;
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mContext, verifyCode, Toast.LENGTH_LONG)
								.show();
					}
				});
				System.out.println(verifyCode);
			}
		}).start();
		mbtnSendCode.setEnabled(false);
		if (mCountTimer == null) {
			mCountTimer = new CountDownTimer(60000, 1000) {
				@Override
				public void onFinish() {
					mbtnSendCode.setEnabled(true);
					mAuthCode.setText("(60秒后重发)");
				}

				@Override
				public void onTick(long millisUntilFinished) {
					mAuthCode.setText("(" + millisUntilFinished / 1000
							+ "秒后重发)");
				}
			};
		}
		mCountTimer.start();
	}

	@OnClick(R.id.add_back)
	public void clickBack(View view) {
		finish();
	}

	@OnFocusChange({ R.id.reg_name, R.id.reg_phone, R.id.reg_code,
			R.id.reg_identity,R.id.reg_psw,R.id.reg_conform_psw })
	public void clickFource(View view) {
		if (view.isFocused() && (view.getId()==R.id.reg_psw
				||view.getId()==R.id.reg_conform_psw
				||view.getId() == R.id.reg_identity)) {
			scrollToBottom();
		}

		if (!view.isFocused()) {
			switch (view.getId()) {
			case R.id.reg_name:
				String name = mRegName.getText().toString();
				verifyName(name);
				break;
			case R.id.reg_phone:
				String phone = mRegPhone.getText().toString();
				verifyPhone(phone);
				break;
			case R.id.reg_code:
				String code = mRegCode.getText().toString();
				verifyCode(code);
				break;
			case R.id.reg_identity:
				String idcard = mRegIdentity.getText().toString();
				verifyCard(idcard);
				break;
			case R.id.reg_psw:
				String pwd = mRegPsw.getText().toString();
				verifyPwd(pwd);
				break;
			case R.id.reg_conform_psw:
				String pwd2 = mRegPsw.getText().toString();
				String conformPwd = mRegConformPsw.getText().toString();
				verifyConformPwd(pwd2, conformPwd);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mCountTimer != null) {
			mCountTimer.cancel();
		}
		super.onDestroy();
	}

	@OnTouch(R.id.reg_content)
	public boolean mInnerTouch(View v) {
		v.setFocusable(true);
		v.setFocusableInTouchMode(true);
		v.requestFocus();
		// 隐藏键盘
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mRegName.getWindowToken(), 0);
		return false;
	}

	/**
	 * 滚动到底部
	 */
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
		}, 200);
	}

	/**
	 * 输入内容校验
	 * 
	 * @return
	 */
	private boolean verify() {
		String name = mRegName.getText().toString().trim();
		String phone = mRegPhone.getText().toString().trim();
		String code = mRegCode.getText().toString().trim();
		String identityCard = mRegIdentity.getText().toString().trim();
		String psw = mRegPsw.getText().toString().trim();
		String conformPsw = mRegConformPsw.getText().toString().trim();

		String[] fields = new String[] { name, phone, code, identityCard, psw,
				conformPsw };
		boolean result = isEmpty(fields) && verifyName(name)
				&& verifyPhone(phone) && verifyCode(code)
				&& verifyCard(identityCard)
				&& verifyPwd(psw)
				&& verifyConformPwd(psw, conformPsw);
		return result;
	}

	private boolean isEmpty(String[] fields) {
		for (int i = 0, size = fields.length; i < size; i++) {
			if ("".equals(fields[i])) {
				DialogUtils.showToast(this, getString(R.string.required_msg),
						DialogUtils.ERROR);
				return false;
			}
		}
		return true;
	}

	private boolean verifyCode(String code) {
		final String[] msg = new String[1];
		boolean result = true;
		if("".equals(code)) {
			msg[0] = "请输入验证码";
			result = false;
		} else if (!Pattern.matches("^[0-9]{6}$", code)) {
			msg[0] = "验证码格式不正确";
			result = false;
		}
		if (!result) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogUtils.showToast(RegisterActivity.this, msg[0],
							DialogUtils.ERROR);

				}
			}, 400);
		}

		return result;
	}

	private boolean verifyName(String name) {
		final String[] msg = new String[1];
		boolean result = true;
		if("".equals(name)) {
			msg[0] = "请输入姓名";
			result = false;
		}else if (!Pattern.matches("^([\u4E00-\u9FA5]{2,7})|([a-zA-Z]{2,30})$", name)) {
			msg[0] = getString(R.string.name_fromat_error);
			result = false;
		}

		if (!result) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogUtils.showToast(RegisterActivity.this, msg[0],
							DialogUtils.ERROR);
				}
			}, 400);
		}
		return result;
	}

	private boolean verifyPhone(String phoneNum) {

		final String[] msg = new String[1];
		boolean result = true;
		
		if("".equals(phoneNum)) {
			msg[0] ="请输入手机号码";
			result = false;
		}else if (!Pattern.matches("^1[3-8]{1}\\d{9}$", phoneNum)) {
			msg[0] = getString(R.string.phone_format_error);
			result = false;
		}

		if (!result) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogUtils.showToast(RegisterActivity.this, msg[0],
							DialogUtils.ERROR);

				}
			}, 400);
		}
		return result;
	}

	private boolean verifyCard(String idcard) {
		final String[] msg = new String[1];
		boolean result = true;

		if("".equals(idcard)) {
			msg[0] = "请输入身份证";
			result = false;
		}else if (!Pattern
				.matches(
						"^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$",
						idcard)) {
			msg[0] = getString(R.string.idcard_format_error);
			result = false;
		}

		if (!result) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogUtils.showToast(RegisterActivity.this, msg[0],
							DialogUtils.ERROR);
				}
			}, 400);
		}
		return result;
	}

	private boolean verifyPwd(String pwd) {
		final String[] msg = new String[1];
		boolean result = true;

		if ("".equals(pwd)) {
			msg[0] = "请输入密码";
			result = false;
		} else if(pwd.length() <6) {
			msg[0] = "密码太短";
			result = false;
		}

		if (!result) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogUtils.showToast(RegisterActivity.this, msg[0],
							DialogUtils.ERROR);
				}
			}, 400);
		}

		return result;
	}

	/**
	 * 验证输入密码是否匹配
	 * 
	 * @param psw
	 * @param conformPsw
	 * @return
	 */
	private boolean verifyConformPwd(String psw, String conformPsw) {
		System.out.println(" verify conform pwd");

		final String[] msg = new String[1];
		boolean result = true;

		if (!psw.equals(conformPsw)) {
			msg[0] = getString(R.string.pwd_conform_error);
			result = false;
		}
		if (!result) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					DialogUtils.showToast(RegisterActivity.this, msg[0],
							DialogUtils.ERROR);
				}
			}, 400);
		}

		return result;
	}

	@OnClick(R.id.btn_reg)
	public void clickReg(View view) {
		boolean result = verify();
		if (!result)
			return;
		String phone = mRegPhone.getText().toString().trim();
		String code = mRegCode.getText().toString().trim();
		String userName = mRegName.getText().toString().trim();
		
		String userCard = mRegIdentity.getText().toString().trim();
		String pwd = MD5Utils.string2MD5(mRegPsw.getText().toString());
		RegisterEntity registerEntity = new RegisterEntity(userName, phone,
				userCard, pwd, code);
		RequestEntity<RegisterEntity> request = new RequestEntity<>("test",
				"test", registerEntity);
		new RegisterTask(request).execute();
	}

	public class RegisterTask extends AsyncTask<Void, Void, Response> {

		private RequestEntity<RegisterEntity> mRequest;
		private ProgressDialog mProgress;

		public RegisterTask(RequestEntity<RegisterEntity> request) {
			this.mRequest = request;
		}

		@Override
		protected void onPreExecute() {
			mProgress = DialogUtils
					.createProgressDialog(mContext, "", "请稍后...");
			mProgress.show();
		}

		@Override
		protected Response doInBackground(Void... params) {
			if (SystemUtils.getConnectState(mContext) == SystemUtils.TYPE_NONE) {
				return null;
			}
			Response response = null;
			try {
//				response = NetworkFactory.getAuthService().register(mRequest);
//				
				Gson gson = new Gson();
				String jsonStr = gson.toJson(mRequest);
				System.out.println("gson " + jsonStr);
				JSONObject jsonObject = new JSONObject(jsonStr);
				String result = HttpUtils.post(jsonObject, HttpUtils.base_url+HttpUtils.reg_url);
				System.out.println("register result " + result);
				
				Gson gsonResult = new Gson();
				response = gsonResult.fromJson(result, Response.class);
			} catch (Exception e) {

			}
			return response;
		}

		@Override
		protected void onPostExecute(Response result) {
			mProgress.dismiss();
			if (result == null) {
				DialogUtils.showToast(RegisterActivity.this,
						getString(R.string.network_error), DialogUtils.ERROR);
				return;
			}
			if (result.info.equals("success")) {
				DialogUtils.showToast(RegisterActivity.this,
						getString(R.string.register_success),
						DialogUtils.SUCCESS);
				PropertiesSharePrefs.getInstance(RegisterActivity.this)
						.saveProperty(PropertiesSharePrefs.TYPE_USER_PHONE,
								mRegPhone.getText().toString().trim());
				mBtnReg.setEnabled(false);

				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
			} else {
				DialogUtils.showToast(RegisterActivity.this, result.info,
						DialogUtils.ERROR);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mBtnReg.setEnabled(isChecked);
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
