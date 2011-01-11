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

package org.mifos.test.acceptance.loan;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
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
public class RedoLoanDisbursalTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,26,15,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium, navigationHelper);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /*
     * Verify a redone loan directly moves into "Closed-Met Obligation"
     * state when the loan is wholly paid off before the current date.
     *
     * http://mifosforge.jira.com/browse/MIFOSTEST-12
     * http://mifosforge.jira.com/browse/MIFOSTEST-17
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalWithPastDate() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        RedoLoanDisbursalParameters paramsPastDate = new RedoLoanDisbursalParameters();
        paramsPastDate.setDisbursalDateDD("09");
        paramsPastDate.setDisbursalDateMM("07");
        paramsPastDate.setDisbursalDateYYYY("2009");

        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursal("MyGroup1233266255641", "WeeklyGroupFlatLoanWithOnetimeFee", paramsPastDate, null, 3237);

        loanAccountPage.verifyStatus("Closed- Obligation met");
    }

    /*
     * Verify that the status of the loan is Active in Good Standing
     * when the loan is not wholly paid off before current date.
     * Also verifies that loan cannot be redone on a date equal to
     * or greater than the current date.
     *
     * http://mifosforge.jira.com/browse/MIFOSTEST-13
     * http://mifosforge.jira.com/browse/MIFOSTEST-15
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalWithPastDateUnpaid() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        RedoLoanDisbursalParameters paramsPastDate = new RedoLoanDisbursalParameters();
        paramsPastDate.setDisbursalDateDD("09");
        paramsPastDate.setDisbursalDateMM("07");
        paramsPastDate.setDisbursalDateYYYY("2009");
        RedoLoanDisbursalParameters paramsCurrentDate = new RedoLoanDisbursalParameters();
        paramsCurrentDate.setDisbursalDateDD("29");
        paramsCurrentDate.setDisbursalDateMM("07");
        paramsCurrentDate.setDisbursalDateYYYY("2009");

        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursal("MyGroup1233266255641", "WeeklyGroupFlatLoanWithOnetimeFee", paramsPastDate, paramsCurrentDate, 1237);

        loanAccountPage.verifyStatus("Active in Good Standing");
    }
}
