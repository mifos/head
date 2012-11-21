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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineNewOfficePage;
import org.mifos.test.acceptance.framework.admin.ManageRolePage;
import org.mifos.test.acceptance.framework.admin.ViewRolesPage;
import org.mifos.test.acceptance.framework.client.ClientCloseReason;
import org.mifos.test.acceptance.framework.client.ClientStatus;
import org.mifos.test.acceptance.framework.client.ClientViewChangeLogPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.client.QuestionGroup;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.group.EditCustomerStatusParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.office.CreateOfficePreviewDataPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.office.OfficeViewDetailsPage;
import org.mifos.test.acceptance.framework.questionnaire.AttachQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.Choice;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionGroupsPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionsPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.OfficeHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static java.util.Arrays.*;  //NOPMD
import static org.apache.commons.lang.ArrayUtils.*; //NOPMD
import static org.junit.Assert.*; //NOPMD


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@SuppressWarnings("PMD.CyclomaticComplexity")
@Test(singleThreaded = true, groups = {"client", "acceptance", "ui", "no_db_unit"})
public class QuestionGroupTest extends UiTestCaseBase {
    private AppLauncher appLauncher;
    private OfficeHelper officeHelper;
    private QuestionGroupTestHelper questionGroupTestHelper;
    private ClientTestHelper clientTestHelper;
    private String qgTitle1, qgTitle2, qgTitle3, qgTitle4;
    private String qTitle1, qTitle2, qTitle3, qTitle4, qTitle5;
    private static final String TITLE_MISSING = "Please specify Question Group title.";
    private static final String APPLIES_TO_MISSING = "Please choose a valid 'Applies To' value.";
    private static final String SECTION_MISSING = "Please add at least one section.";
    private static final String QUESTION_MISSING = "Section should have at least one question.";
    public static final String APPLIES_TO_CREATE_CLIENT = "Create Client";
    public static final String APPLIES_TO_CREATE_LOAN = "Create Loan";
    public static final String SECTION_DEFAULT = "Default";
    private static final String SECTION_MISC = "Misc";
    private static final int CREATE_OFFICE_QUESTION_GROUP_ID = 12;
    private static final String CLIENT = "WeeklyClient Wednesday";
    private Map<Integer, QuestionGroup> questionGroupInstancesOfClient;
	private LoanTestHelper loanTestHelper;
    private static final List<String> charactersList = new ArrayList<String>(Arrays.asList("عربية", "有", "òèßñ"));
    private static final String noNumber = "qwerty";
    private static final List<String> EMPTY_LIST = new ArrayList<String>();
    private static final List<String> QUESTIONS_LIST = new ArrayList<String>(Arrays.asList("QGForViewClientCentreGroupLoan",
            "ViewCenterQG", "QGForCreateSavingsAccount", "QGForViewSavings", "QGForCreateLoan1", "QGForCreateLoan2", "QGForLoanApproval",
            "QGForApproveLoan1", "QGForApproveLoan2", "QGForDisburseLoan1", "QGForDisburseLoan2"));
    private static final Map<String, String> QUESTIONS = new HashMap<String, String>();
    private static final String ADMIN_ROLE = "Admin";
    private static final String QUESTION_PERMISSION_ID = "9_6_0";
    private static final String QUESTION_PERMISSION_HEADER = "Can edit ";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        officeHelper = new OfficeHelper(selenium);
        appLauncher = new AppLauncher(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        questionGroupInstancesOfClient = new HashMap<Integer, QuestionGroup>();
        qgTitle1 = "QuestionGroup1";
        qgTitle2 = "QuestionGroup2";
        qgTitle3 = "QuestionGroup3";
        qgTitle4 = "QuestionGroup4";
        qTitle1 = "Question1";
        qTitle2 = "Question2";
        qTitle3 = "Question3";
        qTitle4 = "Question4";
        qTitle5 = "Question5";
        QUESTIONS.put("TextQuestionTest", CreateQuestionParameters.TYPE_FREE_TEXT);
        QUESTIONS.put("DateQuestionTest", CreateQuestionParameters.TYPE_DATE);
        QUESTIONS.put("NumberQuestionTest", CreateQuestionParameters.TYPE_NUMBER);
        QUESTIONS.put("MultiSelectQuestionTest", CreateQuestionParameters.TYPE_MULTI_SELECT);
        QUESTIONS.put("SingleSelectQuestionTest", CreateQuestionParameters.TYPE_SINGLE_SELECT);
        QUESTIONS.put("SmartSelectQuestionTest", CreateQuestionParameters.TYPE_SMART_SELECT);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createQuestionGroup() throws Exception {
        try {
            AdminPage adminPage = createQuestions(qTitle1, qTitle2, qTitle3);
            CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
            testMissingMandatoryInputs(createQuestionGroupPage);
            testCreateQuestionGroup(createQuestionGroupPage, qgTitle1, APPLIES_TO_CREATE_CLIENT, true, SECTION_DEFAULT, asList(qTitle1, qTitle2), asList(qTitle3), qTitle4);
            testShouldAllowDuplicateTitlesForQuestionGroup();
            testCancelCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)));
            testViewQuestionGroups();
        } finally {
            questionGroupTestHelper.markQuestionGroupAsInactive(qgTitle1);
            questionGroupTestHelper.markQuestionGroupAsInactive(qgTitle2);
            questionGroupTestHelper.markQuestionGroupAsInactive(qgTitle3);
        }
    }
    @Test(enabled=true)
    public void createQuestionGroupForAllLoans() {
        AdminPage adminPage = getAdminPage();
        CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
        String random = String.valueOf(Math.random());
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle("QuestionGroupForAllLoans"+random);
        parameters.setAppliesTo(APPLIES_TO_CREATE_LOAN);
        parameters.setApplyToAllLoanProducts(true);
        parameters.setAnswerEditable(false);
        CreateQuestionParameters questionParameters = new CreateQuestionParameters();
        questionParameters.setType("Free Text");
        questionParameters.setText("QuestionForAllLoans"+random);
        createQuestionGroupPage.setSection(SECTION_MISC);
        createQuestionGroupPage.addNewQuestion(questionParameters);

        createQuestionGroupPage.submit(parameters);
        assertPage(AdminPage.PAGE_ID);
        EditQuestionGroupPage editPage = questionGroupTestHelper.naviagateToEditQuestionGroup("QuestionGroupForAllLoans"+random);
        if (!editPage.isApplayForAllLoansCheckboxChecked()){
            Assert.fail();
        }
        editPage.setApplayForAllLoansCheckbox(false);
        editPage.submit();
        editPage  = questionGroupTestHelper.naviagateToEditQuestionGroup("QuestionGroupForAllLoans"+random);
        if (editPage.isApplayForAllLoansCheckboxChecked()){
            Assert.fail();
        }
        editPage.deactivate();
    }
    
    @Test(enabled = true)
    public void checkQuestionGroupPermission() {
        AdminPage adminPage = getAdminPage();
        CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(qgTitle4);
        parameters.setAppliesTo(APPLIES_TO_CREATE_CLIENT);
        parameters.setAnswerEditable(true);
        for (String question : asList(qTitle1, qTitle2)) {
            parameters.addExistingQuestion(SECTION_DEFAULT, question);
        }
        for (String section : parameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.submit(parameters);
        ViewRolesPage rolesPage = adminPage.navigateToViewRolesPage();
        ManageRolePage manageRolePage = rolesPage.navigateToManageRolePage(ADMIN_ROLE);
        manageRolePage.verifyPermissionText(QUESTION_PERMISSION_ID, QUESTION_PERMISSION_HEADER + qgTitle4);
        manageRolePage.disablePermission(QUESTION_PERMISSION_ID);
        manageRolePage.submitAndGotoViewRolesPage();
        adminPage = getAdminPage();
        Assert.assertTrue(adminPage.navigateToViewAllQuestionGroups().navigateToQuestionGroupDetailPage(qgTitle4).isAccessDeniedDisplayed());
        //set question group on inactive
        adminPage.navigateBack();
        adminPage.navigateToAdminPageUsingHeaderTab();
        manageRolePage = adminPage.navigateToViewRolesPage().navigateToManageRolePage(ADMIN_ROLE);
        manageRolePage.verifyPermissionText(QUESTION_PERMISSION_ID, QUESTION_PERMISSION_HEADER + qgTitle4);
        manageRolePage.enablePermission(QUESTION_PERMISSION_ID);
        manageRolePage.submitAndGotoViewRolesPage().navigateToAdminPage().navigateToViewAllQuestionGroups().navigateToQuestionGroupDetailPage(qgTitle4);
        questionGroupTestHelper.markQuestionGroupAsInactive(qgTitle4);
        
    }

    /**
     * Verify that user is able to edit the defined Question Groups
     * and create Office with edited Question Group
     * http://mifosforge.jira.com/browse/MIFOSTEST-666
     *
     * @throws Exception
     */
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void editQuestionGroupsForCreateOffice() throws Exception {
        //When
        ViewAllQuestionGroupsPage viewAllQuestionGroupsPage = questionGroupTestHelper.navigateToViewQuestionGroups(QUESTIONS_LIST);
        String oldTitle = "CreateOffice";
        EditQuestionGroupPage editQuestionGroupPage = viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage(oldTitle).navigateToEditPage();
        editQuestionGroupPage.moveSectionUp("Misc");
        editQuestionGroupPage.moveQuestionUp(9);
        editQuestionGroupPage.moveQuestionDown(2);
        String newTitle = "CreateOfficeQG";
        QuestionGroupDetailPage questionGroupDetailPage = editQuestionGroupPage.editQuestionGroup(false, newTitle, "Create Office", Collections.<String>emptyList());
        //Then
        questionGroupDetailPage.verifyOrderQuestions(asList("MultiSelect", "Date", "FreeText"), 4);
        questionGroupDetailPage.verifyOrderSections(asList("Misc", "Default"));
        questionGroupDetailPage.verifyMandatoryQuestions(Arrays.asList("FreeText"), "Misc");
        questionGroupDetailPage.verifyMandatoryQuestions(EMPTY_LIST, "Default");
        questionGroupDetailPage.verifyTitle(newTitle);
        questionGroupTestHelper.markQuestionGroupAsActive(newTitle);
        createOfficeWithQuestionGroup();
        questionGroupTestHelper.markQuestionGroupAsInactive(newTitle);
        //When
        viewAllQuestionGroupsPage = questionGroupDetailPage.navigateToViewQuestionGroupsPage();
        //Then
        viewAllQuestionGroupsPage.verifyInactiveQuestions(0, 0);
    }
  
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void createOfficeWithQuestionGroup() throws Exception {
        //When
        QuestionResponsePage questionResponsePage = officeHelper.navigateToQuestionResponsePage(getOfficeParameters("MyOfficeDHMFT", "DHM"));
        QuestionResponseParameters initialResponse = getResponse("123");
        QuestionResponseParameters updatedResponse = getResponse("1234");
        CreateOfficePreviewDataPage createOfficePreviewDataPage = questionGroupTestHelper.createOfficeWithQuestionGroup(questionResponsePage, initialResponse, updatedResponse);
        assertTextFoundOnPage("This office name already exist");
        DefineNewOfficePage defineNewOfficePage = createOfficePreviewDataPage.editOfficeInformation();
        defineNewOfficePage.setOfficeName("TestOffice");
        defineNewOfficePage.setOfficeShortName("TO");
        defineNewOfficePage.preview();
        defineNewOfficePage.next();
        OfficeViewDetailsPage officeViewDetailsPage = createOfficePreviewDataPage.submit().navigateToOfficeViewDetailsPage();
        ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = officeViewDetailsPage.navigateToViewAdditionalInformation();
        viewQuestionResponseDetailPage.verifyQuestionPresent("FreeText", "1234");
        officeViewDetailsPage = viewQuestionResponseDetailPage.navigateToDetailsPage();

        String newQuestion = "Text";
        addQuestion(newQuestion, "Default", CREATE_OFFICE_QUESTION_GROUP_ID);

        String questionToDeactivate = "FreeText";
        questionGroupTestHelper.markQuestionAsInactive(questionToDeactivate);
        questionResponsePage = officeHelper.navigateToQuestionResponsePage(getOfficeParameters("TestOffice2", "TO2"));

        //Then
        questionResponsePage.verifyQuestionsDoesnotappear(new String[]{questionToDeactivate});
        questionResponsePage.verifyQuestionsExists(new String[]{newQuestion});

        officeHelper.verifyQuestionPresent("TestOffice", "Text", "");
    }
    


    
    /**
     * Verifying that Change Log for Question Groups has an appropriate format
     * http://mifosforge.jira.com/browse/MIFOSTEST-667
     *
     * @throws Exception
     */
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void testChangeLog() throws Exception {
        String questionGroup = "CreateClientQG-1";
        try {
            questionGroupTestHelper.markQuestionAsActive("FreeText");
            questionGroupTestHelper.markQuestionGroupAsActive(questionGroup);
            //Given
            ClientViewDetailsPage clientViewDetailsPage = clientTestHelper.navigateToClientViewDetailsPage(CLIENT);
            //When
            clientViewDetailsPage = clientTestHelper.editQuestionGroupResponses(
                    clientViewDetailsPage, "0", "details[0].sectionDetails[0].questions[0].value", "qwert"
            );
            //Then
            ClientViewChangeLogPage clientViewChangeLogPage = clientViewDetailsPage.navigateToClientViewChangeLog();
            clientViewChangeLogPage.verifyChangeLog(asList("CreateClientQG-1/Misc/FreeText"),
                    asList("-"), asList("qwert"), asList("mifos"), 2);
        } finally {
            questionGroupTestHelper.markQuestionGroupAsInactive(questionGroup);
        }
    }

    private void addQuestion(String newQuestion, String section, int questionGroupId) {
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.addExistingQuestion(section, newQuestion);
        questionGroupTestHelper.addQuestionsToQuestionGroup(questionGroupId, createQuestionGroupParameters.getExistingQuestions());
    }

    private OfficeParameters getOfficeParameters(String name, String shortName) {
        OfficeParameters officeParameters = new OfficeParameters();
        officeParameters.setOfficeName(name);
        officeParameters.setOfficeType(OfficeParameters.REGIONAL_OFFICE);
        officeParameters.setParentOffice("Head Office(Mifos HO )");
        officeParameters.setShortName(shortName);
        return officeParameters;
    }

    private QuestionResponseParameters getResponse(String answer) {
        QuestionResponseParameters initialResponse = new QuestionResponseParameters();
        initialResponse.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", answer);
        return initialResponse;
    }

    /**
     * Creating and editing Questions
     * http://mifosforge.jira.com/browse/MIFOSTEST-700
     *
     * @throws Exception
     */
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createAndEditQuestionsTest() throws Exception {
        //When
        testValidationAddQuestion();
        CreateQuestionPage createQuestionPage = questionGroupTestHelper.navigateToCreateQuestionPage();
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setText("TextQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        createQuestionPage.addQuestion(createQuestionParameters);

        createQuestionParameters.setText("DateQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_DATE);
        createQuestionPage.addQuestion(createQuestionParameters);

        createQuestionParameters.setText("NumberQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_NUMBER);
        createQuestionParameters.setNumericMin(1);
        createQuestionParameters.setNumericMax(10);
        createQuestionPage.addQuestion(createQuestionParameters);

        createQuestionParameters.setText("MultiSelectQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_MULTI_SELECT);
        List<Choice> choices = new ArrayList<Choice>();
        Choice c = new Choice("choice 1", EMPTY_LIST);
        choices.add(c);
        c = new Choice("choice 2", EMPTY_LIST);
        choices.add(c);
        c = new Choice("choice 3", EMPTY_LIST);
        choices.add(c);
        createQuestionParameters.setChoices(choices);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionParameters.setText("SingleSelectQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        createQuestionParameters.setChoices(choices);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionParameters.setText("SmartSelectQuestionTest");
        createQuestionParameters.setChoices(choices);
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_SMART_SELECT);
        createQuestionPage.addQuestion(createQuestionParameters);
        AdminPage adminPage = createQuestionPage.submitQuestions();
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();

        //Then
        viewAllQuestionsPage.verifyQuestions(QUESTIONS.keySet());

        //When
        c = new Choice("answerChoice1", EMPTY_LIST);
        choices.add(c);
        c = new Choice("answerChoice3", EMPTY_LIST);
        choices.add(c);
        for (String question : QUESTIONS.keySet()) {
            QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(question);
            EditQuestionPage editQuestionPage = questionDetailPage.navigateToEditQuestionPage();
            createQuestionParameters.setText("");
            editQuestionPage.tryUpdate(createQuestionParameters);
            //Then
            editQuestionPage.verifyTextPresent("Please specify the question", "No text <Please specify the question> present on the page");
            questionDetailPage = editQuestionPage.cancelEdit();
            //When
            editQuestionPage = questionDetailPage.navigateToEditQuestionPage();
            for (String characters : charactersList) {
                editQuestionPage.setQuestionName(characters);
                editQuestionPage.verifyQuestionName(characters);
            }
            if ("NumberQuestionTest".equals(question)) {
                editQuestionPage.setNumberQuestion(noNumber, noNumber);
                editQuestionPage.verifyNumberQuestion("", "");
                editQuestionPage.setNumberQuestion("", "");
            } else if ("MultiSelectQuestionTest".equals(question) || "SingleSelectQuestionTest".equals(question)) {
                editQuestionPage.addAnswerChoices(asList("answerChoice1", "answerChoice2", "answerChoice3"));
                editQuestionPage.removeAnswerChoice("4");
            } else if ("SmartSelectQuestionTest".equals(question)) {
                editQuestionPage.addSmartAnswerChoices(asList("answerChoice1", "answerChoice2", "answerChoice3"));
                editQuestionPage.removeAnswerChoice("4");
            }
            editQuestionPage.setQuestionName(question + "Edit");
            questionDetailPage = editQuestionPage.deactivate();
            //Then
            questionDetailPage.verifyQuestionTitle(question + "Edit");
            if ("MultiSelectQuestionTest".equals(question) || "SingleSelectQuestionTest".equals(question) || "SmartSelectQuestionTest".equals(question)) {
                questionDetailPage.assertForChoices(QUESTIONS.get(question), choices);
            }
            viewAllQuestionsPage = questionDetailPage.navigateToViewAllQuestionsPage();
        }
    }

    /**
     * Attaching a Question Group to Multiple flows
     * http://mifosforge.jira.com/browse/MIFOSTEST-701
     *
     * @throws Exception
     */
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachingQuestionGroupToMultipleFlowsTest() throws Exception {
        String newClient = "Joe701 Doe701";
        questionGroupTestHelper.markQuestionGroupAsInactive("CreateOffice");
        createClient("Joe701","Doe701");
        //When
        testValidationAddQuestionGroup();
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
        List<String> questions = new ArrayList<String>();
        questions.add("Date");
        questions.add("Number");
        questions.add("Text");
        sectionQuestions.put("Sec Test", questions);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setAnswerEditable(true);
        String testQuestionGroup = "TestQuestionGroup";
        createQuestionGroupParameters.setTitle(testQuestionGroup);
        createQuestionGroupParameters.setAppliesTo("View Client");
        createQuestionGroupParameters.setAppliesTo("Close Client");
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
        try {
            questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);
            Map<String, String> answers = new HashMap<String, String>();
            answers.put("Text", "Test - Text");
            answers.put("Number", "2");
            answers.put("Date", "11/11/2009");
            ClientViewDetailsPage clientViewDetailsPage = questionGroupTestHelper.attachQuestionGroup(newClient, testQuestionGroup, asList("Sec Test"), answers);
            CustomerChangeStatusPage customerChangeStatusPage = clientViewDetailsPage.navigateToCustomerChangeStatusPage();
            EditCustomerStatusParameters customerStatusParameters = new EditCustomerStatusParameters();
            customerStatusParameters.setNote("TEST");
            customerStatusParameters.setClientStatus(ClientStatus.CLOSED);
            customerStatusParameters.setClientCloseReason(ClientCloseReason.TRANSFERRED);
            QuestionResponsePage questionResponsePage = customerChangeStatusPage.changeStatusAndNavigateToQuestionResponsePage(customerStatusParameters);
            //Then
            questionResponsePage.verifyQuestionsExists(questions.toArray(new String[questions.size()]));
            //When
            clientViewDetailsPage = questionResponsePage.cancel();
            ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = clientViewDetailsPage.navigateToViewAdditionalInformationPage();
            //Then
            viewQuestionResponseDetailPage.verifyQuestionsDoesnotappear(questions.toArray(new String[questions.size()]));
            clientViewDetailsPage = viewQuestionResponseDetailPage.navigateToClientViewDetailsPage();
            answers = new HashMap<String, String>();
            answers.put("Text", "Test - Text - Edit");
            answers.put("Number", "22");
            questionGroupInstancesOfClient = clientViewDetailsPage.getQuestionGroupInstances();
            questionGroupTestHelper.editResponses(clientViewDetailsPage, latestInstanceId(questionGroupInstancesOfClient), answers);
            
            // extension MIFOS-5821
        	CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        	searchParameters.setSearchString("Client - Mary Monthly");
        	searchParameters.setLoanProduct("MonthlyClientFlatLoanThirdFridayOfMonth");
           	loanTestHelper.createDefaultLoanAccount(searchParameters);                

        } finally {
            questionGroupTestHelper.markQuestionGroupAsInactive(testQuestionGroup);
            questionGroupTestHelper.markQuestionGroupAsInactive("CreateOffice");
        }
    }

    private void createClient(String firstName, String lastName) {
        String groupName = "group1";
        CreateClientEnterPersonalDataPage.SubmitFormParameters clientParams = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        clientParams.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        clientParams.setFirstName(firstName);
        clientParams.setLastName(lastName);
        clientParams.setDateOfBirthDD("22");
        clientParams.setDateOfBirthMM("06");
        clientParams.setDateOfBirthYYYY("1987");
        clientParams.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.MALE);
        clientParams.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.NOT_POOR);
        clientParams.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        clientParams.setSpouseFirstName("fatherNameTest");
        clientParams.setSpouseLastName("fatherLastNameTest");
        clientTestHelper.createNewClient(groupName, clientParams);
        clientTestHelper.activateClient(firstName + " " + lastName);
    }
    @Test(enabled=true)
    //http://mifosforge.jira.com/browse/MIFOSTEST-660
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToGroup() throws Exception {
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
        List<String> questions = new ArrayList<String>();
        String title = "TestQuestionGroup" + StringUtil.getRandomString(6);
        questions.add("Date");
        questions.add("question 3");
        questions.add("question 4");
        sectionQuestions.put("Sec 1", questions);
        questions = new ArrayList<String>();
        questions.add("DateQuestion");
        questions.add("Number");
        questions.add("question 1");
        questions.add("Text");
        sectionQuestions.put("Sec 2", questions);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setAppliesTo("View Group");
        createQuestionGroupParameters.setTitle(title);
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
        AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
        attachParams.setTarget("Default Group");
        attachParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachParams.addTextResponse("Date", "09/02/2011");
        attachParams.addCheckResponse("question 4", "yes");
        attachParams.addTextResponse("DateQuestion", "19/02/2011");
        attachParams.addTextResponse("question 3", "25/02/2011");
        attachParams.addTextResponse("Number", "60");
        attachParams.addTextResponse("question 1", "tekst tekst");
        attachParams.addTextResponse("Text", "ale alo olu");
        AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
        attachErrorParams.setTarget("Default Group");
        attachErrorParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachErrorParams.addTextResponse("Number", "sdfsdf");
        attachErrorParams.addTextResponse("question 3", "25/02/2011");
        attachErrorParams.addCheckResponse("question 4", "yes");
        attachErrorParams.addError("Please specify a number for Number.");

        //When
        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

        questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToGroup(attachErrorParams);
        questionGroupTestHelper.attachQuestionGroupToGroup(attachParams);

        attachParams.addTextResponse("Number", "20");
        attachParams.addTextResponse("question 3", "21/02/2011");
        //Then
        questionGroupTestHelper.editQuestionGroupResponsesInGroup(attachParams);
        
        questionGroupTestHelper.markQuestionGroupAsInactive(title);
    }
    @Test(enabled=true)
    //http://mifosforge.jira.com/browse/MIFOSTEST-662
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToLoan() throws Exception {
        String title = "TestQuestionGroup" + StringUtil.getRandomString(6);
        try {
            Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
            List<String> questions = new ArrayList<String>();
            questions.add("question 4");
            questions.add("question 3");
            questions.add("Date");
            sectionQuestions.put("Sec 1", questions);
            questions = new ArrayList<String>();
            questions.add("question 1");
            questions.add("Number");
            questions.add("DateQuestion");
            questions.add("Text");
            sectionQuestions.put("Sec 2", questions);
            CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
            createQuestionGroupParameters.setAppliesTo("View Loan");
            createQuestionGroupParameters.setAnswerEditable(true);
            createQuestionGroupParameters.setTitle(title);
            createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
            AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
            attachParams.setTarget("000100000000012");
            attachParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
            attachParams.addCheckResponse("question 4", "yes");
            attachParams.addTextResponse("Date", "09/02/2011");
            attachParams.addTextResponse("DateQuestion", "19/02/2011");
            attachParams.addTextResponse("question 3", "25/02/2011");
            attachParams.addTextResponse("question 1", "tekst tekst");
            attachParams.addTextResponse("Text", "ale alo olu");
            attachParams.addTextResponse("Number", "60");
            AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
            attachErrorParams.setTarget("000100000000012");
            attachErrorParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
            attachErrorParams.addError("Please specify a number for Number.");
            attachErrorParams.addTextResponse("Number", "sdfsdf");
            attachErrorParams.addTextResponse("question 3", "25/02/2011");
            attachErrorParams.addCheckResponse("question 4", "yes");

            //When
            questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

            questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToLoan(attachErrorParams);
            questionGroupTestHelper.attachQuestionGroupToLoan(attachParams);

            attachParams.addTextResponse("Number", "20");
            attachParams.addTextResponse("question 3", "21/02/2011");
            //Then
            questionGroupTestHelper.editQuestionGroupResponsesInLoan(attachParams);
        } finally {
            questionGroupTestHelper.markQuestionGroupAsInactive(title);
        }
    }
    @Test(enabled=true)
    //http://mifosforge.jira.com/browse/MIFOSTEST-680
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToViewClient() throws Exception {
        String title = "TestQuestionGroup" + StringUtil.getRandomString(6);
        try {
            Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
            List<String> questions = new ArrayList<String>();
            questions.add("Date");
            questions.add("question 4");
            questions.add("question 3");
            sectionQuestions.put("Sec 1", questions);
            questions = new ArrayList<String>();
            questions.add("Number");
            questions.add("DateQuestion");
            questions.add("question 1");
            questions.add("Text");
            sectionQuestions.put("Sec 2", questions);
            CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
            createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
            createQuestionGroupParameters.setAnswerEditable(true);
            createQuestionGroupParameters.setAppliesTo("View Client");
            createQuestionGroupParameters.setTitle(title);
            AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
            attachParams.setTarget("0002-000000003");
            attachParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
            attachParams.addTextResponse("Number", "60");
            attachParams.addTextResponse("question 1", "tekst tekst");
            attachParams.addCheckResponse("question 4", "yes");
            attachParams.addTextResponse("question 3", "25/02/2011");
            attachParams.addTextResponse("Date", "09/02/2011");
            attachParams.addTextResponse("DateQuestion", "19/02/2011");
            attachParams.addTextResponse("Text", "ale alo olu");
            AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
            attachErrorParams.setTarget("0002-000000003");
            attachErrorParams.addError("Please specify a number for Number.");
            attachErrorParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
            attachErrorParams.addTextResponse("Number", "sdfsdf");
            attachErrorParams.addCheckResponse("question 4", "yes");
            attachErrorParams.addTextResponse("question 3", "25/02/2011");

            //When
            questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

            questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToClient(attachErrorParams);
            questionGroupTestHelper.attachQuestionGroupToClient(attachParams);

            attachParams.addTextResponse("Number", "20");
            attachParams.addTextResponse("question 3", "21/02/2011");
            //Then
            questionGroupTestHelper.editQuestionGroupResponsesInClient(attachParams);
        } finally {
            questionGroupTestHelper.markQuestionGroupAsInactive(title);
        }
    }

    private void testValidationAddQuestionGroup() {
        questionGroupTestHelper.validatePageBlankMandatoryField();
        questionGroupTestHelper.validateQuestionGroupTitle(charactersList);
    }

    private void testValidationAddQuestion() {
        CreateQuestionPage createQuestionPage = questionGroupTestHelper.navigateToCreateQuestionPage();
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setText("");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionPage.verifyTextPresent("Please specify the question", "No text <Please specify the question> present on the page");
        createQuestionPage.verifySubmitButtonStatus("true");
        createQuestionPage.cancelQuestion();
        createQuestionPage = questionGroupTestHelper.navigateToCreateQuestionPage();
        createQuestionPage.setNumberQuestion("NumberQuestion", noNumber, noNumber);
        createQuestionPage.verifyNumberQuestion("", "");
        createQuestionPage.setNumberQuestion("NumberQuestion", "", "");
        for (int i = 0; i < charactersList.size(); i++) {
            createQuestionParameters.setText(charactersList.get(i));
            createQuestionPage.addQuestion(createQuestionParameters);
            createQuestionPage.verifyQuestionPresent(createQuestionParameters, i + 1);
        }
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
        for (String section : createQuestionGroupParameters.getExistingQuestions().keySet()) {
            editQuestionGroupPage.addExistingQuestion(section, createQuestionGroupParameters.getExistingQuestions().get(section));
        }
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
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
        for (String section : parameters.getExistingQuestions().keySet()) {
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
        Assert.assertTrue(contains(questionGroups, qgTitle1));
        Assert.assertTrue(contains(questionGroups, qgTitle2));
        Assert.assertTrue(indexOf(questionGroups, qgTitle2, indexOf(questionGroups, qgTitle1, 0)) > 0);
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
        for (String section : parameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(questionsToSelect);
        assertTrue(createQuestionGroupPage.getAvailableQuestions().containsAll(questionsNotToSelect));
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setType("Free Text");
        createQuestionParameters.setText(questionToAdd);
        createQuestionGroupPage.setSection(SECTION_MISC);
        if (questionToAdd != null) {
            createQuestionGroupPage.addNewQuestion(createQuestionParameters);
        }
        createQuestionGroupPage.submit(parameters);
        assertPage(AdminPage.PAGE_ID);
    }

    private Integer latestInstanceId(Map<Integer, QuestionGroup> questionGroups) {
        Set<Integer> keys = questionGroups.keySet();
        return Collections.max(keys);
    }
    
    private void editResponses(ClientViewDetailsPage clientViewDetailsPage, int id, Map<String,String> answers) {
        QuestionGroupResponsePage questionGroupResponsePage = clientViewDetailsPage.navigateToQuestionGroupResponsePage(id);
        QuestionnairePage questionnairePage = questionGroupResponsePage.navigateToEditResponses();
        for(String question: answers.keySet()) {
            questionnairePage.setResponse(question, answers.get(question));
        }
        ClientViewDetailsPage clientViewDetailsPage2 = (ClientViewDetailsPage)questionnairePage.submit();
        Assert.assertEquals(clientViewDetailsPage2.getQuestionGroupInstances().get(id).getName(), "TestQuestionGroup");
    }
}

