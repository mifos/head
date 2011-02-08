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
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.CreateMeetingPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage.CreateGroupSubmitParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
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
    SavingsAccountHelper savingsAccountHelper;
    CenterTestHelper centerTestHelper;
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
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
        centerTestHelper = new CenterTestHelper(selenium);
        super.setUp();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-228
    public void verifyPropertyBackDatedTransactionsAllowedFalse() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setBackDatedTransactionsAllowed("false");
        //When
        navigationHelper.navigateToLoanAccountPage("000100000000004").navigateToDisburseLoan().verifyDisbursalDateIsDisabled();
        //Then
        propertiesHelper.setBackDatedTransactionsAllowed("true");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-235
    public void verifyPropertyClientRulesClientCanExistOutsideGroupFalse() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setClientCanExistOutsideGroup("false");
        //When
        navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateNewClientPage();
        //Then
        Assert.assertFalse(selenium.isElementPresent("group_search.link.membershipNotRequired"));
        propertiesHelper.setClientCanExistOutsideGroup("true");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-234
    public void verifyPropertyGroupCanApplyLoansTrue() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setGroupCanApplyLoans("true");
        LoanTestHelper helper = new LoanTestHelper(selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setLoanProduct("WeeklyGroupDeclineLoanWithPeriodicFee");
        searchParameters.setSearchString("MyGroup1232993846342");
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2000.0");
        //When Then
        helper.createLoanAccount(searchParameters, submitAccountParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-233
    public void verifyPropertyGroupCanApplyLoansFalse() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setGroupCanApplyLoans("false");
        //When
        navigationHelper.navigateToGroupViewDetailsPage("MyGroup1232993846342");
        //Then
        Assert.assertFalse(selenium.isElementPresent("viewgroupdetails.link.newLoanAccount"));
        propertiesHelper.setGroupCanApplyLoans("true");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-232
    public void verifyPropertyClientRulesCenterHierarchyExistsFalse() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setCenterHierarchyExists("false");

        //When
        navigationHelper.navigateToClientsAndAccountsPage();

        //Then
        Assert.assertFalse(selenium.isElementPresent("menu.link.label.createnew.center"));
        Assert.assertTrue(selenium.isTextPresent("To review or edit a Client, Group or account"));

        //Given
        propertiesHelper.setCenterHierarchyExists("true");

        //When
        navigationHelper.navigateToClientsAndAccountsPage();

        //Then
        Assert.assertTrue(selenium.isElementPresent("menu.link.label.createnew.center"));
        Assert.assertTrue(selenium.isTextPresent("To review or edit a Client, Group, Center or account"));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-231
    public void verifyPropertyClientRulesCenterHierarchyExistsTrue() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setCenterHierarchyExists("true");

        //When
        CreateCenterEnterDataPage.SubmitFormParameters formParameters = new CreateCenterEnterDataPage.SubmitFormParameters();
        formParameters.setCenterName("testCenterName12123");
        formParameters.setLoanOfficer("Joe1232993835093 Guy1232993835093");
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("Bangalore");
        meeting.setWeekDay(MeetingParameters.WEDNESDAY);
        meeting.setWeekFrequency("1");
        formParameters.setMeeting(meeting);
        CenterViewDetailsPage centerViewDetailsPage = centerTestHelper.createCenter(formParameters, "MyOffice1232993831593");

        //Then
        centerViewDetailsPage.verifyActiveCenter(formParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-216
    public void verifyPropertySavingsPendingApprovalStateEnabled() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setSavingsPendingApprovalStateEnabled("false");
        //When
        CreateSavingsAccountSearchParameters searchParameters = getCreateSavingsAccountSearchParameters();
        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248.0");
        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccountWithoutPendingApprovalState(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        //Then
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
        savingsAccountPage.verifyStatus("Active");

        propertiesHelper.setSavingsPendingApprovalStateEnabled("true");
    }

    private CreateSavingsAccountSearchParameters getCreateSavingsAccountSearchParameters(){
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString("Stu1233266079799 Client1233266079799");
        searchParameters.setSavingsProduct("MandClientSavings3MoPostMinBal");
        return searchParameters;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-215
    public void verifyPropertyPendingApprovalStateEnabledForSavingsAndLoanAccounts() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setSavingsPendingApprovalStateEnabled("true");
        propertiesHelper.setLoanPendingApprovalStateEnabled("true");
        propertiesHelper.setGroupPendingApprovalStateEnabled("true");
        //When
        CreateSavingsAccountSearchParameters searchParameters = getCreateSavingsAccountSearchParameters();

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248.0");

        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        //Then
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
        savingsAccountPage.verifyStatus("Application Pending Approval");
        //when
        CreateLoanAccountSearchParameters searchParameters2 = new CreateLoanAccountSearchParameters();
        searchParameters2.setSearchString("Stu1233266079799 Client1233266079799");
        searchParameters2.setLoanProduct("MonthlyClientFlatLoanWithFees");
        CreateLoanAccountSubmitParameters submitAccountParameters2 = new CreateLoanAccountSubmitParameters();
        submitAccountParameters2.setAmount("2765.0");
        submitAccountParameters2.setGracePeriodTypeNone(true);
        LoanTestHelper loanTestHelper = new LoanTestHelper(selenium);
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters2, submitAccountParameters2);
        loanAccountPage.verifyStatus("Application Pending Approval");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-211
    public void verifyPropertyGroupPendingApprovalStateEnabled() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        propertiesHelper.setGroupPendingApprovalStateEnabled("false");
        //When
        GroupTestHelper groupTestHelper = new GroupTestHelper(selenium);
        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName("testGroup123123123123");
        GroupViewDetailsPage groupViewDetailsPage = groupTestHelper.createNewGroupWithoutPendingForApproval("MyCenter1232993841778" , groupParams);
        //Then
        groupViewDetailsPage.verifyStatus("Active");

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
    //http://mifosforge.jira.com/browse/MIFOSTEST-200
    public void changeDigitsAfterDecimal() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml", dataSource, selenium);

        propertiesHelper.setDigitsAfterDecimal(3);

        LoanAccountPage loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000004");
        loanAccountPage.verifyExactLoanAmount("1000.000");

        propertiesHelper.setDigitsAfterDecimal(2);

        loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000004");
        loanAccountPage.verifyExactLoanAmount("1000.00");

        propertiesHelper.setDigitsAfterDecimal(0);

        loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000004");
        loanAccountPage.verifyExactLoanAmount("1000");

        propertiesHelper.setDigitsAfterDecimal(1);

        loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000004");
        loanAccountPage.verifyExactLoanAmount("1000.0");

    }
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-195
    public void checkNumberDigitsBeforeDecimalForAmountAndAfterDecimalForInterest() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        //When
        ClientsAndAccountsHomepage accountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateLoanAccountSearchPage accountSearchPage = accountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();

        CreateLoanAccountSearchParameters formParameters = new CreateLoanAccountSearchParameters();
        formParameters.setLoanProduct("MonthlyClientFlatLoanWithFees");
        formParameters.setSearchString("Stu1232993852651 Client1232993852651");
        accountSearchPage.searchAndNavigateToCreateLoanAccountPage(formParameters);

        selenium.type("loancreationdetails.input.sumLoanAmount", "123456789012345");
        selenium.type("loancreationdetails.input.interestRate", "12.000001");
        selenium.click("loancreationdetails.button.continue");
        selenium.waitForPageToLoad("30000");
        //Then
        String error = selenium.getText("loancreationdetails.error.message");
        boolean interestError = error.contains("The Interest ratethe number of digits after the decimal separator exceeds the allowed number 5 is invalid because");
        boolean amountError = error.contains("The Amount is invalid because the number of digits before the decimal separator exceeds the allowed number 14");
        Assert.assertEquals(interestError, true);
        Assert.assertEquals(amountError, true);
        //When
        selenium.type("loancreationdetails.input.sumLoanAmount", "12345678901234");
        selenium.type("loancreationdetails.input.interestRate", "12.00001");
        selenium.click("loancreationdetails.button.continue");
        selenium.waitForPageToLoad("30000");
        //Then
        error = selenium.getText("loancreationdetails.error.message");
        interestError = error.contains("The Interest ratethe number of digits after the decimal separator exceeds the allowed number 5 is invalid because");
        amountError = error.contains("The Amount is invalid because the number of digits before the decimal separator exceeds the allowed number 14");
        Assert.assertEquals(interestError, false);
        Assert.assertEquals(amountError, false);

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
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml", dataSource, selenium);
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
