/*
 * Copyright Grameen Foundation USA
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

public class ConfirmParentTransferPage extends MifosPage {

    public ConfirmParentTransferPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ConfirmParentTransfer");
    }

    private void submit(){
        selenium.click("confirmParentTransfer.button.submit");
        waitForPageToLoad();
    }

    public ClientViewDetailsPage submitAddGroup(){
        submit();
        return new ClientViewDetailsPage(selenium);
    }

    public ConfirmParentTransferPage submitAddGroupWithError(){
        submit();
        return new ConfirmParentTransferPage(selenium);
    }
}
