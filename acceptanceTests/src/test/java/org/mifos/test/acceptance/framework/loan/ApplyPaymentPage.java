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

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class ApplyPaymentPage extends MifosPage {

    public ApplyPaymentPage(Selenium selenium) {
        super(selenium);
        verifyPage("ApplyPayment");
    }

    public ApplyPaymentConfirmationPage submitAndNavigateToApplyPaymentConfirmationPage(PaymentParameters params)
    {
        enterPaymentData(params);
        return new ApplyPaymentConfirmationPage(selenium);
    }

    public void verifyPaymentPriorLastPaymentDate(PaymentParameters params) {
        enterPaymentData(params);
        selenium.isTextPresent("Date of transaction cannot be less than the last payment date");
    }

    private void enterPaymentData(PaymentParameters params) {
        selenium.type("transactionDateDD", params.getTransactionDateDD());
        selenium.type("transactionDateMM", params.getTransactionDateMM());
        selenium.type("transactionDateYY", params.getTransactionDateYYYY());

        selenium.type("applypayment.input.amount", params.getAmount());

        selenium.select("applypayment.input.paymentType", "value=" + params.getPaymentTypeValue());

        if (params.getPaymentType().equals(PaymentParameters.TRANSFER)) {
            selenium.select("applypayment.input.accountForTransfer", "value=" + params.getSavingsAccountGlobalNum());
        }

        this.typeTextIfNotEmpty("applypayment.input.receiptId", params.getReceiptId());

        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());

        selenium.click("applypayment.button.reviewTransaction");
        waitForPageToLoad();
    }

    public void verifyModeOfPayments(){
        String[] modesOfPayment=selenium.getSelectOptions("applypayment.input.paymentType");
        Assert.assertEquals(modesOfPayment[1], RepayLoanParameters.CASH);
        Assert.assertEquals(modesOfPayment[2], RepayLoanParameters.CHEQUE);
        Assert.assertEquals(modesOfPayment[3], RepayLoanParameters.VOUCHER);
    }
}
