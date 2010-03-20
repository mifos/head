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

package org.mifos.test.acceptance.framework.reports;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * Encapsulates the GUI based actions that can
 * be done from the Reports page.
 *
 */
public class ReportsPage extends MifosPage {

    public ReportsPage() {
        super();
    }

    public ReportsPage(Selenium selenium) {
        super(selenium);
    }

    public CollectionSheetReportParametersPage selectCollectionSheetEntryReport() {
        selenium.click("link=Collection Sheet Report");
        waitForPageToLoad();
        return new CollectionSheetReportParametersPage(selenium);
    }

    public ReportsPage verifyPage() {
        Assert.assertTrue(selenium.isTextPresent("Welcome to mifos reports area. Click on a report name below to view the report ."));
        return this;
    }
}
