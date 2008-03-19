package org.mifos.application.reports.business.service;


import java.util.Calendar;
import java.util.Date;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.NumberUtils;


public class BranchReportTestCase extends MifosTestCase {

	protected static final Date FIRST_JAN_2008 = DateUtils.getDate(2008,
			Calendar.JANUARY, 1);
	protected static final Integer BRANCH_ID = Integer.valueOf(2);
	public static final Short BRANCH_ID_SHORT = NumberUtils
			.convertIntegerToShort(BRANCH_ID);
	protected static final String RUN_DATE_STR = ReportsConstants.REPORT_DATE_FORMAT
			.format(DateUtils.currentDate());
	public static final Date RUN_DATE = DateUtils
			.getCurrentDateWithoutTimeStamp();
	public static final MifosCurrency DEFAULT_CURRENCY = Configuration
			.getInstance().getSystemConfig().getCurrency();
	protected static final Short CURRENCY_ID = DEFAULT_CURRENCY.getCurrencyId();

	public BranchReportTestCase() {
		super();
	}
}
