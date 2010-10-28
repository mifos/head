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

package org.mifos.test.acceptance.loanproduct;


import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.framework.util.DbUnitUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loanproduct", "acceptance", "ui","smoke"})
public class VariableInstalmentLoanProductTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    String officeName = "test_office";
    String userLoginName = "test_user";
    String userName="test user";
    String clientName = "test client";
    String loanProductName;
    LoanProductTestHelper loanProductTestHelper;
    LoanTestHelper loanTestHelper;
    DateTime systemDateTime;
    // TODO: please ensure that the database contains Mrs Salutation!
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    // ---

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        // TODO: please ensure that the database contains Mrs Salutation!
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
        // ---
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        loanTestHelper.setApplicationTime(systemDateTime);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        dataSetup.createBranch(OfficeParameters.BRANCH_OFFICE, officeName, "Off");
        dataSetup.createUser(userLoginName, userName, officeName);
        dataSetup.createClient(clientName, officeName, userName);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyLSIMDisabled() throws Exception {
        applicationDatabaseOperation.updateLSIM(0);
        loanProductTestHelper.navigateToDefineNewLoanProductPage().
        verifyVariableInstalmentNotAvailable();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyVariableInstalmentUnChecked() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanProductName = formParameters.getOfferingName();
        applicationDatabaseOperation.updateLSIM(1);
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().
                verifyVariableInstalmentUnChecked().submit().
                navigateToViewLoanDetails().
                verifyVariableInstalmentOptionsUnChecked();
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                verifyUncheckedVariableInstalmentsInLoanProductSummery();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyVariableInstalmentWithNullValue() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanProductName = formParameters.getOfferingName();
        createAndValidateLoanProductWithVariableInstalment("","1","",formParameters);
        navigateToCreateNewLoanPageAndValidateInstallmentSummary("","1","");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyVariableInstalment() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        loanProductName = formParameters.getOfferingName();
        createAndValidateLoanProductWithVariableInstalment("60", "1", "100.5", formParameters);
        navigateToCreateNewLoanPageAndValidateInstallmentSummary("60", "1", "100.5");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyVariableInstalmentField() throws Exception {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        applicationDatabaseOperation.updateLSIM(1);
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                verifyVariableInstalmentOptionsDefaults().
                verifyVariableInstalmentOptionsFields();
    }

    private void createAndValidateLoanProductWithVariableInstalment(String maxGap, String minGap, String minInstalmentAmount, DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption(maxGap, minGap, minInstalmentAmount).
                submitAndGotoNewLoanProductPreviewPage().
                verifyVariableInstalmentOption(maxGap, minGap, minInstalmentAmount).
                submit().
                navigateToViewLoanDetails().
                verifyVariableInstalmentOptions(maxGap, minGap, minInstalmentAmount);
    }

    private void navigateToCreateNewLoanPageAndValidateInstallmentSummary(String maxGap, String minGap, String minInstalmentAmount) {
        new NavigationHelper(selenium).navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                verifyVariableInstalmentsInLoanProductSummery(maxGap, minGap, minInstalmentAmount);
    }
}

