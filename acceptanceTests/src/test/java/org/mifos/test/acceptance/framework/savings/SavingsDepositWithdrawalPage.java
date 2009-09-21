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
        selenium.type("trxnDateDD", params.getTrxnDateDD());
        selenium.type("trxnDateMM", params.getTrxnDateMM());
        selenium.type("trxnDateYY", params.getTrxnDateYYYY());
        
        selenium.select("applypayment_savingsaccount.input.trxnType", "value=" + params.getTrxnTypeValue() );
 
        selenium.type("applypayment_savingsaccount.input.amount", params.getAmount());

        selenium.select("applypayment_savingsaccount.input.paymentType", "value=" + params.getPaymentTypeValue() );
        
        this.typeTextIfNotEmpty("applypayment.input.receiptId", params.getReceiptId());
        
        this.typeTextIfNotEmpty("receiptDateDD", params.getReceiptDateDD());
        this.typeTextIfNotEmpty("receiptDateMM", params.getReceiptDateMM());
        this.typeTextIfNotEmpty("receiptDateYY", params.getReceiptDateYYYY());

        selenium.click("applypayment_savingsaccount.button.submit");
        waitForPageToLoad();
               
        return new SavingsDepositWithdrawalConfirmationPage(selenium);
    }

 
}
