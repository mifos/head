package org.mifos.framework.struts.tags;

import org.mifos.framework.util.helpers.DateUtils;

public class DateHelper {
	
	/* Called via datehelper.tld */
	public static String getUserLocaleDateObject(
		java.util.Locale locale, java.util.Date databaseDate) {
		return DateUtils.getUserLocaleDate(locale, databaseDate);
	}
	
	public static String getUserLocaleDate(
			java.util.Locale locale, String databaseDate) {
			return DateUtils.getUserLocaleDate(locale, databaseDate);
		}

}
