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

package org.mifos.test.acceptance.properties;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.CreateMeetingPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"acceptance","ui", "properties"})
public class UpdateCustomPropertiesTest extends UiTestCaseBase {
    NavigationHelper navigationHelper;
    CustomPropertiesHelper propertiesHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    String errorInterestExceedsLimit = "The max interest is invalid because it is not in between";
    String errorInterestDigitsAfterDecimal ="The max interest is invalid because the number of digits after the decimal separator";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
        super.setUp();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void changeLocale() {
        // update the language to French
        propertiesHelper.setLocale("FR", "FR");

        // make sure that the welcome text does not contain "Welcome"
        HomePage homePage = navigationHelper.navigateToHomePage();
        String welcomeText = homePage.getWelcome();
        Assert.assertFalse(welcomeText.contains("Welcome"), "The welcome text contained \"Welcome\" even though the language is supposed to have changed!");

        propertiesHelper.setLocale("EN", "GB");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void changeDecimalsToThree() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        propertiesHelper.setDigitsAfterDecimal(3);

        LoanAccountPage loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000004");
        loanAccountPage.verifyLoanAmount("1050.000");

        propertiesHelper.setDigitsAfterDecimal(1);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void changeMinInterestRateToTwelve() throws Exception {
        propertiesHelper.setMinInterest(12);
        SubmitFormParameters  submitFormParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        submitFormParameters.setMaxInterestRate("8");//invalid value
        submitFormParameters.setMinInterestRate("1");
        submitFormParameters.setDefaultInterestRate("3");
        verifyInvalidInterestInLoanProduct(submitFormParameters,true);
        propertiesHelper.setMinInterest(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void changeMaxInterestRateToFive() throws Exception {
        propertiesHelper.setMaxInterest(5);
        SubmitFormParameters  submitFormParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        submitFormParameters.setMaxInterestRate("12");//invalid value
        submitFormParameters.setMinInterestRate("1");
        submitFormParameters.setDefaultInterestRate("3");
        verifyInvalidInterestInLoanProduct(submitFormParameters,true);
        propertiesHelper.setMaxInterest(999);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void changeDigitsAfterDecimalForInterestToThree() throws Exception {
        propertiesHelper.setDigitsAfterDecimalForInterest(3);
        SubmitFormParameters  submitFormParameters =FormParametersHelper.getWeeklyLoanProductParameters();
        submitFormParameters.setMaxInterestRate("6.33333");//invalid value
        submitFormParameters.setMinInterestRate("1");
        submitFormParameters.setDefaultInterestRate("3");
        verifyInvalidInterestInLoanProduct(submitFormParameters,false);
        propertiesHelper.setDigitsAfterDecimalForInterest(5);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void removeThursdayFromWorkingDays() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        String workingDays ="Monday,Tuesday,Wednesday,Friday,Saturday";
        propertiesHelper.setWorkingDays(workingDays);
        CreateCenterEnterDataPage createCenterEnterDataPage = navigationHelper.navigateToCreateCenterEnterDataPage("Test Branch Office");
        CreateMeetingPage createMeetingPage = createCenterEnterDataPage.navigateToCreateMeetingPage();
        createMeetingPage.verifyWorkingDays(workingDays);

        CreateClientEnterMfiDataPage createClientEnterMfiDataPage = navigationHelper.navigateToCreateClientEnterMfiDataPage("Test Branch Office");
        createMeetingPage = createClientEnterMfiDataPage.navigateToCreateMeetingPage();
        createMeetingPage.verifyWorkingDays(workingDays);
        propertiesHelper.setWorkingDays("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");
    }

    private void verifyInvalidInterestInLoanProduct(SubmitFormParameters formParameters,boolean checkInterestExceedsLimit)
    {
        DefineNewLoanProductPage newLoanPage = navigationHelper.navigateToDefineNewLoanProductPage();
        newLoanPage.fillLoanParameters(formParameters);
        DefineNewLoanProductPreviewPage previewPage = newLoanPage.submitAndGotoNewLoanProductPreviewPage();
        if(checkInterestExceedsLimit)
        {
            previewPage.verifyErrorInForm(errorInterestExceedsLimit);
        }
        else
        {
            previewPage.verifyErrorInForm(errorInterestDigitsAfterDecimal);
        }
    }
}
