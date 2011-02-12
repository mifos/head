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
package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.admin.EditFundPage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ViewFundsPage extends MifosPage{
    public ViewFundsPage(Selenium selenium) {
        super(selenium);
    }

    public ViewFundsPage verifyPage() {
        verifyPage("viewFunds");
        return this;
    }


    public void verifyFundNameAndCode(String[] expectedFundNames, String[] expectedFundCodes) {

        for (int i = 0; i < expectedFundNames.length; i++) {
            String expectedFundNameCell = expectedFundNames[i];
            String expectedFundCodeCell = expectedFundCodes[i];
            String actualFundName = selenium.getTable("fundDisplayTable."+(i+1)+".0");
            String actualFundCode = selenium.getTable("fundDisplayTable."+(i+1)+".1");
            Assert.assertEquals(actualFundName, expectedFundNameCell);
            Assert.assertEquals(actualFundCode, expectedFundCodeCell);
        }
    }
    public EditFundPage editAndNavigateToEditFundPage(){
        selenium.click("//*[@id=\"fundDisplayTable\"]/tbody/tr[7]/td[3]/a[1]");
        waitForPageToLoad();
        return new EditFundPage(selenium);
    }
}


