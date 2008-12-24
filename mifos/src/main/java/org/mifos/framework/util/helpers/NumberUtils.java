package org.mifos.framework.util.helpers;

import java.math.BigDecimal;


public class NumberUtils {
	public static final int ZERO = Integer.valueOf(0);
	public static final Double DOUBLE_ZERO = Double.valueOf(0d);
	public static final Short SHORT_ZERO = Short.valueOf("0");

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

	public static BigDecimal getPercentage(Number part, Number full) {
		float fullValue = full.floatValue();
		if (fullValue == 0.0)
			return BigDecimal.ZERO;
		return BigDecimal.valueOf(part.floatValue() / fullValue * 100f);
	}

	public static boolean isBetween(Comparable start, Comparable end,
			Comparable value) {
		return start.compareTo(value) <= 0 && end.compareTo(value) >= 0;
	}


	public static Integer nullSafeValue(Integer value, int defaultValue) {
		if (value == null)
			return defaultValue;
		return value;
	}

	public static Integer nullSafeValue(Integer value) {
		return nullSafeValue(value, ZERO);
	}
}
