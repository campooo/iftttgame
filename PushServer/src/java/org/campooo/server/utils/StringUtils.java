/*
 * as class name
 * 
 * @author ckb
 * 
 * @date 2015年11月10日 上午12:19:12
 */
package org.campooo.server.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class StringUtils {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
	public static final DateFormat DATE_FORMAT_CHAR15 = new SimpleDateFormat("yyMMddHHmmssSSS", Locale.CHINA);

	/**
	 * yyMMddHHmmssSSS
	 */
	public static String getTimeChar15() {
		return DATE_FORMAT_CHAR15.format(new Date());
	}

	public static String randomString(int length) {
		if (length < 1) {
			return null;
		}
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}

	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	private static Random randGen = new Random();

}
