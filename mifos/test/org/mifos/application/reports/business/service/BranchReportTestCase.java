/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
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
