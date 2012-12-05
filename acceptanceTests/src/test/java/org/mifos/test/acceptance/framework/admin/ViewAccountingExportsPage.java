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

package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ViewAccountingExportsPage extends MifosPage {

    public ViewAccountingExportsPage(Selenium selenium) {
        super(selenium);
    }

    public ViewAccountingExportsPage verifyPage() {
        verifyPage("view_accounting_data_exports");
        return this;
    }

    public ViewAccountingExportsPage verifyExportsListExists() {
        verifyPage();
        Assert.assertTrue(selenium.isElementPresent("table"),"Accounting exports list table ");
        return this;
    }

    public ViewAccountingExportsPage verifyNoExportPresent() {
        selenium.isTextPresent("NO DATA");
        return this;
    }
    
    public ViewAccountingDataDetailPage navigateToViewAccountingDataDetail(String date){
        selenium.click("render.date="+date);
        waitForPageToLoad();
        return new ViewAccountingDataDetailPage(selenium);
    }

    public ViewAccountingExportsPage clickCancel() {
        selenium.click("cancel");
        waitForPageToLoad();
        return this;
    }

    public ViewAccountingExportsPage clickSubmit() {
        selenium.click("submit");
        waitForPageToLoad();
        return this;
    }

    public ViewAccountingExportsPage clickClearExports() {
        selenium.click("clearexport");
        waitForPageToLoad();
        return this;
    }

    public ViewAccountingExportsPage verifyConfirmationPage() {
        verifyPage("confirm_clear_exports");
        return this;

    }

}
