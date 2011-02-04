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

package org.mifos.test.acceptance.client;

import org.junit.Assert;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPage;
import org.mifos.test.acceptance.framework.client.ClientEditMFIParameters;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPreviewPage;
import org.mifos.test.acceptance.framework.client.ClientEditPersonalInfoPage;
import org.mifos.test.acceptance.framework.client.ClientNotesPage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientConfirmationPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientPreviewDataPage;
import org.mifos.test.acceptance.framework.client.QuestionGroup;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPreviewDataPage;
import org.mifos.test.acceptance.framework.questionnaire.Choice;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionsPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static java.util.Arrays.asList;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"client", "acceptance", "ui", "smoke"})
public class ClientTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    CustomPropertiesHelper propertiesHelper;
    ClientTestHelper clientTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private Random random;
    private static final String FREE_TEXT = "Free Text";
    public static final String MULTI_SELECT = "Multi Select";
    public static final String EXPECTED_DATE_FORMAT = "%02d/%02d/%04d";
    public static final String NUMBER = "Number";
    public static final String SMART_SELECT = "Smart Select";
    private String questionGroupTitle;
    private String question1;
    private String question2;
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
        clientTestHelper = new ClientTestHelper();
        random = new Random();
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-208
    public void createClientAndChangeStatusTest() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        ClientViewDetailsPage clientDetailsPage = clientsAndAccountsPage.createClientAndVerify("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227");

        //When / Then
        clientTestHelper.changeCustomerStatus(clientDetailsPage);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-310
    public void searchClientAndEditExistingClientDetails() throws Exception{
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        HomePage homePage = navigationHelper.navigateToHomePage();
        homePage = searchForClient("Stu123",homePage,38);
        homePage = searchForClient("zzz",homePage, 0);

        SearchResultsPage searchResultsPage = homePage.search("Stu1232993852651 Client1232993852651");
        searchResultsPage.verifyPage();
        int numResults = searchResultsPage.countSearchResults();
        Assert.assertEquals( numResults, 1 );

        ClientViewDetailsPage viewDetailsPage = searchResultsPage.navigateToClientViewDetailsPage("link=Stu1232993852651 Client1232993852651*");
        ClientNotesPage notesPage = viewDetailsPage.navigateToNotesPage();
        notesPage.addNotePreviewAndSubmit("test note");
        viewDetailsPage.verifyNotes("test note");

        CustomerChangeStatusPage changeStatusPage = viewDetailsPage.navigateToCustomerChangeStatusPage();
        SubmitFormParameters parameters = new SubmitFormParameters();
        parameters.setStatus(SubmitFormParameters.ON_HOLD);
        parameters.setNotes("test");
        CustomerChangeStatusPreviewDataPage changeStatusPreviewDataPage = changeStatusPage.submitAndGotoCustomerChangeStatusPreviewDataPage(parameters);
        viewDetailsPage = changeStatusPreviewDataPage.submitAndGotoClientViewDetailsPage();
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
    public void createClientOutsideGroup() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
        //When
        CreateClientEnterMfiDataPage clientEnterMfiDataPage = navigationHelper.navigateToCreateClientEnterMfiDataPage("MyOffice1232993831593");

        CreateClientEnterMfiDataPage.SubmitFormParameters parameters = new CreateClientEnterMfiDataPage.SubmitFormParameters();
        parameters.setLoanOfficerId("Joe1232993835093 Guy1232993835093");

        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("testMeetingPlace");
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.MONDAY);
        parameters.setMeeting(meeting);

        CreateClientPreviewDataPage createClientPreviewDataPage = clientEnterMfiDataPage.submitAndGotoCreateClientPreviewDataPage(parameters);
        CreateClientConfirmationPage clientConfirmationPage = createClientPreviewDataPage.submit();
        //Then
        clientConfirmationPage.navigateToClientViewDetailsPage();
    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private HomePage searchForClient(String clientName, HomePage homePage, int expectedNumberOfClients) throws Exception {
        SearchResultsPage searchResultsPage = homePage.search(clientName);
        searchResultsPage.verifyPage();
        int numResults = searchResultsPage.countSearchResults();
        Assert.assertEquals( numResults, expectedNumberOfClients );

        selenium.click("clientsAndAccountsHeader.link.home");
        selenium.waitForPageToLoad("30000");

        return new HomePage(selenium);
     }

    // implementation of test described in issue 2454
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchForClientAndEditDetailsTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml",
                dataSource, selenium);

        ClientsAndAccountsHomepage clientsPage = navigationHelper.navigateToClientsAndAccountsPage();
        ClientSearchResultsPage searchResultsPage = clientsPage.searchForClient("Stu1232993852651");
        searchResultsPage.verifyPage();
        ClientViewDetailsPage clientDetailsPage = searchResultsPage.navigateToSearchResult("Stu1232993852651 Client1232993852651: ID 0002-000000003");

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

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(groups = {"smoke"})
    public void createClientWithCorrectAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientsAndAccountsPage.createClient("MyOffice1233171674227", "11", "12", "1987");
        CreateClientEnterMfiDataPage nextPage = clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientMfiInfo");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientWithMoreThanMaximumAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientsAndAccountsPage.createClient("MyOffice1233171674227", "11", "12", "1940");
        CreateClientEnterPersonalDataPage nextPage = clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientWithLessThanMinimumAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientsAndAccountsPage.createClient("MyOffice1233171674227", "11", "12", "1995");
        CreateClientEnterPersonalDataPage nextPage = clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchForClientAndAddSurveysTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        createQuestionGroup();
        navigateToClientDetailsPage();

        testAttachQuestionGroup(response);
        verifyQuestionGroupInstanceListing(1);
        verifyQuestionGroupResponse(response);

        testEditQuestionGroup(response + 1);
        verifyQuestionGroupInstanceListing(2);
        verifyQuestionGroupResponse(response +1);

        testShouldEditInactiveQuestion(response + 2);
        verifyQuestionGroupInstanceListing(3);
        verifyQuestionGroupResponse(response +2);

        testSectionShouldNotAppearInQuestionnaireWhenAllQuestionsAreInactive();
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
        QuestionGroupResponsePage questionGroupResponsePage = viewClientDetailsPage.navigateToQuestionGroupResponsePage(instanceId);
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
        QuestionGroupResponsePage questionGroupResponsePage = viewClientDetailsPage.navigateToQuestionGroupResponsePage(latestInstanceId(questionGroupInstancesOfClient));
        questionGroupResponsePage.verifyPage();
        String msg = response + " not found for question " + question1 + ". Instead found " + questionGroupResponsePage.getAnswerHtml(question1);
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
        String expectedDate = String.format(EXPECTED_DATE_FORMAT, calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        Assert.assertEquals(questionGroupTitle, latestInstance.getName());
        Assert.assertEquals(expectedDate, latestInstance.getDate());
    }

    private QuestionGroup getLatestQuestionGroupInstance() {
        return questionGroupInstancesOfClient.get(latestInstanceId(questionGroupInstancesOfClient));
    }

    private void testAttachQuestionGroup(String response) {
        QuestionnairePage questionnairePage = viewClientDetailsPage.getQuestionnairePage(questionGroupTitle);
        verifyCancel(questionnairePage);
        questionnairePage = checkMandatoryQuestionValidation(questionGroupTitle, question1, question2, viewClientDetailsPage);
        questionnairePage.setResponse(question1, response);
        MifosPage mifosPage = questionnairePage.submit();
        Assert.assertTrue(mifosPage instanceof ClientViewDetailsPage);
        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) mifosPage;
        viewClientDetailsPage = clientViewDetailsPage;
    }

    private void navigateToClientDetailsPage() {
        ClientsAndAccountsHomepage clientsPage = navigationHelper.navigateToClientsAndAccountsPage();
        ClientSearchResultsPage searchResultsPage = clientsPage.searchForClient("Stu1232993852651");
        searchResultsPage.verifyPage();
        viewClientDetailsPage = searchResultsPage.navigateToSearchResult("Stu1232993852651 Client1232993852651: ID 0002-000000003");
    }

    private void createQuestionGroup() {
        questionGroupTitle = "QG1" + random.nextInt(100);
        question1 = "FT_" + random.nextInt(100);
        question2 = "MS_" + random.nextInt(100);
        response = "Hello World";

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, FREE_TEXT, null));
        createQuestionPage.addQuestion(getCreateQuestionParams(question2, MULTI_SELECT, asList("Choice1", "Choice2", "Choice3", "Choice4")));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        CreateQuestionGroupParameters parameters;
        parameters = getCreateQuestionGroupParameters(questionGroupTitle, asList(question1), "View Client", "Section1");
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        parameters = getCreateQuestionGroupParameters(questionGroupTitle, asList(question2), "View Client", "Section2");
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.submit(parameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientWithQuestionGroups() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "NU_" + random.nextInt(100);
        String question2 = "SS_" + random.nextInt(100);
        String answer = "30";
        List<Choice> choices = asList(new Choice("Choice1", asList("Tag1", "Tag2")), new Choice("Choice2", asList("Tag3", "Tag4")));

        createQuestionGroupForCreateClient(questionGroupTitle, question1, question2, choices);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();

        ClientViewDetailsPage clientDetailsPage = clientsAndAccountsPage.createClientWithQuestionGroups("Joe1233171679953 Guy1233171679953",
                                                "MyOffice1233171674227", getChoiceTags(), answer);
        ViewQuestionResponseDetailPage responseDetailPage = clientDetailsPage.navigateToViewAdditionalInformationPage();
        responseDetailPage.verifyQuestionPresent(question1, answer);
        responseDetailPage.verifyQuestionPresent(question2, "Choice1", "Choice2");
        responseDetailPage.navigateToDetailsPage();
    }

    private Map<String, String> getChoiceTags() {
        Map<String,String> tags = new HashMap<String, String>();
        tags.put("Tag1", "Choice1");
        tags.put("Tag3", "Choice2");
        return tags;
    }

    private QuestionnairePage checkMandatoryQuestionValidation(String questionGroupTitle, String question1, String question2, ClientViewDetailsPage viewDetailsPage) {
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

    private void createQuestionGroupForCreateClient(String qgTitle, String q1, String q2, List<Choice> choiceTags) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage cqPage = adminPage.navigateToCreateQuestionPage();
        cqPage.addQuestion(getCreateQuestionParams(q1, NUMBER, 10, 100, null));
        cqPage.addQuestion(getCreateQuestionParams(q2, SMART_SELECT, null, null, choiceTags));
        adminPage = cqPage.submitQuestions();

        CreateQuestionGroupPage cqGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        CreateQuestionGroupParameters parameters = getCreateQuestionGroupParameters(qgTitle, asList(q1, q2), "Create Client", "Section1");
        for(String section : parameters.getExistingQuestions().keySet()){
            cqGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        cqGroupPage.markEveryOtherQuestionsMandatory(asList(q1));
        cqGroupPage.submit(parameters);
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters(String questionGroupTitle, List<String> questions, String appliesTo, String sectionName) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(questionGroupTitle);
        parameters.setAppliesTo(appliesTo);
        parameters.setAnswerEditable(true);
        for (String question : questions) {
            parameters.addExistingQuestion(sectionName, question);
        }
        return parameters;
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, List<String> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setText(title);
        parameters.setType(type);
        parameters.setChoicesFromStrings(choices);
        return parameters;
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, Integer numericMin, Integer numericMax, List<Choice> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setText(title);
        parameters.setType(type);
        parameters.setChoices(choices);
        parameters.setNumericMin(numericMin);
        parameters.setNumericMax(numericMax);
        return parameters;
    }

}
