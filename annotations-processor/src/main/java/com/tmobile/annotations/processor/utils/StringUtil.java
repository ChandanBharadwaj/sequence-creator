package com.tmobile.annotations.processor.utils;

public class StringUtil {

	public static boolean isNotBlank(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return false;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return true;
			}
		}
		return false;
	}
}
