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
import org.mifos.test.acceptance.framework.admin.ViewAllQuestionsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    private static final String TITLE_MISSING = "Please specify the title.";
    private static final String DUPLICATE_TITLE = "The name specified already exists.";
    private CreateQuestionParameters createQuestionParameters;

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
        CreateQuestionPage createQuestionPage = testMissingTitle(adminPage);
        testAddQuestion(createQuestionPage);
        testDuplicateTitle(createQuestionPage);
        adminPage = testCreateQuestions(createQuestionPage);
        adminPage = testDuplicateTitleForExistingQuestionInDB(adminPage);
        testViewQuestions(adminPage);
    }

    private void testViewQuestions(AdminPage adminPage) {
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        viewAllQuestionsPage.verifyPage();
        assertTextFoundOnPage(title);
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage();
    }

    private AdminPage testDuplicateTitleForExistingQuestionInDB(AdminPage adminPage) {
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.verifyPage();
        createQuestionParameters.setTitle(title);
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
        createQuestionParameters.setTitle(title);
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertTrue(selenium.isTextPresent(DUPLICATE_TITLE), "Duplicate title error should appear");
    }

    private void testAddQuestion(CreateQuestionPage createQuestionPage) {
        createQuestionParameters.setTitle(title);
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertFalse(selenium.isTextPresent(TITLE_MISSING), "Missing title error should not appear");
        Assert.assertEquals(title, selenium.getTable("questions.table.1.0"));
    }

    private CreateQuestionPage testMissingTitle(AdminPage adminPage) {
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage().verifyPage();
        createQuestionParameters.setTitle("");
        createQuestionPage.addQuestion(createQuestionParameters);
        assertTextFoundOnPage(TITLE_MISSING);
        return createQuestionPage;
    }
}

class CreateQuestionParameters {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}