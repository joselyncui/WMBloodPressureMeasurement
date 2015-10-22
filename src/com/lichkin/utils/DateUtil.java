package com.lichkin.utils;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	public static final String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATA_FORMAT_CHINESE = "MM‘¬dd»’";
	public static final String DATA_FORMAT_ENGLISH = "M.d";

	public static Date longToDate(long longDate) {
		Date date = new Date(longDate);
		return date;
	}

	public static long dateToLong(Date date) {
		return date.getTime();
	}

	public static String getFormatDate(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		return sdf.format(date);
	}

	public static String getFormatDate(String format, long longDate) {
		return getFormatDate(format, longToDate(longDate));
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatBsMeasure(String dateStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
		Date date = sdf.parse(dateStr);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyy-MM-dd HH:mm");
		System.out.println(sdf2.format(date));

		return sdf2.format(date);
	}

	@SuppressLint("SimpleDateFormat")
	public static String formateDate(String dateStr, String format)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm");
		Date date = sdf.parse(dateStr);
		SimpleDateFormat sdf2 = new SimpleDateFormat(format);
		return sdf2.format(date);
	}
}
