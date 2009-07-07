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

package org.mifos.test.acceptance.framework.loan;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;


public class EditLoanAccountStatusPage extends MifosPage {
    public EditLoanAccountStatusPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("ChangeStatus");
    }
    
    public EditAccountStatusConfirmationPage submitAndNavigateToEditStatusConfirmationPage(EditLoanAccountStatusParameters params) {
        /* usually this would be an "id=..." locator but since this is a radio button we have to use
           name + value (id + value is not supported by Selenium). */
        selenium.check("name=newStatusId value=" + params.getStatusValue());        
        
        selenium.fireEvent("name=newStatusId value=10", "click");
        
        this.selectIfNotEmpty("change_status.input.cancel_reason", params.getCancelReason());
        selenium.type("change_status.input.note", params.getNote());
        
        selenium.click("change_status.button.submit");
        
        waitForPageToLoad();
        return new EditAccountStatusConfirmationPage(selenium);
    }
}
