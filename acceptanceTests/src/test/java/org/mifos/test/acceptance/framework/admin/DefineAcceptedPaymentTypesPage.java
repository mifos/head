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

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;

public class DefineAcceptedPaymentTypesPage extends MifosPage {
    public DefineAcceptedPaymentTypesPage(Selenium selenium) {
        super(selenium);
    }

    public static final String CASH = "Cash";
    public static final String VOUCHER = "Voucher";
    public static final String CHEQUE = "Cheque";

    public void addLoanFeesPaymentType(String payment) {
        if (isElementAvailable(payment, "chosenNonAcceptedFees")) {
            selenium.select("chosenNonAcceptedFees", payment);
            selenium.click("defineAcceptedPaymentTypes.button.addFees");
        }
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    public void addLoanDisbursementsPaymentType(String payment) {
        if (isElementAvailable(payment, "chosenNonAcceptedLoanDisbursements")) {
            selenium.select("chosenNonAcceptedLoanDisbursements", payment);
            selenium.click("defineAcceptedPaymentTypes.button.addLoans");
        }
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    public void addLoanRepaymentsPaymentType(String payment) {
        if (isElementAvailable(payment, "chosenNonAcceptedLoanRepayments")) {
            selenium.select("chosenNonAcceptedLoanRepayments", payment);
            selenium.click("defineAcceptedPaymentTypes.button.addRepayments");
        }
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    public void addSavingsWithdrawalsType(String payment) {
        if (isElementAvailable(payment, "chosenNonAcceptedSavingWithdrawals")) {
            selenium.select("chosenNonAcceptedSavingWithdrawals", payment);
            selenium.click("defineAcceptedPaymentTypes.button.addWithdrawals");
        }
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    public void addSavingsDepositsPaymentType(String payment) {
        if (isElementAvailable(payment, "chosenNonAcceptedSavingDeposits")) {
            selenium.select("chosenNonAcceptedSavingDeposits", payment);
            selenium.click("defineAcceptedPaymentTypes.button.addDeposits");
        }
        selenium.click("defineAcceptedPaymentTypes.button.submit");
        waitForPageToLoad();
    }

    private boolean isElementAvailable(String payment, String name) {
        return Boolean.parseBoolean(selenium.getEval("window.$(\"#" + name + " option:contains('" + payment + "')\").length>0"));
    }
}