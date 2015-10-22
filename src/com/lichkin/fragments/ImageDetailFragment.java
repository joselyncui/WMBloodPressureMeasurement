package com.lichkin.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lichkin.activity.GuideViewActivity;
import com.lichkin.activity.R;
import com.lichkin.utils.ImgUtil;

public class ImageDetailFragment extends Fragment{
	
	private static final String IMAGE_DATA_EXTRA = "resid";
	private int mImageNum;
	private ImageView mImageView;
	private Resources resources;
	
	public static ImageDetailFragment newInstance(Resources resources, int imageNum) {
		
		final ImageDetailFragment fragment = new ImageDetailFragment(resources);
		final Bundle args = new Bundle();
		args.putInt(IMAGE_DATA_EXTRA,imageNum);
		
		fragment.setArguments(args);
		return fragment;
	}

	public ImageDetailFragment(){}
	public ImageDetailFragment(Resources resources) {
		super();
		this.resources = resources;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageNum = getArguments() != null?getArguments().getInt(IMAGE_DATA_EXTRA):-1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_image_detail, container,false);
		mImageView = (ImageView) v.findViewById(R.id.imageView);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final int resId = GuideViewActivity.pics[mImageNum];

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, opts);
		mImageView.setImageBitmap(bitmap);
	}
	
}
