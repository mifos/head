/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.reports.struts.action;

import junit.framework.Assert;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class ReportsUserParamsActionStrutsTest extends MifosMockStrutsTestCase {

    public ReportsUserParamsActionStrutsTest() throws Exception {
        super();
    }

    private UserContext userContext;

    @Override
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/reports-struts-config.xml");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
       Assert.assertEquals(SecurityConstants.CAN_VIEW_COLLECTION_SHEET_REPORT, security.get("loadAddList-1").shortValue());
       Assert.assertEquals(SecurityConstants.CAN_VIEW_BRANCH_CASH_CONFIRMATION_REPORT, security.get("loadAddList-2")
                .shortValue());
       Assert.assertEquals(SecurityConstants.CAN_VIEW_BRANCH_REPORT, security.get("loadAddList-3").shortValue());
       Assert.assertEquals(SecurityConstants.CAN_VIEW_DETAILED_AGING_PORTFOLIO_AT_RISK, security.get("loadAddList-4").shortValue());
       Assert.assertEquals(SecurityConstants.CAN_VIEW_GENERAL_LEDGER, security.get("loadAddList-5").shortValue());

    }

}
