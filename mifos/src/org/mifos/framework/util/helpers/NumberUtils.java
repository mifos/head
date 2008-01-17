package org.mifos.framework.util.helpers;

public class NumberUtils {

	public static Short convertIntegerToShort(Integer intValue) {
		if (intValue == null) {
			return null;
		}
		return new Short(intValue.shortValue());
	}

	public static Integer convertShortToInteger(Short shortValue) {
		if (shortValue == null) {
			return null;
		}
		return new Integer(shortValue.intValue());
	}

	public static boolean isDigits(String number) {
		if (number == null)
			return false;
		if (number.startsWith("-"))
			number = number.substring(1);
		return org.apache.commons.lang.math.NumberUtils.isDigits(number);
	}
}
