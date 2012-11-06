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

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class RepayLoanPage extends MifosPage {
    public RepayLoanPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("RepayLoan");
    }

    public RepayLoanConfirmationPage submitAndNavigateToRepayLoanConfirmationPage(RepayLoanParameters params) {
        selenium.select("RepayLoan.input.modeOfRepayment", "value=" + params.getModeOfRepaymentValue());
        if(params.getModeOfRepayment().equals(RepayLoanParameters.TRANSFER_FROM_SAVINGS)){
            selenium.select("repayLoan.input.accountForTransfer", "value=" + params.getAccountForTransferGlobalNum());
            String [] savingsAccountForTransferDetails = 
                    selenium.getSelectedLabel("repayLoan.input.accountForTransfer").split(" - ");

            String savingsAccountGlobalNum = savingsAccountForTransferDetails[0];
            String savingsAccountName = savingsAccountForTransferDetails[1];
            
            String savingsAccountType = selenium.getText(
                    String.format("//table/tbody/tr[@class='%s'][1]/td[2]",savingsAccountGlobalNum));
            BigDecimal savingsAccountBalance = new BigDecimal(selenium.getText(String.format(
                    "//table/tbody/tr[@class='%s'][2]/td[2]", savingsAccountGlobalNum)).replaceAll(",", ""));
            BigDecimal savingsAccountMaxAmountPerWithdrawal = new BigDecimal(selenium.getText(String.format(
                    "//table/tbody/tr[@class='%s'][3]/td[2]", savingsAccountGlobalNum)).replaceAll(",", ""));
          
            Assert.assertEquals(savingsAccountGlobalNum, params.getAccountForTransferGlobalNum());
            Assert.assertEquals(savingsAccountName, params.getAccountForTransferName());
            Assert.assertEquals(savingsAccountType, params.getAccountForTransferType());
            Assert.assertEquals(savingsAccountBalance, new BigDecimal(params.getAccountForTransferBalance()));
            Assert.assertEquals(savingsAccountMaxAmountPerWithdrawal, 
                                new BigDecimal(params.getAccountForTransferMaxWithdrawalAmount()));
        }
        this.typeTextIfNotEmpty("RepayLoan.input.receiptId", params.getReceiptId());

        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());
        selenium.click("RepayLoan.button.reviewTransaction");
        waitForPageToLoad();
        return new RepayLoanConfirmationPage(selenium);
    }


    public boolean isWaiverInterestSelectorVisible() {
        return selenium.isElementPresent("name=waiverInterestChckBox");
    }

    public boolean isWaiverInterestWarningVisible() {
        return !StringUtils.equals("hidden", selenium.getEval("window.$(\"#waiverInterestWarning\").css(\"visibility\")"));
    }

    public boolean isWaivedRepaymentAmoutVisible() {
        return StringUtils.equals("true", selenium.getEval("window.$(\"#waivedRepaymentAmount\").is(\":visible\")"));
    }

    public String waivedRepaymentAmount() {
        return selenium.getEval("window.$(\"#waivedRepaymentAmount\").html()").trim();
    }

    public boolean isTotalRepaymentAmountVisible() {
        return StringUtils.equals("true", selenium.getEval("window.$(\"#totalRepaymentAmount\").is(\":visible\")"));
    }

    public String totalRepaymentAmount() {
        return selenium.getEval("window.$(\"#totalRepaymentAmount\").html()").trim();
    }

    public boolean isWaiveInterestSelected() {
        return StringUtils.equals("true", selenium.getEval("window.$(\"input:checkbox[name=waiverInterestChckBox]:checked\").val()"));
    }

    public void interestWaiver(boolean value) {
        if (value) {
            selenium.check("name=waiverInterestChckBox");
        } else {
            selenium.uncheck("name=waiverInterestChckBox");
        }
        selenium.fireEvent("name=waiverInterestChckBox", "change");
    }
}
