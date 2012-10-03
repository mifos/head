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

package org.mifos.test.acceptance.framework.loanproduct;

import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

import java.util.Arrays;

public class DefineProductMixPage extends MifosPage {

    public DefineProductMixPage(Selenium selenium) {
        super(selenium);
        verifyPage("createProductsMix");
    }

    public ClientsAndAccountsHomepage createOneMixAndNavigateToClientsAndAccounts(String prod1, String prod2) {
        if (selenium.getSelectedLabel("productTypeId").equalsIgnoreCase("--Select--")) {
            selenium.select("productTypeId", "Loan");
            waitForPageToLoad();
        }

        if (selenium.getSelectedLabel("productId").equalsIgnoreCase("--Select--")) {
            selenium.select("productId", prod1);
            waitForPageToLoad();
        }
        
        if (!Arrays.asList(selenium.getSelectOptions("notAllowed")).contains(prod2))
        {
            selenium.select("allowed", prod2);
        }
        selenium.click("defineProductMix.button.remove");
        selenium.click("holiday.button.preview");
        waitForPageToLoad();
        DefineProductMixPreviewPage defineProductMixPreviewPage = new DefineProductMixPreviewPage(selenium);

        return defineProductMixPreviewPage.submit().navigateToClientsAndAccountsUsingHeaderTab();
    }
}
