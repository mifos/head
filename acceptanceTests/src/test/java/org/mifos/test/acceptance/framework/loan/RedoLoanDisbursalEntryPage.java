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

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class RedoLoanDisbursalEntryPage extends MifosPage {


    public RedoLoanDisbursalEntryPage(Selenium selenium) {
        super(selenium);
        verifyPage("LoanCreationDetail");
    }

    public void verifyFutureDateInputError() {
        verifyPage("LoanCreationDetail");
        Assert.assertTrue(selenium.isTextPresent("You have entered an invalid disbursal date. Enter a disbursal date less than today's date"));
    }

    public RedoLoanDisbursalSchedulePreviewPage submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(RedoLoanDisbursalParameters params) {
        typeData(params);

        submit();

        return new RedoLoanDisbursalSchedulePreviewPage(selenium);
    }

    public RedoLoanDisbursalEntryPage submitFutureDateAndReloadPageWithInputError(RedoLoanDisbursalParameters params) {
        typeData(params);

        submit();

        return new RedoLoanDisbursalEntryPage(selenium);
    }

    private void typeData(RedoLoanDisbursalParameters params) {
        this.typeTextIfNotEmpty("loancreationdetails.input.sumLoanAmount", params.getLoanAmount());
        this.typeTextIfNotEmpty("loancreationdetails.input.interestRate", params.getInterestRate());
        this.typeTextIfNotEmpty("loancreationdetails.input.numberOfInstallments", params.getNumberOfInstallments());
        selenium.type("disbursementDateDD", params.getDisbursalDateDD());
        selenium.type("disbursementDateMM", params.getDisbursalDateMM());
        selenium.type("disbursementDateYY", params.getDisbursalDateYYYY());

        selenium.fireEvent("disbursementDateYY", "blur");
    }

    private void submit() {
        selenium.click("loancreationdetails.button.continue");
        waitForPageToLoad();
    }

    public RedoLoanDisbursalEntryPage verifyFeeBlockedForVariableInstallmentLoan(String[] fees) {
        selectFee(fees);
        submit();
        for (String fee : fees) {
            Assert.assertTrue(selenium.isTextPresent(fee + " fee cannot be applied to loan with variable installments"));
        }
        for (int index = 0; index < fees.length; index++) {
            selenium.select("selectedFee[" + index + "].feeId","--Select--");
        }
        return this;

    }

    public RedoLoanDisbursalEntryPage selectFee(String[] fees) {
        for (int index = 0; index < fees.length; index++) {
            String fee = fees[index];
            selenium.select("selectedFee[" + index + "].feeId",fee);
        }
        return this;
    }
}
