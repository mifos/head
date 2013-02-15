package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;

public class ApplyGroupPaymentIndividualClientPage extends ApplyGroupPaymentPage {

    public ApplyGroupPaymentIndividualClientPage(Selenium selenium) {
        super(selenium);
    }

    @Override
    protected void enterPaymentData(PaymentParameters params) {
        selenium.type("transactionDateDD", params.getTransactionDateDD());
        selenium.type("transactionDateMM", params.getTransactionDateMM());
        selenium.type("transactionDateYY", params.getTransactionDateYYYY());
        selenium.select("applypayment.input.paymentType", "value=" + params.getPaymentTypeValue());
        this.typeTextIfNotEmpty("applypayment.input.receiptId", params.getReceiptId());

        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());
        
        selenium.type("applypayment.input.amount", params.getAmount());
        selenium.click("applypayment.button.reviewTransaction");
        waitForPageToLoad();
    }

}
