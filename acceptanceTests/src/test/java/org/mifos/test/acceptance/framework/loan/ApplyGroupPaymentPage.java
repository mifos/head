package org.mifos.test.acceptance.framework.loan;

import junit.framework.Assert;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class ApplyGroupPaymentPage extends MifosPage {

    public ApplyGroupPaymentPage(Selenium selenium) {
        super(selenium);
        verifyPage("ApplyGroupPayment");
    }

    public ApplyGroupPaymentConfirmationPage submitAndNavigateToApplyGroupPaymentConfirmationPage(PaymentParameters params, boolean waitFoReloadAfterAmountType)
    {
        enterPaymentData(params, waitFoReloadAfterAmountType);
        return submit();
    }

    public void verifyPaymentPriorLastPaymentDate(PaymentParameters params) {
        enterPaymentData(params, true);
        selenium.isTextPresent("Date of transaction cannot be less than the last payment date");
    }

    public void setAmount(String amount) {
    	selenium.type("applypayment.input.amount", amount);
    	waitForPageToLoad();
    }
    
    public void setIndividualAmount(int clientIndex, String amount) {
    	selenium.type("clientAmount" + clientIndex, amount);
    }
    
    public void verifyAmount(String amountExpected) {
    	Assert.assertEquals(amountExpected, selenium.getValue("applypayment.input.amount"));
    }

    public void verifyIndividualAmount(int clientIndex, String amountExpected) {
    	Assert.assertEquals(amountExpected, selenium.getValue("clientAmount" + clientIndex));
    }
    
    public void setDate(String dd, String mm, String yyyy) {
    	selenium.type("transactionDateDD", dd);
        selenium.type("transactionDateMM", mm);
        selenium.type("transactionDateYY", yyyy);
    }
    
    public void setPaymentMethod(String paymentMethod) {
    	selenium.select("applypayment.input.paymentType", "value=" + paymentMethod);
    }
    
    public ApplyGroupPaymentConfirmationPage submit() {
    	selenium.click("applypayment.button.reviewTransaction");
        waitForPageToLoad();
        return new ApplyGroupPaymentConfirmationPage(selenium);
    }
    
    private void enterPaymentData(PaymentParameters params, boolean waitForReloadAfterAmountType) {
    	setDate(params.getTransactionDateDD(), params.getTransactionDateMM(), params.getTransactionDateYYYY());
    	selenium.select("applypayment.input.paymentType", "value=" + params.getPaymentTypeValue());
    	params.getPaymentTypeValue();
        this.typeTextIfNotEmpty("applypayment.input.receiptId", params.getReceiptId());

        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());
        
        selenium.type("applypayment.input.amount", params.getAmount());
        if (waitForReloadAfterAmountType) {
        	waitForPageToLoad();
        }
    }

}
