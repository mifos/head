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
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPage;
import org.mifos.test.acceptance.framework.client.ClientEditMFIParameters;
import org.mifos.test.acceptance.framework.client.ClientEditMFIPreviewPage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.client.QuestionGroup;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.questionnaire.QuestionGroupResponsePage;
import org.mifos.test.acceptance.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static java.util.Arrays.asList;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"client", "acceptance", "ui", "smoke"})
public class ClientTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    CustomPropertiesHelper propertiesHelper;

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

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
        random = new Random();
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientAndChangeStatusTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);

        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();

        ClientViewDetailsPage clientDetailsPage = clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227");

        clientsAndAccountsPage.changeCustomerStatus(clientDetailsPage);
    }

    // implementation of test described in issue 2454
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchForClientAndEditDetailsTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
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
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227", "11", "12", "1987");
        CreateClientEnterMfiDataPage nextPage = clientPersonalDataPage.submitAndGotoCreateClientEnterMfiDataPage();
        nextPage.verifyPage("CreateClientMfiInfo");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientWithMoreThanMaximumAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227", "11", "12", "1940");
        CreateClientEnterPersonalDataPage nextPage = clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createClientWithLessThanMinimumAgeTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities,
                "acceptance_small_003_dbunit.xml.zip",
                dataSource, selenium);
        propertiesHelper.setMinimumAgeForClients(18);
        propertiesHelper.setMaximumAgeForClients(60);
        ClientsAndAccountsHomepage clientsAndAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateClientEnterPersonalDataPage clientPersonalDataPage = clientsAndAccountsPage.createClient("Joe1233171679953 Guy1233171679953", "MyOffice1233171674227", "11", "12", "1995");
        CreateClientEnterPersonalDataPage nextPage = clientPersonalDataPage.dontLoadNext();
        nextPage.verifyPage("CreateClientPersonalInfo");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void searchForClientAndAddSurveysTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "FT_" + random.nextInt(100);
        String question2 = "MS_" + random.nextInt(100);
        String answer = "Hello World";

        createQuestionGroupForViewClient(questionGroupTitle, question1, question2);
        ClientViewDetailsPage viewDetailsPage = getClientViewDetailsPage("Stu1232993852651", "Stu1232993852651 Client1232993852651: ID 0002-000000003");
        viewDetailsPage = testAttachSurvey(questionGroupTitle, question1, question2, answer, viewDetailsPage);

        Map<Integer, QuestionGroup> questionGroups = viewDetailsPage.getQuestionGroupInstances();
        Integer latestInstanceId = latestInstanceId(questionGroups);
        QuestionGroup latestInstance = questionGroups.get(latestInstanceId);
        verifyLatestInstanceDetails(latestInstance, questionGroupTitle, 1, questionGroups);
        viewDetailsPage = verifyLatestInstanceResponses(latestInstanceId, viewDetailsPage, question1, question2, answer);

        viewDetailsPage = editViewSurvey(question1, answer + 1, viewDetailsPage, latestInstanceId);

        questionGroups = viewDetailsPage.getQuestionGroupInstances();
        latestInstanceId = latestInstanceId(questionGroups);
        latestInstance = questionGroups.get(latestInstanceId);
        verifyLatestInstanceDetails(latestInstance, questionGroupTitle, 2, questionGroups);
        verifyLatestInstanceResponses(latestInstanceId, viewDetailsPage, question1, question2, answer + 1);
    }

    private ClientViewDetailsPage testAttachSurvey(String questionGroupTitle, String question1, String question2, String answer1, ClientViewDetailsPage viewDetailsPage) {
        QuestionnairePage questionnairePage = viewDetailsPage.getQuestionnairePage(questionGroupTitle);
        verifyCancel(questionnairePage);
        questionnairePage = checkMandatoryQuestionValidation(questionGroupTitle, question1, question2, viewDetailsPage);
        questionnairePage.setResponse(question1, answer1);
        MifosPage mifosPage = questionnairePage.submit();
        Assert.assertTrue(mifosPage instanceof ClientViewDetailsPage);
        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) mifosPage;
        clientViewDetailsPage.verifyPage();
        return clientViewDetailsPage;
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    private ClientViewDetailsPage verifyLatestInstanceResponses(Integer latestInstanceId, ClientViewDetailsPage viewDetailsPage, String question1,
                                                                String question2, String expectedAnswer) {
        QuestionGroupResponsePage questionGroupResponsePage = viewDetailsPage.navigateToQuestionGroupResponsePage(latestInstanceId);
        questionGroupResponsePage.verifyPage();
        String msg = expectedAnswer + " not found for question " + question1 + ". Instead found " + questionGroupResponsePage.getAnswerHtml(question1);
        Assert.assertTrue(msg, questionGroupResponsePage.getAnswerHtml(question1).contains(expectedAnswer));
        Assert.assertTrue(questionGroupResponsePage.getAnswerHtml(question2).contains("Choice1, Choice3, Choice4"));
        viewDetailsPage = questionGroupResponsePage.navigateToViewClientDetailsPage();
        viewDetailsPage.verifyPage();
        return viewDetailsPage;
    }

    private void verifyLatestInstanceDetails(QuestionGroup latestInstance, String questionGroupTitle, int expectedSize, Map<Integer, QuestionGroup> questionGroups) {
        Assert.assertEquals(expectedSize, questionGroups.size());
        Calendar calendar = Calendar.getInstance();
        String expectedDate = String.format(EXPECTED_DATE_FORMAT, calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        Assert.assertEquals(questionGroupTitle, latestInstance.getName());
        Assert.assertEquals(expectedDate, latestInstance.getDate());
    }

    private ClientViewDetailsPage editViewSurvey(String question1, String answer1, ClientViewDetailsPage viewDetailsPage, int instanceId) {
        QuestionGroupResponsePage questionGroupResponsePage = viewDetailsPage.navigateToQuestionGroupResponsePage(instanceId);
        QuestionnairePage questionnairePage = questionGroupResponsePage.navigateToEditResponses();
        verifyCancel(questionnairePage);
        questionGroupResponsePage = viewDetailsPage.navigateToQuestionGroupResponsePage(instanceId);
        questionnairePage = questionGroupResponsePage.navigateToEditResponses();
        questionnairePage.setResponse(question1, answer1);
        MifosPage mifosPage = questionnairePage.submit();
        Assert.assertTrue(mifosPage instanceof ClientViewDetailsPage);
        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) mifosPage;
        clientViewDetailsPage.verifyPage();
        return clientViewDetailsPage;
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
        ClientViewDetailsPage viewDetailsPage = questionnairePage.cancel();
        viewDetailsPage.verifyPage();
        return viewDetailsPage;
    }

    private ClientViewDetailsPage getClientViewDetailsPage(String searchName, String clientName) {
        ClientsAndAccountsHomepage clientsPage = navigationHelper.navigateToClientsAndAccountsPage();
        ClientSearchResultsPage searchResultsPage = clientsPage.searchForClient(searchName);
        searchResultsPage.verifyPage();
        return searchResultsPage.navigateToSearchResult(clientName);
    }

    public Integer latestInstanceId(Map<Integer, QuestionGroup> questionGroups) {
        Set<Integer> keys = questionGroups.keySet();
        return Collections.max(keys);
    }

    private void createQuestionGroupForViewClient(String questionGroupTitle, String question1, String question2) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage().verifyPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, FREE_TEXT, null));
        createQuestionPage.addQuestion(getCreateQuestionParams(question2, MULTI_SELECT, asList("Choice1", "Choice2", "Choice3", "Choice4")));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage().verifyPage();
        CreateQuestionGroupParameters parameters = getCreateQuestionGroupParameters(questionGroupTitle, question1, question2);
        createQuestionGroupPage.addSection(parameters);
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        createQuestionGroupPage.submit(parameters);
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters(String questionGroupTitle, String question1, String question2) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(questionGroupTitle);
        parameters.setAppliesTo("View Client");
        parameters.setAnswerEditable(true);
        parameters.setSectionName("Default Section");
        parameters.setQuestions(asList(question1, question2));
        return parameters;
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, List<String> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setTitle(title);
        parameters.setType(type);
        parameters.setChoicesFromStrings(choices);
        return parameters;
    }

}