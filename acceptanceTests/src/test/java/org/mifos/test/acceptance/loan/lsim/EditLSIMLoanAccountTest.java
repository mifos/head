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

package org.mifos.test.acceptance.loan.lsim;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditPreviewLoanAccountPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"loan","acceptance","ui"})
public class EditLSIMLoanAccountTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private static final String START_DATA_SET = "acceptance_small_011_dbunit.xml.zip";

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010,1,19,17,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    private void initData() throws DatabaseUnitException, SQLException, IOException, URISyntaxException {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
    }
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void editLSIMLoanWithMonthlySecondTuesdayPayment() throws Exception {
        initData();
        String testAccount = "000100000000221";
        EditLoanAccountInformationParameters params = new EditLoanAccountInformationParameters();
        params.setExternalID("ID83328");
        LoanAccountPage loanAccountPage = editLoanAccount(testAccount, params);
        loanAccountPage.verifyPage();
        assertTextFoundOnPage(params.getExternalID());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void editLSIMLoanWithMonthlyTenthDayOfMonthPayment() throws Exception {
        initData();
        String testAccount = "000100000000222";
        EditLoanAccountInformationParameters params = new EditLoanAccountInformationParameters();
        params.setExternalID("ID98765");
        LoanAccountPage loanAccountPage = editLoanAccount(testAccount, params);
        loanAccountPage.verifyPage();
        assertTextFoundOnPage(params.getExternalID());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void editLSIMLoanWithWeeklyPayment() throws Exception {
        initData();
        String testAccount = "000100000000223";
        EditLoanAccountInformationParameters params = new EditLoanAccountInformationParameters();
        params.setExternalID("ID2323ID");
        LoanAccountPage loanAccountPage = editLoanAccount(testAccount, params);
        loanAccountPage.verifyPage();
        assertTextFoundOnPage(params.getExternalID());
    }

    private LoanAccountPage editLoanAccount(String accountID, EditLoanAccountInformationParameters params) {
        NavigationHelper helper = new NavigationHelper(selenium);

        LoanAccountPage loanAccountPage = helper.navigateToLoanAccountPage(accountID);
        loanAccountPage.verifyPage();

        EditLoanAccountInformationPage editAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
        editAccountInformationPage.verifyPage();
        editAccountInformationPage.editExternalID(params);
        EditPreviewLoanAccountPage editPreviewLoanAccountPage = editAccountInformationPage.submitAndNavigateToAccountInformationPreviewPage();
        editPreviewLoanAccountPage.verifyPage();
        loanAccountPage = editPreviewLoanAccountPage.submitAndNavigateToLoanAccountPage();

        return loanAccountPage;
    }


}
