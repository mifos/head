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

import org.mifos.test.acceptance.framework.office.ChooseOfficePage;
import org.mifos.test.acceptance.framework.user.CreateUserConfirmationPage;
import org.mifos.test.acceptance.framework.user.CreateUserEnterDataPage;
import org.mifos.test.acceptance.framework.user.CreateUserParameters;
import org.mifos.test.acceptance.framework.user.CreateUserPreviewDataPage;
import org.mifos.test.acceptance.framework.user.EditUserDataPage;
import org.mifos.test.acceptance.framework.user.UserViewDetailsPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class UserHelper {
    NavigationHelper navigationHelper;

    public UserHelper(Selenium selenium) {
        navigationHelper = new NavigationHelper(selenium);
    }

    /**
     * Creates a user.
     * @param userParameters Parameters for the user that is created.
     * @param officeName The name of the office that this user will belong to.
     * @return The user details page for the newly created user.
     */
    public UserViewDetailsPage createUser(CreateUserParameters userParameters, String officeName) {
        ChooseOfficePage chooseOfficePage = navigationHelper.navigateToCreateUserPage();
        CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);

        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.submitAndGotoCreateUserPreviewDataPage(userParameters);
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();
        userConfirmationPage.verifyPage();

        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        userDetailsPage.verifyPage();
        
        if (userParameters.getSecondLastName() == null){
        	Assert.assertTrue(userDetailsPage.getFullName().contains(userParameters.getFirstName() + " " + userParameters.getLastName()));
        }
        
        else if (userParameters.getSecondLastName() != null){
        	Assert.assertTrue(userDetailsPage.getFullName().contains(userParameters.getFirstName() + " " + userParameters.getSecondLastName() + " " + userParameters.getLastName()));
        }
        

        return userDetailsPage;
    }

    public UserViewDetailsPage changeUserStatus(String userName, String status) {
        return navigateToEditUserDataPage(userName)
            .changeStatusAndSubmit(status);
    }

    public EditUserDataPage navigateToEditUserDataPage(String userName) {
        return navigationHelper
            .navigateToAdminPage()
            .navigateToViewSystemUsersPage()
            .searchAndNavigateToUserViewDetailsPage(userName)
            .navigateToEditUserDataPage();
    }
}
