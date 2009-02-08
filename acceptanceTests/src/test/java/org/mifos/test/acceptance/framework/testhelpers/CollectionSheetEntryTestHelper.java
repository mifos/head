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

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.LoginPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;

import com.thoughtworks.selenium.Selenium;

/**
 * Holds common methods used for CollectionSheetEntry tests.
 *
 */
public class CollectionSheetEntryTestHelper {

    private final Selenium selenium;

    public CollectionSheetEntryTestHelper(Selenium selenium) {
        this.selenium = selenium;
    }
    
    public CollectionSheetEntrySelectPage loginAndNavigateToCollectionSheetEntrySelectPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
    }

    
}
