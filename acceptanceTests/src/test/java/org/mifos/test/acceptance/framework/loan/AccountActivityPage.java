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
package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.util.StringUtil;
import org.testng.Assert;

public class AccountActivityPage extends MifosPage{

    public AccountActivityPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ViewLoanAccountActivity");
    }

    public String getRunningTotal(int row) {
        return selenium.getTable("accountActivityTable." + row + ".11").trim();
    }

    public String getRunningFees(int row) {
        return selenium.getTable("accountActivityTable." + row + ".10").trim();
    }

    public String getRunningInterest(int row) {
        return selenium.getTable("accountActivityTable." + row + ".9").trim();
    }

    public String getRunningPrinciple(int row) {
        return selenium.getTable("accountActivityTable." + row + ".8").trim();
    }

    public String getLastTotalPaid(int row) {
        return selenium.getTable("accountActivityTable." + row + ".6").trim();
    }

    public String getLastPenalty(int row) {
        return selenium.getTable("accountActivityTable." + row + ".5").trim();
    }

    public String getLastFeePaid(int row) {
        return selenium.getTable("accountActivityTable." + row + ".4");
    }

    public String getLastInterestPaid(int row) {
        return selenium.getTable("accountActivityTable." + row + ".3").trim();
    }

    public String getLastPrinciplePaid(int row) {
        return selenium.getTable("accountActivityTable." + row + ".2").trim();
    }

    public void verifyLastTotalPaid(String amount, int row){
        Assert.assertEquals(getLastTotalPaid(row), StringUtil.formatNumber(amount));
    }

    public String getActivity(int row) {
        return selenium.getTable("accountActivityTable." + row + ".1").trim();
    }
}
