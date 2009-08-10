/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import org.mifos.test.acceptance.framework.user.UserViewDetailsPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class UserHelper {
    NavigationHelper navigationHelper;
    
    public UserHelper(Selenium selenium) {
        navigationHelper = new NavigationHelper(selenium);
    }
    
    public UserViewDetailsPage createUser(CreateUserParameters formParameters, String officeName) {
        ChooseOfficePage chooseOfficePage = navigationHelper.navigateToCreateUserPage();
        CreateUserEnterDataPage userEnterDataPage = chooseOfficePage.selectOffice(officeName);

        CreateUserPreviewDataPage userPreviewDataPage = userEnterDataPage.submitAndGotoCreateUserPreviewDataPage(formParameters);
        CreateUserConfirmationPage userConfirmationPage = userPreviewDataPage.submit();
        userConfirmationPage.verifyPage();
        
        UserViewDetailsPage userDetailsPage = userConfirmationPage.navigateToUserViewDetailsPage();
        userDetailsPage.verifyPage();
        Assert.assertTrue(userDetailsPage.getFullName().contains(formParameters.getFirstName() + " " + formParameters.getLastName()));

        return userDetailsPage;
    }
}
