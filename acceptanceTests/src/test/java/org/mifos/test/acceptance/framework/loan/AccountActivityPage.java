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
package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

public class AccountActivityPage extends MifosPage{

    public AccountActivityPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ViewLoanAccountActivity");
    }

    public String getRunningTotal() {
        return selenium.getTable("accountActivityTable.2.11").trim();
    }

    public String getRunningFees() {
        return selenium.getTable("accountActivityTable.2.10").trim();
    }

    public String getRunningInterest() {
        return selenium.getTable("accountActivityTable.2.9").trim();
    }

    public String getRunningPrinciple() {
        return selenium.getTable("accountActivityTable.2.8").trim();
    }

    public String getLastTotalPaid() {
        return selenium.getTable("accountActivityTable.2.6").trim();
    }

    public String getLastPenaltyPaid() {
        return selenium.getTable("accountActivityTable.2.5").trim();
    }

    public String getLastFeePaid() {
        return selenium.getTable("accountActivityTable.2.4");
    }

    public String getLastInterestPaid() {
        return selenium.getTable("accountActivityTable.2.3").trim();
    }

    public String getLastPrinciplePaid() {
        return selenium.getTable("accountActivityTable.2.2").trim();
    }

    public void verifyLastTotalPaid(String amount){
        Assert.assertEquals(Double.parseDouble(getLastTotalPaid()), Double.parseDouble(amount));
    }
}
