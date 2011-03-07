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
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"acceptance", "ui", "loan", "no_db_unit"})
public class ClientLoanStatusChangeTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 7, 1, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void pendingApprovalToApplicationRejected() throws Exception {
        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.CANCEL);
        statusParameters.setCancelReason(EditLoanAccountStatusParameters.CANCEL_REASON_REJECTED);
        statusParameters.setNote("Test");
        String loanId = "000100000000054";
        loanTestHelper.changeLoanAccountStatus(loanId, statusParameters);
        loanTestHelper.verifyLastEntryInStatusHistory(loanId, EditLoanAccountStatusParameters.PENDING_APPROVAL,
                EditLoanAccountStatusParameters.CANCEL);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void pendingApprovalToApplicationApprovedWithQuestionGroup() throws Exception {
        String qgForLoanApproval = "QGForLoanApproval";
        QuestionGroupTestHelper questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        questionGroupTestHelper.markQuestionGroupAsActive(qgForLoanApproval);
        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParameters.setNote("Test");
        QuestionResponseParameters responseParameters = new QuestionResponseParameters();
        responseParameters.addTextAnswer("create_ClientPersonalInfo.input.customField", "testResponse");
        loanTestHelper.changeLoanAccountStatusProvidingQuestionGroupResponses("000100000000055", statusParameters, responseParameters);
        questionGroupTestHelper.markQuestionGroupAsInactive(qgForLoanApproval);
    }


}
