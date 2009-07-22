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
 
package org.mifos.test.acceptance.framework.center;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.loan.ClosedAccountsPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CenterViewDetailsPage extends MifosPage {

    public CenterViewDetailsPage(Selenium selenium) {
        super(selenium);
    }
    
    public String getCenterName() {
        return selenium.getText("viewCenterDetails.text.displayName");
    }

    public String getStatus() {
        return selenium.getText("viewCenterDetails.text.status");
    }
    
    public String getLoanOfficer() {
        return selenium.getText("viewCenterDetails.text.loanOfficer");
    }

    public void verifyActiveCenter(SubmitFormParameters formParameters) {
        Assert.assertEquals(getCenterName(), formParameters.getCenterName());
        Assert.assertEquals(getStatus(), "Active");
        Assert.assertEquals(getLoanOfficer(), formParameters.getLoanOfficer());
    }

    public CenterViewDetailsPage verifyPage() {
        verifyPage("CenterDetails");
        return this;
    }
    
    public ClosedAccountsPage navigateToClosedAccountsPage() {
        selenium.click("viewCenterDetails.link.viewAllClosedAccounts");
        waitForPageToLoad();
        return new ClosedAccountsPage(selenium);
    }
}
