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

import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@edu.umd.cs.findbugs.annotations.SuppressWarnings("CPD")
@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"collectionsheet","acceptance","ui"})
public class CollectionSheetEntrySimpleTest extends UiTestCaseBase {


    private static final String VALID_RECEIPT_DAY = "4";

    private static final String VALID_TRANSACTION_DAY = "7";

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        setSystemDate();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
        new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void checkForValueObjectConversionErrorWhenEnteringInvalidDateIntoReceipt() throws Exception {
        SubmitFormParameters invalidFormParameters = getFormParametersWithInvalidReceiptDay();
        SubmitFormParameters validFormParameters = getFormParameters();
        checkForValueObjectConversionErrorWhenEnteringInvalidDate(invalidFormParameters, validFormParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void checkForValueObjectConversionErrorWhenEnteringInvalidDate(SubmitFormParameters invalidFormParameters,
            SubmitFormParameters validFormParameters) throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
        CollectionSheetEntrySelectPage selectPage =
            new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();

        boolean onlyTypeIfFieldIsEmpty = true;
        boolean waitForPageToLoad = true;
        selectPage.submitAndGotoCollectionSheetEntryEnterDataPageWithoutVerifyingPage(invalidFormParameters, onlyTypeIfFieldIsEmpty, waitForPageToLoad);
        CollectionSheetEntrySelectPage collectionSheetEntrySelectPageWithError = new CollectionSheetEntrySelectPage(selenium);
        collectionSheetEntrySelectPageWithError.verifyPage();
        Assert.assertTrue(collectionSheetEntrySelectPageWithError.isErrorMessageDisplayed());

        onlyTypeIfFieldIsEmpty = false;
        waitForPageToLoad = false;
        CollectionSheetEntryEnterDataPage enterDataPage =
            collectionSheetEntrySelectPageWithError.submitAndGotoCollectionSheetEntryEnterDataPageWithoutVerifyingPage(validFormParameters, onlyTypeIfFieldIsEmpty, waitForPageToLoad);
        enterDataPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void checkThatPreviewEditButtonWorks() throws Exception {
        SubmitFormParameters formParameters = getFormParameters();
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
        CollectionSheetEntrySelectPage selectPage =
            new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterDataPage.verifyPage();
        CollectionSheetEntryPreviewDataPage previewDataPage = enterDataPage.submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewDataPage.verifyPage(formParameters);
        CollectionSheetEntryEnterDataPage newEnterDataPage = previewDataPage.editAndGoToCollectionSheetEntryEnterDataPage();
        newEnterDataPage.verifyPage();
    }

    private SubmitFormParameters getFormParametersWithInvalidReceiptDay() {
        String invalidReceiptDay = "4.";
        return getFormParameters(invalidReceiptDay, VALID_TRANSACTION_DAY);
    }

    private SubmitFormParameters getFormParameters() {
        return getFormParameters(VALID_RECEIPT_DAY,VALID_TRANSACTION_DAY);
    }

    private SubmitFormParameters getFormParameters(String receiptDay, String transactionDay) {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("Office1");
        formParameters.setLoanOfficer("Bagonza Wilson");
        formParameters.setCenter("Center1");
        formParameters.setPaymentMode("Cash");
        formParameters.setReceiptDay(receiptDay);
        formParameters.setReceiptMonth("11");
        formParameters.setReceiptYear("2009");
        formParameters.setTransactionDay(transactionDay);
        formParameters.setTransactionMonth("2");
        formParameters.setTransactionYear("2009");
        return formParameters;
    }

    private void setSystemDate() throws UnsupportedEncodingException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,2,23,2,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

}

