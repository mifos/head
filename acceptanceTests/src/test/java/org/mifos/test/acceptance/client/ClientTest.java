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

package org.mifos.test.acceptance.client;

import static java.util.Arrays.asList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineAcceptedPaymentTypesPage;
import org.mifos.test.acceptance.framework.admin.DefineHiddenMandatoryFieldsPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPage;
import org.mifos.test.acceptance.framework.client.ClientEditMFIParameters;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPreviewPage;
import org.mifos.test.acceptance.framework.client.ClientEditPersonalInfoPage;
import org.mifos.test.acceptance.framework.client.ClientNotesPage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientStatus;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientConfirmationPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientPreviewDataPage;
import org.mifos.test.acceptance.framework.client.QuestionGroup;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPreviewPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage.CreateGroupSubmitParameters;
import org.mifos.test.acceptance.framework.group.EditCustomerStatusParameters;
import org.mifos.test.acceptance.framework.group.GroupCloseReason;
import org.mifos.test.acceptance.framework.group.GroupStatus;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentConfirmationPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionsPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsProductHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.StringUtil;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@SuppressWarnings({"PMD.TooManyFields","PMD.ExcessiveClassLength"})
@Test(singleThreaded = true, groups = {"client", "acceptance", "ui", "no_db_unit"})
public class ClientTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    private CustomPropertiesHelper propertiesHelper;
    private ClientTestHelper clientTestHelper;
    private QuestionGroupTestHelper questionGroupTestHelper;
    private GroupTestHelper groupTestHelper;
    private SavingsAccountHelper savingsAccountHelper;
    private SavingsProductHelper savingsProductHelper;
    private FeeTestHelper feeTestHelper;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    public static final String MULTI_SELECT = "Multi Select";
    public static final String EXPECTED_DATE_FORMAT = "%02d/%02d/%04d";
    public static final String NUMBER = "Number";
    public static final String SMART_SELECT = "Smart Select";
    private String questionGroupTitle;
    private String question1 = "663q1";
    private String question2 = "663q2";
    private static final String question3 = "663q3";
    private static final String question4 = "663q4";
    private static final String question5 = "663q5";
    private static final String question6 = "663q6";
    private static final String question7 = "663q7";
    private static final String question8 = "663q8";
    private static final String question9 = "663q9";
    private static final String question10 = "663q10";
    private static final String question11 = "663q11";
    private static final String officeName = "MyOfficeDHMFT";
    private String response;
    private ClientViewDetailsPage viewClientDetailsPage;
    private Map<Integer, QuestionGroup> questionGroupInstancesOfClient;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        groupTestHelper = new GroupTestHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
        savingsProductHelper = new SavingsProductHelper(selenium);
        
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 7, 11, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        
        feeTestHelper = new FeeTestHelper(dataSetup, navigationHelper);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        setDefaultProperties();
        (new MifosPage(selenium)).logout();
    }

    private void setDefaultProperties() {
        propertiesHelper.setClientPendingApprovalStateEnabled("true");
        propertiesHelper.setClientsNameSequence("first_name,middle_name,last_name,second_last_name");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-248
    @Test(enabled=true)
    public void verifyAcceptedPaymentTypes() throws Exception {
        // When
        String groupName = "group1";
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        clientParams = clientParams();
        clientParams.setFirstName("John");
        clientParams.setLastName("Doe123");

        ClientViewDetailsPage clientViewDetailsPage = clientTestHelper.createNewClient(groupName, clientParams);
        clientViewDetailsPage.verifyHeading("John Doe123");

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineAcceptedPaymentTypesPage defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(DefineAcceptedPaymentTypesPage.CHEQUE);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(DefineAcceptedPaymentTypesPage.VOUCHER);

        ApplyPaymentPage applyPaymentPage = navigationHelper.navigateToClientViewDetailsPage("John Doe123")
                .navigateToViewClientChargesDetail().navigateToApplyPayments();
        // Then
        applyPaymentPage.verifyModeOfPayments();

    }
    
    @Test(singleThreaded = true, groups = {"smoke", "client", "acceptance", "ui"}, enabled=true)
    public void verifyErrorsMessages() {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineHiddenMandatoryFieldsPage mandatoryFieldsPage = adminPage.navigateToDefineHiddenMandatoryFields();

        mandatoryFieldsPage.checkMandatoryCitizenShip();
        mandatoryFieldsPage.checkMandatoryEthnicity();
        mandatoryFieldsPage.checkMandatoryMaritalStatus();
        
        mandatoryFieldsPage.submit();
        
        CreateClientEnterPersonalDataPage personalDataPage = navigationHelper.navigateToCreateClientEnterPersonalDataPage(officeName);
        
        String[] errors = personalDataPage.getMandatoryBlankFieldsNames();
        String[] fields = new String[] { "Salutation", "First Name", "Last Name", "Date of birth", "Gender",
                "Ethnicity", "Citizenship", "Poverty status", "Marital Status" };
        
        for(int i = 0; i < fields.length; ++i) {
            Assert.assertEquals(fields[i], errors[i]);
        }
        
        adminPage = navigationHelper.navigateToAdminPage();
        
        adminPage.navigateToDefineHiddenMandatoryFields();
        
        mandatoryFieldsPage.uncheckMandatoryCitizenShip();
        mandatoryFieldsPage.uncheckMandatoryEthnicity();
        mandatoryFieldsPage.uncheckMandatoryMaritalStatus();
        
        mandatoryFieldsPage.submit();
        adminPage.logout();
    }

    @Test(singleThreaded = true, groups = {"smoke", "client", "acceptance", "ui"}, enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-208
    public void createClientAndChangeStatusTest() throws Exception {

        ClientViewDetailsPage clientDetailsPage = clientTestHelper.createClientAndVerify("loan officer",
                "MyOfficeDHMFT");

        // When / Then
        clientTestHelper.changeCustomerStatus(clientDetailsPage);
    }

    @Test(enabled=true)
    // http://mifosforge.jira.com/browse/MIFOS-4776
    public void createClientAddTwoFeesTryPayMoreThanAmountVerifyErrorMessage() {
        String oneTimeFeeName = "One Time Fee";
        String periodicTimeFee = "Periodic Time Fee";
        
        defineNewFree(oneTimeFeeName, SubmitFormParameters.ONETIME_FEE_FREQUENCY, 10.0);
        defineNewFree(periodicTimeFee, SubmitFormParameters.PERIODIC_FEE_FREQUENCY, 37.0);
        
        String clientName = "Stu1233266299995 Client1233266299995";

        ChargeParameters chargeParameters = new ChargeParameters();
        chargeParameters.setAmount("10");
        chargeParameters.setType(oneTimeFeeName);
        clientTestHelper.applyCharge(clientName, chargeParameters);
        
        chargeParameters.setAmount("37");
        chargeParameters.setType(periodicTimeFee);
        clientTestHelper.applyCharge(clientName, chargeParameters);
        
        PaymentParameters params = new PaymentParameters();
        params.setTransactionDateDD("11");
        params.setTransactionDateMM("02");
        params.setTransactionDateYYYY("2009");
        params.setAmount("48");
        params.setPaymentType(PaymentParameters.CASH);
        params.setReceiptId("");
        params.setReceiptDateDD("");
        params.setReceiptDateMM("");
        params.setReceiptDateYYYY("");
        
        ApplyPaymentConfirmationPage applyPaymentConfirmationPage = clientTestHelper
                .navigateToClientViewDetailsPage(clientName)
                .navigateToViewClientChargesDetail().navigateToApplyPayments()
                .submitAndNavigateToApplyPaymentConfirmationPage(params);
        
        applyPaymentConfirmationPage.dontLoadNext();
        
        String actual = applyPaymentConfirmationPage.getSelenium()
                .getText("//span[@id='reviewapplypayment.error.message']");
        
        String expected = "Payment cannot be more than Amount due.";
        
        Assert.assertEquals(expected, actual);
    }

    private void defineNewFree(String name, int feeFrequencyType, double amount) {
        SubmitFormParameters params = new SubmitFormParameters();
        params.setFeeName(name);
        params.setCategoryType(SubmitFormParameters.ALL_CUSTOMERS);
        params.setFeeFrequencyType(feeFrequencyType);
        
        if(feeFrequencyType == SubmitFormParameters.ONETIME_FEE_FREQUENCY) {
            params.setCustomerCharge(SubmitFormParameters.UPFRONT);
        } else {
            params.setFeeRecurrenceType(SubmitFormParameters.WEEKLY_FEE_RECURRENCE);
            params.setWeekRecurAfter(1);
        }
        params.setAmount(amount);
        params.setGlCode(31301);
        
        feeTestHelper.defineFees(params);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-310
    @Test(enabled=true)
    public void searchClientAndEditExistingClientDetails() throws Exception {
        HomePage homePage = navigationHelper.navigateToHomePage();
        homePage = searchForClient("client1 lastname", homePage, 1);
        homePage = searchForClient("zzz", homePage, 0);

        SearchResultsPage searchResultsPage = homePage.search("client1 lastname");
        searchResultsPage.verifyPage();
        int numResults = searchResultsPage.countSearchResults();
        Assert.assertEquals(numResults, 1);

        ClientViewDetailsPage viewDetailsPage = searchResultsPage
                .navigateToClientViewDetailsPage("link=client1 lastname*");
        ClientNotesPage notesPage = viewDetailsPage.navigateToNotesPage();
        notesPage.addNotePreviewAndSubmit("test note");
        viewDetailsPage.verifyNotes("test note");

        CustomerChangeStatusPage changeStatusPage = viewDetailsPage.navigateToCustomerChangeStatusPage();
        EditCustomerStatusParameters parameters = new EditCustomerStatusParameters();
        parameters.setClientStatus(ClientStatus.ON_HOLD);
        parameters.setNote("test");
        CustomerChangeStatusPreviewPage changeStatusPreviewPage = changeStatusPage
                .setChangeStatusParametersAndSubmit(parameters);
        viewDetailsPage = changeStatusPreviewPage.submitAndGotoClientViewDetailsPage();
        viewDetailsPage.verifyStatus("On Hold");

        ClientEditPersonalInfoPage editPersonalInfoPage = viewDetailsPage.editPersonalInformation();
        CreateClientEnterPersonalDataPage.SubmitFormParameters parameters2 = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        parameters2.setSpouseFirstName("FatherFirstnameTest");
        parameters2.setSpouseLastName("FatherLastNameTest");
        parameters2.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        parameters2.setDateOfBirthYYYY("1960");
        parameters2.setDateOfBirthMM("08");
        parameters2.setDateOfBirthDD("01");
        viewDetailsPage = editPersonalInfoPage.submitAndNavigateToViewDetailsPage(parameters2);
        viewDetailsPage.verifySpouseFather("FatherFirstnameTest FatherLastNameTest");

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-236
    @Test(enabled=true)
    public void createClientOutsideGroup() throws Exception {
        // When
        CreateClientEnterPersonalDataPage clientPersonalDataPage = navigationHelper.navigateToCreateClientEnterPersonalDataPage(officeName);
        // we remember form parameters to verify MIFOS-5032
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = FormParametersHelper.getClientEnterPersonalDataPageFormParameters();
        clientPersonalDataPage=clientPersonalDataPage.create(formParameters);
        CreateClientEnterMfiDataPage clientEnterMfiDataPage = clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();

        CreateClientEnterMfiDataPage.SubmitFormParameters parameters = new CreateClientEnterMfiDataPage.SubmitFormParameters();
        parameters.setLoanOfficerId("loan officer");

        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("testMeetingPlace");
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.MONDAY);
        parameters.setMeeting(meeting);

        CreateClientPreviewDataPage createClientPreviewDataPage = clientEnterMfiDataPage
                .submitAndGotoCreateClientPreviewDataPage(parameters);
        CreateClientConfirmationPage clientConfirmationPage = createClientPreviewDataPage.submit();

        // Then
        clientConfirmationPage.navigateToClientViewDetailsPage();

        // extension to verify MIFOS-5032
        clientPersonalDataPage = navigationHelper.navigateToCreateClientEnterPersonalDataPage(officeName);
        clientPersonalDataPage=clientPersonalDataPage.create(formParameters);
        clientEnterMfiDataPage = clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        createClientPreviewDataPage = clientEnterMfiDataPage.submitAndGotoCreateClientPreviewDataPage(parameters);
        createClientPreviewDataPage.submitWithOneError("The combination of the specified Date of Birth and name " +
                formParameters.getFirstName() + " " + formParameters.getLastName() + " already exists in the application. Please specify a different name.");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private HomePage searchForClient(String clientName, HomePage homePage, int expectedNumberOfClients)
            throws Exception {
        SearchResultsPage searchResultsPage = homePage.search(clientName);
        searchResultsPage.verifyPage();
        int numResults = searchResultsPage.countSearchResults();
        Assert.assertEquals(expectedNumberOfClients, numResults);

        selenium.click("clientsAndAccountsHeader.link.home");
        selenium.waitForPageToLoad("30000");

        return new HomePage(selenium);
    }

    // implementation of test described in issue 2454
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void searchForClientAndEditDetailsTest() throws Exception {

        ClientsAndAccountsHomepage clientsPage = navigationHelper.navigateToClientsAndAccountsPage();
        ClientSearchResultsPage searchResultsPage = clientsPage.searchForClient("client1");
        searchResultsPage.verifyPage();
        ClientViewDetailsPage clientDetailsPage = searchResultsPage
                .navigateToSearchResult("client1 lastname: ID 0002-000000005");

        ClientEditMFIPage editMFIPage = clientDetailsPage.navigateToEditMFIPage();
        editMFIPage.verifyPage();

        ClientEditMFIParameters params = new ClientEditMFIParameters();
        params.setExternalId("extID123");
        params.setTrainedDateDD("15");
        params.setTrainedDateMM("12");
        params.setTrainedDateYYYY("2008");

        ClientEditMFIPreviewPage mfiPreviewPage = editMFIPage.submitAndNavigateToClientEditMFIPreviewPage(params);
        mfiPreviewPage.verifyPage();
        clientDetailsPage = mfiPreviewPage.submit();
        assertTextFoundOnPage("extID123");
        assertTextFoundOnPage("15/12/2008");
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-663
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void closeClientAccountWithQG() throws Exception {
        //Given
        String groupName = "group1";
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams = clientParams();
        clientParams.setFirstName("John");
        clientParams.setLastName("Doe4321");
        ClientViewDetailsPage clientPage = clientTestHelper.createNewClient(groupName, clientParams);
        String clientName = clientPage.getGlobalCustNum();
        clientTestHelper.changeCustomerStatus(clientPage, ClientStatus.ACTIVE);
        
        createQuestions2();
        createQuestionGroup2();

        String qG_1 = "CloseClientQG";
        String qG_2 = "CloseClientQG2";
        QuestionResponseParameters responseParams = getQuestionResponseParametersForClientAccountClose("answer1");
        QuestionResponseParameters responseParams2 = getQuestionResponseParametersForClientAccountClose("answer2");
        QuestionResponseParameters responseParamsAfterModyfication = getQuestionResponseParametersForClientAccountCloseAfterModyfication("answer2");
        List<CreateQuestionParameters> questionsList = new ArrayList<CreateQuestionParameters>();
        questionsList.add(newFreeTextQuestionParameters("663new question 1"));
        questionsList.add(newFreeTextQuestionParameters("663new question 2"));
        questionsList.add(newFreeTextQuestionParameters("663new question 3"));
        String[] newActiveQuestions = { "663new question 1", "663new question 2" };
        String[] deactivateArray = { "663new question 3", question3, question6, question2, question5};
        String[] deactivatedGroupArray = {question10, question11};
        List<String> deactivateList = Arrays.asList(deactivateArray);
        Map<String, String> questionsAndAnswers = new HashMap<String, String>();
        questionsAndAnswers.put("663new question 1", "answer2");
        questionsAndAnswers.put("663new question 2", "answer2");
        questionsAndAnswers.put(question1, "24/01/2011");
        questionsAndAnswers.put(question4, "10");
        questionsAndAnswers.put(question7, "24/01/2011");
        questionsAndAnswers.put(question8, "jan");
        questionsAndAnswers.put(question9, "answer2");
        //When / Then
        QuestionResponsePage responsePage = clientTestHelper.navigateToQuestionResponsePageWhenCloseClientAccount(clientName);
        responsePage.populateAnswers(responseParams);
        responsePage.navigateToNextPage();
        responsePage = new CustomerChangeStatusPreviewPage(selenium).navigateToEditAdditionalInformation();
        responsePage.populateAnswers(responseParams2);
        responsePage.navigateToNextPage();
        new CustomerChangeStatusPreviewPage(selenium).cancelAndGotoClientViewDetailsPage();

        QuestionGroupTestHelper questionTestHelper = new QuestionGroupTestHelper(selenium);
        questionTestHelper.addNewQuestionsToQuestionGroup(qG_1, questionsList);
        questionTestHelper.markQuestionsAsInactive(deactivateList);
        questionTestHelper.markQuestionGroupAsInactive(qG_2);
        responsePage = clientTestHelper.navigateToQuestionResponsePageWhenCloseClientAccount(clientName);

        responsePage.verifyQuestionsDoesnotappear(deactivateArray);
        responsePage.verifyQuestionsDoesnotappear(deactivatedGroupArray);
        responsePage.verifyQuestionsExists(newActiveQuestions);
        
        clientTestHelper.closeClientWithQG(clientName, responseParamsAfterModyfication);

        verifyQuestionResponsesExistInDatabase(clientName, "Close Client", questionsAndAnswers);
        
        questionTestHelper.markQuestionsAsInactive(asList("663new question 1","663new question 2", question1,
                question2, question4, question7, question8, question9, question10, question11));
        questionTestHelper.markQuestionGroupAsInactive(qG_1);
    }
    
    private void createQuestions2() {
        List<CreateQuestionParameters> questions = new ArrayList<CreateQuestionParameters>();
        CreateQuestionParameters q1 = new CreateQuestionParameters();
        q1.setType(CreateQuestionParameters.TYPE_DATE);
        q1.setText(question1);
        questions.add(q1);
        CreateQuestionParameters q2 = new CreateQuestionParameters();
        q2.setType(CreateQuestionParameters.TYPE_MULTI_SELECT);
        q2.setText(question2);
        q2.setChoicesFromStrings(Arrays.asList(new String[] {"first", "second"}));
        questions.add(q2);
        CreateQuestionParameters q3 = new CreateQuestionParameters();
        q3.setType(CreateQuestionParameters.TYPE_NUMBER);
        q3.setText(question3);
        q3.setNumericMax(10);
        q3.setNumericMin(0);
        questions.add(q3);
        CreateQuestionParameters q4 = new CreateQuestionParameters();
        q4.setType(CreateQuestionParameters.TYPE_NUMBER);
        q4.setText(question4);
        q4.setNumericMax(10);
        q4.setNumericMin(0);
        questions.add(q4);
        CreateQuestionParameters q5 = new CreateQuestionParameters();
        q5.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q5.setText(question5);
        questions.add(q5);
        CreateQuestionParameters q6 = new CreateQuestionParameters();
        q6.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q6.setText(question6);
        q6.setChoicesFromStrings(asList("good", "wrong"));
        questions.add(q6);
        CreateQuestionParameters q7 = new CreateQuestionParameters();
        q7.setType(CreateQuestionParameters.TYPE_DATE);
        q7.setText(question7);
        questions.add(q7);
        CreateQuestionParameters q8 = new CreateQuestionParameters();
        q8.setType(CreateQuestionParameters.TYPE_MULTI_SELECT);
        q8.setText(question8);
        q8.setChoicesFromStrings(Arrays.asList(new String[] {"jan", "feb"}));
        questions.add(q8);
        CreateQuestionParameters q9 = new CreateQuestionParameters();
        q9.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q9.setText(question9);
        questions.add(q9);
        CreateQuestionParameters q10 = new CreateQuestionParameters();
        q10.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q10.setText(question10);
        q10.setChoicesFromStrings(Arrays.asList(new String[] {"1", "2","3"}));
        questions.add(q10);
        CreateQuestionParameters q11 = new CreateQuestionParameters();
        q11.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q11.setText(question11);
        questions.add(q11);
        questionGroupTestHelper.createQuestions(questions);
    }
    
    private void createQuestionGroup2() {
        String qG_1 = "CloseClientQG";
        String qG_2 = "CloseClientQG2";

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        CreateQuestionGroupParameters parameters;
        parameters = questionGroupTestHelper.getCreateQuestionGroupParameters(qG_1, asList(question1, question2, question3, question4, question5),
                "Close Client", "Section1");
        parameters.addExistingQuestion("Section2", question7);
        parameters.addExistingQuestion("Section2", question6);
        parameters.addExistingQuestion("Section2", question9);
        parameters.addExistingQuestion("Section2", question8);
        for (String section : parameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        createQuestionGroupPage.submit(parameters);
        adminPage = navigationHelper.navigateToAdminPage();
        createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        parameters = questionGroupTestHelper.getCreateQuestionGroupParameters(qG_2, asList(question1, question6, question3, question5),
                "Close Client", "Section1");
        parameters.addExistingQuestion("Section2", question9);
        parameters.addExistingQuestion("Section2", question10);
        parameters.addExistingQuestion("Section2", question8);
        parameters.addExistingQuestion("Section2", question11);
        for (String section : parameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.submit(parameters);
    }
    
    private void verifyQuestionResponsesExistInDatabase(String clientID, String event, Map<String, String> questions) throws SQLException {
        for (String question : questions.keySet()) {
            Assert.assertTrue(applicationDatabaseOperation.deosQuestionResponseForClientExist(clientID, event, question, questions.get(question)));
        }
    }

    private QuestionResponseParameters getQuestionResponseParametersForClientAccountClose(String answer) {
        QuestionResponseParameters responseParams = new QuestionResponseParameters();
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "24/01/2011");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].valuesAsArray", "first");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "10");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[3].value", "10");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[4].value", answer);

        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "good");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "24/01/2011");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[2].valuesAsArray", "feb");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[3].value", answer);

        responseParams.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "24/01/2011");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[0].questions[1].value", "10");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[0].questions[2].value", answer);
        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[3].value", "good");

        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].value", "1");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", answer);
        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[2].valuesAsArray", "jan");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[1].questions[3].value", answer);

        return responseParams;
    }

    private QuestionResponseParameters getQuestionResponseParametersForClientAccountCloseAfterModyfication(String answer) {
        QuestionResponseParameters responseParams = new QuestionResponseParameters();
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", answer);
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[1].value", answer);

        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "24/01/2011");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "10");
        
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[2].questions[0].value", "24/01/2011");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[2].questions[1].valuesAsArray", "jan");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[2].questions[2].value", answer);
        return responseParams;
    }

    private CreateQuestionParameters newFreeTextQuestionParameters(String text) {
        CreateQuestionParameters questionParams = new CreateQuestionParameters();

        questionParams.setText(text);
        questionParams.setType(CreateQuestionParameters.TYPE_FREE_TEXT);

        return questionParams;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void createClientWithCorrectAgeTest() throws Exception {
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientTestHelper.createClient("MyOfficeDHMFT", "11",
                "12", "1987");
        CreateClientEnterMfiDataPage nextPage = clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientMfiInfo");
        propertiesHelper.setMinimumAgeForClients(0);
        propertiesHelper.setMaximumAgeForClients(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void createClientWithMoreThanMaximumAgeTest() throws Exception {
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientTestHelper.createClient("MyOfficeDHMFT", "11",
                "12", "1940");
        CreateClientEnterPersonalDataPage nextPage = clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
        propertiesHelper.setMinimumAgeForClients(0);
        propertiesHelper.setMaximumAgeForClients(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void createClientWithLessThanMinimumAgeTest() throws Exception {
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientTestHelper.createClient("MyOfficeDHMFT", "11",
                "12", "1995");
        CreateClientEnterPersonalDataPage nextPage = clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
        propertiesHelper.setMinimumAgeForClients(0);
        propertiesHelper.setMaximumAgeForClients(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void searchForClientAndAddSurveysTest() throws Exception {

        createQuestionGroup();
        navigateToClientDetailsPage();

        testAttachQuestionGroup(response);
        verifyQuestionGroupInstanceListing(1);
        verifyQuestionGroupResponse(response);

        testEditQuestionGroup(response + 1);
        verifyQuestionGroupInstanceListing(2);
        verifyQuestionGroupResponse(response + 1);

        testShouldEditInactiveQuestion(response + 2);
        verifyQuestionGroupInstanceListing(3);
        verifyQuestionGroupResponse(response + 2);

        testSectionShouldNotAppearInQuestionnaireWhenAllQuestionsAreInactive();
    }

    private void createQuestionGroup() {
        Random random = new Random();
        questionGroupTitle = "QG1" + random.nextInt(100);
        question1 = "FT_" + random.nextInt(100);
        question2 = "MS_" + random.nextInt(100);
        response = "Hello World";

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, "Free Text", null));
        createQuestionPage.addQuestion(getCreateQuestionParams(question2, MULTI_SELECT,
                asList("Choice1", "Choice2", "Choice3", "Choice4")));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        CreateQuestionGroupParameters parameters;
        parameters = questionGroupTestHelper.getCreateQuestionGroupParameters(questionGroupTitle, asList(question1),
                "View Client", "Section1");
        for (String section : parameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        parameters = questionGroupTestHelper.getCreateQuestionGroupParameters(questionGroupTitle, asList(question2),
                "View Client", "Section2");
        for (String section : parameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.submit(parameters);
    }

    private void testSectionShouldNotAppearInQuestionnaireWhenAllQuestionsAreInactive() {
        testActivateQuestion(question1);
        testDeactivateQuestion(question2);
        navigateToClientDetailsPage();
        viewClientDetailsPage.getQuestionnairePage(questionGroupTitle);
        Assert.assertFalse("Section2 should not be present on questionnaire when all questions are inactive",
                selenium.isTextPresent("Section2"));
    }

    private void testShouldEditInactiveQuestion(String response) {
        testDeactivateQuestion(question1);
        navigateToClientDetailsPage();
        testEditQuestionGroup(response);
    }

    private void testDeactivateQuestion(String question) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(question);
        EditQuestionPage editQuestionPage = questionDetailPage.navigateToEditQuestionPage();
        questionDetailPage = editQuestionPage.deactivate();
    }

    private void testActivateQuestion(String question) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(question);
        EditQuestionPage editQuestionPage = questionDetailPage.navigateToEditQuestionPage();
        questionDetailPage = editQuestionPage.activate();
    }

    private void testEditQuestionGroup(String answer) {
        int instanceId = latestInstanceId(questionGroupInstancesOfClient);
        QuestionGroupResponsePage questionGroupResponsePage = viewClientDetailsPage
                .navigateToQuestionGroupResponsePage(instanceId);
        QuestionnairePage questionnairePage = questionGroupResponsePage.navigateToEditResponses();
        verifyCancel(questionnairePage);
        questionGroupResponsePage = viewClientDetailsPage.navigateToQuestionGroupResponsePage(instanceId);
        questionnairePage = questionGroupResponsePage.navigateToEditResponses();
        questionnairePage.setResponse(question1, answer);
        MifosPage mifosPage = questionnairePage.submit();
        Assert.assertTrue(mifosPage instanceof ClientViewDetailsPage);
        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) mifosPage;
        viewClientDetailsPage = clientViewDetailsPage;
    }

    private void verifyQuestionGroupResponse(String response) {
        QuestionGroupResponsePage questionGroupResponsePage = viewClientDetailsPage
                .navigateToQuestionGroupResponsePage(latestInstanceId(questionGroupInstancesOfClient));
        questionGroupResponsePage.verifyPage();
        String msg = response + " not found for question " + question1 + ". Instead found "
                + questionGroupResponsePage.getAnswerHtml(question1);
        Assert.assertTrue(msg, questionGroupResponsePage.getAnswerHtml(question1).contains(response));
        Assert.assertTrue(questionGroupResponsePage.getAnswerHtml(question2).contains("Choice1"));
        Assert.assertTrue(questionGroupResponsePage.getAnswerHtml(question2).contains("Choice3"));
        Assert.assertTrue(questionGroupResponsePage.getAnswerHtml(question2).contains("Choice4"));
        viewClientDetailsPage = questionGroupResponsePage.navigateToViewClientDetailsPage();
    }

    private void verifyQuestionGroupInstanceListing(int expectedSize) {
        questionGroupInstancesOfClient = viewClientDetailsPage.getQuestionGroupInstances();
        QuestionGroup latestInstance = getLatestQuestionGroupInstance();
        Assert.assertEquals(expectedSize, questionGroupInstancesOfClient.size());
        Calendar calendar = Calendar.getInstance();
        String expectedDate = String.format(EXPECTED_DATE_FORMAT, calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        Assert.assertEquals(questionGroupTitle, latestInstance.getName());
        Assert.assertEquals(expectedDate, latestInstance.getDate());
    }

    private QuestionGroup getLatestQuestionGroupInstance() {
        return questionGroupInstancesOfClient.get(latestInstanceId(questionGroupInstancesOfClient));
    }

    private void testAttachQuestionGroup(String response) {
        QuestionnairePage questionnairePage = viewClientDetailsPage.getQuestionnairePage(questionGroupTitle);
        verifyCancel(questionnairePage);
        questionnairePage = checkMandatoryQuestionValidation(questionGroupTitle, question1, question2,
                viewClientDetailsPage);
        questionnairePage.setResponse(question1, response);
        MifosPage mifosPage = questionnairePage.submit();
        Assert.assertTrue(mifosPage instanceof ClientViewDetailsPage);
        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) mifosPage;
        viewClientDetailsPage = clientViewDetailsPage;
    }

    private void navigateToClientDetailsPage() {
        ClientsAndAccountsHomepage clientsPage = navigationHelper.navigateToClientsAndAccountsPage();
        ClientSearchResultsPage searchResultsPage = clientsPage.searchForClient("client1 lastname");
        searchResultsPage.verifyPage();
        viewClientDetailsPage = searchResultsPage.navigateToSearchResult("client1 lastname: ID 0002-000000005");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-681
    @Test(enabled=true)
    public void createClientWithQuestionGroups() throws Exception {
        createQuestions();

        CreateQuestionGroupParameters qg = questionGroupTestHelper.getCreateQuestionGroupParameters("CreateClientQG",
                asList("q1", "q2", "q3"), "Create Client", "Sec 1");
        questionGroupTestHelper.createQuestionGroup(qg);

        CreateQuestionGroupParameters qg2 = questionGroupTestHelper.getCreateQuestionGroupParameters("CreateClientQG2",
                asList("q6"), "Create Client", "Sec 2");
        questionGroupTestHelper.createQuestionGroup(qg2);

        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = createFormParameters();

        QuestionResponseParameters responseParams = new QuestionResponseParameters();
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "yes");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "good");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "qwer");

        // When
        clientTestHelper.createClientWithQuestionGroups(formParameters, "group1", responseParams);

        List<String> questionToAdd = new ArrayList<String>();
        questionToAdd.add("q4");
        questionToAdd.add("q5");

        List<String> questionToDeactivate = new ArrayList<String>();
        questionToDeactivate.add("q6");

        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        for (String question : questionToAdd) {
            createQuestionGroupParameters.addExistingQuestion("Sec 1", question);
        }
        questionGroupTestHelper.addQuestionsToQuestionGroup("CreateClientQG",
                createQuestionGroupParameters.getExistingQuestions());

        for (String question : questionToDeactivate) {
            questionGroupTestHelper.markQuestionAsInactive(question);
        }
        questionGroupTestHelper.markQuestionGroupAsInactive("CreateClientQG2");

        QuestionResponsePage questionResponsePage = clientTestHelper.navigateToQuestionResponsePage(formParameters,
                "group1");
        // Then
        questionResponsePage.verifyQuestionsDoesnotappear(questionToDeactivate.toArray(new String[questionToDeactivate
                .size()]));
        questionResponsePage.verifyQuestionsExists(questionToAdd.toArray(new String[questionToAdd.size()]));
        questionResponsePage.verifySectionDoesnotappear("Sec 2");
        questionGroupTestHelper.markQuestionGroupAsInactive("CreateClientQG");
    }

    private CreateClientEnterPersonalDataPage.SubmitFormParameters createFormParameters() {
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        formParameters.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        formParameters.setFirstName("test");
        formParameters.setLastName("Customer" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("01");
        formParameters.setDateOfBirthMM("02");
        formParameters.setDateOfBirthYYYY("1988");
        formParameters.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.FEMALE);
        formParameters.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.POOR);
        formParameters.setHandicapped("Yes");
        formParameters.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        formParameters.setSpouseFirstName("father");
        formParameters.setSpouseLastName("lastname" + StringUtil.getRandomString(8));
        return formParameters;
    }

    private void createQuestions() {
        List<CreateQuestionParameters> questions = new ArrayList<CreateQuestionParameters>();
        CreateQuestionParameters q1 = new CreateQuestionParameters();
        q1.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q1.setText("q1");
        q1.setChoicesFromStrings(Arrays.asList(new String[]{"yes", "no"}));
        questions.add(q1);
        CreateQuestionParameters q2 = new CreateQuestionParameters();
        q2.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q2.setText("q2");
        q2.setChoicesFromStrings(Arrays.asList(new String[]{"good", "bad", "average"}));
        questions.add(q2);
        CreateQuestionParameters q3 = new CreateQuestionParameters();
        q3.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q3.setText("q3");
        questions.add(q3);
        CreateQuestionParameters q4 = new CreateQuestionParameters();
        q4.setType(CreateQuestionParameters.TYPE_DATE);
        q4.setText("q4");
        questions.add(q4);
        CreateQuestionParameters q5 = new CreateQuestionParameters();
        q5.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q5.setText("q5");
        questions.add(q5);
        CreateQuestionParameters q6 = new CreateQuestionParameters();
        q6.setType(CreateQuestionParameters.TYPE_NUMBER);
        q6.setText("q6");
        q6.setNumericMax(10);
        q6.setNumericMin(0);
        questions.add(q6);
        questionGroupTestHelper.createQuestions(questions);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-35
    @Test(enabled=false) //TODO http://mifosforge.jira.com/browse/MIFOS-5081
    public void addingMemeberToGroupWithDiffrentStatuses() throws Exception {
        String clientName;
        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName("testGroup" + StringUtil.getRandomString(5));
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setNote("change status");

        // When
        ClientViewDetailsPage clientDetailsPage = clientTestHelper.createClientAndVerify("loan officer",
                "MyOfficeDHMFT");
        clientName = clientDetailsPage.getHeading();
        clientTestHelper.changeCustomerStatus(clientDetailsPage, ClientStatus.ACTIVE);
        groupTestHelper.createNewGroupPartialApplication("Default Center", groupParams);
        // Then
        clientTestHelper.addClientToGroupWithErrorGroupLowerStatus(clientName, groupParams.getGroupName());

        // When
        editCustomerStatusParameters.setGroupStatus(GroupStatus.PENDING_APPROVAL);
        groupTestHelper.changeGroupStatus(groupParams.getGroupName(), editCustomerStatusParameters);
        // Then
        clientTestHelper.addClientToGroupWithErrorGroupLowerStatus(clientName, groupParams.getGroupName());

        // When
        editCustomerStatusParameters.setGroupStatus(GroupStatus.ACTIVE);
        groupTestHelper.changeGroupStatus(groupParams.getGroupName(), editCustomerStatusParameters);
        // Then
        clientTestHelper.addClientToGroup(clientName, groupParams.getGroupName());

        // When
        clientTestHelper.deleteClientGroupMembership(clientName, "remove group membership");
        editCustomerStatusParameters.setGroupStatus(GroupStatus.ON_HOLD);
        groupTestHelper.changeGroupStatus(groupParams.getGroupName(), editCustomerStatusParameters);
        // Then
        clientTestHelper.tryAddClientToClosedOrOnHoldGroup(clientName, groupParams.getGroupName());

        // When
        editCustomerStatusParameters.setGroupStatus(GroupStatus.CLOSED);
        editCustomerStatusParameters.setCloseReason(GroupCloseReason.DUPLICATE);
        groupTestHelper.changeGroupStatus(groupParams.getGroupName(), editCustomerStatusParameters);
        // Then
        clientTestHelper.tryAddClientToClosedOrOnHoldGroup(clientName, groupParams.getGroupName());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-40
    @Test(enabled=false) //TODO http://mifosforge.jira.com/browse/MIFOS-5081
    public void addingMemeberOnHoldStatusToGroupWithDiffrentStatuses() throws Exception {
        String groupName = "testGroup";
        String clientName = "test";
        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName(groupName);
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setNote("change status");

        // When
        ClientViewDetailsPage clientDetailsPage = clientTestHelper.createClientAndVerify("loan officer",
                "MyOfficeDHMFT");
        clientTestHelper.changeCustomerStatus(clientDetailsPage, ClientStatus.ACTIVE);
        clientTestHelper.changeCustomerStatus(clientDetailsPage, ClientStatus.ON_HOLD);
        groupTestHelper.createNewGroupPartialApplication("Default Center", groupParams);
        // Then
        clientTestHelper.addClientToGroupWithErrorGroupLowerStatus(clientName, groupName);

        // When
        editCustomerStatusParameters.setGroupStatus(GroupStatus.PENDING_APPROVAL);
        groupTestHelper.changeGroupStatus(groupName, editCustomerStatusParameters);
        // Then
        clientTestHelper.addClientToGroupWithErrorGroupLowerStatus(clientName, groupName);

        // When
        editCustomerStatusParameters.setGroupStatus(GroupStatus.ACTIVE);
        groupTestHelper.changeGroupStatus(groupName, editCustomerStatusParameters);
        // Then
        clientTestHelper.addClientToGroup(clientName, groupName);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-51
    @Test(enabled=true)
    public void tryRemoveClientWithLoanFromGroup() throws Exception {
        String clientName = "ClientWithLoan 20110221";

        // When / Then
        clientTestHelper.deleteClientGroupMembershipWithError(clientName);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-50
    @Test(enabled=true)
    public void tryRemoveClientWithLoanFromGroupWithLoan() throws Exception {
        String clientName = "client1 lastname";

        // When / Then
        clientTestHelper.deleteClientGroupMembershipWithError(clientName);
    }

    /**
     * Verify that sequence of client names in the properties file is used for displaying the order of client names in
     * the UI http://mifosforge.jira.com/browse/MIFOSTEST-205
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void verifySequenceOfClientNamesInPropertiesFile() throws Exception {
        String groupName = "group";
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        clientParams.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        clientParams.setFirstName("firstName");
        clientParams.setMiddleName("middleName");
        clientParams.setLastName("lastName");
        clientParams.setSecondLastName("secondLastName");
        clientParams.setDateOfBirthDD("22");
        clientParams.setDateOfBirthMM("05");
        clientParams.setDateOfBirthYYYY("1987");
        clientParams.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.FEMALE);
        clientParams.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.POOR);
        clientParams.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        clientParams.setSpouseFirstName("fatherName");
        clientParams.setSpouseLastName("fatherLastName");

        ClientViewDetailsPage clientViewDetailsPage = clientTestHelper.createNewClient(groupName, clientParams);
        clientViewDetailsPage.verifyHeading("firstName middleName lastName secondLastName");

        propertiesHelper.setClientsNameSequence("last_name,second_last_name,middle_name,first_name");
        clientParams.setFirstName("firstName2");
        clientViewDetailsPage = clientTestHelper.createNewClient(groupName, clientParams);
        clientViewDetailsPage.verifyHeading("lastName secondLastName middleName firstName2");

        propertiesHelper.setClientsNameSequence("first_name,middle_name,last_name,second_last_name");
        clientParams.setFirstName("firstName3");
        clientViewDetailsPage = clientTestHelper.createNewClient(groupName, clientParams);
        clientViewDetailsPage.verifyHeading("firstName3 middleName lastName secondLastName");
    }

    /**
     * Verify when Pending Approval (Clients) is set to false; the system transitions the account to 'Active state' when
     * creating new clients http://mifosforge.jira.com/browse/MIFOSTEST-209
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void verifyClientCreatedWithActiveStatus() throws Exception {
        applicationDatabaseOperation.updateCustomerState("2", "0");
        propertiesHelper.setClientPendingApprovalStateEnabled("false");

        String officeName = "MyOfficeDHMFT";
        String loanOfficer = "loan officer";

        ClientViewDetailsPage clientViewDetailsPage = clientTestHelper.createClientAndVerify(loanOfficer, officeName);
        clientViewDetailsPage.verifyStatus("Active");

        // restore original settings
        applicationDatabaseOperation.updateCustomerState("2", "1");
        propertiesHelper.setClientPendingApprovalStateEnabled("true");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-48
    @Test(enabled=false)  //blocked by http://mifosforge.jira.com/browse/MIFOS-4272 - ldomzalski
    public void removeClientWithLoanFromGroup() throws Exception {
        // Given
        String clientName = "client1 lastname";
        String groupName = navigationHelper.navigateToClientViewDetailsPage(clientName).getGroupMembership();
        SavingsProductParameters params = savingsProductHelper.
                getGenericSavingsProductParameters(new DateTime(2009, 7, 13, 12, 0, 0, 0),
                        SavingsProductParameters.MANDATORY,SavingsProductParameters.GROUPS);
        savingsProductHelper.createSavingsProduct(params);
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString(groupName);
        searchParameters.setSavingsProduct(params.getProductInstanceName());

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("250.0");

        String savingsId = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters).getAccountId();
        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        // When / Then
        clientTestHelper.deleteClientGroupMembership(clientName, "remove group membership");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-52
    @Test(enabled=false)  //blocked by http://mifosforge.jira.com/browse/MIFOS-4272 - ldomzalski
    public void removeClientWithSavingsFromGroupWithSavingsCheckGroupCalculation() throws Exception {
        String clientName = "client1 lastname";
        String groupName = navigationHelper.navigateToClientViewDetailsPage(clientName).getGroupMembership();
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("240.0");
        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");

        // When
        searchParameters.setSearchString(clientName);
        searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");
        String savingsId = savingsAccountHelper.createSavingsAccountWithQG(searchParameters, submitAccountParameters)
                .getAccountId();
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        searchParameters.setSearchString(groupName);
        searchParameters.setSavingsProduct("MandGroupSavingsPerIndiv1MoPost");
        savingsId = savingsAccountHelper.createSavingsAccountWithQG(searchParameters, submitAccountParameters)
                .getAccountId();
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        clientTestHelper.deleteClientGroupMembership(clientName, "remove group membership");
        Integer numberOfGroupMembers = Integer.parseInt(navigationHelper.navigateToGroupViewDetailsPage(groupName)
                .getNumberOfClientsInGroup());

        // Then
        savingsAccountHelper.verifyTotalAmountDue(savingsId, numberOfGroupMembers,
                Float.parseFloat(submitAccountParameters.getAmount()));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-45
    @Test(enabled=false) //blocked by http://mifosforge.jira.com/browse/MIFOS-4272 - ldomzalski
    public void addClientWithSavingToGroupWithSavingsCheckGroupCalculation() throws Exception {

        String groupName = "group1";

        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("240.0");
        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");

        // When
        ClientViewDetailsPage clientDetailsPage = clientTestHelper.createClientAndVerify("loan officer",
                "MyOfficeDHMFT");
        clientTestHelper.changeCustomerStatus(clientDetailsPage, ClientStatus.ACTIVE);
        String clientName = clientDetailsPage.getHeading();

        searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");
        searchParameters.setSearchString(clientName);
        
        String savingsId = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters).getAccountId();
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        SavingsProductParameters savingsProductParameters = savingsProductHelper.
                getGenericSavingsProductParameters(new DateTime(2009, 7, 13, 12, 0, 0, 0),
                        SavingsProductParameters.MANDATORY,SavingsProductParameters.GROUPS);
        savingsProductParameters.setShortName("M-45");
        savingsProductParameters.setAmountAppliesTo(SavingsProductParameters.PER_INDIVIDUAL);
        savingsProductHelper.createSavingsProduct(savingsProductParameters);
        
        searchParameters.setSavingsProduct(savingsProductParameters.getProductInstanceName());
        searchParameters.setSearchString(groupName);
        savingsId = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters).getAccountId();
        savingsAccountHelper.changeStatus(savingsId, editAccountStatusParameters);

        clientTestHelper.addClientToGroup(clientName, groupName);

        // Then
        Integer numberOfGroupMembers = Integer.parseInt(navigationHelper.navigateToGroupViewDetailsPage(groupName)
                .getNumberOfClientsInGroup());
        savingsAccountHelper.verifyTotalAmountDue(savingsId, numberOfGroupMembers,
                Float.parseFloat(submitAccountParameters.getAmount()));
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-43
    @Test(enabled=true)
    public void addingMemeberPendingApprovalStatusToGroupWithActiveStatus() throws Exception {
        String groupName = "group1";

        // When
        String clientName = clientTestHelper.createClientAndVerify("loan officer", "MyOfficeDHMFT").getHeading();

        // Then
        clientTestHelper.addClientToGroup(clientName, groupName);
    }

    private QuestionnairePage checkMandatoryQuestionValidation(String questionGroupTitle, String question1,
                                                               String question2, ClientViewDetailsPage viewDetailsPage) {
        QuestionnairePage questionnairePage = viewDetailsPage.getQuestionnairePage(questionGroupTitle);
        questionnairePage.setResponsesForMultiSelect(question2, 4, "Choice1", "Choice3", "Choice4");
        MifosPage mifosPage = questionnairePage.submit();
        Assert.assertTrue(mifosPage instanceof QuestionnairePage);
        questionnairePage = (QuestionnairePage) mifosPage;
        Assert.assertTrue(questionnairePage.isErrorPresent("Please specify " + question1));
        return questionnairePage;
    }

    private ClientViewDetailsPage verifyCancel(QuestionnairePage questionnairePage) {
        return questionnairePage.cancel();
    }

    public Integer latestInstanceId(Map<Integer, QuestionGroup> questionGroups) {
        Set<Integer> keys = questionGroups.keySet();
        return Collections.max(keys);
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, List<String> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setText(title);
        parameters.setType(type);
        parameters.setChoicesFromStrings(choices);
        return parameters;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-305
    @Test(enabled=true)
    public void createClientWithSaveForLaterAndChangeStatusTest() throws Exception {
        ClientViewDetailsPage clientDetailsPage = clientTestHelper.createClientAndVerify("loan officer",
                "MyOfficeDHMFT");

        // When
        String groupName = "group1";
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        clientParams = clientParams();

        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientsAndAccountsPage()
                .navigateToCreateNewClientPage().selectGroup(groupName).create(clientParams)
                .submitAndGotoCreateClientEnterMfiDataPage().navigateToPreview().saveForLater()
                .navigateToClientViewDetailsPage();
        clientViewDetailsPage.verifyStatus(ClientTestHelper.PARTIAL_APPLICATION);
        // Then
        clientTestHelper.changeCustomerStatus(clientDetailsPage, ClientStatus.PENDING_APPROVAL);
    }

    private CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams() {
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        clientParams.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        clientParams.setFirstName("John");
        clientParams.setLastName("Doe");
        clientParams.setDateOfBirthDD("22");
        clientParams.setDateOfBirthMM("05");
        clientParams.setDateOfBirthYYYY("1987");
        clientParams.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.MALE);
        clientParams.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.NOT_POOR);
        clientParams.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        clientParams.setSpouseFirstName("fatherName");
        clientParams.setSpouseLastName("fatherLastName");
        return clientParams;
    }
}
