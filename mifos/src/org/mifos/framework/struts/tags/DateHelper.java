package org.mifos.framework.struts.tags;

import org.mifos.framework.util.helpers.DateUtils;

public class DateHelper {
	
	/* Called via datehelper.tld */
	public static String getUserLocaleDate(
		java.util.Locale locale, java.lang.String databaseDate) {
		return DateUtils.getUserLocaleDate(locale, databaseDate);
	}

}
