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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.office.CreateOfficeConfirmationPage;
import org.mifos.test.acceptance.framework.office.CreateOfficeEnterDataPage;
import org.mifos.test.acceptance.framework.office.CreateOfficePreviewDataPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.office.OfficeViewDetailsPage;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

public class OfficeHelper {
    private final NavigationHelper navigationHelper;

    public OfficeHelper (Selenium selenium) {
        navigationHelper = new NavigationHelper(selenium);
    }

    /**
     * Creates an office.
     * @param officeParameters The office parameters.
     * @return The admin page.
     */
    public AdminPage createOffice(OfficeParameters officeParameters) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();

        CreateOfficeEnterDataPage officeEnterDataPage = adminPage.navigateToCreateOfficeEnterDataPage();
        officeEnterDataPage.verifyPage();

        CreateOfficePreviewDataPage previewDataPage = officeEnterDataPage.submitAndGotoCreateOfficePreviewDataPage(officeParameters);
        previewDataPage.verifyPage();

        CreateOfficeConfirmationPage confirmationPage = previewDataPage.submit();
        confirmationPage.verifyPage();

        OfficeViewDetailsPage detailsPage = confirmationPage.navigateToOfficeViewDetailsPage();
        detailsPage.verifyPage();

        return detailsPage.navigateToAdminPage();
    }

    public QuestionResponsePage navigateToQuestionResponsePage(OfficeParameters officeParameters) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();

        CreateOfficeEnterDataPage officeEnterDataPage = adminPage.navigateToCreateOfficeEnterDataPage();
        officeEnterDataPage.verifyPage();

        QuestionResponsePage questionResponsePage = officeEnterDataPage.submitAndGotoQuestionResponsePage(officeParameters);
        questionResponsePage.verifyPage();

        return questionResponsePage;
    }

    public OfficeViewDetailsPage changeOfficeStatus(String officeName, String status) {
        return navigationHelper
                .navigateToAdminPage()
                .navigateToViewOfficesPage()
                .navigateToOfficeViewDetailsPage(officeName)
                .navigateToOfficeEditInformationPage()
                .changeStatusAndSubmit(status);
    }

    public void verifyQuestionPresent(String officeName,String question,String... answers) {
        navigationHelper
            .navigateToAdminPage()
            .navigateToViewOfficesPage()
            .navigateToOfficeViewDetailsPage(officeName).
            navigateToViewAdditionalInformation().
            verifyQuestionPresent(question, answers);
    }
}
