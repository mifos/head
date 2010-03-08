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

package org.mifos.test.acceptance.framework.office;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.user.CreateUserEnterDataPage;

import com.thoughtworks.selenium.Selenium;

public class ChooseOfficePage extends MifosPage {

    public ChooseOfficePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("choose_office");
    }

    public OfficeViewDetailsPage navigateToOfficeViewDetailsPage() {
        selenium.click("createNewOfficeSuccessful.link.viewOfficeDetails");
        waitForPageToLoad();
        return new OfficeViewDetailsPage(selenium);
    }

    public CreateUserEnterDataPage selectOffice(String officeName) {
        selenium.click("link=" + officeName);
        waitForPageToLoad();
        return new CreateUserEnterDataPage(selenium);
    }
}
