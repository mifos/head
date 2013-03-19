package org.mifos.test.acceptance.framework.loan;

import java.util.List;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class DivideGroupChargesPage extends MifosPage  {
    
    public DivideGroupChargesPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("DivideGroupCharges");
    }
    
    public LoanAccountPage submitAndNavigateToLoanAccountPage(ChargeParameters params) {
        typeGroupLoanIndividualAmounts(params.getGroupLoanIndividualAmounts());
        selenium.click("divideGroupCharges.button.reviewTransaction");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }
    
    private void typeGroupLoanIndividualAmounts(List<String> groupLoanIndividualAmounts) {
        for (int i = 0; i < groupLoanIndividualAmounts.size(); i++) {
            String locator = "//form[2]/table[2]/tbody/tr/td/table/tbody/tr[" + (i+2) +"]/td[4]/input";
            typeTextIfNotEmpty(locator, groupLoanIndividualAmounts.get(i));
        }
    }
}
