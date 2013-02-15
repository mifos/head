package org.mifos.test.acceptance.framework.loan;

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
        return new ApplyGroupPaymentConfirmationPage(selenium);
    }

    public void verifyPaymentPriorLastPaymentDate(PaymentParameters params) {
        enterPaymentData(params, true);
        selenium.isTextPresent("Date of transaction cannot be less than the last payment date");
    }

    private void enterPaymentData(PaymentParameters params, boolean waitForReloadAfterAmountType) {
        selenium.type("transactionDateDD", params.getTransactionDateDD());
        selenium.type("transactionDateMM", params.getTransactionDateMM());
        selenium.type("transactionDateYY", params.getTransactionDateYYYY());
        selenium.select("applypayment.input.paymentType", "value=" + params.getPaymentTypeValue());
        this.typeTextIfNotEmpty("applypayment.input.receiptId", params.getReceiptId());

        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());
        
        selenium.type("applypayment.input.amount", params.getAmount());
        if (waitForReloadAfterAmountType) {
        	waitForPageToLoad();
        }
        selenium.click("applypayment.button.reviewTransaction");
        waitForPageToLoad();
    }

}
