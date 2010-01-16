package org.mifos.test.acceptance.framework.loanproduct.multicurrrency;

import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;

import com.thoughtworks.selenium.Selenium;

public class DefineNewDifferentCurrencyLoanProductPage extends DefineNewLoanProductPage {

    public DefineNewDifferentCurrencyLoanProductPage() {
        super();
    }
    
    public DefineNewDifferentCurrencyLoanProductPage(Selenium selenium) {
        super(selenium);
    }
    
    @Override
    public void verifyPage() {
          this.verifyPage("CreateLoanProduct");

    }

    @Override
    public DefineNewLoanProductPage submitPage() {
        return this;
    }
    
    public static class SubmitMultiCurrencyFormParameters extends SubmitFormParameters{
        private Short currencyId;

        public void setCurrencyId(Short currencyId) {
            this.currencyId = currencyId;
        }

        public Short getCurrencyId() {
            return currencyId;
        }   
    }
    
    public void fillLoanParameters(SubmitMultiCurrencyFormParameters parameters) {
        super.fillLoanParameters(parameters);
        selenium.select("currencyId", "value=" + parameters.getCurrencyId());
    }
}
