package org.mifos.test.acceptance.framework.savings;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class SavingsDepositWithdrawalPage  extends MifosPage{

    public SavingsDepositWithdrawalPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("applypayment_savingsaccount");
    }

    public SavingsDepositWithdrawalConfirmationPage submitAndNavigateToDepositWithdrawalConfirmationPage(DepositWithdrawalSavingsParameters params)
    {
        this.typeTextIfNotEmpty("trxnDateDD", params.getTrxnDateDD());
        this.typeTextIfNotEmpty("trxnDateMM", params.getTrxnDateMM());
        this.typeTextIfNotEmpty("trxnDateYY", params.getTrxnDateYYYY());

        selenium.select("applypayment_savingsaccount.input.trxnType", "value=" + params.getTrxnTypeValue() );
        waitForPageToLoad();

        this.typeTextIfNotEmpty("applypayment_savingsaccount.input.amount", params.getAmount());

        selenium.select("applypayment_savingsaccount.input.paymentType", "value=" + params.getPaymentTypeValue() );

        this.typeTextIfNotEmpty("applypayment_savingsaccount.input.receiptId", params.getReceiptId());

        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());

        selenium.click("applypayment_savingsaccount.button.submit");
        waitForPageToLoad();

        return new SavingsDepositWithdrawalConfirmationPage(selenium);
    }


}
