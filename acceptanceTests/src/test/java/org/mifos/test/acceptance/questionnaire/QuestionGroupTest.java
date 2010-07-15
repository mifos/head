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
import org.mifos.test.acceptance.framework.admin.QuestionGroupDetailPage;
import org.mifos.test.acceptance.framework.admin.ViewAllQuestionGroupsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"client", "acceptance", "ui", "smoke"})
public class QuestionGroupTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml.zip";
    private String qgTitle1, qgTitle2;
    private String qTitle1, qTitle2, qTitle3;
    private static final String TITLE_MISSING = "Please specify Question Group title";
    private static final String APPLIES_TO_MISSING = "Please choose a valid 'Applies To' value";
    private static final String SECTION_MISSING = "Please add at least one section";
    private static final String QUESTION_MISSING = "Section should have at least one question";
    public static final String APPLIES_TO_CREATE_CLIENT = "Create Client";
    public static final String SECTION_DEFAULT = "Default";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        qgTitle1 = "QuestionGroup1 " + System.currentTimeMillis();
        qgTitle2 = "QuestionGroup2 " + System.currentTimeMillis();
        qTitle1 = "Question1 " + System.currentTimeMillis();
        qTitle2 = "Question2 " + System.currentTimeMillis();
        qTitle3 = "Question3 " + System.currentTimeMillis();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void createQuestionGroup() {
        AdminPage adminPage = createQuestions(qTitle1, qTitle2, qTitle3);
        CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
        testMissingMandatoryInputs(createQuestionGroupPage);
        testCreateQuestionGroup(createQuestionGroupPage, qgTitle1, APPLIES_TO_CREATE_CLIENT, SECTION_DEFAULT, asList(qTitle1, qTitle2), asList(qTitle3));
        testShouldAllowDuplicateTitlesForQuestionGroup();
        testCancelCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)));
        testViewQuestionGroups();
    }

    private void testViewQuestionGroups() {
        ViewAllQuestionGroupsPage viewQuestionGroupsPage = getViewQuestionGroupsPage(new AdminPage(selenium));
        testViewQuestionGroups(viewQuestionGroupsPage);
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle1, "Default", asList(qTitle1, qTitle2), asList(qTitle1));
        viewQuestionGroupsPage.navigateToViewAllQuestionGroupsPage();
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle2, "Misc", asList(qTitle1, qTitle3), asList(qTitle1));
    }

    private AdminPage createQuestions(String... qTitles) {
        CreateQuestionPage createQuestionPage = getAdminPage().navigateToCreateQuestionPage().verifyPage();
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        for (String qTitle : qTitles) {
            parameters.setTitle(qTitle);
            parameters.setType("Free text");
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
        createQuestionGroupPage.addSection(parameters);
        assertTextFoundOnPage(QUESTION_MISSING);
    }

    private void testCancelCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage) {
        createQuestionGroupPage.cancel();
        assertPage(AdminPage.PAGE_ID);
    }

    private void testQuestionGroupDetail(ViewAllQuestionGroupsPage viewAllQuestionGroupsPage, String title, String sectionName, List<String> questions, List<String> mandatoryQuestions) {
        QuestionGroupDetailPage questionGroupDetailPage = viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage(title);
        questionGroupDetailPage.verifyPage();
        assertEquals(title, questionGroupDetailPage.getTitle());
        assertEquals(APPLIES_TO_CREATE_CLIENT, questionGroupDetailPage.getAppliesTo());
        assertEquals(asList(sectionName), questionGroupDetailPage.getSections());
        assertEquals(questions, questionGroupDetailPage.getSectionsQuestions(sectionName));
        assertEquals(mandatoryQuestions, questionGroupDetailPage.getMandatoryQuestions(sectionName));
    }

    private CreateQuestionGroupPage getCreateQuestionGroupPage(AdminPage adminPage) {
        return adminPage.navigateToCreateQuestionGroupPage().verifyPage();
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
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), qgTitle2, APPLIES_TO_CREATE_CLIENT, "", asList(qTitle1, qTitle3), asList(qTitle2));
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), qgTitle2, "Create Client", "Hello", asList(qTitle2), asList(qTitle1, qTitle3));
    }

    private void testCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage, String title, String appliesTo,
                                         String sectionName, List<String> questionsToSelect, List<String> questionsNotToSelect) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(title);
        parameters.setAppliesTo(appliesTo);
        parameters.setSectionName(sectionName);
        parameters.setQuestions(questionsToSelect);
        createQuestionGroupPage.addSection(parameters);
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(questionsToSelect);
        assertPage(CreateQuestionGroupPage.PAGE_ID);
        assertTrue(createQuestionGroupPage.getAvailableQuestions().containsAll(questionsNotToSelect));
        createQuestionGroupPage.submit(parameters);
        assertPage(AdminPage.PAGE_ID);
    }

}

