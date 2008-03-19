package org.mifos.application.reports.util.helpers;

import java.text.ParseException;
import java.util.Date;

public class ReportUtils {

	public static Date parseReportDate(String runDate) throws ParseException {
		return ReportsConstants.REPORT_DATE_FORMAT.parse(runDate);
	}

	public static String toDisplayDate(Date date) {
		return ReportsConstants.REPORT_DATE_FORMAT.format(date);
	}

	public static String reportDatePattern() {
		return ReportsConstants.REPORT_DATE_FORMAT.toPattern();
	}

}
