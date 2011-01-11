package org.mifos.test.acceptance.framework.loan;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.SystemPrintln")
public class TransactionHistoryPage extends AbstractPage {

    public TransactionHistoryPage(Selenium selenium) {
        super(selenium);
        verifyPage("ViewTransactionHistory");
    }
}
