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
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
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

    // http://mifosforge.jira.com/browse/MIFOSTEST-139
    public void createVoluntarySavingsProductForCenters() throws Exception {
        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CENTERS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Center", params.getProductInstanceName(), "7777.8");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-137
    public void createVoluntarySavingsProductForGroups() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.GROUPS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Group", params.getProductInstanceName(), "234.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1093
    public void createVoluntarySavingsProductForClients() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CLIENTS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Client - Mary Monthly", params.getProductInstanceName(), "200.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1094
    public void createMandatorySavingsProductForGroups() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.GROUPS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Group", params.getProductInstanceName(), "534.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-138
    public void createMandatorySavingsProductForClients() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.CLIENTS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Client - Mary Monthly", params.getProductInstanceName(), "248.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1095
    public void createMandatorySavingsProductForCenters() throws Exception {

        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.CENTERS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Default Center", params.getProductInstanceName(), "7777.8");
    }

    private SavingsAccountDetailPage createSavingAccountWithCreatedProduct(String client, String productName, String amount){
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString(client);
        searchParameters.setSavingsProduct(productName);

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount(amount);
        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
        return savingsAccountPage;
    }

}