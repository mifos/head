package org.mifos.test.acceptance.loanproduct;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.StringUtil;

public class LoanProductTestHelper {

        Selenium selenium;

    public LoanProductTestHelper(Selenium selenium) {
        this.selenium = selenium;
    }

    public DefineNewLoanProductPage navigateToDefineNewLoanPangAndFillMandatoryFields(DefineNewLoanProductPage.SubmitFormParameters formParameters) {
        formParameters.setOfferingShortName(StringUtil.getRandomString(4));
        return navigateToDefineNewLoanProductPage().
                fillLoanParameters(formParameters);
    }

    public DefineNewLoanProductPage navigateToDefineNewLoanProductPage() {
        return new NavigationHelper(selenium).
                navigateToAdminPage().
                navigateToDefineLoanProduct();
    }

}
