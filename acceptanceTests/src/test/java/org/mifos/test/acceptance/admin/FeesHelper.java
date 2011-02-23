/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.admin.ViewFeesPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;

public class FeesHelper {
    private final NavigationHelper navigationHelper;

    public FeesHelper(NavigationHelper navigationHelper) {
        this.navigationHelper = navigationHelper;
    }

    public void defineFees(FeesCreatePage.SubmitFormParameters feeParameters) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.defineNewFees(feeParameters);
    }

    public FeesCreatePage.SubmitFormParameters getFeeParameters(String feeName, String categoryType, boolean defaultFees,
                                                                int frequencyType, int amount, int glCode) {
        FeesCreatePage.SubmitFormParameters formParameters = new FeesCreatePage.SubmitFormParameters();
        formParameters.setFeeName(feeName);
        formParameters.setCategoryType(categoryType);
        formParameters.setDefaultFees(defaultFees);
        formParameters.setFeeFrequencyType(frequencyType);
        formParameters.setAmount(amount);
        formParameters.setGlCode(glCode);
        return formParameters;
    }

    public void viewClientFees(String expectedClientFees) {
        ViewFeesPage viewFeesPage = navigateToViewFeesPage();
        viewFeesPage.verifyClientFees(expectedClientFees);
    }

    public void viewProductFees(String expectedProductFees) {
        ViewFeesPage viewFeesPage = navigateToViewFeesPage();
        viewFeesPage.verifyProductFees(expectedProductFees);
    }

    private ViewFeesPage navigateToViewFeesPage() {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        return adminPage.navigateToViewFeesPage();
    }
}
