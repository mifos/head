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

package org.mifos.test.acceptance.savingsproduct;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
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
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"savingsproduct","acceptance", "smoke"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class DefineNewSavingsProductTest extends UiTestCaseBase {

    private SavingsProductHelper savingsProductHelper;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private SavingsAccountHelper savingsAccountHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
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
    @Test(sequential = true, groups = { "savings", "acceptance", "ui" })
    public void createVoluntarySavingsProductForCenters() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY,SavingsProductParameters.CENTERS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("MyCenter1233266075715",params.getProductInstanceName(),"7777.8");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-137
    @Test(enabled=true)
    public void createVoluntarySavingsProductForGroups() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY,SavingsProductParameters.GROUPS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("MyGroup1232993846342", params.getProductInstanceName(), "234.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1093
    @Test(enabled=true)
    public void createVoluntarySavingsProductForClients() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY,SavingsProductParameters.CLIENTS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799",params.getProductInstanceName(),"200.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1094
    @Test(enabled=true)
    public void createMandatorySavingsProductForGroups() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY,SavingsProductParameters.GROUPS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("MyGroup1232993846342",params.getProductInstanceName(),"534.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-138
    @Test(enabled=true)
    public void createMandatorySavingsProductForClients() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY,SavingsProductParameters.CLIENTS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("Stu1233266079799 Client1233266079799",params.getProductInstanceName(),"248.0");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1095
    @Test(enabled=true)
    public void createMandatorySavingsProductForCenters() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY,SavingsProductParameters.CENTERS);
        DefineNewSavingsProductConfirmationPage confirmationPage = savingsProductHelper.createSavingsProduct(params);

        confirmationPage.navigateToSavingsProductDetails();
        createSavingAccountWithCreatedProduct("MyCenter1233266075715",params.getProductInstanceName(),"7777.8");
    }


    /**
     * This method return a fully useable parameter object for the type of deposit (mandatory/voluntary)
     * and applicable for (clients/groups/centers).
     * @return Parameters like noted above.
     */
    private SavingsProductParameters getGenericSavingsProductParameters(int typeOfDeposits,int applicableFor) {
        SavingsProductParameters params = new SavingsProductParameters();

        params.setProductInstanceName("Savings product test" + StringUtil.getRandomString(3));
        params.setShortName("SV" + StringUtil.getRandomString(2));
        params.setProductCategory(SavingsProductParameters.OTHER);
        DateTime today = new DateTime();
        params.setStartDateDD(Integer.valueOf(today.getDayOfMonth()).toString());
        params.setStartDateMM(Integer.valueOf(today.getMonthOfYear()).toString());
        params.setStartDateYYYY(Integer.valueOf(today.getYearOfEra()).toString());

        params.setApplicableFor(applicableFor);
        params.setTypeOfDeposits(typeOfDeposits);

        // these two settings are not required in all configurations
        // but they're good to have anyway
        params.setMandatoryAmount("10");
        params.setAmountAppliesTo(SavingsProductParameters.WHOLE_GROUP);

        params.setInterestRate("4");
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.AVERAGE_BALANCE);
        params.setDaysOrMonthsForInterestCalculation(SavingsProductParameters.MONTHS);
        params.setNumberOfDaysOrMonthsForInterestCalculation("3");
        params.setFrequencyOfInterestPostings("6");

        params.setGlCodeForDeposit("24101");
        params.setGlCodeForInterest("41102");

        return params;
    }

    private void createSavingAccountWithCreatedProduct(String client, String productName, String amount){
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString(client);
        searchParameters.setSavingsProduct(productName);

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount(amount);
        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());

    }
    /**
     * note: verifying stored state of tables should be responsibility of dao/service integration tests.
     * commenting out verification as offerings used to stored office
     */
//    private void verifySavingsProduct(String resultDataSetFile) throws Exception {
//        String[] tablesToValidate = { "PRD_OFFERING",  "SAVINGS_OFFERING" };
//
//        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
//        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);

//        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);
//    }
}