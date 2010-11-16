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

package org.mifos.test.acceptance.framework.user;

import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class UserViewDetailsPage extends MifosPage {

    public UserViewDetailsPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("personneldetails");
    }

    public EditUserDataPage navigateToEditUserDataPage() {
        selenium.click("personneldetails.link.editUser");
        waitForPageToLoad();
        return new EditUserDataPage(selenium);
    }

    public ClientsAndAccountsHomepage navigateToClientsAndAccountsHomepage() {
        selenium.click("header.link.clientsAndAccounts");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }

    public String getFullName() {
        return selenium.getText("personneldetails.text.fullName");
    }

    public String getEmail() {
        return selenium.getText("personneldetails.text.email");
    }

    public String getStatus() {
        return selenium.getText("personneldetails.text.status");
    }

    public void verifyModifiedNameAndEmail(CreateUserParameters formParameters) {
        Assert.assertTrue(getFullName().contains(
                formParameters.getFirstName() + " " + formParameters.getLastName()));
        Assert.assertEquals(getEmail(), formParameters.getEmail());
    }

    public void verifyVariableInstalmentOptions(String maximumGap, String minGap, String minimumInstalmentAmount) {
        Assert.assertTrue(selenium.isTextPresent("Minimum gap between installments: " + minGap));
        if ("".equals(maximumGap)) {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: " + maximumGap  + " days"));
        }
        if ("".equals(minimumInstalmentAmount)) {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: N/A")) ;
        } else {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: " + minimumInstalmentAmount)) ;
        }
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: Yes"));
    }

    public UserViewDetailsPage verifyVariableInstalmentOptionsUnChecked() {
        Assert.assertTrue(!selenium.isTextPresent("Minimum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Maximum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Minimum installment amount:" )) ;
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: No"));
        return this;
    }

    public UserViewDetailsPage verifyCashFlowInViewLoanProcutPage(String warningThreshold) {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: Yes"));
        if ("".equals(warningThreshold)) {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: " + warningThreshold + " %"));
        }
        return this;
    }

    public UserViewDetailsPage verifyCashFlowUnCheckedIn() {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: No"));
        Assert.assertTrue(!selenium.isTextPresent("Warning Threshold:"));
        return this;
        
    }

    public UserViewDetailsPage verifyInterestTypeInSummary(String interestType) {
        Assert.assertTrue(selenium.isTextPresent("Interest rate type: " + interestType));
        return this;
    }
}