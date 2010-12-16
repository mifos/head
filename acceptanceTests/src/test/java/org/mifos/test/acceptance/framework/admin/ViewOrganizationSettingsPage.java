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

package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ViewOrganizationSettingsPage extends MifosPage {
    public ViewOrganizationSettingsPage(Selenium selenium) {
        super(selenium);
    }

    public ViewOrganizationSettingsPage verifyPage() {
        verifyPage("view_organization_settings");
        return this;

    }


    public void verifyFiscalYear(String[] expectedData) {
        for (String expected_FiscalYear : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expected_FiscalYear), "Expected settings in Fiscal year category: " + expected_FiscalYear);
        }
    }

    public void verifyLocale(String[] expectedData) {
        for (String expected_Locale : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expected_Locale), "Expected settings in Locale category: " + expected_Locale);
        }
    }

    public void verifyAccountingRules(String[] expectedData) {
        for (String expected_AccountingRules : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expected_AccountingRules), "Expected settings in Accounting rules category: " + expected_AccountingRules);
        }
    }

    public void verifyCurrencies(String[] expectedData) {
        for (String expected_Currencies : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expected_Currencies), "Expected settings in Currencies category: " + expected_Currencies);
        }
    }

    public void verifyClientRules(String[] expectedData) {
        for (String expected_ClientRules : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expected_ClientRules), "Expected settings in Client rules category: " + expected_ClientRules);
        }
    }

    public void verifyProcessFlow(String[] expectedData) {
        for (String expected_ProcessFlow : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expected_ProcessFlow), "Expected settings in Process flow category: " + expected_ProcessFlow);
        }
    }

    public void verifyMiscellaneous(String[] expectedData) {
        for (String expected_Miscellaneous : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expected_Miscellaneous), "Expected settings in Miscellaneous category: " + expected_Miscellaneous);
        }
    }

}


