package org.mifos.framework.util.helpers;

import org.mifos.framework.util.LocalizationConverter;

public class FormUtils {

	public static Double getDoubleValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? LocalizationConverter
				.getInstance().getDoubleValueForCurrentLocale(str) : null;
	}

}
