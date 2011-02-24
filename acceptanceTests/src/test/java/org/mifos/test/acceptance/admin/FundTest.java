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

package org.mifos.test.acceptance.admin;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.admin.EditFundPage.EditFundSubmitParameters;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.CreateNewFundConfirmationPage;
import org.mifos.test.acceptance.framework.admin.EditFundConfirmationPage;
import org.mifos.test.acceptance.framework.admin.FundCreatePage;
import org.mifos.test.acceptance.framework.admin.ViewFundsPage;
import org.mifos.test.acceptance.framework.admin.FundCreatePage.CreateFundSubmitParameters;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"fund","acceptance","ui"})
public class FundTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    private AppLauncher appLauncher;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;


    private static String dataFileName = "acceptance_small_003_dbunit.xml";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);

    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    //http://mifosforge.jira.com/browse/MIFOSTEST-703
    public void createViewAndEditFundTest() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, dataFileName, dataSource, selenium);
        //When
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();

        FundCreatePage fundCreatePage = adminPage.navigateToFundCreatePage();

        CreateFundSubmitParameters submitParameters = new CreateFundSubmitParameters();
        submitParameters.setFundCode("01");
        submitParameters.setFundName("aaa");

        CreateNewFundConfirmationPage confirmationPage = fundCreatePage.submitAndNavigateToNewFundConfirmationPage(submitParameters);
        confirmationPage.submitAndNavigateToAdminPage();

        ViewFundsPage fundsPage = adminPage.navigateToViewFundsPage();
        String[] expectedFundNames = new String[]{
                "Non Donor",
                "Funding Org A",
                "Funding Org B",
                "Funding Org C",
                "Funding Org D",
                "aaa"
        };

        String[] expectedFundCodes = new String[]{
                "00",
                "00",
                "00",
                "00",
                "00",
                "01"
        };
        //Then
        fundsPage.verifyFundNameAndCode(expectedFundNames, expectedFundCodes);
        //When
        String arabic = "الإدارة";
        String chinese = "贷款";
        EditFundSubmitParameters editSubmitParameters = new EditFundSubmitParameters();
        editSubmitParameters.setFundName("aaa"+arabic+chinese);

        EditFundPage editFundPage = fundsPage.editAndNavigateToEditFundPage();
        EditFundConfirmationPage editFundConfirmationPage = editFundPage.submitAndNavigateToEditFundConfirmationPage(editSubmitParameters);
        adminPage = editFundConfirmationPage.submitAndNavigateToAdminPage();

        fundsPage = adminPage.navigateToViewFundsPage();
        String[] expectedFundNamesSpecial = new String[]{
                "Non Donor",
                "Funding Org A",
                "Funding Org B",
                "Funding Org C",
                "Funding Org D",
                "aaa"+arabic+chinese
        };
        //Then
        fundsPage.verifyFundNameAndCode(expectedFundNamesSpecial, expectedFundCodes);

    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
         .launchMifos()
         .loginSuccessfullyUsingDefaultCredentials()
         .navigateToAdminPage();
     }

}