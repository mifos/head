/*
 * Copyright Grameen Foundation USA
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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.CreateCenterConfirmationPage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.CreateCenterPreviewDataPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

import com.thoughtworks.selenium.Selenium;

public class CenterTestHelper {
    private final NavigationHelper navigationHelper;
    private final Selenium selenium;

    public CenterTestHelper(Selenium selenium) {
        this.navigationHelper = new NavigationHelper(selenium);
        this.selenium = selenium;
    }

    public CenterViewDetailsPage createCenter(CreateCenterEnterDataPage.SubmitFormParameters formParameters, String officeName) {
        CreateCenterConfirmationPage confirmationPage = navigationHelper
            .navigateToClientsAndAccountsPage()
            .navigateToCreateNewCenterPage()
            .selectOffice(officeName)
            .submitAndGotoCreateCenterPreviewDataPage(formParameters)
            .submit();
        confirmationPage.verifyPage();

        return confirmationPage.navigateToCenterViewDetailsPage();
    }

    public CenterViewDetailsPage createCenterWithQuestionGroupsEdited(CreateCenterEnterDataPage.SubmitFormParameters formParameters, String officeName, QuestionResponseParameters responseParams, QuestionResponseParameters responseParams2) {
        QuestionResponsePage responsePage = navigationHelper
            .navigateToClientsAndAccountsPage()
            .navigateToCreateNewCenterPage()
            .selectOffice(officeName)
            .submitAndNavigateToQuestionResponsePage(formParameters);
        responsePage.populateAnswers(responseParams);
        responsePage.navigateToNextPage();
        new CreateCenterPreviewDataPage(selenium).navigateToEditQuestionResponsePage();
        responsePage.populateAnswers(responseParams2);
        responsePage.navigateToNextPage();

        return new CreateCenterPreviewDataPage(selenium).submit().navigateToCenterViewDetailsPage();
    }

    public QuestionResponsePage navigateToQuestionResponsePageWhenCreatingCenter(CreateCenterEnterDataPage.SubmitFormParameters formParameters, String officeName) {
        QuestionResponsePage responsePage = navigationHelper
            .navigateToClientsAndAccountsPage()
            .navigateToCreateNewCenterPage()
            .selectOffice(officeName)
            .submitAndNavigateToQuestionResponsePage(formParameters);
        return responsePage;
    }

    public CenterViewDetailsPage navigateToCenterViewDetailsPage(String centerName) {
        return navigationHelper.navigateToCenterViewDetailsPage(centerName);
    }
}
