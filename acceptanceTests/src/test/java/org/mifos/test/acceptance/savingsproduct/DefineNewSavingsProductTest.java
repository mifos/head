/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import org.dbunit.dataset.IDataSet;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"savingsproduct","acceptance"})
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class DefineNewSavingsProductTest extends UiTestCaseBase {
    
    SavingsProductHelper savingsProductHelper;    
    
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        savingsProductHelper = new SavingsProductHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    public void createVoluntarySavingsProductForClients() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_default_003_dbunit.xml.zip", dataSource, selenium);
        
        SavingsProductParameters params = getGenericSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.VOLUNTARY);
        params.setApplicableFor(SavingsProductParameters.CLIENTS);
        
        savingsProductHelper.createSavingsProduct(params);
        
        verifySavingsProduct("DefineNewSavingsProduct_001_result_dbunit.xml.zip");
    }
    
    public void createMandatorySavingsProductForClients() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_default_003_dbunit.xml.zip", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.MANDATORY);
        params.setApplicableFor(SavingsProductParameters.CLIENTS);
        
        savingsProductHelper.createSavingsProduct(params);

        verifySavingsProduct("DefineNewSavingsProduct_002_result_dbunit.xml.zip");
    }

    public void createVoluntarySavingsProductForGroups() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_default_003_dbunit.xml.zip", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.VOLUNTARY);
        params.setApplicableFor(SavingsProductParameters.GROUPS);
        
        savingsProductHelper.createSavingsProduct(params);

        verifySavingsProduct("DefineNewSavingsProduct_003_result_dbunit.xml.zip");
    }   
    
    public void createMandatorySavingsProductForGroups() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_default_003_dbunit.xml.zip", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.MANDATORY);
        params.setApplicableFor(SavingsProductParameters.GROUPS);
        
        savingsProductHelper.createSavingsProduct(params);

        verifySavingsProduct("DefineNewSavingsProduct_004_result_dbunit.xml.zip");
    }
    
    public void createVoluntarySavingsProductForCenters() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_default_003_dbunit.xml.zip", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.VOLUNTARY);
        params.setApplicableFor(SavingsProductParameters.CENTERS);
        
        savingsProductHelper.createSavingsProduct(params);

        verifySavingsProduct("DefineNewSavingsProduct_005_result_dbunit.xml.zip");
    }
    
    public void createMandatorySavingsProductForCenters() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_default_003_dbunit.xml.zip", dataSource, selenium);

        SavingsProductParameters params = getGenericSavingsProductParameters();
        params.setTypeOfDeposits(SavingsProductParameters.MANDATORY);
        params.setApplicableFor(SavingsProductParameters.CENTERS);

        savingsProductHelper.createSavingsProduct(params);

        verifySavingsProduct("DefineNewSavingsProduct_006_result_dbunit.xml.zip");
    }
    
    
    /**
     * This method return a fully useable parameter object EXCEPT for the type of deposit (mandatory/voluntary)
     * and applicable for (clients/groups/centers).
     * @return Parameters like noted above.
     */
    private SavingsProductParameters getGenericSavingsProductParameters() {
        SavingsProductParameters params = new SavingsProductParameters();
        
        params.setProductInstanceName("Savings product" + StringUtil.getRandomString(3));
        params.setShortName("SV" + StringUtil.getRandomString(2));
        params.setProductCategory(SavingsProductParameters.OTHER);
        params.setStartDateDD("15");
        params.setStartDateMM("06");
        params.setStartDateYYYY("2010");
        
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
    
    private void verifySavingsProduct(String resultDataSetFile) throws Exception {
        String[] tablesToValidate = { "PRD_OFFERING",  "SAVINGS_OFFERING" };
        
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);

        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);     
        
        
    }

    
}
