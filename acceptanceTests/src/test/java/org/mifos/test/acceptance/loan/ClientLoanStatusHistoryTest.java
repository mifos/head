/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.loan;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"acceptance", "ui", "loan", "no_db_unit"})
public class ClientLoanStatusHistoryTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    // acceptance_small_007 account id's:
    private static final String ACCOUNT_APPROVED_ID = "000100000000052";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 7, 4, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void approvedToRejected() throws Exception {
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setStatus(EditLoanAccountStatusParameters.CANCEL);
        params.setCancelReason("Rejected");
        params.setNote("Test");
        loanTestHelper.changeLoanAccountStatus(ACCOUNT_APPROVED_ID, params);
        loanTestHelper.verifyLastEntryInStatusHistory(ACCOUNT_APPROVED_ID, EditLoanAccountStatusParameters.APPROVED, EditLoanAccountStatusParameters.CANCEL);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newLoanToPartialApplicationToPendingApprovalToApproved() throws Exception {
        String loanId = "000100000000053";
        loanTestHelper.verifyLastEntryInStatusHistory(loanId, EditLoanAccountStatusParameters.NEW, EditLoanAccountStatusParameters.PENDING_APPROVAL);

        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();

        params.setStatus(EditLoanAccountStatusParameters.PARTIAL_APPLICATION);
        params.setNote("Partial app.");
        loanTestHelper.changeLoanAccountStatus(loanId, params);
        loanTestHelper.verifyLastEntryInStatusHistory(loanId, EditLoanAccountStatusParameters.PENDING_APPROVAL, EditLoanAccountStatusParameters.PARTIAL_APPLICATION);

        params.setStatus(EditLoanAccountStatusParameters.PENDING_APPROVAL);
        params.setNote("More data arrived.");
        loanTestHelper.changeLoanAccountStatus(loanId, params);
        loanTestHelper.verifyLastEntryInStatusHistory(loanId, EditLoanAccountStatusParameters.PARTIAL_APPLICATION, EditLoanAccountStatusParameters.PENDING_APPROVAL);

        params.setStatus(EditLoanAccountStatusParameters.APPROVED);
        params.setNote("Approved.");
        loanTestHelper.changeLoanAccountStatus(loanId, params);
        loanTestHelper.verifyLastEntryInStatusHistory(loanId, EditLoanAccountStatusParameters.PENDING_APPROVAL, EditLoanAccountStatusParameters.APPROVED);
    }
}
