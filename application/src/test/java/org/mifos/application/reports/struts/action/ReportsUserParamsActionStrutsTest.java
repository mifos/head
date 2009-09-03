/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.reports.struts.action;

import junit.framework.Assert;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ReportSecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;

public class ReportsUserParamsActionStrutsTest extends MifosMockStrutsTestCase {

    public ReportsUserParamsActionStrutsTest() throws SystemException, ApplicationException {
        super();
    }

    private UserContext userContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestDatabase.resetMySQLDatabase();
        userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadAddListShouldGoToBirtReport() throws Exception {
        setRequestPathInfo("/reportsUserParamsAction.do");
        addRequestParameter("method", "loadAddList");
        addRequestParameter("reportId", "1");
        actionPerform();
        verifyForwardPath("/pages/application/reports/jsp/birtReport.jsp");
        // FIXME This test leave CUSTOMER table in dirty state 
        TestDatabase.resetMySQLDatabase();
    }

    public void testGetSecurityShouldGetReportSecurityConstantsCorrespondingReportId() {

        ActionSecurity security = ReportsUserParamsAction.getSecurity();
       Assert.assertEquals(ReportSecurityConstants.COLLECTION_SHEET_REPORT, security.get("loadAddList-1").shortValue());
       Assert.assertEquals(ReportSecurityConstants.BRANCH_CASH_CONFIRMATION_REPORT, security.get("loadAddList-2")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.BRANCH_PROGRESS_REPORT, security.get("loadAddList-3").shortValue());
       Assert.assertEquals(ReportSecurityConstants.CLIENT_LOAN_REPAYMENT_SCHEDULE, security.get("loadAddList-4").shortValue());
       Assert.assertEquals(ReportSecurityConstants.CLIENT_FEES_CHARGES_AND_PENALTIES_REPORT, security.get("loadAddList-5")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.CLIENT_PENDING_APPROVAL_REPORT, security.get("loadAddList-6").shortValue());
       Assert.assertEquals(ReportSecurityConstants.CLIENTS_WITHOUT_SAVINGS_ACCOUNT, security.get("loadAddList-7")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.BRANCH_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-8")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.AREA_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-9").shortValue());
       Assert.assertEquals(ReportSecurityConstants.DIVISION_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-10")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.REGION_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-11")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.GRAMEEN_KOOTA_PERFORMANCE_STATUS_REPORT, security.get("loadAddList-12")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.STAFF_PERFORMANCE_REPORT, security.get("loadAddList-13").shortValue());
       Assert.assertEquals(ReportSecurityConstants.OUTREACH_REPORT, security.get("loadAddList-14").shortValue());
       Assert.assertEquals(ReportSecurityConstants.CENTER_SUMMARY_REPORT, security.get("loadAddList-15").shortValue());
       Assert.assertEquals(ReportSecurityConstants.COLLECTION_SHEET, security.get("loadAddList-16").shortValue());
       Assert.assertEquals(ReportSecurityConstants.LOAN_PRODUCT_DISTRIBUTION, security.get("loadAddList-17").shortValue());
       Assert.assertEquals(ReportSecurityConstants.BRANCH_DUE_DISBURSEMENT_REPORT, security.get("loadAddList-18")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.LOANS_PENDING_APPROVAL_REPORT, security.get("loadAddList-19").shortValue());
       Assert.assertEquals(ReportSecurityConstants.LOAN_ACCOUNTS_REPORTS, security.get("loadAddList-20").shortValue());
       Assert.assertEquals(ReportSecurityConstants.DAILY_CASH_CONFIRMATION_REPORT_STAFF_WISE, security.get("loadAddList-21")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.DAILY_CASH_FLOW_REPORT_BRANCH, security.get("loadAddList-22").shortValue());
       Assert.assertEquals(ReportSecurityConstants.FUND_REQUIREMENT_REPORT, security.get("loadAddList-23").shortValue());
       Assert.assertEquals(ReportSecurityConstants.DAILY_TRANSACTION_SUMMARY_REPORT, security.get("loadAddList-24")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.DAILY_PORTFOLIO_QUALITY_DATA_REPORT, security.get("loadAddList-25")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.CENTER_MEETING_SCHEDULE, security.get("loadAddList-26").shortValue());
       Assert.assertEquals(ReportSecurityConstants.DETAILED_AGING_OF_PORTFOLIO_AT_RISK, security.get("loadAddList-28")
                .shortValue());
       Assert.assertEquals(ReportSecurityConstants.ACTIVE_LOANS_BY_LOAN_OFFICER, security.get("loadAddList-29").shortValue());
    }

}
