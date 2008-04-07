package org.mifos.application.reports.struts.action;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.ReportSecurityConstants;
import org.mifos.framework.util.helpers.Constants;

public class ReportsUserParamsActionTest extends MifosMockStrutsTestCase {
	
	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLoadAddListShouldGoToBirtReport(){
		setRequestPathInfo("/reportsUserParamsAction.do");
		addRequestParameter("method", "loadAddList");
		addRequestParameter("reportId", "28");
		actionPerform();
		verifyForwardPath("/pages/application/reports/jsp/birtReport.jsp");
		
	}
	
	public void testGetSecurityShouldGetReportSecurityConstantsCorrespondingReportId(){
		
		ActionSecurity security = ReportsUserParamsAction.getSecurity();
		assertEquals(ReportSecurityConstants.CLIENT_SUMMARY_AND_HISTORY_REPORT, security.get("loadAddList-1").shortValue());
		assertEquals(ReportSecurityConstants.CLIENT_PRODUCT_WISE_HISTORY_REPORT, security.get("loadAddList-2").shortValue());
		assertEquals(ReportSecurityConstants.CLIENT_SETTLEMENT_INFO_REPORT, security.get("loadAddList-3").shortValue());
		assertEquals(ReportSecurityConstants.CLIENT_LOAN_REPAYMENT_SCHEDULE, security.get("loadAddList-4").shortValue());
		assertEquals(ReportSecurityConstants.CLIENT_FEES_CHARGES_AND_PENALTIES_REPORT, security.get("loadAddList-5").shortValue());
		assertEquals(ReportSecurityConstants.CLIENT_PENDING_APPROVAL_REPORT, security.get("loadAddList-6").shortValue());
		assertEquals(ReportSecurityConstants.CLIENTS_WITHOUT_SAVINGS_ACCOUNT, security.get("loadAddList-7").shortValue());
		assertEquals(ReportSecurityConstants.BRANCH_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-8").shortValue());
		assertEquals(ReportSecurityConstants.AREA_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-9").shortValue());
		assertEquals(ReportSecurityConstants.DIVISION_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-10").shortValue());
		assertEquals(ReportSecurityConstants.REGION_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-11").shortValue());
		assertEquals(ReportSecurityConstants.GRAMEEN_KOOTA_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-12").shortValue());
		assertEquals(ReportSecurityConstants.STAFF_PERFORMANCE_REPORT, security.get("loadAddList-13").shortValue());
		assertEquals(ReportSecurityConstants.OUTREACH_REPORT, security.get("loadAddList-14").shortValue());
		assertEquals(ReportSecurityConstants.CENTER_SUMMARY_REPORT, security.get("loadAddList-15").shortValue());
		assertEquals(ReportSecurityConstants.COLLECTION_SHEET, security.get("loadAddList-16").shortValue());
		assertEquals(ReportSecurityConstants.LOAN_PRODUCT_DISTRIBUTION, security.get("loadAddList-17").shortValue());
		assertEquals(ReportSecurityConstants.BRANCH_DUE_DISBURSEMENT_REPORT, security.get("loadAddList-18").shortValue());
		assertEquals(ReportSecurityConstants.LOANS_PENDING_APPROVAL_REPORT, security.get("loadAddList-19").shortValue());
		assertEquals(ReportSecurityConstants.LOAN_ACCOUNTS_REPORTS, security.get("loadAddList-20").shortValue());
		assertEquals(ReportSecurityConstants.DAILY_CASH_CONFIRMATION_REPORT_STAFF_WISE, security.get("loadAddList-21").shortValue());
		assertEquals(ReportSecurityConstants.DAILY_CASH_FLOW_REPORT_BRANCH, security.get("loadAddList-22").shortValue());
		assertEquals(ReportSecurityConstants.FUND_REQUIREMENT_REPORT, security.get("loadAddList-23").shortValue());
		assertEquals(ReportSecurityConstants.DAILY_TRANSACTION_SUMMARY_REPORT, security.get("loadAddList-24").shortValue());
		assertEquals(ReportSecurityConstants.DAILY_PORTFOLIO_QUALITY_DATA_REPORT, security.get("loadAddList-25").shortValue());
		assertEquals(ReportSecurityConstants.CENTER_MEETING_SCHEDULE, security.get("loadAddList-26").shortValue());
		assertEquals(ReportSecurityConstants.DETAILED_AGING_OF_PORTFOLIO_AT_RISK, security.get("loadAddList-28").shortValue());
		assertEquals(ReportSecurityConstants.ACTIVE_LOANS_BY_LOAN_OFFICER, security.get("loadAddList-29").shortValue());
	}

}
