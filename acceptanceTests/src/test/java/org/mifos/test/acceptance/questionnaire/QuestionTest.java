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
package org.mifos.test.acceptance.questionnaire;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.QuestionDetailPage;
import org.mifos.test.acceptance.framework.admin.ViewAllQuestionsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"client", "acceptance", "ui", "smoke"})
public class QuestionTest extends UiTestCaseBase {
    private AppLauncher appLauncher;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml.zip";
    private String title;
    private static final String TITLE_MISSING = "Please specify the question title.";
    private static final String AT_LEAST_2_CHOICES = "Please specify at least 2 choices.";
    private static final String DUPLICATE_TITLE = "Question title already exists.";
    private CreateQuestionParameters createQuestionParameters;
    private static final String DATE = "Date";
    private static final String FREE_TEXT = "Free Text";
    private static final String MULTI_SELECT = "Multi Select";
    private static final String SINGLE_SELECT = "Single Select";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        title = "Title " + System.currentTimeMillis();
        createQuestionParameters = new CreateQuestionParameters();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void createQuestion() {
        AdminPage adminPage = getAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage().verifyPage();
        testSubmitButtonDisabled(createQuestionPage);
        testMissingTitle(createQuestionPage);
        testAddQuestions(createQuestionPage);
        testDuplicateTitle(createQuestionPage);
        adminPage = testCreateQuestions(createQuestionPage);
        adminPage = testDuplicateTitleForExistingQuestionInDB(adminPage);
        ViewAllQuestionsPage viewAllQuestionsPage = testViewQuestions(adminPage);
        testViewQuestions(viewAllQuestionsPage);
    }

    private void testViewQuestions(ViewAllQuestionsPage viewAllQuestionsPage) {
        testViewQuestionDetail(viewAllQuestionsPage, DATE);
        testViewQuestionDetail(viewAllQuestionsPage, FREE_TEXT);
        testViewQuestionDetail(viewAllQuestionsPage, MULTI_SELECT, "choice2", "choice3");
        testViewQuestionDetail(viewAllQuestionsPage, SINGLE_SELECT, "choice1", "choice2");
    }

    private void testAddQuestions(CreateQuestionPage createQuestionPage) {
        testAddQuestion(createQuestionPage, DATE, title);
        testAddQuestion(createQuestionPage, FREE_TEXT, title);
        testAddQuestion(createQuestionPage, SINGLE_SELECT, title, "choice1", "choice2");
        testAddQuestion(createQuestionPage, MULTI_SELECT, title, "choice2", "choice3");
        testAddQuestion(createQuestionPage, MULTI_SELECT, title + 1, "choice2");
    }

    private void testViewQuestionDetail(ViewAllQuestionsPage viewAllQuestionsPage, String type, String... choices) {//NOPMD
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(title + type);
        questionDetailPage.verifyPage();
        Assert.assertTrue(selenium.isTextPresent("Question: " + title + type), "Title is missing");
        Assert.assertTrue(selenium.isTextPresent("Answer Type: " + type), "Answer type is missing");
        if (questionHasAnswerChoices(type)) {
            Assert.assertTrue(selenium.isTextPresent("Answer Choices: " + choices[0] + ", " + choices[1]));
        } else {
            Assert.assertFalse(selenium.isTextPresent("Answer Choices: "), "Answer type should not be present");
        }
        viewAllQuestionsPage = questionDetailPage.navigateToViewAllQuestionsPage();
    }

    private ViewAllQuestionsPage testViewQuestions(AdminPage adminPage) {
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        viewAllQuestionsPage.verifyPage();
        assertTextFoundOnPage(title);
        return viewAllQuestionsPage;
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage();
    }

    private AdminPage testDuplicateTitleForExistingQuestionInDB(AdminPage adminPage) {
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.verifyPage();
        createQuestionParameters.setTitle(title + DATE);
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertTrue(selenium.isTextPresent(DUPLICATE_TITLE), "Duplicate title error should appear");
        return createQuestionPage.navigateToAdminPage();
    }

    private AdminPage testCreateQuestions(CreateQuestionPage createQuestionPage) {
        AdminPage adminPage;
        adminPage = createQuestionPage.submitQuestions();
        adminPage.verifyPage();
        return adminPage;
    }

    private void testDuplicateTitle(CreateQuestionPage createQuestionPage) {
        createQuestionParameters.setTitle(title + FREE_TEXT);
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertTrue(selenium.isTextPresent(DUPLICATE_TITLE), "Duplicate title error should appear");
    }

    private void testAddQuestion(CreateQuestionPage createQuestionPage, String type, String title, String... choices) {
        setupQuestionParameters(title + type, type, Arrays.asList(choices));
        testAddQuestion(createQuestionPage);
    }

    private void setupQuestionParameters(String title, String type, List<String> choices) {
        createQuestionParameters.setTitle(title);
        createQuestionParameters.setType(type);
        createQuestionParameters.setChoices(choices);
    }

    private void testAddQuestion(CreateQuestionPage createQuestionPage) {
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertFalse(selenium.isTextPresent(TITLE_MISSING), "Missing title error should not appear");
        List<String> choices = createQuestionParameters.getChoices();
        if (questionHasAnswerChoices() && choices.size() < 2) {
            Assert.assertTrue(selenium.isTextPresent(AT_LEAST_2_CHOICES), "Missing warning for giving at least 2 choices");
        } else {
            assertLastAddedQuestion(createQuestionPage);
        }
    }

    private void assertLastAddedQuestion(CreateQuestionPage createQuestionPage) {
        CreateQuestionParameters question = createQuestionPage.getLastAddedQuestion();
        assertEquals(createQuestionParameters.getTitle(), question.getTitle());
        assertEquals(createQuestionParameters.getType(), question.getType());
        if (questionHasAnswerChoices()) {
            Assert.assertEquals(createQuestionParameters.getChoices(), question.getChoices());
        } else {
            Assert.assertEquals(Arrays.asList("Not Applicable"), question.getChoices());
        }
        testSubmitButtonEnabled(createQuestionPage);
    }

    private boolean questionHasAnswerChoices() {
        return MULTI_SELECT.equals(createQuestionParameters.getType()) || SINGLE_SELECT.equals(createQuestionParameters.getType());
    }

    private boolean questionHasAnswerChoices(String type) {
        return MULTI_SELECT.equals(type) || SINGLE_SELECT.equals(type);
    }

    private void testSubmitButtonDisabled(CreateQuestionPage createQuestionPage) {
        assertEquals("true", createQuestionPage.submitButtonStatus());
        assertEquals("disabledbuttn", createQuestionPage.submitButtonClass());
    }

    private void testSubmitButtonEnabled(CreateQuestionPage createQuestionPage) {
        assertEquals("false", createQuestionPage.submitButtonStatus());
        assertEquals("buttn", createQuestionPage.submitButtonClass());
    }

    private void testMissingTitle(CreateQuestionPage createQuestionPage) {
        createQuestionParameters.setTitle("");
        createQuestionParameters.setType(DATE);
        createQuestionPage.addQuestion(createQuestionParameters);
        assertTextFoundOnPage(TITLE_MISSING);
    }
}

