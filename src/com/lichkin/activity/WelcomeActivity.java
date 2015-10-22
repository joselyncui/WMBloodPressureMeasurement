package com.lichkin.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.lichkin.utils.PropertiesSharePrefs;
import com.testin.agent.TestinAgent;

public class WelcomeActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {

	public final static String NAME = "loginName";
	public final static String PASSWORD = "loginPwd";

	@InjectView(R.id.welcome_logo)
	ImageView mLogo;
	@InjectView(R.id.welcome_company_name)
	ImageView mCompanyName;

	private Context mContext;
	private PropertiesSharePrefs mState;
	private AlertDialog mAalertDialog;
	private View mDialogView;
	private CheckBox mAuthCheckBox;
	private Button mBtnYes, mBtnNo;
	private TextView mLinkText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestinAgent.init(this, "c4b338451aae8a2acce515ab0ccdc005", "wmbug");
		setContentView(R.layout.activity_welcome);
		ButterKnife.inject(this);
		getWindow().setBackgroundDrawable(null);//remove window bg

		mContext = this;
		mState = PropertiesSharePrefs.getInstance(mContext);

		Animation logoAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.welcome_logo_anim);
		logoAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Animation shakeAnimation = AnimationUtils.loadAnimation(
						mContext, R.anim.welcome_logo_shake);
				mLogo.startAnimation(shakeAnimation);
			}
		});
		mLogo.startAnimation(logoAnimation);
		Animation companyNameAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.welcome_company_name_anim);
		mCompanyName.startAnimation(companyNameAnimation);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				// 判断如果同意之后， 不在提示
				boolean auth = mState.getProperty(
						PropertiesSharePrefs.TYPE_AUTH, false);
				// if (!auth) {
				showAuthDialog();
				// } else {
				// jumpPage();
				// }

			}
		}, 3000);

	}

	public void showAuthDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		LayoutInflater inflater = getLayoutInflater();
		mDialogView = inflater.inflate(R.layout.dialog_authority, null);

		mAuthCheckBox = (CheckBox) mDialogView.findViewById(R.id.auth_checkbox);
		mLinkText = (TextView) mDialogView.findViewById(R.id.link);
		mBtnNo = (Button) mDialogView.findViewById(R.id.btn_no);
		mBtnYes = (Button) mDialogView.findViewById(R.id.btn_yes);
		mAalertDialog = builder.create();
		mAalertDialog.setCanceledOnTouchOutside(false);
		mAalertDialog.setView(mDialogView, 0, 0, 0, 0);
		mAalertDialog.show();

		mAuthCheckBox.setOnCheckedChangeListener(this);
		mBtnNo.setOnClickListener(this);
		mBtnYes.setOnClickListener(this);

		Window dialogWindow = mAalertDialog.getWindow();
		WindowManager m = this.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		Point p = new Point();
		d.getSize(p);
		params.width = (int) (p.x * 0.9); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(params);

		authLink();
	}

	private void authLink() {
		// 创建一个 SpannableString对象
		SpannableString sp = new SpannableString(
				"为了给您更好的服务,本应用将会上传您的检测数据进行分析,您是否同意无忧健康协议？");
		// 设置超链接
		sp.setSpan(new URLSpan(
				"http://www.leadingtechmed.cn/agreements/dataUpload.html"), 33,
				39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 设置高亮样式二
		sp.setSpan(new ForegroundColorSpan(Color.WHITE), 33, 39,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		// 设置斜体
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 33,
				39, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		// SpannableString对象设置给TextView
		mLinkText.setText(sp);
		// 设置TextView可点击
		mLinkText.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			mBtnYes.setEnabled(true);
		} else {
			mBtnYes.setEnabled(false);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_yes:
			mState.saveProperty(PropertiesSharePrefs.TYPE_AUTH, true);
			break;
		case R.id.btn_no:
			mState.saveProperty(PropertiesSharePrefs.TYPE_AUTH, false);
			break;
		default:
			mState.saveProperty(PropertiesSharePrefs.TYPE_AUTH, false);
			break;
		}

		mAalertDialog.dismiss();
		jumpPage();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (isFinishing()) {
			overridePendingTransition(R.anim.scale_fade_in,
					R.anim.slide_out_to_left);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;// 禁止back
	}

	private void jumpPage() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();

	}

}
