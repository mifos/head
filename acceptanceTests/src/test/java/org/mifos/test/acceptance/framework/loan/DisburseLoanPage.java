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

import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class DisburseLoanPage extends MifosPage {

    public DisburseLoanPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("DisburseLoan");
    }

    public DisburseLoanConfirmationPage submitAndNavigateToDisburseLoanConfirmationPage(DisburseLoanParameters params) {
        fillForm(params);
        submit();
        return new DisburseLoanConfirmationPage(selenium);
    }

    public QuestionResponsePage submitAndNavigateToQuestionResponsePage(DisburseLoanParameters params) {
        fillForm(params);
        submit();
        return new QuestionResponsePage(selenium);
    }

    private void fillForm(DisburseLoanParameters params) {
        this.typeTextIfNotEmpty("transactionDateDD", params.getDisbursalDateDD());
        this.typeTextIfNotEmpty("transactionDateMM", params.getDisbursalDateMM());
        this.typeTextIfNotEmpty("transactionDateYY", params.getDisbursalDateYYYY());

        this.typeTextIfNotEmpty("DisburseLoan.input.receiptId", params.getReceiptId());

        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());

        this.typeTextIfNotEmpty("DisburseLoan.input.disbursementAmount", params.getAmount());

        selenium.select("DisburseLoan.input.paymentType", "value=" + params.getPaymentTypeValue());
        
        selenium.select("paymentModeOfPayment", "value=" + params.getPaymentTypeValue());
    }

    private void submit() {
        selenium.fireEvent("transactionDateMM", "blur");
        selenium.click("DisburseLoan.button.reviewTransaction");
        waitForPageToLoad();
    }

    public void submitWithWrongParams(DisburseLoanParameters params, String msg) {
        this.typeTextIfNotEmpty("transactionDateDD", params.getDisbursalDateDD());
        this.typeTextIfNotEmpty("transactionDateMM", params.getDisbursalDateMM());
        this.typeTextIfNotEmpty("transactionDateYY", params.getDisbursalDateYYYY());

        selenium.select("DisburseLoan.input.paymentType", "value=" + params.getPaymentTypeValue());

        selenium.click("DisburseLoan.button.reviewTransaction");

        waitForPageToLoad();
        Assert.assertTrue(selenium.isElementPresent("//span[@id='DisburseLoan.error.message']/li[text()='"+msg+"']"));
    }

    public void verifyPaymentModeOfPaymentIsEditable(String message) {
        Assert.assertTrue(selenium.isEditable("paymentModeOfPayment"), message);
    }

    public void verifyPaymentModesOfPaymentAreEmpty() {
        Assert.assertEquals(selenium.getSelectedValue("DisburseLoan.input.paymentType"), "");
        Assert.assertEquals(selenium.getSelectedValue("paymentModeOfPayment"), "");
    }

    public void verifyDisbursalDateIsDisabled(){
        Assert.assertFalse(selenium.isEditable("transactionDateDD"));
        Assert.assertFalse(selenium.isEditable("transactionDateMM"));
        Assert.assertFalse(selenium.isEditable("transactionDateYY"));
    }

    public void setModesOfPaymentAndReviewTransaction() {
        selenium.select("DisburseLoan.input.paymentType", "label=" + DisburseLoanParameters.CASH);
        selenium.select("paymentModeOfPayment", "label=" + DisburseLoanParameters.CASH);

        selenium.click("DisburseLoan.button.reviewTransaction");

        waitForPageToLoad();
    }

    public void verifyModeOfPayments(){
        String[] modesOfPayment=selenium.getSelectOptions("DisburseLoan.input.paymentType");
        Assert.assertEquals(RepayLoanParameters.CASH,modesOfPayment[1]);
        Assert.assertEquals(RepayLoanParameters.CHEQUE,modesOfPayment[2]);
        Assert.assertEquals(RepayLoanParameters.VOUCHER,modesOfPayment[3]);
    }
    
    public void verifyDisbursalDateIsFutureDate(DisburseLoanParameters params){
    	fillForm(params);
    	
        selenium.click("DisburseLoan.button.reviewTransaction");
        waitForPageToLoad();
        Assert.assertTrue(selenium.isTextPresent("Date of transaction can not be a future date"));
    }
    
    public void verifyDisbursalDateIsPriorToClientMeetingSchedule(DisburseLoanParameters params){
    	fillForm(params);
    	
        selenium.click("DisburseLoan.button.reviewTransaction");
        waitForPageToLoad();
        selenium.click("Review_loanDisbursement.button.submit");
        waitForPageToLoad();
        Assert.assertTrue(selenium.isTextPresent("Date of transaction is invalid. It can not be prior to last meeting date of the customer."));
        selenium.click("Review_loanDisbursement.button.edit");
        waitForPageToLoad();
    }
    
    public HomePage navigateToHomePage() {
        selenium.click("id=header.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }
}
