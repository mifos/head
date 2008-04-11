/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.application.branchreport;

import static org.mifos.application.reports.business.service.BranchReportTestCase.DEFAULT_CURRENCY;
import static org.mifos.framework.util.helpers.MoneyFactory.createMoney;

import java.math.BigDecimal;
import java.util.Date;

import org.mifos.framework.util.helpers.DateUtils;

public class BranchReportBOFixture {
	private static final String BRANCH_PERSONNEL_NAME = "Branch-Personnel";
	private static final Integer ZERO = Integer.valueOf(0);
	private static final Short PERSONNEL_ID = Short.valueOf("1");
	protected static final Short BRANCH_ID = Short.valueOf("1");
	protected static final Date BRANCH_REPORT_RUN_DATE = DateUtils
			.currentDate();
	protected static final Integer BRANCH_REPORT_ID = new Integer(1);

	public static BranchReportBO createBranchReport(Integer branchReportId,
			Short branchId, Date runDate) {
		return BranchReportBO.createInstanceForTest(branchReportId, branchId,
				runDate);
	}

	public static BranchReportBO createBranchReportWithStaffSummary(
			Short branchId, Date runDate) {
		BranchReportBO branchReportWithStaffSummary = new BranchReportBO(
				branchId, runDate);
		BranchReportStaffSummaryBO staffSummary = createBranchReportStaffSummaryBO(
				PERSONNEL_ID, BRANCH_PERSONNEL_NAME, null, ZERO, ZERO, ZERO,
				ZERO, ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		branchReportWithStaffSummary.addStaffSummary(staffSummary);
		return branchReportWithStaffSummary;
	}

	public static BranchReportClientSummaryBO createBranchReportClientSummaryBO(
			String fieldName) {
		return new BranchReportClientSummaryBO(fieldName, ZERO, ZERO);
	}

	public static BranchReportLoanArrearsAgingBO createLoanArrearsAging(
			LoanArrearsAgingPeriod period) {
		return BranchReportLoanArrearsAgingBO.createInstanceForTest(period);
	}

	public static BranchReportStaffSummaryBO createBranchReportStaffSummaryBO() {
		return createBranchReportStaffSummaryBO(PERSONNEL_ID,
				BRANCH_PERSONNEL_NAME, null, Integer.valueOf(2), Integer
						.valueOf(3), ZERO, ZERO, ZERO, BigDecimal.valueOf(0),
				BigDecimal.valueOf(0), BigDecimal.ZERO);
	}

	private static BranchReportStaffSummaryBO createBranchReportStaffSummaryBO(
			Short personnelId, String personnelDisplayName, Date joiningDate,
			Integer borrowersCount, Integer activeLoansCount,
			Integer centerCount, Integer clientsCount, Integer newGroupCount,
			BigDecimal loanAmountOutstanding,
			BigDecimal interestAndFeesAmountOutstanding,
			BigDecimal portfolioAtRisk) {
		return new BranchReportStaffSummaryBO(
				personnelId,
				personnelDisplayName,
				joiningDate,
				borrowersCount,
				activeLoansCount,
				centerCount,
				clientsCount,
				newGroupCount,
				createMoney(DEFAULT_CURRENCY, loanAmountOutstanding),
				createMoney(DEFAULT_CURRENCY, interestAndFeesAmountOutstanding),
				portfolioAtRisk);
	}

	public static BranchReportStaffingLevelSummaryBO createStaffingLevelBO(
			Integer roleId) {
		return BranchReportStaffingLevelSummaryBO.createInstanceForTest(roleId);
	}
}
