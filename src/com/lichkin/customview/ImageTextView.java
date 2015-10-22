package com.lichkin.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lichkin.activity.R;

public class ImageTextView extends ImageView {

	private int mDegree = 0;
	private Matrix mMatrix;
	private String mText;
	private int mTextSize = 35;
	private int mColor = Color.GRAY;
	private Paint mTextPaint;
	private Bitmap mDSBitmap;
	private boolean mRotating = false;

	private Handler mHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (mRotating) {
				if (mDegree == -360) {
					mDegree = 0;
				}
				mDegree -= 10;
				setDegree(mDegree);
				mHandler.sendEmptyMessageDelayed(0, 100);
			}
			return false;
		}
	});

	public ImageTextView(Context context) {
		super(context);
	}

	public ImageTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ImageTextView);
		mText = a.getString(R.styleable.ImageTextView_text);
		mColor = a.getColor(R.styleable.ImageTextView_textColor, Color.GRAY);
		mTextSize = a.getDimensionPixelSize(R.styleable.ImageTextView_textSize,
				mTextSize);
		a.recycle();
	}

	private void initMatrix() {
		if (mMatrix != null)
			return;
		mMatrix = new Matrix();
	}

	private void initPaint() {
		if (mTextPaint != null)
			return;
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFilterBitmap(true);
		mTextPaint.setColor(mColor);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setStrokeWidth(1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		initPaint();
		Drawable drawable = getDrawable();
		int dsWidth = getWidth();
		int dsHeight = getHeight();
		if (mDSBitmap == null)
			mDSBitmap = Bitmap.createScaledBitmap(
					((BitmapDrawable) drawable).getBitmap(), dsWidth, dsHeight,
					true);
		float centerX = dsWidth * 1.0f / 2;
		float centerY = dsHeight * 1.0f / 2;
		initMatrix();
		mMatrix.setRotate(mDegree, centerX, centerY);
		canvas.drawBitmap(mDSBitmap, mMatrix, null);
		if (mText == null)
			return;
		float textLength = mTextPaint.measureText(mText);
		canvas.drawText(mText, centerX - textLength / 2, centerY
				+ (int) (mTextSize / 2.5), mTextPaint);
	}

	private void setDegree(int degree) {
		this.mDegree = degree;
		invalidate();
	}

	public void startRotate() {
		mRotating = true;
		mHandler.sendEmptyMessage(0);
	}

	public void stopRotate() {
		mRotating = false;
		mHandler.removeMessages(0);
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		this.mText = text;
		invalidate();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopRotate();
	}

}
