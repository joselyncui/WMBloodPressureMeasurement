package com.lichkin.utils;

import java.text.NumberFormat;

/**
 * 
 * 对数据进行进制的转换
 * 
 * @author Like
 * 
 */
public class DataConvertUtils {

	/**
	 * 16进制转10进制
	 * 
	 * @param hex
	 *            十六进制字符串
	 * @return 转换好的十进制字符串
	 */
	public static String hexToDecimal(String hex) {
		return Integer.valueOf(hex, 16).toString();
	}

	/**
	 * 10进制转16进制
	 * 
	 * @param decimal
	 *            十进制字符串
	 * @return 转换好的十六进制字符串
	 */
	public static String decimalToHex(String decimal) {
		int decNumber = Integer.valueOf(decimal);
		return Integer.toHexString(decNumber);
	}

	public static String format(double number, int save) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(save);
		return nf.format(number);
	}

	/**
	 * 保留一位小数， 并且不四舍五入
	 * 
	 * @param value
	 * @return
	 */
	public static float formatNoRound(Double value) {
		float result = (float) (Math.floor(value * 10) / 10);
		return result;
	}

}
