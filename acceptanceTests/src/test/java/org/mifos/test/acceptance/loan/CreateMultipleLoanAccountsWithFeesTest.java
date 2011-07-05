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

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateMultipleLoanAccountSelectParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateMultipleLoanAccountsWithFeesTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-54
    public void createBulkLoanAccountsInPendingApprovalStatus() throws Exception {
        //Given
        CreateMultipleLoanAccountSelectParameters multipleAccParameters = new CreateMultipleLoanAccountSelectParameters();
        multipleAccParameters.setBranch("MyOfficeDHMFT");
        multipleAccParameters.setLoanOfficer("loan officer");
        multipleAccParameters.setCenter("Default Center");
        String loanProduct = "WeeklyFlatLoanWithOneTimeFees";
        multipleAccParameters.setLoanProduct(loanProduct);
        String[] clients = new String[2];
        clients[0] = "client1 lastname";
        clients[1] = "ClientWithLoan 20110221";
        String[] clientsDeselected = new String[1];
        clientsDeselected[0] = "Stu1233266319760 Client1233266319760";
        //Then
        loanTestHelper.createMultipleLoanAccountsAndVerify(multipleAccParameters, clients, "0000-Animal Husbandry", loanTestHelper.METHOD_SUBMIT_FOR_APPROVAL);
        loanTestHelper.verifyLoansAreNotOnClientsLoanLists(clientsDeselected, loanProduct);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-55
    public void createBulkLoanAccountsInPartialApplicationStatus() throws Exception {
        //Given
        CreateMultipleLoanAccountSelectParameters multipleAccParameters = new CreateMultipleLoanAccountSelectParameters();
        multipleAccParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        multipleAccParameters.setBranch("MyOfficeDHMFT");
        multipleAccParameters.setCenter("Default Center");
        multipleAccParameters.setLoanOfficer("loan officer");
        String[] clients = new String[2];
        clients[0] = "client1 lastname";
        clients[1] = "ClientWithLoan 20110221";

        //Then
        loanTestHelper.createMultipleLoanAccountsAndVerify(multipleAccParameters, clients, "0000-Animal Husbandry", LoanTestHelper.METHOD_SAVE_FOR_LATER);
    }
}

