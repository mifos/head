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
import com.thoughtworks.selenium.Selenium;

public class DefineNewOfficePage extends MifosPage {

    public DefineNewOfficePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("CreateNewOffice");
    }

    public void setOfficeName(String officeName) {
        selenium.type("CreateNewOffice.input.officeName", officeName);
    }

    public void setOfficeShortName(String officeShortName) {
        selenium.type("CreateNewOffice.input.shortName", officeShortName);
    }

    public void setOfficeType(String officeShortName) {
        selenium.select("officeLevel", "label=" + officeShortName);
        waitForPageToLoad();
    }

    public void setParentOffice(String parentOffice) {
        selenium.select("parentOfficeId", "label=" + parentOffice);
    }

    public void preview() {
        selenium.click("CreateNewOffice.button.preview");
        waitForPageToLoad();
    }

    public void submit() {
        selenium.click("preview_new_office.button.submit");
        waitForPageToLoad();
    }
}
