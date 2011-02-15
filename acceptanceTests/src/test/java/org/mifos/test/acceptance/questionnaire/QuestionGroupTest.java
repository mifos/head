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
package org.mifos.test.acceptance.questionnaire;

import org.mifos.test.acceptance.framework.client.ClientViewChangeLogPage;
import java.util.Arrays;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionGroupsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"client", "acceptance", "ui"})
public class QuestionGroupTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private QuestionGroupTestHelper questionGroupTestHelper;
    private ClientTestHelper clientTestHelper;
    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml";
    private String qgTitle1, qgTitle2, qgTitle3;
    private String qTitle1, qTitle2, qTitle3, qTitle4, qTitle5;
    private static final String TITLE_MISSING = "Please specify Question Group title.";
    private static final String APPLIES_TO_MISSING = "Please choose a valid 'Applies To' value.";
    private static final String SECTION_MISSING = "Please add at least one section.";
    private static final String QUESTION_MISSING = "Section should have at least one question.";
    public static final String APPLIES_TO_CREATE_CLIENT = "Create Client";
    public static final String SECTION_DEFAULT = "Default";
    private static final String SECTION_MISC = "Misc";
    private static final String VIEW_CLIENT_QUESTION_GROUP = "ViewClientQG";
    private static final String CLIENT = "Stu1232993852651 Client1232993852651";
    private static final List<String> EMPTY_LIST = new ArrayList<String>();
    private static final List<String> QUESTIONS_LIST = new ArrayList<String>(Arrays.asList("CreateClientQG",
        "CreateClientQG2", "ViewClientQG", "ViewClientQG2", "Survey 1", "CloseClientQG", "CloseClientQG2",
        "CreateGroupQG", "CreateGroupQG2", "ViewGroupQG", "ViewGroupQG2", "CreateCenterQG",
        "CreateCenterQG2", "ViewCenterQG", "ViewCenterQG2", "CreateSavingsQG",
        "CreateSavingsQG2","ViewSavingsQG", "ViewSavingsQG2", "CreateLoanQG",
        "CreateLoanQG2", "ViewLoanQG", "ViewLoanQG2", "DisburseLoanQG", "DisburseLoanQG2"));
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        qgTitle1 = "QuestionGroup1 " + System.currentTimeMillis();
        qgTitle2 = "QuestionGroup2 " + System.currentTimeMillis();
        qgTitle3 = "QuestionGroup3 " + System.currentTimeMillis();
        qTitle1 = "Question1 " + System.currentTimeMillis();
        qTitle2 = "Question2 " + System.currentTimeMillis();
        qTitle3 = "Question3 " + System.currentTimeMillis();
        qTitle4 = "Question4 " + System.currentTimeMillis();
        qTitle5 = "Question5 " + System.currentTimeMillis();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createQuestionGroup() throws Exception{
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        AdminPage adminPage = createQuestions(qTitle1, qTitle2, qTitle3);
        CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
        testMissingMandatoryInputs(createQuestionGroupPage);
        testCreateQuestionGroup(createQuestionGroupPage, qgTitle1, APPLIES_TO_CREATE_CLIENT, true, SECTION_DEFAULT, asList(qTitle1, qTitle2), asList(qTitle3), qTitle4);
        testShouldAllowDuplicateTitlesForQuestionGroup();
        testCancelCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)));
        testViewQuestionGroups();
    }
    
    /**
     * Verify that user is able to edit the defined Question Groups and
     * change the order of questions and sections
     * http://mifosforge.jira.com/browse/MIFOSTEST-666
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void editQuestionGroups() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);

        //When
        ViewAllQuestionGroupsPage viewAllQuestionGroupsPage = questionGroupTestHelper.navigateToViewQuestionGroups(QUESTIONS_LIST);
        EditQuestionGroupPage editQuestionGroupPage = viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage(VIEW_CLIENT_QUESTION_GROUP).navigateToEditPage();
        editQuestionGroupPage.moveSectionUp("Sec 2");
        editQuestionGroupPage.moveQuestionUp("Sec 1", 15);
        editQuestionGroupPage.moveQuestionDown("Sec 1", 16);
        QuestionGroupDetailPage questionGroupDetailPage = editQuestionGroupPage.editQuestionGroup(false, "ViewClientQuestionGroupName", "Create Client", Arrays.asList("2","5","6"));

        //Then
        questionGroupDetailPage.verifyOrderQuestions(asList("MultiSelect","Date","Number","Text","SmartSelect"), 6);
        questionGroupDetailPage.verifyOrderSections(asList("Sec 2","Sec 1"));
        questionGroupDetailPage.verifyMandatoryQuestions(EMPTY_LIST, "Sec 1");
        questionGroupDetailPage.verifyMandatoryQuestions(EMPTY_LIST, "Sec 2");
        questionGroupDetailPage.verifyTitle("ViewClientQuestionGroupName");

        //When
        viewAllQuestionGroupsPage = questionGroupDetailPage.navigateToViewQuestionGroupsPage();

        //Then
        viewAllQuestionGroupsPage.verifyInactiveQuestions(0,0);

    }

    /**
     * Verifying that Change Log for Question Groups has an appropriate format
     * http://mifosforge.jira.com/browse/MIFOSTEST-667
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void testChangeLog() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        ClientViewDetailsPage clientViewDetailsPage = clientTestHelper.navigateToClientViewDetailsPage(CLIENT);

        //When
        Map<String,Integer> questions = new HashMap<String, Integer>();
        questions.put("question 4:yes",0);
        clientViewDetailsPage = clientTestHelper.editQuestionGroupResponses(
                clientViewDetailsPage, "0",
                questions);

        questions = new HashMap<String, Integer>();
        questions.put("Number:123",1);
        questions.put("NumberQuestion2:9",1);
        clientViewDetailsPage = clientTestHelper.editQuestionGroupResponses(
                clientViewDetailsPage, "0",
                questions);
        
        //Then
        ClientViewChangeLogPage clientViewChangeLogPage = clientViewDetailsPage.navigateToClientViewChangeLog();
        clientViewChangeLogPage.verifyChangeLog(asList("CreateClientQG/Sec 2/Number","CreateClientQG/Sec 2/NumberQuestion2"),
                asList("-","-"), asList("123","9"), asList("mifos","mifos"),3);
    }

    private void testViewQuestionGroups() {
        ViewAllQuestionGroupsPage viewQuestionGroupsPage = getViewQuestionGroupsPage(new AdminPage(selenium));
        testViewQuestionGroups(viewQuestionGroupsPage);
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle1, SECTION_DEFAULT, asList(qTitle1, qTitle2), asList(qTitle1));
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle1, SECTION_MISC, asList(qTitle4), EMPTY_LIST);
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle2, SECTION_MISC, asList(qTitle1, qTitle3), asList(qTitle1));
        testEditQuestionGroupDetail(viewQuestionGroupsPage.navigateToQuestionGroupDetailPage(qgTitle2));
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle3, SECTION_MISC, asList(qTitle1, qTitle3), asList(qTitle1));
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle3, "New Section", asList(qTitle4), EMPTY_LIST);
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle3, "Hello World", asList(qTitle5), EMPTY_LIST);
    }

    private void testEditQuestionGroupDetail(QuestionGroupDetailPage questionGroupDetailPage) {
        EditQuestionGroupPage editQuestionGroupPage = questionGroupDetailPage.navigateToEditPage();
        editQuestionGroupPage.setTitle(qgTitle3);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.addExistingQuestion("New Section", qTitle4);
        for(String section : createQuestionGroupParameters.getExistingQuestions().keySet()){
            editQuestionGroupPage.addExistingQuestion(section, createQuestionGroupParameters.getExistingQuestions().get(section));
        }
        CreateQuestionParameters createQuestionParameters= new CreateQuestionParameters();
        createQuestionParameters.setType("Free Text");
        createQuestionParameters.setText(qTitle5);
        editQuestionGroupPage.setSection("Hello World");
        editQuestionGroupPage.addNewQuestion(createQuestionParameters);
        editQuestionGroupPage.submit();
        questionGroupDetailPage.verifyPage();
        questionGroupDetailPage.navigateToViewQuestionGroupsPage();
    }

    private AdminPage createQuestions(String... qTitles) {
        CreateQuestionPage createQuestionPage = getAdminPage().navigateToCreateQuestionPage();
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        for (String qTitle : qTitles) {
            parameters.setText(qTitle);
            parameters.setType("Free Text");
            createQuestionPage.addQuestion(parameters);
        }
        return createQuestionPage.submitQuestions().verifyPage();
    }

    private void testMissingMandatoryInputs(CreateQuestionGroupPage createQuestionGroupPage) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        createQuestionGroupPage.submit(parameters);
        assertTextFoundOnPage(TITLE_MISSING);
        assertTextFoundOnPage(APPLIES_TO_MISSING);
        assertTextFoundOnPage(SECTION_MISSING);
        createQuestionGroupPage.addEmptySection("Empty Section");
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        assertTextFoundOnPage(QUESTION_MISSING);
    }

    private void testCancelCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage) {
        createQuestionGroupPage.cancel();
        assertPage(AdminPage.PAGE_ID);
    }

    private void testQuestionGroupDetail(ViewAllQuestionGroupsPage viewAllQuestionGroupsPage, String title,
                                         String sectionName, List<String> questions, List<String> mandatoryQuestions) {
        QuestionGroupDetailPage questionGroupDetailPage = navigateToQuestionGroupDetailPage(viewAllQuestionGroupsPage, title);
        testQuestionGroupDetail(questionGroupDetailPage, title, sectionName, questions, mandatoryQuestions);
        questionGroupDetailPage.navigateToViewQuestionGroupsPage().verifyPage();
    }

    private QuestionGroupDetailPage navigateToQuestionGroupDetailPage(ViewAllQuestionGroupsPage viewAllQuestionGroupsPage, String title) {
        QuestionGroupDetailPage questionGroupDetailPage = viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage(title);
        questionGroupDetailPage.verifyPage();
        return questionGroupDetailPage;
    }

    private void testQuestionGroupDetail(QuestionGroupDetailPage questionGroupDetailPage, String title, String sectionName,
                                                            List<String> questions, List<String> mandatoryQuestions) {
        assertEquals(title, questionGroupDetailPage.getTitle());
        assertEquals(APPLIES_TO_CREATE_CLIENT, questionGroupDetailPage.getAppliesTo());
        assertTrue(questionGroupDetailPage.getSections().contains(sectionName));
        assertEquals(questions, questionGroupDetailPage.getSectionsQuestions(sectionName));
        assertEquals(mandatoryQuestions, questionGroupDetailPage.getMandatoryQuestions(sectionName));
    }

    private CreateQuestionGroupPage getCreateQuestionGroupPage(AdminPage adminPage) {
        return adminPage.navigateToCreateQuestionGroupPage();
    }

    private void testViewQuestionGroups(ViewAllQuestionGroupsPage viewQuestionGroupsPage) {
        String[] questionGroups = viewQuestionGroupsPage.getAllQuestionGroups();
        assertEquals(3, questionGroups.length);
        assertEquals(qgTitle1, questionGroups[0]);
        assertEquals(qgTitle2, questionGroups[1]);
        assertEquals(qgTitle2, questionGroups[2]);
    }

    private ViewAllQuestionGroupsPage getViewQuestionGroupsPage(AdminPage adminPage) {
        return adminPage.navigateToViewAllQuestionGroups().verifyPage();
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage().verifyPage();
    }

    private void testShouldAllowDuplicateTitlesForQuestionGroup() {
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), qgTitle2, APPLIES_TO_CREATE_CLIENT, false, "", asList(qTitle1, qTitle3), asList(qTitle2), null);
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), qgTitle2, APPLIES_TO_CREATE_CLIENT, false, "Hello", asList(qTitle2), asList(qTitle1, qTitle3), null);
    }

    private void testCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage, String title, String appliesTo, boolean isAnswerEditable,
                                         String sectionName, List<String> questionsToSelect, List<String> questionsNotToSelect, String questionToAdd) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(title);
        parameters.setAppliesTo(appliesTo);
        parameters.setAnswerEditable(isAnswerEditable);
        for (String question : questionsToSelect) {
            parameters.addExistingQuestion(sectionName, question);
        }
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(questionsToSelect);
        assertTrue(createQuestionGroupPage.getAvailableQuestions().containsAll(questionsNotToSelect));
        CreateQuestionParameters createQuestionParameters= new CreateQuestionParameters();
        createQuestionParameters.setType("Free Text");
        createQuestionParameters.setText(questionToAdd);
        createQuestionGroupPage.setSection(SECTION_MISC);
        if (questionToAdd != null) {
            createQuestionGroupPage.addNewQuestion(createQuestionParameters);
        }
        createQuestionGroupPage.submit(parameters);
        assertPage(AdminPage.PAGE_ID);
    }

}

