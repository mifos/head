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
import org.mifos.test.acceptance.framework.admin.ViewAllQuestionGroupsPage;
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
    private CreateQuestionGroupParameters createQuestionGroupParameters;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        title1 = "Title1 " + System.currentTimeMillis();
        title2 = "Title2 " + System.currentTimeMillis();
        createQuestionGroupParameters = new CreateQuestionGroupParameters();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void createQuestionGroup() {
        AdminPage adminPage = getAdminPage();
        CreateQuestionGroupPage createQuestionGroupPage = testMissingTitle(adminPage);
        adminPage = testCreateQuestionGroup(createQuestionGroupPage, title1);
        createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
        adminPage = testShouldAllowDuplicateTitlesForQuestionGroup(createQuestionGroupPage);
        ViewAllQuestionGroupsPage viewAllQuestionGroupsPage = testViewQuestionGroups(adminPage);
        testQuestionGroupDetail(viewAllQuestionGroupsPage);
    }

    private void testQuestionGroupDetail(ViewAllQuestionGroupsPage viewAllQuestionGroupsPage) {
        viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage("questionGroupId_1").verify();
        assertTextFoundOnPage(title1);
    }

    private CreateQuestionGroupPage getCreateQuestionGroupPage(AdminPage adminPage) {
        return adminPage.navigateToCreateQuestionGroupPage().verifyPage();
    }

    private ViewAllQuestionGroupsPage testViewQuestionGroups(AdminPage adminPage) {
        ViewAllQuestionGroupsPage viewQuestionGroupsPage = getViewQuestionGroupsPage(adminPage);
        Assert.assertEquals(title1, selenium.getTable("questionGroups.table.0.1"));
        Assert.assertEquals(title2, selenium.getTable("questionGroups.table.1.1"));
        Assert.assertEquals(title2, selenium.getTable("questionGroups.table.2.1"));
        return viewQuestionGroupsPage;
    }

    private ViewAllQuestionGroupsPage getViewQuestionGroupsPage(AdminPage adminPage) {
        return adminPage.navigateToViewAllQuestionGroups().verify();
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage();
    }

    private AdminPage testCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage, String title) {
        createQuestionGroupParameters.setTitle(title);
        return createQuestionGroupPage.submit(createQuestionGroupParameters).verifyPage();
    }

    private AdminPage testShouldAllowDuplicateTitlesForQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage) {
        AdminPage adminPage = testCreateQuestionGroup(createQuestionGroupPage, title2);
        return testCreateQuestionGroup(getCreateQuestionGroupPage(adminPage), title2);
    }

    private CreateQuestionGroupPage testMissingTitle(AdminPage adminPage) {
        CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
        createQuestionGroupParameters.setTitle("");
        createQuestionGroupPage.submit(createQuestionGroupParameters);
        assertTextFoundOnPage(TITLE_MISSING);
        return createQuestionGroupPage;
    }

}

class CreateQuestionGroupParameters {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
