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

public class ViewOrganizationSettingsPage extends MifosPage {
    public ViewOrganizationSettingsPage(Selenium selenium) {
        super(selenium);
    }

    public ViewOrganizationSettingsPage verifyPage() {
        verifyPage("view_organization_settings");
        return this;

    }

    public void verifyFiscalYear(String[] expectedData) {
        for (String expected : expectedData) {
            Assert.assertTrue(selenium.getText("//div[@id='fiscalyear']").contains(expected));
        }
    }

    public void verifyLocale(String[] expectedData) {
        for (String expected : expectedData) {
            Assert.assertTrue(selenium.getText("//div[@id='locale']").contains(expected));
        }
    }

    public void verifyAccountingRules(String[] expectedData) {
        for (String expected : expectedData) {
            Assert.assertTrue(selenium.getText("//div[@id='accountingrules']").contains(expected));
        }
    }

    public void verifyCurrencies(String[] expectedData) {
        for (String expected : expectedData) {
            Assert.assertTrue(selenium.getText("//div[@id='currencies']").contains(expected));
        }
    }

    public void verifyClientRules(String[] expectedData) {
        for (String expected : expectedData) {
            Assert.assertTrue(selenium.getText("//div[@id='clientrules']").contains(expected));
        }
    }

    public void verifyProcessFlow(String[] expectedData) {
        for (String expected : expectedData) {
            Assert.assertTrue(selenium.getText("//div[@id='processflow']").contains(expected));
        }
    }

    public void verifyMiscellaneous(String[] expectedData) {
        for (String expected : expectedData) {
            Assert.assertTrue(selenium.getText("//div[@id='miscellaneous']").contains(expected));
        }
    }

    public void verifyDefaultConfiguration() {
        verifyAccountingRules(getDefaultAccountingRules());
        verifyClientRules(getDefaultClientRules());
        verifyCurrencies(getDefaultCurrencies());
        verifyFiscalYear(getDefaultFiscalYear());
        verifyLocale(getDefaultLocale());
        verifyMiscellaneous(getDefaultMiscellaneous());
        verifyProcessFlow(getDefaultProcessFlow());
    }

    public String[] getDefaultFiscalYear() {
        return new String[] { "Working days: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday",
                "Allow calendar definition for next year:  30 days before end of current year",
                "Start of Week: Monday",
                "Non-working days: Sunday",
                "Meeting in case of non-working day: same_day" };
    }

    public String[] getDefaultFiscalYearValues() {
        return new String[] { "Monday, Tuesday, Wednesday, Thursday, Friday, Saturday",
                "30 days before end of current year", "Monday", "Sunday", "same_day" };
    }

    public String[] getDefaultLocale() {
        return new String[] { "Country: GB", "Language: EN" };
    }

    public String[] getDefaultAccountingRules() {
        return new String[] { "Maximum Interest: 999.0", "Minimum Interest: 0.0",
                "Number of digits before decimal: 14", "Number of digits after decimal for interest: 5",
                "Number of digits before decimal for interest: 10", "Number of interest days: 365",
                "Currency Rounding Mode: HALF_UP", "Initial Rounding Mode: HALF_UP", "Final Rounding Mode: CEILING", 
                "GL names mode: GL Code - GL Name", "Simple accounting module: Yes", "Overdue interest paid first: No" };
    }

    public String[] getDefaultCurrencies() {
        return new String[] { "Currency: INR", "Number of digits after decimal: 1", "Final Round Off Multiple: 1",
                "Initial Round Off Multiple: 1" };
    }

    public String[] getDefaultCurrenciesValues() {
        return new String[] { "INR", "1", "1", "1" };
    }

    public String[] getDefaultClientRules() {
        return new String[] { "Center hierarchy exists: Yes", "Groups allowed to apply for loans: Yes",
                "Client can exist outside group: Yes",
                "Name sequence: first_name, middle_name, last_name, second_last_name", "Age check enabled: No",
                "Minimum allowed age for new clients: 0", "Maximum allowed age for new clients: 0",
                "Additional family details required: No", "Maximum number of family members: 10" };
    }

    public String[] getDefaultProcessFlow() {
        return new String[] { "Client pending approval state enabled: Yes",
                "Group pending approval state enabled: Yes", "Loan pending approval state enabled: Yes",
                "Savings pending approval state enabled: Yes" };
    }

    public String[] getDefaultMiscellaneous() {
        return new String[] { "Session timeout: 30",
                "Back dated transactions allowed: Yes", "Back dated approvals allowed: Yes",
                "Group loan with individual monitoring (GLIM): No",
                "Loan schedule independent of meeting (LSIM): No" };
    }

}
