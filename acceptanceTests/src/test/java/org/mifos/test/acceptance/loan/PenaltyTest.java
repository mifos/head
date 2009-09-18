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

package org.mifos.test.acceptance.loan;

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"loan","acceptance","ui"})
public class PenaltyTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,11,14,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
                
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    } 
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyPenalty() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        
        // the data set contains an approved loan w/ id 000100000000005  
        // we use this loan for our test
        ChargeParameters params = new ChargeParameters();
        
        params.setType(ChargeParameters.MISC_PENALTY);
        params.setAmount("10");
        
        loanTestHelper.applyCharge("000100000000005", params);
        
        verifyPenalties("PenaltyTest_001_result_dbunit.xml.zip");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyAndWaivePenalty() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        // this account has an approved but not disbursed loan.
        String accountId = "000100000000005";        
        
        DisburseLoanParameters disbursalParameters = new DisburseLoanParameters();
        
        disbursalParameters.setDisbursalDateDD("08");
        disbursalParameters.setDisbursalDateMM("07");
        disbursalParameters.setDisbursalDateYYYY("2009");
        disbursalParameters.setPaymentType(DisburseLoanParameters.CASH);
        
        // we disburse the loan so that we can waive the penalty.
        loanTestHelper.disburseLoan(accountId, disbursalParameters);
        
        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("15");
        feeParameters.setType(ChargeParameters.MISC_PENALTY);
        
        loanTestHelper.applyCharge(accountId, feeParameters);
        
        loanTestHelper.waivePenalty(accountId);
                
        verifyPenalties("PenaltyTest_002_result_dbunit.xml.zip");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyPenalties(String resultDataSetFile) throws Exception {
        String[] tablesToValidate = { "LOAN_ACTIVITY_DETAILS",  "LOAN_SUMMARY" };
        
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);

        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);             
    }
}
