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

package org.mifos.test.acceptance.collectionsheet;

import junit.framework.Assert;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "collectionsheet", "acceptance", "ui" })
public class CollectionSheetEntryInputErrorsTest extends UiTestCaseBase {

    String collectionSheetAccountError = "The following accounts have not been updated due to simultaneous updates/insufficient balance/invalid disbursement date/invalid repayment";
    String loanRepayments = "Loan repayments";
    String savingsWithdrawal = "Savings Withdrawal";

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void enteringAnInvalidAmountAndClickingPreviewShouldCauseAReturnToCollectionSheetEntryPage()
    throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        SubmitFormParameters formParameters = getFormParametersForTestOffice();
        CollectionSheetEntryEnterDataPage enterDataPage = navigateToCollectionSheetEntryPage(formParameters);
        enterDataPage.verifyPage();

        enterDataPage.enterCustomerAccountValue(0, 6, 888.4); // invalid amount

        CollectionSheetEntryEnterDataPage nextPage = enterDataPage.clickPreviewButton();

        nextPage.verifyPage();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void enteringOverpaymentForLoanAndClickingSubmitShouldWarnUserOfOverpayment() throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        SubmitFormParameters formParameters = getFormParametersForTestOffice();
        CollectionSheetEntryEnterDataPage enterDataPage = navigateToCollectionSheetEntryPage(formParameters);
        enterDataPage.verifyPage();

        Double overPayment = 8500.8;
        enterDataPage.enterAccountValue(0, 1, overPayment);

        CollectionSheetEntryPreviewDataPage previewPage = enterDataPage
        .submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);

        CollectionSheetEntryConfirmationPage confirmationPage = previewPage
        .submitAndGotoCollectionSheetEntryConfirmationPage();

        confirmationPage.verifyPage();

        // Not able to bring in internationalized string yet - jpw
        // Locale defaultLocale = Locale.getDefault();
        // String errorsUpdate =
        // SearchUtils.getMessageWithSubstitution(FilePaths.BULKENTRY_RESOURCE,
        // defaultLocale,
        // CollectionSheetEntryConstants.ERRORSUPDATE, null);
        //

        Assert.assertTrue(confirmationPage.isCollectionSheetAccountErrorMessageDisplayed(collectionSheetAccountError,
                loanRepayments));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void enteringExcessiveWithdrawalAmountAndClickingSubmitShouldWarnUserOfInvalidWithdrawal() throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        SubmitFormParameters formParameters = getFormParametersForTestOffice();
        CollectionSheetEntryEnterDataPage enterDataPage = navigateToCollectionSheetEntryPage(formParameters);
        enterDataPage.verifyPage();

        Double excessiveWithdrawal = 80000.8;
        enterDataPage.enterWithdrawalAccountValue(0, 5, excessiveWithdrawal);

        CollectionSheetEntryPreviewDataPage previewPage = enterDataPage
        .submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);

        CollectionSheetEntryConfirmationPage confirmationPage = previewPage
        .submitAndGotoCollectionSheetEntryConfirmationPage();

        confirmationPage.verifyPage();

        Assert.assertTrue(confirmationPage.isCollectionSheetAccountErrorMessageDisplayed(collectionSheetAccountError,
                savingsWithdrawal));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void clickingContinueInBulkEntryPageWithNoInputShouldResultInErrorMessage() throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        CollectionSheetEntrySelectPage selectPage = new CollectionSheetEntryTestHelper(selenium)
        .loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();

        selectPage.submit();

        selectPage.verifyPage();
        Assert.assertTrue(selectPage.isErrorMessageDisplayed());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void clickingContinueInBulkEntryPageAndOnlyEnteringBranchShouldResultInErrorMessage() throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        CollectionSheetEntrySelectPage selectPage = new CollectionSheetEntryTestHelper(selenium)
        .loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();


        SubmitFormParameters formParameters = setFormParametersForTestOffice("MyOffice1233265929385", null, null, null);
        selectPage.fillOutDropDownMenusWithGivenInput(formParameters);

        selectPage.submit();

        selectPage.verifyPage();
        Assert.assertTrue(selectPage.isErrorMessageDisplayed());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void clickingContinueInBulkEntryPageAndOnlyEnteringBranchAndLoanOfficerShouldResultInErrorMessage()
    throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        CollectionSheetEntrySelectPage selectPage = new CollectionSheetEntryTestHelper(selenium)
        .loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();

        SubmitFormParameters formParameters = setFormParametersForTestOffice("MyOffice1233265929385",
                "Joe1233265931256 Guy1233265931256", null, null);
        selectPage.fillOutDropDownMenusWithGivenInput(formParameters);

        selectPage.submit();

        selectPage.verifyPage();
        Assert.assertTrue(selectPage.isErrorMessageDisplayed());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void clickingContinueInBulkEntryPageAndOnlyEnteringBranchAndLoanOfficerAndCenterShouldResultInErrorMessage()
    throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        CollectionSheetEntrySelectPage selectPage = new CollectionSheetEntryTestHelper(selenium)
        .loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();

        SubmitFormParameters formParameters = setFormParametersForTestOffice("MyOffice1233265929385",
                "Joe1233265931256 Guy1233265931256", "MyCenter1233265933427", null);
        selectPage.fillOutDropDownMenusWithGivenInput(formParameters);

        selectPage.submit();

        selectPage.verifyPage();
        Assert.assertTrue(selectPage.isErrorMessageDisplayed());
    }

    private CollectionSheetEntryEnterDataPage navigateToCollectionSheetEntryPage(SubmitFormParameters formParameters) {
        CollectionSheetEntrySelectPage selectPage = new CollectionSheetEntryTestHelper(selenium)
        .loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage
        .submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        return enterDataPage;
    }

    private SubmitFormParameters getFormParametersForTestOffice() {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("MyOffice1233265929385");
        formParameters.setLoanOfficer("Joe1233265931256 Guy1233265931256");
        formParameters.setCenter("MyCenter1233265933427");
        formParameters.setPaymentMode("Cash");
        return formParameters;
    }

    private SubmitFormParameters setFormParametersForTestOffice(String branch, String loanOfficer, String center,
            String paymentMode) {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch(branch);
        formParameters.setLoanOfficer(loanOfficer);
        formParameters.setCenter(center);
        formParameters.setPaymentMode(paymentMode);
        return formParameters;
    }

}
