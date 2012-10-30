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

package org.mifos.test.acceptance.framework.savings;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class SavingsApplyTransferPage extends MifosPage {
    public SavingsApplyTransferPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("FundTrasferCustomerSearch");
    }

    public SavingFundTransferCustomerSelect navigateToBeneficientSearch(String beneficent) {
        verifyPage();
        selenium.type("cust_search_transfer.input.searchString", beneficent);
        selenium.click("cust_search_transfer.button.search");
        
        return new SavingFundTransferCustomerSelect(selenium);
    }
    
    public void verifyIsAccessDeniedMessageDisplayed() {
        verifyPage("admin");
        Assert.assertEquals(selenium.getText("accessDeniedHeading"), "Access Denied");
        Assert.assertEquals(selenium.getText("accessDeniedMessage"), "You are not allowed to access this page.");
    }
}
