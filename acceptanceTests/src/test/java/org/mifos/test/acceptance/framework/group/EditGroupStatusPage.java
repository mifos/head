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

package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;


public class EditGroupStatusPage extends MifosPage {
    public EditGroupStatusPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("CustomerChangeStatus");
    }

    public EditGroupStatusConfirmationPage submitAndNavigateToEditStatusConfirmationPage(EditGroupStatusParameters params) {
        /* usually this would be an "id=..." locator but since this is a radio button we have to use
           name + value (id + value is not supported by Selenium). */
        selenium.check("name=newStatusId value=" + params.getStatus());

        // simulate a click on "Closed", enabling the drop down for choosing a reason
        selenium.fireEvent("name=newStatusId value=12", "click");

        this.selectIfNotEmpty("customerchangeStatus.input.cancel_reason", params.getCancelReason());
        selenium.type("customerchangeStatus.input.notes", params.getNote());

        selenium.click("customerchangeStatus.button.preview");

        waitForPageToLoad();
        return new EditGroupStatusConfirmationPage(selenium);
    }
}