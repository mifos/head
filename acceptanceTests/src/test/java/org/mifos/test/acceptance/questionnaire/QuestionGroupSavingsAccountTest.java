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

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.questionnaire.AttachQuestionGroupParameters;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(groups = {"client", "acceptance", "ui"})
public class QuestionGroupSavingsAccountTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private QuestionGroupTestHelper questionGroupTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Attaching a Question Group to a Savings Account and capturing responses
     * http://mifosforge.jira.com/browse/MIFOSTEST-659
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToSavingsAccount() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
        attachParams.setTarget("000100000000015");
        attachParams.setQuestionGroupName("ViewSavingsQG");
        attachParams.addTextResponse("DateQuestion", "09/02/2011");
        attachParams.addTextResponse("NumberQuestion", "10");
        attachParams.addTextResponse("NumberQuestion2", "13");
        attachParams.addTextResponse("question 2", "22");
        attachParams.addCheckResponse("question 4", "yes");

        AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
        attachErrorParams.setTarget("000100000000015");
        attachErrorParams.setQuestionGroupName("ViewSavingsQG");
        attachErrorParams.addTextResponse("NumberQuestion", "qwerty");
        attachErrorParams.addTextResponse("NumberQuestion2", "qwerty");
        attachErrorParams.addTextResponse("question 2", "test");
        attachErrorParams.addCheckResponse("question 4", "yes");
        attachErrorParams.addError("Please specify DateQuestion");
        attachErrorParams.addError("Please specify a number for NumberQuestion");
        attachErrorParams.addError("Please specify a number between 5 and 15 for NumberQuestion2");
        attachErrorParams.addError("Please specify a number for question 2");
        //When
        questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToSavingsAccount(attachErrorParams);
        questionGroupTestHelper.attachQuestionGroupToSavingsAccount(attachParams);
        attachParams.addTextResponse("NumberQuestion", "13");
        attachParams.addTextResponse("NumberQuestion2", "10");
        //Then
        questionGroupTestHelper.editQuestionGroupResponsesInSavingsAccount(attachParams);
    }
}