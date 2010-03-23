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

package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.loan.ClosedAccountsPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class GroupViewDetailsPage extends MifosPage {
    public GroupViewDetailsPage() {
        super();
    }

    public GroupViewDetailsPage(Selenium selenium) {
        super(selenium);
    }

    public GroupViewDetailsPage verifyPage() {
        verifyPage("ViewGroupDetails");
        return this;
    }

    public void verifyStatus(String status) {
        Assert.assertTrue(selenium.isTextPresent(status), "Expected string: " + status);
    }

    public CenterSearchTransferGroupPage editCenterMembership() {
        selenium.click("viewgroupdetails.link.editCenterMembership");
        waitForPageToLoad();
        return new CenterSearchTransferGroupPage(selenium);
    }

    public void verifyLoanOfficer(String loanOfficer) {
        Assert.assertTrue(selenium.isTextPresent(loanOfficer), "Expected string: " + loanOfficer);

    }

    public ClosedAccountsPage navigateToClosedAccountsPage() {
        selenium.click("viewgroupdetails.link.viewAllClosedAccounts");
        waitForPageToLoad();
        return new ClosedAccountsPage(selenium);
    }

    public EditGroupStatusPage navigateToEditGroupStatusPage() {
        selenium.click("viewgroupdetails.link.editStatus");
        waitForPageToLoad();
        return new EditGroupStatusPage(selenium);
    }

    public HistoricalDataPage navigateToHistoricalDataPage() {
        selenium.click("viewgroupdetails.link.viewHistoricalData");
        waitForPageToLoad();
        return new HistoricalDataPage(selenium);
    }

    public ChangeLogPage navigateToChangeLogPage() {
        selenium.click("viewgroupdetails.link.viewChangeLog");
        waitForPageToLoad();
        return new ChangeLogPage(selenium);
    }

    public EditCenterMembershipSearchPage navigateToEditCenterMembership() {
        selenium.click("viewgroupdetails.link.editCenterMembership");
        waitForPageToLoad();
        return new EditCenterMembershipSearchPage(selenium);
    }

    public AttachSurveyPage navigateToAttachSurveyPage() {
        selenium.click("viewgroupdetails.link.attachSurvey");
        waitForPageToLoad();
        return new AttachSurveyPage(selenium);
    }

}
