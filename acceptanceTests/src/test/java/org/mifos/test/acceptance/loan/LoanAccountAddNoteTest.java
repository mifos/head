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
import org.mifos.test.acceptance.framework.loan.AccountAddNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountPreviewNotesPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class LoanAccountAddNoteTest extends UiTestCaseBase {

    private static final String TEST_ACCOUNT = "000100000000012";
    private static final String TEST_ACCOUNT_NOTE = "Acceptance Test note for Issue 2456";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 6, 25, 8, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void addNoteToLoanAccountAndVerifyRecentNotes() throws Exception {
        LoanAccountPage loanAccountPage = addNoteToAccount();
        assertTextFoundOnPage(TEST_ACCOUNT_NOTE);
        AccountNotesPage accountNotesPage = loanAccountPage.navigateToAccountNotesPage();
        accountNotesPage.verifyPage();
        assertTextFoundOnPage(TEST_ACCOUNT_NOTE);
    }

    private LoanAccountPage addNoteToAccount() {
        NavigationHelper helper = new NavigationHelper(selenium);
        LoanAccountPage loanAccountPage = helper.navigateToLoanAccountPage(TEST_ACCOUNT);
        AccountAddNotesPage addNotesPage = loanAccountPage.navigateToAddNotesPage();
        addNotesPage.verifyPage();
        AccountPreviewNotesPage previewPage = addNotesPage.submitAndNavigateToAccountAddNotesPreviewPage(TEST_ACCOUNT_NOTE);
        previewPage.verifyPage();
        loanAccountPage = previewPage.submitAndNavigateToLoanAccountPage();
        return loanAccountPage;
    }


}
