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

package org.mifos.test.acceptance.savings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.savings.SavingsApplyAdjustmentPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductConfirmationPage;
import org.mifos.test.acceptance.framework.savingsproduct.EditSavingsProductPage;
import org.mifos.test.acceptance.framework.savingsproduct.EditSavingsProductPreviewPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductDetailsPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.savingsproduct.ViewSavingsProductsPage;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"additionalsavingsaccount", "acceptance", "ui"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class AdditionalSavingsAccountTest extends UiTestCaseBase {

    private SavingsProductHelper savingsProductHelper;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private SavingsAccountHelper savingsAccountHelper;
    private NavigationHelper navigationHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.resetDateTime();

        savingsProductHelper = new SavingsProductHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }


    //http://mifosforge.jira.com/browse/MIFOSTEST-712
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void savingsAccountWithDailyInterestMandatoryDeposits() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,10,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();     //"Stu1233266079799 Client1233266079799"
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799", params.getProductInstanceName(), "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        make3StraightDeposit(savingsId);

        depositParams = setDepositParams(depositParams, "01", "03", "2011");
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        //Then
        targetTime = new DateTime(2011,3,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"57.4");

        //When
        targetTime = new DateTime(2011,3,7,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        targetTime = new DateTime(2011,3,14,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        targetTime = new DateTime(2011,3,21,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        targetTime = new DateTime(2011,3,28,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        //Then
        targetTime = new DateTime(2011,4,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"402.7");
    }
    //http://mifosforge.jira.com/browse/MIFOSTEST-141
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void savingsAccountWith3monthInterestVoluntaryDeposits() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,15,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getVoluntaryClients3MonthCalculactionPostingProductParameters();

        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799",params.getProductInstanceName(),"100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        //Then
        targetTime = new DateTime(2011,5,15,22,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"602.7");

        targetTime = new DateTime(2011,9,1,22,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"1,254.1");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-721
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(groups = "no_db_unit")
    public void savingsAccountsWithDifferentTransactionsOrdering() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,10,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.VOLUNTARY);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();

        //account1
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233171716380 Client1233171716380",params.getProductInstanceName(),"100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        targetTime = new DateTime(2011,2,15,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.WITHDRAWAL, "100000.0");
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        //account2
        targetTime = new DateTime(2011,2,10,13,0,0,0);
        savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233171716380 Client1233171716380",params.getProductInstanceName(),"100000.0");
        String savingsId2 = savingsAccountDetailPage.getAccountId();

        editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId2, editAccountStatusParameters);

        depositParams = new DepositWithdrawalSavingsParameters();

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId2, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        targetTime = new DateTime(2011,2,15,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId2, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId2, DepositWithdrawalSavingsParameters.WITHDRAWAL, "100000.0");

        //Then
        targetTime = new DateTime(2011,4,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPostingWithCleanup();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"48.6");

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId2);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"48.6");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-624
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(groups = "no_db_unit")
    public void savingsMonthlyAccountsAverageBalance() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,10,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.AVERAGE_BALANCE);

        String savingsId = createSavingsAccount(params);
        make3StraightDeposit(savingsId);

        //Then
        targetTime = new DateTime(2011,3,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPostingWithCleanup();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"57.4");

    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-1070
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(groups = "no_db_unit")
    public void restrictionsSavingsTransactions() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,25,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        //When
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233171716380 Client1233171716380", "SavingsProductWithInterestOnMonthlyAvgBalance", "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        make3StraightDeposit(savingsId);

        targetTime = new DateTime(2012,1,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPostingWithCleanup();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"20,594.9");

        //Then
        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();
        DateTime badDate = new DateTime(2011,5,5,13,0,0,0);

        makeDefaultDepositWithdrawal(badDate,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000");

        Assert.assertTrue(selenium.isTextPresent("Date of transaction is invalid. It can not be prior to the last meeting date of the customer or prior to activation date of the savings account."));
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-725
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void savingsProductUpdateableFields() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        SavingsProductParameters params = savingsProductHelper.getMandatoryClientsMinimumBalanceSavingsProductParameters();
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.AVERAGE_BALANCE);
        params.setTypeOfDeposits(SavingsProductParameters.VOLUNTARY);
        params.setStartDateDD("1");
        params.setStartDateMM("2");
        params.setStartDateYYYY("2011");
        
        String productName = params.getProductInstanceName();
        String savingsId = createSavingsAccount(params);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "2000");

        //Then
        targetTime = new DateTime(2011,3,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"2.7");

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewSavingsProductsPage viewSavingsProducts = adminPage.navigateToViewSavingsProducts();
        viewSavingsProducts.verifyPage();

        SavingsProductDetailsPage savingsProductDetailsPage = viewSavingsProducts.viewSavingsProductDetails(productName);
        EditSavingsProductPage editSavingsProductPage = savingsProductDetailsPage.editSavingsProduct();

        selenium.type("interestRate", "10");
        selenium.type("minBalanceRequiredForInterestCalculation", "100");

        EditSavingsProductPreviewPage editSavingsProductPreviewPage = editSavingsProductPage.editSubmit();
        savingsProductDetailsPage = editSavingsProductPreviewPage.submit();

        makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "2000");

        targetTime = new DateTime(2011,4,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"33.1");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-144
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    public void savingsAdjustmentsForDepositsWithdrawals() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,1,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        //When
        SavingsProductParameters params = getVoluntaryGroupsMonthCalculactionProductParameters();
        params.setApplicableFor(SavingsProductParameters.CLIENTS);
        params.setStartDateDD("01");
        params.setStartDateMM("01");
        params.setStartDateYYYY("2011");
        params.setInterestRate("10");
        String savingsId = createSavingsAccount(params);
        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "500");

        targetTime = new DateTime(2011,2,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.WITHDRAWAL, "200");

        targetTime = new DateTime(2011,3,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"4.1");

        targetTime = new DateTime(2011,4,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();
        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"2.6");

        SavingsAccountDetailPage savingsAccountDetailPage = new SavingsAccountDetailPage(selenium);
        SavingsApplyAdjustmentPage savingsApplyAdjustmentPage = new SavingsApplyAdjustmentPage(selenium);

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "123");
        savingsAccountDetailPage=navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        savingsApplyAdjustmentPage = savingsAccountDetailPage.navigateToApplyAdjustmentPage();
        savingsApplyAdjustmentPage.applyAdjustment("234", "adjustment");

        targetTime = new DateTime(2011,5,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"4.4");
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.WITHDRAWAL, "45");
        savingsAccountDetailPage=navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        savingsApplyAdjustmentPage = savingsAccountDetailPage.navigateToApplyAdjustmentPage();
        savingsApplyAdjustmentPage.applyAdjustment("55", "adjustment");

        targetTime = new DateTime(2011,6,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"4.2");
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "555");
        savingsAccountDetailPage=navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        savingsApplyAdjustmentPage = savingsAccountDetailPage.navigateToApplyAdjustmentPage();
        savingsApplyAdjustmentPage.applyAdjustment("444", "adjustment");

        targetTime = new DateTime(2011,7,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"7.6");
        //step 12-15
        params = getVoluntaryGroupsMonthCalculactionProductParameters();
        params.setApplicableFor(SavingsProductParameters.CLIENTS);
        params.setStartDateDD("01");
        params.setStartDateMM("07");
        params.setStartDateYYYY("2011");

        String savingsId2 = createSavingsAccount(params);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId2, DepositWithdrawalSavingsParameters.DEPOSIT, "555");
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId2, DepositWithdrawalSavingsParameters.WITHDRAWAL, "222");
        // month with last day is a non-working day
        targetTime = new DateTime(2011,8,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPosting();

        navigationHelper.navigateToSavingsAccountDetailPage(savingsId2);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"1.4");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-722
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")// one of the dependent methods throws Exception
    @Test(groups = "no_db_unit")
    public void savingsAccountsWithAdjustments() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2012,1,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        //When
        SavingsProductParameters params = getVoluntaryGroupsMonthCalculactionProductParameters();
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();

        //account1
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Default Group",params.getProductInstanceName(),"2000");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "2000");

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.WITHDRAWAL, "250");

        targetTime = new DateTime(2012,2,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPostingWithCleanup();

        //Then
        navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"7.2");

        //account2
        //When
        targetTime = new DateTime(2011,3,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Default Group",params.getProductInstanceName(),"2000.0");
        String savingsId2 = savingsAccountDetailPage.getAccountId();

        editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId2, editAccountStatusParameters);

        depositParams = new DepositWithdrawalSavingsParameters();

        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId2, DepositWithdrawalSavingsParameters.DEPOSIT, "2000.0");
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId2, DepositWithdrawalSavingsParameters.WITHDRAWAL, "250.0");

        targetTime = new DateTime(2011,4,1,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToAdminPage();
        runBatchJobsForSavingsIntPostingWithCleanup();

        //Then
        navigationHelper.navigateToSavingsAccountDetailPage(savingsId2);
        Assert.assertEquals(selenium.getTable("recentActivityForDetailPage.1.2"),"7.2");

    }

    private String createSavingsAccount(SavingsProductParameters params){
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);
        confirmationPage.navigateToSavingsProductDetails();
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingAccountWithCreatedProduct("Stu1233266299995 Client1233266299995", params.getProductInstanceName(), "100000.0");
        String savingsId = savingsAccountDetailPage.getAccountId();

        EditAccountStatusParameters editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);
        return savingsId;
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

    private void make3StraightDeposit(String savingsId) throws Exception{
        DateTime targetTime = new DateTime(2011,2,10,13,0,0,0);
        DepositWithdrawalSavingsParameters depositParams = new DepositWithdrawalSavingsParameters();

        targetTime = new DateTime(2011,2,14,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        targetTime = new DateTime(2011,2,21,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");

        targetTime = new DateTime(2011,2,28,13,0,0,0);
        depositParams=makeDefaultDepositWithdrawal(targetTime,depositParams,savingsId, DepositWithdrawalSavingsParameters.DEPOSIT, "100000.0");
    }

    private DepositWithdrawalSavingsParameters makeDefaultDepositWithdrawal(DateTime date, DepositWithdrawalSavingsParameters depositParams, String savingsId,String type, String ammount) throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = date;
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        depositParams.setAmount(ammount);
        DepositWithdrawalSavingsParameters depositParamsreturn = setDepositParams(depositParams, Integer.toString(date.getDayOfMonth()), Integer.toString(date.getMonthOfYear()), Integer.toString(date.getYear()));
        depositParams.setTrxnType(type);
        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount(savingsId, depositParams);
        return depositParamsreturn;
    }

    private SavingsProductParameters getVoluntaryGroupsMonthCalculactionProductParameters() {
        SavingsProductParameters params = savingsProductHelper.getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY,SavingsProductParameters.GROUPS);
        params.setMandatoryAmount("2000");
        params.setInterestRate("5");
        params.setDaysOrMonthsForInterestCalculation(params.MONTHS);
        params.setFrequencyOfInterestPostings("1");
        params.setNumberOfDaysOrMonthsForInterestCalculation("1");
        params.setAmountAppliesTo(SavingsProductParameters.WHOLE_GROUP);
        return params;
    }

    private DepositWithdrawalSavingsParameters setDepositParams(DepositWithdrawalSavingsParameters depositParams,String dd, String mm, String yy){
        depositParams.setTrxnDateMM(mm);
        depositParams.setTrxnDateDD(dd);
        depositParams.setTrxnDateYYYY(yy);
        depositParams.setPaymentType(DepositWithdrawalSavingsParameters.CASH);
        depositParams.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);
        return depositParams;
    }

    private void runBatchJobsForSavingsIntPosting() {
        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("SavingsIntPostingTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);
    }

     private void runBatchJobsForSavingsIntPostingWithCleanup() throws SQLException {
        applicationDatabaseOperation.cleanBatchJobTables();
        runBatchJobsForSavingsIntPosting();
    }

}
