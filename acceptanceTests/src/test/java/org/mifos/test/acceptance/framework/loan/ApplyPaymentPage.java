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


public class ApplyPaymentPage extends MifosPage {

    public ApplyPaymentPage(Selenium selenium) {
        super(selenium);
    }
    
    public void verifyPage() {
        verifyPage("ApplyPayment");
    }
    
    public ApplyPaymentConfirmationPage submitAndNavigateToApplyPaymentConfirmationPage(PaymentParameters params)
    {
        selenium.type("transactionDateDD", params.getTransactionDateDD());
        selenium.type("transactionDateMM", params.getTransactionDateMM());
        selenium.type("transactionDateYY", params.getTransactionDateYYYY());

        selenium.type("applypayment.input.amount", params.getAmount());
        
        selenium.select("applypayment.input.paymentType", "value=" + params.getPaymentTypeValue());
        
        this.typeTextIfNotEmpty("applypayment.input.receiptId", params.getReceiptId());
        
        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());

        selenium.click("applypayment.button.reviewTransaction");
        waitForPageToLoad();
        
        return new ApplyPaymentConfirmationPage(selenium);
    }
}
