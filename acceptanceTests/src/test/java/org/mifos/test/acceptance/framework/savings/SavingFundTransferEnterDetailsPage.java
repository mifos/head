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

public class SavingFundTransferEnterDetailsPage extends MifosPage {

    public SavingFundTransferEnterDetailsPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("fundTransferEnterDetails");
    }
    
    public SavingFundTrasferPreviewPage SubmitAndNavigateToSavingFundPreviewPage(FundsParameters params) {
        verifyPage();
        fillForm(params);
    	
        selenium.click("fundTransfer.enterDetails.submit");
        waitForPageToLoad();
        
        return new SavingFundTrasferPreviewPage(selenium);
    }
    
    private void fillForm(FundsParameters params) {
        this.typeTextIfNotEmpty("fundTransfer.trxnDateDD", params.getTransactionDateDD());
        this.typeTextIfNotEmpty("fundTransfer.trxnDateMM", params.getTransactionDateMM());
        this.typeTextIfNotEmpty("fundTransfer.trxnDateYY", params.getTransactionDateYYYY());

        this.typeTextIfNotEmpty("fundTransfer.amount", params.getAmount());
        this.typeTextIfNotEmpty("receiptId", params.getReceiptId());
        this.typeTextIfNotEmpty("fundTransfer.receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("fundTransfer.receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("fundTransfer.receiptDateYY", params.getReceiptDateYYYY());
    }

    public void submitWithWrongParams(FundsParameters params, String ErrorMessage) {
        verifyPage();
        fillForm(params);
        
        selenium.click("fundTransfer.enterDetails.submit");
        waitForPageToLoad();
        verifyPage();
        
        Assert.assertTrue(selenium.isTextPresent(ErrorMessage));
    }
    
}
