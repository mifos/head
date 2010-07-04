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

import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"client", "acceptance", "ui", "smoke"}, enabled = false)
public class QuestionGroupTest extends UiTestCaseBase {
    private AppLauncher appLauncher;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml.zip";
    private String title1;
    private String title2;
    private static final String TITLE_MISSING = "Please specify the title.";
    private static final String APPLIES_TO_MISSING = "Please choose a valid 'Applies To' value";
    private static final String SECTION_MISSING = "Please specify at least one question or section";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        title1 = "Title1 " + System.currentTimeMillis();
        title2 = "Title2 " + System.currentTimeMillis();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void createQuestionGroup() {
        AdminPage adminPage = getAdminPage();
        CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);

        testMissingMandatoryInputs(createQuestionGroupPage);
        testCreateQuestionGroup(createQuestionGroupPage, title1, "Create Client", "Default");
        testShouldAllowDuplicateTitlesForQuestionGroup();
        testCancelCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)));

        ViewAllQuestionGroupsPage viewQuestionGroupsPage = getViewQuestionGroupsPage(new AdminPage(selenium));
        testViewQuestionGroups(viewQuestionGroupsPage);
        testQuestionGroupDetail(viewQuestionGroupsPage);
    }

    private void testMissingMandatoryInputs(CreateQuestionGroupPage createQuestionGroupPage) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        createQuestionGroupPage.submit(parameters);
        assertTextFoundOnPage(TITLE_MISSING);
        assertTextFoundOnPage(APPLIES_TO_MISSING);
        assertTextFoundOnPage(SECTION_MISSING);
    }

    private void testCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage, String title, String appliesTo, String sectionName) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(title);
        parameters.setAppliesTo(appliesTo);
        parameters.setSectionName(sectionName);
        createQuestionGroupPage.addSection(parameters);
        assertPage(CreateQuestionGroupPage.PAGE_ID);
        createQuestionGroupPage.submit(parameters);
        assertPage(AdminPage.PAGE_ID);
    }

    private void testCancelCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage) {
        createQuestionGroupPage.cancel();
        assertPage(AdminPage.PAGE_ID);
    }

    private void testQuestionGroupDetail(ViewAllQuestionGroupsPage viewAllQuestionGroupsPage) {
        QuestionGroupDetailPage questionGroupDetailPage = viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage(title1);
        questionGroupDetailPage.verifyPage();
        assertTextFoundOnPage(title1);
    }

    private CreateQuestionGroupPage getCreateQuestionGroupPage(AdminPage adminPage) {
        return adminPage.navigateToCreateQuestionGroupPage().verifyPage();
    }

    private void testViewQuestionGroups(ViewAllQuestionGroupsPage viewQuestionGroupsPage) {
        String[] questionGroups = viewQuestionGroupsPage.getAllQuestionGroups();
        assertEquals(3, questionGroups.length);
        assertEquals(title1, questionGroups[0]);
        assertEquals(title2, questionGroups[1]);
        assertEquals(title2, questionGroups[2]);
    }

    private ViewAllQuestionGroupsPage getViewQuestionGroupsPage(AdminPage adminPage) {
        return adminPage.navigateToViewAllQuestionGroups().verifyPage();
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage().verifyPage();
    }

    private void testShouldAllowDuplicateTitlesForQuestionGroup() {
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), title2, "View Client", "Hello");
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), title2, "Create Client", "World");
    }

}

class CreateQuestionGroupParameters {
    private String title = "";
    private String appliesTo = "--select one--";
    private String sectionName = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
