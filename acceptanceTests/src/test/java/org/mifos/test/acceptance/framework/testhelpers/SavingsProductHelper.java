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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductConfirmationPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPreviewPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;

import com.thoughtworks.selenium.Selenium;

public class SavingsProductHelper {
    private final NavigationHelper navigationHelper;

    public SavingsProductHelper(Selenium selenium) {
        navigationHelper = new NavigationHelper(selenium);
    }

    public void createSavingsProduct(SavingsProductParameters productParameters) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();

        DefineNewSavingsProductPage newSavingsProductPage = adminPage.navigateToDefineSavingsProduct();
        newSavingsProductPage.verifyPage();

        DefineNewSavingsProductPreviewPage newSavingsProductPreviewPage = newSavingsProductPage.submitAndNavigateToDefineNewSavingsProductPreviewPage(productParameters);
        newSavingsProductPreviewPage.verifyPage();

        DefineNewSavingsProductConfirmationPage newSavingsProductConfirmationPage = newSavingsProductPreviewPage.submitAndNavigateToDefineNewSavingsProductConfirmationPage();
        newSavingsProductConfirmationPage.verifyPage();
    }

}
