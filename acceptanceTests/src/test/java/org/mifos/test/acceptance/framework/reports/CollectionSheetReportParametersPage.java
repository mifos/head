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

package org.mifos.test.acceptance.framework.reports;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.util.UiTestUtils;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetReportParametersPage extends MifosPage {

    public CollectionSheetReportParametersPage() {
        super();
    }

    public CollectionSheetReportParametersPage(Selenium selenium) {
        super(selenium);
    }

    public CollectionSheetReportPage generateCollectionSheetEntryReport(SubmitFormParameters formParameters) {
        selenium.select("branchId_selection", formParameters.getBranch());
        UiTestUtils.sleep(1000);
        selenium.select("loanOfficerId_selection", formParameters.getLoanOfficer());
        UiTestUtils.sleep(1000);
        selenium.select("centerId_selection", formParameters.getCenter());
        UiTestUtils.sleep(1000);
        selenium.type("meetingDate", formParameters.getTransactionDay() + "/" +
                                     formParameters.getTransactionMonth() + "/" +
                                     formParameters.getTransactionYear());
        selenium.click("//input[@value='OK']");
        waitForPageToLoad();
        return new CollectionSheetReportPage(selenium);
    }

    public CollectionSheetReportParametersPage verifyPage() {
        Assert.assertTrue(selenium.isTextPresent("Select"));
        return this;
    }
}
