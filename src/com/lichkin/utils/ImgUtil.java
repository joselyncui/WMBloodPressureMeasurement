package com.lichkin.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ImgUtil {
	public static int calcuteInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight){
		final int height = options.outHeight;
		final int width = options.outWidth;
		
		
		int inSampleSize = 1;
		if(height>reqHeight || width > reqWidth) {
			
			final int halfHeight = height/2;
			final int hafWidth = width/2;
			
			while ((halfHeight/inSampleSize) > reqHeight 
					|| (hafWidth/inSampleSize) > reqWidth) {
				inSampleSize *=2;
				System.out.println("simple size " + inSampleSize);
			}
		}
		
		return inSampleSize;
	}
	
	public static Bitmap decodeSampleBitmapFromResource(Resources resources, 
			int resId, int reqWidth, int reqHeight){
		final BitmapFactory.Options  options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId,options);
		options.inSampleSize = calcuteInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		System.out.println("sample size " + options.inSampleSize);
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resId,options);
		return bitmap;
	}
	
}
