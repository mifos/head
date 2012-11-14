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

package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;

public class CreateClientPreviewDataPage extends MifosPage {

    public CreateClientPreviewDataPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("PreviewClientPersonalInfo");
    }

    public CreateClientConfirmationPage submit() {
        clickSubmit();
        return new CreateClientConfirmationPage(selenium);
    }

    public void submitWithOneError(String msg) {
        clickSubmit();
        Assert.assertEquals(selenium.getText("//span[@id='preview_ClientDetails.error.message']/li"), msg);
    }

    public CreateClientConfirmationPage saveForLater() {
        selenium.click("preview_ClientDetails.button.saveForLater");
        waitForPageToLoad();
        return new CreateClientConfirmationPage(selenium);
    }

    public CreateClientEnterFamilyDetailsPage edit() {
        selenium.click("preview_ClientDetails.button.editFamilyInformation");
        waitForPageToLoad();
        return new CreateClientEnterFamilyDetailsPage(selenium);
    }

    private void clickSubmit() {
        if(selenium.isElementPresent("preview_ClientDetails.button.submitForApproval")) {
            selenium.click("preview_ClientDetails.button.submitForApproval");
        }
        else {
            selenium.click("preview_ClientDetails.button.approve");
        }
        waitForPageToLoad();
    }
    
    public String getMeetingSchedule(){
        return selenium.getText("preview_ClientDetails.text.meetingSchedule");
    }

    public String getMeetingPlace(){
        return selenium.getText("preview_ClientDetails.text.meetingPlace");
    }
}
