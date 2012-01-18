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

package org.mifos.test.acceptance.savingsproduct;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductConfirmationPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPreviewPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"savingsproduct", "acceptance", "ui", "no_db_unit"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class DefineNewSavingsProductTest extends UiTestCaseBase {

    private SavingsProductHelper savingsProductHelper;

    private SavingsAccountHelper savingsAccountHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.resetDateTime();

        savingsProductHelper = new SavingsProductHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void validateDefineSavingsProductForm() throws Exception {
        SavingsProductParameters params = savingsProductHelper.getInvalidSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.GROUPS);
        params.setShortName("V140");
        DefineNewSavingsProductPage newSavingsProductPage = savingsProductHelper.getDefineSavingsProductPageWithValidationErrors(params);
        
        newSavingsProductPage.verifyValidationErrors("Please specify the Time period for Interest calculation.", 
            "Please specify the Frequency of Interest posting to accounts.", "Please specify the Interest rate. Interest must be in range (0-100).",
            "Please specify a value greater than zero for Mandatory amount for deposit.", "Please select the Amount Applies to."
            );
    }
    
    public void createSavingsProductWithoutInterestRateDetails() throws Exception {
        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CENTERS);
        
        params.setShortName(StringUtil.getRandomString(3));
        DefineNewSavingsProductPreviewPage savingsProductPreviewPage = savingsProductHelper.getDefineSavingsProductPreviewPageWithoutInterestRateDetails(params);
        savingsProductPreviewPage.verifyAllElementsAreNotPresent("id=interestRateDetails");
        savingsProductPreviewPage.submitAndNavigateToDefineNewSavingsProductConfirmationPage()
            .navigateToSavingsProductDetails()
            .verifyAllElementsAreNotPresent("id=interestRateDetails");
    }
    
    // http://mifosforge.jira.com/browse/MIFOSTEST-139
    public void createVoluntarySavingsProductForCenters() throws Exception {
        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CENTERS);
        params.setShortName("M139");
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("DefineNewSavingsProductTestCenter", params.getProductInstanceName(), "7777.8");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-137
    public void createVoluntarySavingsProductForGroups() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.GROUPS);
        params.setShortName("M137");
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("DefineNewSavingsProductTestGroup", params.getProductInstanceName(), "234");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1093
    public void createVoluntarySavingsProductForClients() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CLIENTS);
        params.setShortName("1093");
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("DefineNewSavingsProduct TestClient", params.getProductInstanceName(), "200");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1094
    public void createMandatorySavingsProductForGroups() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.GROUPS);
        params.setShortName("1094");
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("DefineNewSavingsProductTestGroup", params.getProductInstanceName(), "534");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-138
    public void createMandatorySavingsProductForClients() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.CLIENTS);
        params.setShortName("M138");
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("DefineNewSavingsProduct TestClient", params.getProductInstanceName(), "248");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1095
    public void createMandatorySavingsProductForCenters() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.CENTERS);
        params.setShortName("1095");
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("DefineNewSavingsProductTestCenter", params.getProductInstanceName(), "7777.8");
    }

    private SavingsAccountDetailPage createSavingAccountWithCreatedProduct(String client, String productName, String amount){
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString(client);
        searchParameters.setSavingsProduct(productName);

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount(amount);
        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        savingsAccountPage.verifySavingsAmount(amount);
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
        return savingsAccountPage;
    }

}