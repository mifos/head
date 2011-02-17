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

package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import com.thoughtworks.selenium.Selenium;

public class DefineAcceptedPaymentTypesPage extends MifosPage {
    public DefineAcceptedPaymentTypesPage(Selenium selenium) {
        super(selenium);
    }

    public static final int CASH = 1;
    public static final int VOUCHER = 2;
    public static final int CHEQUE = 3;

    public void addLoanFeesPaymentType(int payment) {
        selenium.select("chosenNonAcceptedFees", "value=" + payment);
        selenium.click("defineAcceptedPaymentTypes.button.addFees");
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    public void addLoanDisbursementsPaymentType(int payment) {
        selenium.select("chosenNonAcceptedLoanDisbursements", "value=" + payment);
        selenium.click("defineAcceptedPaymentTypes.button.addLoans");
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    public void addLoanRepaymentsPaymentType(int payment)
    {
        selenium.select("chosenNonAcceptedLoanRepayments", "value=" + payment);
        selenium.click("defineAcceptedPaymentTypes.button.addRepayments");
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    public void addSavingsWithdrawalsType(int payment)
    {
        selenium.select("chosenNonAcceptedSavingWithdrawals", "value=" + payment);
        selenium.click("defineAcceptedPaymentTypes.button.addWithdrawals");
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }
    public void addSavingsDepositsPaymentType(int payment)
    {
        selenium.select("chosenNonAcceptedSavingDeposits", "value=" + payment);
        selenium.click("defineAcceptedPaymentTypes.button.addDeposits");
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }
}